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

import java.io.IOException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class WanLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;
	private ITransport transport;
	private Receiver receiveThread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	public WanLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		this.reflector = null;   // set in setReflector() prior to call to up()
		this.transport = null;   // set in up()
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
		
		// 1. Make sure we have everything we need
		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: WAN" );
		
		// 2. Create the transport and connect
		this.transport = createTransport();
		this.transport.up();
		
		// 3. Start the receiver thread processing
		this.receiveThread = new Receiver();
		this.receiveThread.start();
		
		super.linkUp = true;
	}
	
	public void down()
	{
		if( isDown() )
			return;
		
		logger.debug( "Taking down link: "+super.getName() );
		
		// 1. Cut off the incoming stream
		transport.down();
		
		// 2. Kill the receiver thread
		this.receiveThread.interrupt();
		ThreadUtils.exceptionlessThreadJoin( this.receiveThread );
		
		logger.debug( "Link is down" );
		super.linkUp = false;
	}

	public String getConfigSummary()
	{
		return "{ Not Implemented }";
	}

	public String getStatusSummary()
	{
		return "{ Not Implemented }";
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		try
		{
			byte[] pdu = message.getPdu().toByteArray();
			transport.reflect( pdu );
		}
		catch( IOException ioex )
		{
			logger.error( "Failed to convert PDU into byte[]: "+ioex.getMessage(), ioex );
		}
	}

	/**
	 * Processes the received payload and passes it up to the reflector.
	 * This method is called from the Receiver thread.
	 */
	private void receive( byte[] payload )
	{
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
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private ITransport createTransport() throws DiscoException
	{
		switch( super.linkConfiguration.getWanTransport() )
		{
			//case UDP:
			//	return new UdpTransport( super.linkConfiguration );
			case TCP:
				return new TcpTransport( super.linkConfiguration );
			default:
				throw new DiscoException( "Unknown Relay transport type: "+super.linkConfiguration.getWanTransport() );
		}
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
		public void run()
		{
			try
			{
				// Process requests from the client
				while( Thread.interrupted() == false )
					receive( transport.receiveNext() );
			}
			catch( IOException ioe )
			{
			}
		}
	}

}
