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
package org.openlvc.disco.pdu;

import java.io.IOException;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.field.StopFreezeReason;
import org.openlvc.disco.pdu.record.ClockTime;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.FrozenBehavior;
import org.openlvc.disco.pdu.simman.StopFreezePdu;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"pdu","siman","StopFreeze"})
public class StopFreezePduTest extends AbstractTest
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
		// no-op
	}
	
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod()
	{
		// no-op
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
		// no-op
	}
	
	@AfterClass(alwaysRun=true)
	public void afterClass()
	{
		// no-op
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testStopFreezePduSerialize() throws IOException
	{
		StopFreezePdu original = new StopFreezePdu();
		original.setOriginatingEntity( new EntityId(1,2,3) );
		original.setReceivingEntity( new EntityId(4,5,6) );
		original.setRealWorldTime( new ClockTime(7,8) );
		original.setReason( StopFreezeReason.SecurityViolation );
		original.setFrozenBehavior( new FrozenBehavior(true, false, true) );
		original.setRequestId( 11 );
		
		byte[] serialized = original.toByteArray();
		
		
		// Is the size what we expected?
		int expectedLength = original.getPduLength();
		Assert.assertEquals( serialized.length, expectedLength );
		
		// convert it back
		StopFreezePdu deserialized = (StopFreezePdu)PduFactory.getDefaultFactory()
		                                                      .create( serialized );
		Assert.assertEquals( deserialized, original );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
