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
 * Damage appearance values (not to be confused with other values for mobility and firepower killed.
 * 
 * @see "SISO-REF-10 [UID 379]"
 */
public enum DamageState
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	NoDamage      ( (byte)0 ),
	SlightDamage  ( (byte)1 ),
	ModerateDamage( (byte)2 ),
	Destroyed     ( (byte)3 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private DamageState( byte value )
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
	public static DamageState fromValue( byte value )
	{
		switch( value )
		{
			case 0: return NoDamage;
			case 1: return SlightDamage;
			case 2: return ModerateDamage;
			case 3: return Destroyed;
			default: throw new IllegalArgumentException( "Invalid Damage Code: "+value );
		}
	}
}
