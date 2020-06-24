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
package org.openlvc.disco.connection.rpr.interactions;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.basic.StreamTag;
import org.openlvc.disco.connection.rpr.types.fixed.AudioDataTypeStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.EncodingClass;
import org.openlvc.disco.pdu.field.EncodingType;
import org.openlvc.disco.pdu.radio.SignalPdu;

public class EncodedAudioRadioSignal extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private AudioDataTypeStruct audioData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EncodedAudioRadioSignal()
	{
		super();
		
		this.audioData = new AudioDataTypeStruct();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		SignalPdu pdu = incoming.as( SignalPdu.class );
		if( pdu.getEncodingScheme().getEncodingClass() != EncodingClass.EncodedVoice )
			throw new DiscoException( "Expected EncodedVoice Signal PDU" );

		// StreamTag
		audioData.setStreamTag( StreamTag.encode(pdu.getEntityId(),pdu.getRadioID()) );

		// EncodingType
		audioData.setEncodingType( pdu.getEncodingScheme().getEncodingType().value() );
		
		// SampleRate
		audioData.setSampleRate( pdu.getSampleRate() );
		
		// SampleCount
		audioData.setSampleCount( pdu.getSamples() );

		// Data
		audioData.setDataLength( pdu.getDataLength() );
		
		// DataLength
		audioData.setData( pdu.getData() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		SignalPdu pdu = new SignalPdu();
		
		// Entity Identification
		StreamTag.decode( audioData.getStreamTag(), pdu );
		
		// Encoding
		pdu.getEncodingScheme().setEncodingClass( EncodingClass.EncodedVoice );
		pdu.getEncodingScheme().setEncodingType( EncodingType.fromValue((short)audioData.getEncodingType()) );
		
		// SampleRate
		pdu.setSampleRate( audioData.getSampleRate() );
		
		// SampleCount
		pdu.setSamples( (int)audioData.getSampleCount() );
		
		// DataLength -- set in pdu.setData()
		// Data
		pdu.setData( audioData.getData() );
		
		return pdu;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public AudioDataTypeStruct getAudioData()
	{
		return audioData;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
}
