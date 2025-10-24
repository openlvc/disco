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

import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.application.deadreckoning.model.FPW;
import org.openlvc.disco.application.deadreckoning.model.FVW;
import org.openlvc.disco.application.deadreckoning.model.RPW;
import org.openlvc.disco.application.deadreckoning.model.RVW;
import org.openlvc.disco.application.deadreckoning.model.Static;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.utils.Quaternion;
import org.openlvc.disco.utils.Vec3;

/**
 * Base class for implementations of the dead-reckoning models described in 1278.1-2012 Annex E.
 */
public abstract class DeadReckoningModel
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected DrmState initialState;
	private double initialTime; // s

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected DeadReckoningModel( IDeadReckoningEnabled object )
	{
		this.initialState = DrmState.fromObject( object );
		this.initialTime = object.getLocalTimestamp() / 1000.0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the state of the dead-reckoning model when extrapolated for the given duration.
	 * 
	 * @param dt time passed, in s
	 * @return a {@link DrmState} with the state of the model after the given duration
	 */
	public abstract DrmState getStateAfter( double dt );

	/**
	 * Returns the state of the dead-reckoning model when extrapolated to the given local
	 * timestamp.
	 * 
	 * @param localTimestamp target timestamp, in ms since epoch
	 * @return a {@link DrmState} with the state of the model at the given time
	 */
	public DrmState getStateAtLocalTime( long localTimestamp )
	{
		double targetTime = localTimestamp / 1000.0;
		double dt = targetTime - this.initialTime;
		return getStateAfter( dt );
	}

	// q_DR in the spec
	protected Quaternion makeRotationQuaternion( double dt )
	{
		Vec3 rotAxis = new Vec3( this.initialState.angularVelocity() ).normalize();

		double halfBeta = (this.initialState.angularVelocity().length() * dt) / 2.0;
		double sinHalfBeta = Math.sin( halfBeta );

		double w = Math.cos( halfBeta );
		double x = rotAxis.x * sinHalfBeta;
		double y = rotAxis.y * sinHalfBeta;
		double z = rotAxis.z * sinHalfBeta;

		return new Quaternion( w, x, y, z );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the DIS value for the algorithm this model uses.
	 * 
	 * @return the {@link DeadReckoningAlgorithm} this model uses
	 */
	public abstract DeadReckoningAlgorithm getAlgorithm();

	/**
	 * Gets a copy of the initial state used by this model.
	 * 
	 * @return a copy of the initial state
	 */
	public DrmState getInitialState()
	{
		// applications shouldn't be modifying the returned drm state, but wrap it to be safe
		return new DrmState( this.initialState );
	}

	/**
	 * Returns the timestamp the model was initialised with.
	 * Does not have a reference point, but most likely ms since epoch rather than DIS timestamp.
	 * 
	 * @return a timestamp with 1 ms increments
	 */
	public long getInitialLocalTimestamp()
	{
		return (long)(this.initialTime * 1000.0);
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Constructs a new dead-reckoning model with the parameters derived from the passed object, including the algorithm.
	 * 
	 * @param object an object that supports dead-reckoning (see: {@link IDeadReckoningEnabled})
	 * @param logger an optional logger for the construction process
	 * @return a {@link DeadReckoningModel} for the given object
	 */
	public static DeadReckoningModel makeDeadReckoningModel( IDeadReckoningEnabled object, Optional<Logger> logger )
	{
		DeadReckoningParameter params = object.getDeadReckoningParams();
		switch( params.getDeadReckoningAlgorithm() )
		{
			case FPW:
				return new FPW( object );

			case RPW:
				return new RPW( object );

			case RVW:
				return new RVW( object );

			case FVW:
				return new FVW( object );

			case FPB:
			case RPB:
			case RVB:
			case FVB:
				if( logger.isPresent() )
					logger.get().error( "Unimplemented dead-reckoning algorithm: %s (%d). Falling back to STATIC.".formatted( params.getDeadReckoningAlgorithm(),
					                                                                                                          params.getDeadReckoningAlgorithm().value() ) );
				return new Static( object );

			default:
			case Other:
				if( logger.isPresent() )
					logger.get().warn( "Unknown dead-reckoning algorithm: %d. Falling back to STATIC.".formatted( params.getDeadReckoningAlgorithm().value() ) );
			case Static:
				return new Static( object );
		}
	}
}
