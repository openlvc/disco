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
package org.openlvc.disco.application;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.application.deadreckoning.DeadReckoningModel;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.EntityId;

/**
 * Tracks objects and provides dead-reckoning models based on their last known state.
 * As updates are received the previous model is replaced.
 */
public class DeadReckoningModelStore implements IDeleteReaperManaged
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger logger;
	private PduStore parentStore;

	private EntityStateStore entityStore;
	private ConcurrentMap<EntityId,Optional<DeadReckoningModel>> byId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected DeadReckoningModelStore( PduStore parentStore )
	{
		this.parentStore = parentStore;

		this.entityStore = parentStore.getEntityStore();
		this.byId = new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private Logger getLogger()
	{
		if( this.logger == null )
			this.logger = LogManager.getFormatterLogger( this.parentStore.getLogger().getName()+".drmstore" );
		return this.logger;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		this.byId.clear();
	}
	
	public int size()
	{
		return byId.size();
	}

	private void addPdu( EntityStatePdu pdu )
	{
		EntityId entityId = pdu.getEntityID();

		// if the entity is frozen then don't perform dead-reckoning
		if( pdu.isFrozen() )
		{
			this.byId.put( entityId, Optional.empty() );
			this.getLogger().trace( "Processed entity as frozen: entityId=%s", entityId );
			return;
		}

		// update the DRM for the entity
		DeadReckoningModel drm = DeadReckoningModel.makeDeadReckoningModel( pdu, Optional.of(this.getLogger()) );
		this.byId.put( entityId, Optional.of(drm) );
		this.getLogger().trace( "Updated entity DRM: entityId=%s, algorithm=%d (%s)",
		                        entityId,
		                        drm.getAlgorithm().value(),
		                        drm.getAlgorithm() );
	}

	/**
	 * Flags an entity to be tracked by the store. Does nothing if already tracking. <br/>
	 * <br/>
	 * Will initialise the store with a dead-reckoning model based on the last received
	 * {@link EntityStatePDU}, if available.
	 * 
	 * @param entityId the {@link EntityId} of the entity to track
	 */
	public void registerEntity( EntityId entityId )
	{
		// don't re-register an entity
		if( this.isRegistered( entityId ) )
			return;

		// mark the entity to be tracked
		this.byId.put( entityId, Optional.empty() );
		this.getLogger().trace( "Marked entity for tracking: entityId=%s", entityId );

		// we need entity details to actually create a DRM - grab them from the entity store
		EntityStatePdu pdu = this.entityStore.getEntityState( entityId );
		if( pdu == null )
			return;

		this.addPdu( pdu );
	}

	public void unregisterEntity( EntityId entityId )
	{
		this.byId.remove( entityId );
	}

	/**
	 * Checks if the given entity is marked for tracking.
	 * 
	 * @param entityId the {@link EntityId} of the entity to test
	 * @return true iff the entity is being tracked
	 * 
	 * @see #isDrmAvailable(EntityId)
	 */
	public boolean isRegistered( EntityId entityId )
	{
		return this.byId.containsKey( entityId );
	}

	protected void receivePdu( EntityStatePdu pdu )
	{
		// if we're not tracking the entity then we don't care about the PDU
		if( !this.isRegistered( pdu.getEntityID() ) )
			return;

		this.addPdu( pdu );	
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DRM Query Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a DRM is available for the given entity.
	 * 
	 * @param entityId the {@link EntityId} of the entity to test
	 * @return true iff a DRM is available for the entity
	 * 
	 * @see #isRegistered(EntityId)
	 */
	public boolean isDrmAvailable( EntityId entityId )
	{
		return this.byId.containsKey( entityId ) && this.byId.get( entityId ).isPresent();
	}

	/**
	 * Gets the dead-reckoning model associated with an entity.
	 * 
	 * @param entityId the {@link EntityId} of the entity
	 * @return the latest {@link DeadReckoningModel} for the entity, or `null` if not available
	 */
	public DeadReckoningModel getDrm( EntityId id )
	{
		return this.byId.get( id ).orElse( null );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Delete Timeout Support Methods   ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int removeStaleData( long oldestTimestamp )
	{
		AtomicInteger removed = new AtomicInteger( 0 );

		Predicate<Entry<EntityId,Optional<DeadReckoningModel>>> isOld = entry -> {
			Optional<DeadReckoningModel> drm = entry.getValue();

			// we can't know how old an entry that doesn't yet have a DRM is, so skip it
			if( drm.isEmpty() )
				return false;

			return drm.get().getInitialLocalTimestamp() < oldestTimestamp;
		};

		this.byId.entrySet().parallelStream().filter( isOld )
		                                     .map( Entry::getKey )
		                                     .forEach( id -> {
		                                         this.byId.remove( id );
		                                         removed.incrementAndGet();
		                                     } );

		return removed.intValue();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
