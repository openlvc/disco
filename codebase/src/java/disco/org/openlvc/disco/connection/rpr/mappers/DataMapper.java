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
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.connection.rpr.objects.Data;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.simman.DataPdu;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class DataMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// Data
	private InteractionClass hlaClass;
	private ParameterClass originatingEntity;
	private ParameterClass receivingEntity;
	private ParameterClass requestIdentifier;
	private ParameterClass fixedDatums;
	private ParameterClass variableDatumSet;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.Data );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// Data
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.Data" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.Data" );
		
		this.originatingEntity = hlaClass.getParameter( "OriginatingEntity" );
		this.receivingEntity   = hlaClass.getParameter( "ReceivingEntity" );
		this.requestIdentifier = hlaClass.getParameter( "RequestIdentifier" );
		this.fixedDatums       = hlaClass.getParameter( "FixedDatums" );
		this.variableDatumSet  = hlaClass.getParameter( "VariableDatumSet" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( this.hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( DataPdu pdu )
	{
		// Populate the Interaction
		InteractionInstance interaction = serializeData( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}
	
	private InteractionInstance serializeData( DataPdu pdu )
	{
		// Create the interaction object
		Data data = new Data(); 
		data.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		data.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		data.setParameters( map );
		
		// Populate the Parameters
		// OriginatingEntity
		ByteWrapper wrapper = new ByteWrapper( data.getOriginatingEntity().getEncodedLength() );
		data.getOriginatingEntity().encode( wrapper );
		map.put( originatingEntity.getHandle(), wrapper.array() );
		
		// ReceivingEntity
		wrapper = new ByteWrapper( data.getReceivingEntity().getEncodedLength() );
		data.getReceivingEntity().encode( wrapper );
		map.put( receivingEntity.getHandle(), wrapper.array() );

		// RequestIdentifier
		wrapper = new ByteWrapper( data.getRequestIdentifier().getEncodedLength() );
		data.getRequestIdentifier().encode( wrapper );
		map.put( requestIdentifier.getHandle(), wrapper.array() );

		// FixedDatums
		wrapper = new ByteWrapper( data.getFixedDatums().getEncodedLength() );
		data.getFixedDatums().encode( wrapper );
		map.put( fixedDatums.getHandle(), wrapper.array() );

		// VariableDatumSet
		wrapper = new ByteWrapper( data.getVariableDatumSet().getEncodedLength() );
		data.getVariableDatumSet().encode( wrapper );
		map.put( variableDatumSet.getHandle(), wrapper.array() );

		// Send it
		return data;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		if( hlaClass == event.theClass )
		{
			Data interaction = null;
			
			// Deserialize the parameters into an interaction instance
			try
			{
				interaction = deserializeData( event.parameters );
			}
			catch( DecoderException de )
			{
				throw new DiscoException( de.getMessage(), de );
			}

			// If the request ID is negative, discard it.
			// This shoudn't happen, because the spec says the request ID is unsigned.
			// However, VRF seems to be throwing these out, so let's discard them.
			if( interaction.getRequestIdentifier().getValue() < 0 )
			{
				if( logger.isTraceEnabled() )
				    logger.fatal( "hla >> dis (Data) Interaction has invalid request id (%d), discarding",
				                  interaction.getRequestIdentifier().getValue() );
				return;
			}

			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			opscenter.getPduReceiver().receive( interaction.toPdu().toByteArray() );
		}
	}

	private Data deserializeData( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		Data interaction = new Data();

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
