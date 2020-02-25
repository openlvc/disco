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

import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.pdu.entity.EntityStatePdu;

public abstract class Platform extends PhysicalEntity
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected EnumHolder<RPRboolean> afterburnerOn;
	protected EnumHolder<RPRboolean> antiCollisionLightsOn;
	protected EnumHolder<RPRboolean> blackOutBrakeLightsOn;
	protected EnumHolder<RPRboolean> blackOutLightsOn;
	protected EnumHolder<RPRboolean> brakeLightsOn;
	protected EnumHolder<RPRboolean> formationLightsOn;
	protected EnumHolder<RPRboolean> hatchState;
	protected EnumHolder<RPRboolean> headLightsOn;
	protected EnumHolder<RPRboolean> interiorLightsOn;
	protected EnumHolder<RPRboolean> landingLightsOn;
	protected EnumHolder<RPRboolean> launcherRaised;
	protected EnumHolder<RPRboolean> navigationLightsOn;
	protected EnumHolder<RPRboolean> rampDeployed;
	protected EnumHolder<RPRboolean> runningLightsOn;
	protected EnumHolder<RPRboolean> spotLightsOn;
	protected EnumHolder<RPRboolean> tailLightsOn;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Platform()
	{
		super();
		
		this.afterburnerOn = new EnumHolder<>( RPRboolean.False );
		this.antiCollisionLightsOn = new EnumHolder<>( RPRboolean.False );
		this.blackOutBrakeLightsOn = new EnumHolder<>( RPRboolean.False );
		this.blackOutLightsOn = new EnumHolder<>( RPRboolean.False );
		this.brakeLightsOn = new EnumHolder<>( RPRboolean.False );
		this.formationLightsOn = new EnumHolder<>( RPRboolean.False );
		this.hatchState = new EnumHolder<>( RPRboolean.False );
		this.headLightsOn = new EnumHolder<>( RPRboolean.False );
		this.interiorLightsOn = new EnumHolder<>( RPRboolean.False );
		this.landingLightsOn = new EnumHolder<>( RPRboolean.False );
		this.launcherRaised = new EnumHolder<>( RPRboolean.False );
		this.navigationLightsOn = new EnumHolder<>( RPRboolean.False );
		this.rampDeployed = new EnumHolder<>( RPRboolean.False );
		this.runningLightsOn = new EnumHolder<>( RPRboolean.False );
		this.spotLightsOn = new EnumHolder<>( RPRboolean.False );
		this.tailLightsOn = new EnumHolder<>( RPRboolean.False );
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
