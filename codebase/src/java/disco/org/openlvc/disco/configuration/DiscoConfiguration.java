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
	/** Should parsing problems result in exceptions? */
	public static boolean STRICT_MODE = Boolean.valueOf( System.getProperty("disco.strict","false") );
	
	public static final String PROP_CONNECTION = "disco.connection";

	public static final String PROP_LOG_LEVEL   = "disco.log.level";
	public static final String PROP_LOG_CONSOLE = "disco.log.console"; // true, false
	public static final String PROP_LOG_FILE    = "disco.log.file";    // filename
	
	public static final String PROP_PDU_SENDER   = "disco.pdu.sender";
	public static final String PROP_PDU_RECEIVER = "disco.pdu.receiver";
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected Properties properties;
	
	private Log4jConfiguration loggingConfiguration;
	private Logger applicationLogger;
	
	private DisConfiguration disConfiguration;
	private UdpConfiguration udpConfiguration;
	private RprConfiguration rprConfiguration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DiscoConfiguration()
	{
		this.properties = new Properties();
		this.loggingConfiguration = null; // lazy loaded
		this.disConfiguration = new DisConfiguration( this );
		this.udpConfiguration = new UdpConfiguration( this );
		this.rprConfiguration = new RprConfiguration( this );
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
			this.applicationLogger = LogManager.getFormatterLogger( getLoggingConfiguration().getAppName() );
		}
	
		return applicationLogger;
	}

	/**
	 * Override the locally configured logger and just use the provided logger. Note that this
	 * has to be done before the `OpsCenter` is started because that is when all the other
	 * components grab a reference to the logger. After that, they just use their local ref,
	 * so changing it here will do nothing.
	 */
	public void setDiscoLogger( Logger logger )
	{
		if( logger == null )
			return;

		// If we already have an active logger, print a warning to it
		if( this.applicationLogger != null )
		{
			this.applicationLogger.warn( "We have been asked to switch to a different logger" );
			this.applicationLogger.warn( "See stacktrace for who asked us to do this:" );
			try { throw new Exception(); } catch( Exception e )
			{ this.applicationLogger.warn(e); }
		}

		// Set the logger
		this.applicationLogger = logger;
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
		String loglevel = System.getProperty( PROP_LOG_LEVEL, "INFO" );
		boolean logToConsole = getBoolean( System.getProperty(PROP_LOG_CONSOLE,"true") );
		String logFile = System.getProperty( PROP_LOG_FILE, "disco.log" );
		boolean logToFile = false;
		try
		{
			logToFile = getBoolean(System.getProperty(PROP_LOG_FILE,"false") );
		}
		catch( IllegalArgumentException ia )
		{
			// must point to a log file, which is an implicit "yes, turn it on"
			logToFile = true;
		}

		// Set up the logging configuration based on the above information
		Log4jConfiguration configuration = new Log4jConfiguration( "disco" );
		configuration.setLevel( loglevel );
		configuration.setConsoleOn( logToConsole );
		if( logToFile )
		{
			configuration.setFileOn( true );
			configuration.setFile( logFile );
		}
		
		this.loggingConfiguration = configuration;
		return configuration;
	}

	public void setLoggingConfiguration( Log4jConfiguration configuration )
	{
		this.loggingConfiguration = configuration;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Processing Properties   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getPduSender()
	{
		return properties.getProperty( PROP_PDU_SENDER, "single-thread" );
	}

	/** See PduSender.create() for valid values */
	public void setPduSender( String sender )
	{
		if( sender != null )
			properties.setProperty( PROP_PDU_SENDER, sender );
	}

	public String getPduReceiver()
	{
		return properties.getProperty( PROP_PDU_RECEIVER, "single-thread" );
	}

	/** See PduReceiver.create() for valid values */
	public void setPduReceiver( String receiver )
	{
		if( receiver != null )
			properties.setProperty( PROP_PDU_RECEIVER, receiver );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Connection Properties   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getConnection()
	{
		return properties.getProperty( PROP_CONNECTION, "udp" );
	}
	
	public void setConnection( String connection )
	{
		properties.setProperty( PROP_CONNECTION, connection );
	}

	public DisConfiguration getDisConfiguration()
	{
		return this.disConfiguration;
	}

	public UdpConfiguration getUdpConfiguration()
	{
		return this.udpConfiguration;
	}
	
	public RprConfiguration getRprConfiguration()
	{
		return this.rprConfiguration;
	}
	
	
	protected String getProperty( String key, String defaultValue )
	{
		return properties.getProperty( key, defaultValue );
	}

	protected void setProperty( String key, String value )
	{
		properties.setProperty( key, value );
	}

	protected boolean isProperty( String key, boolean defaultValue )
	{
		return getBoolean( properties.getProperty(key,""+defaultValue) );
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
