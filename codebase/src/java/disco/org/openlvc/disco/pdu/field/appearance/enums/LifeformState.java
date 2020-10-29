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
 * State appearance values for lifeforms
 * 
 * @see "SISO-REF-10 s4.4.3"
 */
public enum LifeformState
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	NotApplicable       ( (byte)0 ),
	UprightStandingStill( (byte)1 ),
	UprightWalking      ( (byte)2 ),
	UprightRunning      ( (byte)3 ),
	Kneeling            ( (byte)4 ),
	Prone               ( (byte)(5) ),
	Crawling            ( (byte)(6) ),
	Swimming            ( (byte)(7) ),
	Parachuting         ( (byte)(8) ),
	Jumping             ( (byte)(9) ),
	Sitting             ( (byte)(10) ),
	Squatting           ( (byte)(11) ),
	Crouching           ( (byte)(12) ),
	Wading              ( (byte)(13) ),
	Surrender           ( (byte)(14) ),
	Detained            ( (byte)(15) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private LifeformState( byte value )
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
	public static LifeformState fromValue( byte value )
	{
		switch( value )
		{
			case 0:  return NotApplicable;
			case 1:  return UprightStandingStill;
			case 2:  return UprightWalking;
			case 3:  return UprightRunning;
			case 4:  return Kneeling;
			case 5:  return Prone;
			case 6:  return Crawling;
			case 7:  return Swimming;
			case 8:  return Parachuting;
			case 9:  return Jumping;
			case 10: return Sitting;
			case 11: return Squatting;
			case 12: return Crouching;
			case 13: return Wading;
			case 14: return Surrender;
			case 15: return Detained;
			default: throw new IllegalArgumentException( "Invalid Lifeform State Code: "+value );
		}
	}
}
