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
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
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
	/**
	 * Gets an InetAddress, performing substitutions for common symbolic names.
	 * If any of the following names are found, they will be replaced with the address
	 * of the first local network interface that matches.
	 * <p/>
	 * If none is found, null will be returned. 
	 * 
	 * <ul>
	 *   <li><code>LOOPBACK</code></li>
	 *   <li><code>LINK_LOCAL</code></li>
	 *   <li><code>SITE_LOCAL</code></li>
	 *   <li><code>GLOBAL</code></li>
	 * </ul>
	 * 
	 * If the name is not symbolic, a direct <code>InetAddress.getByName()</code> will be
	 * run, which should match any domain names, or direct IP address references. Should
	 * it not, an exception will be thrown.
	 * 
	 * @param name The name or ip address to look up
	 * @return The InetAddress that matches, or null if a symbolic name is passed but none is found
	 * @throws DiscoException If the given non-symbolic name doesn't match an IP or valid host name
	 */
	public static InetAddress resolveInetAddress( String name ) throws DiscoException
	{
		try
		{
    		if( name.equalsIgnoreCase("LOOPBACK" ) )
    			return InetAddress.getLoopbackAddress();
    
    		// Get a list of all the nics we have. The one we want will (hopefully) be in here somewhere!
    		List<InetAddress> pool = getListOfNetworkAddresses();
    
    		if( name.equalsIgnoreCase("LINK_LOCAL") )
    		{
    			return pool.stream()
    			           .filter( address -> address.isLinkLocalAddress() )
    			           .findFirst()
    			           .get();
    		}
    		
    		if( name.equalsIgnoreCase("SITE_LOCAL") )
    		{
    			return pool.stream()
    			           .filter( address -> address.isSiteLocalAddress() )
    			           .findFirst()
    			           .get();
    		}
    		
    		if( name.equalsIgnoreCase("GLOBAL") )
    		{
    			return pool.stream()
    			           .filter( address -> address.isLoopbackAddress() == false )
    			           .filter( address -> address.isAnyLocalAddress() == false )
    			           .filter( address -> address.isLinkLocalAddress() == false )
    			           .filter( address -> address.isSiteLocalAddress() == false )
    			           .findFirst()
    			           .get();
    		}
		}
		catch( NoSuchElementException no )
		{
			throw new DiscoException( "Cannot find an address for setting: "+name );
		}
		
		// Try a direct name match
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
	 * Return a list of all the {@link NetworkInterface}s in the machine, regardless of whether
	 * they are up or not at the moment.
	 */
	public static List<NetworkInterface> getAllNetworkInterfaces()
	{
		try
		{
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
    		List<NetworkInterface> list = new ArrayList<>();
    		while( nics.hasMoreElements() )
    			list.add( nics.nextElement() );
    		
    		return list;
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Exception while fetching all network interfaces: "+
			                          ioex.getMessage(), ioex );
		}
	}
	
	/**
	 * Return a list of all the {@link NetworkInterface}s in the machine that are currently up
	 * and active.
	 */
	public static List<NetworkInterface> getAllNetworkInterfacesUp()
	{
		try
		{
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
    		List<NetworkInterface> list = new ArrayList<>();
    		while( nics.hasMoreElements() )
    		{
    			NetworkInterface nic = nics.nextElement();
    			if( nic.isUp() )
    				list.add( nic );
    		}
    		
    		return list;
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Exception while fetching all network interfaces: "+
			                          ioex.getMessage(), ioex );
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
	
	private static List<NetworkInterface> getListOfNetworkInterfaces() throws DiscoException
	{
		try
		{
    		List<NetworkInterface> interfaces = new ArrayList<NetworkInterface>();
    		Enumeration<NetworkInterface> temp = NetworkInterface.getNetworkInterfaces();
    		while( temp.hasMoreElements() )
    			interfaces.add( temp.nextElement() );
    		
    		return interfaces;
		}
		catch( SocketException se )
		{
			throw new DiscoException( "Exception fetching all networks interfaces: "+se.getMessage(), se );
		}
	}

	/**
	 * Return a list of all addresses associated with any active NIC in this computer.
	 */
	private static List<InetAddress> getListOfNetworkAddresses() throws DiscoException
	{
		List<NetworkInterface> nics = getListOfNetworkInterfaces();
		List<InetAddress> pool = new ArrayList<>();
		for( NetworkInterface nic : nics )
		{
			Enumeration<InetAddress> addresses = nic.getInetAddresses();
			while( addresses.hasMoreElements() )
				pool.add( addresses.nextElement() );
		}
		
		return pool;
	}

	/**
	 * Return the first IPv4 address associated with the given network interface.
	 * Return null if one could not be found.
	 */
	public static Inet4Address getFirstIPv4Address( NetworkInterface nic )
	{
		Optional<Inet4Address> found = nic.getInterfaceAddresses().stream()
			   .filter( addr -> (addr.getAddress() instanceof Inet4Address) )
			   .map( addr -> (Inet4Address)addr.getAddress() )
			   .findFirst();
		
		if( found.isPresent() )
			return found.get();
		else
			return null;
	}

	/**
	 * Return the first {@link InterfaceAddress} for IPv4 in the given NIC.
	 * Return null if none could be found.
	 */
	public static InterfaceAddress getFirstIPv4InterfaceAddress( NetworkInterface nic )
	{
		for( InterfaceAddress ifaddress : nic.getInterfaceAddresses() )
		{
			if( ifaddress.getAddress() instanceof Inet4Address )
				return ifaddress;
		}
		
		return null;
	}
	
	public static InterfaceAddress getFirstIPv6InterfaceAddress( NetworkInterface nic )
	{
		for( InterfaceAddress ifaddress : nic.getInterfaceAddresses() )
		{
			if( ifaddress.getAddress() instanceof Inet6Address )
				return ifaddress;
		}
		
		return null;
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
	
	/**
	 * Log some information on startup about all the available NICs to the DEBUG level
	 */
	public static void logNetworkInterfaceInformation( Logger logger )
	{
		logger.debug( "List of Available Network Interfaces" );
		logger.debug( "------------------------------------" );

		for( NetworkInterface nic : NetworkUtils.getAllNetworkInterfacesUp() )
		{
			InterfaceAddress if4addr = getFirstIPv4InterfaceAddress( nic );
			String ipv4   = if4addr == null ? "" : ((Inet4Address)if4addr.getAddress()).getHostAddress();
			String subnet = if4addr == null ? "" : getSubnetMaskString( if4addr.getNetworkPrefixLength() );
			String bcast  = if4addr == null ? "" : if4addr.getBroadcast().getHostAddress();

			InterfaceAddress if6addr = getFirstIPv6InterfaceAddress( nic );
			String ipv6   = if6addr == null ? "" : ((Inet6Address)if6addr.getAddress()).getHostAddress();
			
			logger.debug( nic.getDisplayName()+" ("+nic.getName()+")" );
			logger.debug( "  Link-local IPv6 Address . . . . . : "+ipv6 );
			logger.debug( "  IPv4 Address. . . . . . . . . . . : "+ipv4 );
			logger.debug( "  Subnet Mask . . . . . . . . . . . : "+subnet );
			logger.debug( "  Broadcast . . . . . . . . . . . . : "+bcast );
			logger.debug( "" ); // spacer
			logger.debug( "" ); // spacer
		}
	}

	/**
	 * Returns the IPv4 subnet mask string for the given prefix length. If there is a problem the
	 * string `<exception:message>` is returned.
	 * @param prefix
	 * @return
	 */
	public static String getSubnetMaskString( short prefix )
	{
	    int mask = 0xffffffff << (32 - prefix);
	    int value = mask;
	    byte[] bytes = new byte[]{ 
	            (byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };

	    try
	    {
	    	InetAddress netAddr = InetAddress.getByAddress(bytes);
	    	return netAddr.getHostAddress();
	    }
	    catch( Exception e )
	    {
	    	return "<exception:"+e.getMessage()+">";
	    }
	}
}

