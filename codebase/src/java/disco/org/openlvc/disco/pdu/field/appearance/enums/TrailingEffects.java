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

import org.openlvc.disco.utils.EnumLookup;

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
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<TrailingEffects> DISVALUE_LOOKUP = 
		new EnumLookup<>( TrailingEffects.class, TrailingEffects::value );
	
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
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
