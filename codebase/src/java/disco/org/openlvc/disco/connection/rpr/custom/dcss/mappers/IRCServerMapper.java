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
package org.openlvc.disco.connection.rpr.custom.dcss.mappers;

import java.util.Collection;
import java.util.Collections;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.custom.dcss.objects.IRCServer;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaDiscover;
import org.openlvc.disco.connection.rpr.mappers.HlaReflect;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.simman.DataPdu;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class IRCServerMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// IRCServer
	private ObjectClass hlaClass;
	private AttributeClass serverId;
	private AttributeClass channels;
	private AttributeClass connectedNicks;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Collections.emptySet();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// IRCServer
		this.hlaClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.IRCServer" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.IRCServer" );
		
		this.serverId        = hlaClass.getAttribute( "ServerId" );
		this.channels        = hlaClass.getAttribute( "Channels" );
		this.connectedNicks  = hlaClass.getAttribute( "ConnectedNicks" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( DataPdu pdu )
	{
		// Check if we're interested in this PDU
		
		// Do we already have an object cached for this?
		IRCServer hlaObject = null; //objectStore.get...
		
		// If there is no HLA object yet, we have to register one
		if( hlaObject == null )
		{
			// No object registered, do it now
			hlaObject = new IRCServer();
			hlaObject.setObjectClass( this.hlaClass );
			super.registerObjectInstance( hlaObject );
			//objectStore.addLocal...
		}
		
		// Suck the values out of the PDU and put into the object
		hlaObject.fromPdu( pdu );
		
		// Send an update for the object
		super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );
		
//		if( logger.isTraceEnabled() )
//			logger.trace( "dis >> hla (PhysicalEntity) Updated attributes for entity: id=%s, handle=%s",
//			              pdu.getEntityID(), hlaObject.getObjectHandle() );
	}

	private AttributeHandleValueMap serializeToHla( IRCServer object )
	{
		AttributeHandleValueMap map = object.getObjectAttributes();
		
		// ServerId
		ByteWrapper wrapper = new ByteWrapper( object.getServerId().getEncodedLength() );
		object.getServerId().encode( wrapper );
		map.put( serverId.getHandle(), wrapper.array() );
		
		// Channels
		wrapper = new ByteWrapper( object.getChannels().getEncodedLength() );
		object.getChannels().encode(wrapper);
		map.put( channels.getHandle(), wrapper.array() );

		// ConnectedNicks
		wrapper = new ByteWrapper( object.getConnectedNicks().getEncodedLength() );
		object.getConnectedNicks().encode(wrapper);
		map.put( connectedNicks.getHandle(), wrapper.array() );

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
//			IRCServer hlaObject = new IRCServer();
//			hlaObject.setObjectClass( event.theClass );
//			hlaObject.setObjectHandle( event.theObject );
//			objectStore.addDiscoveredHlaObject( event.theObject, hlaObject );
//
//			if( logger.isDebugEnabled() )
//			{
//    			logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
//    			              event.theClass.getLocalName(),
//    			              event.theObject );
//			}
//			
//			// Request an attribute update for the object so that we can get everything we need
//			super.requestAttributeUpdate( hlaObject );
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		if( (event.hlaObject instanceof IRCServer) == false )
			return;
		
		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( (IRCServer)event.hlaObject, event.attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		opscenter.getPduReceiver().receive( event.hlaObject.toPdu().toByteArray() );
	}
	
	private void deserializeFromHla( IRCServer server, AttributeHandleValueMap map )
		throws DecoderException
	{
		// ServerId
		if( map.containsKey(serverId.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(serverId.getHandle()) );
			server.getServerId().decode( wrapper );
		}
		
		// Channels
		if( map.containsKey(channels.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(channels.getHandle()) );
			server.getChannels().decode( wrapper );
		}
		
		// ConnectedNicks
		if( map.containsKey(connectedNicks.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(connectedNicks.getHandle()) );
			server.getConnectedNicks().decode( wrapper );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
