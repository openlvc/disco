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

import java.util.Properties;
import java.util.Random;

import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.utils.StringUtils;
import org.openlvc.distributor.Mode;

public class LinkConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
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
	
	public static final String LINK_DIS_ADDRESS         = "dis.address";
	public static final String LINK_DIS_PORT            = "dis.port";
	public static final String LINK_DIS_NIC             = "dis.nic";
	public static final String LINK_DIS_EXID            = "dis.exerciseId";
	public static final String LINK_DIS_SITE_ID         = "dis.siteId";
	public static final String LINK_DIS_APP_ID          = "dis.appId";
	public static final String LINK_DIS_LOG_LEVEL       = "dis.loglevel";
	public static final String LINK_DIS_LOG_FILE        = "dis.logfile";
	public static final String LINK_DIS_LOG_TO_FILE     = "dis.logtofile";
	
	public static final String LINK_WAN_ADDRESS         = "wan.address";
	public static final String LINK_WAN_PORT            = "wan.port";
	public static final String LINK_WAN_TYPE            = "wan.type"; // tcp|udp
	public static final String LINK_WAN_BUNDLING        = "wan.bundling";
	public static final String LINK_WAN_BUNDLING_SIZE   = "wan.bundling.maxSize";
	public static final String LINK_WAN_BUNDLING_TIME   = "wan.bundling.maxTime";

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
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_ADDRESS) )
				this.setWanAddress( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_PORT) )
				this.setWanPort( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_TYPE) )
				this.setWanType( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_BUNDLING) )
				this.setWanBundling( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_BUNDLING_SIZE) )
				this.setWanBundlingSize( value );
			else if( key.equalsIgnoreCase(prefix+LINK_WAN_BUNDLING_TIME) )
				this.setWanBundlingTime( value );
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
		return (short)getAsInt( LINK_DIS_EXID, 1 );
	}

	public void setDisExerciseId( int id )
	{
		if( id > 255 )
			throw new IllegalArgumentException( "Site ID cannot be greater than 255" );
		set( LINK_DIS_EXID, id );
	}

	/**
	 * Accepts an integer or the special string `<any>`
	 */
	public void setDisExerciseId( String id )
	{
		if( id.trim().equalsIgnoreCase("<any>") )
			setDisExerciseId( -1 );
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
		
		return getAsInt( LINK_DIS_SITE_ID );
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
		
		return getAsInt( LINK_DIS_APP_ID );
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
		value = value.trim();
		if( value.equalsIgnoreCase("true")   ||
			value.equalsIgnoreCase("on")     ||
			value.equalsIgnoreCase("yes")    ||
			value.equalsIgnoreCase("enabled") )
			setDisLogToFile( true );
		else
			setDisLogToFile( false );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	// WAN Properties   ////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getWanAddress()
	{
		return getAsString( LINK_WAN_ADDRESS );
	}
	
	public void setWanAddress( String address )
	{
		set( LINK_WAN_ADDRESS, address );
	}
	
	//public InetAddress getResolvedWanAddress()
	//{
	//}
	
	public int getWanPort()
	{
		return getAsInt( LINK_WAN_PORT, 4919/*D-I-S*/ );
	}

	public void setWanPort( int port )
	{
		if( port > 65536 )
			throw new IllegalArgumentException( "Port must be less than 65536" );
		else if( port < 1 )
			throw new IllegalArgumentException( "Port must be positive number" );

		set( LINK_WAN_PORT, port );
	}

	public String getWanConnectionType()
	{
		return getAsString( LINK_WAN_TYPE, "udp" );
	}

	public boolean isWanConnectionTypeTcp()
	{
		return getWanConnectionType().equals("tcp");
	}
	
	public boolean isWanConnectionTypeUdp()
	{
		return getWanConnectionType().equals("udp");
	}
	
	public void setWanConnectionTypeTcp()
	{
		set( LINK_WAN_TYPE, "tcp" );
	}
	
	public void setWanConnectionTypeUdp()
	{
		set( LINK_WAN_TYPE, "udp" );
	}
	
	public boolean isWanBundling()
	{
		return getAsBoolean( LINK_WAN_BUNDLING, false );
	}
	
	public void setWanBundling( boolean enableBundling )
	{
		set( LINK_WAN_BUNDLING, enableBundling );
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
	
	public void setWanBundlingSizeBytes( int bytes )
	{
		set( LINK_WAN_BUNDLING_SIZE, ""+bytes+"b" );
	}
	
	public int getWanBundlingTime()
	{
		return getAsInt( LINK_WAN_BUNDLING_TIME );
	}
	
	public void setWanBundlingTime( int maxTime )
	{
		set( LINK_WAN_BUNDLING_TIME, maxTime );
	}
	
	//
	// Properties parsing specific methods
	//
	private void setWanPort( String value )
	{
		setWanPort( Integer.parseInt(value) );
	}
	
	private void setWanType( String value )
	{
		value = value.trim();
		if( value.equalsIgnoreCase("tcp") )
			this.setWanConnectionTypeTcp();
		else if( value.equalsIgnoreCase("udp") )
			this.setWanConnectionTypeUdp();
		else
			throw new IllegalArgumentException( "WAN Type must be tcp or udp, found: "+value );
	}
	
	private void setWanBundling( String bundling )
	{
		if( Boolean.valueOf(bundling) )
			this.setWanBundling( true );
		else
			this.setWanBundling( false );
	}
	
	private void setWanBundlingTime( String time )
	{
		setWanBundlingTime( Integer.parseInt(time) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private final int getAsInt( String key )
	{
		return Integer.parseInt( properties.getProperty(key) );
	}

	private final int getAsInt( String key, int defaultValue )
	{
		String value = properties.getProperty( key );
		if( value == null )
			return defaultValue;
		else
			return Integer.parseInt( value );
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
