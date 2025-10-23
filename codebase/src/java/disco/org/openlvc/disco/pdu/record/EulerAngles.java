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
import org.openlvc.disco.utils.FloatingPointUtils;

/**
 * Orientation of a simulated entity shall be specified by the Euler Angles Record. This record
 * shall specify three angles which are specified with respect to the entities coordinate system.
 * The three angles shall be represented in radians.
 */
public class EulerAngles implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static float PSI_MIN = -(float)Math.PI;
	public static float PSI_MAX = (float)Math.PI;

	public static float THETA_MIN = -(float)(Math.PI / 2);
	public static float THETA_MAX = (float)(Math.PI / 2);

	public static float PHI_MIN = -(float)Math.PI;
	public static float PHI_MAX = (float)Math.PI;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float psi;
	private float theta;
	private float phi;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EulerAngles()
	{
		this( 0f, 0f, 0f );
	}
	
	public EulerAngles( float psi, float theta, float phi )
	{
		this.psi = psi;
		this.theta = theta;
		this.phi = phi;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;

		if( other instanceof EulerAngles )
		{
			EulerAngles otherAngle = (EulerAngles)other;
			// psi and phi are continuous (yaw, roll), but theta is discontinuous (pitch)
			if( FloatingPointUtils.floatRadEqual(otherAngle.psi,this.psi) &&
				FloatingPointUtils.floatEqual(otherAngle.theta,this.theta) &&
				FloatingPointUtils.floatRadEqual(otherAngle.phi,this.phi) )
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public EulerAngles clone()
	{
		return new EulerAngles( psi, theta, phi );
	}

	@Override
	public String toString()
	{
		return "EulerAngles[psi=%f, theta=%f, phi=%f]".formatted( this.psi, this.theta, this.phi );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		psi = dis.readFloat();
		theta = dis.readFloat();
		phi = dis.readFloat();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeFloat( psi );
		dos.writeFloat( theta );
		dos.writeFloat( phi );
    }
	
	@Override
    public int getByteLength()
	{
		return DisSizes.FLOAT32_SIZE * 3;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public float getPsi()
    {
    	return psi;
    }

	public void setPsi( float psi )
    {
    	this.psi = psi;
    }

	public float getTheta()
    {
    	return theta;
    }

	public void setTheta( float theta )
    {
    	this.theta = theta;
    }

	public float getPhi()
    {
    	return phi;
    }

	public void setPhi( float phi )
    {
    	this.phi = phi;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
