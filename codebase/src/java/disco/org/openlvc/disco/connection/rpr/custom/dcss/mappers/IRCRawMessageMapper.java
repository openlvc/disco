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
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.IRCRawMessage;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaInteraction;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.custom.IrcRawMessagePdu;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

/**
 * Mapper to/from IRC FOM channel message interactions.
 */
public class IRCRawMessageMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass hlaClass;
	private ParameterClass prefix;
	private ParameterClass command;
	private ParameterClass commandParameters;
	private ParameterClass timeReceived;
	private ParameterClass sender;
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
		return Arrays.asList( PduType.IRCRawMessage );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// Cache up the handles
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.Service.IRCRawlMessage" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.Service.IRCRawlMessage" );
		
		this.prefix             = hlaClass.getParameter( "Prefix" );
		this.command            = hlaClass.getParameter( "Command" );
		this.commandParameters  = hlaClass.getParameter( "CommandParameters" );
		this.timeReceived       = hlaClass.getParameter( "TimeReceived" );
		this.sender             = hlaClass.getParameter( "Sender" );
		this.origin             = hlaClass.getParameter( "Origin" );
		
		// Do publication and subscription
		super.publishAndSubscribe( hlaClass );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( IrcRawMessagePdu pdu )
	{
		// Populate the Interaction
		InteractionInstance interaction = serializeSetData( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}

	private InteractionInstance serializeSetData( IrcRawMessagePdu pdu )
	{
		// Create the interaction object
		IRCRawMessage ircMessage = new IRCRawMessage();
		ircMessage.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		ircMessage.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		ircMessage.setParameters( map );
		
		// Populate the Parameters
		// Prefix
		ByteWrapper wrapper = new ByteWrapper( ircMessage.getPrefix().getEncodedLength() );
		ircMessage.getPrefix().encode( wrapper );
		map.put( prefix.getHandle(), wrapper.array() );
		
		// Command
		wrapper = new ByteWrapper( ircMessage.getCommand().getEncodedLength() );
		ircMessage.getCommand().encode( wrapper );
		map.put( command.getHandle(), wrapper.array() );
		
		// CommandParameters
		wrapper = new ByteWrapper( ircMessage.getCommandParameters().getEncodedLength() );
		ircMessage.getCommandParameters().encode( wrapper );
		map.put( commandParameters.getHandle(), wrapper.array() );
		
		// TimeReceived
		wrapper = new ByteWrapper( ircMessage.getTimeReceived().getEncodedLength() );
		ircMessage.getTimeReceived().encode( wrapper );
		map.put( timeReceived.getHandle(), wrapper.array() );
		
		// Sender
		wrapper = new ByteWrapper( ircMessage.getSender().getEncodedLength() );
		ircMessage.getSender().encode( wrapper );
		map.put( sender.getHandle(), wrapper.array() );
		
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
			IRCRawMessage interaction = null;
			
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

	private IRCRawMessage deserializeIrcChannelMessage( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		IRCRawMessage interaction = new IRCRawMessage();

		if( map.containsKey(prefix.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(prefix.getHandle()) );
			interaction.getPrefix().decode( wrapper );
		}
		
		if( map.containsKey(command.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(command.getHandle()) );
			interaction.getCommand().decode( wrapper );
		}
		
		if( map.containsKey(commandParameters.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(commandParameters.getHandle()) );
			interaction.getCommandParameters().decode( wrapper );
		}
		
		if( map.containsKey(timeReceived.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(timeReceived.getHandle()) );
			interaction.getTimeReceived().decode( wrapper );
		}
		
		if( map.containsKey(sender.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(sender.getHandle()) );
			interaction.getSender().decode( wrapper );
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
