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
package org.openlvc.disco.application.pdu;

import java.util.Map;

import org.openlvc.disco.application.utils.DrmState;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.LruCache;

/**
 * A logical extension of {@link EntityStatePdu} to include dead-reckoning, with methods for
 * querying the state of the entity at a future timestamp. <br/>
 * <br/>
 * When queries are made for dead-reckoned information at a particular timestamp, stores the
 * computed state in an internal LRU Cache.
 */
public class DrEntityStatePdu extends EntityStatePdu {
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final DrmState initialDrmState;
	private Map<CacheKey,DrmState> drmStateCache; // access must be synchronized
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DrEntityStatePdu( EntityStatePdu pdu, int cacheCapacity )
	{
		super( pdu );

		this.initialDrmState = new DrmState( pdu.getLocation(),
		                                     pdu.getLinearVelocity(),
		                                     pdu.getDeadReckoningParams().getEntityLinearAcceleration(),
		                                     pdu.getOrientation(),
		                                     pdu.getDeadReckoningParams().getEntityAngularVelocity() );

		this.drmStateCache = new LruCache<>( cacheCapacity );
	}

	/**
	 * Uses a capacity of 3 as the default for the DRM State LRU Cache.
	 */
	public DrEntityStatePdu( EntityStatePdu pdu )
	{
		this( pdu, 3 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	protected DrmState getInitialDrmState()
	{
		return this.initialDrmState;
	}

	protected DrmState getDrmStateAtLocalTime( DeadReckoningAlgorithm algorithm, long localTimestamp )
	{
		// ! TODO err if timestamp in the past

		if( this.isFrozen() )
			return this.getInitialDrmState();
		
		CacheKey cacheKey = new CacheKey( algorithm, localTimestamp );

		synchronized( this.drmStateCache )
		{
			DrmState state = this.drmStateCache.get( cacheKey );
			if( state != null )
				return state;

			// hold access to the cache until we have written to it to prevent calculating the same state twice

			double dt_ms = localTimestamp - this.getLocalTimestamp();
			double dt = dt_ms / 1000.0;

			DrmState initialState = this.getInitialDrmState();
			if( algorithm.getReferenceFrame() != this.getDeadReckoningAlgorithm().getReferenceFrame() )
			{
				// handle conversion between body and world coords if the algorithms don't match coord systems
				switch( algorithm.getReferenceFrame() ) // what frame are we switching _to_
				{
					case WorldCoordinates:
						// Body -> World
						initialState = initialState.asWorldCoords();
						break;

					case BodyCoordinates:
						// World -> Body
						initialState = initialState.asBodyCoords();
						break;
				
					default:
					case Other:
						// TODO warn?
						break;
				}
			}

			state = algorithm.computeStateAfter( initialState, dt );
			this.drmStateCache.put( cacheKey, state );
			return state;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public DeadReckoningAlgorithm getDeadReckoningAlgorithm()
	{
		return this.getDeadReckoningParams().getDeadReckoningAlgorithm();
	}

	/**
	 * Gets the position of the entity at the given local timestamp, extrapolated using the
	 * dead-reckoning algorithm of this entity. <br/>
	 * <br/>
	 * Uses the internally cached value for the state at this timestamp, if available, rather than
	 * recomputing the state.
	 * 
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the position of the entity, as a {@link WorldCoordinate}
	 */
	public WorldCoordinate getDrLocation( long localTimestamp )
	{
		return this.getDrLocation( this.getDeadReckoningAlgorithm(), localTimestamp );
	}

	/**
	 * Gets the position of the entity at the given local timestamp, extrapolated using the
	 * specified dead-reckoning algorithm. <br/>
	 * <br/>
	 * Uses the internally cached value for the state at this timestamp (with this algorithm), if
	 * available, rather than recomputing the state.
	 * 
	 * @param algorithm the dead-reckoning algorithm to use
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the position of the entity, as a {@link WorldCoordinate}
	 */
	public WorldCoordinate getDrLocation( DeadReckoningAlgorithm algorithm, long localTimestamp )
	{
		return this.getDrmStateAtLocalTime( algorithm, localTimestamp ).getLocation();
	}

	/**
	 * Gets the velocity of the entity at the given local timestamp, extrapolated using the
	 * dead-reckoning algorithm of this entity. <br/>
	 * <br/>
	 * Uses the internally cached value for the state at this timestamp, if available, rather than
	 * recomputing the state.
	 * 
	 * @return the velocity of the entity, as a {@link VectorRecord}
	 */
	public VectorRecord getDrLinearVelocity( long localTimestamp )
	{
		return this.getDrLinearVelocity( this.getDeadReckoningAlgorithm(), localTimestamp );
	}

	/**
	 * Gets the velocity of the entity at the given local timestamp, extrapolated using the
	 * specified dead-reckoning algorithm. <br/>
	 * <br/>
	 * Uses the internally cached value for the state at this timestamp (with this algorithm), if
	 * available, rather than recomputing the state.
	 * 
	 * @param algorithm the dead-reckoning algorithm to use
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the velocity of the entity, as a {@link VectorRecord}
	 */
	public VectorRecord getDrLinearVelocity( DeadReckoningAlgorithm algorithm, long localTimestamp )
	{
		return this.getDrmStateAtLocalTime( algorithm, localTimestamp ).getLinearVelocity();
	}

	/**
	 * Gets the orientation of the entity at the given local timestamp, extrapolated using the
	 * dead-reckoning algorithm of this entity. <br/>
	 * <br/>
	 * Uses the internally cached value for the state at this timestamp, if available, rather than
	 * recomputing the state.
	 * 
	 * @return the orientation of the entity, as {@link EulerAngles}
	 */
	public EulerAngles getDrOrientation( long localTimestamp )
	{
		return this.getDrOrientation( this.getDeadReckoningAlgorithm(), localTimestamp );
	}

	/**
	 * Gets the orientation of the entity at the given local timestamp, extrapolated using the
	 * specified dead-reckoning algorithm. <br/>
	 * <br/>
	 * Uses the internally cached value for the state at this timestamp (with this algorithm), if
	 * available, rather than recomputing the state.
	 * 
	 * @param algorithm the dead-reckoning algorithm to use
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the orientation of the entity, as a {@link EulerAngles}
	 */
	public EulerAngles getDrOrientation( DeadReckoningAlgorithm algorithm, long localTimestamp )
	{
		return this.getDrmStateAtLocalTime( algorithm, localTimestamp ).getOrientation();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * A record of each value that acts as a key to the dead-reckoning cache.
	 */
	private record CacheKey( DeadReckoningAlgorithm algorithm, long localTimestamp )
	{
	}
}
