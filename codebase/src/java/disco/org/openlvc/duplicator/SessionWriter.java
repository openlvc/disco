/*
 *   Copyright 2018 Open LVC Project.
 *
 *   This file is part of Open LVC Disco.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openlvc.duplicator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.utils.BitHelpers;
import org.openlvc.disco.utils.FileUtils;
import org.openlvc.disco.utils.ThreadUtils;

/**
 * The {@link SessionWriter} class is responsible for managing all I/O between incoming PDUs
 * and the session file on disk. It has an internal buffer into which PDUs are put (wrapped
 * in {@link Track} objects) while a separate thread handles the bulk write operation to the
 * underlying session file.
 */
public class SessionWriter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// Session File Properties
	private File sessionFile;
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	private WriterThread writerThread;
	
	// PDU Management
	private long openingTimestamp;
	private BlockingQueue<Track> queue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SessionWriter( String sessionFileName )
	{
		this( new File(sessionFileName) );
	}

	public SessionWriter( File sessionFile )
	{
		// Session File Properties
		this.sessionFile = sessionFile;
		this.fos = null;                  // set in open()
		this.bos = null;                  // set in open()
		this.writerThread = null;         // set in open()

		// PDU Management
		this.openingTimestamp = 0;        // set in open()
		this.queue = new LinkedBlockingQueue<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Add the given {@link PDU} to the session. This will queue the PDU for writing to
	 * to session file on disk (handled in a separate thread). This call WILL NOT block
	 * while adding to the queue.
	 * 
	 * @param pdu The PDU to add to the session
	 */
	protected void add( PDU pdu )
	{
		long offset = System.currentTimeMillis() - openingTimestamp;
		this.queue.add( new Track(pdu,offset) );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Session Writing Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Open up the session file and prepare us to start writing to it. If the file exists it
	 * will be overwritten by this call. <p/> 
	 * 
	 * All write operations happen in bulk on a separate thread, with this call
	 * also starting that background thread.
	 * 
	 * @throws DiscoException If the file cannot be opened for writing
	 */
	public void open() throws DiscoException
	{
		// Open the session file and get us ready to write to it
		if( this.sessionFile.exists() == false )
			FileUtils.createFile( sessionFile );
		
		try
		{
			this.fos = new FileOutputStream( sessionFile );
			this.bos = new BufferedOutputStream( this.fos );
		}
		catch( IOException e )
		{
			throw new DiscoException( "Could not open session file for writing: "+e.getMessage(), e );
		}
		
		// Start the session writer thread so that we're ready for all teh PDUs
		this.openingTimestamp = System.currentTimeMillis();
		this.writerThread = new WriterThread();
		this.writerThread.start();
	}

	/**
	 * Close the session file off and stop the background writer thread.
	 */
	public void close() throws DiscoException
	{
		// Stop the writer thread
		this.writerThread.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.writerThread );

		// Close out the stream
		try
		{
			this.fos.close();
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Exception while closing session file: "+ioex.getMessage(), ioex );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Set the session file that we should write to.
	 * 
	 * @param file The file that the session should be written to
	 * @throws DiscoException If the session file we have already is currently open
	 */
	public void setSessionFile( File file ) throws DiscoException
	{
		if( fos != null )
			throw new DiscoException( "Cannot set session file: Session is currently open" );
		else
			this.sessionFile = file;
	}
	
	public File getSessionFile()
	{
		return this.sessionFile;
	}
	
	public boolean isOpen()
	{
		return this.fos != null;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Session Writer   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This class is responsible for managing access to the underlying Duplicator session file.
	 * It takes PDUs from the local Queue and flushes them to the file as soon as it can. All
	 * writing is executed on a dedicated thread.
	 * 
	 * NOTE: It will not handle any opening or closing of the file, it will just handle any
	 *       write operations.
	 */
	private class WriterThread extends Thread
	{
		public WriterThread()
		{
			super( "SessionWriter" );
		}
		
		public void run()
		{
			LinkedList<Track> flushList = new LinkedList<>();
			while( Thread.interrupted() == false )
			{
				flushList.clear();

				// 
				// Drain all available PDUs
				//
				// Get the first PDU, blocking until it turns up 
				try
				{
					flushList.add( queue.take() );
				}
				catch( InterruptedException ie )
				{
					// time to exit
					return;
				}

				// We've got the first PDU, if there are more just keep going, but don't block
				while( queue.peek() != null )
					flushList.add( queue.poll() );

				//
				// Flush PDUs to disk
				//
				for( Track track : flushList )
				{
					try
					{
						// Structure on Disk:
						//  - int64  timestamp
						//  - int8   size
						//  - byte[] data
						byte[] pdubytes = track.pdu.toByteArray();
						bos.write( BitHelpers.longToBytes(track.offset) );
						bos.write( BitHelpers.shortToBytes((short)pdubytes.length) );
						bos.write( pdubytes );
					}
					catch( IOException ioex )
					{
						// Toss this out there. The UncaughtExceptionHandler will log it for us
						throw new DiscoException( "Failed to write PDU to session file: "+ioex.getMessage(), ioex );
					}
				}
			}
		}
	}

}
