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

public enum ForceIdentifierEnum8 implements ExtendedDataElement<ForceIdentifierEnum8>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other(0),
	Friendly(1),     Opposing(2),     Neutral(3),
	Friendly_2(4),   Opposing_2(5),   Neutral_2(6),
	Friendly_3(7),   Opposing_3(8),   Neutral_3(9),
	Friendly_4(10),  Opposing_4(11),  Neutral_4(12),
	Friendly_5(13),  Opposing_5(14),  Neutral_5(15),
	Friendly_6(16),  Opposing_6(17),  Neutral_6(18),
	Friendly_7(19),  Opposing_7(20),  Neutral_7(21),
	Friendly_8(22),  Opposing_8(23),  Neutral_8(24),
	Friendly_9(25),  Opposing_9(26),  Neutral_9(27),
	Friendly_10(28), Opposing_10(29), Neutral_10(30);

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ForceIdentifierEnum8( int value )
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
	public ForceIdentifierEnum8 valueOf( ByteWrapper value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public ForceIdentifierEnum8 valueOf( byte[] value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static ForceIdentifierEnum8 valueOf( short value )
	{
		for( ForceIdentifierEnum8 temp : ForceIdentifierEnum8.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		// Don't be so strict
		// throw new UnsupportedException( "Unknown enumerator value: "+value+" (ForceIdentifierEnum8)" );
		return Other;
	}
}
