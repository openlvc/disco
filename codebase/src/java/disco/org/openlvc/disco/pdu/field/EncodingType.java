/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.pdu.field;

import org.openlvc.disco.pdu.DisSizes;

/**
 * The fourteen least significant bits of the encoding scheme shall represent 
 * encoding type when the encoding class is encoded audio.
 * 
 * The valid values of encoding type are enumerated in Section 9 of EBV-DOC.
 * 
 * @see "Section 9 of EBV-DOC"
 */
public enum EncodingType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Mulaw8( (short)1 ),
	CVSD  ( (short)2 ),
	ADPCM ( (short)3 ),
	PCM16 ( (short)4 ),
	PCM8  ( (short)5 ),
	VQ    ( (short)6 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private EncodingType( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static EncodingType fromValue( short value )
	{
		for( EncodingType type : values() )
		{
			if( type.value == value )
				return type;
		}
		
		throw new IllegalArgumentException( value+" not a valid EncodingType" );
	}
}
