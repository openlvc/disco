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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openlvc.disco.DiscoException;
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
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Map<String,SiteConfiguration> sites;
	private Log4jConfiguration loggingConfiguration;	
	private String configFile = "etc/distributor.config";

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration( String[] args )
	{
		this.sites = new HashMap<>();

		// logging configuration
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
	public Log4jConfiguration getLogConfiguration()
	{
		return this.loggingConfiguration;
	}

	public void setLogLevel( String loglevel )
	{
		this.loggingConfiguration.setLevel( loglevel );
	}

	public void setLogFile( String logfile )
	{
		this.loggingConfiguration.setFile( logfile );
		this.loggingConfiguration.setFileOn( true );
	}
	
	public Map<String,SiteConfiguration> getSites()
	{
		return this.sites;
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Command Line Argument Methods   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** If --config-file is in the args, load it into the local var for processing. We do
	    this separately so we can load the config file before the command line args, which
	    we do later so that they override all other values */
	private void checkArgsForConfigFile( String[] args )
	{
		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("--config-file") )
			{
				this.configFile = args[++i];
				return;
			}
		}
	}

	/**
	 * Apply the given command line args to override any defaults that we have
	 */
	private void applyCommandLine( String[] args ) throws DiscoException
	{
		for( int i = 0; i < args.length; i++ )
		{
			String argument = args[i];
			if( argument.equalsIgnoreCase("--config-file") )
				this.configFile = args[++i];
			else if( argument.equalsIgnoreCase("--log-level") )
				this.loggingConfiguration.setLevel( args[++i] );
			else
				throw new DiscoException( "Unknown argument: "+argument );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Configuration File Loading   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void loadConfigFile()
	{
		//
		// Load the configuration file into a Properties object
		//
		Properties properties = new Properties();
		File configurationFile = new File( this.configFile );
		if( configurationFile.exists() )
		{
    		// configuration file exists, load the properties into it
    		try
    		{
    			properties.load( configurationFile.toURI().toURL().openStream() );
    		}
    		catch( Exception e )
    		{
    			throw new RuntimeException( "Problem parsing config file: "+e.getMessage(), e );
    		}
		}
		
		//
		// pull the logging configuration out of the properties
		//
		if( properties.containsKey(KEY_LOG_FILE) )
		{
			this.loggingConfiguration.setFile(properties.getProperty(KEY_LOG_FILE));
			this.loggingConfiguration.setFileOn( true );
		}

		if( properties.containsKey(KEY_LOG_LEVEL) )
			this.loggingConfiguration.setLevel( properties.getProperty(KEY_LOG_LEVEL) );
		
		//
		// Site Configuration
		//
		if( properties.containsKey(KEY_SITES) )
		{
			String siteString = properties.getProperty( KEY_SITES );
			String[] siteNames = siteString.split( "," );
			for( String siteName : siteNames )
			{
				siteName = siteName.trim();
				SiteConfiguration siteConfiguration = new SiteConfiguration( siteName, properties );
				this.sites.put( siteName, siteConfiguration );
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( " Distributor - Bridging and filtering between DIS networks" );
		builder.append( "\n Version 0.0" ); // FIXME
		builder.append( "\n" );

		builder.append( "\n    config file: "+configFile );
		builder.append( "\n       log file: "+loggingConfiguration.getFile() );
		builder.append( "\n      log level: "+loggingConfiguration.getLevel() );
		builder.append( "\n" );
		builder.append( "\n ======== Site Configuration ========" );
		builder.append( "\n  "+sites.size()+" sites configured: "+sites.keySet() );
		builder.append( "\n" );
		for( SiteConfiguration config : sites.values() )
		{
			builder.append( "\n ----------------------------" );
			builder.append( "\n Site Name: "+config.getName() );
			builder.append( "\n                 Mode: "+config.getMode() );
			if( config.getMode() == SiteConfiguration.Mode.DIS )
			{
				builder.append( "\n     DIS Adddress: "+config.getDisAddress() );
				builder.append( "\n         DIS Port: "+config.getDisPort() );
				builder.append( "\n          DIS NIC: "+config.getDisNic() );
			}
			else
			{
				builder.append( "\n         WAN Adddress: "+config.getWanAddress() );
				builder.append( "\n             WAN Port: "+config.getWanPort() );
				//builder.append( "\n              WAN NIC: "+config.getWanNic() );
				builder.append( "\n         WAN Bundling: "+config.isWanBundling() );
				builder.append( "\n  WAN Bundle Max Size: "+config.getWanBundlingSizeBytes() );
				builder.append( "\n  WAN Bundle Max Time: "+config.getWanBundlingTime() );
				
			}
			builder.append( "\n" );
		}
		
		return builder.toString();
	}

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
