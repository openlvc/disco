/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.pdu.field;

import java.util.Arrays;
import java.util.Iterator;

import org.openlvc.disco.application.utils.DrmState;
import org.openlvc.disco.pdu.record.AngularVelocityVector;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups={"deadreckoning"})
public class DeadReckoningAlgorithmTest
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
	@BeforeClass(alwaysRun = true)
	public void beforeClass()
	{
	}

	@BeforeMethod(alwaysRun = true)
	public void beforeMethod()
	{
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod()
	{
	}

	@AfterClass(alwaysRun = true)
	public void afterClass()
	{
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Testing Methods   /////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testRVW()
	{
		// set up the initial state
		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

		EulerAngles orientation = new EulerAngles( 0.0f, 0.1f, 0.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

		DrmState initialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

		// check the state after 3s
		DrmState newState = DeadReckoningAlgorithm.RVW.computeStateAfter( initialState, 3 );
		Assert.assertEquals( newState.getLocation(), new WorldCoordinate(84.5, 125.5, 168.5) );
		Assert.assertEquals( newState.getLinearVelocity(), new VectorRecord(19.0f, 35.0f, 51.0f) );
		Assert.assertEquals( newState.getLinearAcceleration(), acceleration );
		Assert.assertEquals( newState.getOrientation(), new EulerAngles(0.0f, 1.0f, 0.0f) );
		Assert.assertEquals( newState.getAngularVelocity(), angularVelocity );
	}

	@Test(dataProvider="axisRotations")
	public void testRVWRotation( RotationTestSet rotTest )
	{
		// set up the initial state
		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 0, 0, 0 );
		VectorRecord acceleration = new VectorRecord( 0, 0, 0 );

		DrmState initialState = new DrmState( location, velocity, acceleration, rotTest.initialOrientation, rotTest.angularVelocity );

		// check the state after 1s
		DrmState dtState1s = DeadReckoningAlgorithm.RVW.computeStateAfter( initialState, 1.0 );
		Assert.assertEquals( dtState1s, new DrmState(location, velocity, acceleration, rotTest.outOrientation1s, rotTest.angularVelocity) );
		
		// check the state after 3s
		DrmState dtState3s = DeadReckoningAlgorithm.RVW.computeStateAfter( initialState, 3.0 );
		Assert.assertEquals( dtState3s, new DrmState(location, velocity, acceleration, rotTest.outOrientation3s, rotTest.angularVelocity) );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	private static record RotationTestSet( EulerAngles initialOrientation,
	                                       AngularVelocityVector angularVelocity,
	                                       EulerAngles outOrientation1s,
	                                       EulerAngles outOrientation3s )
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Data Providers   //////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@DataProvider(name="axisRotations",parallel=true)
	public static Iterator<RotationTestSet> bounds()
	{
		return Arrays.asList( new RotationTestSet( new EulerAngles( 0.0f, 0.0f, 0.1f ),
		                                           new AngularVelocityVector( 0.3f, 0.0f, 0.0f ),
		                                           new EulerAngles( 0.0f, 0.0f, 0.4f ),
		                                           new EulerAngles( 0.0f, 0.0f, 1.0f ) ),

		                      new RotationTestSet( new EulerAngles( 0.0f, 0.1f, 0.0f ),
		                                           new AngularVelocityVector( 0.0f, 0.3f, 0.0f ),
		                                           new EulerAngles( 0.0f, 0.4f, 0.0f ),
		                                           new EulerAngles( 0.0f, 1.0f, 0.0f ) ),

		                      new RotationTestSet( new EulerAngles( 0.1f, 0.0f, 0.0f ),
		                                           new AngularVelocityVector( 0.0f, 0.0f, 0.3f ),
		                                           new EulerAngles( 0.4f, 0.0f, 0.0f ),
		                                           new EulerAngles( 1.0f, 0.0f, 0.0f ) ) ).iterator();
	}
}
