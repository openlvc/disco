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

import org.openlvc.disco.pdu.field.appearance.enums.CamouflageType;
import org.openlvc.disco.pdu.field.appearance.enums.HatchState;
import org.openlvc.disco.pdu.field.appearance.enums.PaintScheme;
import org.openlvc.disco.pdu.field.appearance.enums.TrailingEffects;

/**
 * This class will extract the Aircraft Apperance bits from a given 32-bit integer
 * are received in an {@link EntityStatePdu}. Check EBV Section 17.11.1.1.
 */
public class GroundPlatformAppearance extends CommonAppearance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int INDEX_PAINT_SCHEME = 0;
	private static final int INDEX_MKILL = 1;
	private static final int INDEX_FIREPOWER_KILLED = 2;
	// (inherited) INDEX_DAMAGE_STATE = 3-4
	// (inherited) INDEX_SMOKING = 5
	// (inherited) INDEX_ENGINE_SMOKE = 6
	private static final int INDEX_DUST_CLOUD = 7; // 7-8
	private static final int INDEX_HATCH_STATE = 9; // 9-11
	private static final int INDEX_HEAD_LIGHTS = 12;
	private static final int INDEX_TAIL_LIGHTS = 13;
	private static final int INDEX_BRAKE_LIGHTS = 14;
	// (inherited) INDEX_IS_FLAMING = 15;
	private static final int INDEX_LAUNCHER_RAISED = 16;
	private static final int INDEX_CAMOUFLAGE_TYPE = 17; // 17-18
	private static final int INDEX_IS_CONCEALED = 19;
	// (empty) Bit 20
	// (inherited) INDEX_IS_FROZEN = 21;
	// (inherited) INDEX_POWERPLANT = 22;
	// (inherited) INDEX_IS_ACTIVE = 23;
	private static final int INDEX_TENT_EXTENDED = 24;
	private static final int INDEX_RAMP_DEPLOYED = 25;
	private static final int INDEX_BLACKOUT_LIGHTS = 26;
	private static final int INDEX_BLACKOUT_BREAK_LIGHTS = 27;
	private static final int INDEX_SPOT_LIGHTS = 28;
	private static final int INDEX_INTERIOR_LIGHTS = 29;
	private static final int INDEX_OCCUPANTS_SURRENDERED = 30;
	private static final int INDEX_IS_MASKED = 31; // Masked/Cloaked
	
	// End values for multi-byte spanning
	private static final int INDEX_DUST_CLOUD_END = 8;
	private static final int INDEX_HATCH_STATE_END = 11;
	private static final int INDEX_CAMOUFLAGE_TYPE_END = 18;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public GroundPlatformAppearance( int bits )
	{
		super( bits );
	}
	
	public GroundPlatformAppearance()
	{
		this( 0 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public GroundPlatformAppearance setBits( int bits )
	{
		super.setBits( bits );
		return this;
	}
	
	//
	// Paint Scheme
	//
	public PaintScheme getPaintScheme()
	{
		return bitfield.isSet(INDEX_PAINT_SCHEME) ? PaintScheme.Camouflage : PaintScheme.UniformColor;
	}
	
	public byte getPaintSchemeValue()
	{
		return bitfield.isSet(INDEX_PAINT_SCHEME) ? (byte)1 : (byte)0;
	}
	
	public GroundPlatformAppearance setPaintScheme( PaintScheme scheme )
	{
		if( scheme == PaintScheme.UniformColor )
			bitfield.setBit( INDEX_PAINT_SCHEME, false );
		else
			bitfield.setBit( INDEX_PAINT_SCHEME, true );

		return this;
	}
	
	public GroundPlatformAppearance setPaintScheme( byte scheme )
	{
		if( scheme == 0 )
			bitfield.setBit( INDEX_PAINT_SCHEME, false );
		else
			bitfield.setBit( INDEX_PAINT_SCHEME, true );
		
		return this;
	}
	
	//
	// Mobility Killed
	//
	public boolean isMobilityKilled()
	{
		return bitfield.isSet( INDEX_MKILL );
	}

	public GroundPlatformAppearance setMobilityKilled( boolean mkill )
	{
		bitfield.setBit( INDEX_MKILL, mkill );
		return this;
	}

	//
	// Firepower Killed
	//
	public boolean isFirepowerKilled()
	{
		return bitfield.isSet( INDEX_FIREPOWER_KILLED );
	}

	public GroundPlatformAppearance setFirepowerKilled( boolean value )
	{
		bitfield.setBit( INDEX_FIREPOWER_KILLED, value );
		return this;
	}

	//
	// Trailing Dust (2 bit) 7-8
	//
	public TrailingEffects getDustTrail()
	{
		return TrailingEffects.fromValue( (byte)bitfield.getBits(INDEX_DUST_CLOUD,INDEX_DUST_CLOUD_END) );
	}
	
	public byte getDustTrailValue()
	{
		return (byte)bitfield.getBits( INDEX_DUST_CLOUD, INDEX_DUST_CLOUD_END );
	}
	
	public GroundPlatformAppearance setDustTrail( TrailingEffects effects )
	{
		bitfield.setBits( INDEX_DUST_CLOUD, INDEX_DUST_CLOUD_END, effects.value() );
		return this;
	}
	
	public GroundPlatformAppearance setDustTrail( byte value )
	{
		bitfield.setBits( INDEX_DUST_CLOUD, INDEX_DUST_CLOUD_END, value );
		return this;
	}
	
	//
	// Hatch Raised
	//
	public HatchState getHatchState()
	{
		return HatchState.fromValue( (byte)bitfield.getBits(INDEX_HATCH_STATE,INDEX_HATCH_STATE_END) );
	}

	public short getHatchStateValue()
	{
		return (short)bitfield.getBits( INDEX_HATCH_STATE, INDEX_HATCH_STATE_END );
	}

	public GroundPlatformAppearance setHatchState( HatchState state )
	{
		bitfield.setBits( INDEX_HATCH_STATE, INDEX_HATCH_STATE_END, state.value() );
		return this;
	}
	
	public GroundPlatformAppearance setHatchState( int state )
	{
		bitfield.setBits( INDEX_HATCH_STATE, INDEX_HATCH_STATE_END, state );
		return this;
	}

	//
	// HeadLights On
	//
	public boolean isHeadLightsOn()
	{
		return bitfield.isSet( INDEX_HEAD_LIGHTS );
	}

	public GroundPlatformAppearance setHeadLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_HEAD_LIGHTS, value );
		return this;
	}

	
	//
	// TailLights On
	//
	public boolean isTailLightsOn()
	{
		return bitfield.isSet( INDEX_TAIL_LIGHTS );
	}

	public GroundPlatformAppearance setTailLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_TAIL_LIGHTS, value );
		return this;
	}

	//
	// BreakLights On
	//
	public boolean isBreakLightsOn()
	{
		return bitfield.isSet( INDEX_BRAKE_LIGHTS );
	}

	public GroundPlatformAppearance setBreakLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_BRAKE_LIGHTS, value );
		return this;
	}

	//
	// Launcher Raised
	//
	public boolean isLauncherRaised()
	{
		return bitfield.isSet( INDEX_LAUNCHER_RAISED );
	}

	public GroundPlatformAppearance setLauncherRaised( boolean value )
	{
		super.bitfield.setBit( INDEX_LAUNCHER_RAISED, value );
		return this;
	}

	//
	// Camo Type (2 bit) 17-18
	//
	public CamouflageType getCamouflageType()
	{
		return CamouflageType.fromValue( (byte)bitfield.getBits(INDEX_CAMOUFLAGE_TYPE,
		                                                        INDEX_CAMOUFLAGE_TYPE_END) );
	}

	public int getCamouflageTypeValue()
	{
		return bitfield.getBits( INDEX_CAMOUFLAGE_TYPE, INDEX_CAMOUFLAGE_TYPE_END );
	}
	
	public GroundPlatformAppearance setCamouflageType( CamouflageType type )
	{
		bitfield.setBits( INDEX_CAMOUFLAGE_TYPE, INDEX_CAMOUFLAGE_TYPE_END, type.value() );
		return this;
	}
	
	public GroundPlatformAppearance setCamouflageType( int value )
	{
		bitfield.setBits( INDEX_CAMOUFLAGE_TYPE, INDEX_CAMOUFLAGE_TYPE_END, value );
		return this;
	}

	//
	// Is Concealed
	//
	public boolean isConcealed()
	{
		return bitfield.isSet( INDEX_IS_CONCEALED );
	}
	
	public GroundPlatformAppearance setConcealed( boolean value )
	{
		bitfield.setBit( INDEX_IS_CONCEALED, value );
		return this;
	}

	//
	// Tent Extended
	//
	public boolean isTentExtended()
	{
		return bitfield.isSet( INDEX_TENT_EXTENDED );
	}
	
	public GroundPlatformAppearance setTentExtended( boolean value )
	{
		bitfield.setBit( INDEX_TENT_EXTENDED, value );
		return this;
	}
	
	//
	// Ramp Deployed
	//
	public boolean isRampDeployed()
	{
		return bitfield.isSet( INDEX_RAMP_DEPLOYED );
	}

	public GroundPlatformAppearance setRampDeployed( boolean value )
	{
		super.bitfield.setBit( INDEX_RAMP_DEPLOYED, value );
		return this;
	}

	//
	// BlackoutLights On
	//
	public boolean isBlackOutLightsOn()
	{
		return bitfield.isSet( INDEX_BLACKOUT_LIGHTS );
	}

	public GroundPlatformAppearance setBlackOutLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_BLACKOUT_LIGHTS, value );
		return this;
	}

	//
	// BlackoutBreakLights On
	//
	public boolean isBlackoutBreakLightsOn()
	{
		return bitfield.isSet( INDEX_BLACKOUT_BREAK_LIGHTS );
	}

	public GroundPlatformAppearance setBlackoutBreakLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_BLACKOUT_BREAK_LIGHTS, value );
		return this;
	}

	//
	// SpotLights On
	//
	public boolean isSpotLightsOn()
	{
		return bitfield.isSet( INDEX_SPOT_LIGHTS );
	}

	public GroundPlatformAppearance setSpotLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_SPOT_LIGHTS, value );
		return this;
	}

	//
	// InteriorLights On
	//
	public boolean isInteriorLightsOn()
	{
		return bitfield.isSet( INDEX_INTERIOR_LIGHTS );
	}

	public GroundPlatformAppearance setInteriorLightsOn( boolean value )
	{
		super.bitfield.setBit( INDEX_INTERIOR_LIGHTS, value );
		return this;
	}

	//
	// Occupants Surrendered
	//
	public boolean isOccupantsSurrendered()
	{
		return bitfield.isSet( INDEX_OCCUPANTS_SURRENDERED );
	}
	
	public GroundPlatformAppearance setOccupantsSurrendered( boolean value )
	{
		bitfield.setBit( INDEX_OCCUPANTS_SURRENDERED, value );
		return this;
	}
	
	//
	// Is Masked
	//
	public boolean isMasked()
	{
		return bitfield.isSet( INDEX_IS_MASKED );
	}
	
	public GroundPlatformAppearance setMasked( boolean value )
	{
		bitfield.setBit( INDEX_IS_MASKED, value );
		return this;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
