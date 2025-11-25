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
package org.openlvc.disco.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.openlvc.disco.pdu.record.EulerAngles;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups={"utils"})
public class QuaternionTest
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
	/// Testing Helpers   /////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	private void assertEulerAngleInBounds( EulerAngles orientation )
	{
		Assert.assertTrue( orientation.getPsi() <= EulerAngles.PSI_MAX );
		Assert.assertTrue( EulerAngles.PSI_MIN <= orientation.getPsi() );

		Assert.assertTrue( orientation.getTheta() <= EulerAngles.THETA_MAX );
		Assert.assertTrue( EulerAngles.THETA_MIN <= orientation.getTheta() );

		Assert.assertTrue( orientation.getPhi() <= EulerAngles.PHI_MAX );
		Assert.assertTrue( EulerAngles.PHI_MIN <= orientation.getPhi() );
	} 
	
	private void testQuaternionEulerAngleConversion( EulerAngles testOrientation,
	                                                 EulerAngles expectedOrientation )
	{
		// double check our test values are in bounds
		assertEulerAngleInBounds( testOrientation );
		assertEulerAngleInBounds( expectedOrientation );

		// convert to a quaternion, and then back
		Quaternion q = Quaternion.fromPduEulerAngles( testOrientation );
		EulerAngles orientation = q.toPduEulerAngles();

		// check the output is in bounds
		assertEulerAngleInBounds( orientation );

		// check the output is what we expect
		Assert.assertEquals( orientation, expectedOrientation );
	}

	private void testQuaternionEulerAngleConversion( EulerAngles testOrientation )
	{
		testQuaternionEulerAngleConversion( testOrientation, testOrientation );
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Testing Methods   /////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	// TODO test with known quaternion output?

	@Test(dataProvider="eulerAngleBounds")
	public void testQuaternionEulerAnglesBounds( EulerAngles testOrientation )
	{
		testQuaternionEulerAngleConversion( testOrientation );
	}

	@Test(dataProvider="orientations_random100")
	public void testQuaternionEulerAnglesRandom( EulerAngles testOrientation )
	{
		testQuaternionEulerAngleConversion( testOrientation );
	}

	@Test(dataProvider="eulerAngleSingularityBounds")
	public void testQuaternionEulerAnglesSingularityBounds( EulerAngles testOrientation )
	{
		// figure out the correct yaw
		EulerAngles expectedOrientation = testOrientation.clone();
		float yaw = expectedOrientation.getPsi() - expectedOrientation.getPhi();
		if( yaw >= EulerAngles.PSI_MAX ) yaw -= EulerAngles.PSI_MAX - EulerAngles.PSI_MIN;
		if( yaw <  EulerAngles.PSI_MIN ) yaw += EulerAngles.PSI_MAX - EulerAngles.PSI_MIN;
		expectedOrientation.setPsi( yaw );
		expectedOrientation.setPhi( 0 );

		testQuaternionEulerAngleConversion( testOrientation, expectedOrientation );
	}

	@Test(dataProvider="orientations_random100")
	public void testQuaternionEulerAnglesSingularitiesRandom( EulerAngles testOrientation )
	{
		// we get a random non-singularity orientation as input, set theta so we have a singularity
		testOrientation.setTheta( testOrientation.getTheta() > 0f ? EulerAngles.THETA_MAX
		                                                          : EulerAngles.THETA_MIN );

		testQuaternionEulerAnglesSingularityBounds( testOrientation );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	///////////////////////////////////////////////////////////////////////////////////
	/// Data Providers   //////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@DataProvider(name="eulerAngleBounds",parallel=true)
	public static Iterator<EulerAngles> bounds()
	{
		ArrayList<EulerAngles> orientations = new ArrayList<>( 7 * 3 * 7 );

		// tests with: [ MIN, NEAR MIN, NEGATIVE, 0, POSITIVE, NEAR MAX, MAX ]
		// '-1.3' and '1.1' chosen as random positive/negative values
		for( float psi : new float[]{ EulerAngles.PSI_MIN, EulerAngles.PSI_MIN + 0.01f, -1.3f, 0,
		                              1.1f, EulerAngles.PSI_MAX - 0.01f, EulerAngles.PSI_MAX } )
		{
			for( float theta : new float[]{ -1.3f, 0, 1.1f } ) // test singularities separately
			{
				for( float phi : new float[]{ EulerAngles.PHI_MIN, EulerAngles.PHI_MIN + 0.01f,
				                              -1.3f, 0, 1.1f, EulerAngles.PHI_MAX - 0.01f,
				                              EulerAngles.PHI_MAX } )
				{
					orientations.add( new EulerAngles( psi, theta, phi ) );
				}
			}
		}

		return orientations.iterator();
	}

	@DataProvider(name="eulerAngleSingularityBounds",parallel=true)
	public static Iterator<EulerAngles> boundsWithSingularities()
	{
		ArrayList<EulerAngles> orientations = new ArrayList<>( 7 * 2 * 7 );

		// tests with: [ MIN, NEAR MIN, NEGATIVE, 0, POSITIVE, NEAR MAX, MAX ]
		// '-1.3' and '1.1' chosen as random positive/negative values
		for( float psi : new float[]{ EulerAngles.PSI_MIN, EulerAngles.PSI_MIN + 0.01f, -1.3f, 0,
		                              1.1f, EulerAngles.PSI_MAX - 0.01f, EulerAngles.PSI_MAX } )
		{
			// only test singularities
			for( float theta : new float[]{ EulerAngles.THETA_MIN, EulerAngles.THETA_MAX } )
			{
				for( float phi : new float[]{ EulerAngles.PHI_MIN, EulerAngles.PHI_MIN + 0.01f,
				                              -1.3f, 0, 1.1f, EulerAngles.PHI_MAX - 0.01f,
				                              EulerAngles.PHI_MAX } )
				{
					orientations.add( new EulerAngles( psi, theta, phi ) );
				}
			}
		}

		return orientations.iterator();
	}

	@DataProvider(name="orientations_random100",parallel=true)
	public static Iterator<EulerAngles> randomOrientations()
	{
		return new Iterator<EulerAngles>() {
			Random rand = new Random();
			int i = 0;

			@Override
			public boolean hasNext()
			{
				return i < 100;
			}

			@Override
			public EulerAngles next()
			{
				i++;

				float psi = rand.nextFloat( EulerAngles.PSI_MIN, EulerAngles.PSI_MAX );
				// prevent singularities by using a maximum theta magnitude of 86.3 degrees
				float theta = rand.nextFloat( EulerAngles.THETA_MIN * (86.3f / 90),
				                              EulerAngles.THETA_MAX * (86.3f / 90) );
				float phi = rand.nextFloat( EulerAngles.PHI_MIN, EulerAngles.PHI_MAX );

				return new EulerAngles( psi, theta, phi );
			}
		};
	}
}
