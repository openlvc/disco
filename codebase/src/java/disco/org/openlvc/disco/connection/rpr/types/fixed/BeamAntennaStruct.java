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
package org.openlvc.disco.connection.rpr.types.fixed;

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.ReferenceSystemEnum8;
import org.openlvc.disco.pdu.record.EulerAngles;

public class BeamAntennaStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private OrientationStruct beamOrientation;
	private HLAfloat32BE beamAzimuthBeamWidth;
	private HLAfloat32BE beamElevationBeamwidth;
	private EnumHolder<ReferenceSystemEnum8> referenceSystem;
	private HLAfloat32BE ez;
	private HLAfloat32BE ex;
	private HLAfloat32BE beamPhaseAngle;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public BeamAntennaStruct()
	{
		this.beamOrientation = new OrientationStruct();
		this.beamAzimuthBeamWidth = new HLAfloat32BE( 0.0f );
		this.beamElevationBeamwidth = new HLAfloat32BE( 0.0f );
		this.referenceSystem = new EnumHolder<>( ReferenceSystemEnum8.EntityCoordinates );
		this.ez = new HLAfloat32BE( 0.0f );
		this.ex = new HLAfloat32BE( 0.0f );
		this.beamPhaseAngle = new HLAfloat32BE( 0.0f );
		
		super.add( this.beamOrientation );
		super.add( this.beamAzimuthBeamWidth );
		super.add( this.beamElevationBeamwidth );
		super.add( this.referenceSystem );
		super.add( this.ez );
		super.add( this.ex );
		super.add( this.beamPhaseAngle );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue()
	{
		// FIXME!!!! We need the DIS-side implementation for this before we can write the setter
		this.beamOrientation.setValue( new EulerAngles() );
		this.beamAzimuthBeamWidth.setValue( 0.0f );
		this.beamElevationBeamwidth.setValue( 0.0f );
		this.referenceSystem.setEnum( ReferenceSystemEnum8.EntityCoordinates );
		this.ez.setValue( 0.0f );
		this.ex.setValue( 0.0f );
		this.beamPhaseAngle.setValue( 0.0f );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
