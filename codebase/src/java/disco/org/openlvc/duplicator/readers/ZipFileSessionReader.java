/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.duplicator.readers;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.duplicator.Track;

public class ZipFileSessionReader implements ISessionReader
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Always try and maintain a buffer of at least 100 PDUs, but preferrably up to 1000.
	// If we say the average PDU size is 500b, then 1000 is only 500K of ram to hold anyway.
	/** The lowest we should let the buffer get (excluding when coming to end of session */
	private static final int BUFFER_LOW_THRESHOLD = 100;

	/** When refilling the buffer from disk, this is the max we should try and fetch. Note
	    that the actual figure may be slightly above or under this */
	private static final int BUFFER_REFILL_THRESHOLD = 1000;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private File sessionFile;

	// Zip and File Reading
	private ZipFile zipFile;
	private InputStream zipIn;
	private DataInputStream dataIn;
	
	// PDU Buffering
	private boolean fileIsEmpty;
	private Queue<Track> queue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ZipFileSessionReader( File sessionFile ) throws DiscoException
	{
		this.sessionFile = sessionFile;
		
		// Zip and File Reading
		this.zipFile = null; // initialized in openSession()
		this.zipIn = null;   // initialized in openSession()
		this.dataIn = null;  // initialized in openSession()
		
		// PDU Buffering
		this.fileIsEmpty = false; // managed in refillBugger
		this.queue = new LinkedList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Set the file from which we are reading. If the session is already open, an exception
	 * will be thrown.
	 * 
	 * @param sessionFile The file to read from
	 * @throws DiscoException If the session is already open.
	 */
	@Override
	public void setSessionFile( File sessionFile ) throws DiscoException
	{
		if( this.isOpen() )
			throw new DiscoException( "Cannot change the zip session file while session is open" );
		else
			this.sessionFile = sessionFile;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Session Reading Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Open the supporting session file so that we can read PDUs from it. Throws an
	 * exception if there is an underlying IO issue. If the session is already open
	 * we just return immediately.
	 */
	@Override
	public void open() throws DiscoException
	{
		if( isOpen() )
			return;

		try
		{
			this.zipFile = new ZipFile( this.sessionFile );
			ZipEntry zipEntry = getFirstSessionInZip( this.zipFile );
			if( zipEntry == null )
			{
				this.zipFile.close();
				throw new DiscoException( "Zip file does not contain a .session or .disco file" );
			}

			this.zipIn = zipFile.getInputStream( zipEntry );
			this.dataIn = new DataInputStream( this.zipIn );
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Could not open zip session file for reading: "+ioex.getMessage(), ioex );
		}
	}

	/**
	 * Close the reference to the session file, throwing an exception if there is an
	 * underlying IO issue. If the session is not open this method will return immediately.
	 */
	@Override
	public void close() throws DiscoException
	{
		if( isOpen() == false )
			return;
		
		try
		{
			if( this.dataIn != null )
				this.dataIn.close();
			if( this.zipFile != null )
				this.zipFile.close();
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Could not close zip session file: "+ioex.getMessage(), ioex );
		}
		finally
		{
			this.zipFile = null;
			this.zipIn = null;
			this.dataIn = null;
		}
	}

	@Override
	public boolean isOpen()
	{
		return this.dataIn != null;
	}

	/**
	 * Return the next {@link Track} in the session. Note that tracks are loaded in blocks,
	 * and this call may trigger the next block to be loaded.
	 * 
	 * @return The next available track in the session, or null if there is none.
	 */
	@Override
	public Track next()
	{
		// If we don't have at least 100 PDUs in the buffer, and there are more left, fetch them
		if( queue.size() <= BUFFER_LOW_THRESHOLD )
			refillBuffer();
		
		// Return the next thing on the queue
		if( queue.isEmpty() )
			return null;
		else
			return queue.poll();
	}

	/**
	 * This method does the actual reading of the PDU/Track data from disk. It will attempt to
	 * refill the queue with up to another BUFFER_REFILL_THRESHOLD tracks, but it may end early
	 * if there are none left to read.
	 */
	private final void refillBuffer() throws DiscoException
	{
		// check to see if there are any more to get, if not, just return
		try
		{
			if( dataIn.available() == 0 )
			{
				this.fileIsEmpty = true;
				return;
			}
		}
		catch( IOException e )
		{
			throw new DiscoException( "Exeption while refilling PDU buffer: "+e.getMessage(), e );
		}

		// there are more cookies, let's refill the buffer so we can have some
		try
		{
			int pdusRead = 0;
			while( pdusRead < BUFFER_REFILL_THRESHOLD )
			{
				long timeOffset = dataIn.readLong(); // timestamp
				short pdusize = dataIn.readShort();
				byte[] pdubytes = new byte[pdusize];
				dataIn.readFully( pdubytes );
				
				// convert the byte[] into a PDU
				
				PDU pdu = PduFactory.getDefaultFactory().create( pdubytes );
				queue.add( new Track(pdu,timeOffset) );
				
				// is there more information to process?
				if( dataIn.available() <= 0 )
				{
					this.fileIsEmpty = true;
					return;
				}
				
				++pdusRead;
			}
		}
		catch( IOException e )
		{
			throw new DiscoException( "Exeption while refilling PDU buffer: "+e.getMessage(), e );
		}
	}

	/**
	 * Finds the first {@code .session} or {@code .disco} file within the given zip file and
	 * returns the {@link ZipEntry} pointing to it. If no such file is found, null is returned.
	 * The order in which files are checked is not defined, so you are recommended to only have
	 * a single file.
	 * 
	 * @param zip The zip file to read
	 * @return The {@link ZipEntry} pointing to the first found Duplicator session
	 */
	private ZipEntry getFirstSessionInZip( ZipFile zip )
	{
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while( entries.hasMoreElements() )
		{
			ZipEntry entry = entries.nextElement();
			if( entry.isDirectory() )
				continue;
			
			if( entry.getName().endsWith(".session") || entry.getName().endsWith(".disco") )
				return entry;
		}

		// Didn't find anything, return null
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Iterator Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<Track> iterator()
	{
		return this;
	}

	@Override
	public boolean hasNext()
	{
		// in case the queue is empty, try to refill the buffer
		if( queue.isEmpty() )
			refillBuffer();
		
		if( queue.isEmpty() == false || this.fileIsEmpty == false )
			return true;
		else
			return false;
	}

	@Override
	public void remove()
	{
		// Purpose: remove from this collection the last item returned by the iterator
		//  Action: none; we remove every element we return
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
