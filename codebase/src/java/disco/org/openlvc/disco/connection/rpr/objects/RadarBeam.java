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
import org.openlvc.disco.connection.rpr.types.array.RTIobjectIdArray;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.record.EventIdentifier;
import org.openlvc.disco.pdu.record.TrackJamData;

public class RadarBeam extends EmitterBeamRpr
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRboolean highDensityTrack;
	private RTIobjectIdArray trackObjectIdentifiers;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RadarBeam()
	{
		super();
		
		this.highDensityTrack = new RPRboolean();
		this.trackObjectIdentifiers = new RTIobjectIdArray();
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
		super.fromPdu( incoming );
		System.out.println( "RadarBeam::fromPdu()" );
	}

	public void fromDis( EmitterBeam disBeam, EventIdentifier event )
	{
		super.fromDis( disBeam, event );
		
		// HighDensityTrackJam
		highDensityTrack.setValue( disBeam.isHighDensity() );

		// TrackObjects
		trackObjectIdentifiers.clear();
		for( TrackJamData target : disBeam.getTargets() )
			trackObjectIdentifiers.addElement( new RTIobjectId(target.getTarget().toString()) );
		throw new RuntimeException( "FIXME: RTIobjectId Translation" );
	}

	public EmitterBeam toDis()
	{
		EmitterBeam beam = super.toDis();
		
		// HighDensityTrackJam
		beam.setHighDensity( highDensityTrack.getValue() );
		
		// TrackedObjects -- Can't do these without a reference to the ObjectStore to look up ids.
		//                   It'll have to get done in the mapper.
		//for( int i = 0; i < trackObjectIdentifiers.size(); i++ )
		//	beam.addTarget( trackObjectIdentifiers.get(i).getAsEntityId() );
		
		return beam;
	}

	@Override
	public PDU toPdu()
	{
		throw new DiscoException( "EmitterBeams (Radar) cannot be serialized directly to PDUs" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RPRboolean getHighDensityTrack()
	{
		return highDensityTrack;
	}

	public RTIobjectIdArray getTrackObjectIdentifiers()
	{
		return trackObjectIdentifiers;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
