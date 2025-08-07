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

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.utils.StringUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.duplicator.readers.ISessionReader;

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

	// Session Reading
	private ISessionReader sessionReader;

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
		
		// Session Reading
		this.sessionReader = ISessionReader.getReaderForFile( sessionFile );
		
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
		
		// open the session
		this.sessionReader.open();
		logger.debug( "Session file is open and ready for reading" );
		logger.info( "Session file reader is "+this.sessionReader.getClass().getSimpleName() );

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
		logger.info( "Session replay has been STARTED" );
	}
	
	public void pauseReplay()
	{
		this.status = Status.Paused;
		logger.info( "Session replay has been PAUSED" );
	}
	
	public void resumeReplay()
	{
		this.status = Status.Running;
		logger.info( "Session replay has been RESUMED" );
	}
	
	public void stopReplay()
	{
		if( this.status == Status.Finished )
			return;
		
		// stop the replay thread
		if( this.replayThread != null )
		{
    		this.replayThread.interrupt();
    		ThreadUtils.exceptionlessThreadJoin( this.replayThread );
		}
		
		// close the session and timers
		this.endReplay();
		logger.info( "Session replay has been STOPPED" );
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
		this.sessionReader.close();
		logger.debug( "Session file has been closed" );
		
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
		
		logger.info( "Session replay has finished" );
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
		
		// record start time and pdu count for logging later
		long startTime = System.currentTimeMillis();
		
		// iterate over all PDUs, waiting the appropriate time if we are running in real time
		long lastTrackOffset = 0;

		while( sessionReader.hasNext() )
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
			Track next = sessionReader.next();
			long delay = next.offset - lastTrackOffset;
			
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
			this.pduListener.receive( next.pdu );
			lastTrackOffset = next.offset;
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
	
	public void setSessionFile( File sessionFile ) throws DiscoException
	{
		if( this.status != Status.BeforeStart )
			throw new DiscoException( "Cannot change the session file while replay is active" );

		this.sessionReader.setSessionFile( sessionFile );
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
