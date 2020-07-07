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
package org.openlvc.disco.connection.rpr.objects;

import java.math.BigInteger;

import org.openlvc.disco.connection.rpr.types.array.AntennaPatternVariantStructLengthlessArray;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger64BE;
import org.openlvc.disco.connection.rpr.types.basic.StreamTag;
import org.openlvc.disco.connection.rpr.types.enumerated.CryptographicModeEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.CryptographicSystemTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.RFmodulationSystemTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.enumerated.RadioInputSourceEnum8;
import org.openlvc.disco.connection.rpr.types.enumerated.TransmitterOperationalStatusEnum8;
import org.openlvc.disco.connection.rpr.types.fixed.RadioTypeStruct;
import org.openlvc.disco.connection.rpr.types.fixed.WorldLocationStruct;
import org.openlvc.disco.connection.rpr.types.variant.AntennaPatternVariantStruct;
import org.openlvc.disco.connection.rpr.types.variant.RFmodulationTypeVariantStruct;
import org.openlvc.disco.connection.rpr.types.variant.SpreadSpectrumVariantStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.CryptoSystem;
import org.openlvc.disco.pdu.field.InputSource;
import org.openlvc.disco.pdu.field.MajorModulationType;
import org.openlvc.disco.pdu.field.ModulationSystem;
import org.openlvc.disco.pdu.field.TransmitState;
import org.openlvc.disco.pdu.radio.TransmitterPdu;

/**
 *  > HLAobjectRoot.EmbeddedSystem
 *     >> EntityIdentifier : EntityIdentifierStruct     
 *     >> HostObjectIdentifier : RTIobjectId
 *     >> RelativePosition : RelativePositionStruct
 *  > HLAobjectRoot.EmbeddedSystem.RadioTransmitter
 *     >> AntennaPatternData : AntennaPatternVariantStructLengthlessArray
 *     >> CryptographicMode : CryptographicModeEnum32
 *     >> CryptoSystem : CryptographicSystemTypeEnum16
 *     >> EncryptionKeyIdentifier : UnsignedInteger16
 *     >> Frequency : FrequencyHertzUnsignedInteger64
 *     >> FrequencyBandwidth : FrequencyHertzFloat32
 *     >> RadioIndex : UnsignedInteger16
 *     >> RadioInputSource : RadioInputSourceEnum8
 *     >> RadioSystemType : RadioTypeStruct
 *     >> RFModulationSystemType : RFModulationSystemTypeEnum16
 *     >> RFModulationType : RFModulationTypeVariantStruct
 *     >> SpreadSpectrum : SpreadSpectrumVariantStruct
 *     >> StreamTag : UnsignedInteger64
 *     >> TimeHopInUse : RPRboolean
 *     >> TransmittedPower : PowerRatioDecibelMilliwattFloat32
 *     >> TransmitterOperationalStatus : TransmitterOperationalStatusEnum8
 *     >> WorldLocation : WorldLocationStruct
 * 
 */
public class RadioTransmitter extends EmbeddedSystem
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private AntennaPatternVariantStructLengthlessArray antennaPatternDataArray; // FIXME
	private EnumHolder<CryptographicModeEnum32> cryptographicMode;
	private EnumHolder<CryptographicSystemTypeEnum16> cryptoSystem;
	private RPRunsignedInteger16BE encryptionKeyIdentifier;
	private RPRunsignedInteger64BE frequency;
	private HLAfloat32BE frequencyBandwidth;
	private RPRunsignedInteger16BE radioIndex;
	private EnumHolder<RadioInputSourceEnum8> radioInputSource;
	private RadioTypeStruct radioSystemType;
	private EnumHolder<RFmodulationSystemTypeEnum16> rfModulationSystemType;
	private RFmodulationTypeVariantStruct rfModulationType;
	private SpreadSpectrumVariantStruct spreadSpectrum;
	private RPRunsignedInteger64BE streamTag;
	private RPRboolean timeHopInUse;
	private HLAfloat32BE transmittedPower;
	private EnumHolder<TransmitterOperationalStatusEnum8> transmitterOperationalStatus;
	private WorldLocationStruct worldLocation;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RadioTransmitter()
	{
		super();
		
		this.antennaPatternDataArray = new AntennaPatternVariantStructLengthlessArray();
		this.antennaPatternDataArray.add( new AntennaPatternVariantStruct() );

		this.cryptographicMode = new EnumHolder<>( CryptographicModeEnum32.BasebandEncryption );
		this.cryptoSystem = new EnumHolder<>( CryptographicSystemTypeEnum16.Other );
		this.encryptionKeyIdentifier = new RPRunsignedInteger16BE(0);
		this.frequency = new RPRunsignedInteger64BE( BigInteger.ZERO );
		this.frequencyBandwidth = new HLAfloat32BE( 0.0f );
		this.radioIndex = new RPRunsignedInteger16BE(1);
		this.radioInputSource = new EnumHolder<>( RadioInputSourceEnum8.Other );
		this.radioSystemType = new RadioTypeStruct();
		this.rfModulationSystemType = new EnumHolder<>( RFmodulationSystemTypeEnum16.Other );
		this.rfModulationType = new RFmodulationTypeVariantStruct();
		this.spreadSpectrum = new SpreadSpectrumVariantStruct();
		this.streamTag = new RPRunsignedInteger64BE();
		this.timeHopInUse = new RPRboolean(false);
		this.transmittedPower = new HLAfloat32BE( 0.0f );
		this.transmitterOperationalStatus = new EnumHolder<>( TransmitterOperationalStatusEnum8.Off );
		this.worldLocation = new WorldLocationStruct();
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
		TransmitterPdu pdu = incoming.as( TransmitterPdu.class );
		
		// EntityIdentifier - Inherited
		// HostObjectId - Inherited
		// RelativePosition - Inherited
		super.fromPdu( pdu );
		
		// AntennaPatternData
		antennaPatternDataArray.get(0).setValue( pdu.getAntennaPatternType() );
		
		// CryptographicMode
		cryptographicMode.setEnum( CryptographicModeEnum32.valueOf(pdu.getCryptoKey()) );

		// CryptoSystem
		cryptoSystem.setEnum( CryptographicSystemTypeEnum16.valueOf(pdu.getCryptoSystem().value()) );
		
		// EncryptionKeyIdentifier
		encryptionKeyIdentifier.setValue( pdu.getCryptoKey() );
		
		// Frequency
		frequency.setValue( pdu.getTransmissionFrequency() );
		
		// FrequencyBandwidth
		frequencyBandwidth.setValue( pdu.getTransmissionFrequencyBandwidth() );
		
		// RadioIndex
		radioIndex.setValue( pdu.getRadioID() );
		
		// RadioInputSource
		radioInputSource.setEnum( RadioInputSourceEnum8.valueOf(pdu.getInputSource().value()) );
		
		// RadioSystemType
		radioSystemType.setValue( pdu.getRadioEntityType() );
		
		// RFModulationSystemType
		rfModulationSystemType.setEnum( RFmodulationSystemTypeEnum16.valueOf(pdu.getModulationType().getSystem().value()) );

		// RFModulationType
		rfModulationType.setValue( pdu.getModulationType().getMajorModulationType() ); // FIXME
		
		// SpreadSpectrum
		spreadSpectrum.setValue( pdu.getModulationType().getSpreadSpectrum() );
		
		// StreamTag
		streamTag.setValue( StreamTag.encode(pdu.getEntityId(),pdu.getRadioID()) );

		// TimeHopInUse
		timeHopInUse.setValue( pdu.getModulationType().getSpreadSpectrum().isTimeHopping() );
		
		// TransmittedPower
		transmittedPower.setValue( pdu.getPower() );
		
		// TransmitterOperationalStatus
		transmitterOperationalStatus.setEnum( TransmitterOperationalStatusEnum8.valueOf(pdu.getTransmitState().value()) );
		
		// WorldLocation
		worldLocation.setValue( pdu.getAntennaLocation().getAntennaLocation() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		TransmitterPdu pdu = new TransmitterPdu();
		
		// EntityIdentifier - Inherited
		// HostObjectId - Inherited
		// RelativePosition - Inherited
		super.toPdu( pdu );
		
		// AntennaPatternData - FIXME
		AntennaPatternVariantStruct as = getAntennaPatternDataArray().get(0);
		pdu.setAntennaPattern( as.getDisDiscriminant(), as.getDisValue() );
		
		// CryptographicMode
		pdu.setCryptoKey( (int)cryptographicMode.getEnum().getValue() );

		// CryptoSystem
		pdu.setCryptoSystem( CryptoSystem.fromValue(cryptoSystem.getEnum().getValue()) );
		
		// EncryptionKeyIdentifier
		pdu.setCryptoKey( encryptionKeyIdentifier.getValue() );
		
		// Frequency
		pdu.setTransmissionFrequency( frequency.getValue() );
		
		// FrequencyBandwidth
		pdu.setTransmissionFrequencyBandwith( frequencyBandwidth.getValue() );
		
		// RadioIndex
		pdu.setRadioID( radioIndex.getValue() );
		
		// RadioInputSource
		pdu.setInputSource( InputSource.fromValue(radioInputSource.getEnum().getValue()) );
		
		// RadioSystemType
		pdu.setRadioEntityType( radioSystemType.getDisValue() );
		
		// RFModulationSystemType
		pdu.getModulationType().setSystem( ModulationSystem.fromValue(rfModulationSystemType.getEnum().getValue()) );

		// RFModulationType
		pdu.getModulationType().setMajorModulationType( MajorModulationType.fromValue(rfModulationType.getDiscriminant().getValue()) );
		
		// SpreadSpectrum
		// FIXME
		
		// StreamTag
		// No-op

		// TimeHopInUse
		pdu.getModulationType().getSpreadSpectrum().setTimeHopping( timeHopInUse.getValue() );
		
		// TransmittedPower
		pdu.setPower( transmittedPower.getValue() );
		
		// TransmitterOperationalStatus
		pdu.setTransmitState( TransmitState.fromValue(transmitterOperationalStatus.getEnum().getValue()) );
		
		// WorldLocation
		pdu.getAntennaLocation().setAntennaLocation( worldLocation.getDisValue() );
		
		return pdu;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected boolean checkReady()
	{
		return super.entityIdentifier.isDecodeCalled();
	}

	public AntennaPatternVariantStructLengthlessArray getAntennaPatternDataArray()
	{
		return antennaPatternDataArray;
	}

	public EnumHolder<CryptographicModeEnum32> getCryptographicMode()
	{
		return cryptographicMode;
	}

	public EnumHolder<CryptographicSystemTypeEnum16> getCryptoSystem()
	{
		return cryptoSystem;
	}

	public RPRunsignedInteger16BE getEncryptionKeyIdentifier()
	{
		return encryptionKeyIdentifier;
	}

	public RPRunsignedInteger64BE getFrequency()
	{
		return frequency;
	}

	public HLAfloat32BE getFrequencyBandwidth()
	{
		return frequencyBandwidth;
	}

	public RPRunsignedInteger16BE getRadioIndex()
	{
		return radioIndex;
	}

	public EnumHolder<RadioInputSourceEnum8> getRadioInputSource()
	{
		return radioInputSource;
	}

	public RadioTypeStruct getRadioSystemType()
	{
		return radioSystemType;
	}

	public EnumHolder<RFmodulationSystemTypeEnum16> getRfModulationSystemType()
	{
		return rfModulationSystemType;
	}

	public RFmodulationTypeVariantStruct getRfModulationType()
	{
		return rfModulationType;
	}

	public SpreadSpectrumVariantStruct getSpreadSpectrum()
	{
		return spreadSpectrum;
	}

	public RPRunsignedInteger64BE getStreamTag()
	{
		return streamTag;
	}

	public RPRboolean getTimeHopInUse()
	{
		return timeHopInUse;
	}

	public HLAfloat32BE getTransmittedPower()
	{
		return transmittedPower;
	}

	public EnumHolder<TransmitterOperationalStatusEnum8> getTransmitterOperationalStatus()
	{
		return transmitterOperationalStatus;
	}

	public WorldLocationStruct getWorldLocation()
	{
		return worldLocation;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
