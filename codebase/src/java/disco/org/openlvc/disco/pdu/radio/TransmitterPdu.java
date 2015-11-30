/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.pdu.radio;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.AntennaPatternType;
import org.openlvc.disco.pdu.field.CryptoSystem;
import org.openlvc.disco.pdu.field.InputSource;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.TransmitState;
import org.openlvc.disco.pdu.record.AntennaLocation;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.ModulationType;
import org.openlvc.disco.pdu.record.PduHeader;
import org.openlvc.disco.pdu.record.RadioEntityType;
import org.openlvc.disco.utils.DisUnsignedInt64;

/**
 * This class represents a Receiver PDU.
 * <p/>
 * PDUs of this type contain information about...
 * 
 * @see "IEEE Std 1278.1-1995 section 4.5.7.4"
 */
public class TransmitterPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId entityID;
	private int radioID;
	private RadioEntityType radioEntityType;
	private TransmitState transmitState;
	private InputSource inputSource;
	private AntennaLocation antennaLocation;
	private AntennaPatternType antennaPatternType;
	private DisUnsignedInt64 transmissionFrequency;
	private float transmissionFrequencyBandwidth;
	private float power;
	private ModulationType modulationType;
	private CryptoSystem cryptoSystem;
	private int cryptoKey;
	private byte[] modulationParameter;
	private byte[] antennaPatternParameter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public TransmitterPdu( PduHeader header )
	{
		super( header );

		if( header.getPduType() != PduType.Transmitter )
			throw new IllegalStateException( "Expected Transmitter header, found "+header.getPduType() );
		
		this.entityID = new EntityId();
		this.radioID = 0;
		this.radioEntityType = new RadioEntityType();
		this.transmitState = TransmitState.Off;
		this.inputSource = InputSource.Other;
		this.antennaLocation = new AntennaLocation();
		this.transmissionFrequency = DisUnsignedInt64.ZERO;
		this.transmissionFrequencyBandwidth = 0f;
		this.power = 0f;
		this.cryptoSystem = CryptoSystem.Other;
		this.cryptoKey = 0;
		
		setModulation( new ModulationType(), new byte[0] );
		setAntennaPattern( AntennaPatternType.OmniDirectional, new byte[0] );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		entityID.from( dis );
		radioID = dis.readUI16();
		radioEntityType.from( dis );
		transmitState = TransmitState.fromValue( dis.readUI8() );
		inputSource = InputSource.fromValue( dis.readUI8() );
		
		// Padding
		dis.skip16();
				
		antennaLocation.from( dis );
		AntennaPatternType antennaPatternType = AntennaPatternType.fromValue( dis.readUI16() );
		int antennaPatternLength = dis.readUI16();
		transmissionFrequency.from( dis );
		transmissionFrequencyBandwidth = dis.readFloat();
		power = dis.readFloat();
		modulationType.from( dis );
		cryptoSystem = CryptoSystem.fromValue( dis.readUI16() );
		cryptoKey = dis.readUI16();
		
		short modulationParametersLength = dis.readUI8();
		
		// Padding
		dis.skip24();
		byte[] modulationParameter = new byte[modulationParametersLength];
		dis.readFully( modulationParameter );
		setModulation( modulationType, modulationParameter );
		
		byte[] antennaParameter = new byte[antennaPatternLength];
		dis.readFully( antennaParameter );
		setAntennaPattern( antennaPatternType, antennaParameter );
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		entityID.to( dos );
		dos.writeUI16( radioID );
		radioEntityType.to( dos );
		dos.writeUI8( transmitState.value() );
		dos.writeUI8( inputSource.value() );
		
		dos.writePadding16();
		
		antennaLocation.to( dos );
		dos.writeUI16( antennaPatternType.value() );
		// This will never be beyond the bounds of a UI16 due to input verification in the setter
		dos.writeUI16( antennaPatternParameter.length );
		transmissionFrequency.to( dos );
		dos.writeFloat( transmissionFrequencyBandwidth );
		dos.writeFloat( power );
		modulationType.to( dos );
		dos.writeUI16( cryptoSystem.value() );
		dos.writeUI16( cryptoKey );
				
		// This will never be beyond the bounds of a UI8 due to input verification in the setter
		dos.writeUI8( (short)modulationParameter.length );
		
		dos.writePadding24();
		dos.write( modulationParameter );
		dos.write( antennaPatternParameter );
	}
	
	@Override
	public int getContentLength()
	{
		int size = entityID.getByteLength();
		size += DisSizes.UI16_SIZE;	// Radio ID
		size += radioEntityType.getByteLength();
		size += TransmitState.getByteLength();
		size += InputSource.getByteLength();
		size += 2;								// Padding
		size += antennaLocation.getByteLength();
		size += AntennaPatternType.getByteLength();
		size += DisSizes.UI16_SIZE;				// Antenna Pattern Parameter Length
		size += transmissionFrequency.getByteLength();
		size += DisSizes.FLOAT32_SIZE;			// Bandwidth
		size += DisSizes.FLOAT32_SIZE;			// Power
		size += modulationType.getByteLength();
		size += CryptoSystem.getByteLength();
		size += DisSizes.UI16_SIZE;				// Crypto Key
		size += DisSizes.UI8_SIZE;				// Modulation Parameter Length
		size += 3;								// Padding
		size += modulationParameter.length;
		size += antennaPatternParameter.length;

		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getEntityIdentifier()
	{
		return entityID;
	}
	
	public void setEntityIdentifier( EntityId id )
	{
		entityID = id;
	}
	
	public int getRadioID()
	{
		return radioID;
	}
	
	public void setRadioID( int radioID )
	{
		this.radioID = radioID;
	}

	public RadioEntityType getRadioEntityType()
	{
		return radioEntityType;
	}
	
	public void setRadioEntityType( RadioEntityType radioEntityType )
	{
		this.radioEntityType = radioEntityType;
	}
	
	public TransmitState getTransmitState()
	{
		return transmitState;
	}
		
	public void setTransmitState( TransmitState transmitState )
	{
		this.transmitState = transmitState;
	}
	
	public InputSource getInputSource()
	{
		return inputSource;
	}
	
	public void setInputSource( InputSource inputSource )
	{
		this.inputSource = inputSource;
	}
	
	public AntennaPatternType getAntennaPatternType()
	{
		return antennaPatternType;
	}
	
	public byte[] getAntennaParameter()
	{
		return antennaPatternParameter;
	}
	
	public void setAntennaPattern( AntennaPatternType antennaPatternType, byte[] antennaParameter )
	{
		int antennaParameterLength = antennaParameter.length;
		if( (antennaParameterLength % 8) != 0 )
		{
			throw new IllegalArgumentException( "Antenna Parameter BLOB must be aligned to 64bit boundary" );
		}
		else if( antennaParameterLength > DisSizes.UI16_MAX_VALUE )
		{
			throw new IllegalArgumentException( "Antenna Parameter BLOB may not be larger than " + 
				DisSizes.UI16_MAX_VALUE + "bytes" );
		}
		
		this.antennaPatternType = antennaPatternType;
		this.antennaPatternParameter = antennaParameter;
	}
	
	public AntennaLocation getAntennaLocation()
	{
		return antennaLocation;
	}
	
	public void setAntennaLocation( AntennaLocation antennaLocation )
	{
		this.antennaLocation = antennaLocation;
	}
	
	public DisUnsignedInt64 getTransmissionFrequency()
	{
		return transmissionFrequency;
	}
	
	public void setTransmissionFrequency( DisUnsignedInt64 transmissionFrequency )
	{
		this.transmissionFrequency = transmissionFrequency;
	}
	
	public float getTransmissionFrequencyBandwidth()
	{
		return transmissionFrequencyBandwidth;
	}
	
	public void setTransmissionFrequencyBandwitch( float transmissionFrequencyBandwidth )
	{
		this.transmissionFrequencyBandwidth = transmissionFrequencyBandwidth;
	}
	
	public float getPower()
	{
		return power;
	}
	
	public void setPower( float power )
	{
		this.power = power;
	}
	
	public ModulationType getModulationType()
	{
		return modulationType;
	}
	
	public byte[] getModulationParameter()
	{
		return modulationParameter;
	}
	
	public void setModulation( ModulationType modulationType, byte[] modulationParameter )
	{
		int parameterLength = modulationParameter.length;
		if( (parameterLength % 8) != 0 )
		{
			throw new IllegalArgumentException( "Modulation Parameter BLOB must be aligned to "+
			                                 "64bit boundary" );
		}
		else if( parameterLength > DisSizes.UI8_MAX_VALUE )
		{
			throw new IllegalArgumentException( "Modulation Parameter BLOB may not be larger than " + 
				DisSizes.UI8_MAX_VALUE + "bytes" );
		}
		
		this.modulationType = modulationType;
		this.modulationParameter = modulationParameter;
	}
	
	public CryptoSystem getCryptoSystem()
	{
		return cryptoSystem;
	}
	
	public void setCryptoSystem( CryptoSystem cryptoSystem )
	{
		this.cryptoSystem = cryptoSystem;
	}
	
	public int getCryptoKey()
	{
		return cryptoKey;
	}
	
	public void setCryptoKey( int cryptoKey )
	{
		this.cryptoKey = cryptoKey;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
