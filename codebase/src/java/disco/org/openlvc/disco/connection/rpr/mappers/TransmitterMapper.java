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
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

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
	
	private AttributeHandleValueMap serializeToHla( RadioTransmitter object )
	{
		AttributeHandleValueMap map = object.getObjectAttributes();
		
		// EntityIdentifier
		ByteWrapper wrapper = new ByteWrapper( object.getEntityIdentifier().getEncodedLength() );
		object.getEntityIdentifier().encode(wrapper);
		map.put( entityIdentifier.getHandle(), wrapper.array() );
		
		// HostObjectIdentifier
		wrapper = new ByteWrapper( object.getHostObjectIdentifier().getEncodedLength() );
		object.getHostObjectIdentifier().encode(wrapper);
		map.put( hostObjectIdentifier.getHandle(), wrapper.array() );
		
		// RelativePosition
		wrapper = new ByteWrapper( object.getRelativePosition().getEncodedLength() );
		object.getRelativePosition().encode(wrapper);
		map.put( relativePosition.getHandle(), wrapper.array() );
		
		// Antenna Pattern Data
		wrapper = new ByteWrapper( object.getAntennaPatternDataArray().getEncodedLength() );
		object.getAntennaPatternDataArray().encode(wrapper);
		map.put( antennaPatternData.getHandle(), wrapper.array() );
		
		// CryptographicMode
		wrapper = new ByteWrapper( object.getCryptographicMode().getEncodedLength() );
		object.getCryptographicMode().encode( wrapper );
		map.put( cryptographicMode.getHandle(), wrapper.array() );

		// CryptographicMode
		wrapper = new ByteWrapper( object.getCryptoSystem().getEncodedLength() );
		object.getCryptoSystem().encode( wrapper );
		map.put( cryptoSystem.getHandle(), wrapper.array() );

		// EncryptionKeyIdentifier
		wrapper = new ByteWrapper( object.getEncryptionKeyIdentifier().getEncodedLength() );
		object.getEncryptionKeyIdentifier().encode( wrapper );
		map.put( encryptionKeyIdentifier.getHandle(), wrapper.array() );
		
		// Frequency
		wrapper = new ByteWrapper( object.getFrequency().getEncodedLength() );
		object.getFrequency().encode( wrapper );
		map.put( frequency.getHandle(), wrapper.array() );
		
		// FrequencyBandwidth
		wrapper = new ByteWrapper( object.getFrequencyBandwidth().getEncodedLength() );
		object.getFrequencyBandwidth().encode( wrapper );
		map.put( frequencyBandwidth.getHandle(), wrapper.array() );
		
		// RadioIndex
		wrapper = new ByteWrapper( object.getRadioIndex().getEncodedLength() );
		object.getRadioIndex().encode( wrapper );
		map.put( radioIndex.getHandle(), wrapper.array() );
		
		// RadioInputSource
		wrapper = new ByteWrapper( object.getRadioInputSource().getEncodedLength() );
		object.getRadioInputSource().encode( wrapper );
		map.put( radioInputSource.getHandle(), wrapper.array() );
		
		// RadioSystemType
		wrapper = new ByteWrapper( object.getRadioSystemType().getEncodedLength() );
		object.getRadioSystemType().encode( wrapper );
		map.put( radioSystemType.getHandle(), wrapper.array() );
		
		// RFModulationSystemType
		wrapper = new ByteWrapper( object.getRfModulationSystemType().getEncodedLength() );
		object.getRfModulationSystemType().encode( wrapper );
		map.put( rfModulationSystemType.getHandle(), wrapper.array() );

		// RFModulationType
		wrapper = new ByteWrapper( object.getRfModulationType().getEncodedLength() );
		object.getRfModulationType().encode( wrapper );
		map.put( rfModulationType.getHandle(), wrapper.array() );
		
		// SpreadSpectrum
		wrapper = new ByteWrapper( object.getSpreadSpectrum().getEncodedLength() );
		object.getSpreadSpectrum().encode( wrapper );
		map.put( spreadSpectrum.getHandle(), wrapper.array() );
		
		// StreamTag
		wrapper = new ByteWrapper( object.getStreamTag().getEncodedLength() );
		object.getStreamTag().encode( wrapper );
		map.put( streamTag.getHandle(), wrapper.array() );

		// TimeHopInUse
		wrapper = new ByteWrapper( object.getTimeHopInUse().getEncodedLength() );
		object.getTimeHopInUse().encode( wrapper );
		map.put( timeHopInUse.getHandle(), wrapper.array() );
		
		// TransmittedPower
		wrapper = new ByteWrapper( object.getTransmittedPower().getEncodedLength() );
		object.getTransmittedPower().encode( wrapper );
		map.put( transmittedPower.getHandle(), wrapper.array() );
		
		// TransmitterOperationalStatus
		wrapper = new ByteWrapper( object.getTransmitterOperationalStatus().getEncodedLength() );
		object.getTransmitterOperationalStatus().encode( wrapper );
		map.put( transmitterOperationalStatus.getHandle(), wrapper.array() );
		
		// WorldLocation
		wrapper = new ByteWrapper( object.getWorldLocation().getEncodedLength() );
		object.getWorldLocation().encode( wrapper );
		map.put( worldLocation.getHandle(), wrapper.array() );
		
		return map;
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

		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( rprTransmitter, event.attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
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
	
	private void deserializeFromHla( RadioTransmitter object, AttributeHandleValueMap map )
		throws DecoderException
	{
		// EntityIdentifier
		if( map.containsKey(entityIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(entityIdentifier.getHandle()) );
			object.getEntityIdentifier().decode( wrapper );
		}
		
		// HostObjectIdentifier
		if( map.containsKey(hostObjectIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hostObjectIdentifier.getHandle()) );
			object.getHostObjectIdentifier().decode( wrapper );
		}
		
		// RelativePosition
		if( map.containsKey(relativePosition.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(relativePosition.getHandle()) );
			object.getRelativePosition().decode( wrapper );
		}
		
		// Antenna Pattern Data
		if( map.containsKey(antennaPatternData.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(antennaPatternData.getHandle()) );
			object.getAntennaPatternDataArray().decode( wrapper );
		}
		
		// CryptographicMode
		if( map.containsKey(cryptographicMode.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(cryptographicMode.getHandle()) );
			object.getCryptographicMode().decode( wrapper );
		}

		// CryptographicMode
		if( map.containsKey(cryptoSystem.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(cryptoSystem.getHandle()) );
			object.getCryptoSystem().decode( wrapper );
		}

		// EncryptionKeyIdentifier
		if( map.containsKey(encryptionKeyIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(encryptionKeyIdentifier.getHandle()) );
			object.getEncryptionKeyIdentifier().decode( wrapper );
		}
	
		// Frequency
		if( map.containsKey(frequency.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(frequency.getHandle()) );
			object.getFrequency().decode( wrapper );
		}
	
		// FrequencyBandwidth
		if( map.containsKey(frequencyBandwidth.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(frequencyBandwidth.getHandle()) );
			object.getFrequencyBandwidth().decode( wrapper );
		}
		
		// RadioIndex
		if( map.containsKey(radioIndex.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(radioIndex.getHandle()) );
			object.getRadioIndex().decode( wrapper );
		}
		
		// RadioInputSource
		if( map.containsKey(radioInputSource.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(radioInputSource.getHandle()) );
			object.getRadioInputSource().decode( wrapper );
		}
	
		// RadioSystemType
		if( map.containsKey(radioSystemType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(radioSystemType.getHandle()) );
			object.getRadioSystemType().decode( wrapper );
		}
		
		// RFModulationSystemType
		if( map.containsKey(rfModulationSystemType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(rfModulationSystemType.getHandle()) );
			object.getRfModulationSystemType().decode( wrapper );
		}

		// RFModulationType
		if( map.containsKey(rfModulationType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(rfModulationType.getHandle()) );
			object.getRfModulationType().decode( wrapper );
		}
	
		// SpreadSpectrum
		if( map.containsKey(spreadSpectrum.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(spreadSpectrum.getHandle()) );
			object.getSpreadSpectrum().decode( wrapper );
		}
		
		// StreamTag
		if( map.containsKey(streamTag.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(streamTag.getHandle()) );
			object.getStreamTag().decode( wrapper );
		}

		// TimeHopInUse
		if( map.containsKey(timeHopInUse.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(timeHopInUse.getHandle()) );
			object.getTimeHopInUse().decode( wrapper );
		}
		
		// TransmittedPower
		if( map.containsKey(transmittedPower.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(transmittedPower.getHandle()) );
			object.getTransmittedPower().decode( wrapper );
		}
		
		// TransmitterOperationalStatus
		if( map.containsKey(transmitterOperationalStatus.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(transmitterOperationalStatus.getHandle()) );
			object.getTransmitterOperationalStatus().decode( wrapper );
		}
		
		// WorldLocation
		if( map.containsKey(worldLocation.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(worldLocation.getHandle()) );
			object.getWorldLocation().decode( wrapper );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
