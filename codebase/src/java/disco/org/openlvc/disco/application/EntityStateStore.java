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
import java.util.stream.Collectors;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.LLA;

/**
 * Tracks the current state of all known {@link EntityStatePdu}s received from the network
 * for later access. As updates are received the previous state is replaced and released.
 */
public class EntityStateStore
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ConcurrentMap<EntityId,EntityStatePdu> byId;
	private ConcurrentMap<String,EntityStatePdu> byMarking;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EntityStateStore()
	{
		this.byId = new ConcurrentHashMap<>();
		this.byMarking = new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Processing   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void receivePdu( EntityStatePdu pdu )
	{
		// bang the entity into the ID indexed store
		EntityStatePdu existing = byId.put( pdu.getEntityID(), pdu );

		// if we are discovering this entity for first time, store in marking indexed store as well
		if( existing == null )
		{
			byMarking.put( pdu.getMarking(), pdu );
		}
		else if( existing.getMarking().equals(pdu.getMarking()) == false )
		{
			// marking has changed, need to update the marking indexed store
			byMarking.remove( existing.getMarking() );
			byMarking.put( pdu.getMarking(), pdu );
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

	public EntityStatePdu getEntityState( String marking )
	{
		if( marking.length() > 11 )
			throw new DiscoException( "DIS markings limited to 11 characters: [%s] too long", marking );
		
		return byMarking.get( marking );
	}
	
	public EntityStatePdu getEntityState( EntityId id )
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
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Location Based Query Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Set<EntityStatePdu> getEntityStatesNear( EntityStatePdu entity, long radiusMeters )
	{
		// only do this once
		LLA target = entity.getLocation().toLLA();

		return byId.values().parallelStream()
		                    .filter( other -> target.distanceBetweenHavershine(other.getLocation().toLLA()) < radiusMeters )
		                    .collect( Collectors.toSet() );
	}
	
	public Set<EntityStatePdu> getEntityStatesNear( WorldCoordinate location, long radiusMeters )
	{
		// only do this once
		LLA target = location.toLLA();
		
		return byId.values().parallelStream()
		                    .filter( other -> target.distanceBetweenHavershine(other.getLocation().toLLA()) < radiusMeters )
		                    .collect( Collectors.toSet() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
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
