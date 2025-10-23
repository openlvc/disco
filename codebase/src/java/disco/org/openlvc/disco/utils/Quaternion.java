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

	public Quaternion( Quaternion q )
	{
		this( q.w, q.x, q.y, q.z );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( !(other instanceof Quaternion otherQuat) )
			return false;

		return FloatingPointUtils.doubleEqual( otherQuat.w, this.w ) &&
		       FloatingPointUtils.doubleEqual( otherQuat.x, this.x ) &&
		       FloatingPointUtils.doubleEqual( otherQuat.y, this.y ) &&
		       FloatingPointUtils.doubleEqual( otherQuat.z, this.z );
	}

	/**
	 * Indicates whether the rotation represented by some other object is "equal to" this one.
	 * <p/>
	 * Returns {@code true} for this quaternion {@code q} iff {@code q} or {@code -q} gives
	 * {@code true} with {@link #equals(Object)} and the obj argument.
	 * 
	 * @param other the reference object with which to compare.
	 * @return {@code true} if the rotation of this object is the same as the obj argument;
	 *         {@code false} otherwise.
	 * 
	 * @see #equals(Object)
	 */
	public boolean equalsRotation( Object other )
	{
		return this.equals( other ) || this.inverseSign().equals( other );
	}

	@Override
	public String toString()
	{
		return "Quaternion[w=%f, x=%f, y=%f, z=%f]".formatted( this.w, this.x, this.y, this.z );
	}

	/**
	 * Returns the Hamilton product of this quaternion right-multiplied by the given quaternion.
	 * 
	 * @return {@code this * rhs}
	 */
	public Quaternion multiply( Quaternion rhs )
	{
        double w = this.w * rhs.w - this.x * rhs.x - this.y * rhs.y - this.z * rhs.z;
        double x = this.w * rhs.x + this.x * rhs.w + this.y * rhs.z - this.z * rhs.y;
        double y = this.w * rhs.y - this.x * rhs.z + this.y * rhs.w + this.z * rhs.x;
        double z = this.w * rhs.z + this.x * rhs.y - this.y * rhs.x + this.z * rhs.w;		
		return new Quaternion( w, x, y, z );
	}

	/**
	 * Returns a new quaternion that is the conjugate of the current quaternion. For unit
	 * quaternions this is also the Hamilton product inverse; quaternion {@code q}, returns
	 * {@code q* = q^-1} such that {@code q^-1 * (q * v * q^-1) * q = v} (the inverse of a unit
	 * quaternion is both the left and right inverse).
	 * 
	 * @return A new unit {@link Quaternion} evaluating to {@code q*}
	 */
	public Quaternion conjugate()
	{
		return new Quaternion( w, -x, -y, -z );
	}

	/**
	 * Returns a new quaternion with opposite sign; for quaternion {@code q}, returns {@code -q}.
	 * For quaternions representing a rotation in 3d space, these two quaternions represent
	 * equivalent rotations.
	 * 
	 * @return A new {@link Quaternion} evaluating to {@code -q}
	 */
	public Quaternion inverseSign()
	{
		return new Quaternion( -this.w, -this.x, -this.y, -this.z );
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
	 * 
	 * @see #fromPduEulerAngles(EulerAngles)
	 */
	public EulerAngles toPduEulerAngles()
	{
		double pitchRatio = -2.0 * (x * z - y * w);

		double divA = w * w - y * y;
		double divB = x * x - z * z;

		double yaw = Math.atan2( 2.0 * (x * y + z * w), divA + divB );
		double pitch;
		double roll = Math.atan2( 2.0 * (y * z + x * w), divA - divB );

		// handle pitch singularities
		if( Math.abs(pitchRatio) > 0.998 ) // > ~86.4 degrees
		{
			// yaw and roll are aligned - use only yaw
			yaw = yaw - roll;
			pitch = pitchRatio > 0d ? EulerAngles.THETA_MAX
			                        : EulerAngles.THETA_MIN;
			roll = 0;

			// wrap yaw
			if( yaw >= EulerAngles.PHI_MAX ) yaw -= EulerAngles.PHI_MAX - EulerAngles.PHI_MIN;
			if( yaw <  EulerAngles.PHI_MIN ) yaw += EulerAngles.PHI_MAX - EulerAngles.PHI_MIN;
		}
		else
		{
			// regular pitch
			pitch = Math.asin( pitchRatio );
		}
		
		return new EulerAngles( (float)yaw, (float)pitch, (float)roll );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
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

	/**
	 * Create a Quaternion from an Euler which follows the DIS PDU orientation conventions.
	 * 
	 * @see #toPduEulerAngles()
	 */
	public static Quaternion fromPduEulerAngles( EulerAngles eulerAngles )
	{
		double halfPsiRad   = eulerAngles.getPsi()   / 2.0;
		double halfThetaRad = eulerAngles.getTheta() / 2.0;
		double halfPhiRad   = eulerAngles.getPhi()   / 2.0;

		double cs = Math.cos( halfPsiRad );
		double ct = Math.cos( halfThetaRad );
		double cp = Math.cos( halfPhiRad );
		double ss = Math.sin( halfPsiRad );
		double st = Math.sin( halfThetaRad );
		double sp = Math.sin( halfPhiRad );

		return new Quaternion( cs * ct * cp + ss * st * sp, // w
		                       cs * ct * sp - ss * st * cp, // x
		                       cs * st * cp + ss * ct * sp, // y
		                       ss * ct * cp - cs * st * sp  // z
		);
	}
}
