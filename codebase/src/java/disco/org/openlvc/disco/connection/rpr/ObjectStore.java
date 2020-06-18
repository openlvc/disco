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

import java.util.HashMap;
import java.util.Map;

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
	private Map<EntityId,RadioTransmitter> disTransmitters;
	private Map<EntityId,PhysicalEntity> disEntities;
	private Map<EmitterSystemId,EmitterSystemRpr> disEmitters;
	private Map<EmitterSystemId,Map<Short,EmitterBeamRpr>> disBeams; // Maps of maps *facepalm*

	// HLA Object Storage
	private Map<ObjectInstanceHandle,ObjectInstance> hlaObjects;
	private Map<RTIobjectId,ObjectInstance> rtiObjects; // HLA Objects, but indexed by "RTI id"
//	private Map<RTIobjectId,EmitterSystemRpr> hlaEmitterSystems;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectStore()
	{
		// DIS Object Storage
		this.disTransmitters = new HashMap<>();
		this.disEntities = new HashMap<>();
		this.disEmitters = new HashMap<>();
		this.disBeams = new HashMap<>();
		
		// HLA Object Storage
		this.hlaObjects = new HashMap<>();
		this.rtiObjects = new HashMap<>();
//		this.hlaEmitterSystems = new HashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Local HLA Objects - Indexed by DIS ID   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void addLocalTransmitter( EntityId disId, RadioTransmitter hlaObject )
	{
		this.disTransmitters.put( disId, hlaObject );
		this.rtiObjects.put( hlaObject.getRtiObjectId(), hlaObject );
	}
	
	public RadioTransmitter getLocalTransmitter( EntityId disId )
	{
		return disTransmitters.get( disId );
	}

	public void addLocalEntity( EntityId disId, PhysicalEntity hlaObject )
	{
		this.disEntities.put( disId, hlaObject );
		this.rtiObjects.put( hlaObject.getRtiObjectId(), hlaObject );
	}

	public PhysicalEntity getLocalEntity( EntityId disId )
	{
		return this.disEntities.get( disId );
	}

	public void addLocalEmitter( EmitterSystemId disId, EmitterSystemRpr hlaObject )
	{
		this.disEmitters.put( disId, hlaObject );
		this.rtiObjects.put( hlaObject.getRtiObjectId(), hlaObject );
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
			beams = new HashMap<>();
			this.disBeams.put( disId, beams );
		}
		
		beams.put( beamId, hlaObject );
		this.rtiObjects.put( hlaObject.getRtiObjectId(), hlaObject );
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
	public void addDiscoveredHlaObject( ObjectInstanceHandle hlaId, ObjectInstance hlaObject )
	{
		this.hlaObjects.put( hlaId, hlaObject );
		
		// Store the object via its RTIobjectId as well. Although this is a RPR construct,
		// it is based on the object name, which is provided in the RTI callback. As such,
		// unlike any attribute values, it is the one thing that will be there from the start
		// and we can used it to index from discovery.
		this.rtiObjects.put( hlaObject.getRtiObjectId(), hlaObject );

		// We put it into the provisional map for now until we get an attribute update
//		if( hlaObject.isLoaded() == false )
//			this.hlaProvisionalObjects.put( hlaId, hlaObject );
		
//		if( hlaObject instanceof PhysicalEntity )
//		{
//			PhysicalEntity rprEntity = (PhysicalEntity)hlaObject;
//			EntityId disId = rprEntity.getEntityIdentifier().getDisValue(); // WON"T FUCKING WORK BECUASE NOT REFLECTED YET
//			disObjectIds.put( disId,  )
//		}
//		else if( hlaObject instanceof EmitterSystemRpr )
//		{
//			EmitterSystemRpr rprSystem = (EmitterSystemRpr)hlaObject;       // ALSO WON"T FUCKING WORK
//			hlaEmitterSystems.put( rprSystem.getHostObjectIdentifier(), rprSystem );
//		}
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
		if( hlaObject != null )
			this.rtiObjects.remove( hlaObject.getRtiObjectId() );
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
	public RTIobjectId getHostIdForEntityId( EntityId disId )
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
	public ObjectInstance getHlaObjectByRtiId( RTIobjectId id )
	{
		return this.rtiObjects.get( id );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
