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
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.connection.rpr.objects.EncodedAudioRadioSignal;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.radio.SignalPdu;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class SignalMapper extends AbstractMapper implements IInteractionMapper
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
	public SignalMapper( RprConverter rprConverter )
	{
		super( rprConverter );
		
		// Cache up all the attributes we need
		
		// EncodedAudio
		this.hlaClass = rprConverter.model.getInteractionClass( "HLAinteractionRoot.RadioSignal.EncodedAudioRadioSignal" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAinteractionRoot.RadioSignal.EncodedAudioRadioSignal" );
		
		this.audioData = hlaClass.getParameter( "AudioData" );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public PduType getSupportedPduType()
	{
		return PduType.Signal;
	}
	
	@Override
	public InteractionClass getSupportedHlaClass()
	{
		return this.hlaClass;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendDisToHla( PDU genericPdu, RTIambassador rtiamb )
	{
		SignalPdu pdu = genericPdu.as( SignalPdu.class );

		InteractionInstance interaction = null;
		switch( pdu.getEncodingScheme().getEncodingClass() )
		{
			case EncodedVoice:
				interaction = serializeAudioSignal( pdu, rtiamb );
				break;
			case RawBinaryData:
			case ApplicationSpecificData:
			case DatabaseIndex:
			default:
				break; // not supported
		}

		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters(), rtiamb );
	}

	private InteractionInstance serializeAudioSignal( SignalPdu pdu, RTIambassador rtiamb )
	{
		// Create the interaction object
		EncodedAudioRadioSignal signal = new EncodedAudioRadioSignal();
		signal.setInteractionClass( hlaClass );
		
		// Populate it from the PDU
		signal.fromPdu( pdu );
		
		// Serialize it to a set of Parameters
		ParameterHandleValueMap map = super.createParameters( this.hlaClass, rtiamb );
		signal.setParameters( map );
		
		// Populdate the Parameters
		// AudioData
		ByteWrapper wrapper = new ByteWrapper( signal.getAudioData().getEncodedLength() );
		signal.getAudioData().encode( wrapper );
		map.put( audioData.getHandle(), wrapper.array() );
		
		// Send it
		return signal;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendHlaToDis( InteractionClass hlaClass,
	                          ParameterHandleValueMap parameters,
	                          OpsCenter opscenter )
	{
		InteractionInstance interaction = null;
		
		// Deserialize the parameters into the right signal type
		try
		{
    		if( hlaClass.equals(this.hlaClass) )
    			interaction = deserializeAudioSignal( parameters );
    		else
    			; // Unsupported Type
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		// Turn the attribute into a PDU
		PDU pdu = interaction.toPdu();
		
		// Send the PDU off
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		opscenter.getPduReceiver().receive( pdu.toByteArray() );
	}

	private InteractionInstance deserializeAudioSignal( ParameterHandleValueMap map )
		throws DecoderException
	{
		// Create an instance to decode in to
		EncodedAudioRadioSignal interaction = new EncodedAudioRadioSignal();
		
		if( map.containsKey(audioData.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(audioData.getHandle()) );
			interaction.getAudioData().decode( wrapper );
		}
		
		return interaction;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
