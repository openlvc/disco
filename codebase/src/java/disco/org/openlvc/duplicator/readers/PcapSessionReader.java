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

import java.io.File;
import java.io.FileInputStream;

import org.openlvc.disco.DiscoException;
import org.openlvc.duplicator.Track;

public class PcapSessionReader implements ISessionReader
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private File sessionFile;
	private PcapDisStream pcapStream;
	private Track nextTrack;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PcapSessionReader( File sessionFile ) throws DiscoException
	{
		if( sessionFile == null || !sessionFile.exists() )
			throw new DiscoException( "Session file cannot be found: "+sessionFile );

		this.sessionFile = sessionFile;
		this.pcapStream = null;  // set in open();
		this.nextTrack = null;   // set each time a new PDU is pulled
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void open() throws DiscoException
	{
		if( isOpen() )
			return;

		if( sessionFile == null || !this.sessionFile.exists() )
			throw new DiscoException( "Session file cannot be found: "+sessionFile );

		try
		{
			this.pcapStream = new PcapDisStream( new FileInputStream(sessionFile) );
			pullTrackFromStream();
		}
		catch( Exception e )
		{
			throw new DiscoException( e, "Exception opening session file: "+sessionFile );
		}
	}

	@Override
	public void close() throws DiscoException
	{
		if( isOpen() == false )
			return;
		
		try
		{
			this.nextTrack = null;
			this.pcapStream.close();
			this.pcapStream = null;
		}
		catch( Exception e )
		{
			throw new DiscoException( e, "Exception closing session file: "+sessionFile );
		}
	}
	
	@Override
	public boolean isOpen()
	{
		return this.pcapStream != null;
	}

	@Override
	public Track next()
	{
		// store the current so we can return it
		Track current = this.nextTrack;
		
		// pull the next track into place, ready for next time
		pullTrackFromStream();
		
		// return the current track
		return current;
	}
	
	@Override
	public boolean hasNext()
	{
		return this.nextTrack != null;
	}

	private void pullTrackFromStream()
	{
		this.nextTrack = pcapStream.readTrack();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setSessionFile( File sessionFile ) throws DiscoException
	{
		if( this.isOpen() )
			throw new DiscoException( "Cannot change the session file while session is open" );
		else
			this.sessionFile = sessionFile;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
