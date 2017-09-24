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
package org.openlvc.distributor.links.wan.udp.msg;

import java.io.IOException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;

public class UdpKeepAlive extends UdpMessage
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	/** Size of this packet including the header. Keep Alive packets are always the same size. */
	private static final short PACKET_SIZE = UdpMessage.HEADER_SIZE+1;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean request;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpKeepAlive()
	{
		this.request = true;
	}

	public UdpKeepAlive( boolean request )
	{
		this.request = request;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public MessageType getType()
	{
		return MessageType.KeepAlive;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Serialization Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void writeTo( DisOutputStream stream ) throws IOException
	{
		stream.writeShort( PACKET_SIZE );
		stream.writeBoolean( this.request );
	}

	@Override
	public void loadFrom( DisInputStream stream ) throws DiscoException, IOException
	{
		short length = stream.readShort();
		if( length != PACKET_SIZE )
			throw new DiscoException( "Incorrect packet length in header" );
		
		this.request = stream.readBoolean();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isRequest()
	{
		return this.request;
	}
	
	public boolean isResponse()
	{
		return !this.request;
	}
	
	public void setRequest( boolean request )
	{
		this.request = request;
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
