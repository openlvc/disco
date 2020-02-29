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
package org.openlvc.disco.pdu.field.appearance.enums;

/**
 * Platform trailing effects appearance values.
 * 
 * @see "SISO-REF-10 [UID 381]"
 */
public enum TrailingEffects
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	None  ( (byte)0 ),
	Small ( (byte)1 ),
	Medium( (byte)2 ),
	Large ( (byte)3 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private TrailingEffects( byte value )
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
	public static TrailingEffects fromValue( byte value )
	{
		switch( value )
		{
			case 0: return None;
			case 1: return Small;
			case 2: return Medium;
			case 3: return Large;
			default: throw new IllegalArgumentException( "Invalid Trailing Effects Code: "+value );
		}
	}
}
