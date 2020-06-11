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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.RprConnection;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.pdu.emissions.EmissionPdu;

public class EmitterSystemMapper extends AbstractMapper
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
		this.emitterIndex = hlaClass.getAttribute( "emitterIndex" );
		this.eventIdentifier = hlaClass.getAttribute( "eventIdentifier" );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( EmissionPdu pdu )
	{
		// Check to see if an HLA object already exists for this transmitter
//		RadioTransmitter hlaObject = objectStore.getLocalTransmitter( pdu.getEntityId() );
//		
//		// If there is no HLA object yet, we have to register one
//		if( hlaObject == null )
//		{
//			hlaObject = new RadioTransmitter();
//			hlaObject.setObjectClass( this.hlaClass );
//			RtiHelpers.registerObjectInstance( hlaObject, rprConnection.getRtiAmb() );
//			objectStore.addLocalTransmitter( pdu.getEntityId(), hlaObject );
//		}
//
//		// Suck the values out of the PDU and into the object
//		hlaObject.fromPdu( pdu );
//		
//		// Send an update for the object
//		RtiHelpers.sendAttributeUpdate( hlaObject,
//		                                serializeToHla(hlaObject),
//		                                rprConnection.getRtiAmb() );
//		
//		if( logger.isTraceEnabled() )
//			logger.trace( "(Transmitter) Updated attributes for transmitter: id=%s, handle=%s",
//			              pdu.getFullId(), hlaObject.getObjectHandle() );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( hlaClass == event.theClass )
		{
//			if( logger.isDebugEnabled() )
//			{
//    			logger.debug( "(HLA->DIS) Created [%s] for discovery of object handle [%s]",
//    			              event.theClass.getLocalName(),
//    			              event.theObject );
//			}
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
