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
import org.openlvc.disco.connection.rpr.objects.EmitterBeamRpr;
import org.openlvc.disco.connection.rpr.objects.EmitterSystemRpr;
import org.openlvc.disco.connection.rpr.objects.JammerBeam;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.objects.RadarBeam;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.emissions.EmitterSystem;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;

public class EmitterBeamMapper extends AbstractEmitterMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// HLA Handle Information
	private ObjectClass jammerClass;
	private ObjectClass radarClass;
	// EmitterBeam
	private AttributeClass beamAzimuthCenter;
	private AttributeClass beamAzimuthSweep;
	private AttributeClass beamElevationCenter;
	private AttributeClass beamElevationSweep;
	private AttributeClass beamFunctionCode;
	private AttributeClass beamIdentifier;
	private AttributeClass beamParameterIndex;
	private AttributeClass effectiveRadiatedPower;
	private AttributeClass emissionFrequency;
	private AttributeClass emitterSystemIdentifier;
	private AttributeClass eventIdentifier;
	private AttributeClass frequencyRange;
	private AttributeClass pulseRepetitionFrequency;
	private AttributeClass pulseWidth;
	private AttributeClass sweepSynch;
	// JammerBeam
	private AttributeClass jammingModeSequence;
	private AttributeClass highDensityJam;
	private AttributeClass jammedObjectIdentifiers;
	// RadarBeam
	private AttributeClass highDensityTrack;
	private AttributeClass trackObjectIdentifiers;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.Emission );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		// Cache all the attributes we need
		this.jammerClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.EmitterBeam.JammerBeam" );
		if( this.jammerClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.EmitterBeam.JammerBeam" );

		this.radarClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.EmitterBeam.RadarBeam" );
		if( this.radarClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.EmitterBeam.RadarBeam" );

		// Emitter Beam
		this.beamAzimuthCenter = jammerClass.getAttribute( "BeamAzimuthCenter" );
		this.beamAzimuthSweep = jammerClass.getAttribute( "BeamAzimuthSweep" );
		this.beamElevationCenter = jammerClass.getAttribute( "BeamElevationCenter" );
		this.beamElevationSweep = jammerClass.getAttribute( "BeamElevationSweep" );
		this.beamFunctionCode = jammerClass.getAttribute( "BeamFunctionCode" );
		this.beamIdentifier = jammerClass.getAttribute( "BeamIdentifier" );
		this.beamParameterIndex = jammerClass.getAttribute( "BeamParameterIndex" );
		this.effectiveRadiatedPower = jammerClass.getAttribute( "EffectiveRadiatedPower" );
		this.emissionFrequency = jammerClass.getAttribute( "EmissionFrequency" );
		this.emitterSystemIdentifier = jammerClass.getAttribute( "EmitterSystemIdentifier" );
		this.eventIdentifier = jammerClass.getAttribute( "EventIdentifier" );
		this.frequencyRange = jammerClass.getAttribute( "FrequencyRange" );
		this.pulseRepetitionFrequency = jammerClass.getAttribute( "PulseRepetitionFrequency" );
		this.pulseWidth = jammerClass.getAttribute( "PulseWidth" );
		this.sweepSynch = jammerClass.getAttribute( "SweepSynch" );
		// JammerBeam
		this.jammingModeSequence = jammerClass.getAttribute( "JammingModeSequence" );
		this.highDensityJam = jammerClass.getAttribute( "HighDensityJam" );
		this.jammedObjectIdentifiers = jammerClass.getAttribute( "JammedObjectIdentifiers" );
		// RadarBeam
		this.highDensityTrack = radarClass.getAttribute( "HighDensityTrack" );
		this.trackObjectIdentifiers = radarClass.getAttribute( "TrackObjectIdentifiers" );
		
		// Publish and Subscribe
		super.publishAndSubscribe( this.jammerClass );
		super.publishAndSubscribe( this.radarClass );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( EmissionPdu pdu )
	{
		//
		// 1. Review each EmitterSystem
		//
		for( EmitterSystem disSystem : pdu.getEmitterSystems() )
		{
			//
			// 2. Get the emitter system RTIobjectId as each beam will need this
			//
			EmitterSystemRpr rprSystem = objectStore.getLocalEmitter( disSystem.getEmitterSystemId() );
			if( rprSystem == null )
			{
				logger.warn( "(EmitterBeam) Cannot find RPR EmitterSystem [%s], skipping",
				             disSystem.getEmitterSystemId() );
				continue;
			}

			//  
			// 3. Review each Beam in each EmitterSystem
			//
			for( EmitterBeam disBeam : disSystem.getBeams() )
			{
				//
				// 4. Try to find an existing beam HLA object to update
				// FIXME - Can only look up _local_ beams; what is we want to attach to remove ones?
				//
				EmitterBeamRpr hlaObject = objectStore.getLocalBeam( disSystem.getEmitterSystemId(),
				                                                     disBeam.getBeamNumber() );
				
				// If there is no HLA object yet, register one
				if( hlaObject == null )
				{
					boolean jammer = disBeam.getBeamFunction().isJamming();
					hlaObject = jammer ? new JammerBeam() : new RadarBeam();
					hlaObject.setObjectClass( jammer ? jammerClass : radarClass );
					super.registerObjectInstance( hlaObject );
					objectStore.addLocalBeam( disSystem.getEmitterSystemId(),
					                          disBeam.getBeamNumber(),
					                          hlaObject );
				}
				
				//
				// 5. Extract the EmitterBeam values from the PDU
				//
				super.fromPdu( rprSystem, hlaObject, disBeam, pdu.getEventId() );
				
				//
				// 6. Send an update for the object
				//
				// use Jammer to create attribute map as it has more attributes
				super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );

				if( logger.isTraceEnabled() )
					logger.trace( "(EmitterBeam) Updated attributes for beam: id=%s[%d], handle=%s",
					              disSystem.getEmitterSystemId(),
					              disBeam.getBeamNumber(),
					              hlaObject.getObjectHandle() );
			}
		}	
	}

	private AttributeHandleValueMap serializeToHla( EmitterBeamRpr hlaObject )
	{
		AttributeHandleValueMap map = hlaObject.getObjectAttributes();

		// BeamAzimuthCenter
		hlaEncode( hlaObject.getBeamAzimuthCenter(), beamAzimuthCenter, map );

		// BeamAzimuthSweep
		hlaEncode( hlaObject.getBeamAzimuthSweep(), beamAzimuthSweep, map );

		// BeamElevationCenter
		hlaEncode( hlaObject.getBeamElevationCenter(), beamElevationCenter, map );

		// BeamElevationSweep
		hlaEncode( hlaObject.getBeamElevationSweep(), beamElevationSweep, map );

		// BeamFunctionCode
		hlaEncode( hlaObject.getBeamFunctionCode(), beamFunctionCode, map );

		// BeamIdentifier
		hlaEncode( hlaObject.getBeamIdentifier(), beamIdentifier, map );

		// BeamParameterIndex
		hlaEncode( hlaObject.getBeamParameterIndex(), beamParameterIndex, map );

		// EffectiveRadiatedPower
		hlaEncode( hlaObject.getEffectiveRadiatedPower(), effectiveRadiatedPower, map );

		// EmissionFrequency
		hlaEncode( hlaObject.getEmissionFrequency(), emissionFrequency, map );

		// EmitterSystemIdentifier
		hlaEncode( hlaObject.getEmitterSystemIdentifier(), emitterSystemIdentifier, map );

		// EventIdentifier
		hlaEncode( hlaObject.getEventIdentifier(), eventIdentifier, map );
		
		// FrequencyRange
		hlaEncode( hlaObject.getFrequencyRange(), frequencyRange, map );
		
		// PulseRepetitionFrequency
		hlaEncode( hlaObject.getPulseRepetitionFrequency(), pulseRepetitionFrequency, map );
		
		// PulseWidth
		hlaEncode( hlaObject.getPulseWidth(), pulseWidth, map );
		
		// SweepSync
		hlaEncode( hlaObject.getSweepSync(), sweepSynch, map );
		
		if( hlaObject instanceof JammerBeam )
		{
			JammerBeam beam = (JammerBeam)hlaObject;

			// JammingModeSequence
			hlaEncode( beam.getJammingModeSequence(), jammingModeSequence, map );

    		// HighDensityJam
			hlaEncode( beam.getHighDensityJam(), highDensityJam, map );

    		// JammedObjectIdentifiers
			hlaEncode( beam.getJammedObjectIdentifiers(), jammedObjectIdentifiers, map );
		}
		else if( hlaObject instanceof RadarBeam )
		{
			RadarBeam beam = (RadarBeam)hlaObject;

    		// HighDensityTrack
			hlaEncode( beam.getHighDensityTrack(), highDensityTrack, map );

    		// TrackObjectIdentifiers
			hlaEncode( beam.getTrackObjectIdentifiers(), trackObjectIdentifiers, map );
		}
		
		return map;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( event.theClass == this.jammerClass ||
			event.theClass == this.radarClass )
		{
			EmitterBeamRpr hlaObject = event.theClass == this.jammerClass ? new JammerBeam() :
			                                                                new RadarBeam();

			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			hlaObject.setObjectName( event.objectName );
			hlaObject.setObjectAttributes( super.createAttributes(event.theClass ) );
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
		//
		// 1. If it isn't an Emitter Beam, skip it
		//
		if( (event.hlaObject instanceof EmitterBeamRpr) == false )
			return;
		
		EmitterBeamRpr rprBeam = (EmitterBeamRpr)event.hlaObject;
		
		//
		// 2. Update the local representation of the emitter beam
		//
		deserializeFromHla( rprBeam, event.attributes );

		//
		// 3. Check to see if we have enough information to emit a PDU. If not, skip
		//
		if( rprBeam.isReady() == false )
			return;
		
		//
		// 4. To emit a beam in a PDU we need to wrap it in its parent system. Find it.
		//
		ObjectInstance temp = objectStore.getDiscoveredHlaObjectByRtiId( rprBeam.getEmitterSystemIdentifier() );
		
		//
		// 5. Generate the PDU
		//
		EmissionPdu pdu = super.toPdu( (EmitterSystemRpr)temp, rprBeam );
		opscenter.getPduReceiver().receive( pdu.toByteArray() );
	}
	
	private void deserializeFromHla( EmitterBeamRpr hlaObject, AttributeHandleValueMap map )
	{
		// BeamAzimuthCenter
		hlaDecode( hlaObject.getBeamAzimuthCenter(), beamAzimuthCenter, map );

		// BeamAzimuthSweep
		hlaDecode( hlaObject.getBeamAzimuthSweep(), beamAzimuthSweep, map );

		// BeamElevationCenter
		hlaDecode( hlaObject.getBeamElevationCenter(), beamElevationCenter, map );

		// BeamElevationSweep
		hlaDecode( hlaObject.getBeamElevationSweep(), beamElevationSweep, map );

		// BeamFunctionCode
		hlaDecode( hlaObject.getBeamFunctionCode(), beamFunctionCode, map );

		// BeamIdentifier
		hlaDecode( hlaObject.getBeamIdentifier(), beamIdentifier, map );

		// BeamParameterIndex
		hlaDecode( hlaObject.getBeamParameterIndex(), beamParameterIndex, map );

		// EffectiveRadiatedPower
		hlaDecode( hlaObject.getEffectiveRadiatedPower(), effectiveRadiatedPower, map );

		// EmissionFrequency
		hlaDecode( hlaObject.getEmissionFrequency(), emissionFrequency, map );

		// EmitterSystemIdentifier
		hlaDecode( hlaObject.getEmitterSystemIdentifier(), emitterSystemIdentifier, map );

		// EventIdentifier
		hlaDecode( hlaObject.getEventIdentifier(), eventIdentifier, map );
		
		// FrequencyRange
		hlaDecode( hlaObject.getFrequencyRange(), frequencyRange, map );
		
		// PulseRepetitionFrequency
		hlaDecode( hlaObject.getPulseRepetitionFrequency(), pulseRepetitionFrequency, map );
		
		// PulseWidth
		hlaDecode( hlaObject.getPulseWidth(), pulseWidth, map );
		
		// SweepSync
		hlaDecode( hlaObject.getSweepSync(), sweepSynch, map );
		
		if( hlaObject instanceof JammerBeam )
		{
			JammerBeam beam = (JammerBeam)hlaObject;

			// JammingModeSequence
			hlaDecode( beam.getJammingModeSequence(), jammingModeSequence, map );
			
    		// HighDensityJam
			hlaDecode( beam.getHighDensityJam(), highDensityJam, map );
			
    		// JammedObjectIdentifiers
			hlaDecode( beam.getJammedObjectIdentifiers(), jammedObjectIdentifiers, map );
		}
		else if( hlaObject instanceof RadarBeam )
		{
			 RadarBeam beam = (RadarBeam)hlaObject;

    		// HighDensityTrack
			hlaDecode( beam.getHighDensityTrack(), highDensityTrack, map );

			// TrackObjectIdentifiers
			hlaDecode( beam.getTrackObjectIdentifiers(), trackObjectIdentifiers, map );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
