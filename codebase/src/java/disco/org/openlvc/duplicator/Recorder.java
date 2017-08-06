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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.utils.BitHelpers;
import org.openlvc.disco.utils.StringUtils;

public class Recorder implements IPduListener
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;

	// DIS Settings
	private DiscoConfiguration discoConfiguration;
	private OpsCenter opscenter;

	// Packet management
	private long openingTimestamp;
	private BlockingQueue<Track> buffer;
	
	// Session Writing
	private File sessionFile;
	private Thread sessionWriter;
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	
	// PDU Metrics
	private long pdusWritten;
	private long bytesWritten;
	private long[] pduCounter;          // index is ordinal of PduType
	private Queue<Long> lastTenSeconds; // pdu count for each of the last 10 seconds
	
	// Other Things
	private Timer activityTimer;
	private ActivityLogger activityLogger; // timer task
	private PduRateLogger pduRateLogger;   // timer task
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Recorder( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = configuration.getLogger();
		
		// DIS Settings
		this.discoConfiguration = null;   // set in execute()
		this.opscenter = null;            // set in execute()
		
		// Packet management
		this.openingTimestamp = 0;        // set in execute()
		this.buffer = new LinkedBlockingQueue<>();
		
		// Session Writing
		this.sessionFile = new File( configuration.getFilename() );
		this.sessionWriter = new Thread( new SessionWriter(), "SessionWriter" );
		this.fos = null;                  // set in execute()
		this.bos = null;                  // set in execute()
		
		// PDU Metrics
		this.pdusWritten = 0;
		this.bytesWritten = 0;
		this.pduCounter = new long[PduType.values().length];
		this.lastTenSeconds = new LinkedList<Long>();
		
		// Other Things
		this.activityTimer = null;        // set in execute()
		this.activityLogger = null;       // set in execute()
		this.pduRateLogger = null;        // set in execute()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Run the duplicator. It will keep running indefinately until someone tells it to stop,
	 * typically through a Ctrl-C command and {@link #shutdown()} is called.
	 */
	public void execute()
	{
		logger.info( "Mode: Record" );

		//
		// Check the Session parameters
		//
		this.openSession();
		
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
		// PDU processing options
		discoConfiguration.setPduSender( configuration.getPduSender() );
		discoConfiguration.setPduReceiver( configuration.getPduReceiver() );
		
		this.opscenter = new OpsCenter( discoConfiguration );
		this.opscenter.setListener( this );
		
		//
		// Kick things off
		//
		this.openingTimestamp = System.currentTimeMillis();
		this.sessionWriter.start();
		this.opscenter.open();
		logger.info( "Recorder is active - DO YOUR WORST" );
		
		// Start the activity logger
		this.activityTimer =  new Timer( "Activity" );
		this.activityLogger = new ActivityLogger();
		long interval = configuration.getRecordLogInterval();
		this.activityTimer.scheduleAtFixedRate( activityLogger, interval, interval );
		
		this.pduRateLogger = new PduRateLogger();
		this.activityTimer.scheduleAtFixedRate( pduRateLogger, 1000, 1000 );
	}

	/**
	 * Called when we've had enough recording. Will first close out the Disco OpsCenter so that
	 * we stop receiving packets. Will then stop the {@link SessionWriter}, flush and close our
	 * connection to the dump file.
	 */
	public void shutdown()
	{
		// Shut down the DIS receiving thread
		this.opscenter.close();
		
		// Stop the Session Writer
		this.sessionWriter.interrupt();
		try
		{
			this.sessionWriter.join();
			this.fos.close();
			logger.info( "Session writer has shutdown" );
			logger.info( "Captured %,d PDUs (%,d bytes)", pdusWritten, bytesWritten );
		}
		catch( InterruptedException ie )
		{
			logger.warn( "Interrupted while waiting for SessionWriter to stop: "+ie.getMessage(), ie );
		}
		catch( IOException ioex )
		{
			logger.warn( "Exception while closing session file: "+ioex.getMessage(), ioex );
		}
		
		// Kill the activity logger
		this.activityTimer.cancel();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Management Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void receiver( PDU pdu )
	{
		// increment count for this PDU type
		int typeIndex = pdu.getType().ordinal();
		pduCounter[typeIndex] += 1;
		
		this.buffer.add( new Track(pdu) ); // non-blocking call
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Session Writing Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Open up the connection to the Session file so that we are ready to write to it.
	 */
	private void openSession()
	{
		if( this.sessionFile.exists() )
			logger.warn( "Session file exists. Will overwrite: "+sessionFile );
		
		try
		{
			this.fos = new FileOutputStream( sessionFile );
		}
		catch( IOException ioex )
		{
			logger.error( "Could not open session file for writing: "+ioex.getMessage(), ioex );
		}
		
		this.bos = new BufferedOutputStream( this.fos );
		logger.info( "Session file is open and ready for writing" );
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
		public Track( PDU pdu )
		{
			this.pdu = pdu;
			this.timestamp = System.currentTimeMillis() - openingTimestamp;
		}
	}

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
	private class SessionWriter implements Runnable
	{
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
					flushList.add( buffer.take() );
				}
				catch( InterruptedException ie )
				{
					// time to exit
					return;
				}

				// We've got the first PDU, if there are more just keep going, but don't block
				while( buffer.peek() != null )
					flushList.add( buffer.poll() );

				//
				// Flush PDUs to disk
				//
				for( Track track : flushList )
				{
					try
					{
						byte[] pdubytes = track.pdu.toByteArray();
						bos.write( BitHelpers.longToBytes(track.timestamp) );
						bos.write( BitHelpers.shortToBytes((short)pdubytes.length) );
						bos.write( pdubytes );
						pdusWritten++;
						bytesWritten += (pdubytes.length+9);
					}
					catch( IOException ioex )
					{
						logger.warn( "Failed to write PDU to log file: "+ioex.getMessage(), ioex );
					}
				}
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Activity Logger   ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
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

	private class ActivityLogger extends TimerTask
	{
		public void run()
		{
			// Format: "(192.168.0.1:3000) pdus=123,456; bytes=17.8MB [e=1,234; f=1,234; d=1,234]";
			
			// Get network information
			String ip = opscenter.getConfiguration().getUdpConfiguration().getAddress().toString();
			int port = opscenter.getConfiguration().getUdpConfiguration().getPort();

			// Get PDU received breakdown
			long espduCount = pduCounter[PduType.EntityState.ordinal()];
			long firepduCount = pduCounter[PduType.Fire.ordinal()];
			long detpduCount = pduCounter[PduType.Detonation.ordinal()];

			// Get PDU average
			Long[] stream = lastTenSeconds.toArray( new Long[]{} );
			long lastTenTotal = 0;
			for( int i = 1; i < stream.length; i++ )
				lastTenTotal += (stream[i] - stream[i-1]);

			// Generate the log
			String line = String.format( "(%s:%d) pdus=%,d (%,d/s); bytes=%s [e=%,d; f=%,d; d=%,d]",
			                             ip,
			                             port,
			                             pdusWritten,
			                             (lastTenTotal/9),
			                             StringUtils.humanReadableSize(bytesWritten),
			                             espduCount,
			                             firepduCount,
			                             detpduCount );
			
			// Log the log!
			logger.info( line );
		}
	}

}
