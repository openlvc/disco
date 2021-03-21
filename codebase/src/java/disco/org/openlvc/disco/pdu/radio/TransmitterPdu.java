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
import java.math.BigInteger;
import java.util.Arrays;

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
import org.openlvc.disco.pdu.record.FullRadioId;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.ModulationType;
import org.openlvc.disco.pdu.record.RadioEntityType;

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
	private int radioId;
	private RadioEntityType radioEntityType;
	private TransmitState transmitState;
	private InputSource inputSource;
	private AntennaLocation antennaLocation;
	private AntennaPatternType antennaPatternType;
	private BigInteger transmissionFrequency;
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
	public TransmitterPdu()
	{
		super( PduType.Transmitter );

		this.entityID = new EntityId();
		this.radioId = 0;
		this.radioEntityType = new RadioEntityType();
		this.transmitState = TransmitState.Off;
		this.inputSource = InputSource.Other;
		this.antennaLocation = new AntennaLocation();
		this.transmissionFrequency = BigInteger.ZERO;
		this.transmissionFrequencyBandwidth = 0f;
		this.power = 0f;
		this.cryptoSystem = CryptoSystem.None;
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
		radioId = dis.readUI16();
		radioEntityType.from( dis );
		transmitState = TransmitState.fromValue( dis.readUI8() );
		inputSource = InputSource.fromValue( dis.readUI8() );
		
		// Padding
		dis.skip16();
				
		antennaLocation.from( dis );
		AntennaPatternType antennaPatternType = AntennaPatternType.fromValue( dis.readUI16() );
		int antennaPatternLength = dis.readUI16();
		transmissionFrequency = dis.readUI64();
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
		dos.writeUI16( radioId );
		radioEntityType.to( dos );
		dos.writeUI8( transmitState.value() );
		dos.writeUI8( inputSource.value() );
		
		dos.writePadding16();
		
		antennaLocation.to( dos );
		dos.writeUI16( antennaPatternType.value() );
		// This will never be beyond the bounds of a UI16 due to input verification in the setter
		dos.writeUI16( antennaPatternParameter.length );
		dos.writeUI64( transmissionFrequency );
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
		return 92 +
		       modulationParameter.length +
		       antennaPatternParameter.length;
		
		// int size = entityID.getByteLength();                                           // 6
		// size += DisSizes.UI16_SIZE;	// Radio ID                                       // 2
		// size += radioEntityType.getByteLength();                                       // 8
		// size += TransmitState.getByteLength();                                         // 1
		// size += InputSource.getByteLength();                                           // 1
		// size += 2;								// Padding                            // 2
		// size += antennaLocation.getByteLength();                                       // 36
		// size += AntennaPatternType.getByteLength();                                    // 2
		// size += DisSizes.UI16_SIZE;				// Antenna Pattern Parameter Length   // 2
		// size += transmissionFrequency.getByteLength();                                 // 8
		// size += DisSizes.FLOAT32_SIZE;			// Bandwidth                          // 4
		// size += DisSizes.FLOAT32_SIZE;			// Power                              // 4
		// size += modulationType.getByteLength();                                        // 8
		// size += CryptoSystem.getByteLength();                                          // 2
		// size += DisSizes.UI16_SIZE;				// Crypto Key                         // 2
		// size += DisSizes.UI8_SIZE;				// Modulation Parameter Length        // 1 
		// size += 3;								// Padding                            // 3
		// size += modulationParameter.length;                                            // x
		// size += antennaPatternParameter.length;                                        // x
		// return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** @return String in form {siteId}-{appId}-{entityId}-{radioId} */
	public String getFullId()
	{
		return entityID.toString()+"-"+radioId;
	}
	
	/** Just a dup of {@link #getEntityIdentifier()} with a shorter name for compactness. */
	public EntityId getEntityId()
	{
		return entityID;
	}

	public EntityId getEntityIdentifier()
	{
		return entityID;
	}
	
	public FullRadioId getFullRadioId()
	{
		return new FullRadioId( this.entityID, this.radioId );
	}
	
	public void setEntityId( EntityId id )
	{
		entityID = id;
	}

	public void setEntityIdentifier( EntityId id )
	{
		entityID = id;
	}
	
	public int getRadioID()
	{
		return radioId;
	}
	
	public void setRadioID( int radioId )
	{
		this.radioId = radioId;
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
			throw new IllegalArgumentException( "Antenna Parameter BLOB must be aligned to 64bit boundary ["+
			                                    antennaParameterLength+" bytes]" );
		}
		else if( antennaParameterLength > DisSizes.UI16_MAX_VALUE )
		{
			throw new IllegalArgumentException( "Antenna Parameter BLOB may not be larger than " + 
				DisSizes.UI16_MAX_VALUE + "bytes ["+antennaParameterLength+" bytes]" );
		}
		
		this.antennaPatternType = antennaPatternType;
		this.antennaPatternParameter = Arrays.copyOf( antennaParameter, antennaParameterLength );
	}
	
	public AntennaLocation getAntennaLocation()
	{
		return antennaLocation;
	}
	
	public void setAntennaLocation( AntennaLocation antennaLocation )
	{
		this.antennaLocation = antennaLocation;
	}
	
	public BigInteger getTransmissionFrequency()
	{
		return transmissionFrequency;
	}
	
	public void setTransmissionFrequency( BigInteger transmissionFrequency )
	{
		this.transmissionFrequency = transmissionFrequency;
	}
	
	public float getTransmissionFrequencyBandwidth()
	{
		return transmissionFrequencyBandwidth;
	}
	
	public void setTransmissionFrequencyBandwith( float transmissionFrequencyBandwidth )
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
		this.modulationParameter = Arrays.copyOf( modulationParameter, parameterLength );
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
	
	@Override
	public int getSiteId()
	{
		return entityID.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return entityID.getAppId();
	}

	@Override
	public String toString()
	{
		return this.entityID+"-"+this.radioId;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper/Shortcut Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isTransmitting()
	{
		return this.transmitState == TransmitState.OnAndTransmitting;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
