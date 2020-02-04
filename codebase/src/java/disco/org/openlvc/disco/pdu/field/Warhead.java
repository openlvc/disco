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

/**
 * The warhead shall be specified by a 16-bit enumeration.
 * 
 * @see "Section 5 in EBV-DOC"
 */
public enum Warhead
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0000 ),
	Cargo( 0010 ),
	FuelAirExplosive( 0020 ),
	GlassBlads( 0030 ),
	OneUm( 0031 ),
	FiveUm( 0032 ),
	TenUm( 0033 ),
	HighExplosive( 1000 ),
	HePlastic( 1100 ),
	HeIncendiary( 1200 ),
	HeFragmentation( 1300 ),
	HeAntitank( 1400 ),
	HeBomblets( 1500 ),
	HeShapedCharge( 1600 ),
	HeContinuousRod( 1610 ),
	HeTungstenBall( 1615 ),
	HeBlastFragmentation( 1620 ),
	HeSteerableDartsWithHE( 1625 ),
	HeDarts( 1630 ),
	HeFlechettes( 1635 ),
	HeDirectedFragmentation( 1640 ),
	HeSemiarmorPiercing( 1645 ),
	HeShapedChargeFragmentation( 1650 ),
	HeSemiarmorPiercingFragmentation( 1655 ),
	HeHallowCharge( 1660 ),
	HeDoubleHallowCharge( 1665 ),
	HeGeneralPurpose( 1670 ),
	HeBlastPenetrator( 1675 ),
	HeRodPenetrator( 1680 ),
	HeAntipersonnel( 1685 ),
	Smoke( 2000 ),
	Illumination( 3000 ),
	Practice( 4000 ),
	Kinetic( 5000 ),
	Mines( 6000 ),
	Nuclear( 7000 ),
	NuclearIMT( 7010 ),
	ChemicalGeneral( 8000 ),
	ChemicalBlisterAgent( 8100 ),
	HD( 8110 ),
	ThickenedHD( 8115 ),
	DustyHD( 8120 ),
	ChemicalBloodAgent( 8200 ),
	AC( 8210 ),
	CK( 8215 ),
	CG( 8220 ),
	ChemicalNerveAgent( 8300 ),
	VX( 8310 ),
	ThickenedVX( 8315 ),
	DustyVX( 8320 ),
	GA( 8325 ),
	ThickenedGA( 8330 ),
	DustyGA( 8335 ),
	GB( 8340 ),
	ThickenedGB( 8345 ),
	DustyGB( 8350 ),
	GD( 8355 ),
	ThickenedGD( 8360 ),
	DustyGD( 8365 ),
	GF( 8370 ),
	ThickenedGF( 8375 ),
	DustyGF( 8380 ),
	Biological( 9000 ),
	BiologicalVirus( 9100 ),
	BiologicalBacteria( 9200 ),
	BiologicalRickettsia( 9300 ),
	BiologicalGeneticallyModifiedMicroorganisms( 9400 ),
	BiologicalToxin( 9500 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// fast lookup for types with lots of options
	private static final Map<Integer,Warhead> CACHE = Arrays.stream( Warhead.values() )
	                                                        .collect( Collectors.toMap(Warhead::value, 
	                                                                                   warhead -> warhead) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Warhead( int value )
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
	public static Warhead fromValue( int value )
	{
		Warhead result = CACHE.get( value );
		if( result != null )
			return result;

		// Missing
		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" not a valid Warhead number" );
		else
			return Other;
	}
}
