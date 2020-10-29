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
 * Compliance appearance values for lifeforms
 * 
 * @see "SISO-REF-10 s4.4.3"
 */
public enum Compliance
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other             ( (byte)0 ),
	Detained          ( (byte)1 ),
	Surrender         ( (byte)2 ),
	UsingFists        ( (byte)3 ),
	VerbalAbuse1      ( (byte)4 ),
	VerbalAbuse2      ( (byte)(5) ),
	VerbalAbuse3      ( (byte)(6) ),
	PassiveResistance1( (byte)(7) ),
	PassiveResistance2( (byte)(8) ),
	PassiveResistance3( (byte)(9) ),
	NonLethalWeapon1  ( (byte)(10) ),
	NonLethalWeapon2  ( (byte)(11) ),
	NonLethalWeapon3  ( (byte)(12) ),
	NonLethalWeapon4  ( (byte)(13) ),
	NonLethalWeapon5  ( (byte)(14) ),
	NonLethalWeapon6  ( (byte)(15) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Compliance( byte value )
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
	public static Compliance fromValue( byte value )
	{
		switch( value )
		{
			case 0:  return Other;
			case 1:  return Detained;
			case 2:  return Surrender;
			case 3:  return UsingFists;
			case 4:  return VerbalAbuse1;
			case 5:  return VerbalAbuse2;
			case 6:  return VerbalAbuse3;
			case 7:  return PassiveResistance1;
			case 8:  return PassiveResistance2;
			case 9:  return PassiveResistance3;
			case 10: return NonLethalWeapon1;
			case 11: return NonLethalWeapon2;
			case 12: return NonLethalWeapon3;
			case 13: return NonLethalWeapon4;
			case 14: return NonLethalWeapon5;
			case 15: return NonLethalWeapon6;
			default: throw new IllegalArgumentException( "Invalid Compliance Code: "+value );
		}
	}
}
