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
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.InhibitedMidsPairing;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaInteraction;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.custom.InhibitedMidsPairingPdu;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.DecoderException;

public class InhibitedMidsPairingMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// IRCServer
	private InteractionClass hlaClass;
	private ParameterClass tdlType;
	private ParameterClass sourceEntityId;
	private ParameterClass destinationEntityId;
	private ParameterClass isMidsTerminalEnabled;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.InhibitedMidsPairing );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// IRCServer
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.UpdateInhibitedMidsPairing" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find interaction: HLAinteractionRoot.UpdateInhibitedMidsPairing" );

		this.tdlType               = hlaClass.getParameter( "TDLType" );
		this.sourceEntityId        = hlaClass.getParameter( "SourceEntityID" );
		this.destinationEntityId   = hlaClass.getParameter( "DestinationEntityID" );
		this.isMidsTerminalEnabled = hlaClass.getParameter( "EnableDisableMIDSTerminal" );

		// Publish and Subscribe
		super.publishAndSubscribe( hlaClass );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( InhibitedMidsPairingPdu pdu )
	{
		// Populate the Interaction
		InteractionInstance interaction = serializeInhibitedMids( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}

	private InteractionInstance serializeInhibitedMids( InhibitedMidsPairingPdu pdu )
	{
		// Create the interaction object
		InhibitedMidsPairing interaction = new InhibitedMidsPairing();
		interaction.setInteractionClass( hlaClass );

		// Populate it from the PDU
		interaction.fromPdu( pdu );

		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		interaction.setParameters( map );
		
		serializeInto( interaction.getTdlType(), tdlType, map );
		serializeInto( interaction.getSourceEntityId(), sourceEntityId, map );
		serializeInto( interaction.getDestinationEntityId(), destinationEntityId, map );
		serializeInto( interaction.isMidsTerminalEnabled(), isMidsTerminalEnabled, map );
		
		return interaction;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		if( hlaClass == event.theClass )
		{
			InhibitedMidsPairing interaction = null;

			// Deserialize the parameters into an interaction instance
			try
			{
				interaction = deserializeFromHla( event.parameters );
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

	private InhibitedMidsPairing deserializeFromHla( ParameterHandleValueMap map )
		throws DecoderException
	{
		InhibitedMidsPairing interaction = new InhibitedMidsPairing();

		deserializeInto( map, tdlType, interaction.getTdlType() );
		deserializeInto( map, sourceEntityId, interaction.getSourceEntityId() );
		deserializeInto( map, destinationEntityId, interaction.getDestinationEntityId() );
		deserializeInto( map, isMidsTerminalEnabled, interaction.isMidsTerminalEnabled() );

		return interaction;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
