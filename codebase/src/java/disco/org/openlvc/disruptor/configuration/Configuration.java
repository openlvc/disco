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
package org.openlvc.disruptor.configuration;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Log4jConfiguration;

public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String KEY_CONFIG_FILE     = "disruptor.configfile";
	public static final String KEY_LOG_LEVEL       = "disruptor.loglevel";
	public static final String KEY_LOG_FILE        = "disruptor.logfile";
	
	public static final String KEY_OBJECT_COUNT    = "disruptor.objectCount";
	public static final String KEY_LOOPS           = "disruptor.loops";
	public static final String KEY_TICK_INTERVAL   = "disruptor.tickInterval";
	
	public static final String KEY_SIMULATION_ADDRESS = "disruptor.dis.simaddress";

	public static final String KEY_DISCO_LOG_LEVEL = "disruptor.disco.loglevel";
	public static final String KEY_DISCO_ADDRESS   = "disruptor.dis.address";
	public static final String KEY_DISCO_PORT      = "disruptor.dis.port";
	public static final String KEY_DISCO_NIC       = "disruptor.dis.nic";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;
	private Logger applicationLogger;
	private Log4jConfiguration loggingConfiguration;
	
	private String configFile = "etc/disruptor.config";

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration()
	{
		//
		// default configuration
		//
		// place we store all the base properties
		this.properties = new Properties();

		// logging configuration
		this.applicationLogger = null; // set on first access
		this.loggingConfiguration = new Log4jConfiguration( "disruptor" );
		this.loggingConfiguration.setConsoleOn( true );
		this.loggingConfiguration.setFileOn( false );
		this.loggingConfiguration.setLevel( "INFO" );

	}

	public Configuration( String fileLocation )
	{
		this();
		if( fileLocation != null )
			this.configFile = fileLocation;
		
		parseConfigFile( this.configFile );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void override( Arguments arguments )
	{
		this.properties.putAll( arguments.getProperties() );
	}

	private void parseConfigFile( String fileLocation )
	{
		File configurationFile = new File( fileLocation );
		if( configurationFile.exists() )
		{
    		// configuration file exists, load the properties into it
    		Properties fileProperties = new Properties();
    		try
    		{
    			fileProperties.load( configurationFile.toURI().toURL().openStream() );
    		}
    		catch( Exception e )
    		{
    			throw new RuntimeException( "Problem parsing config file: "+e.getMessage(), e );
    		}
    		
    		// store the loaded configuration
    		this.properties.putAll( fileProperties );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public DiscoConfiguration getDiscoConfiguration()
	{
		DiscoConfiguration temp = new DiscoConfiguration();
		temp.getUdpConfiguration().setAddress( getDisAddress() );
		temp.getUdpConfiguration().setPort( getDisPort() );
		temp.getUdpConfiguration().setNetworkInterface( getDisNic() );
		
		// copy the logging configuration
		temp.getLoggingConfiguration().setLevel( properties.getProperty(KEY_DISCO_LOG_LEVEL,"INFO") );
		temp.getLoggingConfiguration().setConsoleOn( loggingConfiguration.isConsoleOn() );
		temp.getLoggingConfiguration().setFileOn( loggingConfiguration.isFileOn() );
		
		return temp;
	}
	
	/** Get the Disruptor application logger. Will lazy-load configuration. */
	public Logger getDisruptorLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;
		
		// check for any properties that may have been specified on command line to override
		loggingConfiguration.setLevel( properties.getProperty(KEY_LOG_LEVEL,"INFO") );
		
		this.loggingConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "disruptor" );
		return applicationLogger;
	}

	public int getObjectCount()
	{
		return Integer.parseUnsignedInt( properties.getProperty(KEY_OBJECT_COUNT,"1000") );
	}
	
	public void setObjectCount( int objectCount )
	{
		this.properties.put( KEY_OBJECT_COUNT, ""+objectCount );
	}

	public int getLoops()
	{
		return Integer.parseUnsignedInt( properties.getProperty(KEY_LOOPS,"300") ); 
	}
	
	public void setLoops( int loops )
	{
		this.properties.put( KEY_LOOPS, ""+loops );
	}

	public long getTickInterval()
	{
		return Long.parseUnsignedLong( properties.getProperty(KEY_TICK_INTERVAL,"1000") ); 
	}
	
	public void setTickInterval( long tickInterval )
	{
		this.properties.put( KEY_TICK_INTERVAL, ""+tickInterval );
	}
	
	public void setLogLevel( String level )
	{
		loggingConfiguration.setLevel( level );
	}
	
	//
	// DIS & Networking
	//
	public String getSimulationAddress()
	{
		return properties.getProperty( KEY_SIMULATION_ADDRESS, "1-1-20913" );
	}

	public String getDisAddress()
	{
		return properties.getProperty( KEY_DISCO_ADDRESS, "BROADCAST" );
	}
	
	public void setDisAddress( String address )
	{
		this.properties.put( KEY_DISCO_ADDRESS, address );
	}
	
	public int getDisPort()
	{
		return Integer.parseUnsignedInt( properties.getProperty(KEY_DISCO_PORT,"3000") );
	}

	public void setDisPort( int port )
	{
		this.properties.put( KEY_DISCO_PORT, ""+port );
	}

	public String getDisNic()
	{
		return properties.getProperty( KEY_DISCO_NIC, "SITE_LOCAL" );
	}
	
	public void setDisNic( String iface )
	{
		this.properties.put( KEY_DISCO_NIC, iface );
	}

	@Override
	public String toString()
	{
		return properties.toString();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
