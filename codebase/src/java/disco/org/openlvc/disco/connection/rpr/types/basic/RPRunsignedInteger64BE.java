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

import java.math.BigInteger;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class RPRunsignedInteger64BE implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final BigInteger MAX = BigInteger.valueOf(2).pow(64).subtract( BigInteger.ONE );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BigInteger value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RPRunsignedInteger64BE()
	{
		this.value = BigInteger.ZERO;
	}

	public RPRunsignedInteger64BE( BigInteger value )
	{
		this.value = value;
	}

	public RPRunsignedInteger64BE( long value )
	{
		this.setValue( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public BigInteger getValue()
	{
		return this.value;
	}
	
	public long getLongValue()
	{
		return this.value.longValue();
	}

	public void setValue( long value )
	{
		assert value >= 0 : "Unsigned integers must be greater than or equal to 0";
		this.value = BigInteger.valueOf( value );
	}

	public void setValue( BigInteger value )
	{
		assert value.compareTo(BigInteger.ZERO) >= 0 : "Unsigned integers must be greater than or equal to 0";
		assert value.compareTo(MAX) <= 0 : "Unsigned integers must be smallre than 2^64-1";
		this.value = value;
	}
	
	/**
	 * Returns the octet boundary of this element.
	 *
	 * @return the octet boundary of this element
	 */
	public int getOctetBoundary()
	{
		return 8;
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
		return 8;
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
		byte[] buffer = new byte[8];
		long temp = this.value.longValue();
		buffer[0] = (byte)(temp >>> 56L & 0xff);
		buffer[1] = (byte)(temp >>> 48L & 0xff);
		buffer[2] = (byte)(temp >>> 40L & 0xff);
		buffer[3] = (byte)(temp >>> 32L & 0xff);
		buffer[4] = (byte)(temp >>> 24L & 0xff);
		buffer[5] = (byte)(temp >>> 16L & 0xff);
		buffer[6] = (byte)(temp >>>  8L & 0xff);
		buffer[7] = (byte)(temp         & 0xff);
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
		if( byteWrapper.remaining() < 8 )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
			
		byte[] buffer = new byte[8];
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
		long temp = (((long)bytes[0] << 56) +
		             ((long)(bytes[1] & 0xff) << 48) +
		             ((long)(bytes[2] & 0xff) << 40) +
		             ((long)(bytes[3] & 0xff) << 32) +
		             ((long)(bytes[4] & 0xff) << 24) +
		             ((long)(bytes[5] & 0xff) << 16) +
		             ((long)(bytes[6] & 0xff) <<  8) +
		             ((long)(bytes[7] & 0xff) <<  0));
		
		this.setValue( BigInteger.valueOf(temp).and(MAX) );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
