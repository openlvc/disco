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
package org.openlvc.disco.connection.rpr.types.basic;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class RPRunsignedInteger16BE implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RPRunsignedInteger16BE()
	{
		this.value = 0;
	}

	public RPRunsignedInteger16BE( int value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Returns the short value of this element.
	 *
	 * @return int value
	 */
	public int getValue()
	{
		return this.value;
	}

	/**
	 * Sets the short value of this element.
	 *
	 * @param value new value
	 */
	public void setValue( int value )
	{
		this.value = value;
	}

	/**
	 * Returns the octet boundary of this element.
	 *
	 * @return the octet boundary of this element
	 */
	public int getOctetBoundary()
	{
		return 2;
	}


	/**
	 * Encodes this element into the specified ByteWrapper.
	 *
	 * @param byteWrapper destination for the encoded element
	 *
	 * @throws EncoderException if the element can not be encoded
	 */
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		byte[] asBytes = toByteArray();
		if( byteWrapper.remaining() < asBytes.length )
			throw new EncoderException( "Insufficient space remaining in buffer to encode this value" );
		
		byteWrapper.put( asBytes );
	}

	/**
	 * Returns the size in bytes of this element's encoding.
	 *
	 * @return the size in bytes of this element's encoding
	 */
	public int getEncodedLength()
	{
		return 2;
	}

	/**
	 * Returns a byte array with this element encoded.
	 *
	 * @return byte array with encoded element
	 *
	 * @throws EncoderException if the element can not be encoded
	 */
	public byte[] toByteArray() throws EncoderException
	{
		byte[] buffer = new byte[2];
		buffer[0] = (byte)(value >>> 8 & 0xff);
		buffer[1] = (byte)(value       & 0xff);
		return buffer;
	}

	/**
	 * Decodes this element from the ByteWrapper.
	 *
	 * @param byteWrapper source for the decoding of this element
	 *
	 * @throws DecoderException if the element can not be decoded
	 */
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		if( byteWrapper.remaining() < 2 )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
			
		byte[] buffer = new byte[2];
		byteWrapper.get( buffer );
		decode( buffer );
	}

	/**
	 * Decodes this element from the byte array.
	 *
	 * @param bytes source for the decoding of this element
	 * 
	 * @throws DecoderException if the element can not be decoded
	 */
	public void decode( byte[] bytes ) throws DecoderException
	{
		this.value = ((bytes[0] & 0xFF) << 8) +
		             ((bytes[1] & 0xFF));
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}

