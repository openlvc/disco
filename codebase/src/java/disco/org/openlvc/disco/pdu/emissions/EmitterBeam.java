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
package org.openlvc.disco.pdu.emissions;

import java.io.IOException;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.BeamFunction;
import org.openlvc.disco.pdu.field.HighDensityTrackJam;
import org.openlvc.disco.pdu.record.BeamData;
import org.openlvc.disco.pdu.record.BeamStatus;
import org.openlvc.disco.pdu.record.FundamentalParameterData;
import org.openlvc.disco.pdu.record.JammingTechnique;
import org.openlvc.disco.pdu.record.TrackJamData;

public class EmitterBeam implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short beamNumber;              // Beam Number (uint8)
	private int   parameterIndex;          // Beam Parameter Index (uint16)
	private FundamentalParameterData parameterData;
	private BeamData beamData;
	private BeamFunction beamFunction; 
	private HighDensityTrackJam highDensityTrackJam; // High Density Track/Jam (uint8)  [UID 79]
	private BeamStatus beamStatus;                   // Beam Status [6.2.12] (uint8)
	private JammingTechnique jammingTechnique;
	// Tracks
	private List<TrackJamData> targets;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public String toString()
	{
		String targetString = targets.toString();
		if( highDensityTrackJam == HighDensityTrackJam.Selected )
			targetString = "<High Density>";
		
		//return "(On) <Function> <Technique> <Targets>";
		return String.format( "(%3s) Function=%s, Technique=%s, Targets=%s",
		                      beamFunction.name(),
		                      jammingTechnique.toString(),
		                      targetString );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		dis.readUI8();  // Beam Data Length
		beamNumber = dis.readUI8();
		parameterIndex  = dis.readUI16();
		parameterData.from( dis );
		beamData.from( dis );
		beamFunction = BeamFunction.fromValue( dis.readUI8() );
		short numberOfTargets = dis.readUI8();
		highDensityTrackJam = HighDensityTrackJam.fromValue( dis.readUI8() );
		beamStatus = BeamStatus.fromValue( dis.readUI8() );
		jammingTechnique.from( dis );
		
		// Read the tracked entities
		this.targets.clear();
		for( int i = 0; i < numberOfTargets; i++ )
		{
			TrackJamData target = new TrackJamData();
			target.from( dis );
			targets.add( target );
		}
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI8( (short)getByteLength() );
		dos.writeUI8( beamNumber );
		dos.writeUI16( parameterIndex );
		parameterData.to( dos );
		beamData.to( dos );
		dos.writeUI8( beamFunction.value() );
		dos.writeUI8( (short)targets.size() );
		dos.writeUI8( highDensityTrackJam.value() );
		dos.writeUI8( beamStatus.value() );
		jammingTechnique.to( dos );
		
		// Write the tracked entities
		for( TrackJamData target : targets )
			target.to( dos );
    }
	
	@Override
    public final int getByteLength()
	{
		/* Static Section
		 * ---------------------
		 * 1  Beam Data Length
		 * 1  Beam Number
		 * 2  Beam Parameter Index
		 * 20 Fundamental Parameter Data
		 * 20 Beam Data
		 * 1  Beam Function
		 * 1  Number of Targets
		 * 1  High-Density Track/Jam
		 * 1  Beam Status
		 * 4  Jamming Technique
		 * ---------------------
		 * 52 Total
		 */
		int size = 52;

		/* Dynamic Section - TrackJamData records is fixed 8-bytes */
		size += (targets.size() * 8);

		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public short getBeamNumber()
	{
		return beamNumber;
	}

	public void setBeamNumber( short beamNumber )
	{
		this.beamNumber = beamNumber;
	}

	public int getParameterIndex()
	{
		return parameterIndex;
	}

	public void setParameterIndex( int parameterIndex )
	{
		this.parameterIndex = parameterIndex;
	}

	public FundamentalParameterData getParameterData()
	{
		return parameterData;
	}

	public void setParameterData( FundamentalParameterData parameterData )
	{
		this.parameterData = parameterData;
	}

	public BeamData getBeamData()
	{
		return beamData;
	}

	public void setBeamData( BeamData beamData )
	{
		this.beamData = beamData;
	}

	public BeamFunction getBeamFunction()
	{
		return beamFunction;
	}

	public void setBeamFunction( BeamFunction beamFunction )
	{
		this.beamFunction = beamFunction;
	}

	public boolean isHighDensity()
	{
		return highDensityTrackJam == HighDensityTrackJam.Selected;
	}

	public HighDensityTrackJam getHighDensity()
	{
		return this.highDensityTrackJam;
	}
	
	public void setHighDensity( boolean selected )
	{
		this.highDensityTrackJam = selected ? HighDensityTrackJam.Selected :
		                                      HighDensityTrackJam.NotSelected;
	}

	public void setHighDensity( HighDensityTrackJam highDensityTrackJam )
	{
		this.highDensityTrackJam = highDensityTrackJam;
	}

	public boolean isBeamActive()
	{
		return beamStatus == BeamStatus.Active;
	}

	public void setBeamActive( boolean active )
	{
		this.beamStatus = active ? BeamStatus.Active : BeamStatus.Deactivated;
	}

	public BeamStatus getBeamStatus()
	{
		return beamStatus;
	}

	public void setBeamStatus( BeamStatus beamStatus )
	{
		this.beamStatus = beamStatus;
	}

	public JammingTechnique getJammingTechnique()
	{
		return jammingTechnique;
	}

	public void setJammingTechnique( JammingTechnique jammingTechnique )
	{
		this.jammingTechnique = jammingTechnique;
	}

	
	public int getNumberOfTargets()
	{
		return targets.size();
	}
	
	public List<TrackJamData> getTargets()
	{
		return targets;
	}

	public void setTargets( List<TrackJamData> targets )
	{
		this.targets = targets;
	}

	public void addTarget( TrackJamData target )
	{
		this.targets.add( target );
	}
	
	public void removeTarget( TrackJamData target )
	{
		this.targets.remove( target );
	}
	
	public void removeTarget( int index )
	{
		this.targets.remove( index );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}