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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.enumerated.RawEnumValue8;
import org.openlvc.disco.connection.rpr.types.fixed.EventIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.field.BeamFunction;
import org.openlvc.disco.pdu.record.EventIdentifier;

public class EmitterBeamRpr extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE beamAzimuthCenter;             // AngleRadianFloat32
	private HLAfloat32BE beamAzimuthSweep;              // AngleRadianFloat32
	private HLAfloat32BE beamElevationCenter;           // AngleRadianFloat32
	private HLAfloat32BE beamElevationSweep;            // AngleRadianFloat32
	private RawEnumValue8 beamFunctionCode;             // BeamFunctionCodeEnum8
	private HLAoctet beamIdentifier;
	private RPRunsignedInteger16BE beamParameterIndex;  // UnsignedInteger16
	private HLAfloat32BE effectiveRadiatedPower;        // PowerRatioDecibelMilliwattFloat32
	private HLAfloat32BE emissionFrequency;             // FrequencyHertzFloat32
	private RTIobjectId emitterSystemIdentifier;
	private HLAfloat32BE frequencyRange;                // FrequencyHertzFloat32
	private HLAfloat32BE pulseRepetitionFrequency;      // FrequencyHertzFloat32
	private HLAfloat32BE pulseWidth;                    // TimeMicrosecondFloat32
	private HLAfloat32BE sweepSync;                     // PercentFloat32
	private EventIdentifierStruct eventIdentifier;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EmitterBeamRpr()
	{
		super();
		
		this.beamAzimuthCenter = new HLAfloat32BE();
		this.beamAzimuthSweep = new HLAfloat32BE();
		this.beamElevationCenter = new HLAfloat32BE();
		this.beamElevationSweep = new HLAfloat32BE();
		this.beamFunctionCode = new RawEnumValue8();
		this.beamIdentifier = new HLAoctet();
		this.beamParameterIndex = new RPRunsignedInteger16BE();
		this.effectiveRadiatedPower = new HLAfloat32BE();
		this.emissionFrequency = new HLAfloat32BE();
		this.emitterSystemIdentifier = new RTIobjectId();
		this.frequencyRange = new HLAfloat32BE();
		this.pulseRepetitionFrequency = new HLAfloat32BE();
		this.pulseWidth = new HLAfloat32BE();
		this.sweepSync = new HLAfloat32BE();
		this.eventIdentifier = new EventIdentifierStruct();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	protected boolean checkLoaded()
	{
		return emitterSystemIdentifier.getValue().equals( "" ) == false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		throw new DiscoException( "EmitterBeams cannot be deserialized directly from PDUs" );
	}

	public void fromDis( EmitterBeam disBeam, EventIdentifier event )
	{
		// BeamAzimuthCenter
		beamAzimuthCenter.setValue( disBeam.getBeamData().getAzimuthCenter() );
		
		// BeamAzimuthSweep
		beamAzimuthSweep.setValue( disBeam.getBeamData().getAzimuthSweep() );
		
		// BeamElevationCenter
		beamElevationCenter.setValue( disBeam.getBeamData().getElevationCenter() );
		
		// BeamElevationSweep
		beamElevationCenter.setValue( disBeam.getBeamData().getElevationSweep() );

		// BeamFunctionCode
		beamFunctionCode.setUnsignedValue( disBeam.getBeamFunction().value() );

		// BeamIdentifier
		beamIdentifier.setUnsignedValue( disBeam.getBeamNumber() );
		
		// BeamParameterIndex
		beamParameterIndex.setValue( disBeam.getParameterIndex() );

		// EffectiveRadiatedPower
		effectiveRadiatedPower.setValue( disBeam.getParameterData().getRadiatedPower() );
		
		// EmissionFrequency
		emissionFrequency.setValue( disBeam.getParameterData().getFrequency() );
		
		// EmitterSystemIdentifier
		//emitterSystemIdentifier.setValue( "..." ); -- Done in mapper - must look up emitter system

		// FrequencyRange
		frequencyRange.setValue( disBeam.getParameterData().getFrequencyRange() );

		// PulseRepetitionFrequency
		pulseRepetitionFrequency.setValue( disBeam.getParameterData().getPulseRepetitionFrequency() );

		// PulseWidth
		pulseWidth.setValue( disBeam.getParameterData().getPulseWidth() );

		// SweepSync
		sweepSync.setValue( disBeam.getBeamData().getSweepSync() );

		// EventIdentifier
		eventIdentifier.setValue( event );
	}

	public EmitterBeam toDis()
	{
		EmitterBeam beam = new EmitterBeam();
		
		// BeamAzimuthCenter
		beam.getBeamData().setAzimuthCenter( beamAzimuthCenter.getValue() );
		
		// BeamAzimuthSweep
		beam.getBeamData().setAzimuthSweep( beamAzimuthSweep.getValue() );
		
		// BeamElevationCenter
		beam.getBeamData().setElevationCenter( beamElevationCenter.getValue() );
		
		// BeamElevationSweep
		beam.getBeamData().setElevationSweep( beamElevationSweep.getValue() );

		// BeamFunctionCode
		beam.setBeamFunction( BeamFunction.fromValue(beamFunctionCode.getValue()) );

		// BeamIdentifier
		beam.setBeamNumber( beamIdentifier.getUnsignedValue() );
		
		// BeamParameterIndex
		beam.setParameterIndex( beamParameterIndex.getValue() );

		// EffectiveRadiatedPower
		beam.getParameterData().setRadiatedPower( effectiveRadiatedPower.getValue() );
		
		// EmissionFrequency
		beam.getParameterData().setFrequency( emissionFrequency.getValue() );
		
		// EmitterSystemIdentifier
		// emitterSystemIdentifier.setValue( "..." ); >> Done in Mapper. Need access to ObjectStore

		// FrequencyRange
		beam.getParameterData().setFrequencyRange( frequencyRange.getValue() );

		// PulseRepetitionFrequency
		beam.getParameterData().setPulseRepetitionFrequency( pulseRepetitionFrequency.getValue() );

		// PulseWidth
		beam.getParameterData().setPulseWidth( pulseWidth.getValue() );

		// SweepSync
		beam.getBeamData().setSweepSync( sweepSync.getValue() );

		// EventIdentifier -- handles in the mapper
		
		return beam;
	}
	
	@Override
	public PDU toPdu()
	{
		throw new DiscoException( "EmitterBeams cannot be serialized directly to PDUs" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAfloat32BE getBeamAzimuthCenter()
	{
		return beamAzimuthCenter;
	}

	public HLAfloat32BE getBeamAzimuthSweep()
	{
		return beamAzimuthSweep;
	}

	public HLAfloat32BE getBeamElevationCenter()
	{
		return beamElevationCenter;
	}

	public HLAfloat32BE getBeamElevationSweep()
	{
		return beamElevationSweep;
	}

	public RawEnumValue8 getBeamFunctionCode()
	{
		return beamFunctionCode;
	}

	public HLAoctet getBeamIdentifier()
	{
		return beamIdentifier;
	}

	public RPRunsignedInteger16BE getBeamParameterIndex()
	{
		return beamParameterIndex;
	}

	public HLAfloat32BE getEffectiveRadiatedPower()
	{
		return effectiveRadiatedPower;
	}

	public HLAfloat32BE getEmissionFrequency()
	{
		return emissionFrequency;
	}

	public RTIobjectId getEmitterSystemIdentifier()
	{
		return emitterSystemIdentifier;
	}

	/**
	 * Set our emitter system identifier to <i>match</i> the given {@link RTIobjectId}.
	 * This won't store a reference to the ID, but rather make the local one use the same
	 * string. This way, we are safe to modify the local without affecting the original.
	 * 
	 * @param hostIdentifier The ID of the emitter system that this emitter beam is part of
	 */
	public void setEmitterSystemIdentifier( RTIobjectId id )
	{
		if( id == null )
			this.emitterSystemIdentifier.setValue( "" );
		else
			this.emitterSystemIdentifier.setValue( id.getValue() );
	}

	public EventIdentifierStruct getEventIdentifier()
	{
		return eventIdentifier;
	}

	public HLAfloat32BE getFrequencyRange()
	{
		return frequencyRange;
	}

	public HLAfloat32BE getPulseRepetitionFrequency()
	{
		return pulseRepetitionFrequency;
	}

	public HLAfloat32BE getPulseWidth()
	{
		return pulseWidth;
	}

	public HLAfloat32BE getSweepSync()
	{
		return sweepSync;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
