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

import java.util.Arrays;
import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.custom.dcss.objects.IRCUser;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaDiscover;
import org.openlvc.disco.connection.rpr.mappers.HlaReflect;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.pdu.custom.IrcUserPdu;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class IRCUserMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// IRCServer
	private ObjectClass hlaClass;
	private AttributeClass userId;
	private AttributeClass userNick;
	private AttributeClass channels;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.IRCUser );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// IRCServer
		this.hlaClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.IRCUser" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.IRCUser" );
		
		this.userId   = hlaClass.getAttribute( "UserId" );
		this.userNick = hlaClass.getAttribute( "UserNick" );
		this.channels = hlaClass.getAttribute( "Channels" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( IrcUserPdu pdu )
	{
		// Do we already have an object cached for this?
		IRCUser hlaObject = objectStore.getLocalObject( pdu.getId() );
		
		// If there is no HLA object yet, we have to register one
		if( hlaObject == null )
		{
			// No object registered, do it now
			hlaObject = new IRCUser();
			hlaObject.setObjectClass( this.hlaClass );
			super.registerObjectInstance( hlaObject );
			objectStore.addLocalObject( pdu.getId(), hlaObject );
		}
		
		// Suck the values out of the PDU and put into the object
		hlaObject.fromPdu( pdu );
		
		// Send an update for the object
		super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );
		
		if( logger.isTraceEnabled() )
			logger.trace( "dis >> hla (IRCUser) Updated attributes for IRC user: id=%s, handle=%s",
			              pdu.getId(), hlaObject.getObjectHandle() );
	}

	private AttributeHandleValueMap serializeToHla( IRCUser object )
	{
		AttributeHandleValueMap map = object.getObjectAttributes();
		
		// UserId
		ByteWrapper wrapper = new ByteWrapper( object.getUserId().getEncodedLength() );
		object.getUserId().encode( wrapper );
		map.put( userId.getHandle(), wrapper.array() );
		
		// UserNick
		wrapper = new ByteWrapper( object.getUserNick().getEncodedLength() );
		object.getUserNick().encode(wrapper);
		map.put( userNick.getHandle(), wrapper.array() );

		// Rooms
		wrapper = new ByteWrapper( object.getRooms().getEncodedLength() );
		object.getRooms().encode(wrapper);
		map.put( channels.getHandle(), wrapper.array() );
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
			IRCUser hlaObject = new IRCUser();
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
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
		if( (event.hlaObject instanceof IRCUser) == false )
			return;
		
		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( (IRCUser)event.hlaObject, event.attributes );
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
	
	private void deserializeFromHla( IRCUser user, AttributeHandleValueMap map )
		throws DecoderException
	{
		// UserId
		if( map.containsKey(userId.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(userId.getHandle()) );
			user.getUserId().decode( wrapper );
		}
		
		// UserNick
		if( map.containsKey(userNick.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(userNick.getHandle()) );
			user.getUserNick().decode( wrapper );
		}
		
		// Rooms
		if( map.containsKey(channels.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(channels.getHandle()) );
			user.getRooms().decode( wrapper );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
