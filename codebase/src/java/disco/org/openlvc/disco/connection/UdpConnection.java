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
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.UdpConfiguration;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.Platform;
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
	private DatagramSocket sendSocket;
	private DatagramSocket recvSocket;
	private Thread receiverThread;

	// cache of details to assist with sending
	private SocketAddress targetAddress;
	
	// cache of configuration to assist with receive filtering
	private short exerciseId;

	// metrics
	private Metrics metrics;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpConnection()
	{
		this.logger = null;          // set in configure()
		this.opscenter = null;       // set in configure()
		this.configuration = null;   // set in configure()
		this.receiverThread = null;  // set in open()
		
		this.sendSocket = null;      // set in open()
		this.recvSocket = null;      // set in open()
		this.targetAddress = null;   // set in open()
		this.exerciseId = -1;        // set in configure()
		this.metrics = null;         // set in open()
	}
	
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<PduType> getSupportedPduTypes()
	{
		return PduFactory.getSupportedPduTypes();
	}

	@Override
	public void configure( OpsCenter opscenter ) throws DiscoException
	{
		this.opscenter = opscenter;
		this.logger = opscenter.getLogger();
		this.configuration = opscenter.getConfiguration().getUdpConfiguration();
		
		// receive-side cache values
		this.exerciseId = opscenter.getConfiguration().getDisConfiguration().getExerciseId();
	}
	
	@Override
	public void open() throws DiscoException
	{
		this.metrics = new Metrics();
		logger.debug( "Opening UDP Provider connection" );
		
		// Store any temporary information we need to make things a bit faster
		this.targetAddress = new InetSocketAddress( configuration.getAddress(), configuration.getPort() );
		
		//
		// Create Socket
		//
		InetAddress address = configuration.getAddress();
		int port = configuration.getPort();
		NetworkInterface networkInterface = configuration.getNetworkInterface();
		
		SocketOptions options = new SocketOptions();
		options.setSendBufferSize( configuration.getSendBufferSize() );
		options.setRecvBufferSize( configuration.getRecvBufferSize() );
		options.setTimeToLive( configuration.getTimeToLive() );
		options.setTrafficClass( configuration.getTrafficClass() );
		
		if( address.isMulticastAddress() )
		{
			logger.info( "Connecting to multicast group - "+address+":"+port+" (interface: "+networkInterface+")" );
			
			//
			// Create a MULTICAST socket
			//
			DatagramSocket[] pair = NetworkUtils.createMulticastPair( address, port, networkInterface, options );
			this.sendSocket = pair[0];
			this.recvSocket = pair[1];

//			this.recvSocket = NetworkUtils.createMulticast( address, port, networkInterface, options );
//			this.sendSocket = this.recvSocket; // same for multicast - no good way to do loopback exclusion
		}
		else
		{
			//
			// Create a BROADCAST socket
			//
			// If we are linux or mac, we have to use the broadcast address. However if we are on
			// windows, we cannot bind to the broadcast address, we have to bind to the actual
			// NIC address.
			// 
			if( Platform.getOperatingSystem() == Platform.OS.Windows )
				address = NetworkUtils.getFirstIPv4Address( networkInterface );
			else
				address = networkInterface.getInterfaceAddresses().get(0).getBroadcast();

			logger.info( "Connecting broadcast socket - %s:%d (interface: %s)", address, port, networkInterface );
			
			// Create the socket pair
			DatagramSocket[] pair = NetworkUtils.createBroadcastPair( address, port, networkInterface, options );
			this.sendSocket = pair[0];
			this.recvSocket = pair[1];
		}

		try
		{
			logger.debug( "  -> Send Buffer: %s  (requested: %s)",
			              StringUtils.humanReadableSize(sendSocket.getSendBufferSize()),
			              StringUtils.humanReadableSize(configuration.getSendBufferSize()) );
			logger.debug( "  -> Recv Buffer: %s  (requested: %s)",
			              StringUtils.humanReadableSize(recvSocket.getReceiveBufferSize()),
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
		if( this.recvSocket == null || this.recvSocket.isClosed() )
			return;
		
		// Close the socket we're listening on and the thread will drop out
		this.receiverThread.interrupt();
		this.sendSocket.close();
		if( this.recvSocket.isClosed() == false )
			// have to be careful - could be the same as the send socket
			this.recvSocket.close();
		
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
			sendSocket.send( new DatagramPacket(payload,0,payload.length,targetAddress) );
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

			// cache this for efficient lookup below
			final int ourSendPort = sendSocket.getLocalPort();
			final InetAddress ourSendAddress = sendSocket.getLocalAddress();

			while( Thread.interrupted() == false )
			{
				try
				{
					// 1. Receive the packet
					byte[] buffer = new byte[DisSizes.PDU_MAX_SIZE];
					DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
					recvSocket.receive( packet );
				
					// 2. Discard if loopback packet (only relevant to broadcast for us)
					if( packet.getPort() == ourSendPort &&
						packet.getAddress().equals(ourSendAddress) )
					{
						continue;
					}
					
					// 3. Discard if outside our exercise (0=accept any exercise)
					if( exerciseId == 0 || buffer[1] == exerciseId )
					{
						// hand it off to the receiver
						if( logger.isTraceEnabled() )
							logger.trace( "(Packet) size="+packet.getLength()+", source="+packet.getSocketAddress() );

						opscenter.getPduReceiver().receive( buffer );
						metrics.pduReceived( packet.getLength() );
					}
					else
					{
						metrics.pduDiscarded();
						continue;
					}
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
