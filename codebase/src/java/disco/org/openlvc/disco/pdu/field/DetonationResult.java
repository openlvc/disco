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

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;

public enum DetonationResult
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other                               ( (short)0 ),
	EntityImpact                        ( (short)1 ),
	EntityProximateDetonation           ( (short)2 ),
	GroundImpact                        ( (short)3 ),
	GroundProximateDetonation           ( (short)4 ),
	Detonation                          ( (short)5 ),
	None                                ( (short)6 ),
	HeHitSmall                          ( (short)7 ),
	HeHitMedium                         ( (short)8 ),
	HeHitLarge                          ( (short)9 ),
	ArmorpiercingHit                    ( (short)10 ),
	DirtBlastSmall                      ( (short)11 ),
	DirtBlastMedium                     ( (short)12 ),
	DirtBlastLarge                      ( (short)13 ),
	WaterBlastSmall                     ( (short)14 ),
	WaterBlastMedium                    ( (short)15 ),
	WaterBlastLarge                     ( (short)16 ),
	AirHit                              ( (short)17 ),
	BuildingHitSmall                    ( (short)18 ),
	BuildingHitMedium                   ( (short)19 ),
	BuildingHitLarge                    ( (short)20 ),
	MineclearingLineCharge              ( (short)21 ),
	EnvironmentObjectImpact             ( (short)22 ),
	EnvironmentObjectProximateDetonation( (short)23 ),
	WaterImpact                         ( (short)24 ),
	AirBurst                            ( (short)25 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final Map<Short,DetonationResult> CACHE = Arrays.stream( DetonationResult.values() )
	                                                               .collect( Collectors.toMap(DetonationResult::value, 
	                                                                                          result -> result) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private DetonationResult( short value )
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
	public static DetonationResult fromValue( short value )
	{
		DetonationResult temp = CACHE.get( value );
		if( temp != null )
			return temp;

		// Missing
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid Detonation Result" );
		else
			return Other;
	}
}
