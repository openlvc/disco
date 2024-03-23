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
		//super.publishAndSubscribe( this.hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( DataQueryPdu pdu )
	{
		// Create a set of parameters to send
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );

		// Populate an interaction instance from the PDU data
		InteractionInstance interaction = serializeToHla( pdu, map );

		// Send the interaction
		super.sendInteraction( interaction, map );
	}
	
	private InteractionInstance serializeToHla( DataQueryPdu pdu, ParameterHandleValueMap map )
	{
		// Create the interaction object
		DataQuery hlaInteraction = new DataQuery(); 
		hlaInteraction.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		hlaInteraction.fromPdu( pdu );
		
		// Populate the Parameters
		// OriginatingEntity
		hlaEncode( hlaInteraction.getOriginatingEntity(), originatingEntity, map );
		
		// ReceivingEntity
		hlaEncode( hlaInteraction.getReceivingEntity(), receivingEntity, map );
		
		// RequestIdentifier
		hlaEncode( hlaInteraction.getRequestIdentifier(), requestIdentifier, map );
		
		// TimeInterval
		hlaEncode( hlaInteraction.getTimeInterval(), timeInterval, map );

		// FixedDatumIdentifiers
		hlaEncode( hlaInteraction.getFixedDatumIdentifiers(), fixedDatumIdentifiers, map );
		
		// VariableDatumIdentifiers
		hlaEncode( hlaInteraction.getVariableDatumIdentifiers(), variableDatumIdentifiers, map );

		// Send it
		return hlaInteraction;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		if( hlaClass == event.theClass )
		{
			DataQuery interaction = deserializeFromHla( event.parameters );

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

	private DataQuery deserializeFromHla( ParameterHandleValueMap map )
	{
		// Create an instance to decode in to
		DataQuery hlaInteraction = new DataQuery();

		// OriginatingEntity
		hlaDecode( hlaInteraction.getOriginatingEntity(), originatingEntity, map );
		
		// ReceivingEntity
		hlaDecode( hlaInteraction.getReceivingEntity(), receivingEntity, map );
		
		// RequestIdentifier
		hlaDecode( hlaInteraction.getRequestIdentifier(), requestIdentifier, map );
		
		// TimeInterval
		hlaDecode( hlaInteraction.getTimeInterval(), timeInterval, map );

		// FixedDatumIdentifiers
		hlaDecode( hlaInteraction.getFixedDatumIdentifiers(), fixedDatumIdentifiers, map );
		
		// VariableDatumIdentifiers
		hlaDecode( hlaInteraction.getVariableDatumIdentifiers(), variableDatumIdentifiers, map );

		return hlaInteraction;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
