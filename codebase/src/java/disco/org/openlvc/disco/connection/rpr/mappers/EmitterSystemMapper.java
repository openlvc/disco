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
package org.openlvc.disco.connection.rpr.mappers;

import java.util.Arrays;
import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.EmitterBeamRpr;
import org.openlvc.disco.connection.rpr.objects.EmitterSystemRpr;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.emissions.EmitterSystem;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;

public class EmitterSystemMapper extends AbstractEmitterMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// HLA Handle Information
	private ObjectClass hlaClass;
	// EmbeddedSystem
	private AttributeClass entityIdentifier;
	private AttributeClass hostObjectIdentifier;
	private AttributeClass relativePosition;
	// Emitter System
	private AttributeClass emitterFunctionCode;
	private AttributeClass emitterType;
	private AttributeClass emitterIndex;
	private AttributeClass eventIdentifier;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.Emission );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// Cache all the attributes we need
		this.hlaClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.EmbeddedSystem.EmitterSystem" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.EmbeddedSystem.EmitterSystem" );
		
		// Embedded System
		this.entityIdentifier = hlaClass.getAttribute( "EntityIdentifier" );
		this.hostObjectIdentifier = hlaClass.getAttribute( "HostObjectIdentifier" );
		this.relativePosition = hlaClass.getAttribute( "RelativePosition" );
		// Emitter System
		this.emitterFunctionCode = hlaClass.getAttribute( "EmitterFunctionCode" );
		this.emitterType = hlaClass.getAttribute( "EmitterType" );
		this.emitterIndex = hlaClass.getAttribute( "EmitterIndex" );
		this.eventIdentifier = hlaClass.getAttribute( "EventIdentifier" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( EmissionPdu pdu )
	{
		for( EmitterSystem disSystem : pdu.getEmitterSystems() )
		{
			// FIXME - Currently only allows attachment to _local_ entities
			EmitterSystemRpr hlaObject = objectStore.getLocalEmitter( disSystem.getEmitterSystemId() );

			// If there is no HLA object yet, we have to register one
			if( hlaObject == null )
			{
				hlaObject = new EmitterSystemRpr();
				hlaObject.setObjectClass( this.hlaClass );
				super.registerObjectInstance( hlaObject );
				objectStore.addLocalEmitter( disSystem.getEmitterSystemId(), hlaObject );
			}
			
			// Extract the EmitterSystem values from the PDU
			// Emitter beams are managed directly in the EmitterBeamMapper
			super.fromPdu( hlaObject, disSystem, pdu.getEventId() );

			// Send an update for the object
			super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );
			
			if( logger.isTraceEnabled() )
				logger.trace( "(EmitterSystem) Updated attributes for emitter: id=%s, handle=%s",
				              disSystem.getEmitterSystemId(), hlaObject.getObjectHandle() );
		}
	}

	private AttributeHandleValueMap serializeToHla( EmitterSystemRpr hlaObject )
	{
		AttributeHandleValueMap map = hlaObject.getObjectAttributes();

		// EntityIdentifier
		hlaEncode( hlaObject.getEntityIdentifier(), entityIdentifier, map );
		
		// HostObjectIdentifier
		hlaEncode( hlaObject.getHostObjectIdentifier(), hostObjectIdentifier, map );
		
		// RelativePosition
		hlaEncode( hlaObject.getRelativePosition(), relativePosition, map );
		
		// EmitterFunctionCode
		hlaEncode( hlaObject.getEmitterFunctionCode(), emitterFunctionCode, map );
		
		// EmitterType
		hlaEncode( hlaObject.getEmitterType(), emitterType, map );
		
		// EmitterIndex
		hlaEncode( hlaObject.getEmitterIndex(), emitterIndex, map );
		
		// EventIdentifier
		hlaEncode( hlaObject.getEventIdentifier(), eventIdentifier, map );
		
		return map;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( hlaClass == event.theClass )
		{
			EmitterSystemRpr hlaObject = new EmitterSystemRpr();
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			hlaObject.setObjectName( event.objectName );
			hlaObject.setObjectAttributes( super.createAttributes(this.hlaClass) );
			objectStore.addDiscoveredHlaObject( hlaObject );
			
			if( logger.isDebugEnabled() )
			{
    			logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
    			              event.theClass.getLocalName(),
    			              event.theObject );
			}
			
			// Request an attribute update for the object so that we can get everything we need
			super.requestAttributeUpdate( hlaObject );
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		//
		// 1. If this isn't an Emitter System, skip it
		//
		if( (event.hlaObject instanceof EmitterSystemRpr) == false )
			return;
		
		EmitterSystemRpr rprSystem = (EmitterSystemRpr)event.hlaObject;
		
		//
		// 2. Update the local representation of the emitter system
		//
		deserializeFromHla( rprSystem, event.attributes );
		
		// 
		// 3. Check to see if the system is loaded enough to emit a PDU.
		//    If not, skip.
		//
		if( rprSystem.isReady() == false )
			return;
		
		//
		// 4. Find any beams we have that need to be updated
		//
		// We flush with all the known beams because they may not have been flushed when we got
		// a reflection for them if this system wasn't loaded at the time
		RTIobjectId emitterId = rprSystem.getRtiObjectId();
		Collection<EmitterBeamRpr> rprBeams = objectStore.getDiscoveredHlaObjectsMatching( EmitterBeamRpr.class,
		                                                                                   beam -> beam.isParent(emitterId) );
		//
		// 5. Generate the PDU
		//
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		EmissionPdu pdu = super.toPdu( rprSystem, rprBeams );
		opscenter.getPduReceiver().receive( pdu.toByteArray() );
	}

	private void deserializeFromHla( EmitterSystemRpr hlaObject, AttributeHandleValueMap map )
	{
		// EntityIdentifier
		hlaDecode( hlaObject.getEntityIdentifier(), entityIdentifier, map );
		
		// HostObjectIdentifier
		hlaDecode( hlaObject.getHostObjectIdentifier(), hostObjectIdentifier, map );
		
		// RelativePosition
		hlaDecode( hlaObject.getRelativePosition(), relativePosition, map );
		
		// EmitterFunctionCode
		hlaDecode( hlaObject.getEmitterFunctionCode(), emitterFunctionCode, map );
		
		// EmitterType
		hlaDecode( hlaObject.getEmitterType(), emitterType, map );
		
		// EmitterIndex
		hlaDecode( hlaObject.getEmitterIndex(), emitterIndex, map );
		
		// EventIdentifier
		hlaDecode( hlaObject.getEventIdentifier(), eventIdentifier, map );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
