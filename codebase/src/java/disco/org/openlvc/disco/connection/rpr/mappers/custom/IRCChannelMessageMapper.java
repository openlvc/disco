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
package org.openlvc.disco.connection.rpr.mappers.custom;

import java.util.Arrays;
import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaInteraction;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.connection.rpr.objects.custom.IRCChannelMessage;
import org.openlvc.disco.pdu.custom.IrcMessagePdu;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

/**
 * Mapper to/from IRC FOM channel message interactions.
 */
public class IRCChannelMessageMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass hlaClass;
	private ParameterClass channelName;
	private ParameterClass sender;
	private ParameterClass message;
	private ParameterClass timeReceived;
	private ParameterClass origin;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.IRCMessage );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// Cache up the handles
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.Service.IRCChannelMessage" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.Service.IRCChannelMessage" );
		
		this.channelName    = hlaClass.getParameter( "ChannelName" );
		this.sender         = hlaClass.getParameter( "Sender" );
		this.message        = hlaClass.getParameter( "Message" );
		this.timeReceived   = hlaClass.getParameter( "TimeReceived" );
		this.origin         = hlaClass.getParameter( "Origin" );
		
		// Do publication and subscription
		super.publishAndSubscribe( hlaClass );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( IrcMessagePdu pdu )
	{
		// Populate the Interaction
		InteractionInstance interaction = serializeSetData( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}

	private InteractionInstance serializeSetData( IrcMessagePdu pdu )
	{
		// Create the interaction object
		IRCChannelMessage ircMessage = new IRCChannelMessage(); 
		ircMessage.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		ircMessage.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		ircMessage.setParameters( map );
		
		// Populate the Parameters
		// ChannelName
		ByteWrapper wrapper = new ByteWrapper( ircMessage.getChannelName().getEncodedLength() );
		ircMessage.getChannelName().encode( wrapper );
		map.put( channelName.getHandle(), wrapper.array() );
		
		// Sender
		wrapper = new ByteWrapper( ircMessage.getSender().getEncodedLength() );
		ircMessage.getSender().encode( wrapper );
		map.put( sender.getHandle(), wrapper.array() );
		
		// Message
		wrapper = new ByteWrapper( ircMessage.getMessage().getEncodedLength() );
		ircMessage.getMessage().encode( wrapper );
		map.put( message.getHandle(), wrapper.array() );
		
		// TimeReceived
		wrapper = new ByteWrapper( ircMessage.getTimeReceived().getEncodedLength() );
		ircMessage.getTimeReceived().encode( wrapper );
		map.put( timeReceived.getHandle(), wrapper.array() );
		
		// Origin
		wrapper = new ByteWrapper( ircMessage.getOrigin().getEncodedLength() );
		ircMessage.getOrigin().encode( wrapper );
		map.put( origin.getHandle(), wrapper.array() );
		
		// Send it
		return ircMessage;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		if( hlaClass == event.theClass )
		{
			IRCChannelMessage interaction = null;
			
			// Deserialize the parameters into an interaction instance
			try
			{
				interaction = deserializeIrcChannelMessage( event.parameters );
			}
			catch( DecoderException de )
			{
				throw new DiscoException( de.getMessage(), de );
			}

			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			opscenter.getPduReceiver().receive( interaction.toPdu().toByteArray() );
		}
	}

	private IRCChannelMessage deserializeIrcChannelMessage( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		IRCChannelMessage interaction = new IRCChannelMessage();

		if( map.containsKey(channelName.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(channelName.getHandle()) );
			interaction.getChannelName().decode( wrapper );
		}
		
		if( map.containsKey(sender.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(sender.getHandle()) );
			interaction.getSender().decode( wrapper );
		}
		
		if( map.containsKey(message.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(message.getHandle()) );
			interaction.getMessage().decode( wrapper );
		}
		
		if( map.containsKey(timeReceived.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(timeReceived.getHandle()) );
			interaction.getTimeReceived().decode( wrapper );
		}
		
		if( map.containsKey(origin.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(origin.getHandle()) );
			interaction.getOrigin().decode( wrapper );
		}

		return interaction;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
