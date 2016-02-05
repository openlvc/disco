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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.StringUtils;

public class UdpConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Keys for properties file
	private static final String PROP_INTERFACE    = "disco.udp.interface";
	private static final String PROP_ADDRESS      = "disco.udp.address";
	private static final String PROP_PORT         = "disco.udp.port";
	private static final String PROP_SEND_BUFFER  = "disco.udp.sendBuffer";
	private static final String PROP_RECV_BUFFER  = "disco.udp.recvBuffer";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration parent;

	private NetworkInterface networkInterface;
	private InetAddress address;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected UdpConfiguration( DiscoConfiguration parent )
	{
		this.parent = parent;

		this.address = null;
		this.networkInterface = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public InetAddress getAddress()
	{
		// have we already done this and found our address?
		if( this.address != null )
			return address;
	
		// Find the Address we are configured to use
		// Valid values for the address are BROADCAST or some literal MulticastAddress
		// that we can use for the NIC
		String prop = parent.getProperty( PROP_ADDRESS, "BROADCAST" );
		if( prop.equals("BROADCAST") )
		{
			// Need to determine the broadcast address for the chosen NIC
			NetworkInterface nic = getNetworkInterface();
			for( InterfaceAddress ifAddr : nic.getInterfaceAddresses() )
			{
				if( ifAddr.getAddress() instanceof Inet6Address )
					continue;
				
				this.address = ifAddr.getAddress();
				break;
			}
			
			if( this.address == null )
				throw new DiscoException( "NIC doesn't support IPv4 Broadcast: "+nic );
		}
		else
		{
			// It if an address, get it directly
			this.address = NetworkUtils.getAddress( prop );
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
		// Works in conjunction with getNetworkInterface()
		// Valid values here are BROADCAST, or some multicast address
		if( address.equals("BROADCAST") )
		{
			parent.setProperty( PROP_ADDRESS, "BROADCAST" );
			this.address = null; // next call to getAddress() will resolve this
			return;
		}
		
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
		return Integer.parseInt( parent.getProperty(PROP_PORT,"3000") );
	}
	
	public void setPort( int port )
	{
		parent.setProperty( PROP_PORT, ""+port );
	}
	
	public NetworkInterface getNetworkInterface()
	{
		if( this.networkInterface == null )
		{
			// have they provided a specific nic?
			String prop = parent.getProperty(PROP_INTERFACE,"SITE_LOCAL" );
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

	public int getSendBufferSize()
	{
		return (int)StringUtils.bytesFromString( parent.getProperty(PROP_SEND_BUFFER,"4MB") );
	}
	
	public void setSendBufferSize( int bytes )
	{
		parent.setProperty(PROP_SEND_BUFFER,""+bytes );
	}

	public int getRecvBufferSize()
	{
		return (int)StringUtils.bytesFromString( parent.getProperty(PROP_RECV_BUFFER,"4MB") );
	}

	public void setRecvBufferSize( int bytes )
	{
		parent.setProperty(PROP_RECV_BUFFER,""+bytes );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
