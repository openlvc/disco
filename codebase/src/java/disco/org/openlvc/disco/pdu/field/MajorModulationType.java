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

public enum MajorModulationType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0 ),
	Amplitude( 1 ),
	AmplitudeAndAngle( 2 ),
	Angle( 3 ),
	Combination( 4 ),
	Pulse( 5 ),
	Unmodulated( 6 ),
	CPSM( 7 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private MajorModulationType( int value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI16_SIZE;
	}
	
	public static MajorModulationType fromValue( int value )
	{
		for( MajorModulationType type : values() )
			if( type.value == value )
				return type;

		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" is not a valid value for MajorModulationType" );
		else
			return Other;
	}

}
