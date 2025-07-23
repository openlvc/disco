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
 * Damage appearance values for objects [UID 405]
 * 
 * @see "SISO-REF-10 s17.11.2.5"
 */
public enum ObjectDamage
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	NoDamage          ( 0 ),
	Damaged           ( 1 ),
	Destroyed         ( 2 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<ObjectDamage> DISVALUE_LOOKUP = 
		new EnumLookup<>( ObjectDamage.class, ObjectDamage::value );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ObjectDamage( int value )
	{
		this.value = (byte)value;
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
	public static ObjectDamage fromValue( byte value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
