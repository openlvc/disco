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

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.utils.NetworkUtils;

/**
 * The DIS Recorder will gather up all DIS traffic on its configured network and save it to
 * a file for later use, or can read from recorded files and replay the traffic to the network,
 * rewriting some of the header information to suit local needs.
 */
public class Main
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String[] args;
	private Recorder recorder;      // store so we can access from shutdown hook
	private NetworkReplayer replay; // store so we can access from shutdown hook

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Main( String[] args )
	{
		this.args = args;
		this.recorder = null;
		this.replay = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private void runMain()
	{
		// register the shutdown hook so we can gracefully exit
		Runtime.getRuntime().addShutdownHook( new ShutdownHook() );
		
		// parse the full command line and start a recorder
		Configuration configuration = new Configuration( args );

		// print a welcome message
		printWelcome( configuration.getLogger() );
		
		// start recording or replaying - whichever we have been told to
		if( configuration.isRecording() )
		{
			this.recorder = new Recorder( configuration );
			this.recorder.execute();
		}
		else
		{
			this.replay = new NetworkReplayer( configuration );
			this.replay.execute();
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			Configuration.printHelp();
			return;
		}
		
		// check the args for the --help command
		for( String temp : args )
		{
			if( temp.equalsIgnoreCase("--help") )
			{
				Configuration.printHelp();
				return;
			}
		}
		
		// Run this thing
		new Main(args).runMain();
	}
	
	public static void printWelcome( Logger logger )
	{
		logger.info( "       Welcome to the Open LVC DIS Duplicator      " );
		logger.info( "    ____              ___            __            " );
		logger.info( "   / __ \\__  ______  / (_)________ _/ /_____  _____" );
		logger.info( "  / / / / / / / __ \\/ / / ___/ __ `/ __/ __ \\/ ___/" );
		logger.info( " / /_/ / /_/ / /_/ / / / /__/ /_/ / /_/ /_/ / /    " );
		logger.info( "/_____/\\__,_/ .___/_/_/\\___/\\__,_/\\__/\\____/_/     " );
		logger.info( "           /_/                                     " );
		logger.info( "" );
		logger.info( " The Duplicator is part of the Open LVC DIS family " );
		logger.info( " Version: "+DiscoConfiguration.getVersion() );
		logger.info( "" );
		
		// Log some network interface information
		NetworkUtils.logNetworkInterfaceInformation( logger );
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Shutdown Hook   ///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	public class ShutdownHook extends Thread
	{
		@Override
		public void run()
		{
			if( recorder != null )
				recorder.shutdown();
			
			if( replay != null )
				replay.shutdown();
		}
	}
}
