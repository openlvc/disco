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

import org.openlvc.disco.pdu.record.WorldCoordinate;

public class Vec3
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public double x;
	public double y;
	public double z;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Vec3()
	{
		this( 0, 0, 0 );
	}

	public Vec3( Vec3 other )
	{
		this( other.x, other.y, other.z );
	}
	
	public Vec3( double x, double y, double z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3( WorldCoordinate ecef )
	{
		this.x = ecef.getX();
		this.y = ecef.getY();
		this.z = ecef.getZ();
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Obtain the length of this {@link Vec3}, as measured from the origin
	 * 
	 * @return the length of this {@link Vec3}, as measured from the origin
	 */
	public double length()
	{
		return Math.sqrt( this.sqrdLength() );
	}

	/**
	 * Obtain the *squared* length of this {@link Vec3}, as measured from the origin
	 * 
	 * This method is useful in that it avoids the costly square root operation when calculating
	 * the actual length (which may not be required if the only need to the length is to compare
	 * greater/less than other vector lengths, for example)
	 * 
	 * @return the *squared* length of this {@link Vec3}, as measured from the origin
	 */
	public double sqrdLength()
	{
		return (this.x * this.x) + 
			   (this.y * this.y) +
			   (this.z * this.z);
	}	
	
	/**
	 * Divide this {@link Vec3} by the given value
	 * 
	 * WARNING: modifies this instance
	 * 
	 * @return this instance, which has been divided
	 */
	public Vec3 divide( double rhs )
	{
		this.x /= rhs; 
        this.y /= rhs;
		this.z /= rhs;
		return this;
	}
	
	/**
	 * Obtain the cross product of this {@link Vec3} and another
	 * 
	 * @param v the other {@link Vec3}
	 * @return the cross product
	 */
	public Vec3 cross(Vec3 v)
	{
		return new Vec3( this.y * v.z - v.y * this.z,
						 this.z * v.x - v.z * this.x,
						 this.x * v.y - v.x * this.y );
	}
	
	/**
	 * Normalizes this {@link Vec3}
	 * 
	 * WARNING: modifies this instance
	 * 
	 * @return this instance, which has been normalized
	 */
	public Vec3 normalize()
	{
		double vecLength = this.length();
		return divide( vecLength );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
