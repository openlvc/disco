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
import org.openlvc.disco.utils.Vec3;

/**
 * An implementation of the 'DRM (FPW)' dead-reckoning model described in 1278.1-2012 Annex E.
 * The standard recommends the use of this model for "Constant velocity (or low acceleration)
 * linear motion".
 * <ul>
 * <li>F: Fixed rotation (constant orientation)</li>
 * <li>P: constant change in Position (1st order w.r.t. position)</li>
 * <li>W: uses World coordinate system</li>
 * </ul>
 */
public class FPW extends DeadReckoningModel
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
	public FPW( IDeadReckoningEnabled object )
	{
		super( object );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public DeadReckoningAlgorithm getAlgorithm()
	{
		return DeadReckoningAlgorithm.FPW;
	}

	@Override
	public DrmState getStateAfter( double dt )
	{
		// compute the displacement
		Vec3 displacement = new Vec3( this.initialState.velocity() );
		displacement.multiply( dt );

		// apply the displacement
		Vec3 position = new Vec3( this.initialState.position() );
		position.add( displacement );

		return new DrmState( position,
		                     this.initialState.velocity(),
		                     this.initialState.acceleration(),
		                     this.initialState.orientation(),
		                     this.initialState.angularVelocity() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
