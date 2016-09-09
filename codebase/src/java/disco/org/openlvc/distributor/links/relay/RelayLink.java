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

import java.io.IOException;
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
	/// Receive Processing  /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Class responsible for receiving messages from the remote host represented by this instance */
	private class ConnectionAcceptor extends Thread
	{
		public void run()
		{
			while( !Thread.interrupted() )
			{
				//
				// Wait for the next socket connection
				//
				Socket socket = null;
				String ip = "unknown";
				try
				{ 				
	                // Process new connection requests from clients
					socket = serverSocket.accept();
					ip = socket.getInetAddress().getHostAddress();
					logger.debug( "Incoming connection from "+ip );
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
				
				//
				// Bring a link online for the new connection
				//
				// Create the configuration details for the link based on the incoming socket
				LinkConfiguration config = new LinkConfiguration( ip );
				config.setName( "wan"+(++linkCounter) );
				config.setWanAddress( ip );
				config.setWanPort( socket.getPort() );
				config.setWanTransport( TransportType.TCP );

				// Create the link and push the socket in
				logger.debug( "Creating TcpWanLink for connection (%s)", ip );
				TcpWanLink link = new TcpWanLink( config );
				link.setTransient( true );
				link.setSocket( socket );
				reflector.getDistributor().addAndBringUp( link );
			}
		}
	}
}
