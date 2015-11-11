/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.configuration;

import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscoConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String PROP_PROVIDER = "disco.provider";

	private static final String PROP_LOG_LEVEL = "disco.log.level";
	private static final String PROP_LOG_CONSOLE = "disco.log.console"; // true, false
	private static final String PROP_LOG_FILE = "disco.log.file";       // filename
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Log4jConfiguration loggingConfiguration;
	private Logger applicationLogger;
	
	private UdpDatasourceConfig networkConfiguration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DiscoConfiguration()
	{
		this.loggingConfiguration = null; // lazy loaded
		this.networkConfiguration = new UdpDatasourceConfig();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Logging Configuration   //////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Get the root application logger according to the current logging configuration.
	    Root logger will be "disco". */
	public Logger getDiscoLogger()
	{
		if( this.applicationLogger == null )
		{
			getLoggingConfiguration().activateConfiguration();
			this.applicationLogger = LogManager.getFormatterLogger( "disco" );
		}
	
		return applicationLogger;
	}

	/**
	 * Return the default log4j configuration, updated with any information found in the
	 * configuration system properties (from configuration file or system properties).
	 */
	public Log4jConfiguration getLoggingConfiguration()
	{
		if( this.loggingConfiguration != null )
			return loggingConfiguration;

		// we haven't got a configuration yet - build one
		Log4jConfiguration configuration = new Log4jConfiguration( "disco" );
		configuration.setLevel( getLogLevel() );
		configuration.setConsoleOn( isLogToConsole() );
		if( isLogToFile() )
		{
			configuration.setFileOn( true );
			configuration.setFile( getLogFile() );
		}
		
		return configuration;
	}

	public void setLoggingConfiguration( Log4jConfiguration configuration )
	{
		this.loggingConfiguration = configuration;
	}

	public String getLogLevel()
	{
		return System.getProperty( PROP_LOG_LEVEL, "DEBUG" );
	}
	
	public boolean isLogToConsole()
	{
		return getBoolean( System.getProperty(PROP_LOG_CONSOLE,"true") );
	}

	public boolean isLogToFile()
	{
		String value = System.getProperty( PROP_LOG_FILE, "true" );
		if( value.equals("false") )
			return false;
		else
			return true;
	}

	public String getLogFile()
	{
		return System.getProperty( PROP_LOG_FILE, "disco.log" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Provider Properties   //////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getProvider()
	{
		return System.getProperty( PROP_PROVIDER, "network.udp" );
	}
	
	public UdpDatasourceConfig getNetworkConfiguration()
	{
		return this.networkConfiguration;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	private static final boolean getBoolean( String value )
	{
		value = value.trim();
		if( value.equalsIgnoreCase("true") ||
			value.equalsIgnoreCase("yes") ||
			value.equalsIgnoreCase("on") )
		{
			return true;
		}
		else if( value.equalsIgnoreCase("false") ||
			value.equalsIgnoreCase("no") ||
			value.equalsIgnoreCase("off") )
		{
			return false;
		}
		else
		{
			throw new IllegalArgumentException( "Must be on/off, true/false or yes/no: "+value );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// System Properties    ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Load the properties file "build.properties" into the system properties and return
	 * the value of the value of "${build.version} (build ${build.number})"
	 */
	public static String getVersion()
	{
		Properties properties = new Properties();
		if( System.getProperty("build.version") == null )
		{
			try
			{
				URL url = ClassLoader.getSystemResource( "build.properties" );
				properties.load( url.openStream() );
			}
			catch( Exception e )
			{
				// do nothing, not much we can do
			}
		}
		
		// Get the build number
		String buildVersion = properties.getProperty( "build.version", "unknown" );
		String buildNumber = properties.getProperty( "build.number", "unknown" );
		return buildVersion + " (build "+buildNumber+")";
	}

}
