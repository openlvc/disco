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

import org.openlvc.disco.pdu.field.appearance.enums.Compliance;
import org.openlvc.disco.pdu.field.appearance.enums.HealthState;
import org.openlvc.disco.pdu.field.appearance.enums.LifeformState;
import org.openlvc.disco.pdu.field.appearance.enums.WeaponState;
import org.openlvc.disco.utils.BitField32;

public class LifeformAppearance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// private static final int INDEX_PAINT_SCHEME       = 0;
	private static final int INDEX_HEALTH             = 3;  // 3-4
	private static final int INDEX_COMPLIANCE         = 5;  // 5-8
	private static final int INDEX_FLASHLIGHTS        = 12;
	private static final int INDEX_LIFEFORM_STATE     = 16; // 16-19
	private static final int INDEX_ACTIVE             = 23;
	private static final int INDEX_WEAPON_1           = 24; // 24-25
	private static final int INDEX_WEAPON_2           = 26; // 26-27

	// bit ending values for multi-bit parameters
	private static final int INDEX_HEALTH_END         = 4;  // 3-4
	private static final int INDEX_COMPLIANCE_END     = 5;  // 5-8
	private static final int INDEX_LIFEFORM_STATE_END = 19; // 16-19
	private static final int INDEX_WEAPON_1_END       = 25; // 24-25
	private static final int INDEX_WEAPON_2_END       = 27; // 26-27
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BitField32 bitfield;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LifeformAppearance( int bits )
	{
		this.bitfield = new BitField32( bits );
		
		// set isActive by default
		this.setActive( true );
	}

	public LifeformAppearance()
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
	
	public LifeformAppearance setBits( int bits )
	{
		this.bitfield.setInt( bits );
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//
	// Health (bits 3-4)
	//
	public HealthState getHealth()
	{
		return HealthState.fromValue( getHealthValue() );
	}
	
	public byte getHealthValue()
	{
		return (byte)bitfield.getBits(INDEX_HEALTH, INDEX_HEALTH_END);
	}
	
	public LifeformAppearance setHealth( HealthState health )
	{
		return setHealthValue( health.value() );
	}
	
	public LifeformAppearance setHealthValue( byte value )
	{
		bitfield.setBits( INDEX_HEALTH, INDEX_HEALTH_END, value );
		return this;
	}
	
	//
	// Compliance (bits 5-8)
	//
	public Compliance getCompliance()
	{
		return Compliance.fromValue( getComplianceValue() );
	}
	
	public byte getComplianceValue()
	{
		return (byte)bitfield.getBits(INDEX_COMPLIANCE, INDEX_COMPLIANCE_END);
	}
	
	public LifeformAppearance setCompliance( Compliance compliance )
	{
		return setComplianceValue( compliance.value() );
	}
	
	public LifeformAppearance setComplianceValue( byte value )
	{
		bitfield.setBits( INDEX_COMPLIANCE, INDEX_COMPLIANCE_END, value );
		return this;
	}
	
	//
	// Flash Light (bit 12)
	//
	public boolean isFlashlightOn()
	{
		return bitfield.isSet( INDEX_FLASHLIGHTS );
	}
	
	public void setFlashlightOn( boolean on )
	{
		bitfield.setBit( INDEX_FLASHLIGHTS, on );
	}
	
	//
	// State (bits 16-19)
	//
	public LifeformState getState()
	{
		return LifeformState.fromValue( getStateValue() );
	}
	
	public byte getStateValue()
	{
		return (byte)bitfield.getBits(INDEX_LIFEFORM_STATE, INDEX_LIFEFORM_STATE_END);
	}
	
	public LifeformAppearance setState( LifeformState state )
	{
		return setStateValue( state.value() );
	}
	
	public LifeformAppearance setStateValue( byte value )
	{
		bitfield.setBits( INDEX_LIFEFORM_STATE, INDEX_LIFEFORM_STATE_END, value );
		return this;
	}
	
	//
	// Active (bit 23)
	//
	public boolean isActive()
	{
		return bitfield.isSet( INDEX_ACTIVE );
	}
	
	public void setActive( boolean active )
	{
		bitfield.setBit( INDEX_ACTIVE, active );
	}
	
	//
	// Primary Weapon (bits 24-25)
	//
	public WeaponState getPrimaryWeaponState()
	{
		return WeaponState.fromValue( getPrimaryWeaponStateValue() );
	}
	
	public byte getPrimaryWeaponStateValue()
	{
		return (byte)bitfield.getBits( INDEX_WEAPON_1, INDEX_WEAPON_1_END );
	}
	
	public LifeformAppearance setPrimaryWeaponState( WeaponState state )
	{
		return setPrimaryWeaponStateValue( state.value() );
	}
	
	public LifeformAppearance setPrimaryWeaponStateValue( byte value )
	{
		bitfield.setBits( INDEX_WEAPON_1, INDEX_WEAPON_1_END, value );
		return this;
	}
	
	//
	// Secondary Weapon (bits 26-27)
	//
	public WeaponState getSecondaryWeaponState()
	{
		return WeaponState.fromValue( getSecondaryWeaponStateValue() );
	}
	
	public byte getSecondaryWeaponStateValue()
	{
		return (byte)bitfield.getBits( INDEX_WEAPON_2, INDEX_WEAPON_2_END );
	}
	
	public LifeformAppearance setSecondaryWeaponState( WeaponState state )
	{
		return setSecondaryWeaponStateValue( state.value() );
	}
	
	public LifeformAppearance setSecondaryWeaponStateValue( byte value )
	{
		bitfield.setBits( INDEX_WEAPON_2, INDEX_WEAPON_2_END, value );
		return this;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
