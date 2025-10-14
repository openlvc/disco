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
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.RadioTransmitter;
import org.openlvc.disco.connection.rpr.types.enumerated.MajorRFModulationTypeEnum16;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.AttributeHandleValueMap;

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
public class TransmitterMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// HLA Handle Information
	private ObjectClass hlaClass;
	// EmbeddedSystem
	private AttributeClass entityIdentifier;
	private AttributeClass hostObjectIdentifier;
	private AttributeClass relativePosition;
	// EmbeddedSystem.RadioTransmitter
	private AttributeClass antennaPatternData;
	private AttributeClass cryptographicMode;
	private AttributeClass cryptoSystem;
	private AttributeClass encryptionKeyIdentifier;
	private AttributeClass frequency;
	private AttributeClass frequencyBandwidth;
	private AttributeClass radioIndex;
	private AttributeClass radioInputSource;
	private AttributeClass radioSystemType;
	private AttributeClass rfModulationSystemType;
	private AttributeClass rfModulationType;
	private AttributeClass spreadSpectrum;
	private AttributeClass streamTag;
	private AttributeClass timeHopInUse;
	private AttributeClass transmittedPower;
	private AttributeClass transmitterOperationalStatus;
	private AttributeClass worldLocation;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.Transmitter );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// Cache up all the attributes we need
		this.hlaClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.EmbeddedSystem.RadioTransmitter" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.EmbeddedSystem.RadioTransmitter" );
		
		this.entityIdentifier = hlaClass.getAttribute( "EntityIdentifier" );
		this.hostObjectIdentifier = hlaClass.getAttribute( "HostObjectIdentifier" );
		this.relativePosition = hlaClass.getAttribute( "RelativePosition" );
			
		this.antennaPatternData = hlaClass.getAttribute( "AntennaPatternData" );
		this.cryptographicMode = hlaClass.getAttribute( "CryptographicMode" );
		this.cryptoSystem = hlaClass.getAttribute( "CryptoSystem" );
		this.encryptionKeyIdentifier = hlaClass.getAttribute( "EncryptionKeyIdentifier" );
		this.frequency = hlaClass.getAttribute( "Frequency" );
		this.frequencyBandwidth = hlaClass.getAttribute( "FrequencyBandwidth" );
		this.radioIndex = hlaClass.getAttribute( "RadioIndex" );
		this.radioInputSource = hlaClass.getAttribute( "RadioInputSource" );
		this.radioSystemType = hlaClass.getAttribute( "RadioSystemType" );
		this.rfModulationSystemType = hlaClass.getAttribute( "RFModulationSystemType" );
		this.rfModulationType = hlaClass.getAttribute( "RFModulationType" );
		this.spreadSpectrum = hlaClass.getAttribute( "SpreadSpectrum" );
		this.streamTag = hlaClass.getAttribute( "StreamTag" );
		this.timeHopInUse = hlaClass.getAttribute( "TimeHopInUse" );
		this.transmittedPower = hlaClass.getAttribute( "TransmittedPower" );
		this.transmitterOperationalStatus = hlaClass.getAttribute( "TransmitterOperationalStatus" );
		this.worldLocation = hlaClass.getAttribute( "WorldLocation" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( this.hlaClass );
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( TransmitterPdu pdu )
	{
		// Check to see if an HLA object already exists for this transmitter
		RadioTransmitter hlaObject = findLocalTransmitter( pdu.getEntityId(), pdu.getRadioID() );
		
		// If there is no HLA object yet, we have to register one
		if( hlaObject == null )
		{
			hlaObject = new RadioTransmitter();
			hlaObject.setObjectClass( this.hlaClass );
			super.registerObjectInstance( hlaObject );
			objectStore.addLocalTransmitter( pdu.getEntityId(), pdu.getRadioID(), hlaObject );
		}

		// Suck the values out of the PDU and into the object
		hlaObject.fromPdu( pdu );
		
		// Additional Items
		// EmbeddedSystem HostObjectIdentifier: Need to look this up in the store
		hlaObject.setHostObjectIdentifier( objectStore.getRtiIdForDisId(pdu.getEntityId()) );
		
		// Send an update for the object
		super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );
		
		if( logger.isTraceEnabled() )
			logger.trace( "dis >> hla (Transmitter) Updated attributes for transmitter: id=%s, handle=%s",
			              pdu.getFullId(), hlaObject.getObjectHandle() );
	}
	
	private AttributeHandleValueMap serializeToHla( RadioTransmitter hlaObject )
	{
		AttributeHandleValueMap map = hlaObject.getObjectAttributes();
		
		// EntityIdentifier
		hlaEncode( hlaObject.getEntityIdentifier(), entityIdentifier, map );
		
		// HostObjectIdentifier
		hlaEncode( hlaObject.getHostObjectIdentifier(), hostObjectIdentifier, map );
		
		// RelativePosition
		hlaEncode( hlaObject.getRelativePosition(), relativePosition, map );
		
		// Antenna Pattern Data
		hlaEncode( hlaObject.getAntennaPatternDataArray(), antennaPatternData, map );
		
		// CryptographicMode
		hlaEncode( hlaObject.getCryptographicMode(), cryptographicMode, map );

		// CryptographicMode
		hlaEncode( hlaObject.getCryptoSystem(), cryptoSystem, map );

		// EncryptionKeyIdentifier
		hlaEncode( hlaObject.getEncryptionKeyIdentifier(), encryptionKeyIdentifier, map );
		
		// Frequency
		hlaEncode( hlaObject.getFrequency(), frequency, map );
		
		// FrequencyBandwidth
		hlaEncode( hlaObject.getFrequencyBandwidth(), frequencyBandwidth, map );
		
		// RadioIndex
		hlaEncode( hlaObject.getRadioIndex(), radioIndex, map );
		
		// RadioInputSource
		hlaEncode( hlaObject.getRadioInputSource(), radioInputSource, map );
		
		// RadioSystemType
		hlaEncode( hlaObject.getRadioSystemType(), radioSystemType, map );
		
		// RFModulationSystemType
		hlaEncode( hlaObject.getRfModulationSystemType(), rfModulationSystemType, map );

		// RFModulationType
		hlaEncode( hlaObject.getRfModulationType(), rfModulationType, map );
		
		// SpreadSpectrum
		hlaEncode( hlaObject.getSpreadSpectrum(), spreadSpectrum, map );
		
		// StreamTag
		hlaEncode( hlaObject.getStreamTag(), streamTag, map );

		// TimeHopInUse
		hlaEncode( hlaObject.getTimeHopInUse(), timeHopInUse, map );
		
		// TransmittedPower
		hlaEncode( hlaObject.getTransmittedPower(), transmittedPower, map );
		
		// TransmitterOperationalStatus
		hlaEncode( hlaObject.getTransmitterOperationalStatus(), transmitterOperationalStatus, map );
		
		// WorldLocation
		hlaEncode( hlaObject.getWorldLocation(), worldLocation, map );
		
		return map;
	}

	/**
	 * Find the locally created RadioTransmitter for the given entity/radio id combination. If we
	 * cannot find an object with that id, we will try some fallbacks, but if we ultimately strike
	 * out, null will be returned. 
	 * 
	 * @param entityId The entity id for the transmitter
	 * @param radioId  The unique radio id for the transmitter
	 * @return The found RadioTransmitter from the locally created store with the given id, or null
	 *         if there is none bound with that id (or any of the fallbacks).
	 */
	private RadioTransmitter findLocalTransmitter( EntityId entityId, int radioId )
	{
		// Try to find a previously registered object under its full id
		RadioTransmitter hlaObject = objectStore.getLocalTransmitter( entityId, radioId );
		
		// HACKS FOR CNR
		// When a radio attaches to an entity, it will mirror its site/app/entity-id. This is fine,
		// until it changes the entity it is attached to (common during startup as a radio goes from
		// unattached to attached.
		// 
		// The problem is that on the HLA side there is a persistent RadioTransmitter object in the
		// world, but on the DIS side we just get a PDU that one moment has one ID, and the next 
		// moment it has a different one. We can't _know_ that these represent the same radio.
		// As such, because we look up based on ID, when the ID changes we think there IS NOT an
		// existing object that we've registered, and that will cause us to create a new one.
		//
		// CNR specifically, depending on version, defaults its site/app/entity-id to one of a few
		// specific combinations. If we don't find a transmitter, we'll also check under these ids
		// just to be sure, discriminating on the radio id component (the 4th number).
		//
		// If we _do_ find a transmitter using a default id, we need to update the store so that
		// the radio is managed under its new id, not the old one
		//
		// Its not pretty, but it may cover some cracks.
		//
		if( hlaObject == null )
		{
			// The list of generic ids to use for subsequent lookups
			EntityId[] possibles = new EntityId[] {
			    new EntityId(1,1,0),
			    new EntityId(1,1,1),
			    new EntityId(0,0,0)
			};
			
			// Perform a lookup until we find (or don't) a match using the generic ids
			for( EntityId possible : possibles )
			{
				hlaObject = objectStore.getLocalTransmitter( possible, radioId );
				if( hlaObject != null )
					break;
			}
			
			// We got a match! We need to update the ID in the store
			if( hlaObject != null )
			{
				objectStore.updateLocalTransmitterId( hlaObject,
				                                      radioId,
				                                      hlaObject.getDisId(), // old
				                                      entityId );           // new
			}
		}

		return hlaObject;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( hlaClass == event.theClass )
		{
			// Create the object and store it locally
			RadioTransmitter hlaObject = new RadioTransmitter();
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			hlaObject.setObjectName( event.objectName );
			hlaObject.setObjectAttributes( super.createAttributes(this.hlaClass) );
			objectStore.addDiscoveredHlaObject( hlaObject );

			if( logger.isDebugEnabled() )
			{
    			logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
    			              event.theClass.getLocalName(),
    			              event.theObject );
			}
			
			// Request an attribute update for the object so that we can get everything we need
			super.requestAttributeUpdate( hlaObject );
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		if( (event.hlaObject instanceof RadioTransmitter) == false )
			return;
		
		RadioTransmitter rprTransmitter = (RadioTransmitter)event.hlaObject;

		// Update the local object representation from the received attributes
		deserializeFromHla( rprTransmitter, event.attributes );
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		if( isReady(rprTransmitter) )
		{
			opscenter.getPduReceiver().receive( event.hlaObject.toPdu().toByteArray() );
			event.hlaObject.setLastUpdatedTimeToNow();
		}
	}

	private boolean isReady( RadioTransmitter rprTransmitter )
	{
		return rprTransmitter.getEntityIdentifier().isDecodeCalled();
	}
	
	private void deserializeFromHla( RadioTransmitter hlaObject, AttributeHandleValueMap map )
	{
		// EntityIdentifier
		hlaDecode( hlaObject.getEntityIdentifier(), entityIdentifier, map );
		
		// HostObjectIdentifier
		hlaDecode( hlaObject.getHostObjectIdentifier(), hostObjectIdentifier, map );
		
		// RelativePosition
		hlaDecode( hlaObject.getRelativePosition(), relativePosition, map );
		
		// Antenna Pattern Data
		hlaDecode( hlaObject.getAntennaPatternDataArray(), antennaPatternData, map );
		
		// CryptographicMode
		hlaDecode( hlaObject.getCryptographicMode(), cryptographicMode, map );

		// CryptographicMode
		hlaDecode( hlaObject.getCryptoSystem(), cryptoSystem, map );

		// EncryptionKeyIdentifier
		hlaDecode( hlaObject.getEncryptionKeyIdentifier(), encryptionKeyIdentifier, map );
	
		// Frequency
		hlaDecode( hlaObject.getFrequency(), frequency, map );
	
		// FrequencyBandwidth
		hlaDecode( hlaObject.getFrequencyBandwidth(), frequencyBandwidth, map );
		
		// RadioIndex
		hlaDecode( hlaObject.getRadioIndex(), radioIndex, map );
		
		// RadioInputSource
		hlaDecode( hlaObject.getRadioInputSource(), radioInputSource, map );
	
		// RadioSystemType
		hlaDecode( hlaObject.getRadioSystemType(), radioSystemType, map );
		
		// RFModulationSystemType
		hlaDecode( hlaObject.getRfModulationSystemType(), rfModulationSystemType, map );

		// RFModulationType
		try
		{
			hlaDecode( hlaObject.getRfModulationType(), rfModulationType, map );
		}
		catch( Exception e )
		{
			// Sigh. In RPR the key to the RFmodulationTypeVariantStruct is a value from
			// MajorRFModulationTypeEnum16. This comes from the RFModulationSystemType.
			// However, not all values in the enum are valid as keys inside the variant struct,
			// so this can cause an exception. We're going to catch that here and use a fallback
			// if it happens (which is seems to a lot!).
			//
			// We might catch some false positives, but I'll take a fallback that isn't quite
			// if it has a chance of keeping things moving over the exceptions we get now.
			//
			// See Github #87 for more information
			hlaObject.getRfModulationType().setUnmodulatedType();
		}
		
	
		// SpreadSpectrum
		hlaDecode( hlaObject.getSpreadSpectrum(), spreadSpectrum, map );
		
		// StreamTag
		hlaDecode( hlaObject.getStreamTag(), streamTag, map );

		// TimeHopInUse
		hlaDecode( hlaObject.getTimeHopInUse(), timeHopInUse, map );
		
		// TransmittedPower
		hlaDecode( hlaObject.getTransmittedPower(), transmittedPower, map );
		
		// TransmitterOperationalStatus
		hlaDecode( hlaObject.getTransmitterOperationalStatus(), transmitterOperationalStatus, map );
		
		// WorldLocation
		hlaDecode( hlaObject.getWorldLocation(), worldLocation, map );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
