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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.utils.StringUtils;
import org.openlvc.disco.utils.ThreadUtils;

/**
 * The {@link Replayer} is a class that reads a session file and replays it, feeding PDUs to
 * a dedicated listener at appropriate intervals.
 * <p/>
 * 
 * Replay happens in one of two modes: <code>RealTime</code> or <code>AsFastAsPossible</code>.
 * Both will happen on a separate thread, with <code>RealTime</code> pausing so that the gap
 * between PDUs is the same as it was when the session was originally recorded.
 * <p/>
 * 
 * Additional logging is done by the {@link Replayer}. You can provide your own logger, or if
 * one is not set it will create one under the name "duplicator". If you don't want the regular
 * status logging methods, you can disable them with {@link #setStatusLogging(boolean)}, or you
 * can adjust the frequency with {@link #setStatusLogInterval(long)} (in millis).
 */
public class Replayer
{
	//----------------------------------------------------------
	//                      ENUMERATIONS
	//----------------------------------------------------------
	public enum Mode{ RealTime, AsFastAsPossible };
	public enum Status{ BeforeStart, Running, Paused, Finished };

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
	// General Configuration
	private Logger logger;
	private IPduListener pduListener;
	private Mode mode;
	
	// Runtime Management
	private Status status;
	private Thread replayThread;

	// Packet management
	private Queue<Track> queue;
	
	// Session Reading
	private File sessionFile;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private DataInputStream dis;

	// Metrics
	private long pdusWritten;
	private long esPdusWritten;
	private long firePdusWritten;
	private long detPdusWritten;
	private long pdusWrittenSize;
	private Queue<Long> lastTenSeconds;
	
	// Status Logging and Timer Tasks
	private boolean statusLogging;
	private long statusInterval;
	private Timer timer;
	private StatusLogger statusLogger;
	private PduRateLogger pduRateLogger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Replayer( File sessionFile ) throws DiscoException
	{
		if( sessionFile.exists() == false )
			throw new DiscoException( "Session file does not exist: "+sessionFile.getAbsolutePath() );
		
		this.logger = null;               // set in startReplay() or setLogger()
		this.pduListener = null;
		this.mode = Mode.RealTime;
		
		// Runtime settings
		this.status = Status.BeforeStart;
		this.replayThread = null;         // set in startReplay()
		
		// Packet management
		this.queue = new LinkedList<>();
		
		// Session Writing
		this.sessionFile = sessionFile;
		this.fis = null;                  // set in openSession()
		this.bis = null;                  // set in openSession()
		
		// Metrics
		// These are all set in startReplay
		
		// Timer Tasks
		this.statusLogging = true;
		this.statusInterval = 10000;
		this.timer = null;                // set in startReplay() 
		this.statusLogger = new StatusLogger();
		this.pduRateLogger = new PduRateLogger();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Replay Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void startReplay() throws DiscoException
	{
		// make sure we're not running already
		if( this.status != Status.BeforeStart && this.status != Status.Finished )
			throw new DiscoException( "Can'ts start a session that is running" );
		
		// make sure we have someone to talk to
		if( this.pduListener == null )
			throw new DiscoException( "Cannot start replay: We have no listener to send PDUs to" );
		
		// make sure we have a logger to use
		if( this.logger == null )
			this.logger = LogManager.getFormatterLogger( "duplicator" );

		// reset metrics
		this.pdusWritten = 0;
		this.esPdusWritten = 0;
		this.firePdusWritten = 0;
		this.detPdusWritten = 0;
		this.pdusWrittenSize = 0;
		this.lastTenSeconds = new LinkedList<Long>();
		
		// configure the timers
		if( this.isStatusLogging() )
		{
    		this.timer = new Timer( "duplicator", true );
    		this.timer.scheduleAtFixedRate( this.statusLogger, statusInterval, statusInterval );
    		this.timer.scheduleAtFixedRate( this.pduRateLogger, 1000, 1000 );
		}

		// run the replay
		this.status = Status.Running;
		this.replayThread = new ReplayThread();
		this.replayThread.start();
	}
	
	public void pauseReplay()
	{
		this.status = Status.Paused;
	}
	
	public void resumeReplay()
	{
		this.status = Status.Running;
	}
	
	public void stopReplay()
	{
		if( this.status == Status.Finished )
			return;
		
		// stop the replay thread
		this.replayThread.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.replayThread );
		
		// close the session and timers
		this.endReplay();
	}

	/**
	 * End the active elements of the session. We do this in a separate method because we need
	 * something we can call when either stopping a replay from outside the replay thread, or
	 * when ending the replay because the session is finished (which happens inside the replay
	 * thread).
	 */
	private void endReplay()
	{
		// close the session
		this.closeSession();
		
		// end the logging timer
		if( this.timer != null )
			this.timer.cancel();
		
		this.status = Status.Finished;
	}

	/**
	 * This method will block until the currently active session is finished replaying.
	 * This block will be held over pause/resume cycles and will only return once the
	 * status for the session reaches Status.Finished.
	 */
	public void waitForSessionToFinish()
	{
		while( this.status != Status.Finished )
		{
			if( Thread.interrupted() )
				return;
			
			ThreadUtils.exceptionlessSleep( 1000 );
		}
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
	
	private void closeSession()
	{
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
	/// Session Management Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	// NOTE: This is run from the ReplayThread
	private void replaySession()
	{
		// Clean up any counters
		this.pdusWritten = 0;
		this.pdusWrittenSize = 0;
		this.lastTenSeconds.clear();
		
		// Open up the session file
		this.openSession();
		
		// fill the buffer initially to make sure we have something to work on
		this.refillBuffer();
		
		// record start time and pdu count for logging later
		long startTime = System.currentTimeMillis();
		
		// iterate over all PDUs, waiting the appropriate time if we are running in real time
		long lastPacketTimestamp = 0;
		while( queue.isEmpty() == false )
		{
			// Make sure we're in the right state first
			switch( status )
			{
				case BeforeStart:
				case Finished:
					return; // we should be done and OUT of here
				case Running:
					break;
				case Paused:
					ThreadUtils.exceptionlessSleep( 500 );
					continue;
			}
			
			// Get the next track
			Track next = getNextTrack();
			long delay = next.timestamp - lastPacketTimestamp;
			
			// Wait for a bit if we have to
			if( this.mode == Mode.RealTime && (delay > 0) )
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
			this.pduListener.receiver( next.pdu );
			lastPacketTimestamp = next.timestamp;
			pdusWritten++;
			pdusWrittenSize += next.pdu.getPduLength();
			switch( next.pdu.getType() )
			{
				case EntityState: esPdusWritten++;   break;
				case Fire:        firePdusWritten++; break;
				case Detonation:  detPdusWritten++;  break;
				default: break;
			}
		}

		// queue should never get empty - getNextTrack() will refill it. If we get here we are done
		logger.info( "No more tracks to replay. Session over." );
		logger.info( "Sent %d PDUs in %dms", pdusWritten, System.currentTimeMillis()-startTime );
		this.endReplay();
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
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setLogger( Logger logger )
	{
		this.logger = logger;
	}
	
	public void setListener( IPduListener pduListener )
	{
		this.pduListener = pduListener;
	}

	public Mode getMode()
	{
		return this.mode;
	}
	
	public void setMode( Mode mode )
	{
		this.mode = mode;
	}
	
	public Status getStatus()
	{
		return this.status;
	}
	
	public File getSessionFile()
	{
		return this.sessionFile;
	}
	
	public void setSessionFile( File sessionFile ) throws DiscoException
	{
		if( this.status != Status.BeforeStart )
			throw new DiscoException( "Cannot change the session file while replay is active" );
		
		this.sessionFile = sessionFile;
	}

	public boolean isStatusLogging()
	{
		return this.statusLogging;
	}

	/**
	 * Turn periodic replay stats status logging on or off
	 * 
	 * @param statusLogging Should periodic stats be logged or not
	 */
	public void setStatusLogging( boolean statusLogging )
	{
		this.statusLogging = statusLogging;
	}
	
	/** @return The number of millis between status logging messages */
	public long getStatusLogInterval()
	{
		return this.statusInterval;
	}

	/**
	 * By default, the Replayer will log status about the number of PDUs read and sent to the
	 * listener periodically. This call sets that period.
	 * 
	 * @param interval The gap between status logging messages in millis
	 */
	public void setStatusLogInterval( long interval )
	{
		this.statusInterval = interval;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: ReplayThread   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class ReplayThread extends Thread
	{
		private ReplayThread()
		{
			super( "duplicator-replay" );
		}
		
		@Override
		public void run()
		{
			replaySession();
		}
	}

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


	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Status Summary Logger   /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class StatusLogger extends TimerTask
	{
		public void run()
		{
			// Format: "(Replay >> [RECEIVER]) pdus=123,456 (123/s); bytes=17.8MB";

			// Get PDU average
			Long[] stream = lastTenSeconds.toArray( new Long[]{} );
			long lastTenTotal = 0;
			for( int i = 1; i < stream.length; i++ )
				lastTenTotal += (stream[i] - stream[i-1]);

			// Generate the log
			String line = String.format( "(Replay >> %s) pdus=%,d (%,d/s); bytes=%s [e=%,d; f=%,d; d=%,d]",
			                             pduListener.toString(),
			                             pdusWritten,
			                             (lastTenTotal/9),
			                             StringUtils.humanReadableSize(pdusWrittenSize),
			                             esPdusWritten,
			                             firePdusWritten,
			                             detPdusWritten );
			
			// Log the log!
			logger.info( line );
		}
	}

	// Watches the number of pdus sent over time to calculate a series
	private class PduRateLogger extends TimerTask
	{
		public void run()
		{
			// Get PDU average
			lastTenSeconds.add( pdusWritten );
			if( lastTenSeconds.size() > 10 )
				lastTenSeconds.remove();
		}
	}
}
