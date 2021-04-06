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
	 * Create and return a pair of sockets that can be used for multicast networking. The first
	 * socket in the returned array is the socket to use for sending. This is a regular datagram
	 * socket with the options that have been passed in. The second socket is for receiving. This
	 * is a multicast socket joined to the group you have identified in the address/port parameters
	 * and on the provided NIC (with the options set as well).
	 * <p/>
	 * <b>Why do I need this?</b><p/>
	 * Having a separate sender socket which uses an ephemeral port for its sending (rather than
	 * a port that is the same as the receive port) lets you identify when the packets you are
	 * receiving are copies that are reflected back to you.
	 * <p/>
	 * Multicast sockets have a setLoopback() method that can prevent message loop back, but its
	 * impact varies across platforms. On Windows, disabling loopback will stop all messages from
	 * the local computer looping back, regardless of whether they are in the same execution or not.
	 * This means that you can't run two instances of an application with the same network settings
	 * on the same computer and have them exchange data.
	 * <p/>
	 * To combat this, we leave local loopback enabled, and on reception <b>YOU</b> need to check
	 * the sender address and port, and if they match that of the sender socket, you know they came
	 * from your application. Because the sender-side source port is ephemeral, it (likely) won't
	 * match the receiving port. For a second application the same applies - the sender ports for
	 * both applications will differ, even though the destination ports are the same. If the ports
	 * (and addresses) match, it's your traffic and you can toss it.
	 * <p/>
	 * 
	 * <b>Example:</b><p/>
	 * <ol>
	 *     <li>App A starts up. Sender port is randomly assigned as 50443. Destination port (the
	 *         one that the multicast socket is listening on) is 3000.</li>
	 *     <li>App B starts up. Sender port is randomly assigned as 35449. Destination port is
	 *         the same; 3000.</li>
	 *     <li>App A sends a message.</li>
	 *     <li>App A receives its own message. It checks the sender port and finds 50443. It 
	 *         compares this to its sender port and finds they're the same. Dicard packet.</li>
	 *     <li>App B receives the message. It checks the sender port (50443) against its sender
	 *         port (35449) and finds they're not the same. Let it pass.</li>
	 * </ol>
	 * <b>YOU HAVE TO DO THESE CHECKS</b>. Also remember to check the address if the ports match,
	 * because a different source IP is fine even if the ports match.
	 * 
	 * @param address The multicast group address that the receiver socket will join
	 * @param port    The multicast port that the receiver socket will use when joining the group
	 * @param nic     The NIC that the multicast socket will listen on
	 * @param options The options for the socket (sender/recevier buffers, TTL, ...)
	 * @return        A pair of sockets that meet the above description. The first is for sending,
	 *                and is datagram socket with an ephemeral port as the source. The second is
	 *                the multicast socket to use for receiving data, joined to the multicast group
	 *                defined by the address/port passed in on the nic that is passed in
	 * @throws DiscoException
	 */
	public static DatagramSocket[] createMulticastPair( InetAddress address,
	                                                    int port,
	                                                    NetworkInterface nic,
	                                                    SocketOptions options )
		throws DiscoException
	{
		try
		{
			// Create the send socket -- won't be joined to the group and will use ephemeral port,
			//                           but will be bound to the first IP on the given NIC, 
			//                           otherwise the address is 0.0.0.0 when a packet is looped
			//                           back from the local app on Windows (didn't test others),
			//                           and our address check against the send socket address will
			//                           differ (valid address != 0.0.0.0 so it thinks they're from
			//                           different PCs
			DatagramSocket sendSocket = new DatagramSocket( 0, getFirstIPv4Address(nic) ); // ephemeral
			if( options != null )
			{
				sendSocket.setSendBufferSize( options.getSendBufferSize() );
				sendSocket.setTrafficClass( options.getTrafficClass() );
			}
			
			// Create the receiver socket
			MulticastSocket recvSocket = new MulticastSocket( port );
			if( options != null )
			{
				recvSocket.setTimeToLive( options.timeToLive );
				//recvSocket.setLoopbackMode( true ); LEAVE LOCAL LOOPBACK ALONE.
				recvSocket.setReceiveBufferSize( options.getRecvBufferSize() );
			}

			// Join the multicast group for the receiver socket
			InetSocketAddress multicastAddress = new InetSocketAddress( address, port );
			recvSocket.joinGroup( multicastAddress, nic );
			
			return new DatagramSocket[] { sendSocket, recvSocket };
		}
		catch( IOException ioex )
		{
			throw new DiscoException( ioex );
		}
	}

	/**
	 * Create and return a pair of datagram sockets configured to use the broadcast address
	 * associated with the given IP address. 
	 * Creates a pair of datagram sockets (one for sending, one for listening/receiving) configured
	 * to use broadcast and returns them. The sender socket will be bound to an ephemeral port as
	 * its source. The receiver socket will listen on the provided port. This is done to allow
	 * accurate filtering of messages that were received by the application that sent them.  
	 * </p>
	 * To avoid loopback you will still need to filter your own traffic. Compare the source port
	 * of the received packet against the port of the sender socket. If they match AND the source
	 * address is the same as the sender socket address, then they are local messages that have
	 * been looped back. If they don't match, they're from somewhere else.
	 * <p/>
	 * 
	 * To enable this we need a socket that sends from a port we're not listening on, which is why
	 * the sender socket sends from an ephemeral port. If send/receive port matched, we couldn't.
	 * <p/>
	 *  
	 * We return two sockets in the array:
	 * <ol>
	 *   <li>Send Socket: Bound to wildcard address with ephemeral port. Target address/port defined on packet when you send.</li>
	 *   <li>Recv Socket: Bound to specified address and port.</li>
	 * </ol>
	 * 
	 * Also see {@link #createMulticastPair(InetAddress, int, NetworkInterface, SocketOptions)} for
	 * a more detailed description of handling local loopback (both methods use the same approach
	 * for dealing with this issue).
	 * 
	 * @param address Address to bind the receive socket to
	 * @param port    Port to bind the receive socket to
	 * @param options Send/Receive socket configuration options
	 * @return A socket pair that can be used with the above steps to mitigate local loopback.
	 */
	public static DatagramSocket[] createBroadcastPair( InetAddress address,
	                                                    int port,
	                                                    NetworkInterface nic,
	                                                    SocketOptions options )
	{
		try
		{
			InetAddress nicAddress = getFirstIPv4Address( nic );
			
			// Create the send socket
			// Bind to ephemeral port (packets will have a target port) and ip-address of given nic.
			// NOTE: SO_REUSEADDR not required here are we are binding the socket to an ephemeral port
			DatagramSocket sendSocket = new DatagramSocket( 0, nicAddress );
			sendSocket.setBroadcast( true );
			if( options != null )
			{
				sendSocket.setSendBufferSize( options.getSendBufferSize() );
				sendSocket.setTrafficClass( options.getTrafficClass() );
			}
			
			// Create the receive socket with a null bindaddr parameter so that it is created in an 
			// unbound state. We need to do this as the SO_REUSEADDR option must be set before binding 
			// (otherwise it is ignored).
			//
			// NOTE: The no-param constructor can NOT be used here as that would create a socket bound to 
			// INADDR_ANY and an ephemeral port. 
			DatagramSocket recvSocket = new DatagramSocket( null );
			recvSocket.setReuseAddress( true );
			recvSocket.setBroadcast( true );
			if( options != null )
				recvSocket.setReceiveBufferSize( options.getRecvBufferSize() );
			
			// Bind receive socket now that we have set SO_REUSEADDR
			recvSocket.bind( new InetSocketAddress(nicAddress, port) );
			
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
		Optional<Inet4Address> found = 
			nic.getInterfaceAddresses().stream()
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
			logNetworkInterfaceInformation( logger, nic );
	}

	/**
	 * Return a string in a format much like `ipconfig` on Windows
	 * 
	 * ```
	 * Network Interface Display Name (short name)
	 * 
	 *   Link-local IPv6 Address . . . . . : fe80::abb2:b5cb:8c7:f16f%10
	 *   IPv4 Address. . . . . . . . . . . : 192.168.7.1
	 *   Subnet Mask . . . . . . . . . . . : 255.255.255.0
	 *   Broadcast . . . . . . . . . . . . : 192.168.7.255
	 * ```
	 * 
	 * @param nic The interface to pull the address information from
	 */
	private static void logNetworkInterfaceInformation( Logger logger, NetworkInterface nic )
	{
		String ipv4 = "", subnet = "", bcast = "";
		String ipv6 = "";
		
		// Get the IPv4 Information
		InterfaceAddress if4addr = getFirstIPv4InterfaceAddress( nic );
		if( if4addr != null )
		{
			ipv4 = ((Inet4Address)if4addr.getAddress()).getHostAddress();
			subnet = getSubnetMaskString( if4addr.getNetworkPrefixLength() );
		}
		
		// Sometimes the broadcast can be null (Linux loopback seems to be)
		if( if4addr != null && if4addr.getBroadcast() != null )
			bcast = if4addr.getBroadcast().getHostAddress();

		// Get IPv6 Information
		InterfaceAddress if6addr = getFirstIPv6InterfaceAddress( nic );
		if( if6addr != null )
			ipv6 = ((Inet6Address)if6addr.getAddress()).getHostAddress();
		
		logger.debug( nic.getDisplayName()+" ("+nic.getName()+")" );
		logger.debug( "  Link-local IPv6 Address . . . . . : "+ipv6 );
		logger.debug( "  IPv4 Address. . . . . . . . . . . : "+ipv4 );
		logger.debug( "  Subnet Mask . . . . . . . . . . . : "+subnet );
		logger.debug( "  Broadcast . . . . . . . . . . . . : "+bcast );
		logger.debug( "" ); // spacer
		logger.debug( "" ); // spacer
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
		byte[] bytes = new byte[] { 
			(byte)(value >>> 24), 
			(byte)(value >> 16 & 0xff), 
			(byte)(value >> 8 & 0xff), 
			(byte)(value & 0xff) 
		};

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

