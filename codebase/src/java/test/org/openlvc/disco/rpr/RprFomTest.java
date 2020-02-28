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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.common.TestListener;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.field.EncodingClass;
import org.openlvc.disco.pdu.field.EncodingType;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.ParameterTypeDesignator;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.AngularVelocityVector;
import org.openlvc.disco.pdu.record.ArticulationParameter;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EncodingScheme;
import org.openlvc.disco.pdu.record.EntityCapabilities;
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
	private TestListener leftListener;
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
		this.leftListener = (TestListener)left.getPduListener();
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
	@Test
	public void testRprConnection()
	{
		// 1. Create an Entity State PDU
		TransmitterPdu transmitter = new TransmitterPdu();
		transmitter.setEntityId( new EntityId(1,2,3) );
		left.send( transmitter );
		
		// 2. See if the right side picked it up
		TransmitterPdu received = rightListener.waitForTransmitter( new EntityId(1,2,3) );
		received.getEntityId();
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Transmitter PDU Tests   ///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testRprTransmitterWithSameSiteAppEntityId()
	{
		// Radio Transmitters can have the same site, application and entity ID, but differ
		// in terms of their Radio Id. Make sure we can ...
		
		// 1. Create a Transmitter
		TransmitterPdu transmitterOne = new TransmitterPdu();
		transmitterOne.setEntityId( new EntityId(1,2,3) );
		transmitterOne.setRadioID( 1 );
		
		TransmitterPdu transmitterTwo = new TransmitterPdu();
		transmitterTwo.setEntityId( new EntityId(1,2,3) );
		transmitterTwo.setRadioID( 2 );
		
		// 2. Send Radio #1 from left, and #2 from right, and make sure they get through
		left.send( transmitterOne );
		TransmitterPdu received = rightListener.waitForTransmitter( new EntityId(1,2,3) );
		Assert.assertEquals( received.getRadioID(), transmitterOne.getRadioID() );
		
		right.send( transmitterTwo );
		received = null;
		received = leftListener.waitForTransmitter( new EntityId(1,2,3) );
		Assert.assertEquals( received.getRadioID(), transmitterTwo.getRadioID() );
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Signal PDU Tests   ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testRprSignalInteraction()
	{
		// 1. Prepare the bits and pieces to test
		EncodingScheme encoding = new EncodingScheme();
		encoding.setEncodingClass( EncodingClass.EncodedVoice );
		encoding.setEncodingType( EncodingType.Mulaw8 );
		byte[] data = new byte[960];
		new Random().nextBytes( data );

		// 2. Create the Signal Interaction
		SignalPdu signal = new SignalPdu();
		signal.setEntityIdentifier( new EntityId(2,3,4) );
		signal.setEncodingScheme( encoding );
		signal.setRadioID( 5 );
		signal.setSampleRate( 32000 );
		signal.setSamples( 960 );
		signal.setData( data );
		signal.getEncodingScheme().setEncodingType( EncodingType.PCM16 );
	
		// 3. Send it from the left
		left.send( signal );

		// 4. See if the right side picks it up
		SignalPdu received = rightListener.waitForSignal( new EntityId(2,3,4) );
		Assert.assertEquals( received.getEntityIdentifier(), new EntityId(2,3,4) );
		Assert.assertEquals( received.getEncodingScheme().getEncodingType(), EncodingType.Mulaw8 );
		Assert.assertEquals( received.getEncodingScheme().getEncodingClass(), EncodingClass.EncodedVoice );
		Assert.assertEquals( received.getEncodingScheme().getEncodingType(), EncodingType.Mulaw8 );
		Assert.assertEquals( received.getRadioID(), 5 );
		Assert.assertEquals( received.getSampleRate(), 32000 );
		Assert.assertEquals( received.getSamples(), 960 );
		// Data
		byte[] receivedData = received.getData();
		Assert.assertEquals( receivedData.length, data.length );
		for( int i = 0; i < data.length; i++ )
			Assert.assertEquals(receivedData[i],data[i]);
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Entity State PDU Tests   //////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test(groups= {"espdu-rpr"})
	public void testRprEntityState()
	{
		// 1. Prepare the values we will use
		EntityId entityId = new EntityId( 12, 13, 14 );
		EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		EntityType alternateType = new EntityType( 3, 3, 13, 1, 3, 5, 7 );
		int appearanceBitMask = 123;
		EntityCapabilities capabilities = new EntityCapabilities( true, false, true, false, true );
		WorldCoordinate location = new WorldCoordinate( 31.9505, 115.8605, 100 );
		EulerAngles orientation = new EulerAngles( 1.0f, 2.0f, 3.0f );
		// Articulations
		ArrayList<ArticulationParameter> articulations = new ArrayList<>();
		ArticulationParameter articulation = new ArticulationParameter( ParameterTypeDesignator.ArticulatedPart,
		                                                                (short)1,
		                                                                1, 
		                                                                123, 
		                                                                BigInteger.valueOf(123456789) );
		articulations.add( articulation );

		// Dead Reckoning
		byte[] drData = new byte[]{ 33, 34, -35, 68, 72, 12, 104, -13, 7, 1, 2, 3, 4, 5, 6 };
		VectorRecord vectorRecord = new VectorRecord( 1.0f, 2.0f, 3.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 4, 5, 6 );
		DeadReckoningParameter drParameter = new DeadReckoningParameter( DeadReckoningAlgorithm.RPW,
		                                                                 drData,
		                                                                 vectorRecord,
		                                                                 angularVelocity );
		
		// 2. Create the Entity State PDU
		EntityStatePdu espdu = new EntityStatePdu();
		espdu.setEntityID( entityId );
		espdu.setEntityType( entityType );
		espdu.setAlternativeEntityType( alternateType );
		espdu.setAppearance( appearanceBitMask );
		espdu.setArticulationParameters( articulations );
		espdu.setCapabilities( capabilities );
		espdu.setDeadReckoningParams( drParameter );
		espdu.setForceID( ForceId.Opposing );
		espdu.setLinearVelocity( vectorRecord );
		espdu.setLocation( location );
		espdu.setMarking( "BingoBob11c" );
		espdu.setOrientation( orientation );
		
		// 3. Send it from the left
		left.send( espdu );

		// 4. See if the right side picks it up
		EntityStatePdu received = rightListener.waitForEntityState( "BingoBob11c" );
		
		// EntityId
		Assert.assertEquals( received.getEntityID().getSiteId(), 12 );
		Assert.assertEquals( received.getEntityID().getAppId(), 13 );
		Assert.assertEquals( received.getEntityID().getEntityIdentity(), 14 );
		// EntityType
		Assert.assertEquals( received.getEntityType().getKind(), 3 );
		Assert.assertEquals( received.getEntityType().getDomain(), 1 );
		Assert.assertEquals( received.getEntityType().getCountry(), 225 );
		Assert.assertEquals( received.getEntityType().getCategory(), 2 );
		Assert.assertEquals( received.getEntityType().getSubcategory(), 4 );
		Assert.assertEquals( received.getEntityType().getSpecific(), 6 );
		Assert.assertEquals( received.getEntityType().getExtra(), 8 );
		// Alternate Entity Type
		Assert.assertEquals( received.getAlternativeEntityType().getKind(), 1 );
		Assert.assertEquals( received.getAlternativeEntityType().getDomain(), 3 );
		Assert.assertEquals( received.getAlternativeEntityType().getCountry(), 13 );
		Assert.assertEquals( received.getAlternativeEntityType().getCategory(), 1 );
		Assert.assertEquals( received.getAlternativeEntityType().getSubcategory(), 3 );
		Assert.assertEquals( received.getAlternativeEntityType().getSpecific(), 5 );
		Assert.assertEquals( received.getAlternativeEntityType().getExtra(), 7 );
		// Appearance
		Assert.assertEquals( received.getAppearance(), appearanceBitMask );
		// Articulation
		Assert.assertEquals( received.getArticulationParameter().size(), 1 );
		ArticulationParameter receivedParam = received.getArticulationParameter().get(0);
		Assert.assertEquals( receivedParam.getTypeDesignator(), ParameterTypeDesignator.ArticulatedPart );
		Assert.assertEquals( receivedParam.getChangeIndicator(), (short)1 );
		Assert.assertEquals( receivedParam.getAttachedTo(), 1 );
		Assert.assertEquals( receivedParam.getParameterType(), 123 );
		Assert.assertEquals( receivedParam.getParameterValue(), BigInteger.valueOf(123456789) );
		// Capabilities
		Assert.assertEquals( received.getCapabilities(), capabilities );
		// ForceId
		Assert.assertEquals( received.getForceID(), ForceId.Opposing );
		// LinearVelocity
		Assert.assertEquals( received.getLinearVelocity(), vectorRecord );
		// Location
		Assert.assertEquals( received.getLocation(), location );
		// Marking
		Assert.assertEquals( received.getMarking(), "BingoBob11c" );
		// Orientation
		Assert.assertEquals( received.getOrientation(), orientation );
		// Dead Reckoning Param
		Assert.assertEquals( received.getDeadReckoningParams().getDeadReckoningAlgorithm(), DeadReckoningAlgorithm.RPW );
		Assert.assertEquals( received.getDeadReckoningParams().getEntityAngularVelocity(), angularVelocity );
		Assert.assertEquals( received.getDeadReckoningParams().getEntityLinearAcceleration(), vectorRecord );
		byte[] receivedDrData = received.getDeadReckoningParams().getDeadReckoningOtherParamaters();
		Assert.assertEquals( receivedDrData.length, drData.length );
		for( int i = 0; i < drData.length; i++ )
			Assert.assertEquals(receivedDrData[i],drData[i]);
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
