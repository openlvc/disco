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
package org.openlvc.disco.pdu.custom.field;

import java.util.HashSet;
import java.util.Set;

public enum DcssWeatherDomain
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	InvalidDomain( (byte)0 ),
	Ground( (byte)1 ),
	Cloud( (byte)2 ),
	Atmosphere( (byte)3 ),
	Surface( (byte)4 ),
	Subsurface( (byte)5 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private DcssWeatherDomain( byte value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public byte getValue()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static DcssWeatherDomain valueOf( byte value )
	{
		for( DcssWeatherDomain temp : DcssWeatherDomain.values() )
			if( temp.value == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (DcssWeatherDomain)" );
	}
	
	public static Set<DcssWeatherDomain> fromBitField( byte field )
	{
		Set<DcssWeatherDomain> specified = new HashSet<DcssWeatherDomain>();
		
		for( DcssWeatherDomain domain : DcssWeatherDomain.values() )
		{
			int mask = (int)Math.pow( 2, domain.getValue() );
			boolean isSpecified = (field & mask) != 0;
			if( isSpecified )
				specified.add( domain );
		}
		
		return specified;
	}
	
	public static byte toBitField( Set<DcssWeatherDomain> domains )
	{
		int field = 0;
		for( DcssWeatherDomain domain : domains )
		{
			int mask = (int)Math.pow( 2, domain.getValue() );
			field |= mask;
		}
		
		return (byte)field;
	}
}
