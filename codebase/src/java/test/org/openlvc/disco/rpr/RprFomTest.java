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
package org.openlvc.disco.rpr;

import java.util.Random;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.common.TestListener;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.field.EncodingType;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"hla","rpr"})
public class RprFomTest extends AbstractTest
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private OpsCenter left;
	private OpsCenter right;
	//private TestListener leftListener;
	private TestListener rightListener;

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
		// create the configuration objects we'll use
		DiscoConfiguration leftConfiguration = new DiscoConfiguration();
		leftConfiguration.setConnection( "rpr" );
		leftConfiguration.getRprConfiguration().setCreateFederation( true );
		leftConfiguration.getRprConfiguration().setFederateName( "left" );
		leftConfiguration.getRprConfiguration().setFederationName( "test-federation" );
		leftConfiguration.getRprConfiguration().setRtiInstallDir( "./" );

		DiscoConfiguration rightConfiguration = new DiscoConfiguration();
		rightConfiguration.setConnection( "rpr" );
		rightConfiguration.getRprConfiguration().setCreateFederation( true );
		rightConfiguration.getRprConfiguration().setFederateName( "right" );
		rightConfiguration.getRprConfiguration().setFederationName( "test-federation" );
		rightConfiguration.getRprConfiguration().setRtiInstallDir( "./" );

		// create the opscenter instances we'll use for testing
		this.left = super.newOpsCenter( leftConfiguration );
		this.right = super.newOpsCenter( rightConfiguration );
		//this.leftListener = (TestListener)left.getPduListener();
		this.rightListener = (TestListener)right.getPduListener();

		this.left.open();
		this.right.open();
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
		this.left.close();
		this.right.close();
	}
	
	@AfterClass(alwaysRun=true)
	public void afterClass()
	{
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// RPR FOM Testing Methods   /////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test(enabled=true)
	public void testRprFomConnection()
	{
		// 1. Create an Entity State PDU
		TransmitterPdu transmitter = new TransmitterPdu();
		transmitter.setEntityId( new EntityId(1,2,3) );
		left.send( transmitter );
		
		// 2. See if the right side picked it up
		TransmitterPdu received = rightListener.waitForTransmitter( new EntityId(1,2,3) );
		received.getEntityId();
	}
	
	@Test
	public void testRprFomSignalInteraction()
	{
		byte[] data = new byte[2048];
		new Random().nextBytes( data );

		// 1. Create the Signal Interaction
		SignalPdu signal = new SignalPdu();
		signal.setEntityIdentifier( new EntityId(2,3,4) );
		signal.setData( data );
		signal.getEncodingScheme().setEncodingType( EncodingType.PCM16 );
		
		// 2. Send it from the left
		left.send( signal );
		
		// 3. See if the right side picks it up
		SignalPdu received = rightListener.waitForSignal( new EntityId(2,3,4) );
		Assert.assertEquals( received.getEntityIdentifier(), new EntityId(2,3,4) );
		Assert.assertEquals( received.getEncodingScheme().getEncodingType(), EncodingType.PCM16 );
		
		byte[] receivedData = received.getData();
		Assert.assertEquals( receivedData.length, data.length );
		for( int i = 0; i < data.length; i++ )
			Assert.assertEquals(receivedData[i],data[i]);
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	// Run some stuff standalone, without the TestNG harness
	//public static void main( String[] args ) throws Exception
	//{
	//	CommonSetup.commonBeforeSuiteSetup();
	//	RprFomTest test = new RprFomTest();
	//	test.beforeClass();
	//	test.beforeMethod();
	//	test.testRprFomSignalInteraction();
	//	test.afterMethod();
	//	test.afterClass();
	//}
}
