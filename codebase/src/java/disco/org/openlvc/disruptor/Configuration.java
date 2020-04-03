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
	// configuration file keys
	public static final String KEY_CONFIG_FILE          = "disruptor.configfile";
	public static final String KEY_LOG_LEVEL            = "disruptor.loglevel";
	public static final String KEY_LOG_FILE             = "disruptor.logfile";

	public static final String KEY_OBJECT_COUNT         = "disruptor.objectCount";
	public static final String KEY_LOOPS                = "disruptor.loops";
	public static final String KEY_TICK_INTERVAL        = "disruptor.tickInterval";

	public static final String KEY_SIMULATION_ADDRESS   = "disruptor.dis.simaddress";

	public static final String KEY_DISCO_LOG_LEVEL      = "disruptor.disco.loglevel";
	public static final String KEY_DISCO_ADDRESS        = "disruptor.dis.address";
	public static final String KEY_DISCO_PORT           = "disruptor.dis.port";
	public static final String KEY_DISCO_NIC            = "disruptor.dis.nic";
	public static final String KEY_DISCO_EXID           = "disruptor.dis.exerciseId";
	
	// configuration defaults
	private static final String DEFAULT_CONFIG_FILE     = "etc/disruptor.config";
	private static final String DEFAULT_PLAN_FILE       = "etc/disruptor.plan";
	private static final String DEFAULT_LOOPS           = "300";
	private static final String DEFAULT_TICK_INTERVAL   = "1000";
	private static final String DEFAULT_SIM_ADDRESS     = "1-1-20913";
	private static final String DEFAULT_LOG_LEVEL       = "INFO";
	private static final String DEFAULT_DIS_EXERCISE_ID = "1";
	private static final String DEFAULT_DIS_NIC         = "SITE_LOCAL";
	private static final String DEFAULT_DIS_PORT        = "3000";
	private static final String DEFAULT_DIS_ADDRESS     = "BROADCAST";
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;

	private Logger applicationLogger;
	private Log4jConfiguration loggingConfiguration;

	private String configFile = DEFAULT_CONFIG_FILE;
	private String planFile   = DEFAULT_PLAN_FILE;

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
		this.loggingConfiguration.setLevel( DEFAULT_LOG_LEVEL );

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
		temp.getLoggingConfiguration().setLevel( properties.getProperty(KEY_DISCO_LOG_LEVEL,DEFAULT_LOG_LEVEL) );
		temp.getLoggingConfiguration().setConsoleOn( loggingConfiguration.isConsoleOn() );
		temp.getLoggingConfiguration().setFileOn( loggingConfiguration.isFileOn() );

		temp.setPduSender( properties.getProperty(DiscoConfiguration.PROP_PDU_SENDER) );
		temp.setPduReceiver( properties.getProperty(DiscoConfiguration.PROP_PDU_RECEIVER) );

		return temp;
	}
	
	public String getPlanFile()
	{
		return this.planFile;
	}

	/** Get the Disruptor application logger. Will lazy-load configuration. */
	public Logger getDisruptorLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;

		// check for any properties that may have been specified on command line to override
		loggingConfiguration.setLevel( properties.getProperty(KEY_LOG_LEVEL,DEFAULT_LOG_LEVEL) );

		this.loggingConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "disruptor" );
		return applicationLogger;
	}

	public int getLoops()
	{
		return Integer.parseUnsignedInt( properties.getProperty(KEY_LOOPS,DEFAULT_LOOPS) );
	}

	public void setLoops( int loops )
	{
		this.properties.put( KEY_LOOPS, ""+loops );
	}

	public long getTickInterval()
	{
		return Long.parseUnsignedLong( properties.getProperty(KEY_TICK_INTERVAL,DEFAULT_TICK_INTERVAL) );
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
		return properties.getProperty( KEY_SIMULATION_ADDRESS, DEFAULT_SIM_ADDRESS );
	}

	public void setSimulationAddress( String simulationAddress )
	{
		properties.put( KEY_SIMULATION_ADDRESS, simulationAddress );
	}

	public String getDisAddress()
	{
		return properties.getProperty( KEY_DISCO_ADDRESS, DEFAULT_DIS_ADDRESS );
	}

	public void setDisAddress( String address )
	{
		this.properties.put( KEY_DISCO_ADDRESS, address );
	}

	public int getDisPort()
	{
		return Integer.parseUnsignedInt( properties.getProperty(KEY_DISCO_PORT,DEFAULT_DIS_PORT) );
	}

	public void setDisPort( int port )
	{
		this.properties.put( KEY_DISCO_PORT, ""+port );
	}

	public String getDisNic()
	{
		return properties.getProperty( KEY_DISCO_NIC, DEFAULT_DIS_NIC );
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
		return Short.parseShort( properties.getProperty(KEY_DISCO_EXID,DEFAULT_DIS_EXERCISE_ID) );
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
			else if( argument.equalsIgnoreCase("--plan-file") )
				this.planFile = args[++i];
			else if( argument.equalsIgnoreCase("--log-level") )
				this.loggingConfiguration.setLevel( args[++i] );
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

		System.out.println( "  --config-file         string   (optional)  path to configuration file                 (default: "+DEFAULT_CONFIG_FILE+")" );
		System.out.println( "  --plan-file           string   (optional)  path to plan file                          (default: "+DEFAULT_PLAN_FILE+")" );
		System.out.println( "  --loops               integer  (optional)  Numbber of sim-loops to run                (default: "+DEFAULT_LOOPS+")" );
		System.out.println( "  --tick-interval       integer  (optional)  Millis between update tick cycle           (default: "+DEFAULT_TICK_INTERVAL+")");
		System.out.println( "  --simulation-address  string   (optional)  Simulation Address                         (default: "+DEFAULT_SIM_ADDRESS+")" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]    (default: "+DEFAULT_LOG_LEVEL+")" );
		System.out.println( "  --dis-exercise-id     short    (optional)  Ex ID to send in outgoing and only recv on (default: "+DEFAULT_DIS_EXERCISE_ID+")" );
		System.out.println( "  --dis-address         string   (optional)  Where to send DIS traffic, or BROADCAST    (default: "+DEFAULT_DIS_ADDRESS+")" );
		System.out.println( "  --dis-port            integer  (optional)  Port for DIS traffic                       (default: "+DEFAULT_DIS_PORT+")" );
		System.out.println( "  --dis-interface       string   (optional)  NIC to use. Address or a special symbol:   (default: "+DEFAULT_DIS_NIC+")" );
		System.out.println( "                                             LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL" );
		System.out.println( "  --pdu-sender          string   (optional)  single-thread, thread-pool, simple         (default: single-thread)" );
		System.out.println( "  --pdu-receiver        string   (optional)  single-thread, thread-pool, simple         (default: single-thread)" );
		System.out.println( "" );
	}

}
