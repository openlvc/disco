/*
 *   Copyright 2025 Open LVC Project.
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
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.interactions.StartResume;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.simman.StartResumePdu;

import hla.rti1516e.ParameterHandleValueMap;

public class StartResumeMapper extends AbstractMapper
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
	private ParameterClass   realWorldTime;
	private ParameterClass   requestIdentifier;
	private ParameterClass   simulationTime;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.StartResume );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// StartResume
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.StartResume" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.StartResume" );
		
		this.originatingEntity          = hlaClass.getParameter( "OriginatingEntity" );
		this.receivingEntity            = hlaClass.getParameter( "ReceivingEntity" );
		this.realWorldTime              = hlaClass.getParameter( "RealWorldTime" );
		this.requestIdentifier          = hlaClass.getParameter( "RequestIdentifier" );
		this.simulationTime             = hlaClass.getParameter( "SimulationTime" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( this.hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( StartResumePdu pdu )
	{
		// Create a set of parameters to send
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );

		// Populate an interaction instance from the PDU data
		InteractionInstance interaction = serializeToHla( pdu, map );

		// Send the interaction
		super.sendInteraction( interaction, map );
	}
	
	private InteractionInstance serializeToHla( StartResumePdu pdu, ParameterHandleValueMap map )
	{
		// Create the interaction object
		StartResume hlaInteraction = new StartResume();
		hlaInteraction.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		hlaInteraction.fromPdu( pdu );
		
		// Populate the parameters
		// OriginatingEntity
		hlaEncode( hlaInteraction.getOriginatingEntity(), originatingEntity, map );
		
		// ReceivingEntity
		hlaEncode( hlaInteraction.getReceivingEntity(), receivingEntity, map );

		// RealWorldTime
		hlaEncode( hlaInteraction.getRealWorldTime(), realWorldTime, map );
		
		// RequestIdentifier
		hlaEncode( hlaInteraction.getRequestIdentifier(), requestIdentifier, map );
		
		// SimulationTime
		hlaEncode( hlaInteraction.getSimulationTime(), simulationTime, map );
		
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
			InteractionInstance interaction = deserializeFromHla( event.parameters );
			
			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			opscenter.getPduReceiver().receive( interaction.toPdu().toByteArray() );
		}
	}

	private InteractionInstance deserializeFromHla( ParameterHandleValueMap map )
	{
		// Create an instance to decode in to
		StartResume hlaInteraction = new StartResume();

		// OriginatingEntity
		hlaDecode( hlaInteraction.getOriginatingEntity(), originatingEntity, map );
		
		// ReceivingEntity
		hlaDecode( hlaInteraction.getReceivingEntity(), receivingEntity, map );
		
		// RealWorldTime
		hlaDecode( hlaInteraction.getRealWorldTime(), realWorldTime, map );
		
		// RequestIdentifier
		hlaDecode( hlaInteraction.getRequestIdentifier(), requestIdentifier, map );
		
		// SimulationTime
		hlaDecode( hlaInteraction.getSimulationTime(), simulationTime, map );
		
		return hlaInteraction;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
