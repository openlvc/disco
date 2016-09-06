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
package org.openlvc.distributor.links.wan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class TcpWanLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;

	private Socket socket;
	private DataInputStream instream;
	private DataOutputStream outstream;
	private Receiver receiveThread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public TcpWanLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		this.reflector     = null;   // set in setReflector() prior to call to up()

		this.socket        = null;   // set in up()
		this.instream      = null;   // set in up()
		this.outstream     = null;   // set in up()
		this.receiveThread = null;   // set in up()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Open a new socket to the next relay and start the thread
	 * that will process incoming messages.
	 * 
	 * @throws DiscoException If there is a timeout trying to connect to the relay, or it can't
	 *                        be contacted
	 */
	public void up()
	{
		if( isUp() )
			return;

		// if we pass null it will create a new socket based on config
		up( null );
	}

	/**
	 * The same as {@link up}, except that the socket has been created elsewhere. This is to
	 * allow the auto-connection of WAN links to Relays. The WAN Link connects to a Relay, which
	 * then creates a new local WAN link object and wires it into the Relay's distributor.
	 */
	public void up( Socket existingSocket )
	{
		if( isUp() )
			throw new DiscoException( "WAN link is already up" );

		//
		// 1. Make sure we have everything we need
		//
		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: WAN" );

		//
		// 2. Create the socket (or use existing) and connect to the relay
		//
		initializeSocket( existingSocket ); // may be null
		
		//
		// 3. Start the receiver processing thread
		//
		this.receiveThread = new Receiver();
		this.receiveThread.start();
		
		super.linkUp = true;
	}


	/**
	 * Create the socket and connect to the remote Relay. Open the streams so that we are ready
	 * to process.
	 */
	private void initializeSocket( Socket existing )
	{
		// 1. Resolve the address, it may be one of the symbolic values
		InetAddress relayAddress = NetworkUtils.resolveInetAddress( linkConfiguration.getWanAddress() );
		InetSocketAddress address = new InetSocketAddress( relayAddress, linkConfiguration.getWanPort() );

		try
		{
			if( existing == null )
			{
				// 2. Create the socket and set its options
				this.socket = new Socket();
    			this.socket.setTcpNoDelay( true );
    			this.socket.setPerformancePreferences( 0, 1, 1 );
    		
    			// 3. Connect to the socket
    			this.socket.connect( address, 5000/*timeout*/ );
			}
			else
			{
				this.socket = existing;
			}
			
			// 4. Get the streams
			this.instream = new DataInputStream( socket.getInputStream() );
			this.outstream = new DataOutputStream( socket.getOutputStream() );
		}
		catch( SocketTimeoutException se )
		{
			this.socket = null;
			throw new DiscoException( "("+address+") "+se.getMessage() );
		}
		catch( Exception e )
		{
			down();
			throw new DiscoException( "Error bringing TCP link up: "+e.getMessage(), e );
		}
	}

	public void down()
	{
		if( isDown() )
			return;
		
		logger.debug( "Taking down link: "+super.getName() );
		
		//
		// 1. Stop processing incoming messages
		//
		this.receiveThread.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.receiveThread );
		this.receiveThread = null;
		
		//
		// 2. Take the connection down
		//
		try
		{
			this.socket.close();
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error bringing TCP transport down: "+e.getMessage(), e );
		}
		finally
		{
			this.socket = null;
			this.instream = null;
			this.outstream = null;
			super.linkUp = false;
		}
		
		logger.debug( "Link is down" );
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
			return String.format( "{ WAN, address:%s, port:%d, transport:tcp }",
			                      socket.getInetAddress(),
			                      socket.getPort() );
		}
		else
		{
			// return raw configuration data
			return String.format( "{ WAN, address:%s, port:%d, transport:tcp }",
			                      linkConfiguration.getRelayAddress(),
			                      linkConfiguration.getRelayPort() );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		try
		{
			byte[] pdu = message.getPdu().toByteArray();
			outstream.writeInt( pdu.length );
			outstream.write( pdu );
		}
		catch( IOException ioex )
		{
			logger.error( "Failed converting/writing PDU: "+ioex.getMessage(), ioex );
		}
	}

	/**
	 * Read the next PDU from the socket and process it.
	 */
	private void receiveNext() throws IOException
	{
		// 1. Wait for the next message
		int length = instream.readInt();
		byte[] payload = new byte[length];
		instream.readFully( payload );

		// 2. Turn it into a PDU and hand-off for processings
		try
		{
			// 1. Turn the payload into a PDU
			PDU pdu = PduFactory.create( payload );

			// 2. Hand the PDU off for processing
			reflector.reflect( new Message(this,pdu) );
		}
		catch( InterruptedException ie )
		{
			logger.warn( "PDU dropped, interrupted while offering to reflector: "+ie.getMessage() );
		}
		catch( IOException io )
		{
			logger.warn( "PDU dropped, error while converting from byte to pdu: "+io.getMessage() );
		}
	}
	
	public void setReflector( Reflector reflector )
	{
		this.reflector = reflector;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/////////////////////////////////////////////////////////////////////////////////////
	/// Receive Processing  /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Class responsible for receiving messages from the remote host represented by this instance */
	private class Receiver extends Thread
	{
		public void run()
		{
			try
			{
				// Process requests from the client
				while( Thread.interrupted() == false )
					receiveNext();
			}
			catch( IOException ioe )
			{
			}
		}
	}

}
