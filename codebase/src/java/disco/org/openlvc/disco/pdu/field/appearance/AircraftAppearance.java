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
package org.openlvc.disco.pdu.field.appearance;

/**
 * This class will extract the Aircraft Apperance bits from a given 32-bit integer
 * are received in an {@link EntityStatePdu}. Check EBV Section 17.11.1.1.
 */
public class AircraftAppearance extends CommonPlatformAppearance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int INDEX_AFTERBURNER = 16;
	private static final int INDEX_ANTI_COLL_LIGHTS = 14;
	private static final int INDEX_FORMATION_LIGHTS = 24;
	private static final int INDEX_INTERIOR_LIGHTS = 29;
	private static final int INDEX_LANDING_LIGHTS = 12;
	private static final int INDEX_NAVIGATION_LIGHTS = 13;
	private static final int INDEX_SPOT_LIGHTS = 28;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AircraftAppearance( int bits )
	{
		super( bits );
	}
	
	public AircraftAppearance()
	{
		this( 0 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public AircraftAppearance setBits( int bits )
	{
		super.setBits( bits );
		return this;
	}
	
	//
	// Afterburner On
	//
	public boolean isAfterburnerOn()
	{
		return bitfield.isSet( INDEX_AFTERBURNER );
	}

	public AircraftAppearance setAfterburnerOn( boolean value )
	{
		super.bitfield.setBit( INDEX_AFTERBURNER, value );
		return this;
	}

	//
	// AntiCollisionLights On
	//
	public boolean isAntiCollisionLightsOn()
	{
		return bitfield.isSet( INDEX_ANTI_COLL_LIGHTS );
	}

	public AircraftAppearance setAntiCollisionLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_ANTI_COLL_LIGHTS, value );
		return this;
	}

	//
	// FormationLights On
	//
	public boolean isFormationLightsOn()
	{
		return bitfield.isSet( INDEX_FORMATION_LIGHTS );
	}

	public AircraftAppearance setFormationLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_FORMATION_LIGHTS, value );
		return this;
	}

	//
	// InteriorLights On
	//
	public boolean isInteriorLightsOn()
	{
		return bitfield.isSet( INDEX_INTERIOR_LIGHTS );
	}

	public AircraftAppearance setInteriorLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_INTERIOR_LIGHTS, value );
		return this;
	}

	//
	// LandingLights On
	//
	public boolean isLandingLightsOn()
	{
		return bitfield.isSet( INDEX_LANDING_LIGHTS );
	}

	public AircraftAppearance setLandingLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_LANDING_LIGHTS, value );
		return this;
	}

	//
	// NavigationLights On
	//
	public boolean isNavigationLightsOn()
	{
		return bitfield.isSet( INDEX_NAVIGATION_LIGHTS );
	}

	public AircraftAppearance setNavigationLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_NAVIGATION_LIGHTS, value );
		return this;
	}

	//
	// Spotlights On
	//
	public boolean isSpotLightsOn()
	{
		return bitfield.isSet( INDEX_SPOT_LIGHTS );
	}

	public AircraftAppearance setSpotLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_SPOT_LIGHTS, value );
		return this;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
