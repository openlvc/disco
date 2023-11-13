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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.utils.StringUtils;

/**
 * The {@link Recorder} will connect to a DIS network as per its {@link Configuration} and 
 * dump all the PDUs it receives into a session that we can replay later using an underlying
 * {@link SessionWriter}. 
 */
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

	// Session Writing
	private SessionWriter sessionWriter;
	
	// PDU Metrics
	private long bytesWritten;
	private PduCounter pduCounter;
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
		
		// Session Writing
		this.sessionWriter = new SessionWriter( configuration.getFilename() );
		
		// PDU Metrics
		this.bytesWritten = 0;
		this.pduCounter = new PduCounter();
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
		// Prepare the Session for writing before we connect
		// to the DIS network and have incoming PDUs to write.
		//
		this.sessionWriter.open();
		
		//
		// Set up the Disco Configuration
		//
		// Create the Disco configuration from the base config data
		// DIS Configuration
		this.discoConfiguration = new DiscoConfiguration();
		discoConfiguration.getLoggingConfiguration().disable();
		discoConfiguration.getUdpConfiguration().setAddress( configuration.getDisAddress() );
		discoConfiguration.getUdpConfiguration().setPort( configuration.getDisPort() );
		discoConfiguration.getUdpConfiguration().setNetworkInterface( configuration.getDisInterface() );
		discoConfiguration.getDisConfiguration().setExerciseId( configuration.getDisExerciseId() );
		// HLA Configuration
		discoConfiguration.getRprConfiguration().setFederateName( configuration.getHlaFederateName() );
		discoConfiguration.getRprConfiguration().setFederationName( configuration.getHlaFederationName() );
		discoConfiguration.getRprConfiguration().setRtiProvider( configuration.getHlaRtiProvider() );
		discoConfiguration.getRprConfiguration().setRtiInstallDir( configuration.getHlaRtiInstallDir() );
		discoConfiguration.getRprConfiguration().setCreateFederation( configuration.isHlaCreateFederation() );
		discoConfiguration.getRprConfiguration().setLocalSettings( configuration.getHlaRtiLocalSettings() );
		if( configuration.useHla() )
			discoConfiguration.setConnection( "rpr" );
		// PDU processing options
		discoConfiguration.setPduSender( configuration.getPduSender() );
		discoConfiguration.setPduReceiver( configuration.getPduReceiver() );

		// Tell Disco to process every PDU as an UnparsedPdu so that we don't get
		// caught up on unsupported PDU types
		DiscoConfiguration.set( Flag.UnparsedExclusive );
		
		// Create the OpsCenter and link up with it
		this.opscenter = new OpsCenter( discoConfiguration );
		this.opscenter.setPduListener( this );
		
		//
		// Kick things off
		//
		this.opscenter.open();
		logger.info( "Recorder is active - DO YOUR WORST" );
		
		// Start the activity logger
		this.activityTimer =  new Timer( "Activity" );
		this.activityLogger = new ActivityLogger();
		long interval = configuration.getStatusLogInterval();
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
		logger.info( "Ops Center has closed; no more DIS traffic" );
		
		// Stop the Session Writer
		this.sessionWriter.close();
		logger.info( "Session has been flushed and closed" );
		
		// Kill the activity logger
		this.activityTimer.cancel();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Management Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void receive( PDU pdu )
	{
		// increment count for this PDU type
		pduCounter.handle( pdu );
		bytesWritten += pdu.getContentLength();
		
		sessionWriter.add( pdu ); // non-blocking call
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Activity Logger   ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class PduRateLogger extends TimerTask
	{
		public void run()
		{
			// Get PDU average
			lastTenSeconds.add( pduCounter.getCount() );
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
			long totalCount = pduCounter.getCount();
			long espduCount = pduCounter.getCount( PduType.EntityState );
			long firepduCount = pduCounter.getCount( PduType.Fire );
			long detpduCount = pduCounter.getCount( PduType.Detonation );

			// Get PDU average
			Long[] stream = lastTenSeconds.toArray( new Long[]{} );
			long lastTenTotal = 0;
			for( int i = 1; i < stream.length; i++ )
				lastTenTotal += (stream[i] - stream[i-1]);

			// Generate the log
			String line = String.format( "(%s:%d) pdus=%,d (%,d/s); bytes=%s [e=%,d; f=%,d; d=%,d]",
			                             ip,
			                             port,
			                             totalCount,
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
