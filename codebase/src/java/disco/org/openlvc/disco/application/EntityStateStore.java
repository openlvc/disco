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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.WorldCoordinate;

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
	private ConcurrentMap<String,EntityStatePdu> byMarking;
	private ConcurrentMap<EntityId,EntityStatePdu> byId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EntityStateStore()
	{
		this.byMarking = new ConcurrentHashMap<>();
		this.byId = new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Processing   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void receivePdu( EntityStatePdu pdu )
	{
		byMarking.put( pdu.getMarking(), pdu );
		byId.put( pdu.getEntityID(), pdu );
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
	public Set<EntityStatePdu> getEntityStatesNear( EntityStatePdu target, double radiusKm )
	{
		return new HashSet<>();
	}
	
	public Set<EntityStatePdu> getEntityStatesNear( WorldCoordinate location, double radiusKm )
	{
		return new HashSet<>();
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
