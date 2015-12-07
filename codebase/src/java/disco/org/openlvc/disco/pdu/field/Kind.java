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
		if( value == Platform.value )             return Platform;
		else if( value == Lifeform.value )        return Lifeform;
		else if( value == Munition.value )        return Munition;
		else if( value == Radio.value )           return Radio;
		else if( value == SensorEmitter.value )   return SensorEmitter;
		else if( value == Environmental.value )   return Environmental;
		else if( value == Supply.value )          return Supply;
		else if( value == Expendable.value )      return Expendable;
		else if( value == CulturalFeature.value ) return CulturalFeature;
		else                                      return Other;
	}
}
