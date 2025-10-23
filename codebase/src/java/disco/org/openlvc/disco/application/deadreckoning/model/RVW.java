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
package org.openlvc.disco.application.deadreckoning.model;

import org.openlvc.disco.application.deadreckoning.DeadReckoningModel;
import org.openlvc.disco.application.deadreckoning.DrmState;
import org.openlvc.disco.application.deadreckoning.IDeadReckoningEnabled;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.utils.Quaternion;
import org.openlvc.disco.utils.Vec3;

/**
 * An implementation of the 'DRM (RVW)' dead-reckoning model described in 1278.1-2012 Annex E. The
 * standard recommends the use of this model for "Constant velocity (or low acceleration) linear
 * motion (similar to FVW) but where orientation is required (e.g., visual simulation)".
 * <ul>
 * <li>R: Rotating (1st order w.r.t. orientation)</li>
 * <li>V: constant change in Velocity (2nd order w.r.t. position)</li>
 * <li>W: uses World coordinate system</li>
 * </ul>
 */
public class RVW extends DeadReckoningModel
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RVW( IDeadReckoningEnabled object )
	{
		super( object );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public DeadReckoningAlgorithm getAlgorithm()
	{
		return DeadReckoningAlgorithm.RVW;
	}

	@Override
	public DrmState getStateAfter( double dt )
	{
		Vec3 position = new Vec3( this.initialState.position() );
		Vec3 velocity = new Vec3( this.initialState.velocity() );

		// effects due to velocity
		Vec3 vDisp = new Vec3( this.initialState.velocity() );
		vDisp.multiply( dt );

		position.add( vDisp );

		// effects due to acceleration
		// change in velocity
		Vec3 dv = new Vec3( this.initialState.acceleration() );
		dv.multiply( dt );

		// displacement
		Vec3 aDisp = new Vec3( dv );
		aDisp.multiply(dt / 2.0);

		position.add( aDisp );
		velocity.add( dv );

		// update the orientation
		Quaternion rotationQuaternion = this.makeRotationQuaternion( dt );
		Quaternion orientation = this.initialState.orientation().multiply( rotationQuaternion );

		return new DrmState( position,
		                     velocity,
		                     this.initialState.acceleration(),
		                     orientation,
		                     this.initialState.angularVelocity() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
