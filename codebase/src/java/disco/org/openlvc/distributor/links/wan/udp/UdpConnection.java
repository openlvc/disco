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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.wan.udp.msg.MessageType;
import org.openlvc.distributor.links.wan.udp.msg.UdpBundle;
import org.openlvc.distributor.links.wan.udp.msg.UdpConfigure;
import org.openlvc.distributor.links.wan.udp.msg.UdpGoodbye;
import org.openlvc.distributor.links.wan.udp.msg.UdpJoin;
import org.openlvc.distributor.links.wan.udp.msg.UdpKeepAlive;
import org.openlvc.distributor.links.wan.udp.msg.UdpMessage;
import org.openlvc.distributor.links.wan.udp.msg.UdpResponse;

/**
 * 
 * 
 * Things left TODO:
 *   - KeepAlive
 *   - AutoDisconnect (quiet period)
 */
public class UdpConnection
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private LinkConfiguration linkConfiguration;
	private Logger logger;

	private boolean connectionUp;
	private Receiver receiver;
	
	// UDP Infrastructure
	private DatagramSocket socket;
	private DatagramPacket incoming;
	private DatagramPacket outgoing;
	private InetSocketAddress relayAddress; // cache of location to send message to
	
	private BlockingQueue<UdpResponse> responseQueue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpConnection( LinkConfiguration linkConfiguration, Logger logger )
	{
		this.linkConfiguration = linkConfiguration;
		this.logger = logger;

		this.connectionUp = false;    // set in up()
		this.receiver = null;         // set in up()
		
		// UDP Infrastructure
		this.socket = null;           // set in openSocket()
		this.incoming = null;         // set in openSocket()
		this.outgoing = null;         // set in openSocket()
		this.relayAddress = null;     // set in openSocket()
		
		this.responseQueue = new LinkedBlockingQueue<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// (Lifecycle) Open Connection   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		if( connectionUp )
			return;
		
		logger.debug( "Opening UDP WAN connection to "+linkConfiguration.getWanAddress() );
		
		// Open the socket
		openSocket();

		// Start a receiver processing thread so we can take incoming messages
		this.receiver = new Receiver( linkConfiguration.getName() );
		this.receiver.start();
		
		// Complete the formal handshake
		try
		{
			doJoin();
			doConfigure();
		}
		catch( Exception e )
		{
			logger.error( "Connection failed: "+e.getMessage(), e );
			this.connectionUp = true; // have to trick down() into running
			down();
			throw new DiscoException( "Connection failed" );
		}

		// We're open for business
		this.connectionUp = true;
	}

	/**
	 * Create the sockets and open a connection. The UDP socket will be unicast and bound to
	 * the server IP. Sends a test message to ensure everything is up and open
	 */
	private void openSocket()
	{
		// 1. Get our configuration information into a usable form
		InetAddress localAddr = NetworkUtils.resolveInetAddress( linkConfiguration.getWanNic() );
		InetAddress relayAddr = NetworkUtils.resolveInetAddress( linkConfiguration.getWanAddress() );
		InetSocketAddress local = new InetSocketAddress( localAddr, 0 /*choose ephemeral port*/ );
		InetSocketAddress relay = new InetSocketAddress( relayAddr, linkConfiguration.getWanPort() );

		// 2. Open up the socket to communicate on and connect it to the remote relay station
		try
		{
			// Create the socket as not bound initially. We'll do some settings and then
			// bind it to a local ip:port combo to listen on
			logger.debug( "Opening datagram socket and binding to "+local );
			this.socket = new DatagramSocket( null );
			this.socket.setTrafficClass( 0x10 ); // Low delay
			this.socket.bind( local );
			
			// Connect it to the remote site
			// This will mean we can only address messages to and receive messages from
			// this particular ip:port combo on this socket.
			//
			// NOTE: You may see BindExceptions with the following message if you attempt
			//       to (or accidentally) specify an IP for a relay that cannot be reached
			//       from the NIC you also specify. For example, if you specify LOOPBACK for
			//       the ip and SITE_LOCAL for the nic:
			//
			//       Message >> Cannot assign requested address: Datagram send failed
			//
			logger.debug( "Connecting socket to remote relay station "+relay );
			this.socket.connect( relay );

			// Connected, store the relay address so we can use it when sending packets
			this.relayAddress = relay;
			final int maxLength = UdpMessage.MAX_PACKET_SIZE;
			this.incoming = new DatagramPacket( new byte[maxLength], maxLength );
			this.outgoing = new DatagramPacket( new byte[maxLength], maxLength, relay );
			
			// Send test message to remote site
			// Make sure we can comms on this channel. Just because it bound and connected
			// without any error doesn't mean that it is usable
			logger.debug( "Sending test message to remote relay "+relay );
			send( new UdpKeepAlive() );
			receiveNext(); // exception only thrown when we try to receive, so try

			logger.debug( "UDP socket is open: "+socket.getLocalSocketAddress()+" >> "+relay );
		}
		catch( IOException ioex )
		{
			this.socket = null;
			this.relayAddress = null;
			this.incoming = null;
			this.outgoing = null;
			
			// if auto reconnecting, schedule a reconnect
//TODO
//			if( linkConfiguration.isWanAutoReconnect() )
//				scheduleReconnect();
			
			throw new DiscoException( "Error bringing UDP link up [openSocket()]: "+ioex.getMessage(), ioex );
		}		
	}

	/**
	 * Sends a join request to the relay complete with the name of the connection that we
	 * want to reserve. If the name is not available, the connection must come down and an
	 * exception is thrown.
	 * 
	 * @throws IOException If there is an IO problem when sending the packet
	 * @throws DiscoException If the connection name is not available or we don't get a response
	 */
	private void doJoin() throws IOException, DiscoException
	{
		String siteName = linkConfiguration.getWanSiteName();
		logger.debug( "Requesting join to relay (%s) with name (%s)", relayAddress, siteName );
		
		UdpJoin request = new UdpJoin( linkConfiguration );
		UdpResponse response = sendAndWaitForResponse( request, 3000 );
		
		// what did we get?
		if( response == null )
			throw new DiscoException( "Failed to connect: no response to join request (%s)", relayAddress );
		else if( response.isError() )
			throw new DiscoException( response.getValueAsString() );
		else
			logger.debug( "Join successful. Connected to relay using name %s", linkConfiguration.getWanSiteName() );
	}

	/**
	 * Send our configuration information to the other end of the pipe so that filters
	 * and similar settings can be applied on the sender side rather.
	 * 
	 * @throws IOException If there is an IO problem while sending the message
	 * @throws DiscoException If we get an error response (or no response at all).
	 */
	private void doConfigure() throws IOException, DiscoException
	{
		logger.debug( "Sending configuration information to the relay" );
		
		UdpConfigure request = new UdpConfigure( linkConfiguration );
		UdpResponse response = sendAndWaitForResponse( request, 3000 );

		if( response == null )
			throw new DiscoException( "Failed to configure: remote relay never replied (%s)", relayAddress );
		else if( response.isError() )
			throw new DiscoException( response.getValueAsString() );
		else
			logger.debug( "Remote configuration information accepted by relay, handshake complete" );
	}

	/**
	 * Helper method that will send the given request and wait for the next response with the
	 * given timeout (in millis). This method will retry this a maximum of three times to ensure
	 * it is not just a bad connection. If a response is found, it is returned, otherwise null.
	 * 
	 * @param request The request to send
	 * @param timeoutPerRetry The timeout to wait for a response (on each retry)
	 * @return Either the {@link UdpResponse} object or <code>null</code>.
	 */
	private UdpResponse sendAndWaitForResponse( UdpMessage request, long timeoutPerRetry )
		throws IOException
	{
		int retry = 3;
		while( retry > 0 )
		{
			send( request );
			UdpResponse response = waitForNextResponse( timeoutPerRetry );
			if( response != null )
				return response;
			else
				--retry;
		}
		
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// (Lifecycle) Close Connection   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void down()
	{
		if( connectionUp == false )
			return;
		
		logger.debug( "Taking down UDP WAN link: "+linkConfiguration.getName() );

		// Send the disconnection notice
		if( this.socket.isConnected() )
			sendGoodbye();

		// Close the socket
		closeSocket();

		// Take down the packet receiver
		ThreadUtils.exceptionlessThreadJoin( receiver, 1000 );
		this.receiver = null;

		this.connectionUp = false;
		logger.info( "Connection is down" );
	}

	private void sendGoodbye()
	{
		try
		{
			UdpGoodbye request = new UdpGoodbye();
			UdpResponse response = sendAndWaitForResponse( request, 500 );
			if( response != null && response.isOk() )
				logger.debug( "Relay has acknowledged disconnection" );
		}
		catch( IOException ioex )
		{
			logger.warn( "IOException while sending Goodbye; log and continue", ioex );
		}
	}
	
	private void closeSocket()
	{
		try
		{
			this.socket.close();
			this.incoming = null;
		}
		finally
		{
			this.socket = null;
			this.relayAddress = null;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Procressing Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void send( UdpMessage message ) throws IOException
	{
		// Serialize the message into our outgoing packet template
		message.toDatagram( this.outgoing );
		this.socket.send( this.outgoing );
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

	/**
	 * Wait for up to the given number of millis for a response message (one that is of type
	 * {@link MessageType#OkResponse} or {@link MessageType#ErrorResponse}. Returns null if
	 * we waited too long.
	 */
	private UdpResponse waitForNextResponse( long timeout )
	{
		try
		{
			return responseQueue.poll( timeout, TimeUnit.MILLISECONDS );
		}
		catch( InterruptedException ie )
		{
			// The world is ending; just let it go
			return null;
		}
	}

	/**
	 * Listen for the next available package on the socket and process it.
	 * Processing for all control messages happens directly in this call
	 * (blocking the receipt of more messages). Message bundles that contain the
	 * PDUs for forwarding are handed off to the {@link Bundler} to deal with.
	 * 
	 * @throws IOException If there is a problem reading from the socket
	 */
	private void receiveNext() throws IOException
	{
		// grab the next packet
		this.socket.receive( this.incoming );

		byte[] data = incoming.getData();
		int length = incoming.getLength();
		logger.trace( "Received message from %s (size=%d)", incoming.getSocketAddress(), length );
		
		// Make sure there is enough data
		if( length < UdpMessage.MIN_PACKET_SIZE )
		{
			logger.warn( "Discarding undersized packet (min: %d, received: %d)",
			             UdpMessage.MIN_PACKET_SIZE, length );
			return;
		}

		//
		// Check the magic number and turn the message into something we can use
		//
		UdpMessage message = UdpMessage.fromByteArray( data, 0 );
		switch( message.getType() )
		{
			case Bundle:
				processBundle( (UdpBundle)message );
				return;
			case OkResponse:
			case ErrorResponse:
				processResponse( (UdpResponse)message );
				break;
			case Join:
			case Configure:
			case Goodbye:
				// ignore all these for now - they're server side messages
				break;
			case KeepAlive:
				processKeepAlive( (UdpKeepAlive)message, this.incoming );
				break;
			default:
				logger.error( "Recieved unknown message of type: "+message.getType() );
		}
	}
	
	//
	// TODO - Should I pull these out into a separate interface so that the infrastructure
	//        can be used on server and client?
	//
	
	private void processBundle( UdpBundle bundle )
	{
		
	}
	
	private void processResponse( UdpResponse response )
	{
		if( responseQueue.offer(response) == false )
			logger.warn( "Response message discarded; no room on response queue ("+response+")" );
	}

	/** Turn this thing around and send a keep-alive right back */
	private void processKeepAlive( UdpKeepAlive message, DatagramPacket received )
	{
		logger.debug( "(Keep Alive) received from %s (request=%s)", received.getSocketAddress(), message.isRequest() );

		if( message.isResponse() )
			return;
		
		UdpKeepAlive response = new UdpKeepAlive();
		response.setRequest( false );
		exceptionlessSend( response, received.getSocketAddress() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public InetAddress getRelayAddress()
	{
		return socket == null ? null : socket.getInetAddress();
	}
	
	public int getRelayPort()
	{
		return socket == null ? 0 : socket.getPort();
	}
	
	public int getLocalPort()
	{
		return socket == null ? 0 : socket.getLocalPort();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Receiver   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This class runs a separate thread that essentially just bounces the requests back to the
	 * {@link UdpConnection#receiveNext()} method so that we're never blocking anyone.
	 */
	private class Receiver extends Thread
	{
		private Receiver( String parentName )
		{
			super( parentName+"-Recv" );
		}
		
		public void run()
		{
			// Prepare to accept incoming messages
			responseQueue.clear();
			
			try
			{
				// Process requests from the client
				while( Thread.interrupted() == false )
					receiveNext(); // back in main class
			}
			catch( SocketException se )
			{
				// we are shutting down - someone has closed the socket intentionally on us
				logger.debug( "Socket closed, shutting down" );
			}
			catch( IOException ioex )
			{
				// failed on the socket.receive() call - that's pretty bad, try again later
				logger.info( "Error receiving message on UDP socket, disconnecting", ioex );
//				takeDownAndRemove();
//				scheduleReconnect();
			}
		}
	}
}
