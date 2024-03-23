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
import org.openlvc.disco.connection.rpr.interactions.EncodedAudioRadioSignal;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.radio.SignalPdu;

import hla.rti1516e.ParameterHandleValueMap;

public class SignalMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// Encoded Audio
	private InteractionClass hlaClass;
	private ParameterClass audioData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.Signal );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// EncodedAudio
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.RadioSignal.EncodedAudioRadioSignal" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.RadioSignal.EncodedAudioRadioSignal" );
		
		this.audioData = hlaClass.getParameter( "AudioData" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( hlaClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( SignalPdu pdu )
	{
		// Create a set of parameters to send
		ParameterHandleValueMap map = super.createParameters( this.hlaClass );

		// Populate an interaction instance from the PDU data
		InteractionInstance interaction = null;
		switch( pdu.getEncodingScheme().getEncodingClass() )
		{
			case EncodedVoice:
				interaction = serializeToAudioSignal( pdu, map );
				break;
			case RawBinaryData:
			case ApplicationSpecificData:
			case DatabaseIndex:
			default:
				break; // not supported
		}

		// Send the interaction
		super.sendInteraction( interaction, map );
	}
	
	private InteractionInstance serializeToAudioSignal( SignalPdu pdu, ParameterHandleValueMap map )
	{
		// Create the interaction object
		EncodedAudioRadioSignal hlaInteraction = new EncodedAudioRadioSignal();
		hlaInteraction.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		hlaInteraction.fromPdu( pdu );
		
		// Populdate the Parameters
		// AudioData
		hlaEncode( hlaInteraction.getAudioData(), audioData, map );
		
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
			InteractionInstance interaction = null;
			
			// Deserialize the parameters into the right signal type
			if( hlaClass.equals(this.hlaClass) )
				interaction = deserializeFromAudioSignal( event.parameters );
			else
				; // Unsupported Type
			
			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			opscenter.getPduReceiver().receive( interaction.toPdu().toByteArray() );
		}
	}

	private InteractionInstance deserializeFromAudioSignal( ParameterHandleValueMap map )
	{
		// Create an instance to decode in to
		EncodedAudioRadioSignal hlaInteraction = new EncodedAudioRadioSignal();
		
		// AudioData
		hlaDecode( hlaInteraction.getAudioData(), audioData, map );
		
		return hlaInteraction;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
