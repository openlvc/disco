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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.RadioEntityType;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.LLA;

/**
 * The {@link TransmitterStore} tracks known instances of Transmitters on the network.
 * Transmitters are indexed by an EntityId. If a radio changes its EntityId, it is considered
 * to be a different, distinct radio. When multiple radios are present for an EntityId, they
 * are distinguished by their RadioID field (which counts upwards from 1 to identify a unique
 * radio on an entity). 
 */
public class TransmitterStore implements IDeleteReaperManaged
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityStateStore entityStore;
	private ConcurrentMap<EntityId,TransmitterSet> byId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected TransmitterStore( PduStore parentStore )
	{
		this.entityStore = parentStore.getEntityStore();
		this.byId = new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Processing   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void receivePdu( TransmitterPdu pdu )
	{
		// Get any existing record for the entity, and if we don't have one, create one
		TransmitterSet set = byId.get( pdu.getEntityId() );
		if( set == null )
		{
			set = new TransmitterSet();
			byId.put( pdu.getEntityId(), set );
		}
		
		// store the pdu at the last known location within the set
		set.radios.put( pdu.getRadioID(), pdu );
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Transmitter Query Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasTransmitter( EntityId id )
	{
		return byId.containsKey( id );
	}

	/**
	 * Get the set of transmitters associated with the given EntityId, indexed by their RadioID
	 * 
	 * @param id The EntityID to look up the radios for
	 * @return The set of radios associated with the given EntityID. If the id isn't known, an
	 *         empty set will be returned.
	 */
	public ConcurrentMap<Integer,TransmitterPdu> getTransmitters( EntityId id )
	{
		return byId.getOrDefault(id,new TransmitterSet()).radios;
	}

	/**
	 * Gets the transmitter stored for the given entity and radio id. If we don't know any
	 * radios by that EntityID, or we don't know that particular radio, null is returned.
	 * 
	 * @param id       The EntityID used by the radio
	 * @param radioId  The unique radio ID for the entity
	 * @return         The last TransmitterPdu found for that combination, or null if not known
	 */
	public TransmitterPdu getTransmitter( EntityId id, int radioId )
	{
		return byId.getOrDefault(id,new TransmitterSet()).radios.get( radioId );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Property Based Query Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Set<TransmitterPdu> getTransmittersOfType( RadioEntityType type )
	{
		Set<TransmitterPdu> results = Collections.synchronizedSet( new HashSet<>() );
		byId.values().parallelStream().forEach( tset -> tset.collectRadiosOfType(type,results) );
		return results;
	}

	/**
	 * Return all the transmitters that have been updated only AFTER the given timestamp (millis
	 * since the epoch). Note that we use Disco's local timestamp, NOT the DIS timestamp.
	 * 
	 * @param time The oldest time a PDU can have been updated to be returned
	 * @return The set of all PDUs that have been updated since the given time, which may be empty
	 */
	public Set<TransmitterPdu> getTransmittersUpdatedSince( long time )
	{
		Set<TransmitterPdu> results = Collections.synchronizedSet( new HashSet<>() );
		byId.values().parallelStream().forEach( tset -> tset.collectRadiosUpdatedSince(time,results) );
		return results;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Location Based Query Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the set of transmitters within the given radius of the given location. Note that if 
	 * multiple transmitters are using the same EntityID (to represent being attached to the
	 * same platform) we only test against the first one. If it matches, all are returned. If
	 * a radio is representing itself as a unique entity, then that entity is tested.
	 * <p/>
	 * This method uses the transmitter's antenna location to determine proximity.
	 * 
	 * @param location     The location we want to find transmitters in proximity to
	 * @param radiusMeters Limit of how far a transmitter can be from the location
	 * @return             Set of all transmitters within the given radius of the given location
	 */
	public Set<TransmitterPdu> getTransmittersNear( WorldCoordinate location, int radiusMeters )
	{
		LLA target = location.toLLA();
		Set<TransmitterPdu> results = Collections.synchronizedSet( new HashSet<>() );

		byId.values().parallelStream()
		             .filter( tset -> tset.isNear(target,radiusMeters) )
		             .forEach( tset -> results.addAll(tset.radios.values()) );
		
		return results;
	}

	/**
	 * Find all the transmitters within the given radius of the entity identified by the given ID.
	 * The EntityID must represent a valid EntityState, and this method looks up the entity state
	 * store to find the location of that ID. If the entity is not known, an empty set is returned.
	 * 
	 * @param entityId     The entity we want to find transmitters that are close to
	 * @param radiusMeters Limit of how far a transmitter can be from the location
	 * @return             Set of all transmitters within the given radius of the identified entity
	 */
	public Set<TransmitterPdu> getTransmittersNear( EntityId entityId, int radiusMeters )
	{
		EntityStatePdu entityState = entityStore.getEntityState( entityId );
		if( entityState == null )
			return new HashSet<>();
		else
			return getTransmittersNear( entityState.getLocation(), radiusMeters );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Delete Timeout Support Methods   ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int removeStaleData( long timestamp )
	{
		Set<TransmitterPdu> removed = new HashSet<>();

		// doens't remove the root entry even if all radios are now gone
		byId.values().parallelStream()
		             .forEach( tset -> tset.removeStaleData(timestamp,removed) );
		
		byId.keySet().removeIf( entityId -> byId.get(entityId).radios.isEmpty() );
		
		return removed.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		this.byId.clear();
	}
	
	public int size()
	{
		return byId.size();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: TransmitterSet     //////////////////////////////////////////////////////   
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Represents the set of transmitters that are associated with a particular entity.
	 * We keep track of each radio (as defined by a unique radio ID) that we hear about
	 * for a particular EntityId. This class captures the set of last known radio state
	 * for an entity.
	 */
	private class TransmitterSet
	{
		//private TransmitterPdu[] radios = new TransmitterPdu[16]; // limit to 16 for now
		private ConcurrentMap<Integer,TransmitterPdu> radios = new ConcurrentHashMap<>();

		/**
		 * Finds the first radio in this transmitter set and uses its antenna location
		 * to decided wheather it is near the given location (within the given range).
		 * <p/>
		 * 
		 * If the radio is a standalone entity, there will only be one, so using the antenna
		 * location of the first is valid. If the radio is attached to a platfrom (using its
		 * EntityID), then all transmitters in the set should be attached to the platform and
		 * thus in the same location (for the most part), so using the first found is a reasonable
		 * approximation.
		 *   
		 * @param location The location to check whether radios in this set are near to
		 * @param radiusMeters The number of meters that radios should be within
		 * @return True if this set of radios is within the radius of the location, false otherwise
		 */
		private boolean isNear( LLA location, int radiusMeters )
		{
			// No easy way to just get the first element, so we pretend we're going to loop,
			// and then just return off the first value. If there are no values, then we can't
			// compare to anything and return false
			for( TransmitterPdu pdu : radios.values() )
			{
				return pdu.getAntennaLocation()
				          .getAntennaLocation()
				          .toLLA()
				          .distanceBetweenHavershine( location ) < radiusMeters;
			}
			
			return false;
		}
		
		/**
		 * Loop over all transmitters in this set and if their radio type is the
		 * same as the given type, add it to the given set.
		 * 
		 * @param type    The type of radio we want to find
		 * @param target  The set to collect any PDUs that meet the criteria in
		 */
		private void collectRadiosOfType( RadioEntityType type, Set<TransmitterPdu> target )
		{
			for( TransmitterPdu pdu : radios.values() )
				if( pdu.getRadioEntityType().equals(type) )
					target.add( pdu );
		}
		
		/**
		 * Loop over all contained transmitter PDUs and if they were received AFTER the given
		 * timestamp, add them to the target set.
		 * 
		 * @param timestamp The timestamp to compare their recieve time to
		 * @param target    The set to collect any PDUs that meet the criteria in
		 */
		private void collectRadiosUpdatedSince( long timestamp, Set<TransmitterPdu> target )
		{
			for( TransmitterPdu pdu : radios.values() )
				if( pdu.getLocalTimestamp() > timestamp )
					target.add( pdu );
		}
		
		private void removeStaleData( long timestamp, Set<TransmitterPdu> removedSet )
		{
			for( TransmitterPdu pdu : radios.values() )
			{
				if( pdu.getLocalTimestamp() < timestamp )
				{
					removedSet.add( pdu );
					radios.remove( pdu.getRadioID() );
				}
			}
		}
		
	}
	
}
