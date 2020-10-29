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

import org.openlvc.disco.pdu.field.appearance.enums.DamageState;
import org.openlvc.disco.utils.BitField32;

public class CommonPlatformAppearance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	protected static final int INDEX_DAMAGE = 3; // 3-4
	protected static final int INDEX_SMOKING = 5;
	protected static final int INDEX_ENGINE_SMOKING = 6;
	protected static final int INDEX_IS_FLAMING = 15;
	protected static final int INDEX_IS_FROZEN = 21;
	protected static final int INDEX_POWERPLANT = 22;
	protected static final int INDEX_IS_ACTIVE = 23;

	// bit ending values for multi-bit parameters
	protected static final int INDEX_DAMAGE_END = 4; // 3-4
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected BitField32 bitfield;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public CommonPlatformAppearance( int bits )
	{
		this.bitfield = new BitField32( bits );
		
		// set isActive by default
		this.setActive( true );
	}
	
	public CommonPlatformAppearance()
	{
		this( 0 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public int getBits()
	{
		return bitfield.getInt();
	}
	
	public CommonPlatformAppearance setBits( int bits )
	{
		this.bitfield.setInt( bits );
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Damage State (2 bit) Bits 3-4
	//
	public DamageState getDamageState()
	{
		return DamageState.fromValue( (byte)bitfield.getBits(INDEX_DAMAGE,INDEX_DAMAGE_END) );
	}
	
	public short getDamageStateValue()
	{
		return (short)bitfield.getBits( INDEX_DAMAGE, INDEX_DAMAGE_END );
	}

	public CommonPlatformAppearance setDamageState( DamageState state )
	{
		bitfield.setBits( INDEX_DAMAGE, INDEX_DAMAGE_END, state.value() );
		return this;
	}
	
	public CommonPlatformAppearance setDamageState( int state )
	{
		bitfield.setBits( 3, 4, state );
		return this;
	}

	//
	// Smoke Emanating
	//
	public boolean isSmokeEmanating()
	{
		return bitfield.isSet( INDEX_SMOKING );
	}
	
	public CommonPlatformAppearance setSmokeEmanating( boolean value )
	{
		bitfield.setBit( INDEX_SMOKING, value );
		return this;
	}
	
	//
	// Engine Smoking
	//
	public boolean isEngineSmoking()
	{
		return bitfield.isSet( INDEX_ENGINE_SMOKING );
	}
	
	public CommonPlatformAppearance setEngineSmoking( boolean value )
	{
		bitfield.setBit( INDEX_ENGINE_SMOKING, value );
		return this;
	}

	//
	// Is Flaming
	//
	public boolean isFlaming()
	{
		return bitfield.isSet( INDEX_IS_FLAMING );
	}
	
	public CommonPlatformAppearance setFlaming( boolean value )
	{
		bitfield.setBit( INDEX_IS_FLAMING, value );
		return this;
	}

	//
	// Is Frozen
	//
	public boolean isFrozen()
	{
		return bitfield.isSet( INDEX_IS_FROZEN );
	}
	
	public void setFrozen( boolean frozen )
	{
		bitfield.setBit( INDEX_IS_FROZEN, frozen );
	}
	
	//
	// Powerplant On/Off
	//
	public boolean isPowerplantOn()
	{
		return bitfield.isSet( INDEX_POWERPLANT );
	}
	
	public CommonPlatformAppearance setPowerplantOn( boolean on )
	{
		bitfield.setBit( INDEX_POWERPLANT, on );
		return this;
	}

	//
	// Is Active
	//
	public boolean isActive()
	{
		return bitfield.isSet( INDEX_IS_ACTIVE );
	}
	
	public CommonPlatformAppearance setActive( boolean value )
	{
		bitfield.setBit( INDEX_IS_ACTIVE, value );
		return this;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
