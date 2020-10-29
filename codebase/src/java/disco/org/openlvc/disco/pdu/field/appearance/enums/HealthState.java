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
 * Health appearance values
 * 
 * @see "SISO-REF-10 s4.4.3"
 */
public enum HealthState
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	NoInjury      ( (byte)0 ),
	SlightInjury  ( (byte)1 ),
	ModerateInjury( (byte)2 ),
	FatalInjury   ( (byte)3 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private HealthState( byte value )
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
	public static HealthState fromValue( byte value )
	{
		switch( value )
		{
			case 0: return NoInjury;
			case 1: return SlightInjury;
			case 2: return ModerateInjury;
			case 3: return FatalInjury;
			default: throw new IllegalArgumentException( "Invalid Health Code: "+value );
		}
	}
}
