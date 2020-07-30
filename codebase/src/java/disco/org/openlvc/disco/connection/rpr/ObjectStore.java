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
package org.openlvc.disco.connection.rpr;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.objects.BaseEntity;
import org.openlvc.disco.connection.rpr.objects.EmitterBeamRpr;
import org.openlvc.disco.connection.rpr.objects.EmitterSystemRpr;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.objects.PhysicalEntity;
import org.openlvc.disco.connection.rpr.objects.RadioTransmitter;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.pdu.emissions.EmitterSystem.EmitterSystemId;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.ObjectInstanceHandle;

/**
 * Storage of all HLA objects that the RPR Connection is using to translate in both dis-to-hla
 * (Local) and hla-to-dis (Remote) directions. Objects created in DIS we reference by a DIS-based
 * ID. Objects discovered via the RTI we reference by the HLA Object Instance Handle.
 */
public class ObjectStore
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// DIS Object Storage
	private Map<EntityId,ObjectInstance> disGeneral;
	private Map<EntityId,RadioTransmitter> disTransmitters;
	private Map<EntityId,PhysicalEntity> disEntities;
	private Map<EmitterSystemId,EmitterSystemRpr> disEmitters;
	private Map<EmitterSystemId,Map<Short,EmitterBeamRpr>> disBeams; // Maps of maps *facepalm*

	// HLA Object Storage
	private Map<ObjectInstanceHandle,ObjectInstance> hlaObjects;
	private Map<RTIobjectId,ObjectInstance> rprObjects; // HLA Objects, but indexed by "RTI id"
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectStore()
	{
		// DIS Object Storage
		this.disGeneral = new ConcurrentHashMap<>();
		this.disTransmitters = new ConcurrentHashMap<>();
		this.disEntities = new ConcurrentHashMap<>();
		this.disEmitters = new ConcurrentHashMap<>();
		this.disBeams = new ConcurrentHashMap<>();
		
		// HLA Object Storage
		this.hlaObjects = new ConcurrentHashMap<>();
		this.rprObjects = new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Local HLA Objects - Indexed by DIS ID   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Add the given generic ObjectInstance to the store, indexed on the given DIS id.
	 * While there are type-specific methods for many common DIS types, this store is
	 * a catch-all for any others (including unspecified types).
	 * 
	 * @param disId      The DIS id we can use to identify the object
	 * @param hlaObject  The HLA ObjectInstance that is associated with it
	 */
	public void addLocalObject( EntityId disId, ObjectInstance hlaObject )
	{
		this.disGeneral.put( disId, hlaObject );
		hlaObject.addToStore( this );
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ObjectInstance> T getLocalObject( EntityId disId )
	{
		return (T)this.disGeneral.get( disId );
	}
	
	public void addLocalTransmitter( EntityId disId, RadioTransmitter hlaObject )
	{
		this.disTransmitters.put( disId, hlaObject );
		this.rprObjects.put( hlaObject.getRtiObjectId(), hlaObject );
		hlaObject.addToStore( this );
	}
	
	public RadioTransmitter getLocalTransmitter( EntityId disId )
	{
		return disTransmitters.get( disId );
	}

	public void addLocalEntity( EntityId disId, PhysicalEntity hlaObject )
	{
		this.disEntities.put( disId, hlaObject );
		this.rprObjects.put( hlaObject.getRtiObjectId(), hlaObject );
		hlaObject.addToStore( this );
	}

	public PhysicalEntity getLocalEntity( EntityId disId )
	{
		return this.disEntities.get( disId );
	}

	public void addLocalEmitter( EmitterSystemId disId, EmitterSystemRpr hlaObject )
	{
		this.disEmitters.put( disId, hlaObject );
		this.rprObjects.put( hlaObject.getRtiObjectId(), hlaObject );
		hlaObject.addToStore( this );
	}
	
	public EmitterSystemRpr getLocalEmitter( EmitterSystemId disId )
	{
		return this.disEmitters.get( disId );
	}
	
	public void addLocalBeam( EmitterSystemId disId, short beamId, EmitterBeamRpr hlaObject )
	{
		Map<Short,EmitterBeamRpr> beams = this.disBeams.get( disId );
		if( beams == null )
		{
			beams = new ConcurrentHashMap<>();
			this.disBeams.put( disId, beams );
		}
		
		beams.put( beamId, hlaObject );
		this.rprObjects.put( hlaObject.getRtiObjectId(), hlaObject );
		hlaObject.addToStore( this );
	}
	
	public EmitterBeamRpr getLocalBeam( EmitterSystemId disId, short beamId )
	{
		Map<Short,EmitterBeamRpr> beams = this.disBeams.get( disId );
		if( beams != null )
			return beams.get( beamId );
		else
			return null;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Remote HLA Object - Indexed by HLA ID   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Add the discovered HLA object to this store. It will be indexed by but its object handle
	 * and its object name (in RPR called its RTIobjectId). If either of these values are null
	 * insidet the given object, an exception will be thrown.
	 *  
	 * @param hlaObject The object to add
	 * @throws DiscoException If the handle or name of the given object is null
	 */
	public void addDiscoveredHlaObject( ObjectInstance hlaObject )
		throws DiscoException
	{
		// Check to make sure we have the required values
		if( hlaObject.getObjectHandle() == null )
			throw new DiscoException( "Cannot add HLA Object with a null ObjectInstanceHandle" );
		
		// We used to require that the object had an RTIobjectId (as all RPR objects do), however for
		// DCSS we also want to be able track WallclockTime which isn't part of the RPR object hierarchy 
		/*
		if( hlaObject.getRtiObjectId() == null )
			throw new DiscoException( "Cannot add HLA Object with a null RTIobjectId" );
		*/
		
		// Store the object by its ObjectInstanceHandle
		this.hlaObjects.put( hlaObject.getObjectHandle(), hlaObject );
		
		RTIobjectId rprId = hlaObject.getRtiObjectId();
		if( rprId != null )
		{
			// Store the object via its RTIobjectId as well. Although this is a RPR construct,
			// it is based on the object name, which is provided in the RTI callback. As such,
			// unlike any attribute values, it is the one thing that will be there from the start
			// and we can used it to index from discovery.
			this.rprObjects.put( rprId, hlaObject );
		}
		
		// Tell the object who is storing it
		hlaObject.addToStore( this );
	}

	/**
	 * A remove callback has been received from the RTI and we should remove the local
	 * representation of the object with the given handle.
	 * 
	 * @param hlaId The object instance handle we should remove from the local store.
	 */
	public void removeDiscoveredHlaObject( ObjectInstanceHandle hlaId )
	{
		ObjectInstance hlaObject = this.hlaObjects.remove( hlaId );		
		if( hlaObject != null && hlaObject.getRtiObjectId() != null )
			this.rprObjects.remove( hlaObject.getRtiObjectId() );
	}

	/**
	 * Return the {@link ObjectInstance} representation of the HLA object that has the
	 * given object handle. If the handle doesn't represent a known object (not discovered
	 * or perhaps removed), then null is returned.
	 * 
	 * @param hlaId The handle for the object instance to look up
	 * @return Our {@link ObjectInstance} representation of the object with the handle, or null
	 */
	public ObjectInstance getDiscoveredHlaObject( ObjectInstanceHandle hlaId )
	{
		return this.hlaObjects.get( hlaId );
	}

	/**
	 * Find and return all discovered HLA objects of the given type (or children of). For example,
	 * if we pass in PhysicalEntity.class as the type, this method will find all discovered 
	 * Platforms and Lifeforms and return them in a collection of Physical Entities. If none are
	 * known, an empty collection is returned.
	 * 
	 * @param <T>   The type we're searching for.
	 * @param clazz The type we want to find all discovered HLA objects of
	 * @return      A collection of all the discovered obejcts.
	 */
	public <T extends ObjectInstance> Collection<T> getDiscoveredHlaObjectsByType( Class<T> clazz )
	{
		return
		hlaObjects.values().stream().filter( oi -> clazz.isInstance(oi) )
		                            .map( oi -> clazz.cast(oi) )
		                            .collect( Collectors.toList() );
	}

	/**
	 * Return a collection of all the HLA objects that are both of the given type (or child) and 
	 * match the given predicate. If there are none, an empty collection will be returned.
	 *  
	 * @param type The class of object we want to consider (this or any child of this)
	 * @param predicate The test we want the objects to pass
	 * @return The collection of ObjectInstance children of the type that pass the predicate test
	 */
	public <T extends ObjectInstance> Collection<T>
	getDiscoveredHlaObjectsMatching( Class<T> type, Predicate<T> predicate )
	{
		return
		hlaObjects.values().parallelStream().filter( type::isInstance )
		                                    .map( type::cast )
		                                    .filter( predicate::test )
		                                    .collect( Collectors.toSet() );
	}

	/**
	 * Return the collection of all known {@link ObjectInstance}s that match the given predicate.
	 * 
	 * @param predicate The predicate to test objects against
	 * @return The collection of objects that match the filter
	 */
	public Collection<ObjectInstance>
	getDiscoveredHlaObjectsMatching( Predicate<? super ObjectInstance> predicate )
	{
		return hlaObjects.values().parallelStream().filter( predicate::test )
		                                           .collect( Collectors.toSet() );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// RTIobjectId Based Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Looks up the {@link RTIobjectId} for the given DIS Entity ID. If we have an object that is
	 * known to use that DIS ID we find it, extract its RTI id and return it. If we can't find one,
	 * we return an {@link RTIobjectId} with an empty string value (should indicate unattached).
	 * <p/>
	 * <b>NOTE:</b> Currently limited to looking at locally created entities only, not any remote
	 * entities discovered through the RTI.
	 * 
	 * @param disId The Entity ID of the Entity we want to get the {@link RTIobjectId} for.
	 * @return The object id of the entity with the given DIS id, or id with an empty string.
	 */
	public RTIobjectId getRtiIdForDisId( EntityId disId )
	{
		// FIXME - Currently only look at the local (DIS) entities, not any we discover through
		//         the RTI. Needs to be updated to allow connection to entities that aren't created
		//         only on the DIS side.
		BaseEntity entity = this.disEntities.get( disId );
		if( entity == null )
			return new RTIobjectId("");
		else
			return entity.getRtiObjectId();
	}

	/**
	 * In RPR, objects of various types (Entity States especially) are often identified by an
	 * {@link RTIobjectId}. Although not clearly stated anywhere in the RPR spec, this is just
	 * a wrapper around the HLA object name. This method returns the {@link ObjectInstance} that
	 * is associated with the given {@link RTIobjectId}.
	 * 
	 * @param id The RTI id we should find the known {@link ObjectInstance} for
	 * @return The RPR object instance for the id, or null if none is known
	 */
	public ObjectInstance getDiscoveredHlaObjectByRtiId( RTIobjectId id )
	{
		if( id == null )
			return null;
		else
			return this.rprObjects.get( id );
	}

	/**
	 * Return the set of known/discovered {@link RTIobjectId}s where the type they are in is
	 * of the given clazz.
	 * 
	 * @param clazz The class we want to find all the IDs of known objects in the store
	 * @return All known RTIobjectIds used by objects of the given type (empty if none)
	 */
	public Collection<RTIobjectId> getDiscoveredHlaObjectIdsForType( Class<? extends ObjectInstance> clazz )
	{
		return
		hlaObjects.values().stream().filter( oi -> clazz.isInstance(oi) )
		                            .map( oi -> oi.getRtiObjectId() )
		                            .collect( Collectors.toList() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
