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
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.connection.rpr.objects.SetData;
import org.openlvc.disco.pdu.simman.SetDataPdu;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class SetDataMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// Set Data
	private InteractionClass hlaClass;
	private ParameterClass originatingEntity;
	private ParameterClass receivingEntity;
	private ParameterClass requestIdentifier;
	private ParameterClass fixedDatums;
	private ParameterClass variableDatumSet;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SetDataMapper( RprConnection connection )
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
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.SetData" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.SetData" );
		
		this.originatingEntity = hlaClass.getParameter( "OriginatingEntity" );
		this.receivingEntity   = hlaClass.getParameter( "ReceivingEntity" );
		this.requestIdentifier = hlaClass.getParameter( "RequestIdentifier" );
		this.fixedDatums       = hlaClass.getParameter( "FixedDatums" );
		this.variableDatumSet  = hlaClass.getParameter( "VariableDatumSet" );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( SetDataPdu pdu )
	{
		// Populate the Interaction
		InteractionInstance interaction = serializeSetData( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}
	
	private InteractionInstance serializeSetData( SetDataPdu pdu )
	{
		// Create the interaction object
		SetData setData = new SetData(); 
		setData.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		setData.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		setData.setParameters( map );
		
		// Populate the Parameters
		// OriginatingEntity
		ByteWrapper wrapper = new ByteWrapper( setData.getOriginatingEntity().getEncodedLength() );
		setData.getOriginatingEntity().encode( wrapper );
		map.put( originatingEntity.getHandle(), wrapper.array() );
		
		// ReceivingEntity
		wrapper = new ByteWrapper( setData.getReceivingEntity().getEncodedLength() );
		setData.getReceivingEntity().encode( wrapper );
		map.put( receivingEntity.getHandle(), wrapper.array() );

		// RequestIdentifier
		wrapper = new ByteWrapper( setData.getRequestIdentifier().getEncodedLength() );
		setData.getRequestIdentifier().encode( wrapper );
		map.put( requestIdentifier.getHandle(), wrapper.array() );

		// FixedDatums
		wrapper = new ByteWrapper( setData.getFixedDatums().getEncodedLength() );
		setData.getFixedDatums().encode( wrapper );
		map.put( fixedDatums.getHandle(), wrapper.array() );

		// VariableDatumSet
		wrapper = new ByteWrapper( setData.getVariableDatumSet().getEncodedLength() );
		setData.getVariableDatumSet().encode( wrapper );
		map.put( variableDatumSet.getHandle(), wrapper.array() );

		// Send it
		return setData;
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
			
			// Deserialize the parameters into an interaction instance
			try
			{
				interaction = deserializeSetData( event.parameters );
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

	private InteractionInstance deserializeSetData( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		SetData interaction = new SetData();

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
