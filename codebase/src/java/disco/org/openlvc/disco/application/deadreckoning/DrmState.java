/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.application.deadreckoning;

import org.openlvc.disco.pdu.record.AngularVelocityVector;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.Quaternion;
import org.openlvc.disco.utils.Vec3;

/**
 * Represents the state of an entity at a point in time in a {@link DeadReckoningModel}.
 */
public record DrmState( Vec3 position, Vec3 velocity, Vec3 acceleration,
                        Quaternion orientation, Vec3 angularVelocity )
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DrmState( DrmState state ) {
		this( new Vec3( state.position() ),
		      new Vec3( state.velocity() ),
		      new Vec3( state.acceleration() ),
		      new Quaternion( state.orientation() ),
		      new Vec3( state.angularVelocity() ) );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor Methods ///////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the position of the represented entity in DIS PDU format.
	 * 
	 * @return the position of the entity, as a {@link WorldCoordinate}
	 */
	public WorldCoordinate getLocation()
	{
		return new WorldCoordinate( this.position().x, this.position().y, this.position().z );
	}
	
	/**
	 * Gets the velocity of the represented entity in DIS PDU format.
	 * Uses the coordinate system of the model that produces this state.
	 * 
	 * @return the velocity of the entity, as a {@link VectorRecord}
	 */
	public VectorRecord getLinearVelocity()
	{
		return new VectorRecord( (float)this.velocity().x, (float)this.velocity().y, (float)this.velocity().z );
	}

	/**
	 * Gets the acceleration of the represented entity in DIS PDU format.
	 * Uses the coordinate system of the model that produces this state.
	 * 
	 * @return the acceleration of the entity, as a {@link VectorRecord}
	 */
	public VectorRecord getLinearAcceleration()
	{
		return new VectorRecord( (float)this.acceleration().x, (float)this.acceleration().y, (float)this.acceleration().z );
	}

	/**
	 * Gets the orientation of the represented entity in DIS PDU format.
	 * 
	 * @return the orientation of the entity, as {@link EulerAngles}
	 */
	public EulerAngles getOrientation()
	{
		return this.orientation().toPduEulerAngles();
	}

	/**
	 * Gets the angular velocity of the represented entity in DIS PDU format.
	 * Uses the coordinate system of the model that produces this state.
	 * 
	 * @return the angular velocity of the entity, as an {@link AngularVelocityVector}
	 */
	public AngularVelocityVector getAngularVelocity()
	{
		return new AngularVelocityVector( (float)this.angularVelocity().x, (float)this.angularVelocity().y, (float)this.angularVelocity().z );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	protected static DrmState fromObject( IDeadReckoningEnabled object )
	{
		Vec3 position = new Vec3( object.getLocation() );

		// (currently) assumed to be in global space, actually depends on the model
		VectorRecord linearVelocity = object.getLinearVelocity();
		Vec3 velocity = new Vec3( linearVelocity.getFirstComponent(),
		                          linearVelocity.getSecondComponent(),
		                          linearVelocity.getThirdComponent() );
		
		// (currently) assumed to be in global space, actually depends on the model
		VectorRecord linearAcceleration = object.getDeadReckoningParams().getEntityLinearAcceleration();
		Vec3 acceleration = new Vec3( linearAcceleration.getFirstComponent(),
		                              linearAcceleration.getSecondComponent(),
		                              linearAcceleration.getThirdComponent() );
		
		
		Quaternion orientation = Quaternion.fromPduEulerAngles( object.getOrientation() );
		
		// local space
		AngularVelocityVector angularVelocityVector = object.getDeadReckoningParams().getEntityAngularVelocity();
		Vec3 angularVelocity = new Vec3( angularVelocityVector.getRateAboutXAxis(),
		                                 angularVelocityVector.getRateAboutYAxis(),
		                                 angularVelocityVector.getRateAboutZAxis() );
		
		return new DrmState( position, velocity, acceleration, orientation, angularVelocity );
	}
}
