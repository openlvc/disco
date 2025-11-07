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
package org.openlvc.disco.application;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.application.pdu.DrEntityStatePdu;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.WorldCoordinate;

/**
 * Tracks the current state of all known {@link EntityStatePdu}s received from the network
 * for later access. As updates are received the previous state is replaced and released.
 */
public class EntityStateStore implements IDeleteReaperManaged
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ConcurrentMap<EntityId,DrEntityStatePdu> byId;
	private ConcurrentMap<String,DrEntityStatePdu> byMarking;

	private BooleanSupplier isDrEnabled;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EntityStateStore( PduStore store )
	{
		this.byId = new ConcurrentHashMap<>();
		this.byMarking = new ConcurrentHashMap<>();

		this.isDrEnabled = store.app.getConfiguration()::getDeadReckoningEnabled;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Processing   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void receivePdu( EntityStatePdu pdu )
	{
		// wrap the PDU to provide dead-reckoning support
		DrEntityStatePdu wrappedPdu = this.isDrEnabled.getAsBoolean() ? new DrEntityStatePdu( pdu )
		                                                              : DrEntityStatePdu.makeWithoutDr( pdu );

		// bang the entity into the ID indexed store
		DrEntityStatePdu existing = byId.put( wrappedPdu.getEntityID(), wrappedPdu );
		
		// if we are discovering this entity for first time, store in marking indexed store as well
		if( existing == null )
		{
			DrEntityStatePdu existingMarking = byMarking.put( wrappedPdu.getMarking(), wrappedPdu );
			
			// If there is already an entity against this marking with a different id, then
			// we assume that it has gone stale in favor of the one that we have just received.
			//
			// Note: This check was added for observed behavior when restarting a VR-Forces 
			// simulation with HLA. The Entities in the scenario are removed and then re-added 
			// with different EntityIds, however their marking are the same.
			if( existingMarking != null )
				byId.remove( existingMarking.getEntityID(), existingMarking );
			
		}
		else if( existing.getMarking().equals(wrappedPdu.getMarking()) == false )
		{	
			// marking has changed, need to update the marking indexed store
			byMarking.remove( existing.getMarking() );
			byMarking.put( wrappedPdu.getMarking(), wrappedPdu );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Locally Created PDU Tracking Methods   /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//public void addEntityState( EntityStatePdu pdu )
	//{
	//}
	
	//public EntityStatePdu removeEntityState( String marking )
	//{
	//	return null;
	//}
	
	//public EntityStatePdu removeEntityState( EntityId id )
	//{
	//	return null;
	//}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Entity State Query Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasEntityState( String marking )
	{
		return byMarking.containsKey( marking );
	}
	
	public boolean hasEntityState( EntityId id )
	{
		return byId.containsKey( id );
	}

	public DrEntityStatePdu getEntityState( String marking )
	{
		if( marking.length() > 11 )
			throw new DiscoException( "DIS markings limited to 11 characters: [%s] too long", marking );
		
		return byMarking.get( marking );
	}
	
	/**
	 * @param id
	 * @return the last {@link DrEntityStatePdu} for the entity, or `null` if not stored
	 */
	public DrEntityStatePdu getEntityState( EntityId id )
	{
		return byId.get( id );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Property Based Query Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Set<String> getAllMarkings()
	{
		return new HashSet<>( byMarking.keySet() );
	}
	
	/**
	 * Return all the entities that have been updated only AFTER the given timestamp (millis
	 * since the epoch). Note that we use Disco's local timestamp, NOT the DIS timestamp.
	 * 
	 * @param time The oldest time a PDU can have been updated to be returned
	 * @return The set of all PDUs that have been updated since the given time, which may be empty
	 */
	public Set<DrEntityStatePdu> getEntityStatesUpdatedSince( long time )
	{
		return this.byId.values().parallelStream()
		                         .filter( espdu -> espdu.getLocalTimestamp() >= time )
		                         .collect( Collectors.toSet() );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Location Based Query Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the set of Entity States whose location is within the radius of the specified entity's
	 * location. If none are close, an empty set is returned.
	 * 
	 * @param entity       The entity we want to find other entities in proximity to
	 * @param radiusMeters Limit of how far a entity can be from the given entity
	 * @return             Set of all entities within the given radius of the given entity
	 */
	public Set<DrEntityStatePdu> getEntityStatesNear( EntityStatePdu entity, int radiusMeters )
	{
		return getEntityStatesNear( entity.getLocation(), radiusMeters );
	}
	
	/**
	 * Get the set of Entity States whose location is within the specified radius of the specified
	 * location. If none are close, an empty set is returned.
	 * 
	 * @param location     The location we want to find entities in proximity to
	 * @param radiusMeters Limit of how far a entity can be from the location
	 * @return             Set of all entities within the given radius of the given location
	 */
	public Set<DrEntityStatePdu> getEntityStatesNear( WorldCoordinate location, int radiusMeters )
	{
		return byId.values().parallelStream()
		                    .filter( other -> WorldCoordinate.getStraightLineDistanceBetween(location, other.getLocation()) < radiusMeters )
		                    .collect( Collectors.toSet() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Delete Timeout Support Methods   ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int removeStaleData( long oldestTimestamp )
	{
		AtomicInteger removed = new AtomicInteger(0); 
		byId.values().parallelStream()
		             .filter( espdu -> espdu.getLocalTimestamp() < oldestTimestamp )
		             .forEach( espdu -> {
		                 EntityId entityId = espdu.getEntityID();
		                 String entityMarking = espdu.getMarking();
		                 
		                 byId.remove( entityId );
		                 EntityStatePdu markingEntry = byMarking.get( entityMarking );
		                 if( markingEntry.getEntityID().equals(entityId) )
		                     byMarking.remove( espdu.getMarking() );
		                 
		                 removed.incrementAndGet();
		              });
		
		return removed.intValue();
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		this.byId.clear();
		this.byMarking.clear();
	}
	
	public int size()
	{
		return byMarking.size();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
