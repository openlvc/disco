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
package org.openlvc.disco.utils;

import org.openlvc.disco.pdu.record.EulerAngles;

public class Quaternion
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final float _180DEGREES = (float)Math.PI;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public double w;
	public double x;
	public double y;
	public double z;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Quaternion()
	{
		this( 0, 0, 0, 0 );
	}

	public Quaternion( double w, double x, double y, double z )
	{
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public Quaternion multiply( Quaternion q )
	{
        double w = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
        double x = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
        double y = this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x;
        double z = this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w;		
		return new Quaternion( w, x, y, z );
	}	
	
	/**
	 * Convert this Quaternion to an Euler which follows the DIS PDU orientation conventions.
	 * 
	 * NOTE: WILL NOT REVERSE CONVERT FROM A QUATERNION PRODUCED WITH {@link #fromEulerRad(Vec3)}!
	 * 
	 * NOTE: DON'T USE THIS FOR OTHER THINGS UNLESS YOU UNDERSTAND WHY DIFFERENT ORIENTATION
	 *       CONVENTIONS MAKE A DIFFERENCE!
	 * 
	 * @return an {@link EulerAngles} instance suitable for use in a DIS PDU for setting entity
	 *         orientation
	 */
	public EulerAngles toPduEulerAngles()
	{
		double sqw = w * w;
		double sqx = x * x;
		double sqy = y * y;
		double sqz = z * z;
		
		double yaw = Math.atan2( 2.0 * (x * y + z * w), (sqx - sqy - sqz + sqw) );
		double roll = Math.atan2( 2.0 * (y * z + x * w), (-sqx - sqy + sqz + sqw) );
		double pitch = Math.asin( -2.0 * (x * z - y * w) / (sqx + sqy + sqz + sqw) );
		
		return new EulerAngles( (float)yaw, (float)pitch, (float)(roll-_180DEGREES));
	}
	
	/**
	 * Create a Quaternion from an Euler which follows the X=PITCH, Y=ROLL, Z=YAW orientation conventions.
	 * 
	 * NOTE: WILL NOT REVERSE CONVERT FROM AN EULER PRODUCED WITH {@link #toPduEulerAngles()}!
	 * 
	 * NOTE: DON'T USE THIS FOR OTHER THINGS UNLESS YOU UNDERSTAND WHY DIFFERENT ORIENTATION
	 *       CONVENTIONS MAKE A DIFFERENCE!
	 * 
	 * @param eulerAngleRad eulerAngle.x=pitch, eulerAngle.y=roll, eulerAngle.z=yaw
	 * @return
	 */
	public static Quaternion fromEulerRad( Vec3 eulerAngleRad )
	{
		double halfXRad = (eulerAngleRad.x / 2.0);
		double halfYRad = (eulerAngleRad.y / 2.0);
		double halfZRad = (eulerAngleRad.z / 2.0);

		double cx = Math.cos( halfXRad );
		double cy = Math.cos( halfYRad );
		double cz = Math.cos( halfZRad );
		double sx = Math.sin( halfXRad );
		double sy = Math.sin( halfYRad );
		double sz = Math.sin( halfZRad );

		return new Quaternion( cx * cy * cz + sx * sy * sz, // w
		                       sx * cy * cz - cx * sy * sz, // x
		                       cx * sy * cz + sx * cy * sz, // y
		                       cx * cy * sz - sx * sy * cz  // z
		);
	}	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
