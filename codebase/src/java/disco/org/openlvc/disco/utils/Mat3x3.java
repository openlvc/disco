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

public class Mat3x3
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public Vec3 a;
	public Vec3 b;
	public Vec3 c;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Mat3x3()
	{
		this( new Vec3(), new Vec3(), new Vec3() );
	}

	// vectors appear to be the columns of the matrix
	public Mat3x3( Vec3 a, Vec3 b, Vec3 c )
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Mat3x3( Mat3x3 original )
	{
		this( new Vec3( original.a ), 
		      new Vec3( original.b ), 
		      new Vec3( original.c ) );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public Quaternion toQuaternion()
	{
		// based heavily on https://github.com/g-truc/glm/blob/master/glm/gtc/quaternion.inl
		double m00 = this.a.x, m10 = this.b.x, m20 = this.c.x, 
			   m01 = this.a.y, m11 = this.b.y, m21 = this.c.y, 
			   m02 = this.a.z, m12 = this.b.z, m22 = this.c.z;

		double fourXSquaredMinus1 = m00 - m11 - m22;
		double fourYSquaredMinus1 = m11 - m00 - m22;
		double fourZSquaredMinus1 = m22 - m00 - m11;
		double fourWSquaredMinus1 = m00 + m11 + m22;

		int biggestIndex = 0;
		double fourBiggestSquaredMinus1 = fourWSquaredMinus1;
		if( fourXSquaredMinus1 > fourBiggestSquaredMinus1 )
		{
			fourBiggestSquaredMinus1 = fourXSquaredMinus1;
			biggestIndex = 1;
		}
		if( fourYSquaredMinus1 > fourBiggestSquaredMinus1 )
		{
			fourBiggestSquaredMinus1 = fourYSquaredMinus1;
			biggestIndex = 2;
		}
		if( fourZSquaredMinus1 > fourBiggestSquaredMinus1 )
		{
			fourBiggestSquaredMinus1 = fourZSquaredMinus1;
			biggestIndex = 3;
		}

		double biggestVal = Math.sqrt( fourBiggestSquaredMinus1 + 1 ) * 0.5;
		double mult = 0.25 / biggestVal;

		Quaternion result = new Quaternion();
		switch( biggestIndex )
		{
			case 0:
				result.w = biggestVal;
				result.x = (m12 - m21) * mult;
				result.y = (m20 - m02) * mult;
				result.z = (m01 - m10) * mult;
				break;
			case 1:
				result.w = (m12 - m21) * mult;
				result.x = biggestVal;
				result.y = (m01 + m10) * mult;
				result.z = (m20 + m02) * mult;
				break;
			case 2:
				result.w = (m20 - m02) * mult;
				result.x = (m01 + m10) * mult;
				result.y = biggestVal;
				result.z = (m12 + m21) * mult;
				break;
			case 3:
				result.w = (m01 - m10) * mult;
				result.x = (m20 + m02) * mult;
				result.y = (m12 + m21) * mult;
				result.z = biggestVal;
				break;
			default: // Should never actually get here
				break;
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Computes the transpose of this matrix.
	 * 
	 * @return the transpose matrix
	 */
	public Mat3x3 transpose()
	{
		return new Mat3x3( new Vec3(this.a.x, this.b.x, this.c.x),
		                   new Vec3(this.a.y, this.b.y, this.c.y),
						   new Vec3(this.a.z, this.b.z, this.c.z) );
	}
	
	/**
	 * Scales this {@link Mat3x3} by the given value.
	 * 
	 * WARNING: modifies this instance
	 * 
	 * @param rhs the scaling factor
	 * @return this instance, after scaling
	 */
	public Mat3x3 multiply( double rhs )
	{
		this.a.multiply( rhs );
		this.b.multiply( rhs );
		this.c.multiply( rhs );
		return this;
	}

	/**
	 * Obtains the matrix product of this {@link Mat3x3} and the given {@link Vec3}.
	 * 
	 * @param rhs the vector to multiply by
	 * @return the matrix product
	 */
	public Vec3 multiply( Vec3 rhs )
	{
		Mat3x3 T = this.transpose();
		return new Vec3( T.a.dot(rhs), T.b.dot(rhs), T.c.dot(rhs) );
	}

	/**
	 * Obtains the matrix product of this {@link Mat3x3} and another.
	 * 
	 * @param rhs the matrix to multiply by
	 * @return the matrix product
	 */
	public Mat3x3 multiply( Mat3x3 rhs )
	{
		Mat3x3 T = this.transpose();
		return new Mat3x3( new Vec3(T.a.dot(rhs.a), T.b.dot(rhs.a), T.c.dot(rhs.a)),
		                   new Vec3(T.a.dot(rhs.b), T.b.dot(rhs.b), T.c.dot(rhs.b)),
		                   new Vec3(T.a.dot(rhs.c), T.b.dot(rhs.c), T.c.dot(rhs.c)) );
	}

	/**
	 * Adds the given {@link Mat3x3} to this {@link Mat3x3}.
	 * 
	 * WARNING: modifies this instance
	 * 
	 * @param rhs the matrix to add
	 * @return this instance, after modification
	 */
	public Mat3x3 add( Mat3x3 rhs )
	{
		this.a.add( rhs.a );
		this.b.add( rhs.b );
		this.c.add( rhs.c );
		return this;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Generates a new {@link Mat3x3} Identity matrix instance.
	 * 
	 * @return an 3x3 identity matrix
	 */
	public static Mat3x3 Identity()
	{
		return new Mat3x3( new Vec3(1, 0, 0),
		                   new Vec3(0, 1, 0),
		                   new Vec3(0, 0, 1) );
	}
}