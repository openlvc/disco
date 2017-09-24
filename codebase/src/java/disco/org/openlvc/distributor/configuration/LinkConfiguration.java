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

import java.io.Serializable;
import java.util.Properties;
import java.util.Random;

import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.utils.StringUtils;
import org.openlvc.distributor.Mode;
import org.openlvc.distributor.TransportType;

public class LinkConfiguration implements Serializable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//
	// Link Specific Snippets
	//
	// Within a Distributor configuration there may be many links. Details specific to a
	// link are identified by a common prefix: <code>distributor.[LINKNAME].</code>.
	// These properties are the names we expect after the prefixes. For example, a configuration
	// might look as follows:
	//
	// distributor.links = per, nyc, sgp
	// distributor.per.mode = dis
	// distributor.per.dis.address = BROADCAST
	// distributor.per.dis.port = 3000
	// distributor.per...
	//
	// The <code>mode</code>, <code>dis.address</code>, ... settings can be specified for
	// any number of links, as long as they appear under the proper prefix.
	//
	public static final String LINK_MODE                = "mode";
	
	// Filtering Properties
	public static final String LINK_FILTER_RECV         = "filter.recv";
	public static final String LINK_FILTER_SEND         = "filter.send";

	// DIS Properties
	public static final String LINK_DIS_ADDRESS         = "dis.address";
	public static final String LINK_DIS_PORT            = "dis.port";
	public static final String LINK_DIS_NIC             = "dis.nic";
	public static final String LINK_DIS_EXID            = "dis.exerciseId";
	public static final String LINK_DIS_SITE_ID         = "dis.siteId";
	public static final String LINK_DIS_APP_ID          = "dis.appId";
	public static final String LINK_DIS_LOG_LEVEL       = "dis.loglevel";
	public static final String LINK_DIS_LOG_FILE        = "dis.logfile";
	public static final String LINK_DIS_LOG_TO_FILE     = "dis.logtofile";

	// WAN Properties
	public static final String LINK_WAN_ADDRESS         = "wan.relay";
	public static final String LINK_WAN_NIC             = "wan.nic";       // only used for udp
	public static final String LINK_WAN_PORT            = "wan.port";
	public static final String LINK_WAN_SEND_PORT       = "wan.send.port"; // only used for udp
	public static final String LINK_WAN_TRANSPORT       = "wan.transport"; // tcp|udp
	public static final String LINK_WAN_SITE_NAME       = "wan.siteName";
	public static final String LINK_WAN_AUTO_RECONNECT  = "wan.autoReconnect";
	public static final String LINK_WAN_BUNDLING        = "wan.bundling";
	public static final String LINK_WAN_BUNDLING_SIZE   = "wan.bundling.maxSize";
	public static final String LINK_WAN_BUNDLING_TIME   = "wan.bundling.maxTime";

	// Relay Properties
	public static final String LINK_RELAY_ADDRESS       = "relay.address";
	public static final String LINK_RELAY_PORT          = "relay.port"; // tcp|udp
	public static final String LINK_RELAY_TRANSPORT     = "relay.transport";

	// Traffic Logger properties
	public static final String LINK_LOGGER_LEVEL        = "logging.level";
	public static final String LINK_LOGGER_USE_FILE     = "logging.logtofile";
	public static final String LINK_LOGGER_FILE         = "logging.logfile";

	// Pulse Link Properties
	public static final String LINK_PULSE_INTERVAL      = "pulse.interval";
	public static final String LINK_PULSE_MARKING       = "pulse.marking";
	public static final String LINK_PULSE_ENUM          = "pulse.enumeration";
	public static final String LINK_PULSE_EX_ID         = "pulse.exerciseId";
	public static final String LINK_PULSE_SITE_ID       = "pulse.siteId";
	public static final String LINK_PULSE_APP_ID        = "pulse.appId";

	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String name;
	private Properties properties;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LinkConfiguration( String name, Properties properties )
	{
		this.name = name;
		this.properties = new Properties();
		
		if( properties != null )
			this.loadFromProperties( properties );
	}
	
	public LinkConfiguration( String name )
	{
		this( name, null );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Load any link settings from the given configuration. We will look for all keys
	 * that start with the prefix "distributor.LINK_NAME.".
	 * 
	 * @param properties The properties file to load from
	 */
	private void loadFromProperties( Properties properties )
	{
		String prefix = "distributor."+name+".";
		for( Object keyObject : properties.keySet() )
		{
			String key = keyObject.toString();
			if( key.startsWith(prefix) == false )
				continue;
			
			String value = properties.getProperty( key );
			if( key.equalsIgnoreCase(prefix+LINK_MODE) )
				this.setMode( value );
			// Filter Settings
			else if( key.equalsIgnoreCase(prefix+LINK_FILTER_RECV) )
				this.setReceiveFilter( value );
			else if( key.equalsIgnoreCase(prefix+LINK_FILTER_SEND) )
				this.setSendFilter( value );
			// DIS Settings
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_ADDRESS) )
				this.setDisAddress( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_PORT) )
				this.setDisPort( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_NIC) )
				this.setDisNic( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_EXID) )
				this.setDisExerciseId( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_SITE_ID) )
				this.setDisSiteId( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_APP_ID) )
				this.setDisAppId( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_LOG_LEVEL) )
				this.setDisLogLevel( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_LOG_FILE) )
				this.setDisLogFile( value );
			else if( key.equalsIgnoreCase(prefix+LINK_DIS_LOG_TO_FILE) )
				this.setDisLogToFile( value );
			// WAN Settings
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_NIC) )
				this.setWanNic( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_ADDRESS) )
				this.setWanAddress( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_PORT) )
				this.setWanPort( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_SEND_PORT) )
				this.setWanSendPort( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_TRANSPORT) )
				this.setWanTransport( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_SITE_NAME) )
				this.setWanSiteName( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_AUTO_RECONNECT) )
				this.setWanAutoReconnect( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_BUNDLING) )
				this.setWanBundling( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_BUNDLING_SIZE) )
				this.setWanBundlingSize( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_BUNDLING_TIME) )
				this.setWanBundlingTime( value );
			// Relay Settings
			else if( key.equalsIgnoreCase(prefix+LINK_RELAY_ADDRESS) )
				this.setRelayAddress( value );
			else if( key.equalsIgnoreCase(prefix+LINK_RELAY_PORT) )
				this.setRelayPort( value );
			else if( key.equalsIgnoreCase(prefix+LINK_RELAY_TRANSPORT) )
				this.setRelayTransport( value );
			// Heartbeat Settings
			else if( key.equalsIgnoreCase(prefix+LINK_PULSE_INTERVAL) )
				this.setPulseInterval( value );
			else if( key.equalsIgnoreCase(prefix+LINK_PULSE_MARKING) )
				this.setPulseMarking( value );
			else if( key.equalsIgnoreCase(prefix+LINK_PULSE_ENUM) )
				this.setPulseEnumeration( value );
			else if( key.equalsIgnoreCase(prefix+LINK_PULSE_EX_ID) )
				this.setPulseExerciseId( value );
			else if( key.equalsIgnoreCase(prefix+LINK_PULSE_SITE_ID) )
				this.setPulseSiteId( value );
			else if( key.equalsIgnoreCase(prefix+LINK_PULSE_APP_ID) )
				this.setPulseAppId( value );
			else
				; // skip
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Mode getMode()
	{
		if( properties.containsKey(LINK_MODE) == false )
			throw new IllegalArgumentException( "Cannot find mode for link: "+getName() );
		else
			return Mode.valueOf( properties.getProperty(LINK_MODE) );
	}
	
	public void setMode( Mode mode )
	{
		set( LINK_MODE, mode );
	}
	
	private void setMode( String value )
	{
		setMode( Mode.valueOfIgnoreCase(value) );
	}

	//
	// Filter Options
	//
	public void setReceiveFilter( String value )
	{
		if( value == null )
			value = "<none>";
		
		set( LINK_FILTER_RECV, value );
	}
	
	public void setSendFilter( String value )
	{
		if( value == null )
			value = "<none>";
		
		set( LINK_FILTER_SEND, value );
	}
	
	public String getReceiveFilter()
	{
		String value = getAsString( LINK_FILTER_RECV, "<none>" );
		if( value == null || value.equals("<none>") )
			return null;
		else
			return value;
	}
	
	public String getSendFilter()
	{
		String value = getAsString( LINK_FILTER_SEND, "<none>" );
		if( value == null || value.equals("<none>") )
			return null;
		else
			return value;
	}

	public boolean isReceiveFiltering()
	{
		String temp = getReceiveFilter();
		if( temp == null || temp.equals("<none>") )
			return false;
		else
			return true;
	}
	
	public boolean isSendFiltering()
	{
		String temp = getSendFilter();
		if( temp == null || temp.equals("<none>") )
			return false;
		else
			return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	// DIS Properties   ////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getDisAddress()
	{
		return getAsString( LINK_DIS_ADDRESS, "BROADCAST" );
	}
	
	public void setDisAddress( String address )
	{
		set( LINK_DIS_ADDRESS, address );
	}
	
	public int getDisPort()
	{
		return getAsInt( LINK_DIS_PORT, 3000 );
	}

	public void setDisPort( int port )
	{
		if( port > 65536 )
			throw new IllegalArgumentException( "Port must be less than 65536" );
		else if( port < 1 )
			throw new IllegalArgumentException( "Port must be positive number" );

		set( LINK_DIS_PORT, port );
	}
	
	private void setDisPort( String value )
	{
		setDisPort( Integer.parseInt(value)  );
	}

	public String getDisNic()
	{
		return getAsString( LINK_DIS_NIC, "SITE_LOCAL" );
	}
	
	public void setDisNic( String nic )
	{
		set( LINK_DIS_NIC, nic );
	}
	
	public short getDisExerciseId()
	{
		return getAsShort( LINK_DIS_EXID, (short)1 );
	}

	public void setDisExerciseId( int id )
	{
		if( id > 255 || id < 0 )
			throw new IllegalArgumentException( "Exercise ID must be between 0-255, was "+id );
		set( LINK_DIS_EXID, id );
	}

	/**
	 * Accepts an integer or the special string `<any>`
	 */
	public void setDisExerciseId( String id )
	{
		if( id.trim().equalsIgnoreCase("<any>") )
			setDisExerciseId( 0 );
		else
			setDisExerciseId( Integer.parseInt(id) );
	}

	/**
	 * Get the site ID for this link. If it is not configured, a random one is assigned.
	 */
	public int getDisSiteId()
	{
		// if we don't have it, set it to a random one
		if( properties.containsKey(LINK_DIS_SITE_ID) == false )
			setDisSiteId( "<random>" );
		
		return getAsInt( LINK_DIS_SITE_ID, 1 );
	}
	
	public void setDisSiteId( int id )
	{
		if( id > EntityId.MAX_SITE_ID )
			throw new IllegalArgumentException( "Site ID cannot be greater than: "+EntityId.MAX_SITE_ID );

		set( LINK_DIS_SITE_ID, id );
	}
	
	/**
	 * Accepts an integer or the special string `<random>`
	 */
	public void setDisSiteId( String id )
	{
		if( id.trim().equalsIgnoreCase("<random>") )
			setDisSiteId( new Random().nextInt(EntityId.MAX_SITE_ID) );
		else
			setDisSiteId( Integer.parseInt(id) );
	}
	
	/**
	 * Get the App ID for this link. If it is not configured, a random one is assigned.
	 */
	public int getDisAppId()
	{
		// if we don't have it, set it to a random one
		if( properties.containsKey(LINK_DIS_APP_ID) == false )
			setDisSiteId( "<random>" );
		
		return getAsInt( LINK_DIS_APP_ID, 1 );
	}
	
	public void setDisAppId( int id )
	{
		if( id > EntityId.MAX_APP_ID )
			throw new IllegalArgumentException( "Site ID cannot be greater than: "+EntityId.MAX_APP_ID );

		set( LINK_DIS_APP_ID, id );
	}
	
	/**
	 * Accepts an integer or the special string `<random>`
	 */
	public void setDisAppId( String id )
	{
		if( id.trim().equalsIgnoreCase("<random>") )
			setDisAppId( new Random().nextInt(EntityId.MAX_APP_ID) );
		else
			setDisAppId( Integer.parseInt(id) );
	}

	/**
	 * The log level to use for the underlying DIS library
	 */
	public String getDisLogLevel()
	{
		return getAsString( LINK_DIS_LOG_LEVEL, "ERROR" );
	}

	public void setDisLogLevel( String level )
	{
		set( LINK_DIS_LOG_LEVEL, level );
	}
	
	/**
	 * The log file to use for the underlying DIS library
	 */
	public String getDisLogFile()
	{
		return getAsString( LINK_DIS_LOG_FILE, "logs/distributor."+name+".log" );
	}

	/**
	 * Set the path to the log file to use for the underlying DIS library. If it contains
	 * the string &lt;name&gt;, that will be replaced with the name of this link.
	 * @param path
	 */
	public void setDisLogFile( String path )
	{
		// replace the <name> token with this link name if it is present
		path = path.replace( "<name>", name );
		set( LINK_DIS_LOG_FILE, path );
	}

	/**
	 * Should file logging for this connection be turned on or off
	 */
	public boolean getDisLogToFile()
	{
		return getAsBoolean( LINK_DIS_LOG_TO_FILE, false );
	}
	
	public void setDisLogToFile( boolean logToFile )
	{
		set( LINK_DIS_LOG_TO_FILE, logToFile );
	}
	
	public void setDisLogToFile( String value )
	{
		setDisLogToFile( StringUtils.stringToBoolean(value) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	// WAN Properties   ////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getWanNic()
	{
		return getAsString( LINK_WAN_NIC, "SITE_LOCAL" );
	}

	/**
	 * The WAN NIC is only used for UDP connections and identifies the device we should both
	 * send outgoing messages on and listen for the incoming ones through.
	 */
	public void setWanNic( String nic )
	{
		set( LINK_WAN_NIC, nic );
	}
	
	public String getWanAddress()
	{
		return getAsString( LINK_WAN_ADDRESS );
	}
	
	public void setWanAddress( String address )
	{
		set( LINK_WAN_ADDRESS, address );
	}
	
	public int getWanPort()
	{
		return getAsInt( LINK_WAN_PORT, 4919/*D-I-S*/ );
	}

	public void setWanPort( int port )
	{
		if( port > 65536 )
			throw new IllegalArgumentException( "WAN port must be less than 65536" );
		else if( port < 1 )
			throw new IllegalArgumentException( "WAN port must be positive number" );

		set( LINK_WAN_PORT, port );
	}
	
	private void setWanPort( String port )
	{
		setWanPort( Integer.parseInt(port) );
	}

	public int getWanSendPort()
	{
		return getAsInt( LINK_WAN_SEND_PORT, 0 /*ephemeral port*/ );
	}

	public void setWanSendPort( int port )
	{
		if( port > 65536 )
			throw new IllegalArgumentException( "WAN send port must be less than 65536" );
		else if( port < 0 )
			throw new IllegalArgumentException( "WAN send port must be positive number" );

		set( LINK_WAN_SEND_PORT, port );
	}
	
	private void setWanSendPort( String port )
	{
		setWanSendPort( Integer.parseInt(port) );
	}
	
	public TransportType getWanTransport()
	{
		return TransportType.valueOfIgnoreCase( getAsString(LINK_WAN_TRANSPORT,"udp") );
	}

	public boolean isWanTransportUdp()
	{
		return getWanTransport() == TransportType.UDP;
	}
	
	public boolean isWanTransportTcp()
	{
		return getWanTransport() == TransportType.TCP;
	}

	private void setWanTransport( String transport )
	{
		setWanTransport( TransportType.valueOfIgnoreCase(transport) );
	}
	
	public void setWanTransport( TransportType transport )
	{
		set( LINK_WAN_TRANSPORT, transport.name().toLowerCase() );
	}

	/**
	 * A WAN connection can have a site name that is used to identify it on the other end.
	 * This can be used to help us specify configuration properties for individual incoming
	 * connections, or to just identify the connection in logs on the other side.
	 * <p/>
	 * The special name "<auto>" tells the Relay on the other end to just choose a generic
	 * name for us.
	 */
	public void setWanSiteName( String name )
	{
		set( LINK_WAN_SITE_NAME, name );
	}
	
	public String getWanSiteName()
	{
		return getAsString( LINK_WAN_SITE_NAME, "<auto>" );
	}

	public void setWanAutoReconnect( boolean autoReconnect )
	{
		set( LINK_WAN_AUTO_RECONNECT, autoReconnect );
	}

	public void setWanAutoReconnect( String autoReconnect )
	{
		setWanAutoReconnect( StringUtils.stringToBoolean(autoReconnect) );
	}

	public boolean isWanAutoReconnect()
	{
		return getAsBoolean( LINK_WAN_AUTO_RECONNECT, true );
	}

	public boolean isWanBundling()
	{
		return getAsBoolean( LINK_WAN_BUNDLING, false );
	}
	
	public void setWanBundling( boolean enableBundling )
	{
		set( LINK_WAN_BUNDLING, enableBundling );
	}
	
	private void setWanBundling( String bundling )
	{
		setWanBundling( StringUtils.stringToBoolean(bundling) );
	}
	
	public String getWanBundlingSize()
	{
		return getAsString( LINK_WAN_BUNDLING_SIZE, "1400b" );
	}

	public int getWanBundlingSizeBytes()
	{
		return (int)StringUtils.bytesFromString( getWanBundlingSize() );
	}
	
	public void setWanBundlingSize( String size )
	{
		set( LINK_WAN_BUNDLING_SIZE, size );
	}
	
	public void setWanBundlingSize( int bytes )
	{
		set( LINK_WAN_BUNDLING_SIZE, ""+bytes+"b" );
	}
	
	public int getWanBundlingTime()
	{
		return getAsInt( LINK_WAN_BUNDLING_TIME, 30 );
	}
	
	/**
	 * If there are no packets in this many milliseconds, flush any bundled set.
	 */
	public void setWanBundlingTime( int millis )
	{
		set( LINK_WAN_BUNDLING_TIME, millis );
	}

	/**
	 * If there are no packets in this many milliseconds, flush any bundled set.
	 */
	private void setWanBundlingTime( String millis )
	{
		setWanBundlingTime( Integer.parseInt(millis) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	// Relay Properties   //////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setRelayAddress( String address )
	{
		set( LINK_RELAY_ADDRESS, address );
	}
	
	public String getRelayAddress()
	{
		return getAsString( LINK_RELAY_ADDRESS, "GLOBAL" );
	}
	
	public void setRelayPort( int port )
	{
		set( LINK_RELAY_PORT, ""+port );
	}
	
	private void setRelayPort( String port )
	{
		Integer.parseInt( port );
		set( LINK_RELAY_PORT, port );
	}
	
	public int getRelayPort()
	{
		return getAsInt( LINK_RELAY_PORT, 4919 );
	}
	
	public void setRelayTransport( TransportType transport )
	{
		setRelayTransport( transport.name().toLowerCase() );
	}
	
	private void setRelayTransport( String transport )
	{
		set( LINK_RELAY_TRANSPORT, transport.toLowerCase() );
	}
	
	public TransportType getRelayTransport()
	{
		return TransportType.valueOfIgnoreCase( getAsString(LINK_RELAY_TRANSPORT,"tcp") );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	// Pulse Link Properties   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void setPulseInterval( String millis )
	{
		Long.parseLong( millis );
		set( LINK_PULSE_INTERVAL, millis );
	}

	public void setPulseInterval( int millis )
	{
		setPulseInterval( ""+millis );
	}

	public long getPulseInterval()
	{
		return getAsLong( LINK_PULSE_INTERVAL, 5000L );
	}

	public void setPulseMarking( String marking )
	{
		set( LINK_PULSE_MARKING, marking );
	}

	public String getPulseMarking()
	{
		return getAsString( LINK_PULSE_MARKING );
	}
	
	public void setPulseEnumeration( String marking )
	{
		set( LINK_PULSE_ENUM, marking );
	}

	public String getPulseEnumeration()
	{
		return getAsString( LINK_PULSE_ENUM );
	}

	private void setPulseExerciseId( String id )
	{
		Short.parseShort( id );
		set( LINK_PULSE_EX_ID, id );
	}
	
	public void setPulseExerciseId( short id )
	{
		setPulseExerciseId( ""+id );
	}
	
	public short getPulseExerciseId()
	{
		return getAsShort( LINK_PULSE_EX_ID, (short)1 );
	}

	private void setPulseSiteId( String id )
	{
		Integer.parseInt( id );
		set( LINK_PULSE_SITE_ID, id );
	}
	
	public void setPulseSiteId( int id )
	{
		setPulseSiteId( ""+id );
	}
	
	public int getPulseAppId()
	{
		return getAsInt( LINK_PULSE_APP_ID, 0 );
	}

	private void setPulseAppId( String id )
	{
		Integer.parseInt( id );
		set( LINK_PULSE_APP_ID, id );
	}
	
	public void setPulseAppId( int id )
	{
		setPulseAppId( ""+id );
	}
	
	public int getPulseSiteId()
	{
		return getAsInt( LINK_PULSE_APP_ID, 0 );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private final short getAsShort( String key, short defaultValue )
	{
		String value = properties.getProperty( key );
		if( value == null )
			return defaultValue;
		else
			return Short.parseShort( value );
	}
	
	private final int getAsInt( String key, int defaultValue )
	{
		String value = properties.getProperty( key );
		if( value == null )
			return defaultValue;
		else
			return Integer.parseInt( value );
	}
	
	private final long getAsLong( String key, long defaultValue )
	{
		String value = properties.getProperty( key );
		if( value == null )
			return defaultValue;
		else
			return Long.parseLong( value );
	}

	private final String getAsString( String key )
	{
		return properties.getProperty( key );
	}
	
	private final String getAsString( String key, String defaultValue )
	{
		return properties.getProperty( key, defaultValue );
	}
	
	private final boolean getAsBoolean( String key, boolean defaultValue )
	{
		String value = properties.getProperty( key );
		if( value == null )
			return defaultValue;
		else
			return Boolean.valueOf( value );
	}
	
	private final void set( String key, String value )
	{
		properties.setProperty( key, value );
	}
	
	private final void set( String key, int value )
	{
		properties.setProperty( key, ""+value );
	}
	
	private final void set( String key, boolean value )
	{
		properties.setProperty( key, Boolean.toString(value) );
	}
	
	private final void set( String key, Enum<?> value )
	{
		properties.setProperty( key, value.toString() );
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
