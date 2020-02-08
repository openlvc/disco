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

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAASCIIchar implements hla.rti1516e.encoding.HLAASCIIchar
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
	public HLAASCIIchar()
	{
		this.value = Byte.MIN_VALUE;
	}

	public HLAASCIIchar( byte value )
	{
		this.value = value;
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

	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// DataElement Methods //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return 1;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		try
		{
			byteWrapper.put( this.value );
		}
		catch( ArrayIndexOutOfBoundsException aioobe )
		{
			// The ByteWrapper class can throw an ArrayIndexOutOfBoundsException, so repackage
			// it as an EncoderException
			throw new EncoderException( aioobe.getMessage(), aioobe );
		}
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
		try
		{
			this.value = (byte)byteWrapper.get();
		}
		catch( ArrayIndexOutOfBoundsException aioobe )
		{
			// The ByteWrapper class can throw an ArrayIndexOutOfBoundsException, so repackage
			// it as an DecoderException
			throw new DecoderException( aioobe.getMessage(), aioobe );
		}
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		if( bytes.length < 1 )
			throw new DecoderException( "Not enough bytes in provided byte array" );
		
		this.value = bytes[0];
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
