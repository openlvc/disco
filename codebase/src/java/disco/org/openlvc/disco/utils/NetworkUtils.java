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

	/** @return true if the given NIC is the loopback. If there is an exception, return false */
	public static boolean isLoopback( NetworkInterface nic )
	{
		try
		{
			return nic.isLoopback();
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	/**
	 * Calls {@link #createMulticast(InetAddress, int, NetworkInterface, SocketOptions)} with
	 * <code>null</code> for socket options.
	 */
	public static DatagramSocket createMulticast( InetAddress address, int port, NetworkInterface nic )
		throws DiscoException
	{
		return createMulticast( address, port, nic, null );
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
	public static DatagramSocket createMulticast( InetAddress address,
	                                              int port,
	                                              NetworkInterface nic,
	                                              SocketOptions options )
			throws DiscoException
	{
		try
		{
    		MulticastSocket socket = new MulticastSocket( port );
    		//asMulticast.setTimeToLive( multicastTTL );
    		//asMulticast.setTrafficClass( multicastTrafficClass );
    		if( options != null )
    		{
    			socket.setSendBufferSize( options.sendBufferSize );
    			socket.setReceiveBufferSize( options.getRecvBufferSize() );
    		}

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
		return createBroadcast( address, port, null );
	}

	public static DatagramSocket createBroadcast( InetAddress address, int port, SocketOptions options )
		throws DiscoException
	{
		try
		{
			// Create socket with null, which will create an unbound socket.
			// We do this so we can modify the socket properties prior to binding
			DatagramSocket socket = new DatagramSocket(null);
			socket.setReuseAddress( true ); // could be others listening as well
			socket.setBroadcast( true );
			if( options != null )
			{
				socket.setSendBufferSize( options.getSendBufferSize() );
				socket.setReceiveBufferSize( options.getRecvBufferSize() );
			}

			// Bind the socket. Because we're broadcast, bind it to the wildcard
			// address, which we can do by creating the socket address with port only
			socket.bind( new InetSocketAddress(address,port) );

			// Write Once, Cry Everywhere
			// First (port) works on both
			// Second (addr/port) works on Windows, not on the mac
			// Third (bcast/port) works on the Mac, not on Windows
			//socket.bind( new InetSocketAddress(port) );
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
	 * Creates a send/receive socket pair and returns. This is used when you want a setup that supports
	 * multiple applications on the same host sending/receiving using broadcast. To avoid loopback we
	 * need to filter out our own traffic. The only way we can distinguish this is by the send port of
	 * a packet (send IP not enough, as there could be many apps on that IP).
	 * 
	 * To do this we need a socket that sends from a port we're not listening on. If send/receive port
	 * matched, we couldn't tell any local machine traffic apart.
	 *  
	 * So, this method creates two sockets:
	 *   1. Send Socket: Bound to wildcard address with ephemeral port. Target address/port defined on packet when you send.
	 *   2. Recv Socket: Bound to specified address and port.
	 * 
	 * In the receiver we can then compare the send port to our own send socket port. If they match, the
	 * packet was sent from our application.
	 * 
	 * @param address Address to bind the receive socket to
	 * @param port    Port to bind the receive socket to
	 * @param options Send/Receive socket configuration options
	 * @return
	 */
	public static DatagramSocket[] createBroadcastPair( InetAddress address,
	                                                    int port,
	                                                    SocketOptions options )
	{
		try
		{
			// Create the send socket
			DatagramSocket sendSocket = new DatagramSocket(null);
			sendSocket.setReuseAddress( true );
			sendSocket.setBroadcast( true );
			if( options != null )
				sendSocket.setSendBufferSize( options.getSendBufferSize() );
			
			// Create the receive socket
			DatagramSocket recvSocket = new DatagramSocket(null);
			recvSocket.setReuseAddress( true );
			recvSocket.setBroadcast( true );
			if( options != null )
				recvSocket.setReceiveBufferSize( options.getRecvBufferSize() );
			
			// bind the two sockets
			sendSocket.bind( new InetSocketAddress(0) ); // ephermal port
			recvSocket.bind( new InetSocketAddress(address,port) );
			return new DatagramSocket[] { sendSocket, recvSocket };
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
