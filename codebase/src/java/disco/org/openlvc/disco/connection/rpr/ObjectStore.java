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

import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.objects.PhysicalEntity;
import org.openlvc.disco.connection.rpr.objects.RadioTransmitter;
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
	
	// HLA Object Storage
	private Map<ObjectInstanceHandle,ObjectInstance> hlaObjects;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectStore()
	{
		// DIS Object Storage
		this.disTransmitters = new HashMap<>();
		this.disEntities = new HashMap<>();
		
		// HLA Object Storage
		this.hlaObjects = new HashMap<>();
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
	}
	
	public RadioTransmitter getLocalTransmitter( EntityId disId )
	{
		return disTransmitters.get( disId );
	}

	public void addLocalEntity( EntityId disId, PhysicalEntity hlaObject )
	{
		this.disEntities.put( disId, hlaObject );
	}

	public PhysicalEntity getLocalEntity( EntityId disId )
	{
		return this.disEntities.get( disId );
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Remote HLA Object - Indexed by HLA ID   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void addDiscoveredHlaObject( ObjectInstanceHandle hlaId, ObjectInstance hlaObject )
	{
		this.hlaObjects.put( hlaId, hlaObject );
	}
	
	public void removeDiscoveredHlaObject( ObjectInstanceHandle hlaId )
	{
		this.hlaObjects.remove( hlaId );
	}
	
	public ObjectInstance getDiscoveredHlaObject( ObjectInstanceHandle hlaId )
	{
		return this.hlaObjects.get( hlaId );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
