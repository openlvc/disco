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
package org.openlvc.disco.connection.rpr.types.enumerated;

import org.openlvc.disco.connection.rpr.types.basic.HLAinteger32BE;
import org.openlvc.disco.utils.BitHelpers;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAboolean implements hla.rti1516e.encoding.HLAboolean
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int HLAtrue = 0x01;
	private static final int HLAfalse = 0x00;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAboolean()
	{
		this.value = new HLAinteger32BE( HLAfalse );
	}

	public HLAboolean( boolean value )
	{
		this();
		this.setValue( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the boolean value of this element.
	 * 
	 * @return value
	 */
	public boolean getValue()
	{
		return this.value.getValue() == HLAtrue;
	}

	/**
	 * Sets the boolean value of this element.
	 * 
	 * @param value new value
	 */
	public void setValue( boolean value )
	{
		int valueAsInt = value ? HLAtrue : HLAfalse;
		this.value.setValue( valueAsInt );
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// DataElement Methods //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return this.value.getOctetBoundary();
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		this.value.encode( byteWrapper );
	}

	@Override
	public int getEncodedLength()
	{
		return this.value.getEncodedLength();
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return this.value.toByteArray();
	}

	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		this.value.decode( byteWrapper );
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		try
		{
			int candidateValue = BitHelpers.readIntBE( bytes, 0 );
			if( candidateValue == HLAtrue || candidateValue == HLAfalse )
				this.value.setValue( candidateValue );
			else
				throw new DecoderException("Only valid values for boolean are 0 and 1, found: "+candidateValue);
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
