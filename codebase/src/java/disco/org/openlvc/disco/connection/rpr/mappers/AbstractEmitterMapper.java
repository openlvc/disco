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

import org.openlvc.disco.connection.rpr.objects.EmitterBeamRpr;
import org.openlvc.disco.connection.rpr.objects.EmitterSystemRpr;
import org.openlvc.disco.connection.rpr.objects.JammerBeam;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.emissions.EmitterSystem;
import org.openlvc.disco.pdu.field.BeamFunction;
import org.openlvc.disco.pdu.field.EmitterSystemFunction;
import org.openlvc.disco.pdu.record.EventIdentifier;
import org.openlvc.disco.pdu.record.TrackJamData;

public abstract class AbstractEmitterMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AbstractEmitterMapper()
	{
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS >> HLA Helper Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void fromPdu( EmitterSystemRpr rprSystem, EmitterSystem disSystem, EventIdentifier disEvent )
	{
		// EntityIdentifier
		rprSystem.getEntityIdentifier().setValue( disSystem.getEmittingEntity() );
		
		// RelativePosition
		rprSystem.getRelativePosition().setValue( disSystem.getLocation() );

		// EmitterFunctionCode
		rprSystem.getEmitterFunctionCode().setUnsignedValue( disSystem.getSystemType().getFunction().value() );
		
		// EmitterType
		rprSystem.getEmitterType().setValue( disSystem.getSystemType().getName() );
		
		// EmitterIndex
		rprSystem.getEmitterType().setValue( (byte)disSystem.getEmitterNumber() );
		
		// EventIdentifier
		rprSystem.getEventIdentifier().setValue( disEvent );
		
		//
		// Values we need to look up
		// 
		// HostObjectId
		RTIobjectId emittingRtiId = objectStore.getRtiIdForDisId( disSystem.getEmittingEntity() );
		rprSystem.setHostObjectIdentifier( emittingRtiId ); // Could be ""
	}

	protected void fromPdu( EmitterSystemRpr rprSystem,
	                        EmitterBeamRpr rprBeam,
	                        EmitterBeam disBeam,
	                        EventIdentifier disEvent )
	{
		// BeamAzimuthCenter
		rprBeam.getBeamAzimuthCenter().setValue( disBeam.getBeamData().getAzimuthCenter() );
		
		// BeamAzimuthSweep
		rprBeam.getBeamAzimuthSweep().setValue( disBeam.getBeamData().getAzimuthSweep() );
		
		// BeamElevationCenter
		rprBeam.getBeamElevationCenter().setValue( disBeam.getBeamData().getElevationCenter() );
		
		// BeamElevationSweep
		rprBeam.getBeamElevationCenter().setValue( disBeam.getBeamData().getElevationSweep() );

		// BeamFunctionCode
		rprBeam.getBeamFunctionCode().setUnsignedValue( disBeam.getBeamFunction().value() );

		// BeamIdentifier
		rprBeam.getBeamIdentifier().setUnsignedValue( disBeam.getBeamNumber() );
		
		// BeamParameterIndex
		rprBeam.getBeamParameterIndex().setValue( disBeam.getParameterIndex() );

		// EffectiveRadiatedPower
		rprBeam.getEffectiveRadiatedPower().setValue( disBeam.getParameterData().getRadiatedPower() );
		
		// EmissionFrequency
		rprBeam.getEmissionFrequency().setValue( disBeam.getParameterData().getFrequency() );
		
		// EmitterSystemIdentifier
		// Take from the RPR EmitterSystem
		rprBeam.setEmitterSystemIdentifier( rprSystem.getRtiObjectId() );

		// FrequencyRange
		rprBeam.getFrequencyRange().setValue( disBeam.getParameterData().getFrequencyRange() );

		// PulseRepetitionFrequency
		rprBeam.getPulseRepetitionFrequency().setValue( disBeam.getParameterData().getPulseRepetitionFrequency() );

		// PulseWidth
		rprBeam.getPulseWidth().setValue( disBeam.getParameterData().getPulseWidth() );

		// SweepSync
		rprBeam.getSweepSync().setValue( disBeam.getBeamData().getSweepSync() );

		// EventIdentifier
		rprBeam.getEventIdentifier().setValue( disEvent );
		
		//
		// Sub-class Shared Fields
		//
		// HighDensityTrack/Jam
		rprBeam.getHighDensityTrackJam().setValue( disBeam.isHighDensity() );
		
		// Track/Jam Targets
		rprBeam.getTargets().resize( disBeam.getNumberOfTargets() );
		for( TrackJamData target : disBeam.getTargets() )
		{
			RTIobjectId rtiId = objectStore.getRtiIdForDisId( target.getTarget() );
			if( rtiId != null )
				rprBeam.getTargets().addElement( rtiId );
		}

		//
		// Jammer Specific Fields
		// 
		if( rprBeam instanceof JammerBeam )
		{
			JammerBeam jammer = (JammerBeam)rprBeam;
			
			// JammingModeSequence
			jammer.getJammingModeSequence().setValue( disBeam.getJammingTechnique().toInteger() );
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA >> DIS Helper Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	protected EmissionPdu toPdu( EmitterSystemRpr rprSystem, EmitterBeamRpr... rprBeams )
	{
		return toPdu( rprSystem, Arrays.asList(rprBeams) );
	}
	
	/**
	 * Convert the given {@link EmitterSystemRpr} into an Emission PDU that contains the given
	 * set of {@link EmitterBeamRpr} representations. This method will look up and map all the
	 * appropriate values from the object store (such as target translations for beams) before
	 * generating and returning the PDU.
	 * 
	 * @param rprSystem The system that should be in the PDU
	 * @param rprBeams  The beams that should be in the system in the PDU. Can be empty.
	 * @return          A populated Emission PDU
	 */
	protected EmissionPdu toPdu( EmitterSystemRpr rprSystem, Collection<EmitterBeamRpr> rprBeams )
	{
		// Create the PDU to populate
		EmissionPdu pdu = new EmissionPdu();
		
		//
		// Emitter System Mapping
		//
		EmitterSystem system = new EmitterSystem();
		
		// EntityIdentifier
		pdu.setEmittingEntityId( rprSystem.getEntityIdentifier().getDisValue() );
		
		// EventIdentifier
		pdu.setEventId( rprSystem.getEventIdentifier().getDisValue() );

		// RelativePosition
		system.setLocation( rprSystem.getRelativePosition().getDisVectorValue() );
		
		// EmitterFunctionCode
		system.getSystemType().setFunction( EmitterSystemFunction.fromValue(rprSystem.getEmitterFunctionCode().getValue()) );
		
		// EmitterIndex
		system.getSystemType().setNumber( rprSystem.getEmitterIndex().getUnsignedValue() );
		
		// EmitterType
		system.getSystemType().setName( rprSystem.getEmitterType().getValue() );
		
		//
		// Emitter Beam Mapping
		//
		for( EmitterBeamRpr rprBeam : rprBeams )
		{
			if( rprBeam.isReady() == false )
				continue;
			
			EmitterBeam beam = new EmitterBeam();
			beam.setBeamActive( true ); // No status in RPR v2.0, so let's just always set to true
			
			// BeamAzimuthCenter
			beam.getBeamData().setAzimuthCenter( rprBeam.getBeamAzimuthCenter().getValue() );
			
			// BeamAzimuthSweep
			beam.getBeamData().setAzimuthSweep( rprBeam.getBeamAzimuthSweep().getValue() );
			
			// BeamElevationCenter
			beam.getBeamData().setElevationCenter( rprBeam.getBeamElevationCenter().getValue() );
			
			// BeamElevationSweep
			beam.getBeamData().setElevationSweep( rprBeam.getBeamElevationSweep().getValue() );

			// BeamFunctionCode
			beam.setBeamFunction( BeamFunction.fromValue(rprBeam.getBeamFunctionCode().getValue()) );

			// BeamIdentifier
			beam.setBeamNumber( rprBeam.getBeamIdentifier().getUnsignedValue() );
			
			// BeamParameterIndex
			beam.setParameterIndex( rprBeam.getBeamParameterIndex().getValue() );

			// EffectiveRadiatedPower
			beam.getParameterData().setRadiatedPower( rprBeam.getEffectiveRadiatedPower().getValue() );
			
			// EmissionFrequency
			beam.getParameterData().setFrequency( rprBeam.getEmissionFrequency().getValue() );
			
			// EmitterSystemIdentifier
			// Done when we add the beam to the system

			// FrequencyRange
			beam.getParameterData().setFrequencyRange( rprBeam.getFrequencyRange().getValue() );

			// PulseRepetitionFrequency
			beam.getParameterData().setPulseRepetitionFrequency( rprBeam.getPulseRepetitionFrequency().getValue() );

			// PulseWidth
			beam.getParameterData().setPulseWidth( rprBeam.getPulseWidth().getValue() );

			// SweepSync
			beam.getBeamData().setSweepSync( rprBeam.getSweepSync().getValue() );

			// EventIdentifier
			// Ignored for now
			
			// HighDensityTrack/Jam
			beam.setHighDensity( rprBeam.getHighDensityTrackJam().getValue() );
			
			// Track/Jam Targets
			for( RTIobjectId id : rprBeam.getTargets() )
			{
				ObjectInstance target = objectStore.getDiscoveredHlaObjectByRtiId(id);
				if( target != null )
				{
					beam.addTarget( target.getDisId() );
				}
				else
				{
    				// This shouldn't be fatal, as it might be targeting something we don't track yet,
    				// like a specific munition or attached part that is also represented as an object.
    				// Just don't pass the target through.
    				logger.debug( "hla >> dis (Reflect) [EmitterBeam] Target unknown or not a platform, omitting: %s",
    				              id.toString() );
				}
			}
			
			//
			// JammerBeam Specific
			//
			if( rprBeam instanceof JammerBeam )
			{
				// JammingModeSequence
				JammerBeam jammer = (JammerBeam)rprBeam;
				beam.getJammingTechnique().fromInteger( (int)jammer.getJammingModeSequence().getValue() );
			}

			// Add the beam to the system
			system.addBeam( beam );
			
			// Update the last published event for the beam
			rprBeam.setLastUpdatedTimeToNow();
		}
		
		// Add the system to the pdu
		pdu.addEmitterSystem( system );
		
		// Update the last published event for the system
		rprSystem.setLastUpdatedTimeToNow();
		
		// Done! Give it back
		return pdu;
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// General Helper Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected String toString( EmitterBeamRpr rprBeam )
	{
		// Get DIS ID of emitter
		ObjectInstance emitter = objectStore.getDiscoveredHlaObjectByRtiId( rprBeam.getEmitterSystemIdentifier() );
		
		// Get the targets and convert to printable string
		StringBuilder targets = new StringBuilder();
		int skipped = 0;
		for( RTIobjectId id : rprBeam.getTargets() )
		{
			ObjectInstance target = objectStore.getDiscoveredHlaObjectByRtiId( id );
			if( target != null )
				targets.append( target.getDisId()+", " );
			else
				skipped++;
		}
		targets.append( "(+"+skipped+")" );
		
		// Generate the full printable string
		// {handle} [id:num] <Function> <Technique> <Targets>
		return String.format( "{%s} [%s:%d] Function=%s, Targets=[%s]",
		                      rprBeam.getObjectHandle().toString(),
		                      emitter.getDisId(),
		                      rprBeam.getBeamIdentifier().getValue(),
		                      BeamFunction.fromValue( rprBeam.getBeamFunctionCode().getValue() ),
		                      targets.toString() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
