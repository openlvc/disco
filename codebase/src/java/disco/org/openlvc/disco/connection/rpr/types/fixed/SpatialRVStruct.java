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
package org.openlvc.disco.connection.rpr.types.fixed;

import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.pdu.entity.EntityStatePdu;

public class SpatialRVStruct extends AbstractSpatialStruct
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private WorldLocationStruct worldLocation;
	private RPRboolean isFrozen;
	private OrientationStruct orientation;
	private VelocityVectorStruct linearVelocity;
	private AccelerationVectorStruct acceleration;
	private AngularVelocityVectorStruct angularVelocity;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SpatialRVStruct()
	{
		this.worldLocation = new WorldLocationStruct();
		this.isFrozen = new RPRboolean( false );
		this.orientation = new OrientationStruct();
		this.linearVelocity = new VelocityVectorStruct();
		this.acceleration = new AccelerationVectorStruct();
		this.angularVelocity = new AngularVelocityVectorStruct();
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( worldLocation );
		super.add( isFrozen );
		super.add( orientation );
		super.add( linearVelocity );
		super.add( acceleration );
		super.add( angularVelocity );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SpatialRVStruct fromPdu( EntityStatePdu pdu )
	{
		this.worldLocation.setValue( pdu.getLocation() );
		this.isFrozen.setValue( pdu.isFrozen() );
		this.orientation.setValue( pdu.getOrientation() );
		this.linearVelocity.setValue( pdu.getLinearVelocity() );
		this.acceleration.setValue( pdu.getDeadReckoningParams().getEntityLinearAcceleration() );
		this.angularVelocity.setValue( pdu.getDeadReckoningParams().getEntityAngularVelocity() );
		return this;
	}

	@Override
	public void toPdu( EntityStatePdu pdu )
	{
		pdu.setLocation( worldLocation.getDisValue() );
		pdu.setFrozen( isFrozen.getValue() );
		pdu.setOrientation( orientation.getDisValue() );
		pdu.setLinearVelocity( linearVelocity.getDisValue() );
		pdu.getDeadReckoningParams().setEntityLinearAcceleration( acceleration.getDisValue() );
		pdu.getDeadReckoningParams().setEntityAngularVelocity( angularVelocity.getDisValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
