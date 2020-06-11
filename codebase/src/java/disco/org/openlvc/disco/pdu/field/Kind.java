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
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;

public enum Kind
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other          ( (short)0 ),
	Platform       ( (short)1 ),
	Munition       ( (short)2 ),
	Lifeform       ( (short)3 ),
	Environmental  ( (short)4 ),
	CulturalFeature( (short)5 ),
	Supply         ( (short)6 ),
	Radio          ( (short)7 ),
	Expendable     ( (short)8 ),
	SensorEmitter  ( (short)9 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Kind( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
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

	public static Kind fromValue( short value )
	{
		switch( value )
		{
			 case 1: return Platform;
			 case 2: return Munition;
			 case 3: return Lifeform;
			 case 4: return Environmental;
			 case 5: return CulturalFeature;
			 case 6: return Supply;
			 case 7: return Radio;
			 case 8: return Expendable;
			 case 9: return SensorEmitter;
			 case 0: return Other;
			default: break;
		}

		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid Kind" );
		else
			return Other;
	}
}
