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

import java.io.EOFException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.DisSizes;
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
	private static final String PROP_TTL          = "disco.udp.ttl";
	private static final String PROP_TRAFFIC_CLASS= "disco.udp.trafficClass";
	private static final String PROP_MAX_PDU_SIZE = "disco.udp.maxPduSize";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration parent;

	private NetworkInterface networkInterface;
	private InetAddress targetAddress;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected UdpConfiguration( DiscoConfiguration parent )
	{
		this.parent = parent;

		this.targetAddress = null;
		this.networkInterface = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Returns the value stored in the "address" field of the configuration. This can
	 * be specified as the symbolic name "BROADCAST". If that is the case, the returned
	 * address will be determined by looking at the network interface, finding the first
	 * IPv4 enabled address and getting its broadcast value.
	 * 
	 * If a literal address is provided, it will be returned without modification.
	 * 
	 * For example:
	 *   +----------------+---------------+
	 *   |   Sepcified    |    Returned   |
	 *   +----------------+---------------+
	 *   | 192.168.0.123  | 192.168.0.123 |
	 *   +----------------+---------------+
	 *   | 192.168.0.255  | 192.168.0.255 |
	 *   +----------------+---------------+
	 *   | BROADCAST      | 192.168.0.255 | << Depends on NIC
	 *   +----------------+---------------+
	 *   | 239.0.1.123    | 239.0.1.123   | << Multicast
	 *   +----------------+---------------+
	 * 
	 * @return Address that all packets we send should be addressed to.
	 */
	public InetAddress getAddress()
	{
		// lazy load
		if( this.targetAddress != null )
			return targetAddress;

		// Find the Address we are configured to use
		// Valid values for the address are BROADCAST or some literal MulticastAddress
		// that we can use for the NIC
		String prop = parent.getProperty( PROP_ADDRESS, "BROADCAST" );
		if( prop.equalsIgnoreCase("BROADCAST") )
		{
			//
			// Broadcast Address
			//
			// Get the NIC (which itself can be specified many ways) and find
			// the first IPv4 address that it has, then set our address to the
			// _broadcast_ address associated with it
			NetworkInterface nic = getNetworkInterface();
			
			// if we are using the loopback adapter, just assign the proper IP and exit
			if( NetworkUtils.isLoopback(nic) )
			{
				this.targetAddress = NetworkUtils.getFirstIPv4InterfaceAddress(nic).getBroadcast();
				//this.targetAddress = NetworkUtils.getAddress( "127.0.0.1" );
				return this.targetAddress;
			}

			// check for a broadcast address on the nic
			for( InterfaceAddress interfaceAddress : nic.getInterfaceAddresses() )
			{
				if( interfaceAddress.getAddress() instanceof Inet6Address )
					continue;
				
				this.targetAddress = interfaceAddress.getBroadcast();
				break;
			}

			if( this.targetAddress == null )
				throw new DiscoException( "NIC doesn't support IPv4 Broadcast: "+nic );
		}
		else
		{
			// It if an address, get it directly
			this.targetAddress = NetworkUtils.getAddress( prop );
		}

		return this.targetAddress;
	}

	/**
	 * @return The address as it was configured. For example, if the symbolic name "BROADCAST" was
	 *         used, this method will return that, rather than whatever the broadcast IP currently
	 *         is for the interface.
	 */
	public String getAddressString()
	{
		return parent.getProperty( PROP_ADDRESS, "BROADCAST" );
	}

	/**
	 * Sets the target address to the given one. This will be used as the target address
	 * for the construction of all packets from here on in.
	 */
	public void setAddress( InetAddress address )
	{
		// no need to do anything special - by setting this value we'll cause getAddress()
		// to short-circuit as it is lazy-loaded
		this.targetAddress = address;
	}

	/**
	 * Set the target address to the given one. This will be used as the target address
	 * for the construction of all packets from here on in.
	 * 
	 * Valid values are either an explicit address (such as a multicast address), or the
	 * symbolic "BROADCAST" value. If that is provided, the broadcast address of the specified
	 * interface (as returned by {@link #getNetworkInterface()} will be used.
	 */
	public void setAddress( String addressString ) throws DiscoException
	{
		parent.setProperty( PROP_ADDRESS, addressString );

		// Works in conjunction with getNetworkInterface()
		// Valid values here are BROADCAST, or some multicast address
		if( addressString.equals("BROADCAST") )
		{
			this.targetAddress = null; // next call to getAddress() will resolve this
			return;
		}
		
		try
		{
			this.targetAddress = InetAddress.getByName( addressString );
		}
		catch( UnknownHostException uhe )
		{
			throw new DiscoException( "Could not find address: "+addressString );
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

	/**
	 * Specifies the network interface that should be used for sending. There are two ways
	 * to specify this.
	 * 
	 *  1. Either as an explicit IP address or DNS name
	 *     The NIC associated with that name will be looked up and used
	 *     
	 *  2. One of the special symbols which will cause a more intelligent lookup
	 *     LOOPBACK  : The local loopback address 
	 *     LINK_LOCAL: An address valid on the local link (typically self-assigned ips)
	 *     SITE_LOCAL: An address that is limited in scope to local site (10.x, 192.168.x, etc...)
	 *     GLOBAL    : An address that is globablly routable on the internet
	 * 
	 * Note that NIC should will affect {@link #getAddress()} if the address has been set to
	 * BROADCAST, then the chosen broadcast address will depend on the address of the specified
	 * NIC. 
	 */
	public NetworkInterface getNetworkInterface()
	{
		if( this.networkInterface == null )
		{
			// have they provided a specific nic?
			String prop = parent.getProperty( PROP_INTERFACE, "SITE_LOCAL" );
			this.networkInterface = NetworkUtils.getNetworkInterface( prop );
		}

		return this.networkInterface;
	}

	/**
	 * @return The network interface as it was configured in "String" form, not transformed into
	 *         a NetworkInterface instance. This is useful if you are using the symbolic name
	 *         support and you want to write the value out based on current configuration. If you
	 *         just used the interface, you'd get the interface name, but often you want the 
	 *         symbolic name, because it's more broad.
	 */
	public String getNetworkInterfaceString()
	{
		return parent.getProperty( PROP_INTERFACE, "SITE_LOCAL" );
	}
	
	public void setNetworkInterface( NetworkInterface networkInterface )
	{
		this.networkInterface = networkInterface;
	}
	
	public void setNetworkInterface( String name )
	{
		NetworkInterface temp = NetworkUtils.getNetworkInterface( name );
		if( temp == null )
			throw new DiscoException( "Invalid network interface [%s]: Not Found", name );

		this.networkInterface = temp;
		parent.setProperty( PROP_INTERFACE, name );
	}

	public int getSendBufferSize()
	{
		return (int)StringUtils.bytesFromString( parent.getProperty(PROP_SEND_BUFFER,"8MB") );
	}
	
	public void setSendBufferSize( int bytes )
	{
		parent.setProperty(PROP_SEND_BUFFER,bytes+"B" );
	}

	public int getRecvBufferSize()
	{
		return (int)StringUtils.bytesFromString( parent.getProperty(PROP_RECV_BUFFER,"8MB") );
	}

	public void setRecvBufferSize( int bytes )
	{
		parent.setProperty(PROP_RECV_BUFFER,bytes+"B" );
	}
	
	public int getTimeToLive()
	{
		return Integer.parseInt( parent.getProperty( PROP_TTL, "254" ) );
	}
	
	public void setTimeToLive( int ttl )
	{
		parent.setProperty( PROP_TTL, String.valueOf(ttl) );
	}
	
	public int getTrafficClass()
	{
		return Integer.parseInt( parent.getProperty(PROP_TRAFFIC_CLASS, "0") );
	}
	
	public void setTrafficClass( int clasz )
	{
		parent.setProperty( PROP_TRAFFIC_CLASS, String.valueOf(clasz) );
	}

	/**
	 * Returns the buffer size that will be allocated for reading a PDU from a UDP Datagram
	 * <p/>
	 * Some systems may take advantage of IP4 Fragmentation and send Datagrams that are beyond the 
	 * size of the UDP Maximum Transmission Unit (1500 bytes). In this case the IP layer will 
	 * fragment the Datagram over several UDP packets and reassemble the Datagram on the other side 
	 * before handing it to the application layer.
	 * <p/>
	 * This value represents the size of the buffer that DISCO will use when reading the complete
	 * Datagram from the application layer. 
	 * <p/>
	 * If you are constantly receiving {@link EOFException}s when receiving PDUs then consider
	 * increasing the size of the buffer by calling {@link #setMaxPduSize(int)}
	 * 
	 * @return the buffer size that will be allocated for reading a PDU from a UDP Datagram, 
	 *         specified in bytes
	 */
	public int getMaxPduSize()
	{
		return Integer.parseInt( parent.getProperty(PROP_MAX_PDU_SIZE, Integer.toString(DisSizes.PDU_MAX_SIZE)) );
	}
	
	public void setMaxPduSize( int bytes )
	{
		parent.setProperty( PROP_MAX_PDU_SIZE, Integer.toString(bytes) );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
