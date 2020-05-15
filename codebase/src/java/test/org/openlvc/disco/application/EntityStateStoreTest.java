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
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"application"})
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
	public void testEntityStatePduStore()
	{
		// 1. Create the entity we want to track as a remote
		EntityStatePdu source = new EntityStatePdu();
		EntityId entityId = new EntityId( 12, 13, 14 );
		EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		WorldCoordinate location = new WorldCoordinate( 31.9505, 115.8605, 100 );
		EulerAngles orientation = new EulerAngles( 1.0f, 2.0f, 3.0f );
		source.setEntityID( entityId );
		source.setEntityType( entityType );
		source.setLocation( location );
		source.setMarking( "PhatEntity" );
		source.setOrientation( orientation );

		// 2. Send it out
		remote.send( source );

		// 3. See if the DisApplication picks it up
		EntityStateStore entityStore = local.getPduStore().getEntityStore();

		// wait for the store to pick the change up
		super.waitFor( () -> {return entityStore.size() > 0;}, 100 );
		// check to make sure the store has what we want
		Assert.assertTrue( entityStore.hasEntityState("PhatEntity") );
		Assert.assertEquals( entityStore.size(), 1 );

		// Check the entity values
		EntityStatePdu received = entityStore.getEntityState("PhatEntity");
		Assert.assertEquals( received.getEntityID(), entityId );
		Assert.assertEquals( received.getEntityType(), entityType );
		Assert.assertEquals( received.getLocation(), location );
		Assert.assertEquals( received.getOrientation(), orientation );
		
		// Check property fetch methods
		Set<String> markings = entityStore.getAllMarkings();
		Assert.assertEquals( markings.size(), 1 );
		Assert.assertTrue( markings.contains("PhatEntity") );
		
		// 4. Update the PDU to change some key settings and make sure that
		//    these are reflected accurately
		source.setMarking( "Ph@tEntity" );
		remote.send( source );
		super.waitFor( () -> entityStore.hasEntityState("Ph@tEntity") );
		
		// we got the update, make sure everything is still cool
		Assert.assertEquals( entityStore.size(), 1 );
		// Check the entity values
		received = entityStore.getEntityState("PhatEntity");
		Assert.assertNull( received );
		received = entityStore.getEntityState("Ph@tEntity");
		Assert.assertEquals( received.getEntityID(), entityId );
		Assert.assertEquals( received.getEntityType(), entityType );
		Assert.assertEquals( received.getLocation(), location );
		Assert.assertEquals( received.getOrientation(), orientation );
		
		// Check property fetch methods
		markings = entityStore.getAllMarkings();
		Assert.assertEquals( markings.size(), 1 );
		Assert.assertFalse( markings.contains("PhatEntity") );
		Assert.assertTrue( markings.contains("Ph@tEntity") );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

