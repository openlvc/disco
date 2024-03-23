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
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.connection.Metrics;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.UnsupportedPDU;
import org.openlvc.disco.utils.BitHelpers;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.SerializationUtils;
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
	
	// metrics gathering
	private Metrics metrics;

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
		this.bundler       = new Bundler( this, logger );
		this.receiveThread = null;   // set in up()
		
		this.metrics       = new Metrics();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods: Bring Connection Up   ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
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
		// 2. Socket Connection
		//
		// The socket might have already been set using setSocket(), so just be wary
		if( this.socket != null )
		{
			// We do have a socket, no need to connect. Just get reference to the streams
			openStreams();
		}
		else
		{
			openSocket();
			openStreams(); // presumed done if socket is externally set
			handshake();   // presumed done if socket is externally set
		}


		//
		// 3. Bring the message processing infrastructure up
		//
		this.bundler.up( outstream );
		
		// Start the receiver processing thread
		this.receiveThread = new Receiver( linkConfiguration.getName() );
		this.receiveThread.start();
		
		super.linkUp = true;
	}

	/**
	 * Establish a connection to the remote socket 
	 */
	private void openSocket()
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
			this.instream = null;
			this.outstream = null;
			
			// if auto reconnecting, schedule a reconnect
			if( linkConfiguration.isWanAutoReconnect() )
				scheduleReconnect();
			
			throw new DiscoException( "("+address+") "+se.getMessage() );
		}
		catch( Exception e )
		{
			this.linkUp = true; /* otherwise will short-circuit */
			down();
			if( e instanceof DiscoException )
				throw (DiscoException)e;
			else
				throw new DiscoException( "Error bringing TCP link up [openSocket()]: "+e.getMessage(), e );
		}
	}

	/**
	 * Fetch wrappers for the incoming and outgoing streams and store them locally
	 */
	private void openStreams()
	{
		try
		{
			this.instream = new DataInputStream( socket.getInputStream() );
			this.outstream = new DataOutputStream( socket.getOutputStream() );
		}
		catch( Exception e )
		{
			this.linkUp = true; /* otherwise will short-circuit */
			down();
			throw new DiscoException( "Error bringing WAN link up [openStreams()]: "+e.getMessage(), e );
		}
	}

	/**
	 * When connecting with the Relay we need to exchange some information first.
	 * This includes providing some details about ourselves such as our site name
	 * so that conflicts can be avoided, and then our full configuration so that
	 * the relay can adapt.
	 */
	private void handshake()
	{
		logger.debug( "Connected, commencing handshake" );
		
		try
		{	
    		// Step 1. Send Name
    		//         We send our name to confirm the connection and its availability.
    		//         Sender will respond with one of the following values:
    		//           >1: Length of the name, confirms it is OK
    		//           -1: Name is taken and cannot be used
    		//           -2: Could not turn the name into a string
    		String siteName = linkConfiguration.getWanSiteName();
			logger.debug( "Writing name (%s), waiting for confirmation", siteName );
    		outstream.writeUTF( siteName );
    
    		logger.debug( "Waiting for reception from end server" );
    		int responseCode = instream.readInt();
    		if( responseCode == -1 )
    			throw new DiscoException( "[Link: %s] Relay connect failure. Site name in use (%s)",
    			                          getName(), siteName );
    		else
    			logger.debug( "Site name is available, serialize configuration and send" );
    
    		// Step 2. Send our configuration
    		//         On the other side the Relay will pull key pieces out of our configuration and
    		//         replicate it so that we have some control over the settings the other side of
    		//         the link uses to communicate with us, not just the settings we use to talk to
    		//         it. This includes things like bundling.
    		byte[] configurationBytes = SerializationUtils.objectToBytes( linkConfiguration );
    		outstream.writeInt( configurationBytes.length );
    		outstream.write( configurationBytes );
    		
    		logger.debug( "Wrote link configuration, waiting for confirmation" );
    		
    		responseCode = instream.readInt();
    		if( responseCode == -1 )
    			throw new DiscoException( "Unknown error sending link configuration to RELAY." );
    		else
    			logger.debug( "Handshake complete" );
		}
		catch( Exception e )
		{
			this.linkUp = true; /* otherwise will short-circuit */
			down();
			if( e instanceof DiscoException )
				throw (DiscoException)e;
			else
				throw new DiscoException( "Error bringing TCP link up: "+e.getMessage(), e );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods: Take Connection Down   //////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
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
		// 2. Close the socket to stop the messages
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
		// 3. Kill the local processing thread
		//
		if( this.receiveThread != null )
		{
    		this.receiveThread.interrupt();
    		ThreadUtils.exceptionlessThreadJoin( this.receiveThread );
    		this.receiveThread = null;
		}

		logger.trace( "Link is down" );
		super.linkUp = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Submit the message to be sent down the point-to-point connection to the relay so that it
	 * can be re-sent to other connections from there. We submit everything to the {@link Bundler}
	 * for processing. If enabled, it will batch messages, otherwise it will send immediately.
	 */
	public void reflect( Message message )
	{
		try
		{
			// all sending goes via the bundler, even if bundling isn't enabled (in which
			// case it will just be flushed immediately)
			bundler.submit( message.getPdu() );
			metrics.pduSent( message.getPdu().getPduLength() );
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
		// read the payload off the socket
		int length = instream.readInt();
		byte[] payload = new byte[length];
		instream.readFully( payload );
		
		if( logger.isDebugEnabled() )
			logger.debug( "Read payload >> %d bytes", length );
		
		// process the bundle
		processBundle( payload );
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
				// FIXME Need to add support for custom PDUs?
				PDU pdu = PduFactory.getDefaultFactory().create( payload, position, pduSize );
				position += pduSize;
				
				// reflect the PDU to the other links
				reflector.reflect( new Message(this,pdu) );
				metrics.pduReceived( pduSize );
	
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
		// store to hand off to incoming connections
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

	protected void takeDownAndRemove()
	{
		reflector.getDistributor().takeDown( this );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getStatusSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			String string = metrics.getSummaryString();
			// put out special marker on the front so that if fits with all the
			// other distributor summary strings
			//string = string.replaceFirst( "\\{ ", "\\{ WAN, tcp/%s:%d, " );
			string = string.replaceFirst( "\\{ ", "\\{ WAN, " );
			string = string.replaceFirst( "\\ }", "\\, %s }" );
			return String.format( string,
			                      socket.getInetAddress().getHostAddress(),
			                      socket.getPort() );
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
					receiveNext(); // back in main class
			}
			catch( SocketException se )
			{
				// we are shutting down - someone has closed the socket intentionally on us
				logger.debug( "Socket closed, shutting down" );
			}
			catch( EOFException eof )
			{
				// if we hit EOF, the other end disconnected on us
				logger.info( "Remote connection was closed "+socket.getRemoteSocketAddress() );
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
