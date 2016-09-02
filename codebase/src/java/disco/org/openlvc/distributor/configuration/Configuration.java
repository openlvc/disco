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
package org.openlvc.distributor.configuration;

import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.Log4jConfiguration;

public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String KEY_CONFIG_FILE     = "distributor.configfile";
	public static final String KEY_LOG_LEVEL       = "distributor.loglevel";
	public static final String KEY_LOG_FILE        = "distributor.logfile";
	
	// Site Configuration
	public static final String KEY_SITES           = "distributor.site";

	
	//
	// Configuration Key Snippets
	//
	// Site-specific configurations will appear multiple times within the configuration.
	// All keys will be prefixed with "distributor.[site]". A sample config might look
	// as follows:
	//
	// distributor.sites = per, nyc, sgp
	// distributor.per.dis.address = BROADCAST
	// distributor.per.dis.port = 3000
	// distributor.per...
	//
	// The keys below are the snippets that follow the prefix
	//
	private static final String KEY_SITE_DIS_ADDRESS    = "dis.address";
	private static final String KEY_SITE_DIS_PORT       = "dis.port";
	private static final String KEY_SITE_DIS_NIC        = "dis.nic";
	private static final String KEY_SITE_UDP_SENDBUFFER = "udp.sendBuffer";
	private static final String KEY_SITE_UDP_RECVBUFFER = "udp.recvBuffer";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;
	private Logger applicationLogger;
	private Log4jConfiguration loggingConfiguration;	
	private String configFile = "etc/distributor.config";

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration( String[] args )
	{
		//
		// default configuration
		//
		// place we store all the base properties
		this.properties = new Properties();

		// logging configuration
		this.applicationLogger = null; // set on first access
		this.loggingConfiguration = new Log4jConfiguration( "distributor" );
		this.loggingConfiguration.setConsoleOn( true );
		this.loggingConfiguration.setFileOn( false );
		this.loggingConfiguration.setLevel( "INFO" );

		// see if the user specified a config file on the command line before we process it
		this.checkArgsForConfigFile( args );
		this.loadConfigFile();
		
		// pull out any command line args and use them to override all values
		this.applyCommandLine( args );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "Distributor - Bridging and filtering between DIS networks" );
		System.out.println( "Usage: bin/distributor [--args]" );
		System.out.println( "" );

		System.out.println( "  --config-file         integer  (optional)  Number of objects to create                (default: 100)" );
		System.out.println( "  --objects             integer  (optional)  Number of objects to create                (default: 100)" );
		System.out.println( "  --loops               integer  (optional)  Numbber of sim-loops to run                (default: 300)" );
		System.out.println( "  --tick-interval       integer  (optional)  Millis between update tick cycle           (default: 1000)");
		System.out.println( "  --simulation-address  string   (optional)  Simulation Address                         (default: 1-1-20913)" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]    (default: INFO)" );
		System.out.println( "  --dis-exercise-id     short    (optional)  Ex ID to send in outgoing and only recv on (default: 1)" );
		System.out.println( "  --dis-address         string   (optional)  Where to send DIS traffic, or BROADCAST    (default: BROADCAST)" );
		System.out.println( "  --dis-port            integer  (optional)  Port for DIS traffic                       (default: 3000)" );
		System.out.println( "  --dis-interface       string   (optional)  NIC to use. Address or a special symbol:   (default: SITE_LOCAL)" );
		System.out.println( "                                             LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL" );
		System.out.println( "  --pdu-sender          string   (optional)  single-thread, thread-pool, simple         (default: single-thread)" );
		System.out.println( "  --pdu-receiver        string   (optional)  single-thread, thread-pool, simple         (default: single-thread)" );
		System.out.println( "" );
	}
}
