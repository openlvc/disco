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
package org.openlvc.disco.rpr.custom;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.common.TestPduListener;
import org.openlvc.disco.pdu.custom.CustomPdu;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"hla","rpr"})
public class CustomMapperTest extends AbstractTest
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
	/// TEST: testCustomerMapperRegistration   /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCustomRprMapperRegistration()
	{
		//
		// Create an OpsCenter and configure it
		//
		OpsCenter opscenterOne = new OpsCenter();
		OpsCenter opscenterTwo = new OpsCenter();

		// Create a listener for us to use
		TestPduListener pduListener = new TestPduListener();
		opscenterTwo.setPduListener( pduListener );

		// Register the custom PDU with the DIS side
		opscenterOne.getConfiguration().getDisConfiguration().registerCustomPdu( CustomPdu.class );
		opscenterTwo.getConfiguration().getDisConfiguration().registerCustomPdu( CustomPdu.class );
		
		// Register the custom FOM and Mapper with the HLA side
		opscenterOne.getConfiguration().getRprConfiguration().registerExtensionModules( CUSTOM_MODULE_PATH );
		opscenterTwo.getConfiguration().getRprConfiguration().registerExtensionModules( CUSTOM_MODULE_PATH );
		opscenterOne.getConfiguration().getRprConfiguration().registerExtensionMapper( CustomObjectClassMapper.class );
		opscenterTwo.getConfiguration().getRprConfiguration().registerExtensionMapper( CustomObjectClassMapper.class );
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

		// Send a PDU
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
