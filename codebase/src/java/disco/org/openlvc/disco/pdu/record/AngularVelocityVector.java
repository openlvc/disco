/*
 *   Copyright 2015 Open LVC Project.
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
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * The angular velocity of simulated entities shall be represented by the Angular Velocity Vector
 * Record. This record shall specify the rate at which an entity's orientation is changing. This
 * rate shall be measured in radians per second measured about each of the entity's own coordinate
 * axes. The record shall consist of three fields. The first field shall represent velocity about
 * the x-axis, the second about the y-axis, and the third about the z-axis (see 5.3.32.1). The
 * positive direction of the angular velocity is defined by the right-hand rule.
 */
public class AngularVelocityVector implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float rateAboutXAxis;
	private float rateAboutYAxis;
	private float rateAboutZAxis;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AngularVelocityVector()
	{
		this( 0, 0, 0 );
	}
	
	public AngularVelocityVector( float rateAboutXAxis, float rateAboutYAxis, float rateAboutZAxis )
	{
		this.rateAboutXAxis = rateAboutXAxis;
		this.rateAboutYAxis = rateAboutYAxis;
		this.rateAboutZAxis = rateAboutZAxis;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object object )
	{
		boolean equal = false;
		
		if( object == this )
		{
			equal = true;
		}
		else
		{
			if( object instanceof AngularVelocityVector )
			{
				AngularVelocityVector other = (AngularVelocityVector)object;
				equal = other.rateAboutXAxis == this.rateAboutXAxis && 
					    other.rateAboutYAxis == this.rateAboutYAxis  &&
					    other.rateAboutZAxis == this.rateAboutZAxis;
			}
		}
		
		return equal;
	}

	@Override
	public String toString()
	{
		return "AngularVelocityVector[x=%f, y=%f, z=%f]".formatted( this.rateAboutXAxis, this.rateAboutYAxis, this.rateAboutZAxis );
	}
	
	@Override
	public AngularVelocityVector clone()
	{
		return new AngularVelocityVector( rateAboutXAxis, rateAboutYAxis, rateAboutZAxis );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		rateAboutXAxis = dis.readFloat();
		rateAboutYAxis = dis.readFloat();
		rateAboutZAxis = dis.readFloat();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeFloat( rateAboutXAxis );
		dos.writeFloat( rateAboutYAxis );
		dos.writeFloat( rateAboutZAxis );
    }
	
	@Override
    public int getByteLength()
	{
		return DisSizes.UI32_SIZE * 3;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
    public float getRateAboutXAxis()
    {
    	return rateAboutXAxis;
    }

    public void setRateAboutXAxis( float rateAboutXAxis )
    {
    	this.rateAboutXAxis = rateAboutXAxis;
    }

    public float getRateAboutYAxis()
    {
    	return rateAboutYAxis;
    }

    public void setRateAboutYAxis( float rateAboutYAxis )
    {
    	this.rateAboutYAxis = rateAboutYAxis;
    }

    public float getRateAboutZAxis()
    {
    	return rateAboutZAxis;
    }

    public void setRateAboutZAxis( float rateAboutZAxis )
    {
    	this.rateAboutZAxis = rateAboutZAxis;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
