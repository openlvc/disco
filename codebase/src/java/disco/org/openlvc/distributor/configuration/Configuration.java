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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	public static final String KEY_STATUS_INTERVAL = "distributor.statusInterval";
	
	// Link Configuration
	public static final String KEY_LINKS           = "distributor.links";
	
	// Link Filtering -- 
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Map<String,LinkConfiguration> links;
	private Log4jConfiguration loggingConfiguration;
	private Logger applicationLogger;
	private String configFile = "etc/distributor.config";
	private int statusInterval = 0;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration( String[] args )
	{
		this.links = new HashMap<>();
		this.statusInterval = 0;

		// logging configuration
		this.loggingConfiguration = new Log4jConfiguration( "distributor" );
		this.loggingConfiguration.setConsoleOn( true );
		this.loggingConfiguration.setFileOn( false );
		this.loggingConfiguration.setLevel( "INFO" );
		this.applicationLogger = null; // lazy loaded via getApplicationLogger()

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
	
	public Map<String,LinkConfiguration> getLinks()
	{
		return this.links;
	}

	/**
	 * Fetch the application logger. If it hasn't been create yet, activate the
	 * stored configuration, generate the logger and return it.
	 */
	public Logger getApplicationLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;

		// activate the configuration and return the logger
		this.loggingConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "distributor" );
		return applicationLogger;
	}

	/**
	 * Return the number of seconds between periodic logging of link status. 0 means disabled.
	 */
	public int getStatusLogInterval()
	{
		return this.statusInterval;
	}

	/**
	 * Set the interval between periodic logging of link status. Set to 0 to disable logging.
	 */
	public void setStatusLogInterval( int seconds )
	{
		if( seconds >= 0 )
			this.statusInterval = seconds;
		else
			throw new IllegalArgumentException( "Cannot set status logging interval to negative number" );
	}

	public boolean isStatusLoggingEnabled()
	{
		return this.statusInterval > 0;
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
			else if( argument.equalsIgnoreCase("--status-interval") )
				this.setStatusLogInterval( Integer.parseInt(args[++i]) );
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
		else
		{
			getApplicationLogger().error( "Configuration file does not exist at "+configurationFile.getPath() );
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
		// Link Configuration
		//
		if( properties.containsKey(KEY_LINKS) )
		{
			String linkString = properties.getProperty( KEY_LINKS );
			String[] linkNames = linkString.split( "," );
			for( String linkName : linkNames )
			{
				linkName = linkName.trim();
				LinkConfiguration linkConfiguration = new LinkConfiguration( linkName, properties );
				this.links.put( linkName, linkConfiguration );
			}
		}
		
		//
		// General Configuration Properties
		//
		if( properties.containsKey(KEY_STATUS_INTERVAL) )
		{
			this.statusInterval = Integer.parseInt( properties.getProperty(KEY_STATUS_INTERVAL) );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "Distributor - Bridging and filtering between DIS networks" );
		System.out.println( "Usage: bin/distributor [--args]" );
		System.out.println( "" );

		System.out.println( "  --config-file      string   (optional)  Relative path to config file               (default: etc/distributor.config)" );
		System.out.println( "  --log-level        string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]    (default: INFO)" );
		System.out.println( "  --status-interval  int      (optional)  Interval between status logging in seconds (default: 0)" );
		System.out.println( "" );
	}
}
