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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.wan.Bundler;
import org.openlvc.distributor.links.wan.udp.msg.MessageType;
import org.openlvc.distributor.links.wan.udp.msg.UdpGoodbye;
import org.openlvc.distributor.links.wan.udp.msg.UdpJoin;
import org.openlvc.distributor.links.wan.udp.msg.UdpKeepAlive;
import org.openlvc.distributor.links.wan.udp.msg.UdpMessage;
import org.openlvc.distributor.links.wan.udp.msg.UdpResponse;

public class UdpWanLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long KEEP_ALIVE_TIMEOUT = 20000;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;
	
	// UDP Socket Management
	private DatagramSocket socket;
	private DatagramPacket incoming;  // configure to have designated size
	private DatagramPacket outgoing;  // configure to have designated address
	private SocketAddress  target;    // points to who we are sending to
	private PacketReceiver receiver;  // thread to run receiveNext() in
	private PaceMaker      pacemaker; // sends KeepAlive messages periodically
	
	private AtomicLong lastSent;
	private AtomicLong lastReceived;  
	
	private ConcurrentMap<Integer,UdpResponse> responseMap;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpWanLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		this.reflector     = null;   // set in setReflector() prior to call to up()
		
		// UDP Socket Management
		this.socket        = null;   // set in connectSocket()
		this.incoming      = null;   // set in connectSocket()
		this.outgoing      = null;   // set in connectSocket()
		this.target        = null;   // set in connectSocket()
		this.receiver      = null;   // set in connectSocket() 
		this.pacemaker     = null;   // set in connectSocket()
		
		this.lastSent      = new AtomicLong(0);
		this.lastReceived  = new AtomicLong(0);
		this.responseMap   = new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods: Bring Connection Up   ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void up()
	{
		// Remember - Keep-Alive
		// Remember - First-Message Block
		
		if( isUp() )
			return;
		
		//
		// 1. Make sure we have everything we need
		//
		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: WAN-UDP" );

		try
		{
			//
			// 2. Establish a connection to the target address.
			//
			connectSocket(); // will start the receiver thread

			//
			// 3. Join to the relay, which also establishes the backward connection
			//
			if( this.isTransient == false )
				joinRelay();
			
			//
			// 4. Start the PaceMaker
			//
			// We're all up, so start the pace maker to ensure we keep our place in the NAT table
			this.pacemaker = new PaceMaker( super.getName() );
			this.pacemaker.start();
		}
		catch( IOException ioex )
		{
			// reset us to start state
			logger.debug( "Error bringing UDP link up [openSocket()]: "+ioex.getMessage(), ioex );
			this.socket = null;
			
			// reconnect if we are on the client size 
			if( linkConfiguration.isWanAutoReconnect() )
				scheduleReconnect();
			
			return;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		// 4. All done!
		logger.debug( "Link is up!" );
		super.linkUp = true;
	}

	/**
	 * Establish a connection to the remote socket as listed in the configuration.
	 * In the process we send a test message ({@link UdpKeepAlive}) down the pipe.
	 */
	private void connectSocket() throws IOException
	{
		// 1. Get our configuration information into a usable form
		InetAddress localAddr    = NetworkUtils.resolveInetAddress( linkConfiguration.getWanNic() );
		InetAddress targetAddr   = NetworkUtils.resolveInetAddress( linkConfiguration.getWanAddress() );
		InetSocketAddress local  = new InetSocketAddress( localAddr, linkConfiguration.getWanSendPort() );
		InetSocketAddress target = new InetSocketAddress( targetAddr, linkConfiguration.getWanPort() );

		// 2. Create and configure the socket
		//    The socket as not bound initially. We'll do some settings and then
		//    bind it to a local ip:port combo to listen on
		try
		{
			logger.trace( "Opening datagram socket and binding to "+local );
			this.socket = new DatagramSocket( null );
			this.socket.setTrafficClass( 0x10 ); // Low delay
			this.socket.setReuseAddress( true );
			this.socket.bind( local );
			
			local = new InetSocketAddress( socket.getLocalAddress(), socket.getLocalPort() );
			logger.trace( "Socket bound, local address %s", local );

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
			logger.debug( "Connecting socket to remote relay station "+target );
//TODO		this.socket.connect( target );
		}
		catch( IOException ioex )
		{
			// clean up before we get out of here
			this.socket = null;
			throw ioex;
		}

		//
		// 3. Connected; set up our send/receive caches
		//
		this.target = target;
		final int maxLength = UdpMessage.MAX_PACKET_SIZE;
		this.incoming = new DatagramPacket( new byte[maxLength], maxLength );
		this.outgoing = new DatagramPacket( new byte[maxLength], maxLength, target );
		this.responseMap.clear();

		//
		// 4. Start message receiver thread
		//
		this.receiver = new PacketReceiver( linkConfiguration.getName() );
		this.receiver.start();
		
		//
		// 5. Make sure we can communicate with the remote site
		//    The receiver thread should be up and running, let's try a KeepAlive volley
		//
		try
		{
			// make sure we can send
			logger.debug( "Sending test message to connection with "+target );
			UdpKeepAlive request = new UdpKeepAlive( true );
			UdpResponse response = sendAndWaitForResponse( request, 3000 );
			if( response == null )
				throw new IOException( "Timed out waiting for response (msgId: "+request.getMessageId()+")" );

			logger.debug( "Response received; moving on" );
		}
		catch( IOException ioex )
		{
			// clean up before we dive out
			this.socket   = null;
			this.incoming = null;
			this.outgoing = null;
			this.target   = null;
			
			// make double sure to kill the packet receiver
			if( receiver.isAlive() )
			{
				receiver.interrupt();
				ThreadUtils.exceptionlessThreadJoin( receiver, 1000 );
			}
			
			// throw the original exception now that we're clean
			throw ioex;
		}
	}

	/**
	 * Establish a connection with the relay by sending them a JOIN request that contains
	 * our configuration information and having them generate a connection back to us on
	 * our outgoing send port (which should hopefully get through any NAT in place).
	 * 
	 * @throws DiscoException If there is a problem with the request, like the site name being in use
	 * @throws IOException Problem sending the message out
	 */
	private void joinRelay() throws DiscoException, IOException
	{
		String siteName = linkConfiguration.getWanSiteName();
		logger.debug( "Requesting join to relay (%s) with name (%s)", target, siteName );
		
		UdpJoin request = new UdpJoin( linkConfiguration );
		UdpResponse response = sendAndWaitForResponse( request, 3000 );
		
		// what did we get?
		if( response == null )
			throw new DiscoException( "Failed to connect: no response to join request (%s)", target );
		else if( response.isError() )
			throw new DiscoException( response.getValueAsString() );
		else
			logger.debug( "Join successful. Connected to relay using name %s", linkConfiguration.getWanSiteName() );

	}

	///////////////////////////////////////////////////////////////
	/// Lifecycle Methods: Take Connection Down   /////////////////
	///////////////////////////////////////////////////////////////
	@Override
	public void down()
	{
		if( isDown() )
			return;

		logger.debug( "Taking down link: "+super.getName() );

		// 1. Send a Goodbye message, which will kill the other end of the connection
		// Send the disconnection notice
		if( this.socket.isConnected() )
			doGoodbye();

		// 2. Disconnect the socket
		this.socket.close();
		
		// 3. Stop the packet receiver thread
		this.receiver.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.receiver, 1000 );
		
		// 4. Stop the pace maker
		this.pacemaker.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.pacemaker, 1000 );
		
		// 4. Zero-out local variables and set us back to start-state
		this.socket   = null;
		this.incoming = null;
		this.outgoing = null;
		this.target   = null;
		this.receiver = null;

		// 5. Mark the connection as down
		super.linkUp = false;
		logger.info( "Connection is down" );
	}
	
	private void doGoodbye()
	{
		try
		{
    		UdpGoodbye request = new UdpGoodbye();
    		UdpResponse response = sendAndWaitForResponse( request, 500 );
    		if( response != null && response.isOk() )
    			logger.debug( "Relay has acknowledged disconnection" );
		}
		catch( Exception e )
		{
			// catch everything; don't want something unexpected to prevent further clean shutdown
			logger.warn( "Exception while sending Goodbye; log and continue", e );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Connection Management Methods   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Puts a task to try and call up() again on a timer. Only use when a connection fails to
	 * establish and if this is a transient/persistent connection.
	 */
	private void scheduleReconnect()
	{
		if( this.isTransient == false )
			return;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Sending Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Submit the message to be sent down the point-to-point connection to the relay so that it
	 * can be re-sent to other connections from there. We submit everything to the {@link Bundler}
	 * for processing. If enabled, it will batch messages, otherwise it will send immediately.
	 */
	@Override
	public void reflect( Message message )
	{
		
	}

	/**
	 * Send the given message to the endpoint we are connected to.
	 */
	private void sendMessage( UdpMessage message ) throws IOException
	{
		// Serialize the message into our outgoing packet template
		message.toDatagram( this.outgoing );
		this.socket.send( this.outgoing );
		
		this.lastSent.set( System.currentTimeMillis() );
	}

	/**
	 * Send the given message to the target device that we are connected to and then
	 * wait for a response. UDP can and will fail, so we will attempt this process
	 * three times. The timeout value is the amount of time we should wait for a response
	 * for before we send another request.
	 * 
	 * @param message The message to send
	 * @param timeout The amount of time to wait for a response (note: we send/wait up to 3 times)
	 * @return The next response message that we receive, or null if none is received
	 */
	private UdpResponse sendAndWaitForResponse( UdpMessage message, long timeout )
		throws IOException
	{
		message.setRandomMessageId();
		int messageId = message.getMessageId();
		
		int retry = 3;
		while( retry > 0 )
		{
			sendMessage( message );
			UdpResponse response = waitForNextResponse( messageId, timeout );
			if( response != null )
				return response;
			else
				--retry;
		}
		
		return null;
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
			this.lastSent.set( System.currentTimeMillis() );
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
	private UdpResponse waitForNextResponse( int messageId, long timeout )
	{
		long deadline = System.currentTimeMillis() + timeout;
		while( Thread.interrupted() == false && deadline > System.currentTimeMillis() )
		{
			try
			{
				synchronized( responseMap )
				{
					responseMap.wait( deadline-System.currentTimeMillis() );
				}

				if( responseMap.containsKey(messageId) )
					return responseMap.remove( messageId );
			}
			catch( InterruptedException ie )
			{ /* ignore */ }
		}

		// never found a response
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Receipt Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void receiveNext() throws SocketException, IOException
	{
		//
		// 1. Grab the next packet
		//
		this.socket.receive( this.incoming );

		byte[] data = incoming.getData();
		int length = incoming.getLength();
		logger.trace( "Received message from %s (size=%d)", incoming.getSocketAddress(), length );
		this.lastReceived.set( System.currentTimeMillis() );
		
		//
		// 2. Verify some basics to make sure it is valid
		//
		if( length < UdpMessage.MIN_PACKET_SIZE )
		{
			logger.warn( "Discarding undersized packet (min: %d, received: %d)",
			             UdpMessage.MIN_PACKET_SIZE, length );
			return;
		}
		
		//
		// 3. Turn it into something we can work with (which will verify its structure)
		//    and then try to process it
		//
		UdpMessage message = UdpMessage.fromByteArray( data, 0 );
		switch( message.getType() )
		{
			case KeepAlive:
				processKeepAlive( (UdpKeepAlive)message, this.incoming.getSocketAddress() );
				break;
			case OkResponse:
			case ErrorResponse:
				processResponse( (UdpResponse)message, this.incoming.getSocketAddress() );
				break;
			default:
				logger.error( "Recieved unknown message of type: "+message.getType() );
		}

	}

	private void processKeepAlive( UdpKeepAlive message, SocketAddress sender )
	{
		logger.debug( "(Keep Alive) received from %s (ack requested=%s)", sender, message.isAckRequested() );

		if( message.isAckRequested() == false )
			return;
		
		UdpResponse response = new UdpResponse( message.getMessageId() );
		response.setAsOk();
		exceptionlessSend( response, sender );
	}

	private void processResponse( UdpResponse response, SocketAddress sender )
	{
		logger.debug( "(%s) received from %s", response.getType(), sender );

		responseMap.put( response.getMessageId(), response );
		synchronized( responseMap )
		{
			responseMap.notifyAll();
		}
		
//		if( responseQueue.offer(response) == false )
//			logger.warn( "Response message discarded; no room on response queue ("+response+")" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Set the location that we should submit received packets to
	 */
	@Override
	public void setReflector( Reflector reflector )
	{
		// store to hand off to incoming connections
		this.reflector = reflector;
	}

	@Override
	public String getStatusSummary()
	{
		return "TBA";
	}

	@Override
	public String getConfigSummary()
	{
		return "TBA";
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: PacketReceiver   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This class runs a separate thread that essentially just bounces the requests back to the
	 * {@link UdpConnection#receiveNext()} method so that we're never blocking anyone.
	 */
	private class PacketReceiver extends Thread
	{
		private PacketReceiver( String parentName )
		{
			super( parentName+"-Recv" );
		}
		
		public void run()
		{
			try
			{
				// Process requests from the client
				while( Thread.interrupted() == false )
					receiveNext(); // implemented in main class
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
// TODO
//				takeDownAndRemove();
//				scheduleReconnect();
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: PaceMaker   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class PaceMaker extends Thread
	{
		private PaceMaker( String parentName )
		{
			super( parentName+"-PaceMaker" );
		}
		
		public void run()
		{
			while( Thread.interrupted() == false )
			{
				// Sleep for a little bit
				ThreadUtils.exceptionlessSleep( 900 );
				long currentTime = System.currentTimeMillis();
				
				// Check to see if we haven't received a KeepAlive from the other size in too long
				// We add 1000ms to give a little bit of wiggle room
				if( currentTime > (lastReceived.get()+KEEP_ALIVE_TIMEOUT+1000) )
				{
					System.out.println( "Other end is dead "+lastReceived.get() );
				}

				// Check to see if it's time for us to send a KeepAlive
				if( currentTime > (lastSent.get()+KEEP_ALIVE_TIMEOUT) )
				{
    				// it's been too long between drinks, send a keep alive packet
					UdpKeepAlive keepAlive = new UdpKeepAlive(true);
					exceptionlessSend( keepAlive, target );
				}
			}
		}
	}

}
