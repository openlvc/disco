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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;

public class Replay implements IPduListener
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
	private Configuration configuration;
	private Logger logger;

	// DIS Settings
	private DiscoConfiguration discoConfiguration;
	private OpsCenter opscenter;

	// Packet management
	private Queue<Track> queue;
	

	// Session Writing
	private File sessionFile;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private DataInputStream dis;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Replay( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = configuration.getLogger();
		
		// DIS Settings
		this.discoConfiguration = null;   // set in execute()
		this.opscenter = null;            // set in execute()
		
		// Packet management
		this.queue = new LinkedList<>();
		
		// Session Writing
		this.sessionFile = new File( configuration.getFilename() );
		this.fis = null;                  // set in execute()
		this.bis = null;                  // set in execute()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Read the contents of the Duplicator Session file back from disk and send
	 * out to the network via a newly created Disco connection
	 */
	public void execute()
	{
		logger.info( "Mode: Replay" );

		//
		// Set up the DIS properties
		//
		// Create the Disco configuration from the base config data
		this.discoConfiguration = new DiscoConfiguration();
		discoConfiguration.getLoggingConfiguration().disable();
		discoConfiguration.getUdpConfiguration().setAddress( configuration.getDisAddress() );
		discoConfiguration.getUdpConfiguration().setPort( configuration.getDisPort() );
		discoConfiguration.getUdpConfiguration().setNetworkInterface( configuration.getDisInterface() );
		discoConfiguration.getDisConfiguration().setExerciseId( configuration.getDisExerciseId() );
		
		this.opscenter = new OpsCenter( discoConfiguration );
		this.opscenter.setListener( this );

		//
		// Run the replay
		//
		this.opscenter.open();

		try
		{
			runReplay( configuration.getLoopCount() );
		}
		finally
		{
    		//
    		// All done! Let's shut ourselves down (nobody else will do it)
    		//
    		this.shutdown();
		}
	}

	/**
	 * Run a replay `loopCount` times. Specify `{@link Configuration#LOOP_INFINITELY} to run it
	 * indefinitely.
	 */
	private void runReplay( int loopCount )
	{
		// Looping - play it some number of times
		String loopString = loopCount == Configuration.LOOP_INFINITELY ? "INDEFINITE" : ""+loopCount;
		logger.info( "Replay the session %s times", loopString );

		boolean loopAgain = true;
		int loopsCompleted = 0;
		do
		{
			logger.info( "Starting replay loop %d of %s", loopsCompleted+1, loopString );

			this.replaySession();
			if( loopCount != 0 && (++loopsCompleted >= loopCount) )
				loopAgain = false;
		}
		while( loopAgain );
	}

	public void shutdown()
	{
		// nothing more to write - close down the ops center
		this.opscenter.close();

		// close off our session file
		try
		{
			if( this.bis != null )
				this.bis.close();
			if( this.fis != null )
				this.fis.close();
		}
		catch( Exception e )
		{
			logger.error( "Exception in shutdown: "+e.getMessage(), e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Management Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void receiver( PDU pdu )
	{
		// no-op for us
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Session Reading Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void openSession()
	{
		if( this.sessionFile.exists() == false )
		{
			logger.error( "Session file doesn't exist: "+sessionFile );
			throw new RuntimeException( "Session file doens't exist: "+sessionFile );
		}

		try
		{
			this.fis = new FileInputStream( sessionFile );
		}
		catch( IOException ioex )
		{
			logger.error( "Could not open session file for reading: "+ioex.getMessage(), ioex );
		}
		
		this.bis = new BufferedInputStream( this.fis );
		this.dis = new DataInputStream( this.bis );
		logger.debug( "Session file is open and ready for reading" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	private void replaySession()
	{
		// Open up the session file
		this.openSession();
		
		// fill the buffer initially to make sure we have something to work on
		this.refillBuffer();
		
		// record start time and pdu count for logging later
		long startTime = System.currentTimeMillis();
		long pduCount  = 0;
		
		// iterate over all PDUs, waiting the appropriate time if we are running in real time
		long lastPacketTimestamp = 0;
		while( queue.isEmpty() == false )
		{
			// Get the next track
			Track next = getNextTrack();
			long delay = next.timestamp - lastPacketTimestamp;
			
			// Wait for a bit if we have to
			if( configuration.isReplayRealtime() && (delay > 0) )
			{
				try
				{
					Thread.sleep( delay );
				}
				catch( InterruptedException ie )
				{
					// someone woke us up when we didn't expect it - must be exit time
					logger.info( "Replay interrupted; returning immediately" );
					return;
				}
			}
			
			// Send the PDU to the network
			opscenter.send( next.pdu );
			lastPacketTimestamp = next.timestamp;
			pduCount++;
		}

		// queue should never get empty - getNextTrack() will refill it. If we get here we are done
		logger.info( "No more tracks to replay. Session over." );
		logger.info( "Sent %d PDUs in %dms", pduCount, System.currentTimeMillis()-startTime );
	}

	private Track getNextTrack()
	{
		// Always try and maintain a buffer of at least 100 PDUs, but preferrably up to 1000.
		// If we say the average PDU size is 500b, then 1000 is only 500K of ram to hold anyway.
		
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
	private final void refillBuffer()
	{
		// check to see if there are any more to get, if not, just return
		try
		{
			if( fis.available() == 0 )
				return;
		}
		catch( IOException e )
		{
			logger.warn( "Exeption while refilling PDU buffer: "+e.getMessage(), e );
			return;
		}

		// refill the buffer
		try
		{
			logger.debug( "Loading more tracks from disk (limit: "+BUFFER_REFILL_THRESHOLD+")" );

			int pdusRead = 0;
			while( (fis.available() > 0) && (pdusRead < BUFFER_REFILL_THRESHOLD) )
    		{
    			long timestamp = dis.readLong();
    			short pdusize = dis.readShort();
    			byte[] pdubytes = new byte[pdusize];
    			dis.readFully( pdubytes );
    			
    			// convert the byte[] into a PDU
    			PDU pdu = PduFactory.create( pdubytes );
    			Track track = new Track( pdu, timestamp );
    			pdusRead++;
    			queue.add( track );
    		}
			
			if( fis.available() == 0 )
				logger.debug( "Read "+pdusRead+" tracks from disk. No more left to read." );
			else
				logger.debug( "Read "+pdusRead+" tracks from disk." );
		}
		catch( IOException e )
		{
			logger.warn( "Exeption while refilling PDU buffer: "+e.getMessage(), e );
			return;
		}
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Track   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class Track
	{
		public long timestamp;
		public PDU pdu;
		public Track( PDU pdu, long timestamp )
		{
			this.pdu = pdu;
			this.timestamp = timestamp;
		}
	}

}
