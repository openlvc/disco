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
import java.util.Iterator;

import org.openlvc.disco.DiscoException;
import org.openlvc.duplicator.Track;

/**
 * Base interface for all Disco session readers. Implementations should extract PDU data
 * (from whatever the implementation-specific supported format is) and then make each PDU
 * available via the {@link #next()} method.
 * 
 * At present, it is assumed that all sessions are <i>file</i> based sessions, so there is
 * a {@link #setSessionFile(File)} method to specify the file that contains the recording.
 */
public interface ISessionReader extends Iterable<Track>, Iterator<Track>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Tell the session read the file that it should extract session data from
	 * 
	 * @param sessionFile The file with the session data
	 * @throws DiscoException If the file could not be read
	 */
	public void setSessionFile( File sessionFile ) throws DiscoException;

	/**
	 * Opens the underlying session data source in preparation for reading.
	 * 
	 * @throws DiscoException If the source cannot be opened (e.g. file does not exist)
	 */
	public void open() throws DiscoException;

	/**
	 * Closes the underlying session data source in preparation for shutdown.
	 * 
	 * @throws DiscoException If the source cannot be closed (e.g. file closure fails)
	 */
	public void close() throws DiscoException;

	/**
	 * @return True if the session data source is open, false otherwise
	 */
	public boolean isOpen();

	/**
	 * Fetch the next {@link Track} from the data source. If there are none left, null is returned.
	 * 
	 * @return The next track from the datastore, or null if there are none
	 */
	public Track next();

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Iterator Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return Default method to return ourselves (we implement this interface)
	 */
	public default Iterator<Track> iterator()
	{
		return this;
	}

	public boolean hasNext();

	/**
	 * Default method implementation that is a no-op. All sessions are <b>read-only</b> currently.
	 */
	public default void remove()
	{
		// no-op
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Factory method to create the approrpiate session reader for the given file. Supports 
	 * Duplicator generated <code>.session</code> files, and PCAP files.
	 * 
	 * If the extension is <code>.pcap</code> or <code>.pcapng</code>, then the PCAP reader
	 * will be returned, otherwise it is assumed to be a Duplicator generated session, regardless
	 * of the extension.
	 * 
	 * @param sessionFile The file containing session data that we want to use
	 * @return A {@link ISessionReader} that can read the file type.
	 * @throws DiscoException If the type of file cannot be determined, or the file cannot be read.
	 */
	public static ISessionReader getReaderForFile( File sessionFile ) throws DiscoException
	{
		if( sessionFile.getName().endsWith(".pcap") ||
			sessionFile.getName().endsWith(".pcapng") )
		{
			return new PcapSessionReader( sessionFile );
		}
		else
		{
			return new DuplicatorSessionReader( sessionFile );
		}
	}
}
