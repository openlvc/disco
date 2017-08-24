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
package org.openlvc.distributor.links.relay;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.TransportType;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.wan.TcpWanLink;

/**
 * The {@link RelayLink} accepts incoming connections from remote sites and creates
 * new {@link TcpWanLink} connections for them. This binds together the local side
 * of the site with the remote end that initiated the connection.
 */
public class RelayLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;
	private ServerSocket serverSocket;
	private ConnectionAcceptor connectionAcceptor;
	
	private int linkCounter; // how many links have we created

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RelayLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		this.reflector = null;          // set in setReflector()
		this.serverSocket = null;       // set in up()
		this.connectionAcceptor = null; // set up up()
		this.linkCounter = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		if( isUp() )
			return;

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: Relay" );

		// 1. Make sure we have everything we need
		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );

		
		// 2. Resolve the address, it may be one of the symbolic values
		InetAddress serverAddress = NetworkUtils.resolveInetAddress( linkConfiguration.getRelayAddress() );
		InetSocketAddress socketAddress = new InetSocketAddress( serverAddress,
		                                                         linkConfiguration.getRelayPort() );
		
		// 3. Create the server socket
		try
		{
			this.serverSocket = new ServerSocket();
			this.serverSocket.bind( socketAddress );
		}
		catch( IOException io )
		{
			this.serverSocket = null;
			throw new DiscoException( "Unable to bind server socket: "+io.getMessage() );
		}

		// 4. Start the connection acceptor
		this.connectionAcceptor = new ConnectionAcceptor();
		this.connectionAcceptor.start();
		
		super.linkUp = true;
	}
	
	public void down()
	{
		if( isDown() )
			return;
		
		logger.debug( "Taking down link: "+super.getName() );

		// 1. Close the server socket off
		try
		{
			this.serverSocket.close();
		}
		catch( IOException ioex )
		{
			// disregard and move on
		}
		finally
		{
			this.serverSocket = null;
		}

		// 2. Shut down the acceptor thread to cease incoming connections
		this.connectionAcceptor.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.connectionAcceptor );
		this.connectionAcceptor = null;
		
		super.linkUp = false;
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
		// if link has never been up, return configuration information
		if( isUp() )
		{
			// return live, resolved connection information
			return String.format( "{ RELAY, address:%s, port:%d, transport:%s }",
			                      serverSocket.getInetAddress().getHostAddress(),
			                      serverSocket.getLocalPort(),
			                      linkConfiguration.getRelayTransport() );
		}
		else
		{
			// return raw configuration data
			return String.format( "{ RELAY, address:%s, port:%d, transport:%s }",
			                      linkConfiguration.getRelayAddress(),
			                      linkConfiguration.getRelayPort(),
			                      linkConfiguration.getRelayTransport() );
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		// no-op
	}
	
	public void setReflector( Reflector reflector )
	{
		// store to hand off to incoming connections
		this.reflector = reflector;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner-Class: ConnectionAcceptor  ////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Class responsible for receiving messages from the remote host represented by this instance */
	/**
	 * This class is responsible for listening for new connections and then kicking off
	 * the establishment of those connections on a separate thread. This is done to ensure
	 * that any issues creating a new connection don't prevent others from being started.
	 */
	private class ConnectionAcceptor extends Thread
	{
		private ConnectionAcceptor()
		{
			super( linkConfiguration.getName() );
		}
		
		public void run()
		{
			while( !Thread.interrupted() )
			{
				try
				{
					Socket socket = serverSocket.accept();
					String name = "Establisher-"+socket.getInetAddress().getHostAddress();
					Thread establisher = new Thread( new ConnectionEstablisher(socket), name );
					establisher.setDaemon( true );
					establisher.start();
				}
				catch( SocketException se )
				{
					// the other end hung up :( bai bai
					return;
				}
    			catch( IOException ioe )
    			{
    				// time to go!
    				continue;
    			}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner-Class: ConnectionEstablisher  /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This class does the work of establishing a connection after the socket creation has
	 * been initiated. It will perform the handshake operation with the remote end on a
	 * separate thread so that issues establishing one connection don't affect others.
	 */
	private class ConnectionEstablisher implements Runnable
	{
		private Socket socket;
		public ConnectionEstablisher( Socket socket )
		{
			this.socket = socket;
		}
		
		public void run()
		{
			String ip = socket.getInetAddress().getHostAddress();
			logger.debug( "Incoming connection from "+ip );
			LinkConfiguration remoteConfiguration = null;

			try
			{ 				
				// Complete handshake with the new connection
				remoteConfiguration = handshake( socket );
			}
			catch( DiscoException de )
			{
				// handshake failed
				logger.debug( "Connection rejected. Handshake failed: "+de.getMessage() );
				try { socket.close(); } catch( Exception e ) {}
				return;
			}
			
			//
			// Create the LinkConfiguration for the new WAN connection we'll be creating
			//
			String linkName = remoteConfiguration.getWanSiteName();
			if( linkName.equalsIgnoreCase("<auto>") )
				linkName = "wan"+(++linkCounter);
			LinkConfiguration localConfiguration = new LinkConfiguration( linkName );
			localConfiguration.setWanAddress( ip );
			localConfiguration.setWanPort( socket.getPort() );
			localConfiguration.setWanTransport( TransportType.TCP );
			
			// copy the relevant settings from the remote to the local configuration
			copyConnectionSettings( localConfiguration, remoteConfiguration );
			
			//
			// Bring a link online for the new connection
			//
			logger.debug( "Creating TcpWanLink for connection (%s)", ip );
			TcpWanLink link = new TcpWanLink( localConfiguration );
			link.setTransient( true );
			link.setSocket( socket );
			reflector.getDistributor().addAndBringUp( link );
		}
		
		/**
		 * When a new connection is made, we wait for it to send us its configuration so that
		 * we can ensure the local link is set up the same way with regard to the key properties
		 * required to send it traffic. Not all properties are copied from the link configuration,
		 * only those necessary to replicate for traffic that is egressed to it. These include:
		 * 
		 * <ul>
		 *   <li>Connection Name</li>
		 *   <li>Bundling settings</li>
		 *   <li>Filtering (we flip the sender side filters)</li>
		 * </ul>
		 * 
		 * @return If the handshake is successful, return the {@link LinkConfiguration}
		 * @throws DiscoException If there was a problem with the handshake, such as the name of
		 *         the link already being in use, or an error while reading the remote config.
		 */
		private LinkConfiguration handshake( Socket socket )
		{
			try
			{
				// Step 0. Set up read/write pipelines for handshake
				DataInputStream in = new DataInputStream( socket.getInputStream() );
				DataOutputStream out = new DataOutputStream( socket.getOutputStream() );

				// Step 1. Read the link's site name
				//         The link will send us their site name first. We will use this for the link name.
				//         If the name is already in use, send an error code back. Otherwise, send an int
				//         that is the same as the length of the string they sent us.
				String name = in.readUTF();
				if( reflector.getDistributor().containsLinkWithName(name) )
				{
					// link name is in use
					out.writeInt( -1 );
					throw new DiscoException( "Name in use ("+name+")" );
				}
				else
				{
					// send confirmation that it was received
					out.writeInt( name.hashCode() );
				}


				// Step 2. Read the remote link's configuration
				//         We need their configuration information so we can steal some of the
				//         connection properties and duplicate them for the use of the return link.
				try
				{
					int length = in.readInt();
					byte[] bytes = new byte[length];
					in.read( bytes );

					ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(bytes) );
					LinkConfiguration remoteConfiguration = (LinkConfiguration)ois.readObject();
					out.writeInt( 1 );
					return remoteConfiguration;
				}
				catch( Exception e )
				{
					out.writeInt( -1 );
					throw e;
				}
			}
			catch( DiscoException de )
			{
				throw de;
			}
			catch( Exception e )
			{
				throw new DiscoException( "Exception during handshake - "+e.getMessage(), e );
			}
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
			
			// Filtering
			// Set remote's ingress as our egress to prevent unnecessary sending.
			// No need to set receive filtering, as it will have been applied before it gets to us
			local.setSendFilter( remote.getReceiveFilter() );
		}
	}

}
