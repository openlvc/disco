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
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAfloat32BE implements hla.rti1516e.encoding.HLAfloat32BE
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAfloat32BE()
	{
		this.value = 0.0f;
	}

	public HLAfloat32BE( float value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the float value of this element.
	 * 
	 * @return float value
	 */
	public float getValue()
	{
		return this.value;
	}

	/**
	 * Sets the float value of this element.
	 * 
	 * @param value new value
	 */
	public void setValue( float value )
	{
		this.value = value;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// DataElement Methods //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public final int getOctetBoundary()
	{
		return 4;
	}

	@Override
	public final int getEncodedLength()
	{
		return 4;
	}

	@Override
	public final void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		byteWrapper.align( 4 );
		byteWrapper.putInt( Float.floatToIntBits(value) );
	}

	@Override
	public final byte[] toByteArray() throws EncoderException
	{
		ByteWrapper wrapper = new ByteWrapper(4);
		encode(wrapper);
		return wrapper.array();
	}

	@Override
	public final void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		if( byteWrapper.remaining() < this.getEncodedLength() )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
		
		byteWrapper.align( 4 );

		if ( (byteWrapper.remaining() % 4) != 0)
		{
			value = 0;
			return;
		}
		value = Float.intBitsToFloat( byteWrapper.getInt() );
	}

	@Override
	public final void decode( byte[] bytes ) throws DecoderException
	{
		ByteWrapper wrapper = new ByteWrapper( bytes );
		decode(wrapper);
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
