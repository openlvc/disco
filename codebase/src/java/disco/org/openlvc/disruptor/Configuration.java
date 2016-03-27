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
package org.openlvc.disruptor;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
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
	public static final String KEY_DISCO_EXID      = "disruptor.dis.exerciseId";

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
	public Configuration( String[] args )
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

		// see if the user specified a config file on the command line before we process it
		this.checkArgsForConfigFile( args );
		this.loadConfigFile();
		
		// pull out any command line args and use them to override all values
		this.applyCommandLine( args );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	private void loadConfigFile()
	{
		File configurationFile = new File( this.configFile );
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
		// Network Settings
		temp.getUdpConfiguration().setAddress( getDisAddress() );
		temp.getUdpConfiguration().setPort( getDisPort() );
		temp.getUdpConfiguration().setNetworkInterface( getDisNic() );
		
		// DIS Settings
		temp.getDisConfiguration().setExerciseId( getDisExerciseId() );
		
		// copy the logging configuration
		temp.getLoggingConfiguration().setLevel( properties.getProperty(KEY_DISCO_LOG_LEVEL,"INFO") );
		temp.getLoggingConfiguration().setConsoleOn( loggingConfiguration.isConsoleOn() );
		temp.getLoggingConfiguration().setFileOn( loggingConfiguration.isFileOn() );
		
		temp.setPduSender( properties.getProperty(DiscoConfiguration.PROP_PDU_SENDER) );
		temp.setPduReceiver( properties.getProperty(DiscoConfiguration.PROP_PDU_RECEIVER) );

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
	
	public void setSimulationAddress( String simulationAddress )
	{
		properties.put( KEY_SIMULATION_ADDRESS, simulationAddress );
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
	
	public void setDisExerciseId( String id )
	{
		this.properties.put( KEY_DISCO_EXID, id );
	}
	
	public short getDisExerciseId()
	{
		return Short.parseShort( properties.getProperty(KEY_DISCO_EXID,"1") );
	}

	//
	// PDU Processing
	//
	public void setPduSender( String pduSender )
	{
		if( pduSender != null )
			this.properties.put( DiscoConfiguration.PROP_PDU_SENDER, pduSender );
	}

	public void setPduReceiver( String pduReceiver )
	{
		if( pduReceiver != null )
			this.properties.put( DiscoConfiguration.PROP_PDU_RECEIVER, pduReceiver );
	}

	@Override
	public String toString()
	{
		return properties.toString();
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
			else if( argument.equalsIgnoreCase("--objects") )
				this.setObjectCount( Integer.parseInt(args[++i]) );
			else if( argument.equalsIgnoreCase("--tick-interval") )
				this.setTickInterval( Long.parseLong(args[++i]) );
			else if( argument.equalsIgnoreCase("--loops") )
				this.setLoops( Integer.parseInt(args[++i]) );
			else if( argument.equalsIgnoreCase("--simulation-address") )
				this.setSimulationAddress( args[++i]);
			else if( argument.equalsIgnoreCase("--dis-address") )
				this.setDisAddress( args[++i] );
			else if( argument.equalsIgnoreCase("--dis-port") )
				this.setDisPort( Integer.parseInt( args[++i]) );
			else if( argument.equalsIgnoreCase("--dis-nic") || argument.equalsIgnoreCase("--dis-interface") )
				this.setDisNic( args[++i] );
			else if( argument.equalsIgnoreCase("--dis-exerciseId")  || argument.equalsIgnoreCase("--dis-exercise-id") )
				this.setDisExerciseId( args[++i] );
			else if( argument.equalsIgnoreCase("--pdu-sender") )
				this.setPduSender( args[++i] );
			else if( argument.equalsIgnoreCase("--pdu-receiver") )
				this.setPduReceiver( args[++i] );
			else
				throw new DiscoException( "Unknown argument: "+argument );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "Disruptor - Load testing tool for DIS applications" );
		System.out.println( "Usage: bin/disruptor [--args]" );
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
