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
import org.openlvc.disco.utils.BitHelpers;

public class UdpResponse extends UdpMessage
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean isError;
	private byte[] returnValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpResponse()
	{
		this.isError = false;
		this.returnValue = new byte[] {};
	}
	
	public UdpResponse( int messageId )
	{
		super( messageId );
		this.isError = false;
		this.returnValue = new byte[] {};
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public MessageType getType()
	{
		if( isError )
			return MessageType.ErrorResponse;
		else
			return MessageType.OkResponse;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Serialization Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void writeTo( DisOutputStream stream ) throws IOException
	{
		// make sure we don't exceed any size limits
		int payloadSize = 1 + returnValue.length;
		if( payloadSize > UdpMessage.MAX_PAYLOAD_SIZE )
		{
			throw new DiscoException( "Cannot send; exceeds max payload size (size=%d, max=%d)",
			                          payloadSize, UdpMessage.MAX_PAYLOAD_SIZE );
		}

		// write the size of the payload to the packet
		stream.writeShort( (short)payloadSize );

		// write the result field
		stream.writeBoolean( isError );
		
		// write the contents of the return value
		if( returnValue.length > 0 )
			stream.write( returnValue, 0, returnValue.length );
	}

	@Override
	public void loadFrom( DisInputStream stream ) throws DiscoException, IOException
	{
		// read the size in
		short payloadSize = stream.readShort();
		
		// read the result field
		this.isError = stream.readBoolean();
		
		// read the payload
		if( payloadSize > 0 )
		{
			this.returnValue = new byte[payloadSize];
			stream.read( returnValue, 0, payloadSize );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isError()
	{
		return this.isError;
	}
	
	public boolean isOk()
	{
		return this.isError == false;
	}
	
	public void setAsError()
	{
		this.isError = true;
	}

	/**
	 * Mark this response as an error with the given message, which can be a format string
	 * when using the optional varargs.
	 */
	public void setAsError( String message, Object... arguments )
	{
		this.isError = true;
		this.returnValue = String.format(message, arguments).getBytes();
	}
	
	public void setAsOk()
	{
		this.isError = false;
	}
	
	/**
	 * Mark this response as a success with the given message, which can be a format string
	 * when using the optional varargs.
	 */
	public void setAsOk( String message, Object... arguments )
	{
		this.isError = false;
		this.returnValue = String.format(message, arguments).getBytes();
	}
	
	public void setError( boolean isError )
	{
		this.isError = isError;
	}
	
	///////////////////////////////////////////////////////////////
	/// Get/Set Response Value    /////////////////////////////////
	///////////////////////////////////////////////////////////////
	public String getValueAsString()
	{
		return new String( returnValue );
	}
	
	public void setValueAsString( String value )
	{
		this.returnValue = value.getBytes();
	}

	public int getValueAsInt()
	{
		return BitHelpers.readIntBE( returnValue, 0 );
	}

	public void setValueAsInt( int value )
	{
		this.returnValue = BitHelpers.intToBytes( value );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
