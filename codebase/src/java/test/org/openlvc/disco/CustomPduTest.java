/*
 *   Copyright 2024 Open LVC Project.
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
package org.openlvc.disco;

import org.openlvc.disco.common.TestPduListener;
import org.openlvc.disco.pdu.UnsupportedPDU;
import org.openlvc.disco.pdu.custom.CustomPdu;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups= {"pdu","custom"})
public class CustomPduTest extends AbstractTest
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// TEST: testCustomPduRegistration   //////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCustomPduRegistration() throws Exception
	{
		// create the custom PDU and serialize it so we can use the factory to deserialize it
		CustomPdu before = new CustomPdu();
		before.setCustomString( "banana" );
		byte[] beforeArray = before.toByteArray();
		
		// try to deserialize it with the default factory - which should fail
		PduFactory defaultFactory = PduFactory.getDefaultFactory();
		try
		{
			defaultFactory.create( beforeArray );
		}
		catch( UnsupportedPDU up )
		{
			// success!
		}

		// register the custom PDU with the factory and try again
		defaultFactory.registerCustomPdu( CustomPdu.class );
		CustomPdu after = (CustomPdu)defaultFactory.create( beforeArray );
		Assert.assertNotNull( after );
		Assert.assertEquals( after.getCustomString(), before.getCustomString() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// TEST: testCustomPduExchange   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCustomPduExchange() throws Exception
	{
		// Create the OpsCenter
		OpsCenter opscenterOne = new OpsCenter();
		OpsCenter opscenterTwo = new OpsCenter();
		
		// Create a listener for us to use
		TestPduListener pduListener = new TestPduListener();
		opscenterTwo.setPduListener( pduListener );

		// Register the custom PDU type
		opscenterTwo.getConfiguration().getDisConfiguration().registerCustomPdu( CustomPdu.class );

		// Open the session and send an instance of a customer PDU
		opscenterOne.open();
		opscenterTwo.open();
		CustomPdu before = new CustomPdu( "banana" );
		opscenterOne.send( before );
		
		// Receive the custom PDU and verify it
		CustomPdu after = pduListener.waitForPdu( CustomPdu.class );
		
		// Shut it down and run some tests
		opscenterTwo.close();
		opscenterOne.close();
		
		// Compare what we got with what we expected
		Assert.assertNotNull( after );
		Assert.assertEquals( after.getCustomString(), before.getCustomString() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
