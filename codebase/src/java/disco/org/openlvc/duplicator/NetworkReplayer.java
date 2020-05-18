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

import java.io.File;
import java.net.NetworkInterface;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;

/**
 * This class reads the contents of a stored session and sents it out to the network as
 * configured in the {@link Configuration} object. 
 */
public class NetworkReplayer
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;

	// Session Reader/Replay
	private Replayer replayer;

	// DIS Settings
	private DiscoConfiguration discoConfiguration;
	private OpsCenter opscenter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public NetworkReplayer( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = configuration.getLogger();
		
		// Session Reader/Replay
		this.replayer = null;             // set in execute()
		
		// DIS Settings
		this.discoConfiguration = null;   // set in execute()
		this.opscenter = null;            // set in execute()
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
		logger.info( "" );
		
		//
		// Set up the Session Replayer
		//
		this.replayer = new Replayer( new File(configuration.getFilename()) );
		this.replayer.setLogger( this.logger );
		this.replayer.setListener( new FileListener() );
		this.replayer.setMode( configuration.isReplayRealtime() ? Replayer.Mode.RealTime :
		                                                          Replayer.Mode.AsFastAsPossible );
		this.replayer.setStatusLogging( configuration.isReplayRealtime() );
		this.replayer.setStatusLogInterval( configuration.getStatusLogInterval() );

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
		
		this.opscenter = new OpsCenter( discoConfiguration );
		this.opscenter.setListener( new NullListener() );

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
		// Log some startup information and metadata
		printWelcome();
		
		// Looping - play it some number of times
		String loopString = loopCount == Configuration.LOOP_INFINITELY ? "INDEFINITE" : ""+loopCount;
		boolean loopAgain = true;
		int loopsCompleted = 0;
		do
		{
			logger.info( "Starting replay loop %d of %s", loopsCompleted+1, loopString );

			replayer.startReplay();
			replayer.waitForSessionToFinish();
			if( (++loopsCompleted >= loopCount) && loopCount != 0 )
				loopAgain = false;
		}
		while( loopAgain );
	}

	public void shutdown()
	{
		// end the replay
		this.replayer.stopReplay();
		
		// nothing more to write - close down the ops center
		this.opscenter.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Management Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Log some information about the network settings that will be used for replay, in addition
	 * to some further metadata about the session itself
	 */
	private void printWelcome()
	{
		String address = opscenter.getConfiguration().getUdpConfiguration().getAddress().getHostAddress() +
		                 ":"+opscenter.getConfiguration().getUdpConfiguration().getPort();
		
		NetworkInterface nic = opscenter.getConfiguration().getUdpConfiguration().getNetworkInterface();
		
		logger.info( "      Address: %s", address );
		logger.info( "    Interface: (%s) %s", nic.getName(), nic.getDisplayName() );
		logger.info( " Session File: %s", configuration.getFilename() );
		logger.info( "   Loop Count: %s", configuration.getLoopCountString() );
		logger.info( "" );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: FileListener   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * PDU listener for the replay file session. It receives PDUs from the replayer and then
	 * loops them out to the OpsCenter.
	 */
	private class FileListener implements IPduListener
	{
		private String connectionInfo;
		
		@Override
		public void receive( PDU pdu )
		{
			// pass to the OpsCenter
			opscenter.send( pdu );
		}
		
		@Override
		public String toString()
		{
			if( connectionInfo == null )
			{
				// Get network information
				String ip = opscenter.getConfiguration().getUdpConfiguration().getAddress().getHostAddress();
				int port = opscenter.getConfiguration().getUdpConfiguration().getPort();
				connectionInfo = ip+":"+port;
			}
			
			return connectionInfo;
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: NullListener   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * PDU listener that just drops the packets. We use this for the DIS network we
	 * connect to because we don't care about incoming DIS packets.
	 */
	private class NullListener implements IPduListener
	{
		@Override
		public void receive( PDU pdu )
		{
			// no-op
		}
	}


}
