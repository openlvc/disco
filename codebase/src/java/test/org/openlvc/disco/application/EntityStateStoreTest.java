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
package org.openlvc.disco.application;

import java.util.Set;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.application.pdu.DrEntityStatePdu;
import org.openlvc.disco.application.utils.DrmState;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.record.AngularVelocityVector;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"application","entityStateStore"})
public class EntityStateStoreTest extends AbstractTest
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DisApplication local;
	private OpsCenter remote;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//==========================================================================================
	//------------------------------- Test Class Setup/Tear Down -------------------------------
	//==========================================================================================
	@BeforeClass(alwaysRun=true)
	public void beforeClass()
	{
	}

	@BeforeMethod(alwaysRun=true)
	public void beforeMethod()
	{
		// create the "local" DisApplication we'll be testing
		DiscoConfiguration localConfiguration = new DiscoConfiguration();
		this.local = new DisApplication( localConfiguration );

		// create the OpsCenter that will mimic a "remote" DIS application
		DiscoConfiguration remoteConfiguration = new DiscoConfiguration();
		this.remote = super.newOpsCenter( remoteConfiguration );

		// open for bizness!
		this.local.start();
		this.remote.open();
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
		this.local.stop();
		this.remote.close();
	}

	@AfterClass(alwaysRun=true)
	public void afterClass()
	{
	}

	//==========================================================================================
	//---------------------------------- PDU Testing Methods -----------------------------------
	//==========================================================================================
	@Test
	public void testEntityStatePduStore()
	{
		// 1. Create the entity we want to track as a remote
		final EntityStatePdu source = new EntityStatePdu();
		final EntityId entityId = new EntityId( 12, 13, 14 );
		final EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		final WorldCoordinate location = new WorldCoordinate( 31.9505, 115.8605, 100 );
		final String marking = "PhatEntity";
		final EulerAngles orientation = new EulerAngles( 1.0f, 2.0f, 3.0f );
		source.setEntityID( entityId );
		source.setEntityType( entityType );
		source.setLocation( location );
		source.setMarking( marking );
		source.setOrientation( orientation );

		// 2. Send it out
		remote.send( source );

		// 3. See if the DisApplication picks it up
		EntityStateStore entityStore = local.getPduStore().getEntityStore();

		// wait for the store to pick the change up
		super.waitFor( () -> entityStore.size() > 0, 100 );
		// check to make sure the store has what we want
		Assert.assertTrue( entityStore.hasEntityState(marking) );
		Assert.assertEquals( entityStore.size(), 1 );

		// Check the entity values
		DrEntityStatePdu received = entityStore.getEntityState( marking );
		Assert.assertTrue( received instanceof EntityStatePdu );
		Assert.assertEquals( received.getEntityID(), entityId );
		Assert.assertEquals( received.getEntityType(), entityType );
		Assert.assertEquals( received.getLocation(), location );
		Assert.assertEquals( received.getOrientation(), orientation );

		// Check property fetch methods
		Set<String> markings = entityStore.getAllMarkings();
		Assert.assertEquals( markings.size(), 1 );
		Assert.assertTrue( markings.contains(marking) );

		// 4. Update the PDU to change some key settings and make sure that
		//    these are reflected accurately
		final String modifiedMarking = "Ph@tEntity";
		source.setMarking( modifiedMarking );
		remote.send( source );
		super.waitFor( () -> entityStore.hasEntityState(modifiedMarking) );

		// we got the update, make sure everything is still cool
		Assert.assertEquals( entityStore.size(), 1 );
		// Check the entity values
		received = entityStore.getEntityState( marking );
		Assert.assertNull( received );
		received = entityStore.getEntityState( modifiedMarking );
		Assert.assertEquals( received.getEntityID(), entityId );
		Assert.assertEquals( received.getEntityType(), entityType );
		Assert.assertEquals( received.getLocation(), location );
		Assert.assertEquals( received.getOrientation(), orientation );

		// Check property fetch methods
		markings = entityStore.getAllMarkings();
		Assert.assertEquals( markings.size(), 1 );
		Assert.assertFalse( markings.contains(marking) );
		Assert.assertTrue( markings.contains(modifiedMarking) );
	}

	@Test(dependsOnMethods={"testEntityStatePduStore"})
	public void testDrDisabled()
	{
		// create the "local" (dead-reckoning disabled) DisApplication we'll be testing
		DiscoConfiguration drDisabledConfig = new DiscoConfiguration(); // need to also apply any changes that are made to localConfig
		drDisabledConfig.setDeadReckoningEnabled( false );
		DisApplication drDisabledApp = new DisApplication( drDisabledConfig );
		drDisabledApp.start();

		try
		{
			// setup the model
			EntityStatePdu source = new EntityStatePdu();
			DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();

			EntityId entityId = new EntityId( 12, 13, 14 );
			long localTimestamp = 10;
			DeadReckoningAlgorithm deadReckoningAlgorithm = DeadReckoningAlgorithm.RVW;

			WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
			VectorRecord velocity = new VectorRecord( 10, 20, 30 );
			VectorRecord acceleration = new VectorRecord( 3, 5, 7 );

			EulerAngles orientation = new EulerAngles( 0.0f, 0.1f, 0.0f );
			AngularVelocityVector angularVelocity = new AngularVelocityVector( 0.00f, 0.3f, 0.00f );

			DrmState expectedInitialState = new DrmState( location, velocity, acceleration, orientation, angularVelocity );

			source.setEntityID( entityId );
			source.setLocalTimestamp( localTimestamp );
			deadReckoningParams.setDeadReckoningAlgorithm( deadReckoningAlgorithm );

			source.setLocation( location );
			source.setLinearVelocity( velocity );
			deadReckoningParams.setEntityLinearAcceleration( acceleration );

			source.setOrientation( orientation );
			deadReckoningParams.setEntityAngularVelocity( angularVelocity );

			source.setDeadReckoningParams( deadReckoningParams );

			// check the stores
			EntityStateStore drDisabledEntityStore = drDisabledApp.getPduStore().getEntityStore();
			EntityStateStore drEnabledEntityStore = local.getPduStore().getEntityStore();
			Assert.assertEquals( drDisabledEntityStore.size(), 0 );
			Assert.assertEquals( drEnabledEntityStore.size(), 0 );

			// send it
			remote.send( source );

			// wait for the stores to pick the change up
			super.waitFor( () -> drDisabledEntityStore.size() > 0, 100 );
			super.waitFor( () -> drEnabledEntityStore.size() > 0, 100 );

			// get the DrEntityStatePdus
			DrEntityStatePdu drDisabledPdu = drDisabledEntityStore.getEntityState( entityId );
			DrEntityStatePdu drEnabledPdu = drEnabledEntityStore.getEntityState( entityId );
			Assert.assertNotNull( drDisabledPdu );
			Assert.assertNotNull( drEnabledPdu );

			// overwrite timestamp
			drDisabledPdu.setLocalTimestamp( localTimestamp );
			drEnabledPdu.setLocalTimestamp( localTimestamp );

			// check the state after 3s when enabled
			long t3s = localTimestamp + 3000;
			Assert.assertEquals( drEnabledPdu.getDrLocation(t3s), new WorldCoordinate(84.5, 125.5, 168.5) );
			Assert.assertEquals( drEnabledPdu.getDrLinearVelocity(t3s), new VectorRecord(19.0f, 35.0f, 51.0f) );
			Assert.assertEquals( drEnabledPdu.getDrOrientation(t3s), new EulerAngles(0.0f, 1.0f, 0.0f) );

			// check the state after 3s when disabled
			Assert.assertEquals( drDisabledPdu.getDrLocation(t3s), expectedInitialState.getLocation() );
			Assert.assertEquals( drDisabledPdu.getDrLinearVelocity(t3s), expectedInitialState.getLinearVelocity() );
			Assert.assertEquals( drDisabledPdu.getDrOrientation(t3s), expectedInitialState.getOrientation() );
		}
		finally
		{
			// clean up
			drDisabledApp.stop();
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

