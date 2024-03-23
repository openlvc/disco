/*
 *   Copyright 2024 Open LVC Project.
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
package org.openlvc.disco.rpr.custom;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaDiscover;
import org.openlvc.disco.connection.rpr.mappers.HlaReflect;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.pdu.custom.CustomPdu;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;

public class CustomObjectClassMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass hlaClass;
	private AttributeClass customString;
	private Map<String,CustomObjectClass> instances;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public CustomObjectClassMapper()
	{
		this.instances = new HashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// CustomObjectClass
		this.hlaClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.CustomObjectClass" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.CustomObjectClass" );
		
		this.customString = hlaClass.getAttribute( "CustomString" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( hlaClass );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( CustomPdu pdu )
	{
		// Do we already have an object cached for this?
		CustomObjectClass hlaObject = instances.get( pdu.getCustomString() );
		
		// If there is no HLA object yet, we have to register one
		if( hlaObject == null )
		{
			// No object registered, do it now
			hlaObject = new CustomObjectClass();
			hlaObject.setObjectClass( this.hlaClass );
			super.registerObjectInstance( hlaObject );
			instances.put( pdu.getCustomString(), hlaObject );
		}
		
		// Suck the values out of the PDU and put into the object
		hlaObject.fromPdu( pdu );
		
		// Send an update for the object
		super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );
		
		if( logger.isTraceEnabled() )
			logger.trace( "dis >> hla (CustomPdu) Updated attributes for Custom Object Class: id=%s, handle=%s",
			              pdu.getCustomString(), hlaObject.getObjectHandle() );
	}

	private AttributeHandleValueMap serializeToHla( CustomObjectClass hlaObject )
	{
		AttributeHandleValueMap map = hlaObject.getObjectAttributes();
		
		// CustomString
		hlaEncode( hlaObject.getCustomString(), customString, map );

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
			CustomObjectClass hlaObject = new CustomObjectClass();
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
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
		if( (event.hlaObject instanceof CustomObjectClass) == false )
			return;
		
		CustomObjectClass hlaObject = (CustomObjectClass)event.hlaObject;
		
		// Update the local object representation from the received attributes
		deserializeFromHla( hlaObject, event.attributes );
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		opscenter.getPduReceiver().receive( event.hlaObject.toPdu().toByteArray() );
	}
	
	private void deserializeFromHla( CustomObjectClass hlaObject, AttributeHandleValueMap map )
	{
		// CustomString
		hlaDecode( hlaObject.getCustomString(), customString, map );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<PduType> getSupportedPdus()
	{
		HashSet<PduType> set = new HashSet<>();
		set.add( new CustomPdu().getType() );
		return set;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
