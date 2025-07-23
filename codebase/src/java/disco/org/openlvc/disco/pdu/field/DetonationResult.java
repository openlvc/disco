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

import org.openlvc.disco.utils.EnumLookup;

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
    private static final EnumLookup<DetonationResult> DISVALUE_LOOKUP = 
    	new EnumLookup<>( DetonationResult.class, DetonationResult::value, DetonationResult.Other );

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
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
