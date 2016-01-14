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
package org.openlvc.disco.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.openlvc.disco.DiscoException;

public class NetworkUtils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	static { System.setProperty("java.net.preferIPv4Stack", "true"); }

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static InetAddress getByName( String name ) throws DiscoException
	{
		try
		{
    		// TODO Implement something clever that will loop up the following symbols
    		//      LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL, 192.168.*.*, etc...
    		return InetAddress.getByName( name );
		}
		catch( Exception e )
		{
			throw new DiscoException( e.getMessage(), e );
		}
	}

	/**
	 * Creates and connects to a multicast socket on the given address/port before returning it.
	 * 
	 * @param hostAddress The multicast address to listen on
	 * @param iface The network interface to communicate through
	 * 
	 * @return A DatagramSocket representing the multicast socket connected to the desired address
	 * 
	 * @throws IOException thrown if there was an error connecting the socket
	 */
	public static DatagramSocket createMulticast( InetAddress address, int port, NetworkInterface nic )
			throws DiscoException
	{
		try
		{
    		MulticastSocket socket = new MulticastSocket( port );
    		//asMulticast.setTimeToLive( multicastTTL );
    		//asMulticast.setTrafficClass( multicastTrafficClass );
    		InetSocketAddress socketAddress = new InetSocketAddress( address, port );
    		socket.joinGroup( socketAddress, nic );
    		return socket;
		}
		catch( IOException ioex )
		{
			throw new DiscoException( ioex );
		}
	}
	
	public static DatagramSocket createBroadcast( InetAddress address, int port )
		throws DiscoException
	{
		try
		{
			DatagramSocket socket = new DatagramSocket(null); // null to avoid implicit bind!
			socket.setReuseAddress( true );                   // could be others listening as well
			socket.setBroadcast( true );
			socket.bind( new InetSocketAddress(port) );       // works everywhere - but on all nics
			// Write Once, Cry Everywhere
			// First (addr/port) works on Windows, not on the mac
			// Second (bcast/port) works on the Mac, not on Windows
			//socket.bind( new InetSocketAddress(address,port) );
			//socket.bind( new InetSocketAddress(getInterfaceAddress(address).getBroadcast(),port) );
			return socket;
		}
		catch( Exception e )
		{
			throw new DiscoException( "Cannot connect to "+address+":"+port+" - "+e.getMessage() , e );
		}
	}

	/**
	 * Takes the given name and converts it into a {@link InetAddress}. This will exchange
	 * the symbols for the following values:
	 * 
	 *   - `LOOPBACK`: Loopback address
	 *   - `LINK_LOCAL`: Anything in the 169.254.*.* block
	 *   - `SITE_LOCAL`: Anything in 192.168.x.x, 10.x.x.x or 172.x.x.x
	 *   - `GLOBAL`: Address that is outside of these
	 * 
	 * If none of those symbols are provided, we will try and resolve the name directly as
	 * provided for an Address from which we can then get an interface.
	 */
	public static NetworkInterface getNetworkInterface( String name )
	{
		try
		{
			if( name.equals("LOOPBACK") )
			{
				return NetworkInterface.getByInetAddress( InetAddress.getLoopbackAddress() );
			}
			else if( name.equals("LINK_LOCAL") || name.equals("SITE_LOCAL") || name.equals("GLOBAL") )
			{
				// get all the network interfaces that are link local
				List<NetworkInterface> interfaces = getListOfNetworkInterfaces();
				NetworkInterface maybe = null;
				for( NetworkInterface nic : interfaces )
				{
					for( InterfaceAddress addr : nic.getInterfaceAddresses() )
					{
						if( (name.equals("LINK_LOCAL") && addr.getAddress().isLinkLocalAddress()) ||
							(name.equals("SITE_LOCAL") && addr.getAddress().isSiteLocalAddress()) ||
							(name.equals("GLOBAL") && !addr.getAddress().isAnyLocalAddress()) )
						{
							// if an IPv4 address, return immediately as it's the nic we want
							// otherwise, store it as a "maybe" in case we find an IPv4 one shortly
							if( addr.getAddress() instanceof Inet4Address )
								return nic;
							else
								maybe = nic;
						}
					}
				}
				
				if( maybe == null )
					throw new DiscoException( "Couldn't find an interface with "+name+" address" );
				else
					return maybe; // will have IPv6 addr bound - would've returned early otherwise
			}
			else
			{
				return NetworkInterface.getByInetAddress( InetAddress.getByName( name ) );
    		}
		}
		catch( Exception e )
		{
			throw new DiscoException( e );
		}
	}
	
	private static List<NetworkInterface> getListOfNetworkInterfaces() throws Exception
	{
		List<NetworkInterface> interfaces = new ArrayList<NetworkInterface>();
		Enumeration<NetworkInterface> temp = NetworkInterface.getNetworkInterfaces();
		while( temp.hasMoreElements() )
			interfaces.add( temp.nextElement() );
		
		return interfaces;
	}

	/**
	 * Wraps up `InetAddress.getByName(String)` so that it throws a `DiscoException`
	 * @param name
	 * @return
	 */
	public static InetAddress getAddress( String name ) throws DiscoException
	{
		try
		{
			return InetAddress.getByName( name );
		}
		catch( Exception e )
		{
			throw new DiscoException( e );
		}
	}

	/**
	 * For the given {@link InetAddress}, find and retuen the {@link InterfaceAddress}.
	 * We can extract more information from this, such as broadcast address.
	 */
	public static InterfaceAddress getInterfaceAddress( InetAddress regular )
	{
		try
		{
			NetworkInterface nic = NetworkInterface.getByInetAddress( regular );
			for( InterfaceAddress addr : nic.getInterfaceAddresses() )
				if( addr.getAddress().equals(regular) )
					return addr;
			
			// we didn't find it if we get here
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
	}
	
}
