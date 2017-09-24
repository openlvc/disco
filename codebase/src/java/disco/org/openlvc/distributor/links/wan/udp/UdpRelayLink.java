/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.distributor.links.wan.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.TransportType;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.wan.udp.msg.UdpBundle;
import org.openlvc.distributor.links.wan.udp.msg.UdpJoin;
import org.openlvc.distributor.links.wan.udp.msg.UdpKeepAlive;
import org.openlvc.distributor.links.wan.udp.msg.UdpMessage;
import org.openlvc.distributor.links.wan.udp.msg.UdpResponse;

public class UdpRelayLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;
	private int linkCounter;
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private PacketReceiver packetReceiver;
	
	private BlockingQueue<String> pendingConnections;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpRelayLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		this.reflector      = null;     // set in setReflector()
		this.linkCounter    = 0;

		this.socket         = null;     // set in up()
		this.packet         = null;     // set in up()
		this.packetReceiver = null;     // set in up()
		
		this.pendingConnections = null; // set in up()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Management Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void up()
	{
		if( isUp() )
			return;

		//
		// 1. Prepare the necessary bits and pieces
		//
		this.packet = new DatagramPacket( new byte[UdpMessage.MAX_PACKET_SIZE], UdpMessage.MAX_PACKET_SIZE );
		this.pendingConnections = new LinkedBlockingQueue<>();
		this.linkCounter = 0;
		
		//
		// 2. Open the socket that we'll use
		//
		InetAddress localAddr = NetworkUtils.resolveInetAddress( linkConfiguration.getRelayAddress() );
		int localPort = linkConfiguration.getRelayPort();
		InetSocketAddress local = new InetSocketAddress( localAddr, localPort );
		
		try
		{
			this.socket = new DatagramSocket( local );
		}
		catch( SocketException se )
		{
			logger.error( "Error creating UDP Socket: "+se.getMessage(), se );
			return;
		}
		
		//
		// 3. Start the packet receiver to process incoming messages
		//
		this.packetReceiver = new PacketReceiver( linkConfiguration.getName() );
		this.packetReceiver.start();

		super.linkUp = true;
	}

	@Override
	public void down()
	{
		if( isDown() )
			return;
		
		logger.debug( "Taking UDP Relay link down" );
		
		// Take the packet receiver down
		this.packetReceiver.interrupt();
		ThreadUtils.exceptionlessThreadJoin( packetReceiver, 5000 );
		
		// Disconnect from the socket
		this.socket.disconnect();
		
		this.linkUp = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Outgoing Message Processing Methods   //////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void reflect( Message message )
	{
		
	}

	/**
	 * Sends the given {@link UdpMessage} in a datagram to the given socket address.
	 * This method will catch and log any exception but will not throw them. This should
	 * only be used inside the relay class for things that shouldn't cause the receiver
	 * thread to crash out.
	 */
	private void exceptionlessSend( UdpMessage message, SocketAddress to )
	{
		try
		{
			DatagramPacket datagram = message.toDatagram();
			datagram.setSocketAddress( to );
			this.socket.send( datagram );
		}
		catch( Exception e )
		{
			logger.warn( "(Exception) Sending message (%s) to %s; discarding", message.getType(), to, e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Incoming Message Processing Methods   //////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private void receiveNext( DatagramPacket packet )
	{
		// Log some packet information
		byte[] data = packet.getData();
		int length = packet.getLength();
		if( logger.isTraceEnabled() )
			logger.trace( "Received packet from: %s (%d bytes)", packet.getSocketAddress(), length );
		
		try
		{
			// Turn the packet into a message so that we can process it
			UdpMessage message = UdpMessage.fromByteArray( data, 0 );
			switch( message.getType() )
			{
				case Bundle:
					incomingBundle( (UdpBundle)message );
					break;
				case KeepAlive:
					incomingKeepAlive( (UdpKeepAlive)message, packet );
					break;
				case Join:
					incomingJoin( packet, (UdpJoin)message );
					break;
				case Configure:
					System.out.println( "RECEIVED CONFIGURE" );
					break;
				case Goodbye:
					System.out.println( "RECEIVED GOODBYE" );
					break;
				default:
					logger.error( "Unsupported message type (%s)", message.getType() );
					return;
			}
		}
		catch( Exception e )
		{
			logger.debug( "(Discard Packet) %s", e.getMessage(), e );
			return;
		}
	}
	
	private final void incomingBundle( UdpBundle bundle )
	{
	}
	
	public final void incomingKeepAlive( UdpKeepAlive message, DatagramPacket packet )
	{
		logger.debug( "(Keep Alive) received from %s (request=%s)", packet.getSocketAddress(), message.isRequest() );

		// don't process response KA packets
		if( message.isResponse() )
			return;
		
		// return volley to the place that this KeepAlive request came from
		UdpKeepAlive keepAlive = new UdpKeepAlive();
		keepAlive.setRequest( false );
		exceptionlessSend( keepAlive, packet.getSocketAddress() );
	}
	
	public void incomingJoin( DatagramPacket packet, UdpJoin request ) throws InterruptedException
	{
		SocketAddress sender = packet.getSocketAddress();
		String requestedName = request.getSiteName();
		logger.debug( "(Join) Request by %s with site name %s", sender, requestedName );

		// Get the name they want to use and make sure it isn't already in use
		if( reflector.getDistributor().containsLinkWithName(requestedName) )
		{
			// Name in use - tell them NO LINK FOR YOU
			logger.debug( "(Join) Request by %s REJECTED; site name in use", sender );
			UdpResponse response = new UdpResponse();
			response.setAsError( "Name taken (%s)", requestedName );
			exceptionlessSend( response, sender );
			return;
		}

		// Make sure we aren't already processing a request for this endpoint
		if( pendingConnections.contains(sender.toString()) )
		{
			logger.debug( "(Join) Request by %s already under way, discard latest", sender );
			return;
		}
		else
		{
			pendingConnections.put( sender.toString() );
		}

		// Time to bring up a link in the reverse direction to send traffic to each site
		// and to track separately

		// Name is either <auto> or is good to use
		if( requestedName.equals("<auto>") )
			requestedName = "wan"+(++linkCounter);

		// Generate our local link configuration with the settings needed to reverse direction
		LinkConfiguration local = new LinkConfiguration( requestedName );
		LinkConfiguration remote = request.getLinkConfiguration();
		copyConnectionSettings( local, remote );
		logger.debug( "(Join) Request by %s ACCEPTED; site name is %s", sender, requestedName );
			
		// Set the reverse IP/Port as the target
		local.setWanAddress( packet.getAddress().getHostAddress() );
		local.setWanPort( packet.getPort() );
			                     
		// Create the reverse connection and initialize it
		UdpWanLink reverseLink = new UdpWanLink( local );
		reverseLink.setTransient( true );
		reflector.getDistributor().addAndBringUp( reverseLink );

		// Let them know everything is cool
		UdpResponse response = new UdpResponse();
		response.setAsOk( "", "" );
		exceptionlessSend( response, sender );

		logger.info( "(Join) %s CONNECTED; site name is %s", sender, requestedName );
		//logger.fatal( request.getLinkConfiguration() );
	}
	
	/**
	 * Take the relevant settings from the remote configuration and apply them to the local
	 * configuration. We'll use this config to control the local WAN link we create to wrap
	 * this end of the connection.
	 * 
	 * @param local  The configuration object we will use locally
	 * @param remote The configuration object the remote connection uses
	 */
	private void copyConnectionSettings( LinkConfiguration local, LinkConfiguration remote )
	{
		// Bundling
		local.setWanBundling( remote.isWanBundling() );
		local.setWanBundlingSize( remote.getWanBundlingSizeBytes() );
		local.setWanBundlingTime( remote.getWanBundlingTime() );
		local.setWanAutoReconnect( false );
		
		// Filtering
		// Set remote's ingress as our egress to prevent unnecessary sending.
		// No need to set receive filtering, as it will have been applied before it gets to us
		local.setSendFilter( remote.getReceiveFilter() );
		
		// Transport - redundant, but maybe someone will log it
		local.setWanTransport( TransportType.UDP );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setReflector( Reflector exchange )
	{
		this.reflector = exchange;
	}

	public String getStatusSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			// TODO Replace with metrics
			return getConfigSummary();
		}
		else
		{
			return getConfigSummary();
		}
	}
	
	public String getConfigSummary()
	{
		return "NFI";
	}


	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Receive Processing  /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Class responsible for receiving messages from the remote host represented by this instance */
	private class PacketReceiver extends Thread
	{
		private PacketReceiver( String parentName )
		{
			super( parentName+"-UDP Packet Manager" );
		}
		
		public void run()
		{
			logger.debug( "Listening for remote packets on "+socket.getLocalSocketAddress() );

			while( Thread.interrupted() == false )
			{
				// 1. Receive the next packet
				try
				{
					socket.receive( packet );
					receiveNext( packet );
				}
				catch( IOException ioex )
				{
					logger.error( "Exception while receiving on UDP socket, disconnecting: "+
					              ioex.getMessage(), ioex );
					down();
					return;
				}
			}
		}
	}
}
