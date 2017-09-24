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
package org.openlvc.distributor.links.relay.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import org.openlvc.disco.utils.BitHelpers;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.StringUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.wan.udp.UdpJoinRequest;
import org.openlvc.distributor.links.wan.udp.UdpMessage;
import org.openlvc.distributor.links.wan.udp.UdpResponse;
import org.openlvc.distributor.links.wan.udp.UdpSendConfiguration;
import org.openlvc.distributor.links.wan.udp.UdpWanLink;

public class UdpRelayLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;

	private DatagramSocket socket;
	private DatagramPacket packet;
	private PacketReceiver packetReceiver;
	
	private Set<SocketAddress> pendingConnections;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpRelayLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		this.reflector = null;          // set in setReflector()

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
	public void up()
	{
		if( isUp() )
			return;

		//
		// 1. Prepare the necessary bits and pieces
		//
		this.packet = new DatagramPacket( new byte[UdpMessage.MAX_PACKET_SIZE], UdpMessage.MAX_PACKET_SIZE );
		this.pendingConnections = new HashSet<>();
		
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
	public void reflect( Message message )
	{
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Incoming Message Processing Methods   //////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void incomingBundle()
	{
		
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
					processPacket( packet );
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
		
		private void processPacket( DatagramPacket received )
		{
			byte[] data = received.getData();
			int length = received.getLength();
			logger.trace( "Received packet from: %s (%d bytes)", received.getSocketAddress(), length );
			
			// Verify there is enough size
			if( length < UdpMessage.MIN_PACKET_SIZE )
			{
				logger.warn( "Discarding undersized packet (min: %d, received: %d)",
				             UdpMessage.MIN_PACKET_SIZE, length );
				return;
			}

			// Read the magic number
			int magicNumber = BitHelpers.readIntBE( data, 0 );
			if( magicNumber != UdpMessage.MAGIC_NUMBER )
			{
				logger.warn( "Received unknown message type. Magic Number = %h", magicNumber );
				return;
			}

			// Read the message type
			int messageType = BitHelpers.readIntBE( data, 4 );
			switch( messageType )
			{
				case UdpMessage.BUNDLE:
					break;
				
				case UdpMessage.JOIN_REQUEST:
					processHandshake( received );
					break;
				
				case UdpMessage.CONFIG_UPLOAD:
					processConfigUpload( received );
					break;

				default:
					logger.error( "Unknown UDP message type, discarding (id=%h)", messageType );
					return;
			}
		}
		
		private void processHandshake( DatagramPacket packet )
		{
			SocketAddress sender = packet.getSocketAddress();
			logger.debug( "Connection request from "+sender );

			// Turn the packet into a UdpHandshakeRequest so we can work with it
			UdpJoinRequest request = new UdpJoinRequest();
			request.fromDatagram( packet );
			
			// Check to see if they can connect
			String siteName = request.getSiteName();
			UdpResponse response = new UdpResponse();
			if( reflector.getDistributor().containsLinkWithName(siteName) )
			{
				response.setResponseCode( UdpResponse.RESPONSE_NAME_TAKEN );
				logger.info( "Connection refused: Site name is taken (%s)", siteName );
			}
			else
			{
				response.setResponseCode( UdpResponse.RESPONSE_OK );
				pendingConnections.add( sender );
			}

			sendResponse( response, sender );
		}
		
		private void processConfigUpload( DatagramPacket packet )
		{
			SocketAddress sender = packet.getSocketAddress();
			logger.debug( "Configuration upload request from "+sender );

			// Check to see if we have a pending connection for them
			if( pendingConnections.contains(sender) == false )
			{
				UdpResponse response = new UdpResponse();
				response.setResponseCode( UdpResponse.RESPONSE_SITE_UNKNOWN );
				sendResponse( response, sender );
			}

			// Turn the packet into something we can work with
			UdpSendConfiguration request = new UdpSendConfiguration();
			request.fromDatagram( packet );
			
			// remove it, we will now process
			pendingConnections.remove( sender );
				
			// TODO Start HERE do something with the configuration
			logger.error( "HERE, wot now? %d bytes", packet.getLength() );

		}
		
		private void sendResponse( UdpResponse response, SocketAddress destination )
		{
			// Send the response message
			DatagramPacket outgoing = response.toDatagram();
			outgoing.setSocketAddress( destination );
			try
			{
				socket.send( outgoing );
			}
			catch( IOException ioex )
			{
				logger.error( "Failed to send control response message to %s: %s",
				              destination, ioex.getMessage() );
				logger.error( ioex );
			}

		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Connection Establisher  /////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Class responsible for handling the handshake with newly connecting sites */
	private class ConnectionEstablisher extends Thread
	{
		private SocketAddress sender;
		private UdpJoinRequest handshakeRequest;

		private ConnectionEstablisher( DatagramPacket request )
		{
			super( "Connection-Establisher" );
			super.setDaemon( true );
			this.sender = request.getSocketAddress();
			
			this.handshakeRequest = new UdpJoinRequest();
			this.handshakeRequest.fromDatagram( request );
		}
		
		@Override
		public void run()
		{
			logger.debug( "Connection request from "+sender );

			//
			// 1. Check that the site name requested is OK and communicate result
			//
			String siteName = handshakeRequest.getSiteName();
			if( reflector.getDistributor().containsLinkWithName(siteName) )
			{
				UdpResponse response = new UdpResponse();
				response.setResponseCode( UdpResponse.RESPONSE_NAME_TAKEN );
				logger.info( "Connection refused: Site name is taken (%s)", siteName );
				sendDatagram( response );
			}
			else
			{
				UdpResponse response = new UdpResponse();
				response.setResponseCode( UdpResponse.RESPONSE_OK );
				sendDatagram( response );
			}
			
			//
			// 2. Get the configuration from the remote site
			//
			LinkConfiguration remoteConfiguration = null;
			// TODO HERE Where is the next message coming in from? It's on the main socket,
			//      how do I get it over here?
			
			// TODO Could set up the UdpLink at this point (for writing only) and register it
			

		}
		
		private void sendDatagram( UdpMessage message )
		{
			try
			{
				DatagramPacket datagram = message.toDatagram();
				datagram.setSocketAddress( sender );
				socket.send( datagram );
				logger.info( "Sent to %s", sender );
			}
			catch( IOException ioex )
			{
				logger.error( "Failed to send control response message to %s: %s",
				              sender, ioex.getMessage() );
				logger.error( ioex );
			}
		}
	}

}
