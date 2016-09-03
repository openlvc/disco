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

import org.openlvc.disco.utils.StringUtils;
import org.openlvc.distributor.Mode;

public class SiteConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	//
	// Configuration Key Snippets
	//
	// Site-specific configurations will appear multiple times within the configuration.
	// All keys will be prefixed with "distributor.[site]". A sample config might look
	// as follows:
	//
	// distributor.sites = per, nyc, sgp
	// distributor.per.mode = dis
	// distributor.per.dis.address = BROADCAST
	// distributor.per.dis.port = 3000
	// distributor.per...
	//
	// The keys below are the snippets that follow the prefix
	//
	public static final String SITE_MODE                = "mode";
	
	public static final String SITE_DIS_ADDRESS         = "dis.address";
	public static final String SITE_DIS_PORT            = "dis.port";
	public static final String SITE_DIS_NIC             = "dis.nic";
	
	public static final String SITE_WAN_ADDRESS         = "wan.address";
	public static final String SITE_WAN_PORT            = "wan.port";
	public static final String SITE_WAN_TYPE            = "wan.type"; // tcp|udp
	public static final String SITE_WAN_BUNDLING        = "wan.bundling";
	public static final String SITE_WAN_BUNDLING_SIZE   = "wan.bundling.maxSize";
	public static final String SITE_WAN_BUNDLING_TIME   = "wan.bundling.maxTime";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String name;
	private Properties properties;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SiteConfiguration( String name, Properties properties )
	{
		this.name = name;
		this.properties = new Properties();
		
		if( properties != null )
			this.loadFromProperties( properties );
	}
	
	public SiteConfiguration( String name )
	{
		this( name, null );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Load any site settings from the given configuration. We will look for all keys
	 * that start with the prefix "distributor.SITE_NAME.".
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
			if( key.equalsIgnoreCase(prefix+SITE_MODE) )
				this.setMode( value );
			else if( key.equalsIgnoreCase(prefix+SITE_DIS_ADDRESS) )
				this.setDisAddress( value );
			else if( key.equalsIgnoreCase(prefix+SITE_DIS_PORT) )
				this.setDisPort( value );
			else if( key.equalsIgnoreCase(prefix+SITE_DIS_NIC) )
				this.setDisNic( value );
			else if( key.equalsIgnoreCase(prefix+SITE_WAN_ADDRESS) )
				this.setWanAddress( value );
			else if( key.equalsIgnoreCase(prefix+SITE_WAN_PORT) )
				this.setWanPort( value );
			else if( key.equalsIgnoreCase(prefix+SITE_WAN_TYPE) )
				this.setWanType( value );
			else if( key.equalsIgnoreCase(prefix+SITE_WAN_BUNDLING) )
				this.setWanBundling( value );
			else if( key.equalsIgnoreCase(prefix+SITE_WAN_BUNDLING_SIZE) )
				this.setWanBundlingSize( value );
			else if( key.equalsIgnoreCase(prefix+SITE_WAN_BUNDLING_TIME) )
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
		return Mode.valueOf( properties.getProperty(SITE_MODE) );
	}
	
	public void setMode( Mode mode )
	{
		set( SITE_MODE, mode );
	}
	
	/////////////////////////////////////////////
	// DIS Properties  //////////////////////////
	/////////////////////////////////////////////
	public String getDisAddress()
	{
		return getAsString( SITE_DIS_ADDRESS, "BROADCAST" );
	}
	
	public void setDisAddress( String address )
	{
		set( SITE_DIS_ADDRESS, address );
	}
	
	//public InetAddress getResolvedDisAddress()
	//{
	//}
	
	public int getDisPort()
	{
		return getAsInt( SITE_DIS_PORT, 3000 );
	}
	
	public void setDisPort( int port )
	{
		if( port > 65536 )
			throw new IllegalArgumentException( "Port must be less than 65536" );
		else if( port < 1 )
			throw new IllegalArgumentException( "Port must be positive number" );

		set( SITE_DIS_PORT, port );
	}
	
	public String getDisNic()
	{
		return getAsString( SITE_DIS_NIC, "SITE_LOCAL" );
	}
	
	public void setDisNic( String nic )
	{
		set( SITE_DIS_NIC, nic );
	}
	
	//public NetworkInterface getResolvedDisNic()
	//{
	//}

	//
	// Properties parsing specific methods
	//
	private void setMode( String value )
	{
		for( Mode mode : Mode.values() )
		{
			if( mode.toString().equalsIgnoreCase(value) )
			{
				setMode( mode );
				return;
			}
		}
		
		throw new IllegalArgumentException( "Invalid Site Mode: "+value );
	}
	
	private void setDisPort( String value )
	{
		setDisPort( Integer.parseInt(value)  );
	}

	/////////////////////////////////////////////
	// WAN Properties  //////////////////////////
	/////////////////////////////////////////////
	public String getWanAddress()
	{
		return getAsString( SITE_WAN_ADDRESS );
	}
	
	public void setWanAddress( String address )
	{
		set( SITE_WAN_ADDRESS, address );
	}
	
	//public InetAddress getResolvedWanAddress()
	//{
	//}
	
	public int getWanPort()
	{
		return getAsInt( SITE_WAN_PORT, 4919/*D-I-S*/ );
	}

	public void setWanPort( int port )
	{
		if( port > 65536 )
			throw new IllegalArgumentException( "Port must be less than 65536" );
		else if( port < 1 )
			throw new IllegalArgumentException( "Port must be positive number" );

		set( SITE_WAN_PORT, port );
	}

	public String getWanConnectionType()
	{
		return getAsString( SITE_WAN_TYPE, "udp" );
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
		set( SITE_WAN_TYPE, "tcp" );
	}
	
	public void setWanConnectionTypeUdp()
	{
		set( SITE_WAN_TYPE, "udp" );
	}
	
	public boolean isWanBundling()
	{
		return getAsBoolean( SITE_WAN_BUNDLING, false );
	}
	
	public void setWanBundling( boolean enableBundling )
	{
		set( SITE_WAN_BUNDLING, enableBundling );
	}
	
	public String getWanBundlingSize()
	{
		return getAsString( SITE_WAN_BUNDLING_SIZE, "1400b" );
	}

	public int getWanBundlingSizeBytes()
	{
		return (int)StringUtils.bytesFromString( getWanBundlingSize() );
	}
	
	public void setWanBundlingSize( String size )
	{
		set( SITE_WAN_BUNDLING_SIZE, size );
	}
	
	public void setWanBundlingSizeBytes( int bytes )
	{
		set( SITE_WAN_BUNDLING_SIZE, ""+bytes+"b" );
	}
	
	public int getWanBundlingTime()
	{
		return getAsInt( SITE_WAN_BUNDLING_TIME );
	}
	
	public void setWanBundlingTime( int maxTime )
	{
		set( SITE_WAN_BUNDLING_TIME, maxTime );
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
