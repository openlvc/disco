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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.NetworkUtils;

public class UdpDatasourceConfig
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Keys for properties file
	private static final String PROP_INTERFACE = "disco.provider.udp.interface";
	private static final String PROP_ADDRESS = "disco.provider.udp.address";
	private static final String PROP_PORT = "disco.provider.udp.port";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private NetworkInterface networkInterface;
	private InetAddress address;
	private int port;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpDatasourceConfig()
	{
		// lazy-load all of these from system properties unless they are set separately
		this.address = null;
		this.port = -1;
		this.networkInterface = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public InetAddress getAddress()
	{
		if( this.address == null )
		{
			String prop = System.getProperty( PROP_ADDRESS, "BROADCAST" );
			if( prop.equals("BROADCAST") )
			{
				// Get Network Interface to determine local address
				NetworkInterface nic = getNetworkInterface();
				this.address = nic.getInterfaceAddresses().get(0).getAddress();
			}
			else
			{
				// It if an address, get it directly
				this.address = NetworkUtils.getAddress( prop );
			}
		}
		
		return this.address;
	}
	
	public void setAddress( InetAddress address )
	{
		this.address = address;
	}

	/**
	 * This can be a domain name or an actual IP address, or alternatively it can be one
	 * of the special symbols accepted by {@link NetworkUtils#getByName(String)}.
	 */
	public void setAddress( String address ) throws DiscoException
	{
		try
		{
			this.address = InetAddress.getByName( address );
		}
		catch( UnknownHostException uhe )
		{
			throw new DiscoException( "Could not find address: "+address );
		}
	}
	
	public int getPort()
	{
		if( this.port == -1 )
			this.port = Integer.parseInt( System.getProperty(PROP_PORT,"3000") );

		return this.port;
	}
	
	public void setPort( int port )
	{
		this.port = port;
	}
	
	public NetworkInterface getNetworkInterface()
	{
		if( this.networkInterface == null )
		{
			// have they provided a specific nic?
			String prop = System.getProperty( PROP_INTERFACE, "SITE_LOCAL" );
			this.networkInterface = NetworkUtils.getNetworkInterface( prop );
		}

		return this.networkInterface;
	}
	
	public void setNetworkInterface( NetworkInterface networkInterface )
	{
		this.networkInterface = networkInterface;
	}
	
	public void setNetworkInterface( String name )
	{
		this.networkInterface = NetworkUtils.getNetworkInterface( name );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
