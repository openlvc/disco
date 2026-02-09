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
import java.util.NoSuchElementException;
import java.util.Random;

import org.openlvc.disco.application.utils.DrmState;
import org.openlvc.disco.pdu.record.AngularVelocityVector;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.Quaternion;
import org.openlvc.disco.utils.Vec3;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.EclipseInterface;

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

	//==========================================================================================
	//------------------------------- Test Class Setup/Tear Down -------------------------------
	//==========================================================================================
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

	//==========================================================================================
	//------------------------------------ Testing Methods -------------------------------------
	//==========================================================================================
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

	@Test
	public void testRVBCircularBasic()
	{
		testRVBCircular( new CircularTestParams(11, 3, new WorldCoordinate(5, 13, 17), new EulerAngles()) );
		// lap 1.5: pos approx <5, 7, 17>, orientation psi approx +/- PI (i.e. 2k*PI +/- PI)
	}

	@Test(dataProvider="circleParams_random100")
	public void testRVBCircularRandom( CircularTestParams testParams )
	{
		testRVBCircular( testParams );
	}

	private void testRVBCircular( CircularTestParams testParams )
	{
		// circular motion parameters
		double period = testParams.period; // seconds
		double radius = testParams.radius; // metres

		double w = Math.TAU / period; // angular velocity w.r.t. circle centre - also angular velocity of entity (in -z, for left-hand turn)
		double v = w * radius; // forwards velocity (in +x)
		double a = v * w; // perpendicular acceleration (in -y, for left-hand turn)

		// set up the initial state
		WorldCoordinate location = testParams.location;
		VectorRecord velocity = new VectorRecord( (float)v, 0, 0 );
		VectorRecord acceleration = new VectorRecord( 0, (float)-a, 0 );

		EulerAngles orientation = testParams.orientation;
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0, 0, (float)-w );

		DrmState initialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

		final double errTolerance = 0.0001; // tolerate 0.01% error

		// check the state after 1 lap - should be (approx) back at the start - tests show this has an error usually not exceeding 10^-6 (~0.0001% error)
		DrmState newState1x = DeadReckoningAlgorithm.RVB.computeStateAfter( initialState, period );
		Assert.assertEquals( newState1x.position().distance(initialState.position()) / radius, // error
		                     0,
		                     errTolerance, 
		                     failFormat(newState1x.position(), initialState.position()) );

		Assert.assertEquals( newState1x.getOrientation(),  orientation );
		Assert.assertEquals( newState1x.velocity(),        initialState.velocity() );
		Assert.assertEquals( newState1x.acceleration(),    initialState.acceleration() );
		Assert.assertEquals( newState1x.angularVelocity(), initialState.angularVelocity() );

		// check the state after 1.5 laps - should be (approx) offset by diameter and rotated halfway around the axis of the circle
		DrmState newState1_5x = DeadReckoningAlgorithm.RVB.computeStateAfter( initialState, period * 1.5 );
		Vec3 halfLapPosition = new Vec3( location ).add( new Vec3(0, -2 * radius, 0).rotate(Quaternion.fromPduEulerAngles(orientation)) );
		Assert.assertEquals( newState1_5x.position().distance( halfLapPosition ) / radius, // error
		                     0,
		                     errTolerance,
		                     failFormat(newState1_5x.position(), halfLapPosition) );

		Quaternion halfLapOrientation = Quaternion.fromPduEulerAngles( orientation )
		                                          .multiply( Quaternion.fromPduEulerAngles(new EulerAngles((float)Math.PI, 0, 0)) );
		Assert.assertEquals( newState1_5x.getOrientation(),  halfLapOrientation.toPduEulerAngles() );
		Assert.assertEquals( newState1_5x.velocity(),        initialState.velocity() );
		Assert.assertEquals( newState1_5x.acceleration(),    initialState.acceleration() );
		Assert.assertEquals( newState1_5x.angularVelocity(), initialState.angularVelocity() );

		// check the state after 5 laps - should be (approx) back at the start
		DrmState newState5x = DeadReckoningAlgorithm.RVB.computeStateAfter( initialState, period * 5 );
		Assert.assertEquals( newState5x.position().distance(initialState.position()) / radius, // error
		                     0,
		                     errTolerance,
		                     failFormat(newState5x.position(), initialState.position()) );

		// error has accumulated larger than the float helper equality bounds - manually set the tolerance
		EulerAngles newOrientation5x = newState5x.getOrientation();
		Assert.assertEquals( newOrientation5x.getPsi(), orientation.getPsi(), errTolerance * 3 );
		Assert.assertEquals( newOrientation5x.getTheta(), orientation.getTheta(), errTolerance * 1.5 );
		Assert.assertEquals( newOrientation5x.getPhi(), orientation.getPhi(), errTolerance * 3 );

		Assert.assertEquals( newState5x.velocity(),        initialState.velocity() );
		Assert.assertEquals( newState5x.acceleration(),    initialState.acceleration() );
		Assert.assertEquals( newState5x.angularVelocity(), initialState.angularVelocity() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	private static String failFormat( Object actual, Object expected )
	{
		return "values: " +
		       EclipseInterface.ASSERT_EQUAL_LEFT +
		       expected +
			   EclipseInterface.ASSERT_MIDDLE +
		       actual +
			   EclipseInterface.ASSERT_RIGHT +
			   ", test: ";
	}

	private static record RotationTestSet( EulerAngles initialOrientation,
	                                       AngularVelocityVector angularVelocity,
	                                       EulerAngles outOrientation1s,
	                                       EulerAngles outOrientation3s )
	{
	}

	private static record CircularTestParams( double period,
	                                          double radius,
	                                          WorldCoordinate location,
	                                          EulerAngles orientation )
	{
	}

	//==========================================================================================
	//------------------------------------- Data Providers -------------------------------------
	//==========================================================================================
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

	@DataProvider(name="circleParams_random100",parallel=true)
	public static Iterator<CircularTestParams> circleParams()
	{
		return new Iterator<CircularTestParams>() {
			// arbitrary axis bounds
			static final double SPATIAL_AXIS_MIN = -10 * 1000d; // 10km
			static final double SPATIAL_AXIS_MAX = 1000 * 1000d; // 1000km

			final Random rand = new Random();
			int i = 0;

			@Override
			public boolean hasNext()
			{
				return i < 100;
			}

			@Override
			public CircularTestParams next() throws NoSuchElementException
			{
				if( !this.hasNext() )
					throw new NoSuchElementException();

				i++;

				double period = rand.nextDouble( 0.1, 7200 ); // 0.1s - 2h
				double radius = rand.nextDouble( 0.1, 100000 ); // 10cm - 100km

				double x = rand.nextDouble( SPATIAL_AXIS_MIN, SPATIAL_AXIS_MAX );
				double y = rand.nextDouble( SPATIAL_AXIS_MIN, SPATIAL_AXIS_MAX );
				double z = rand.nextDouble( SPATIAL_AXIS_MIN, SPATIAL_AXIS_MAX );

				float psi = rand.nextFloat( EulerAngles.PSI_MIN, EulerAngles.PSI_MAX );
				float theta = rand.nextFloat( EulerAngles.THETA_MIN * (86.3f / 90),
				                              EulerAngles.THETA_MAX * (86.3f / 90) ); // prevent singularities
				float phi = rand.nextFloat( EulerAngles.PHI_MIN, EulerAngles.PHI_MAX );

				return new CircularTestParams( period, radius, new WorldCoordinate(x, y, z), new EulerAngles(psi, theta, phi) );
			}
		};
	}
}
