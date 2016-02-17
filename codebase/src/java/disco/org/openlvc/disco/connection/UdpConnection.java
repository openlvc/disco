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
package org.openlvc.disco.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.UdpConfiguration;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.SocketOptions;
import org.openlvc.disco.utils.StringUtils;

public class UdpConnection implements IConnection
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger logger;
	private OpsCenter opscenter;
	private UdpConfiguration configuration;
	private DatagramSocket socket;
	private Thread receiverThread;

	// cache of details to assist with sending
	private SocketAddress targetAddress;

	// metrics
	private Metrics metrics;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure( OpsCenter opscenter ) throws DiscoException
	{
		this.opscenter = opscenter;
		this.logger = opscenter.getLogger();
		this.configuration = opscenter.getConfiguration().getUdpConfiguration();
	}
	
	@Override
	public void open() throws DiscoException
	{
		this.metrics = new Metrics();
		logger.debug( "Opening UDP Provider connection" );
		
		// Store any temporary information we need to make things a bit faster
		this.targetAddress = new InetSocketAddress( configuration.getAddress(), configuration.getPort() );
		
		//
		// Get a reference to the queue we need to dump incoming messages onto
		//
		
		//
		// Get a socket opened up
		//
		InetAddress address = configuration.getAddress();
		int port = configuration.getPort();
		NetworkInterface networkInterface = configuration.getNetworkInterface();
		if( address.isMulticastAddress() )
		{
			logger.info( "Connecting to multicast group - "+address+":"+port+" (interface: "+networkInterface+")" );
			
			//
			// Create a MULTICAST socket
			//
			SocketOptions options = new SocketOptions();
			options.setSendBufferSize( configuration.getSendBufferSize() );
			options.setRecvBufferSize( configuration.getRecvBufferSize() );
			this.socket = NetworkUtils.createMulticast( address, port, networkInterface, options );
		}
		else
		{
			//
			// Create a BROADCAST socket
			//
			logger.info( "Connecting broadcast socket - %s:%d (interface: %s)", address, port, networkInterface );
			SocketOptions options = new SocketOptions();
			options.setSendBufferSize( configuration.getSendBufferSize() );
			options.setRecvBufferSize( configuration.getRecvBufferSize() );
			this.socket = NetworkUtils.createBroadcast( address, port, options );
		}

		try
		{
			logger.debug( "  -> Send Buffer: %s  (requested: %s)",
			              StringUtils.humanReadableSize(socket.getSendBufferSize()),
			              StringUtils.humanReadableSize(configuration.getSendBufferSize()) );
			logger.debug( "  -> Recv Buffer: %s  (requested: %s)",
			              StringUtils.humanReadableSize(socket.getReceiveBufferSize()),
			              StringUtils.humanReadableSize(configuration.getRecvBufferSize()) );
		}
		catch( SocketException se )
		{
			logger.debug( "Could not determine buffer sizes: "+se.getMessage(), se );
		}


		//
		// Start the receiver thread so we can process PDUs
		//
		this.receiverThread = new Thread( new Receiver(), "UDP Receiver" );
		this.receiverThread.start();

		logger.info( "UDP Provider open and processing" );
	}
	
	@Override
	public void close() throws DiscoException
	{
		if( this.socket.isClosed() )
			return;
		
		// Close the socket we're listening on and the thread will drop out
		this.receiverThread.interrupt();
		this.socket.close();
		
		// Print some metrics while we wait
		logger.info( "=== PDU Summary ===" );
		logger.info( "       Sent: %,d (%,d bytes)", metrics.getPdusSent(), metrics.getBytesSent() );
		logger.info( "   Received: %,d (%,d bytes)", metrics.getPdusReceived(), metrics.getBytesReceived() );
		logger.info( "" );

		// Wait for the receiver to close up shop
		try
		{
			this.logger.debug( "Waiting for receiver thread to shut down" );
			this.receiverThread.join();
			this.logger.debug( "Receiver thread is down, UDP provider successfully closed" );
		}
		catch( InterruptedException ie )
		{}
	}
	
	@Override
	public String getName()
	{
		return "network.udp";
	}

	@Override
	public Metrics getMetrics()
	{
		return this.metrics;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Sender Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void send( byte[] payload ) throws DiscoException
	{
		try
		{
			socket.send( new DatagramPacket(payload,0,payload.length,targetAddress) );
			metrics.pduSent( payload.length );
		}
		catch( IOException ioex )
		{
			throw new DiscoException( ioex.getMessage(), ioex );
		}
	}
	
	public void send( PDU pdu ) throws DiscoException
	{
		// Create a DISOutputStream to write to
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DisOutputStream dos = new DisOutputStream( baos );
		
		try
		{
			// Firstly write the PDU header to the stream
			pdu.writeHeader( dos );
			
			// Write the body content
			pdu.to( dos );
			
			// Send the payload
			send( baos.toByteArray() );
		}
		catch( IOException ioex )
		{
			logger.warn( "Error trying to send PDU ("+pdu+"): "+ioex.getMessage(), ioex );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Class: Receiver   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class Receiver implements Runnable
	{
		public void run()
		{
			logger.debug( "UDP Provider receiver thread up and running" );
			
			while( Thread.interrupted() == false )
			{
				try
				{
					byte[] buffer = new byte[DisSizes.PDU_MAX_SIZE];
					DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
					socket.receive( packet );

					if( logger.isTraceEnabled() )
						logger.trace( "(Packet) size="+packet.getLength()+", source="+packet.getSocketAddress() );

					opscenter.getPduReceiver().receive( buffer );
					metrics.pduReceived( packet.getLength() );
				}
				catch( SocketException se )
				{
					// socket was closed on it - that's our cue to leave!
					return;
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
