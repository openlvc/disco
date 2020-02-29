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


		throw new RuntimeException( "Not Implemented Yet" );
	}
	
	protected void toPdu( EntityStatePdu pdu )
	{
		// pass up the tree
		super.toPdu( pdu );

		
		throw new RuntimeException( "Not Implemented Yet" );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
