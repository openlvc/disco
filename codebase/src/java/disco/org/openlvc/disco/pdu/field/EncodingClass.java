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

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.DisSizes;

/**
 * The two most significant bits of the encoding scheme shall enumerate the 
 * following encoding classes.
 * 
 * The valid values of encoding classes are enumerated in Section 9 of EBV-DOC.
 * 
 * @see "Section 9 of EBV-DOC"
 */
public enum EncodingClass
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	EncodedVoice( (byte)0 ),
	RawBinaryData( (byte)1 ),
	ApplicationSpecificData( (byte)2 ),
	DatabaseIndex( (byte)3 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private EncodingClass( byte value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public byte value()
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

	public static EncodingClass fromValue( byte value )
	{
		for( EncodingClass type : values() )
		{
			if( type.value == value )
				return type;
		}

		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" not a valid EncodingClass" );
		else
			return RawBinaryData;
	}
}
