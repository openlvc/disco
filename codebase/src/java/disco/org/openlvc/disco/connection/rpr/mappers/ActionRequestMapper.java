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
import org.openlvc.disco.connection.rpr.interactions.ActionRequest;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.simman.ActionRequestPdu;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class ActionRequestMapper extends AbstractMapper
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
	private ParameterClass   actionRequestCode;
	private ParameterClass   fixedDatums;
	private ParameterClass   variableDatumSet;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.ActionRequest );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// ActionRequest
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.ActionRequest" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.ActionRequest" );
		
		this.originatingEntity = hlaClass.getParameter( "OriginatingEntity" );
		this.receivingEntity   = hlaClass.getParameter( "ReceivingEntity" );
		this.requestIdentifier = hlaClass.getParameter( "RequestIdentifier" );
		this.actionRequestCode = hlaClass.getParameter( "ActionRequestCode" );
		this.fixedDatums       = hlaClass.getParameter( "FixedDatums" );
		this.variableDatumSet  = hlaClass.getParameter( "VariableDatumSet" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( this.hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( ActionRequestPdu pdu )
	{
		InteractionInstance interaction = serializeActionRequest( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}
	
	private InteractionInstance serializeActionRequest( ActionRequestPdu pdu )
	{
		// Create the interaction object
		ActionRequest request = new ActionRequest();
		request.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		request.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		request.setParameters( map );
		
		// Populate the parameters
		// OriginatingEntity
		ByteWrapper wrapper = new ByteWrapper( request.getOriginatingEntity().getEncodedLength() );
		request.getOriginatingEntity().encode( wrapper );
		map.put( originatingEntity.getHandle(), wrapper.array() );
		
		// ReceivingEntity
		wrapper = new ByteWrapper( request.getReceivingEntity().getEncodedLength() );
		request.getReceivingEntity().encode( wrapper );
		map.put( receivingEntity.getHandle(), wrapper.array() );
		
		// RequestIdentifier
		wrapper = new ByteWrapper( request.getRequestIdentifier().getEncodedLength() );
		request.getRequestIdentifier().encode( wrapper );
		map.put( requestIdentifier.getHandle(), wrapper.array() );
		
		// ActionRequestCode
		wrapper = new ByteWrapper( request.getActionRequestCode().getEncodedLength() );
		request.getActionRequestCode().encode( wrapper );
		map.put( actionRequestCode.getHandle(), wrapper.array() );

		// FixedDatums
		wrapper = new ByteWrapper( request.getFixedDatums().getEncodedLength() );
		request.getFixedDatums().encode( wrapper );
		map.put( fixedDatums.getHandle(), wrapper.array() );
		
		// VariableDatumSet
		wrapper = new ByteWrapper( request.getVariableDatumSet().getEncodedLength() );
		request.getVariableDatumSet().encode( wrapper );
		map.put( variableDatumSet.getHandle(), wrapper.array() );

		// Send it
		return request;
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
			
			// Deserialize the parameters into an ActionRequest instance
			try
			{
				interaction = deserializeActionRequest( event.parameters );
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

	private InteractionInstance deserializeActionRequest( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		ActionRequest interaction = new ActionRequest();
		
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
		
		if( map.containsKey(actionRequestCode.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(actionRequestCode.getHandle()) );
			interaction.getActionRequestCode().decode( wrapper );
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
