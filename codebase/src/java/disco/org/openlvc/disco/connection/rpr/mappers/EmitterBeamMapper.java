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
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

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
				super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );

				if( logger.isTraceEnabled() )
					logger.trace( "(EmitterBeam) Updated attributes for beam: id=%s[%d], handle=%s",
					              disSystem.getEmitterSystemId(),
					              disBeam.getBeamNumber(),
					              hlaObject.getObjectHandle() );
			}
		}	
	}

	private AttributeHandleValueMap serializeToHla( EmitterBeamRpr object )
	{
		AttributeHandleValueMap map = object.getObjectAttributes();

		// BeamAzimuthCenter
		ByteWrapper wrapper = new ByteWrapper( object.getBeamAzimuthCenter().getEncodedLength() );
		object.getBeamAzimuthCenter().encode(wrapper);
		map.put( beamAzimuthCenter.getHandle(), wrapper.array() );

		// BeamAzimuthSweep
		wrapper = new ByteWrapper( object.getBeamAzimuthSweep().getEncodedLength() );
		object.getBeamAzimuthSweep().encode(wrapper);
		map.put( beamAzimuthSweep.getHandle(), wrapper.array() );

		// BeamElevationCenter
		wrapper = new ByteWrapper( object.getBeamElevationCenter().getEncodedLength() );
		object.getBeamElevationCenter().encode(wrapper);
		map.put( beamElevationCenter.getHandle(), wrapper.array() );

		// BeamElevationSweep
		wrapper = new ByteWrapper( object.getBeamElevationSweep().getEncodedLength() );
		object.getBeamElevationSweep().encode(wrapper);
		map.put( beamElevationSweep.getHandle(), wrapper.array() );

		// BeamFunctionCode
		wrapper = new ByteWrapper( object.getBeamFunctionCode().getEncodedLength() );
		object.getBeamFunctionCode().encode(wrapper);
		map.put( beamFunctionCode.getHandle(), wrapper.array() );

		// BeamIdentifier
		wrapper = new ByteWrapper( object.getBeamIdentifier().getEncodedLength() );
		object.getBeamIdentifier().encode(wrapper);
		map.put( beamIdentifier.getHandle(), wrapper.array() );

		// BeamParameterIndex
		wrapper = new ByteWrapper( object.getBeamParameterIndex().getEncodedLength() );
		object.getBeamParameterIndex().encode(wrapper);
		map.put( beamParameterIndex.getHandle(), wrapper.array() );

		// EffectiveRadiatedPower
		wrapper = new ByteWrapper( object.getEffectiveRadiatedPower().getEncodedLength() );
		object.getEffectiveRadiatedPower().encode(wrapper);
		map.put( effectiveRadiatedPower.getHandle(), wrapper.array() );

		// EmissionFrequency
		wrapper = new ByteWrapper( object.getEmissionFrequency().getEncodedLength() );
		object.getEmissionFrequency().encode(wrapper);
		map.put( emissionFrequency.getHandle(), wrapper.array() );

		// EmitterSystemIdentifier
		wrapper = new ByteWrapper( object.getEmitterSystemIdentifier().getEncodedLength() );
		object.getEmitterSystemIdentifier().encode(wrapper);
		map.put( emitterSystemIdentifier.getHandle(), wrapper.array() );

		// EventIdentifier
		wrapper = new ByteWrapper( object.getEventIdentifier().getEncodedLength() );
		object.getEventIdentifier().encode(wrapper);
		map.put( eventIdentifier.getHandle(), wrapper.array() );
		
		// FrequencyRange
		wrapper = new ByteWrapper( object.getFrequencyRange().getEncodedLength() );
		object.getFrequencyRange().encode(wrapper);
		map.put( frequencyRange.getHandle(), wrapper.array() );
		
		// PulseRepetitionFrequency
		wrapper = new ByteWrapper( object.getPulseRepetitionFrequency().getEncodedLength() );
		object.getPulseRepetitionFrequency().encode(wrapper);
		map.put( pulseRepetitionFrequency.getHandle(), wrapper.array() );
		
		// PulseWidth
		wrapper = new ByteWrapper( object.getPulseWidth().getEncodedLength() );
		object.getPulseWidth().encode(wrapper);
		map.put( pulseWidth.getHandle(), wrapper.array() );
		this.pulseWidth = jammerClass.getAttribute( "PulseWidth" );
		
		// SweepSync
		wrapper = new ByteWrapper( object.getSweepSync().getEncodedLength() );
		object.getSweepSync().encode(wrapper);
		map.put( sweepSynch.getHandle(), wrapper.array() );
		
		if( object instanceof JammerBeam )
		{
			JammerBeam beam = (JammerBeam)object;

			// JammingModeSequence
			wrapper = new ByteWrapper( beam.getJammingModeSequence().getEncodedLength() );
			beam.getJammingModeSequence().encode(wrapper);
			map.put( jammingModeSequence.getHandle(), wrapper.array() );

    		// HighDensityJam
    		wrapper = new ByteWrapper( beam.getHighDensityJam().getEncodedLength() );
    		beam.getHighDensityJam().encode(wrapper);
    		map.put( highDensityJam.getHandle(), wrapper.array() );

    		// JammedObjectIdentifiers
    		wrapper = new ByteWrapper( beam.getJammedObjectIdentifiers().getEncodedLength() );
    		beam.getJammedObjectIdentifiers().encode(wrapper);
    		map.put( jammedObjectIdentifiers.getHandle(), wrapper.array() );
		}
		else if( object instanceof RadarBeam )
		{
			 RadarBeam beam = (RadarBeam)object;

    		// HighDensityTrack
    		wrapper = new ByteWrapper( beam.getHighDensityTrack().getEncodedLength() );
    		beam.getHighDensityTrack().encode(wrapper);
    		map.put( highDensityTrack.getHandle(), wrapper.array() );

    		// TrackObjectIdentifiers
    		wrapper = new ByteWrapper( beam.getTrackObjectIdentifiers().getEncodedLength() );
    		beam.getTrackObjectIdentifiers().encode(wrapper);
    		map.put( trackObjectIdentifiers.getHandle(), wrapper.array() );
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
		try
		{
			deserializeFromHla( rprBeam, event.attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}

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
	
	private void deserializeFromHla( EmitterBeamRpr object, AttributeHandleValueMap map )
		throws DecoderException
	{
		// BeamAzimuthCenter
		if( map.containsKey(beamAzimuthCenter.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamAzimuthCenter.getHandle()) );
    		object.getBeamAzimuthCenter().decode( wrapper );
		}

		// BeamAzimuthSweep
		if( map.containsKey(beamAzimuthSweep.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamAzimuthSweep.getHandle()) );
    		object.getBeamAzimuthSweep().decode( wrapper );
		}

		// BeamElevationCenter
		if( map.containsKey(beamElevationCenter.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamElevationCenter.getHandle()) );
    		object.getBeamElevationCenter().decode( wrapper );
		}

		// BeamElevationSweep
		if( map.containsKey(beamElevationSweep.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamElevationSweep.getHandle()) );
    		object.getBeamElevationSweep().decode( wrapper );
		}

		// BeamFunctionCode
		if( map.containsKey(beamFunctionCode.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamFunctionCode.getHandle()) );
    		object.getBeamFunctionCode().decode( wrapper );
		}

		// BeamIdentifier
		if( map.containsKey(beamIdentifier.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamIdentifier.getHandle()) );
    		object.getBeamIdentifier().decode( wrapper );
		}

		// BeamParameterIndex
		if( map.containsKey(beamParameterIndex.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(beamParameterIndex.getHandle()) );
    		object.getBeamParameterIndex().decode( wrapper );
		}

		// EffectiveRadiatedPower
		if( map.containsKey(effectiveRadiatedPower.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(effectiveRadiatedPower.getHandle()) );
    		object.getEffectiveRadiatedPower().decode( wrapper );
		}

		// EmissionFrequency
		if( map.containsKey(emissionFrequency.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(emissionFrequency.getHandle()) );
    		object.getEmissionFrequency().decode( wrapper );
		}

		// EmitterSystemIdentifier
		if( map.containsKey(emitterSystemIdentifier.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(emitterSystemIdentifier.getHandle()) );
    		object.getEmitterSystemIdentifier().decode( wrapper );
		}

		// EventIdentifier
		if( map.containsKey(eventIdentifier.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(eventIdentifier.getHandle()) );
    		object.getEventIdentifier().decode( wrapper );
		}

		// FrequencyRange
		if( map.containsKey(frequencyRange.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(frequencyRange.getHandle()) );
    		object.getFrequencyRange().decode( wrapper );
		}

		// PulseRepetitionFrequency
		if( map.containsKey(pulseRepetitionFrequency.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(pulseRepetitionFrequency.getHandle()) );
    		object.getPulseRepetitionFrequency().decode( wrapper );
		}

		// PulseWidth
		if( map.containsKey(pulseWidth.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(pulseWidth.getHandle()) );
    		object.getPulseWidth().decode( wrapper );
		}

		// SweepSync
		if( map.containsKey(sweepSynch.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(sweepSynch.getHandle()) );
    		object.getSweepSync().decode( wrapper );
		}
		
		if( object instanceof JammerBeam )
		{
			JammerBeam beam = (JammerBeam)object;

			// JammingModeSequence
			if( map.containsKey(jammingModeSequence.getHandle()) )
			{
	    		ByteWrapper wrapper = new ByteWrapper( map.get(jammingModeSequence.getHandle()) );
	    		beam.getJammingModeSequence().decode( wrapper );
			}
			
    		// HighDensityJam
			if( map.containsKey(highDensityJam.getHandle()) )
			{
	    		ByteWrapper wrapper = new ByteWrapper( map.get(highDensityJam.getHandle()) );
	    		beam.getHighDensityJam().decode( wrapper );
			}
			
    		// JammedObjectIdentifiers
			if( map.containsKey(jammedObjectIdentifiers.getHandle()) )
			{
	    		ByteWrapper wrapper = new ByteWrapper( map.get(jammedObjectIdentifiers.getHandle()) );
	    		beam.getJammedObjectIdentifiers().decode( wrapper );
			}
		}
		else if( object instanceof RadarBeam )
		{
			 RadarBeam beam = (RadarBeam)object;

    		// HighDensityTrack
			if( map.containsKey(highDensityTrack.getHandle()) )
			{
	    		ByteWrapper wrapper = new ByteWrapper( map.get(highDensityTrack.getHandle()) );
	    		beam.getHighDensityTrack().decode( wrapper );
			}

			// TrackObjectIdentifiers
			if( map.containsKey(trackObjectIdentifiers.getHandle()) )
			{
	    		ByteWrapper wrapper = new ByteWrapper( map.get(trackObjectIdentifiers.getHandle()) );
	    		beam.getTrackObjectIdentifiers().decode( wrapper );
			}				
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
