/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.disco.connection.rpr.types.simple;

import org.openlvc.disco.utils.BitHelpers;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAASCIIstring implements hla.rti1516e.encoding.HLAASCIIstring
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String CHARSET = "ISO-8859-1";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAASCIIstring()
	{
		this.value = "";
	}

	public HLAASCIIstring( String value )
	{
		if( value == null )
			this.value = "null";
		else
			this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the string value of this element.
	 * 
	 * @return string value
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * Sets the string value of this element.
	 * 
	 * @param value new value
	 */
	public void setValue( String value )
	{
		this.value = value;
	}

	public byte[] getBytes()
	{
		try
		{
			return this.value.getBytes( CHARSET );
		}
		catch( Exception e )
		{
			throw new RuntimeException( e.getMessage(), e );
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// DataElement Methods //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return 4 + this.value.length();
	}

	@Override
	public int getEncodedLength()
	{
		return 4 + getBytes().length;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		if( byteWrapper.remaining() < getEncodedLength() )
			throw new EncoderException( "Insufficient space remaining in buffer to encode this value" );
		
		byte[] buffer = getBytes();
		byteWrapper.putInt( buffer.length );
		byteWrapper.put( buffer );
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		byte[] bytes = getBytes();
		byte[] buffer = new byte[4+bytes.length];
		BitHelpers.putIntBE( bytes.length, buffer, 0 );
		BitHelpers.putByteArray( bytes, buffer, 4 );
		return buffer;
	}

	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		try
		{
			int length = byteWrapper.getInt();
			byte[] buffer = new byte[length];
			byteWrapper.get( buffer );
			
			this.value = new String( buffer, CHARSET );
		}
		catch( Exception e )
		{
			throw new DecoderException( e.getMessage(), e );
		}
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		try
		{
			int length = BitHelpers.readIntBE( bytes, 0 );
			byte[] stringBytes = BitHelpers.readByteArray( bytes, 4, length );
			this.value = new String( stringBytes, CHARSET );
		}
		catch( Exception e )
		{
			throw new DecoderException( e.getMessage(), e );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
