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

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum MarkingEncodingEnum8 implements ExtendedDataElement<MarkingEncodingEnum8>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other(0),
	ASCII(1),
	ArmyMarkingCCTT(2),
	DigitChevron(3);

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private MarkingEncodingEnum8( int value )
	{
		this.value = new HLAoctet( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	public byte getValue()
	{
		return this.value.getValue();
	}
	
	public short getUnsignedValue()
	{
		return this.value.getUnsignedValue();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Data Element Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return value.getOctetBoundary();
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		value.encode( byteWrapper );
	}


	@Override
	public int getEncodedLength()
	{
		return value.getEncodedLength();
	}


	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return value.toByteArray();
	}


	@Override
	public MarkingEncodingEnum8 valueOf( ByteWrapper value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public MarkingEncodingEnum8 valueOf( byte[] value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static MarkingEncodingEnum8 valueOf( short value )
	{
		for( MarkingEncodingEnum8 temp : MarkingEncodingEnum8.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		// Don't be so strict
		// throw new UnsupportedException( "Unknown enumerator value: "+value+" (MarkingEncodingEnum8)" );
		return Other;
	}
}
