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
package org.openlvc.disco.application.pdu;

import org.openlvc.disco.application.pdu.DrEntityStatePdu.OutdatedTimestampBehavior;
import org.openlvc.disco.application.utils.DrmState;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.record.AngularVelocityVector;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"deadreckoning"})
public class DrEntityStatePduTest
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
	public void testDrEntityRVW()
	{
		// setup the model
		EntityStatePdu entityPdu = new EntityStatePdu();
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		
		long localTimestamp = 10;
		DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.RVW;

		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

		EulerAngles orientation = new EulerAngles( 0.0f, 0.1f, 0.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

		DrmState expectedInitialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

		entityPdu.setLocalTimestamp( localTimestamp );
		deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );

		entityPdu.setLocation( location );
		entityPdu.setLinearVelocity( velocity );
		deadReckoningParams.setEntityLinearAcceleration( acceleration );

		entityPdu.setOrientation( orientation );
		deadReckoningParams.setEntityAngularVelocity( angularVelocity );

		entityPdu.setDeadReckoningParams( deadReckoningParams );

		DrEntityStatePdu drEntityPdu = new DrEntityStatePdu( entityPdu, 2 );

		// check the initial values are what we set
		Assert.assertEquals( drEntityPdu.getDeadReckoningAlgorithm(), deadReckoningAlgorithm );
		Assert.assertEquals( drEntityPdu.getLocalTimestamp(), localTimestamp );
		Assert.assertEquals( drEntityPdu.getInitialDrmState(), expectedInitialState );

		// check the state after 1s
		DrmState drmState1s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), localTimestamp + 1000 );
		Assert.assertEquals( drmState1s.getLocation(), new WorldCoordinate(52.5, 65.5, 80.5) );
		Assert.assertEquals( drmState1s.getLinearVelocity(), new VectorRecord(13, 25, 37) );
		Assert.assertEquals( drmState1s.getLinearAcceleration(), acceleration );
		Assert.assertEquals( drmState1s.getOrientation(), new EulerAngles(0.0f, 0.4f, 0.0f) );
		Assert.assertEquals( drmState1s.getAngularVelocity(), angularVelocity );

		// check the state after 3s
		DrmState drmState3s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), localTimestamp + 3000 );
		Assert.assertEquals( drmState3s.getLocation(), new WorldCoordinate(84.5, 125.5, 168.5) );
		Assert.assertEquals( drmState3s.getLinearVelocity(), new VectorRecord(19.0f, 35.0f, 51.0f) );
		Assert.assertEquals( drmState3s.getLinearAcceleration(), acceleration );
		Assert.assertEquals( drmState3s.getOrientation(), new EulerAngles(0.0f, 1.0f, 0.0f) );
		Assert.assertEquals( drmState3s.getAngularVelocity(), angularVelocity );
	}

	@Test(dependsOnMethods={"testDrEntityRVW"})
	public void testDrEntityCaching()
	{
		// setup the model
		EntityStatePdu entityPdu = new EntityStatePdu();
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		
		long localTimestamp = 10;
		DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.RVW;

		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

		EulerAngles orientation = new EulerAngles( 0.0f, -0.3f, 0.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

		entityPdu.setLocalTimestamp( localTimestamp );
		deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );

		entityPdu.setLocation( location );
		entityPdu.setLinearVelocity( velocity );
		deadReckoningParams.setEntityLinearAcceleration( acceleration );

		entityPdu.setOrientation( orientation );
		deadReckoningParams.setEntityAngularVelocity( angularVelocity );

		entityPdu.setDeadReckoningParams( deadReckoningParams );

		DrEntityStatePdu drEntityPdu = new DrEntityStatePdu( entityPdu, 2 );

		// check the state after 1s
		long t1s = localTimestamp + 1000;
		DrmState drmState1s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), t1s );
		Assert.assertEquals( drmState1s.getLocation(), new WorldCoordinate(52.5, 65.5, 80.5) );
		Assert.assertEquals( drmState1s.getLinearVelocity(), new VectorRecord(13, 25, 37) );
		Assert.assertEquals( drmState1s.getLinearAcceleration(), acceleration );
		Assert.assertEquals( drmState1s.getOrientation(), new EulerAngles(0.0f, 0.0f, 0.0f) );
		Assert.assertEquals( drmState1s.getAngularVelocity(), angularVelocity );

		// check the state after 3s
		long t3s = localTimestamp + 3000;
		DrmState drmState3s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), t3s );
		Assert.assertEquals( drmState3s.getLocation(), new WorldCoordinate(84.5, 125.5, 168.5) );
		Assert.assertEquals( drmState3s.getLinearVelocity(), new VectorRecord(19.0f, 35.0f, 51.0f) );
		Assert.assertEquals( drmState3s.getLinearAcceleration(), acceleration );
		Assert.assertEquals( drmState3s.getOrientation(), new EulerAngles(0.0f, 0.6f, 0.0f) );
		Assert.assertEquals( drmState3s.getAngularVelocity(), angularVelocity );
		
		// check we get the same objects when fetching those times again (cache hits)
		DrmState drmState1s_2 = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), t1s );
		Assert.assertTrue( drmState1s_2 == drmState1s );
		DrmState drmState3s_2 = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), t3s );
		Assert.assertTrue( drmState3s_2 == drmState3s );

		// check the state after 5s
		long t5s = localTimestamp + 5000;
		DrmState drmState5s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), t5s );
		Assert.assertEquals( drmState5s.getLocation(), new WorldCoordinate(128.5, 205.5, 284.5) );
		Assert.assertEquals( drmState5s.getLinearVelocity(), new VectorRecord(25, 45, 65) );
		Assert.assertEquals( drmState5s.getLinearAcceleration(), acceleration );
		Assert.assertEquals( drmState5s.getOrientation(), new EulerAngles(0.0f, 1.2f, 0.0f) );
		Assert.assertEquals( drmState5s.getAngularVelocity(), angularVelocity );

		// check we get a cache miss when querying at 1s
		DrmState drmState1s_3 = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), t1s );
		Assert.assertFalse( drmState1s_3 == drmState1s );
		// should still have the same values though
		Assert.assertEquals( drmState1s_3, drmState1s );
	}

	@Test
	public void testNoAlgorithm()
	{
		// create the test entity
		EntityStatePdu entityPdu = new EntityStatePdu();
		DrEntityStatePdu drEntityPdu = new DrEntityStatePdu( entityPdu );

		// verify the selected DRM is a Static model
		Assert.assertEquals( drEntityPdu.getDeadReckoningAlgorithm(), DeadReckoningAlgorithm.Static );
	}

	@Test(dependsOnMethods={"testDrEntityRVW"})
	public void testOverrideNoAlgorithm()
	{
		// setup the model
		EntityStatePdu entityPdu = new EntityStatePdu();
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		
		long localTimestamp = 10;
		DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.RVW;

		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

		EulerAngles orientation = new EulerAngles( 0.0f, 0.1f, 0.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

		DrmState expectedInitialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

		entityPdu.setLocalTimestamp( localTimestamp );
		deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );

		entityPdu.setLocation( location );
		entityPdu.setLinearVelocity( velocity );
		deadReckoningParams.setEntityLinearAcceleration( acceleration );

		entityPdu.setOrientation( orientation );
		deadReckoningParams.setEntityAngularVelocity( angularVelocity );

		entityPdu.setDeadReckoningParams( deadReckoningParams );

		DrEntityStatePdu drEntityPdu = new DrEntityStatePdu( entityPdu, 2 );

		// check the initial values are what we set
		Assert.assertEquals( drEntityPdu.getDeadReckoningAlgorithm(), deadReckoningAlgorithm );
		Assert.assertEquals( drEntityPdu.getLocalTimestamp(), localTimestamp );
		Assert.assertEquals( drEntityPdu.getInitialDrmState(), expectedInitialState );

		// check the state after 1s
		DrmState drmState1s = drEntityPdu.getDrmStateAtLocalTime( DeadReckoningAlgorithm.Static, localTimestamp + 1000 );
		Assert.assertEquals( drmState1s, expectedInitialState );

		// check the state after 3s
		DrmState drmState3s = drEntityPdu.getDrmStateAtLocalTime( DeadReckoningAlgorithm.Static, localTimestamp + 3000 );
		Assert.assertEquals( drmState3s, expectedInitialState );
	}

	@Test(dependsOnMethods={"testDrEntityRVW"})
	public void testDrDisabled()
	{
		// setup the model
		EntityStatePdu entityPdu = new EntityStatePdu();
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		
		long localTimestamp = 10;
		DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.RVW;

		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

		EulerAngles orientation = new EulerAngles( 0.0f, 0.1f, 0.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

		DrmState expectedInitialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

		entityPdu.setLocalTimestamp( localTimestamp );
		deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );

		entityPdu.setLocation( location );
		entityPdu.setLinearVelocity( velocity );
		deadReckoningParams.setEntityLinearAcceleration( acceleration );

		entityPdu.setOrientation( orientation );
		deadReckoningParams.setEntityAngularVelocity( angularVelocity );

		entityPdu.setDeadReckoningParams( deadReckoningParams );

		DrEntityStatePdu drEntityPdu = DrEntityStatePdu.makeWithoutDr( entityPdu );

		// check the state after 3s
		DrmState drmState3s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), localTimestamp + 3000 );
		Assert.assertEquals( drmState3s, expectedInitialState );
	}

	@Test(dependsOnMethods={"testDrEntityRVW"})
	public void testFrozenEntity()
	{
		// setup the model
		EntityStatePdu entityPdu = new EntityStatePdu();
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		
		long localTimestamp = 10;
		DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.RVW;

		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

		EulerAngles orientation = new EulerAngles( 0.0f, 0.1f, 0.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

		DrmState expectedInitialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

		entityPdu.setLocalTimestamp( localTimestamp );
		deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );

		entityPdu.setLocation( location );
		entityPdu.setLinearVelocity( velocity );
		deadReckoningParams.setEntityLinearAcceleration( acceleration );

		entityPdu.setOrientation( orientation );
		deadReckoningParams.setEntityAngularVelocity( angularVelocity );

		entityPdu.setDeadReckoningParams( deadReckoningParams );

		DrEntityStatePdu drEntityPdu = new DrEntityStatePdu( entityPdu );

		// check the state after 3s
		DrmState drmState3s = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), localTimestamp + 3000 );
		Assert.assertEquals( drmState3s.getLocation(), new WorldCoordinate(84.5, 125.5, 168.5) );
		Assert.assertEquals( drmState3s.getLinearVelocity(), new VectorRecord(19.0f, 35.0f, 51.0f) );
		Assert.assertEquals( drmState3s.getLinearAcceleration(), acceleration );
		Assert.assertEquals( drmState3s.getOrientation(), new EulerAngles(0.0f, 1.0f, 0.0f) );
		Assert.assertEquals( drmState3s.getAngularVelocity(), angularVelocity );

		drEntityPdu.setFrozen( true );

		// check the state after 3s when frozen
		DrmState drmState3sFrozen = drEntityPdu.getDrmStateAtLocalTime( drEntityPdu.getDeadReckoningAlgorithm(), localTimestamp + 3000 );
		Assert.assertEquals( drmState3sFrozen, expectedInitialState );
	}

	@Test
	public void testOutdatedTimestampBehavior()
	{
		EntityStatePdu entityPdu = new EntityStatePdu();
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();

		long localTimestamp = 10;
		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		VectorRecord velocity = new VectorRecord( 10, 20, 30 );
		DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.FPW; // velocity only

		entityPdu.setLocalTimestamp( localTimestamp );
		entityPdu.setLocation( location );
		entityPdu.setLinearVelocity( velocity );

		deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );
		entityPdu.setDeadReckoningParams( deadReckoningParams );

		DrEntityStatePdu drEntityPdu = new DrEntityStatePdu( entityPdu );

		// verify the DR model is setup correctly
		Assert.assertEquals( drEntityPdu.getDrLocation(localTimestamp), location );
		Assert.assertEquals( drEntityPdu.getDrLocation(localTimestamp + 5000), new WorldCoordinate(91, 143, 197) );

		// default behavior should be to error
		long oldTimestamp = localTimestamp - 5;
		Assert.assertEquals( drEntityPdu.getOutdatedTimestampBehavior(), OutdatedTimestampBehavior.ERROR );
		Assert.assertThrows( IllegalArgumentException.class, () -> drEntityPdu.getDrLocation(oldTimestamp) );
		
		// we should be able to make it use the latest instead
		drEntityPdu.setOutdatedTimestampBehavior( OutdatedTimestampBehavior.USE_CURRENT_STATE );
		Assert.assertEquals( drEntityPdu.getOutdatedTimestampBehavior(), OutdatedTimestampBehavior.USE_CURRENT_STATE );
		Assert.assertEquals( drEntityPdu.getDrLocation(oldTimestamp), location );

		// and we should be able to make it go back to erroring
		drEntityPdu.setOutdatedTimestampBehavior( OutdatedTimestampBehavior.ERROR );
		Assert.assertEquals( drEntityPdu.getOutdatedTimestampBehavior(), OutdatedTimestampBehavior.ERROR );
		Assert.assertThrows( IllegalArgumentException.class, () -> drEntityPdu.getDrLocation(oldTimestamp) );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
