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

import org.openlvc.disco.DiscoException;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class RPRunsignedInteger32BE implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RPRunsignedInteger32BE()
	{
		this.value = 0;
	}

	public RPRunsignedInteger32BE( long value )
	{
		this.setValue( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Returns the long value of this element.
	 *
	 * @return long value
	 */
	public long getValue()
	{
		return this.value;
	}

	/**
	 * Sets the long value of this element.
	 *
	 * @param value new value
	 */
	public void setValue( long value )
	{
		if( value < 0 || value > 4294967295L )
			throw new DiscoException( "UnsignedInteger32 cannot be less than 0 or greater than 4,294,967,295: "+value );
		this.value = value;
	}

	/**
	 * Returns the octet boundary of this element.
	 *
	 * @return the octet boundary of this element
	 */
	public int getOctetBoundary()
	{
		return 4;
	}

	/**
	 * Returns the size in bytes of this element's encoding.
	 *
	 * @return the size in bytes of this element's encoding
	 */
	public int getEncodedLength()
	{
		return 4;
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
		byteWrapper.align(4);
		if( byteWrapper.remaining() < 4 )
			throw new EncoderException( "Insufficient space remaining in buffer to encode this value" );
		
		byte[] asBytes = toByteArray();
		byteWrapper.put( asBytes );
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
		byte[] buffer = new byte[4];
		buffer[0] = (byte)(value >>> 24 & 0xff);
		buffer[1] = (byte)(value >>> 16 & 0xff);
		buffer[2] = (byte)(value >>>  8 & 0xff);
		buffer[3] = (byte)(value        & 0xff);
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
		byteWrapper.align(4);
		if( byteWrapper.remaining() < 4 )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
			
		byte[] buffer = new byte[4];
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
		this.value = ((bytes[0] & 0xff) << 24) +
		             ((bytes[1] & 0xff) << 16) +
		             ((bytes[2] & 0xff) << 8) +
		             ((bytes[3] & 0xff));
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

