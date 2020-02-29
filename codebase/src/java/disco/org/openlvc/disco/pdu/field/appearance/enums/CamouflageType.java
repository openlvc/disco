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
 * Camouflage Type appearance values.
 * 
 * @see "SISO-REF-10 [UID 384]"
 */
public enum CamouflageType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Desert ( (byte)0 ),
	Winter ( (byte)1 ),
	Forest ( (byte)2 ),
	Other  ( (byte)3 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private CamouflageType( byte value )
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
	
	public byte rprValue()
	{
		switch( this )
		{
			case Desert: return 1;
			case Winter: return 2;
			case Forest: return 3;
			case Other:  return 4;
			default: return 4;
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static CamouflageType fromValue( byte value )
	{
		switch( value )
		{
			case 0: return Desert;
			case 1: return Winter;
			case 2: return Forest;
			case 3: return Other;
			case 4: return Other; // Stupid DIS/RPR differences
			default: throw new IllegalArgumentException( "Invalid Camouflage Code: "+value );
		}
	}
}
