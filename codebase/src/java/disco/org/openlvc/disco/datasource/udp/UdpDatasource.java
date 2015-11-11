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
package org.openlvc.disco.datasource.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IDatasource;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.UdpDatasourceConfig;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.utils.NetworkUtils;

public class UdpDatasource implements IDatasource
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger logger;
	private OpsCenter opscenter;
	private UdpDatasourceConfig configuration;
	private DatagramSocket socket;
	private Thread receiverThread;

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
		
		// create the socket is we know what type
		this.configuration = opscenter.getConfiguration().getNetworkConfiguration();
	}
	
	@Override
	public void open() throws DiscoException
	{
		logger.debug( "Opening UDP Provider connection" );
		
		//
		// Get a reference to the queue we need to dump incoming messages onto
		//
		
		//
		// Get a socket opened up
		//
		InetAddress address = configuration.getAddress();
		int port = configuration.getPort();
		NetworkInterface networkInterface = configuration.getNetworkInterface();
		logger.debug( "Connecting to socket "+address+":"+port+" (interface: "+networkInterface+")" );

		if( address.isMulticastAddress() )
			this.socket = NetworkUtils.createMulticast( address, port, networkInterface );
		else
			this.socket = NetworkUtils.createBroadcast( address, port );
		
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
		logger.info( "Provider close()" );
		
		// Close the socket we're listening on and the thread will drop out
		this.receiverThread.interrupt();
		this.socket.close();
		
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

					opscenter.getPduSource().queueForIngest( buffer );
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
