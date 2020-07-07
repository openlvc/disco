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

import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.RprConnection;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.EmitterBeamRpr;
import org.openlvc.disco.connection.rpr.objects.EmitterSystemRpr;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.emissions.EmitterSystem;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

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
	public EmitterSystemMapper( RprConnection connection ) throws DiscoException
	{
		super( connection );
		this.initializeHandles();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void initializeHandles() throws DiscoException
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

	private AttributeHandleValueMap serializeToHla( EmitterSystemRpr object )
	{
		AttributeHandleValueMap map = object.getObjectAttributes();
		
		// EntityIdentifier
		ByteWrapper wrapper = new ByteWrapper( object.getEntityIdentifier().getEncodedLength() );
		object.getEntityIdentifier().encode(wrapper);
		map.put( entityIdentifier.getHandle(), wrapper.array() );
		
		// HostObjectIdentifier
		wrapper = new ByteWrapper( object.getHostObjectIdentifier().getEncodedLength() );
		object.getHostObjectIdentifier().encode(wrapper);
		map.put( hostObjectIdentifier.getHandle(), wrapper.array() );
		
		// RelativePosition
		wrapper = new ByteWrapper( object.getRelativePosition().getEncodedLength() );
		object.getRelativePosition().encode(wrapper);
		map.put( relativePosition.getHandle(), wrapper.array() );
		
		// EmitterFunctionCode
		wrapper = new ByteWrapper( object.getEmitterFunctionCode().getEncodedLength() );
		object.getEmitterFunctionCode().encode(wrapper);
		map.put( emitterFunctionCode.getHandle(), wrapper.array() );
		
		// EmitterType
		wrapper = new ByteWrapper( object.getEmitterType().getEncodedLength() );
		object.getEmitterType().encode(wrapper);
		map.put( emitterType.getHandle(), wrapper.array() );
		
		// EmitterIndex
		wrapper = new ByteWrapper( object.getEmitterIndex().getEncodedLength() );
		object.getEmitterIndex().encode(wrapper);
		map.put( emitterIndex.getHandle(), wrapper.array() );
		
		// EventIdentifier
		wrapper = new ByteWrapper( object.getEventIdentifier().getEncodedLength() );
		object.getEventIdentifier().encode(wrapper);
		map.put( eventIdentifier.getHandle(), wrapper.array() );
		
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
		try
		{
			deserializeFromHla( rprSystem, event.attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
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

	private void deserializeFromHla( EmitterSystemRpr object, AttributeHandleValueMap map )
		throws DecoderException
	{
		// EntityIdentifier
		if( map.containsKey(entityIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(entityIdentifier.getHandle()) );
			object.getEntityIdentifier().decode( wrapper );
		}
		
		// HostObjectIdentifier
		if( map.containsKey(hostObjectIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hostObjectIdentifier.getHandle()) );
			object.getHostObjectIdentifier().decode( wrapper );
		}
		
		// RelativePosition
		if( map.containsKey(relativePosition.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(relativePosition.getHandle()) );
			object.getRelativePosition().decode( wrapper );
		}
		
		// EmitterFunctionCode
		if( map.containsKey(emitterFunctionCode.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(emitterFunctionCode.getHandle()) );
			object.getEmitterFunctionCode().decode( wrapper );
		}
		
		// EmitterType
		if( map.containsKey(emitterType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(emitterType.getHandle()) );
			object.getEmitterType().decode( wrapper );
		}
		
		// EmitterIndex
		if( map.containsKey(emitterIndex.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(emitterIndex.getHandle()) );
			object.getEmitterIndex().decode( wrapper );
		}
		
		// EventIdentifier
		if( map.containsKey(eventIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(eventIdentifier.getHandle()) );
			object.getEventIdentifier().decode( wrapper );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
