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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.openlvc.disco.DiscoException;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class TcpTransport implements ITransport
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private LinkConfiguration linkConfiguration;
	private Socket socket;
	private DataInputStream instream;
	private DataOutputStream outstream;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected TcpTransport( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
		
		this.socket = null;    // set in up()
		this.instream = null;  // set in up()
		this.outstream = null; // set in up()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		if( this.socket != null )
			return;
		
		InetSocketAddress address = new InetSocketAddress( linkConfiguration.getWanAddress(),
		                                                   linkConfiguration.getWanPort() );
		
		try
		{
			// 1. Create the socket and set its options
			this.socket = new Socket();
			this.socket.setTcpNoDelay( true );
			this.socket.setPerformancePreferences( 0, 1, 1 );
		
			// 2. Connect to the socket
			this.socket.connect( address, 5000/*timeout*/ );
			
			// 3. Get the streams
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
			throw new DiscoException( "Error bringing TCP transport up: "+e.getMessage(), e );
		}
		
	}
	
	public void down()
	{
		if( this.socket == null )
			return;

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
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( byte[] message ) throws IOException
	{
		outstream.writeInt( message.length );
		outstream.write( message );
	}

	/**
	 * Reads the next message off the socket and returns the payload.
	 * Blocks until a message is available.
	 */
	public byte[] receiveNext() throws IOException
	{
		int length = instream.readInt();
		byte[] payload = new byte[length];
		instream.readFully( payload );
		return payload;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}

