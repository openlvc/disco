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
package org.openlvc.disco.application;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.application.deadreckoning.DeadReckoningModel;
import org.openlvc.disco.application.deadreckoning.DrmState;
import org.openlvc.disco.application.deadreckoning.model.FPW;
import org.openlvc.disco.application.deadreckoning.model.Static;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"application","deadreckoning"})
public class DeadReckoningModelStoreTest extends AbstractTest
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

	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testReceivePdu()
	{
		DeadReckoningModelStore drmStore = local.getPduStore().getDrmStore();

		// 1. Create the entity we want to track as a remote
		EntityStatePdu source = new EntityStatePdu();
		EntityId entityId = new EntityId( 12, 13, 14 );
		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		EulerAngles orientation = new EulerAngles( 0.1f, 0.2f, 0.3f );
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		deadReckoningParams.setDeadReckoningAlgorithm( DeadReckoningAlgorithm.FPW );
		source.setEntityID( entityId );
		source.setLocation( location );
		source.setOrientation( orientation );
		source.setDeadReckoningParams( deadReckoningParams );

		// 2. Set it to be tracked
		drmStore.registerEntity( entityId );
		Assert.assertEquals( drmStore.size(), 1 );
		Assert.assertTrue( drmStore.isRegistered( entityId ) );
		Assert.assertFalse( drmStore.isDrmAvailable( entityId ) );
		
		// 3. Send it out
		remote.send( source );

		// wait for the store to pick the change up
		super.waitFor( () -> drmStore.isDrmAvailable( entityId ), 100 );
		Assert.assertEquals( drmStore.size(), 1 );
		Assert.assertTrue( drmStore.isRegistered( entityId ) );
		Assert.assertTrue( drmStore.isDrmAvailable( entityId ) );

		// Check the entity values
		DeadReckoningModel receivedDrm = drmStore.getDrm( entityId );
		Assert.assertTrue( receivedDrm instanceof FPW );
		DrmState receivedInitialState = receivedDrm.getInitialState();
		Assert.assertEquals( receivedInitialState.getLocation(), location );
		Assert.assertEquals( receivedInitialState.getOrientation(), orientation );
		
		// 4. Update the PDU to change some key settings and make sure that
		//    these are reflected accurately
		int oldHash = receivedDrm.hashCode();
		EulerAngles orientation2 = new EulerAngles( 3.0f, 1.0f, 2.0f );
		source.setOrientation( orientation2 );
		remote.send( source );
		super.waitFor( () -> drmStore.getDrm(entityId).hashCode() != oldHash, 100 );
		
		// we got the update, make sure everything is still cool
		Assert.assertEquals( drmStore.size(), 1 );
		// copy of old value shouldn't have changed
		Assert.assertEquals( receivedInitialState.getOrientation(), orientation );
		// Check the entity values
		receivedDrm = drmStore.getDrm( entityId );
		receivedInitialState = receivedDrm.getInitialState();
		Assert.assertEquals( receivedInitialState.getLocation(), location );
		Assert.assertEquals( receivedInitialState.getOrientation(), orientation2 );
		Assert.assertEquals( receivedDrm.getAlgorithm(), DeadReckoningAlgorithm.FPW );
		Assert.assertTrue( receivedDrm instanceof FPW );
	}

	@Test //(dependsOnGroups={"entityStateStore"})
	public void testReadPdu()
	{
		DeadReckoningModelStore drmStore = local.getPduStore().getDrmStore();
		EntityStateStore entityStore = local.getPduStore().getEntityStore();

		// 1. Create the entity we want to track as a remote
		EntityStatePdu source = new EntityStatePdu();
		EntityId entityId = new EntityId( 12, 13, 14 );
		WorldCoordinate location = new WorldCoordinate( 41.0, 43.0, 47.0 );
		EulerAngles orientation = new EulerAngles( 0.1f, 0.2f, 0.3f );
		DeadReckoningParameter deadReckoningParams = new DeadReckoningParameter();
		deadReckoningParams.setDeadReckoningAlgorithm( DeadReckoningAlgorithm.FPW );
		source.setEntityID( entityId );
		source.setLocation( location );
		source.setOrientation( orientation );
		source.setDeadReckoningParams( deadReckoningParams );

		// we shouldn't have anything in the entity store or drm store
		Assert.assertEquals( drmStore.size(), 0 );
		Assert.assertEquals( entityStore.size(), 0 );

		// 2. Send it out
		remote.send( source );

		// 3. Wait for the entityStore to pick it up
		super.waitFor( () -> { return entityStore.size() > 0; }, 100 );

		// 4. Register the entity for tracking - should grab the details from the entity store
		drmStore.registerEntity( entityId );
		Assert.assertEquals( drmStore.size(), 1 );

		// Check the entity values
		DeadReckoningModel receivedDrm = drmStore.getDrm( entityId );
		Assert.assertTrue( receivedDrm instanceof FPW );
		DrmState receivedInitialState = receivedDrm.getInitialState();
		Assert.assertEquals( receivedInitialState.getLocation(), location );
		Assert.assertEquals( receivedInitialState.getOrientation(), orientation );
		
		// 5. Update the PDU to change some key settings and make sure that
		//    these are reflected accurately
		int oldHash = receivedDrm.hashCode();
		EulerAngles orientation2 = new EulerAngles( 3.0f, 1.0f, 2.0f );
		source.setOrientation( orientation2 );
		remote.send( source );
		super.waitFor( () -> drmStore.getDrm(entityId).hashCode() != oldHash, 100 );
		
		// we got the update, make sure everything is still cool
		Assert.assertEquals( drmStore.size(), 1 );
		// copy of old value shouldn't have changed
		Assert.assertEquals( receivedInitialState.getOrientation(), orientation );
		// Check the entity values
		receivedDrm = drmStore.getDrm( entityId );
		receivedInitialState = receivedDrm.getInitialState();
		Assert.assertEquals( receivedInitialState.getLocation(), location );
		Assert.assertEquals( receivedInitialState.getOrientation(), orientation2 );
		Assert.assertEquals( receivedDrm.getAlgorithm(), DeadReckoningAlgorithm.FPW );
		Assert.assertTrue( receivedDrm instanceof FPW );
	}

	@Test //(dependsOnMethods={"testReceivePdu"})
	public void testNoAlgorithm()
	{
		// create and track the test entity
		EntityStatePdu source = new EntityStatePdu();
		EntityId entityId = new EntityId( 12, 13, 14 );
		source.setEntityID( entityId );
		
		DeadReckoningModelStore drmStore = local.getPduStore().getDrmStore();
		Assert.assertEquals( drmStore.size(), 0 );
		drmStore.registerEntity( entityId );
		Assert.assertEquals( drmStore.size(), 1 );
		remote.send( source );

		// wait for the store to pick the change up
		super.waitFor( () -> drmStore.isDrmAvailable( entityId ), 100 );
		Assert.assertEquals( drmStore.size(), 1 );

		// verify the selected DRM is a Static model
		DeadReckoningModel drm = drmStore.getDrm( entityId );
		Assert.assertEquals( drm.getAlgorithm(), DeadReckoningAlgorithm.Static );
		Assert.assertTrue( drm instanceof Static );
	}

	@Test //(dependsOnMethods={"testReceivePdu"})
	public void testEntityBecomeFrozen()
	{
		// create and track the test entity
		EntityStatePdu source = new EntityStatePdu();
		EntityId entityId = new EntityId( 12, 13, 14 );
		source.setEntityID( entityId );
		
		DeadReckoningModelStore drmStore = local.getPduStore().getDrmStore();
		Assert.assertEquals( drmStore.size(), 0 );
		drmStore.registerEntity( entityId );
		Assert.assertEquals( drmStore.size(), 1 );
		remote.send( source );

		// wait for the store to pick the change up
		super.waitFor( () -> drmStore.isDrmAvailable( entityId ), 100 );
		Assert.assertEquals( drmStore.size(), 1 );

		// update the entity to be frozen
		source.setFrozen( true );
		remote.send( source );

		// verify the store picks up the change
		super.waitFor( () -> !drmStore.isDrmAvailable( entityId ), 100 );
		Assert.assertEquals( drmStore.size(), 1 );
	}

	@Test //(dependsOnMethods={"testReadPdu"})
	public void testFrozenEntity()
	{
		DeadReckoningModelStore drmStore = local.getPduStore().getDrmStore();
		EntityStateStore entityStore = local.getPduStore().getEntityStore();

		// create the test entity
		EntityStatePdu source = new EntityStatePdu();
		EntityId entityId = new EntityId( 12, 13, 14 );
		source.setEntityID( entityId );
		source.setFrozen( true );

		// wait for the entity store to be populated
		Assert.assertEquals( entityStore.size(), 0 );
		remote.send( source );
		super.waitFor( () -> { return entityStore.size() > 0; }, 100 );
		Assert.assertEquals( entityStore.size(), 1 );

		// register the entity
		drmStore.registerEntity( entityId );
		Assert.assertEquals( drmStore.size(), 1 );

		// the entity should have been seen as frozen and not be available
		Assert.assertFalse( drmStore.isDrmAvailable( entityId ) );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

