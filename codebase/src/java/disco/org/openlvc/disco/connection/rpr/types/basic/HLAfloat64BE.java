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

import org.openlvc.disco.utils.BitHelpers;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAfloat64BE implements hla.rti1516e.encoding.HLAfloat64BE
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private double value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAfloat64BE()
	{
		this.value = Double.MIN_VALUE;
	}

	public HLAfloat64BE( double value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the double value of this element.
	 * 
	 * @return double value
	 */
	public double getValue()
	{
		return this.value;
	}

	/**
	 * Sets the double value of this element.
	 * 
	 * @param value new value
	 */
	public void setValue( double value )
	{
		this.value = value;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// DataElement Methods //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public final int getOctetBoundary()
	{
		return 8;
	}

	@Override
	public final int getEncodedLength()
	{
		return 8;
	}

	@Override
	public final void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		try
		{
			byteWrapper.put( toByteArray() );
		}
		catch( Exception e )
		{
			throw new EncoderException( e.getMessage(), e );
		}
	}

	@Override
	public final byte[] toByteArray() throws EncoderException
	{
		byte[] buffer = new byte[8];
		BitHelpers.putDoubleBE( value, buffer, 0 );
		return buffer;
	}

	@Override
	public final void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		if( byteWrapper.remaining() < 8 )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
		
		byte[] buffer = new byte[8];
		byteWrapper.get( buffer );
		decode( buffer );
	}

	@Override
	public final void decode( byte[] bytes ) throws DecoderException
	{
		if( bytes.length < 8 )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
		
		this.value = BitHelpers.readDoubleBE( bytes, 0 );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
