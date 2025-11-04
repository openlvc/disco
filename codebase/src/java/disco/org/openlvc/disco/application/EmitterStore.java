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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.emissions.EmitterSystem;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.BeamFunction;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.WorldCoordinate;

public class EmitterStore implements IDeleteReaperManaged
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityStateStore entityStore;
	private ConcurrentMap<EntityId,EmitterSet> byId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EmitterStore( PduStore parentStore )
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
	protected void receivePdu( EmissionPdu pdu )
	{
		// NOTE - According to the spec, Emission PDUs can be issued using the following schemes:
		//
		//    (1) Complete-Entity: PDU contains full definition of entire emitter (like ESPDU)
		//    (2) Complete-System: PDU contains full definitions of entire EmitterSystems, but this
		//                         does _not_ mean the set of systems provided is the complete set.
		//    (3) Complete-Beam  : PDU contains full definitions for EmitterBeams inside each 
		//                         EmitterSystem, but the set of beams isn't necessarily the full
		//                         set for the system, nor is the set of systems necessarily the
		//                         full set for the platform.

		// Who is emitting this?
		EntityId emitter = pdu.getEmittingEntityId();

		// Find the emitter set associated with this emitting entity
		EmitterSet set = byId.get( emitter );
		if( set == null )
		{
			set = new EmitterSet();
			byId.put( emitter, set );
		}

		// For each system, add all the beams from the incoming PDU to the existing matching
		// system that we have. If we don't have one, just store the incoming system as a whole
		for( EmitterSystem incoming : pdu.getEmitterSystems() )
		{
			// If there is an existing system for this number, get it, otherwise add it
			EmitterSystem existing = set.systems.get( incoming.getSystemType().getNumber() );
			if( existing == null )
			{
				// Don't track if the system is inactive (no active beams)
				if( !incoming.isSystemActive() )
					continue;

				set.systems.put( incoming.getSystemType().getNumber(), incoming );
				continue;
			}

			// System already being tracked - update it
			// Update the values of the system itself
			existing.setLastUpdatedTime( incoming.getLastUpdatedTime() );
			existing.setLocation( incoming.getLocation() );
			existing.setSystemType( incoming.getSystemType() );

			switch( pdu.getHeader().getVersion() )
			{
				// v6 and lower - beams are disabled by not being present
				case Version1:
				case Version2:
				case Version3:
				case Version4:
				case Version5:
				case Version6:
					// Remove any Beams we have first.
					// If the pdu does not contain any, the beam(s) should be deleted
					existing.clearBeams();

					// Add each beam
					for( EmitterBeam beam : incoming.getBeams() )
						existing.addBeam( beam );

					break;

				// v7 and higher - beams are disabled by 'Beam Status'
				case Version7:
				case Other:
				default:
					// Update or remove each beam
					for( EmitterBeam beam : incoming.getBeams() )
					{
						switch( beam.getBeamStatus() )
						{
							case Deactivated:
								// Remove the beam
								existing.removeBeam( beam );
								break;

							case Active:
							default:
								// Update the beam by replacing the existing copy
								existing.addBeam( beam );
								break;
						}
					}
					break;
			}

			// If the system is inactive (has no active beams), remove it from the store
			if( !existing.isSystemActive() )
				set.systems.remove( existing.getSystemType().getNumber() );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Emitting Entity-Centric Queries   //////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Collection<EmitterSystem> getEmittersOnEntity( EntityId carrierId )
	{
		return Collections.unmodifiableCollection( byId.get(carrierId).systems.values() );
	}
	
	public Set<EmitterBeam> getActiveBeams()
	{
		return findBeamsMatching( beam -> beam.isBeamActive() );
	}

	public Set<EmitterBeam> getActiveBeamsNear( EntityId entity, int radiusMeters )
	{
		EntityStatePdu espdu = entityStore.getEntityState( entity );
		if( espdu != null )
			return getActiveBeamsNear( espdu.getLocation(), radiusMeters );
		else
			return Collections.emptySet();
	}
	
	public Set<EmitterBeam> getActiveBeamsNear( WorldCoordinate location, int radiusMeters )
	{

		Set<EmitterBeam> beams = new HashSet<>();
		
		// For each known EmitterSet, find out if it is close to the target, and if so, grab
		// all the active beams inside it
		for( EntityId localEntity : byId.keySet() )
		{
			// get the location of the parent entity
			EntityStatePdu espdu = entityStore.getEntityState(localEntity);
			if( espdu == null )
				continue; // no entity? ruh-roh
			
			WorldCoordinate entityLocation = espdu.getLocation();
			
			// is this entity close to where our target is?
			if( WorldCoordinate.getStraightLineDistanceBetween(location, entityLocation) > radiusMeters )
				continue;
			
			// we are in proximity; find all the active beams
			EmitterSet set = byId.get(localEntity);
			beams.addAll( set.findBeamsMatching(beam -> beam.isBeamActive()) );
		}

		return beams;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Targeted Entity-Centric Queries   //////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the set of {@link EmitterBeam}s that are currently listing the given {@link EntityId}
	 * in their Track/Jam data fields as a target.
	 * 
	 * @param targetEntity The id of the entity we want to find who is targeting
	 * @return The set of beams targeting the platform. Empty if there are none.
	 */
	public Set<EmitterBeam> getBeamsTargeting( EntityId targetEntity )
	{
		return findBeamsMatching( beam -> beam.isTargeting(targetEntity) );
	}
	
	public Set<EmitterBeam> getBeamsWithFunctionTargeting( BeamFunction function,
	                                                       EntityId targetEntity )
	{
		return findBeamsMatching( beam -> beam.getBeamFunction() == function &&
		                                  beam.isTargeting(targetEntity) );
	}


	/**
	 * Return a set of all the beams contains in any emitter system that match the predicate
	 * provided as a parameter 
	 * @param predicate The predicate we test to determine interest
	 * @return A set of all beams that match the predicate. Empty if there are none.
	 */
	private final Set<EmitterBeam> findBeamsMatching( Predicate<EmitterBeam> predicate )
	{
		Set<EmitterBeam> beams = new HashSet<EmitterBeam>();
		for( EmitterSet set : byId.values() )
		{
			for( EmitterSystem system : set.systems.values() )
			{
				for( EmitterBeam beam : system.getBeams() )
					if( predicate.test(beam) )
						beams.add( beam );
			}
		}
		
		return beams;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Delete Timeout Support Methods   ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int removeStaleData( long oldestTimestamp )
	{
		// Feels bad, but through the "map" function we remove data. The number of emitter systems
		// removed from each set is returned, and so we have mapped to int values, and then count
		// up the total
		int removed = byId.values().parallelStream()
		                           .mapToInt( set -> set.removeStaleData(oldestTimestamp) )
		                           .sum();
		
		// After this we might have some empty emitter sets (platforms with no emitters left) that
		// we need to clean up
		byId.entrySet().removeIf( entry -> entry.getValue().systems.isEmpty() );
		
		return removed;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		this.byId.clear();
	}

	/**
	 * @return The number of <b>entities</b> with {@link EmitterSystem}s. If you want a total
	 *         count of the systems, use {@link #getEmitterSystemCount()}.
	 */
	public int size()
	{
		return this.byId.size();
	}

	/**
	 * @return A count of all the {@link EmitterSystem}s we are tracking (any given platform/entity
	 *         may have multiple emitter systems on it.
	 */
	public int getEmitterSystemCount()
	{
		return byId.values().parallelStream().mapToInt( set -> set.systems.size() ).sum();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: EmitterSet     //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Represents a set of {@link EmitterSystem}s that can be associated with an particular entity.
	 * Each system is uniquely identified by an integer, and the set as a whole is associated with
	 * the {@link EntityId} of carrier platform.
	 */
	private class EmitterSet
	{
		/**
		 * The set of all EmitterSystems on a particular platform, indexed by their ID
		 */
		private ConcurrentMap<Short,EmitterSystem> systems = new ConcurrentHashMap<>();

		/**
		 * Return a set of all the beams contains in any emitter system that match the predicate
		 * provided as a parameter.
		 * 
		 * @param predicate The predicate we test to determine interest
		 * @return A set of all beams that match the predicate. Empty if there are none.
		 */
		private final Set<EmitterBeam> findBeamsMatching( Predicate<EmitterBeam> predicate )
		{
			Set<EmitterBeam> beams = new HashSet<EmitterBeam>();
			for( EmitterSystem system : systems.values() )
			{
				for( EmitterBeam beam : system.getBeams() )
					if( predicate.test(beam) )
						beams.add( beam );
			}
			
			return beams;
		}		
		
		private int removeStaleData( long oldestTimestamp )
		{
			int startSize = systems.size();
			systems.entrySet().removeIf( e -> e.getValue().getLastUpdatedTime() < oldestTimestamp );
			return startSize - systems.size();
		}
	}

}
