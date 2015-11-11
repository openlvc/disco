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
package org.openlvc.disco.utils;

/**
 * Helper class for commonly used floating point operations
 */
public class FloatingPointUtils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final double FP_EQUALITY_THRESHOLD = 1e-5;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Returns whether the two specified double value are equal. The two values
	 * will be considered equal if the absolute difference between the values
	 * is within a predefined threshold.
	 * 
	 * @param d1 The first double to compare
	 * @param d2 The second double to compare
	 * 
	 * @return true if the two values are equal, otherwise false
	 */
	public static boolean doubleEqual( double d1, double d2 )
	{
		double absDiff = Math.abs( d1 - d2 );
		return absDiff < FP_EQUALITY_THRESHOLD;
	}
	
	/**
	 * Returns whether the two specified float value are equal. The two values
	 * will be considered equal if the absolute difference between the values
	 * is within a predefined threshold.
	 * 
	 * @param f1 The first float to compare
	 * @param f2 The second float to compare
	 * 
	 * @return true if the two values are equal, otherwise false
	 */
	public static boolean floatEqual( float f1, float f2 )
	{
		float absDiff = Math.abs( f1 - f2 );
		return absDiff < FP_EQUALITY_THRESHOLD;
	}
}

