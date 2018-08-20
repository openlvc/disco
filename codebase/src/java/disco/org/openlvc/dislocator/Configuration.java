/*
 *   Copyright 2018 Open LVC Project.
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
package org.openlvc.dislocator;

import java.io.File;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Log4jConfiguration;
import org.openlvc.disco.utils.CommandList;
import org.openlvc.disco.utils.NetworkUtils;

import net.sf.marineapi.nmea.sentence.SentenceId;

public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String KEY_CONFIG_FILE     = "dislocator.configfile";
	public static final String KEY_LOG_LEVEL       = "dislocator.loglevel";
	public static final String KEY_LOG_FILE        = "dislocator.logfile";

	/** What is the mode of the dislocator: reflect network or replay from recorded session? */
	public static final String KEY_MODE            = "dislocator.mode";
	public static final String KEY_SESSION_FILE    = "dislocator.sessionFile";
	
	/** Marking of the entity that we are tracking */
	public static final String KEY_TRACKING_ENTITY = "dislocator.tracking";

	public static final String KEY_DISCO_LOG_LEVEL = "dislocator.disco.loglevel";
	public static final String KEY_DISCO_ADDRESS   = "dislocator.dis.address";
	public static final String KEY_DISCO_PORT      = "dislocator.dis.port";
	public static final String KEY_DISCO_NIC       = "dislocator.dis.nic";
	public static final String KEY_DISCO_EXID      = "dislocator.dis.exerciseId";
	
	public static final String KEY_TCP_ADDRESS     = "dislocator.tcp.address";
	public static final String KEY_TCP_PORT        = "dislocator.tcp.port";

	public static final String KEY_NMEA_INTERVAL   = "dislocator.nmea.interval";
	public static final String KEY_NMEA_FORMAT     = "dislocator.nmea.format";
	public static final String KEY_NMEA_TIME       = "dislocator.nmea.timezone"; // UTC (default) or Local
	

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
		this.loggingConfiguration = new Log4jConfiguration( "dislocator" );
		this.loggingConfiguration.setConsoleOn( true );
		this.loggingConfiguration.setFileOn( false );
		this.loggingConfiguration.setLevel( "INFO" );
		
		this.configFile = "etc/dislocator.config";

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
	
	/** Get the Dislocator application logger. Will lazy-load configuration. */
	public Logger getDislocatorLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;
		
		// check for any properties that may have been specified on command line to override
		loggingConfiguration.setLevel( properties.getProperty(KEY_LOG_LEVEL,"INFO") );
		
		this.loggingConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "dislocator" );
		return applicationLogger;
	}

	public void setLogLevel( String level )
	{
		loggingConfiguration.setLevel( level );
	}

	// Replay Mode Settings
	/** @return Are we reading DIS traffic from the network? True if so, false otherwise. */
	public boolean isModeNetwork()
	{
		String mode = properties.getProperty( KEY_MODE, "Network" );
		return mode.equalsIgnoreCase( "Network" );
	}
	
	/** @return Are we reading DIS traffic from a session file? True if so, false otherwise. */
	public boolean isModeFile()
	{
		String mode = properties.getProperty( KEY_MODE, "Network" );
		return mode.equalsIgnoreCase( "File" );
	}
	
	public void setMode( String mode )
	{
		if( mode == null )
			throw new DiscoException( "Cannot set mode to null" );

		if( mode.equalsIgnoreCase("Network") == false &&
			mode.equalsIgnoreCase("File") == false )
		{
			throw new DiscoException( "Mode must be either Network or File. Received: "+mode );
		}
		else
		{
			properties.setProperty( KEY_MODE, mode );
		}
	}
	public File getSessionFile()
	{
		return new File( properties.getProperty(KEY_SESSION_FILE,"duplicator.session") );
	}
	
	public void setSessionFile( File file )
	{
		properties.setProperty( KEY_SESSION_FILE, file.getAbsolutePath() );
	}
	
	// Entity Settings
	/**
	 * @return The marking of the DIS entity we are tracking and reflecting the location of.
	 */
	public String getTrackingEntity()
	{
		return properties.getProperty( KEY_TRACKING_ENTITY );
	}

	/**
	 * The marking of the DIS entity that we wish to track the location for. When watching
	 * the DIS network, we look for an entity with this marking and once found, we publish
	 * its position in NEMA 0183 format to all listeners on the TCP/IP socket.
	 * 
	 * @param marking The marking of the entity we wish to track
	 */
	public void setTrackingEntity( String marking )
	{
		properties.setProperty( KEY_TRACKING_ENTITY, marking );
	}

	/**
	 * @return <code>true<code> if the tracking entity has been set; false otherwise.
	 *        Will return false if {@link #getTrackingEntity()} returns null, an empty
	 *        or whitespace-only string, or the default config file value of "<marking>".
	 *        Will return true for all other cases.
	 */
	public boolean hasTrackingEntity()
	{
		String marking = getTrackingEntity();
		if( marking == null )
			return false;
		
		marking = marking.trim();
		if( marking.equals("") )
			return false;
		
		// is it the default value?
		if( marking.equalsIgnoreCase("<marking>") )
			return false;
		
		return true;
	}
	
	//
	// DIS & Networking
	//
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


	//
	// NEMA TCP Server
	//
	/**
	 * Set the address to use for the NEMA TCP server to listen on. You can use either
	 * an explicit address, or one of the symbolic values: LOOPBACK, LINK_LOCAL, SITE_LOCAL
	 * or GLOBAL, and it will automatically pick the first address it finds that matches.
	 * 
	 * @param address The address string (as IP or symbolic name)
	 */
	public void setTcpServerAddress( String address )
	{
		properties.setProperty( KEY_TCP_ADDRESS, address );
	}
	
	public String getTcpServerAddressString()
	{
		return properties.getProperty( KEY_TCP_ADDRESS, "LOOPBACK" );
	}
	
	public InetAddress getTcpServerAddress()
	{
		return NetworkUtils.resolveInetAddress( getTcpServerAddressString() );
	}

	public int getTcpServerPort()
	{
		return Integer.parseInt( properties.getProperty(KEY_TCP_PORT,"2999") );
	}

	public void setTcpServerPort( int port )
	{
		properties.setProperty( KEY_TCP_PORT, ""+port );
	}

	//
	// NMEA Properties
	//
	/**
	 * @return The interval between updates that the NMEA server will sent to connected clients.
	 *         Defaults to 1000 millis.
	 */
	public long getNmeaPingInterval()
	{
		return Long.parseLong( properties.getProperty(KEY_NMEA_INTERVAL,"1000") );
	}

	/**
	 * The NMEA server will send out pings to connected clients on an interval. Set that
	 * interval in millis
	 * 
	 * @param millis The interval period 
	 */
	public void setNmeaPingInterval( long millis )
	{ 
		// if less than or equal to zero we take that to mean "default"
		if( millis <= 0 )
			millis = 1000;

		properties.setProperty( KEY_NMEA_INTERVAL, ""+millis );
	}
	
	public SentenceId getNmeaOutputFormat()
	{
		return SentenceId.valueOf( properties.getProperty(KEY_NMEA_FORMAT,"RMC").toUpperCase() );
	}
	
	public void setNmeaOutputFormat( String format )
	{
		SentenceId.valueOf( format ); // check it
		properties.setProperty( KEY_NMEA_FORMAT, format );
	}
	
	public void setNmeaOutputFormat( SentenceId format )
	{
		properties.setProperty( KEY_NMEA_FORMAT, format.name() );
	}
	
	public void setNmeaOutputTime( String value )
	{
		if( value.equalsIgnoreCase("local") == false &&
			value.equalsIgnoreCase("utc") == false )
			throw new IllegalArgumentException( "Output time must be \"utc\" or \"local\"" );
		
		properties.setProperty( KEY_NMEA_TIME, value );
	}
	public boolean useUtcTime()
	{
		String value = properties.getProperty(KEY_NMEA_TIME,"utc").toLowerCase();
		return value.equals("utc");
	}
	
	public boolean useLocalTime()
	{
		String value = properties.getProperty(KEY_NMEA_TIME,"utc").toLowerCase();
		return value.equals("local");
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
		boolean isSystemTest = false;

		CommandList list = new CommandList( args );
		while( list.hasMore() )
		{
			String argument = list.next();
			if( argument.equalsIgnoreCase("--config-file") )
				this.configFile = list.next();
			else if( argument.equalsIgnoreCase("--log-level") )
				this.loggingConfiguration.setLevel( list.next() );
			else if( argument.equalsIgnoreCase("--tracking") )
				this.setTrackingEntity( list.next() );
			else if( argument.equalsIgnoreCase("--dis-address") )
				this.setDisAddress( list.next() );
			else if( argument.equalsIgnoreCase("--dis-port") )
				this.setDisPort( Integer.parseInt(list.next()) );
			else if( argument.equalsIgnoreCase("--dis-nic") || argument.equalsIgnoreCase("--dis-interface") )
				this.setDisNic( list.next() );
			else if( argument.equalsIgnoreCase("--dis-exerciseId")  || argument.equalsIgnoreCase("--dis-exercise-id") )
				this.setDisExerciseId( list.next() );
			else if( argument.equalsIgnoreCase("--pdu-sender") )
				this.setPduSender( list.next() );
			else if( argument.equalsIgnoreCase("--pdu-receiver") )
				this.setPduReceiver( list.next() );
			else if( argument.equalsIgnoreCase("--tcp-address") )
				this.setTcpServerAddress( list.next() );
			else if( argument.equalsIgnoreCase("--tcp-port") )
				this.setTcpServerPort( Integer.parseInt(list.next()) );
			else if( argument.equalsIgnoreCase("--nmea-format") )
				this.setNmeaOutputFormat( list.next() );
			else if( argument.equalsIgnoreCase("--nmea-timezone") )
				this.setNmeaOutputTime( list.next() );
			else if( argument.equalsIgnoreCase("--nmea-interval") )
				this.setNmeaPingInterval( Long.parseLong(list.next()) );
			else if( argument.equalsIgnoreCase("--session-file") )
			{
				this.setMode( "File" );
				this.setSessionFile( new File(list.next()) );
			}
			else if( argument.equalsIgnoreCase("--system-test") )
				isSystemTest = true;
			else
				throw new DiscoException( "Unknown argument: "+argument );
		}
		
		// If this is a system test, override other settings
		if( isSystemTest )
		{
    		this.setMode( "File" );
    		this.setSessionFile( new File("etc/dislocator-test.session") );
    		if( this.hasTrackingEntity() == false )
    			this.setTrackingEntity( "Test" );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "Dislocator - Track and republish DIS entity location as NEMA 0183" );
		System.out.println( "Usage: bin/dislocator [--args]" );
		System.out.println( "" );

		System.out.println( "  --tracking            string   (required)  Marking of entity to track. Must specify here, or in config file" );
		System.out.println( "  --nmea-format         string   (optional)  NMEA record output format: RMC, GGA or GLL (default: RMC)" );
		System.out.println( "  --nmea-timezone       string   (optional)  NMEA record timezone used: utc or local    (default: local)" );
		System.out.println( "  --nmea-interval       long     (optional)  Time (ms) server waits between pos reports (default: 1000)" );
		System.out.println( "  --session-file        path     (optional)  Read PDU data from given session file rather than network" );
		System.out.println( "  --config-file         integer  (optional)  Number of objects to create                (default: 100)" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]    (default: INFO)" );
		System.out.println( "" );
		System.out.println( "DIS Listener Settings" );
		System.out.println( "  --dis-exercise-id     short    (optional)  Ex ID to send in outgoing and only recv on (default: 1)" );
		System.out.println( "  --dis-address         string   (optional)  Where to send DIS traffic, or BROADCAST    (default: BROADCAST)" );
		System.out.println( "  --dis-port            integer  (optional)  Port for DIS traffic                       (default: 3000)" );
		System.out.println( "  --dis-interface       string   (optional)  NIC to use. Address or a special symbol:   (default: SITE_LOCAL)" );
		System.out.println( "                                             LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL" );
		System.out.println( "" );
		System.out.println( "NMEA TCP Server Settings" );
		System.out.println( "  --tcp-port            integer  (optional)  TCP Listen port for NMEA server            (default: 2999)" );
		System.out.println( "  --tcp-address         string   (optional)  TCP Listen address or a special symbol:    (default: SITE_LOCAL)" );
		System.out.println( "                                             LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL" );
		System.out.println( "" );
		System.out.println( "System Test Mode" );
		System.out.println( "  --system-test                  (optional)  Puts the Dislocator into File mode and loads test session from" );
		System.out.println( "                                             etc/dislocator-test.session. Session has a single aircraft with" );
		System.out.println( "                                             marking \"Test\" that is loitering near Rockhampton in Queensland" );
		System.out.println( "" );
	}

}
