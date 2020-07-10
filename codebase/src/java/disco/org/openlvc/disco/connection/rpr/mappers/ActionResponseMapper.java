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
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.connection.rpr.objects.ActionResponse;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.pdu.simman.ActionResponsePdu;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class ActionResponseMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass hlaClass;
	private ParameterClass   originatingEntity;
	private ParameterClass   receivingEntity;
	private ParameterClass   requestIdentifier;
	private ParameterClass   requestStatus;
	private ParameterClass   fixedDatums;
	private ParameterClass   variableDatumSet;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ActionResponseMapper( RprConnection connection )
	{
		super( connection );
		initializeHandles();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void initializeHandles() throws DiscoException
	{
		// EncodedAudio
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.ActionResponse" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.ActionResponse" );
		
		this.originatingEntity = hlaClass.getParameter( "OriginatingEntity" );
		this.receivingEntity   = hlaClass.getParameter( "ReceivingEntity" );
		this.requestIdentifier = hlaClass.getParameter( "RequestIdentifier" );
		this.requestStatus     = hlaClass.getParameter( "RequestStatus" );
		this.fixedDatums       = hlaClass.getParameter( "FixedDatums" );
		this.variableDatumSet  = hlaClass.getParameter( "VariableDatumSet" );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( ActionResponsePdu pdu )
	{
		InteractionInstance interaction = serializeActionResponse( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}
	
	private InteractionInstance serializeActionResponse( ActionResponsePdu pdu )
	{
		// Create the interaction object
		ActionResponse response = new ActionResponse();
		response.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		response.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		response.setParameters( map );
		
		// Populate the parameters
		// OriginatingEntity
		ByteWrapper wrapper = new ByteWrapper( response.getOriginatingEntity().getEncodedLength() );
		response.getOriginatingEntity().encode( wrapper );
		map.put( originatingEntity.getHandle(), wrapper.array() );
		
		// ReceivingEntity
		wrapper = new ByteWrapper( response.getReceivingEntity().getEncodedLength() );
		response.getReceivingEntity().encode( wrapper );
		map.put( receivingEntity.getHandle(), wrapper.array() );
		
		// RequestIdentifier
		wrapper = new ByteWrapper( response.getRequestIdentifier().getEncodedLength() );
		response.getRequestIdentifier().encode( wrapper );
		map.put( requestIdentifier.getHandle(), wrapper.array() );
		
		// RequestStatus
		wrapper = new ByteWrapper( response.getRequestStatus().getEncodedLength() );
		response.getRequestStatus().encode( wrapper );
		map.put( requestStatus.getHandle(), wrapper.array() );

		// FixedDatums
		wrapper = new ByteWrapper( response.getFixedDatums().getEncodedLength() );
		response.getFixedDatums().encode( wrapper );
		map.put( fixedDatums.getHandle(), wrapper.array() );
		
		// VariableDatumSet
		wrapper = new ByteWrapper( response.getVariableDatumSet().getEncodedLength() );
		response.getVariableDatumSet().encode( wrapper );
		map.put( variableDatumSet.getHandle(), wrapper.array() );

		// Send it
		return response;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		if( hlaClass == event.theClass )
		{
			InteractionInstance interaction = null;
			
			// Deserialize the parameters into an ActionResponse instance
			try
			{
				interaction = deserializeActionResponse( event.parameters );
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

	private InteractionInstance deserializeActionResponse( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		ActionResponse interaction = new ActionResponse();
		
		if( map.containsKey(originatingEntity.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(originatingEntity.getHandle()) );
			interaction.getOriginatingEntity().decode( wrapper );
		}
		
		if( map.containsKey(receivingEntity.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(receivingEntity.getHandle()) );
			interaction.getReceivingEntity().decode( wrapper );
		}
		
		if( map.containsKey(requestIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(requestIdentifier.getHandle()) );
			interaction.getRequestIdentifier().decode( wrapper );
		}
		
		if( map.containsKey(requestStatus.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(requestStatus.getHandle()) );
			interaction.getRequestStatus().decode( wrapper );
		}
		
		if( map.containsKey(fixedDatums.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(fixedDatums.getHandle()) );
			interaction.getFixedDatums().decode( wrapper );
		}
		
		if( map.containsKey(variableDatumSet.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(variableDatumSet.getHandle()) );
			interaction.getVariableDatumSet().decode( wrapper );
		}

		return interaction;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
