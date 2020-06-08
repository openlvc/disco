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

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * DIS 7 - 6.2.22: EE Fundamental Parameter Data Record
 */
public class FundamentalParameterData implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float frequency;
	private float frequencyRange;
	private float radiatedPower;
	private float pulseRepetitionFrequency;
	private float pulseWidth;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FundamentalParameterData()
	{
		this.frequency = 0.0f;
		this.frequencyRange = 0.0f;
		this.radiatedPower = 0.0f;
		this.pulseRepetitionFrequency = 0.0f;
		this.pulseWidth = 0.0f;
	}

	public FundamentalParameterData( float frequency,
	                                 float frequencyRange,
	                                 float radiatedPower,
	                                 float pulseRepetitionFrequency,
	                                 float pulseWidth )
	{
		this.frequency = frequency;
		this.frequencyRange = frequencyRange;
		this.radiatedPower = radiatedPower;
		this.pulseRepetitionFrequency = pulseRepetitionFrequency;
		this.pulseWidth = pulseWidth;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public boolean equals( Object object )
	{
		if( this == object )
			return true;

		if( object instanceof FundamentalParameterData )
		{
			FundamentalParameterData other = (FundamentalParameterData)object;
			if( other.frequency == this.frequency &&
				other.frequencyRange == this.frequencyRange &&
				other.radiatedPower == this.radiatedPower &&
				other.pulseRepetitionFrequency == this.pulseRepetitionFrequency &&
				other.pulseWidth == this.pulseWidth )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public FundamentalParameterData clone()
	{
		return new FundamentalParameterData( frequency,
		                                     frequencyRange,
		                                     radiatedPower,
		                                     pulseRepetitionFrequency,
		                                     pulseWidth );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		this.frequency = dis.readFloat();
		this.frequencyRange = dis.readFloat();
		this.radiatedPower = dis.readFloat();
		this.pulseRepetitionFrequency = dis.readFloat();
		this.pulseWidth = dis.readFloat();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeFloat( this.frequency );
		dos.writeFloat( this.frequencyRange );
		dos.writeFloat( this.radiatedPower );
		dos.writeFloat( this.pulseRepetitionFrequency );
		dos.writeFloat( this.pulseWidth );

    }
	
	@Override
    public final int getByteLength()
	{
		return 20;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public float getFrequency()
	{
		return frequency;
	}

	public void setFrequency( float frequency )
	{
		this.frequency = frequency;
	}

	public float getFrequencyRange()
	{
		return frequencyRange;
	}

	public void setFrequencyRange( float frequencyRange )
	{
		this.frequencyRange = frequencyRange;
	}

	public float getRadiatedPower()
	{
		return radiatedPower;
	}

	public void setRadiatedPower( float radiatedPower )
	{
		this.radiatedPower = radiatedPower;
	}

	public float getPulseRepetitionFrequency()
	{
		return pulseRepetitionFrequency;
	}

	public void setPulseRepetitionFrequency( float pulseRepetitionFrequency )
	{
		this.pulseRepetitionFrequency = pulseRepetitionFrequency;
	}

	public float getPulseWidth()
	{
		return pulseWidth;
	}

	public void setPulseWidth( float pulseWidth )
	{
		this.pulseWidth = pulseWidth;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
