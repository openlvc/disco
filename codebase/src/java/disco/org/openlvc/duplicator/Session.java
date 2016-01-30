/*
 *   Copyright 2016 Open LVC Project.
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;

/**
 * A {@link Session} object represents a recording session with associated DIS data 
 */
public class Session
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger logger;
	private File file;
	private List<Packet> trackList;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Session( File file )
	{
		this.logger = LogManager.getFormatterLogger( "disco" );
		
		this.file = file;
		this.trackList = new LinkedList<>();
		this.initialize();
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void startRecording()
	{
		logger.info( "Start Recording" );
	}
	
	public void stopRecording()
	{
		logger.info( "Stop Recording" );
		this.writeToFile();
	}

	private void initialize()
	{
		if( this.file.exists() )
		{
			try
			{
				loadFromFile();
			}
			catch( Exception e )
			{
				// could not load from file for whatever reason, ignore it
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void addPdu( PDU pdu )
	{
		this.trackList.add( new Packet(pdu,System.currentTimeMillis()) );
	}

	/**
	 * Returns an unmodifiable list of all tracks contained in the soundtrack
	 */
	public List<Packet> getAllTracks()
	{
		return Collections.unmodifiableList( this.trackList );
	}

	public Stream<Packet> stream()
	{
		return trackList.stream();
	}
	
	public Stream<Packet> parallelStream()
	{
		return trackList.parallelStream();
	}

	public int size()
	{
		return trackList.size();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Soundtrack Save and Restore   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void writeToFile()
	{
		// open the file
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( file );
		}
		catch( IOException e )
		{
			// poo, nothing we can do - exit
			throw new DiscoException( "Can't open Soundtrack file for writing: "+e.getMessage(), e );
		}
		
		BufferedOutputStream bos = new BufferedOutputStream( fos );
		try
		{
			bos.write( null );
		}
		catch( IOException e )
		{
			// we have to drop through so that we close out the file safely
		}
		
		//
		// Close the file
		//
		try
		{
			bos.close();
			fos.close();
		}
		catch( IOException e )
		{
		}
	}
	
	private void loadFromFile()
	{
		
	}

	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Public Inner Class: Packet   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public class Packet
	{
		private PDU pdu;
		private long timestamp;
		private Packet( PDU pdu, long timestamp )
		{
			this.pdu = pdu;
			this.timestamp = timestamp;
		}
		
		public PDU  getPdu() { return this.pdu; }
		public long getTimestamp() { return this.timestamp; }
	}
}
