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
package org.openlvc.disco.pdu.record;

import java.io.IOException;
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * DIS 7 - 6.2.11: Beam Data Record
 */
public class BeamData implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float azimuthCenter;
	private float azimuthSweep;
	private float elevationCenter;
	private float elevationSweep;
	private float sweepSync;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public BeamData()
	{
		this.azimuthCenter = 0.0f;
		this.azimuthSweep = 0.0f;
		this.elevationCenter = 0.0f;
		this.elevationSweep = 0.0f;
		this.sweepSync = 0.0f;
	}

	public BeamData( float azimuthCenter,
	                 float azimuthSweep,
	                 float elevationCenter,
	                 float elevationSweep,
	                 float sweepSync )
	{
		this.azimuthCenter = azimuthCenter;
		this.azimuthSweep = azimuthSweep;
		this.elevationCenter = elevationCenter;
		this.elevationSweep = elevationSweep;
		this.sweepSync = sweepSync;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object object )
	{
		if( this == object )
			return true;

		if( object instanceof BeamData )
		{
			BeamData other = (BeamData)object;
			if( other.azimuthCenter == this.azimuthCenter &&
				other.azimuthSweep == this.azimuthSweep &&
				other.elevationCenter == this.elevationCenter &&
				other.elevationSweep == this.elevationSweep &&
				other.sweepSync == this.sweepSync )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( this.azimuthCenter, 
		                     this.azimuthSweep,
		                     this.elevationCenter,
		                     this.elevationSweep,
		                     this.sweepSync );
	}
	
	@Override
	public BeamData clone()
	{
		return new BeamData( azimuthCenter, azimuthSweep, elevationCenter, elevationSweep, sweepSync );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		this.azimuthCenter = dis.readFloat();
		this.azimuthSweep = dis.readFloat();
		this.elevationCenter = dis.readFloat();
		this.elevationSweep = dis.readFloat();
		this.sweepSync = dis.readFloat();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeFloat( this.azimuthCenter );
		dos.writeFloat( this.azimuthSweep );
		dos.writeFloat( this.elevationCenter );
		dos.writeFloat( this.elevationSweep );
		dos.writeFloat( this.sweepSync );

    }
	
	@Override
    public final int getByteLength()
	{
		return 20;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public float getAzimuthCenter()
	{
		return azimuthCenter;
	}

	public void setAzimuthCenter( float azimuthCenter )
	{
		this.azimuthCenter = azimuthCenter;
	}

	public float getAzimuthSweep()
	{
		return azimuthSweep;
	}

	public void setAzimuthSweep( float azimuthSweep )
	{
		this.azimuthSweep = azimuthSweep;
	}

	public float getElevationCenter()
	{
		return elevationCenter;
	}

	public void setElevationCenter( float elevationCenter )
	{
		this.elevationCenter = elevationCenter;
	}

	public float getElevationSweep()
	{
		return elevationSweep;
	}

	public void setElevationSweep( float elevationSweep )
	{
		this.elevationSweep = elevationSweep;
	}

	public float getSweepSync()
	{
		return sweepSync;
	}

	public void setSweepSync( float sweepSync )
	{
		this.sweepSync = sweepSync;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
