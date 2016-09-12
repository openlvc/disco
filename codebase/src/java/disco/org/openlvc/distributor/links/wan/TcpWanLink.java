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
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.pdu.UnsupportedPDU;
import org.openlvc.disco.utils.BitHelpers;
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
	private Bundler bundler;
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
		this.bundler       = new Bundler( linkConfiguration, logger );
		this.receiveThread = null;   // set in up()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * This class is used on both ends of a connection. When a TcpWanLink connects to a Relay,
	 * internally the relay will create a new TcpWanLink instance with the exception that the
	 * socket used will be the incoming one, rather than a newly created one. This is how we
	 * set that socket.
	 * 
	 * @throws DiscoException If the connection is already up (can't set socket on an active connection)
	 */
	public void setSocket( Socket socket ) throws DiscoException
	{
		if( isUp() )
			throw new DiscoException( "Link is already up, cannot set socket" );
		
		this.socket = socket;
	}
	
	
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

		//
		// 1. Make sure we have everything we need
		//
		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: WAN" );

		//
		// 2. Create Socket
		//    If we don't aleady have a socket, create one and connect to the relay
		//    If we are running inside a relay, the socket will be set externally
		//
		if( this.socket == null )
		{
			// a. Resolve the address, it may be one of the symbolic values
			InetAddress relayAddress = NetworkUtils.resolveInetAddress( linkConfiguration.getWanAddress() );
			InetSocketAddress address = new InetSocketAddress( relayAddress, linkConfiguration.getWanPort() );

			try
			{
    			// b. Create the socket and set its options
    			this.socket = new Socket();
    			this.socket.setTcpNoDelay( true );
    			this.socket.setPerformancePreferences( 0, 1, 1 );
    		
    			// c. Connect to the socket
    			this.socket.connect( address, 5000/*timeout*/ );
			}
			catch( ConnectException | SocketTimeoutException se )
			{
				this.socket = null;
				
				// if auto reconnecting, schedule a reconnect
				if( linkConfiguration.isWanAutoReconnect() )
					scheduleReconnect();
				
				throw new DiscoException( "("+address+") "+se.getMessage() );
			}
			catch( Exception e )
			{
				this.linkUp = true; /* otherwise will short-circuit */
				down();
				throw new DiscoException( "Error bringing TCP link up: "+e.getMessage(), e );
			}
		}

		//
		// 3. Connect the Streams
		//
		try
		{
			this.instream = new DataInputStream( socket.getInputStream() );
			this.outstream = new DataOutputStream( socket.getOutputStream() );
		}
		catch( Exception e )
		{
			this.linkUp = true; /* otherwise will short-circuit */
			down();
			throw new DiscoException( "Error bringing TCP link up: "+e.getMessage(), e );
		}

		//
		// 4. Start the Bundler
		//
		this.bundler.up( outstream );

		//
		// 5. Start the receiver processing thread
		//
		this.receiveThread = new Receiver( linkConfiguration.getName() );
		this.receiveThread.start();
		
		super.linkUp = true;
	}

	public void down()
	{
		if( isDown() )
			return;
		
		logger.debug( "Taking down link: "+super.getName() );
		
		//
		// 1. Flush the bundler
		//
		if( bundler.isUp() )
			bundler.down();
		
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

		//
		// 3. Stop processing incoming messages
		//
		if( this.receiveThread != null )
		{
    		this.receiveThread.interrupt();
    		ThreadUtils.exceptionlessThreadJoin( this.receiveThread );
    		this.receiveThread = null;
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
			                      linkConfiguration.getWanAddress(),
			                      linkConfiguration.getWanPort() );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		try
		{
			bundler.submit( message.getPdu() );
			//byte[] pdu = message.getPdu().toByteArray();
			//outstream.writeInt( pdu.length );
			//outstream.write( pdu );
		}
		catch( IOException ioex )
		{
			logger.error( "Failed converting/writing PDU: "+ioex.getMessage(), ioex );
		}
	}

	/**
	 * Read the next PDU bundle from the socket and process it.
	 */
	private void receiveNext() throws IOException
	{
		try
		{
			// read the payload off the socket
			int length = instream.readInt();
			byte[] payload = new byte[length];
			instream.readFully( payload );
			
			if( logger.isDebugEnabled() )
				logger.debug( "Read payload >> %d bytes", length );
			
			// process the bundle
			processBundle( payload );
		}
		catch( EOFException eof )
		{
			// if we hit EOF, the other end disconnected on us
			logger.info( "Remote disconnection "+socket.getRemoteSocketAddress() );
			takeDownAndRemove();
			scheduleReconnect();
			throw eof;
		}
	}

	private void processBundle( byte[] payload )
	{
		// Iterate over the bundle until there are now more pdus to read
		try
		{
			int position = 0;
			while( position < payload.length )
			{
				// read the pdu size
				int pduSize = BitHelpers.readIntBE( payload, position );
				position += 4;
				
				// read the PDU straight off the buffer
				PDU pdu = PduFactory.create( payload, position, pduSize );
				position += pduSize;
				
				// reflect the PDU to the other links
				reflector.reflect( new Message(this,pdu) );
	
				if( logger.isTraceEnabled() )
					logger.trace( "Received >> "+pdu.getType() );
			}
		}
		catch( InterruptedException ie )
		{
			// while reflecting the message to the other links
			logger.warn( "PDU dropped, interrupted while offering to reflector: "+ie.getMessage() );
		}
		catch( UnsupportedPDU unsupported )
		{
			// pdu we don't care for
			// This really shouldn't happen - to get to a WAN link the PDU has to come
			// through the Disco conversion process before this, and if unsupported, it
			// should fail there. However, this is a good sign of stream corruption.
			logger.warn( "PDU dropped. "+unsupported.getMessage() );
		}
		catch( Exception e )
		{
			logger.warn( "PDU dropped, error while converting from byte to pdu: "+e.getMessage(), e );
		}
	}
	
	public void setReflector( Reflector reflector )
	{
		this.reflector = reflector;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void scheduleReconnect()
	{
		// Only schedule a reconnect if we are not transient
		// When a WAN link connects to a relay, it creates another WAN Link in the receiver around
		// the incoming stream. Just because this exists doesn't mean we can connect back from server
		// to client. Auto-connect attemps should only trigger from the client to the relay, not the
		// other way around.
		//
		// Thankfully, the Relay marks the links it creates as transient so that the distributor removes
		// them from its store when taking them down (they are not persisent over restarts as they are
		// not part of the relay's configuration).
		//
		// As such, Relay embedded/created WAN links will be marked transient, so we can detect this
		// and ignore connections from server->client
		if( this.isTransient() )
		{
			logger.debug( "[%s] is transient, not scheduling a reconnect", getName() );
			return;
		}
		
		// Only schedule a reconnect if we are configured to
		if( linkConfiguration.isWanAutoReconnect() == false )
		{
			logger.error( "Attempted to schedule a reconnect, but reconnect not configured - ignoring" );
			return;
		}
		
		// Schedule the reconnect to happen a bit later
		Runnable reconnector = new Runnable()
		{
			public void run()
			{
				try
				{
					ThreadUtils.exceptionlessSleep(10000);
					logger.debug( "Attempting Reconnect "+getConfigSummary() );
					up();
				}
				catch( Exception e )
				{
					logger.debug( "Auto-reconnect for %s failed: %s", getName(), e.getMessage() );
				}
			}
		};
		
		new Thread(reconnector,getName()+"-reconnect").start();
	}

	private void takeDownAndRemove()
	{
		reflector.getDistributor().takeDown( this );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/////////////////////////////////////////////////////////////////////////////////////
	/// Receive Processing  /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Class responsible for receiving messages from the remote host represented by this instance */
	private class Receiver extends Thread
	{
		private Receiver( String parentName )
		{
			super( parentName+"-Recv" );
		}
		
		public void run()
		{
			try
			{
				// Process requests from the client
				while( Thread.interrupted() == false )
					receiveNext();
			}
			catch( SocketException se )
			{
				// connection was reset
				logger.debug( "Remote connection was closed, scheduling reconnect" );
				takeDownAndRemove();
				scheduleReconnect();
			}
			catch( IOException ioe )
			{
				ioe.printStackTrace();
			}
		}
	}

}
