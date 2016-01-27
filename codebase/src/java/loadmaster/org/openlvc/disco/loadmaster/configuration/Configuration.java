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
package org.openlvc.disco.loadmaster.configuration;

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
	public static final String KEY_CONFIG_FILE     = "lm.configfile";
	public static final String KEY_LOG_LEVEL       = "lm.loglevel";
	public static final String KEY_LOG_FILE        = "lm.logfile";
	
	public static final String KEY_OBJECT_COUNT    = "lm.objectCount";
	public static final String KEY_LOOPS           = "lm.loops";
	public static final String KEY_TICK_INTERVAL   = "lm.tickInterval";
	
	public static final String KEY_SIMULATION_ADDRESS = "lm.dis.simaddress";

	public static final String KEY_DISCO_LOG_LEVEL = "lm.dis.loglevel";
	public static final String KEY_DISCO_ADDRESS   = "lm.dis.address";
	public static final String KEY_DISCO_PORT      = "lm.dis.port";
	public static final String KEY_DISCO_NIC       = "lm.dis.nic";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;
	private Logger applicationLogger;
	private Log4jConfiguration appLoggerConfiguration;
	
	private String configFile = "etc/loadmaster.config";

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
		this.appLoggerConfiguration = new Log4jConfiguration( "lm" );
		this.appLoggerConfiguration.setConsoleOn( true );
		this.appLoggerConfiguration.setFileOn( false );
		this.appLoggerConfiguration.setLevel( "INFO" );

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
		temp.setLoggingConfiguration( this.appLoggerConfiguration );
		temp.getNetworkConfiguration().setAddress( getDisAddress() );
		temp.getNetworkConfiguration().setPort( getDisPort() );
		temp.getNetworkConfiguration().setNetworkInterface( getDisNic() );
		
		return temp;
	}
	
	/** Get the Load Master application logger. Will lazy-load configuration. */
	public Logger getLMLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;
		
		this.appLoggerConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "lm" );
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
		return Integer.parseUnsignedInt( properties.getProperty(KEY_LOOPS,"100") ); 
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
