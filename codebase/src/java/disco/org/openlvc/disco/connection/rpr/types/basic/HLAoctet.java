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
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAoctet implements hla.rti1516e.encoding.HLAoctet
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAoctet()
	{
		this.value = Byte.MIN_VALUE;
	}

	public HLAoctet( byte value )
	{
		this.value = value;
	}

	public HLAoctet( int value )
	{
		this.value = (byte)value;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the byte value of this element.
	 * 
	 * @return value current value
	 */
	public byte getValue()
	{
		return this.value;
	}

	/**
	 * Sets the byte value of this element.
	 * 
	 * @param value new value
	 */
	public void setValue( byte value )
	{
		this.value = value;
	}

	/**
	 * In some instances, an octet can be used to represent, effectively, a uint8.
	 * A java byte, being a signed type, won't support the range natively. To get
	 * around this we take the given short value; confirm it is in the uint8 range,
	 * then convert it to an appropriate bit-format to represent that number and
	 * store in a byte.
	 * 
	 * @param value The value we want to set the octet to. Must be in range 0-255
	 */
	public void setUnsignedValue( short value )
	{
		if( value < 0 || value > 255 )
			throw new IllegalArgumentException( "Unsigned value can only be between 0-255" );
		
		this.value = (byte)value;
	}

	/**
	 * @return The value of this octet as an unsigned 8-bit integer
	 */
	public short getUnsignedValue()
	{
		return (short)Byte.toUnsignedInt(this.value);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Methods /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return 1;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		if( byteWrapper.remaining() < this.getEncodedLength() )
			throw new EncoderException( "Insufficient space remaining in buffer to encode this value" );
		
		byteWrapper.put( this.value );
	}

	@Override
	public int getEncodedLength()
	{
		return 1;
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return new byte[]{ this.value };
	}

	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		if( byteWrapper.remaining() < this.getEncodedLength() )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
		
		this.value = (byte)byteWrapper.get();
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		if( bytes.length < this.getEncodedLength() )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
		
		this.value = bytes[0];
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	/////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Factory /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	public static class Factory implements DataElementFactory<HLAoctet>
	{
		public HLAoctet createElement( int index )
		{
			return new HLAoctet();
		}
	}

}
