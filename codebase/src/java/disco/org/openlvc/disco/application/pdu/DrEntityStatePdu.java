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
public class DrEntityStatePdu extends EntityStatePdu
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final DrmState initialDrmState;
	private LruCache<CacheKey,DrmState> drmStateCache; // access must be synchronized
	private OutdatedTimestampBehavior outdatedTimestampBehavior;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DrEntityStatePdu( EntityStatePdu pdu )
	{
		super( pdu );

		this.initialDrmState = new DrmState( pdu.getLocation(),
		                                     pdu.getLinearVelocity(),
		                                     pdu.getDeadReckoningParams().getEntityLinearAcceleration(),
		                                     pdu.getOrientation(),
		                                     pdu.getDeadReckoningParams().getEntityAngularVelocity() );

		this.setDrmStateCacheSize( 3 );
		this.setOutdatedTimestampBehavior( OutdatedTimestampBehavior.ERROR );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	protected DrmState getInitialDrmState()
	{
		return this.initialDrmState;
	}

	protected DrmState getDrmStateAtLocalTime( DeadReckoningAlgorithm algorithm,
	                                           long localTimestamp )
	    throws IllegalArgumentException
	{
		if( this.getLocalTimestamp() > localTimestamp )
		{
			// Outdated dead-reckoning request - might happen if a new EntityStatePDU comes in
			// before another packet has finished being processed
			switch( this.getOutdatedTimestampBehavior() )
			{
				case USE_CURRENT_STATE:
					localTimestamp = this.getLocalTimestamp();
					break;

				case ERROR:
					throw new IllegalArgumentException( String.format("Timestamp in dead-reckoning query too old. Expected %s or newer, got %s.",
					                                                  this.getLocalTimestamp(),
					                                                  localTimestamp) );
			}
		}

		if( this.isFrozen() || this.getLocalTimestamp() == localTimestamp )
			return this.getInitialDrmState();

		CacheKey cacheKey = new CacheKey( algorithm, localTimestamp );

		synchronized( this.drmStateCache )
		{
			DrmState state = this.drmStateCache.get( cacheKey );
			if( state != null )
				return state;

			// Hold access to the cache until we have written to it to prevent calculating the
			// same state twice

			double dt = (localTimestamp - this.getLocalTimestamp()) / 1000d; // seconds

			DrmState initialState = this.getInitialDrmState();
			if( algorithm.getReferenceFrame() != this.getDefaultDeadReckoningAlgorithm().getReferenceFrame() )
			{
				// Handle conversion between body and world coords if the algorithms don't match
				// coord systems
				initialState = switch( algorithm.getReferenceFrame() ) // what frame are we switching _to_
				{
					// Body -> World
					case WORLD_COORDINATES -> initialState.asWorldCoords();

					// World -> Body
					case BODY_COORDINATES -> initialState.asBodyCoords();

					// Unknown
					// TODO warn?
					case OTHER -> initialState;
				};
			}

			state = algorithm.computeStateAfter( initialState, dt );
			this.drmStateCache.put( cacheKey, state );
			return state;
		}
	}

	//==========================================================================================
	//------------------------------ Accessor and Mutator Methods ------------------------------
	//==========================================================================================
	/**
	 * Sets the behavior when performing a dead-reckoning request with a timestamp older than the
	 * {@link EntityStatePdu}.
	 * 
	 * @param behavior the new {@link OutdatedTimestampBehavior} to use when processing a
	 *            dead-reckoning request with an outdated timestamp
	 */
	public synchronized void setOutdatedTimestampBehavior( OutdatedTimestampBehavior behavior )
	{
		this.outdatedTimestampBehavior = behavior;
	}

	/**
	 * Gets the current {@link OutdatedTimestampBehavior} used when processing a dead-reckoning
	 * request with an outdated timestamp.
	 * 
	 * @return the current {@link OutdatedTimestampBehavior}
	 */
	public synchronized OutdatedTimestampBehavior getOutdatedTimestampBehavior()
	{
		return this.outdatedTimestampBehavior;
	}

	/**
	 * Updates the capacity for the internal LRU cache used for dead-reckoning request results
	 * from the default capacity of 3.
	 */
	public synchronized void setDrmStateCacheSize( int capacity )
	{
		this.drmStateCache = new LruCache<>( capacity );
	}

	/**
	 * Gets the current capacity for the internal LRU cache used for dead-reckoning request results.
	 */
	public synchronized int getDrmStateCacheSize()
	{
		return this.drmStateCache.getCapacity();
	}

	/**
	 * Returns the dead-reckoning algorithm specified for use by the {@link EntityStatePdu}.
	 * 
	 * @return the {@link DeadReckoningAlgorithm} for this {@link DrEntityStatePdu}
	 */
	public DeadReckoningAlgorithm getDefaultDeadReckoningAlgorithm()
	{
		return this.getDeadReckoningParams().getDeadReckoningAlgorithm();
	}

	/**
	 * Gets the position of the entity at the given local timestamp, extrapolated using the
	 * dead-reckoning algorithm of this entity.
	 * <p/>
	 * Uses the internally cached value for the state at this timestamp, if available, rather than
	 * recomputing the state.
	 * 
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the position of the entity, as a {@link WorldCoordinate}
	 * @throws IllegalArgumentException if the given timestamp is older than the
	 *             {@link EntityStatePdu}
	 * @see #setOutdatedTimestampBehavior(OutdatedTimestampBehavior)
	 */
	public WorldCoordinate getDrLocation( long localTimestamp ) throws IllegalArgumentException
	{
		return this.getDrLocation( this.getDefaultDeadReckoningAlgorithm(), localTimestamp );
	}

	/**
	 * Gets the position of the entity at the given local timestamp, extrapolated using the
	 * specified dead-reckoning algorithm.
	 * <p/>
	 * Uses the internally cached value for the state at this timestamp (with this algorithm), if
	 * available, rather than recomputing the state.
	 * 
	 * @param algorithm the dead-reckoning algorithm to use
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the position of the entity, as a {@link WorldCoordinate}
	 * @throws IllegalArgumentException if the given timestamp is older than the
	 *             {@link EntityStatePdu}
	 * @see #setOutdatedTimestampBehavior(OutdatedTimestampBehavior)
	 */
	public WorldCoordinate getDrLocation( DeadReckoningAlgorithm algorithm, long localTimestamp )
	    throws IllegalArgumentException
	{
		return this.getDrmStateAtLocalTime( algorithm, localTimestamp ).getLocation();
	}

	/**
	 * Gets the velocity of the entity at the given local timestamp, extrapolated using the
	 * dead-reckoning algorithm of this entity.
	 * <p/>
	 * Uses the internally cached value for the state at this timestamp, if available, rather than
	 * recomputing the state.
	 * 
	 * @return the velocity of the entity, as a {@link VectorRecord}
	 * @throws IllegalArgumentException if the given timestamp is older than the
	 *             {@link EntityStatePdu}
	 * @see #setOutdatedTimestampBehavior(OutdatedTimestampBehavior)
	 */
	public VectorRecord getDrLinearVelocity( long localTimestamp ) throws IllegalArgumentException
	{
		return this.getDrLinearVelocity( this.getDefaultDeadReckoningAlgorithm(), localTimestamp );
	}

	/**
	 * Gets the velocity of the entity at the given local timestamp, extrapolated using the
	 * specified dead-reckoning algorithm.
	 * <p/>
	 * Uses the internally cached value for the state at this timestamp (with this algorithm), if
	 * available, rather than recomputing the state.
	 * 
	 * @param algorithm the dead-reckoning algorithm to use
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the velocity of the entity, as a {@link VectorRecord}
	 * @throws IllegalArgumentException if the given timestamp is older than the
	 *             {@link EntityStatePdu}
	 * @see #setOutdatedTimestampBehavior(OutdatedTimestampBehavior)
	 */
	public VectorRecord getDrLinearVelocity( DeadReckoningAlgorithm algorithm,
	                                         long localTimestamp )
	    throws IllegalArgumentException
	{
		return this.getDrmStateAtLocalTime( algorithm, localTimestamp ).getLinearVelocity();
	}

	/**
	 * Gets the orientation of the entity at the given local timestamp, extrapolated using the
	 * dead-reckoning algorithm of this entity.
	 * <p/>
	 * Uses the internally cached value for the state at this timestamp, if available, rather than
	 * recomputing the state.
	 * 
	 * @return the orientation of the entity, as {@link EulerAngles}
	 * @throws IllegalArgumentException if the given timestamp is older than the
	 *             {@link EntityStatePdu}
	 * @see #setOutdatedTimestampBehavior(OutdatedTimestampBehavior)
	 */
	public EulerAngles getDrOrientation( long localTimestamp ) throws IllegalArgumentException
	{
		return this.getDrOrientation( this.getDefaultDeadReckoningAlgorithm(), localTimestamp );
	}

	/**
	 * Gets the orientation of the entity at the given local timestamp, extrapolated using the
	 * specified dead-reckoning algorithm.
	 * <p/>
	 * Uses the internally cached value for the state at this timestamp (with this algorithm), if
	 * available, rather than recomputing the state.
	 * 
	 * @param algorithm the dead-reckoning algorithm to use
	 * @param localTimestamp target timestamp, in ms since epoch.
	 * 
	 * @return the orientation of the entity, as a {@link EulerAngles}
	 * @throws IllegalArgumentException if the given timestamp is older than the
	 *             {@link EntityStatePdu}
	 * @see #setOutdatedTimestampBehavior(OutdatedTimestampBehavior)
	 */
	public EulerAngles getDrOrientation( DeadReckoningAlgorithm algorithm, long localTimestamp )
	    throws IllegalArgumentException
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

	/**
	 * The behavior to follow when a dead-reckoning request is made with an outdated timestamp
	 * (older than the current latest timestamp).
	 */
	public enum OutdatedTimestampBehavior
	{
		/**
		 * Throw an {@link IllegalArgumentException}.
		 */
		ERROR,

		/**
		 * Use the entity's last reported state (the state in this EntityStatePdu). <br/>
		 * Unless entity state has been manually stored elsewhere, this will usually be the oldest
		 * state known for the entity that is more recent than the attempted timestamp.
		 */
		USE_CURRENT_STATE;
	}
}
