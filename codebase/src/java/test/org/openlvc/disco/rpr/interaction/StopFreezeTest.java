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
package org.openlvc.disco.rpr.interaction;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.common.TestPduListener;
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

@Test(groups={"hla","rpr","interaction"})
public class StopFreezeTest extends AbstractTest
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
		// NOTE: Portico Specific Code
		// if we are using Portico, we can control the log level through system properties
		System.setProperty( "portico.loglevel", CommonSetup.CONSOLE_LOG_LEVEL );
		System.setProperty( "portico.connection", "jvm" );
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
	/// TEST: testStopFreezeMapper   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testStopFreezeMapper()
	{
		//
		// Create an OpsCenter and configure it
		//
		OpsCenter opscenterOne = new OpsCenter();
		OpsCenter opscenterTwo = new OpsCenter();

		// Create a listener for us to use
		TestPduListener pduListener = new TestPduListener();
		opscenterTwo.setPduListener( pduListener );
		
		opscenterOne.getConfiguration().getRprConfiguration().setFederateName( "opsOne" );
		opscenterTwo.getConfiguration().getRprConfiguration().setFederateName( "opsTwo" );
		
		// Set the connection to use HLA
		opscenterOne.getConfiguration().setConnection( "rpr" );
		opscenterTwo.getConfiguration().setConnection( "rpr" );
		
		//
		// Open the OpsCenter and send some PDUs
		//
		// Open the OpsCenter
		opscenterOne.open();
		opscenterTwo.open();
		
		StopFreezePdu original = new StopFreezePdu();
		original.setOriginatingEntity( new EntityId(1,2,3) );
		original.setReceivingEntity( new EntityId(4,5,6) );
		original.setRealWorldTime( new ClockTime(7,8) );
		original.setReason( StopFreezeReason.SecurityViolation );
		original.setFrozenBehavior( new FrozenBehavior(true, false, true) );
		original.setRequestId( 11 );
		
		opscenterOne.send( original );
		
		// Receive the custom PDU and verify it
		StopFreezePdu received = pduListener.waitForPdu( StopFreezePdu.class );
		
		// Shut it down and run some tests
		opscenterTwo.close();
		opscenterOne.close();
		
		// Compare what we got with what we expected
		Assert.assertNotNull( received );
		Assert.assertEquals( received, original );
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
