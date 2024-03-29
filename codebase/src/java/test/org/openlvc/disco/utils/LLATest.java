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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"utils","coordinates","lla"})
public class LLATest
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	///////////////////////////////////////////////////////////////////////////////////
	/// Test Class Setup/Tear Down   //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@BeforeClass(alwaysRun=true)
	public void beforeClass()
	{
	}
	
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod()
	{
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
	}
	
	@AfterClass(alwaysRun=true)
	public void afterClass()
	{
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testDistanceBetweenTwoPoints()
	{
		LLA pointA = LLA.fromDegrees( 38.898556, -77.037852, 0 );
		LLA pointB = LLA.fromDegrees( 38.897147, -77.043934, 0 );
		double dist = pointB.distanceBetweenHavershine( pointA );
		double expected = 549.1557912038083;
		double epsilon =    0.000000000001;
		Assert.assertTrue( Math.abs(dist-expected) < epsilon );
	}

	@Test
	public void testDestinationFromPoint()
	{
		// north-east hemisphere
		LLA pointA = LLA.fromDegrees( 38.898556, -77.037852, 0 );
		LLA pointB = pointA.destination( 45, 100000 );
		double dist = pointB.distanceBetweenHavershine( pointA );
		Assert.assertEquals( dist, 100000, 0.001 );
		Assert.assertEquals( pointB.getLatitudeDegrees(), 39.53166667, 0.001 );
		Assert.assertEquals( pointB.getLongitudeDegrees(), -76.21333333, 0.001 );
		
		// north-west hemisphere
		pointA = LLA.fromDegrees( 38.898556, 77.037852, 0 );
		pointB = pointA.destination( 45, 100000 );
		dist = pointB.distanceBetweenHavershine( pointA );
		Assert.assertEquals( dist, 100000, 0.001 );
		Assert.assertEquals( pointB.getLatitudeDegrees(), 39.53166667, 0.001 );
		Assert.assertEquals( pointB.getLongitudeDegrees(), 77.86222222, 0.001 );
		
		// south-east hemisphere
		pointA = LLA.fromDegrees( 31.9522, 115.8589, 0 );
		pointB = pointA.destination( 45, 100000 );
		dist = pointB.distanceBetweenHavershine( pointA );
		Assert.assertEquals( dist, 100000, 0.001 );
		Assert.assertEquals( pointB.getLatitudeDegrees(), 32.58583333, 0.001 );
		Assert.assertEquals( pointB.getLongitudeDegrees(), 116.61361111, 0.001 );
		
		// south-west hemisphere
		pointA = LLA.fromDegrees( -31.9522, 115.8589, 0 );
		pointB = pointA.destination( 45, 100000 );
		dist = pointB.distanceBetweenHavershine( pointA );
		Assert.assertEquals( dist, 100000, 0.001 );
		Assert.assertEquals( pointB.getLatitudeDegrees(), -31.31416667, 0.001 );
		Assert.assertEquals( pointB.getLongitudeDegrees(), 116.60333333, 0.001 );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
