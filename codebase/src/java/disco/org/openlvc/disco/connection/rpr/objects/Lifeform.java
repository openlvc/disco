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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.connection.rpr.types.enumerated.ComplianceStateEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.enumerated.StanceCodeEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.WeaponStateEnum32;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.appearance.LifeformAppearance;

public abstract class Lifeform extends PhysicalEntity
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected RPRboolean flashLightsOn;
	protected EnumHolder<StanceCodeEnum32> stanceCode;
	protected EnumHolder<WeaponStateEnum32> primaryWeaponState;
	protected EnumHolder<WeaponStateEnum32> secondaryWeaponState;
	protected EnumHolder<ComplianceStateEnum32> complianceState;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Lifeform()
	{
		super();
		
		this.flashLightsOn = new RPRboolean( false );
		this.stanceCode = new EnumHolder<>( StanceCodeEnum32.NotApplicable );
		this.primaryWeaponState = new EnumHolder<>( WeaponStateEnum32.NoWeapon );
		this.secondaryWeaponState = new EnumHolder<>( WeaponStateEnum32.NoWeapon );
		this.complianceState = new EnumHolder<>( ComplianceStateEnum32.Other );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void fromPdu( EntityStatePdu incoming )
	{
		// pass up the tree
		super.fromPdu( incoming );

		LifeformAppearance disAppearance = new LifeformAppearance( incoming.getAppearance() );
		this.flashLightsOn.setValue( disAppearance.isFlashlightOn() );
		this.stanceCode.setEnum( StanceCodeEnum32.valueOf(disAppearance.getStateValue()) );
		this.primaryWeaponState.setEnum( WeaponStateEnum32.valueOf(disAppearance.getPrimaryWeaponStateValue()) );
		this.secondaryWeaponState.setEnum( WeaponStateEnum32.valueOf(disAppearance.getSecondaryWeaponStateValue()) );
		this.complianceState.setEnum( ComplianceStateEnum32.valueOf(disAppearance.getComplianceValue()) );
	}
	
	protected void toPdu( EntityStatePdu pdu )
	{
		// pass up the tree
		super.toPdu( pdu );

		StanceCodeEnum32 stance = this.stanceCode.getEnum();
		WeaponStateEnum32 primaryWeapon = this.primaryWeaponState.getEnum();
		WeaponStateEnum32 secondaryWeapon = this.secondaryWeaponState.getEnum();
		ComplianceStateEnum32 compliance = this.complianceState.getEnum();
		
		LifeformAppearance disAppearance = new LifeformAppearance( pdu.getAppearance() );
		disAppearance.setFlashlightOn( this.flashLightsOn.getValue() );
		disAppearance.setStateValue( (byte)stance.getValue() );
		disAppearance.setPrimaryWeaponStateValue( (byte)primaryWeapon.getValue() );
		disAppearance.setSecondaryWeaponStateValue( (byte)secondaryWeapon.getValue() );
		disAppearance.setComplianceValue( (byte)compliance.getValue() );
		
		pdu.setAppearance( disAppearance.getBits() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
