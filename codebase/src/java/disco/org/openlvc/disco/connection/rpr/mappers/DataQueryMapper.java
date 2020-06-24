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
import org.openlvc.disco.connection.rpr.interactions.DataQuery;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.simman.DataQueryPdu;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class DataQueryMapper extends AbstractMapper
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
	private ParameterClass timeInterval;
	private ParameterClass fixedDatumIdentifiers;
	private ParameterClass variableDatumIdentifiers;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.DataQuery );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// DataQuery
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.DataQuery" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.SetData" );
		
		this.originatingEntity         = hlaClass.getParameter( "OriginatingEntity" );
		this.receivingEntity           = hlaClass.getParameter( "ReceivingEntity" );
		this.requestIdentifier         = hlaClass.getParameter( "RequestIdentifier" );
		this.timeInterval              = hlaClass.getParameter( "TimeInterval" );
		this.fixedDatumIdentifiers     = hlaClass.getParameter( "FixedDatumIdentifiers" );
		this.variableDatumIdentifiers  = hlaClass.getParameter( "VariableDatumIdentifiers" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( this.hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( DataQueryPdu pdu )
	{
		// Populate the Interaction
		InteractionInstance interaction = serializeDataQuery( pdu );

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}
	
	private InteractionInstance serializeDataQuery( DataQueryPdu pdu )
	{
		// Create the interaction object
		DataQuery dataQuery = new DataQuery(); 
		dataQuery.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		dataQuery.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );
		dataQuery.setParameters( map );
		
		// Populate the Parameters
		// OriginatingEntity
		ByteWrapper wrapper = new ByteWrapper( dataQuery.getOriginatingEntity().getEncodedLength() );
		dataQuery.getOriginatingEntity().encode( wrapper );
		map.put( originatingEntity.getHandle(), wrapper.array() );
		
		// ReceivingEntity
		wrapper = new ByteWrapper( dataQuery.getReceivingEntity().getEncodedLength() );
		dataQuery.getReceivingEntity().encode( wrapper );
		map.put( receivingEntity.getHandle(), wrapper.array() );

		// RequestIdentifier
		wrapper = new ByteWrapper( dataQuery.getRequestIdentifier().getEncodedLength() );
		dataQuery.getRequestIdentifier().encode( wrapper );
		map.put( requestIdentifier.getHandle(), wrapper.array() );

		// TimeInterval
		wrapper = new ByteWrapper( dataQuery.getTimeInterval().getEncodedLength() );
		dataQuery.getTimeInterval().encode( wrapper );
		map.put( timeInterval.getHandle(), wrapper.array() );

		// FixedDatums
		wrapper = new ByteWrapper( dataQuery.getFixedDatumIdentifiers().getEncodedLength() );
		dataQuery.getFixedDatumIdentifiers().encode( wrapper );
		map.put( fixedDatumIdentifiers.getHandle(), wrapper.array() );

		// VariableDatumSet
		wrapper = new ByteWrapper( dataQuery.getVariableDatumIdentifiers().getEncodedLength() );
		dataQuery.getVariableDatumIdentifiers().encode( wrapper );
		map.put( variableDatumIdentifiers.getHandle(), wrapper.array() );

		// Send it
		return dataQuery;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		if( hlaClass == event.theClass )
		{
			DataQuery interaction = null;
			
			// Deserialize the parameters into an interaction instance
			try
			{
				interaction = deserializeDataQuery( event.parameters );
			}
			catch( DecoderException de )
			{
				throw new DiscoException( de.getMessage(), de );
			}

			// If the request ID is negative, discard it.
			// This shoudn't happen, because the spec says the request ID is unsigned.
			// However, VRF seems to be throwing these out for Data interactions, so putting
			// some protections in place across Data/DataQuery/SetData.
			if( interaction.getRequestIdentifier().getValue() < 0 )
			{
				if( logger.isTraceEnabled() )
				    logger.trace( "hla >> dis (DataQuery) Interaction has invalid request id (%d), discarding",
				                  interaction.getRequestIdentifier().getValue() );
				return;
			}

			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			opscenter.getPduReceiver().receive( interaction.toPdu().toByteArray() );
		}
	}

	private DataQuery deserializeDataQuery( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		DataQuery interaction = new DataQuery();

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
		
		if( map.containsKey(timeInterval.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(timeInterval.getHandle()) );
			interaction.getTimeInterval().decode( wrapper );
		}
		
		if( map.containsKey(fixedDatumIdentifiers.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(fixedDatumIdentifiers.getHandle()) );
			interaction.getFixedDatumIdentifiers().decode( wrapper );
		}
		
		if( map.containsKey(variableDatumIdentifiers.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(variableDatumIdentifiers.getHandle()) );
			interaction.getVariableDatumIdentifiers().decode( wrapper );
		}

		return interaction;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
