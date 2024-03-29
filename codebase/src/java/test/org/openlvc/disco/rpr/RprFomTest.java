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

import java.util.ArrayList;
import java.util.Random;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.common.TestPduListener;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.connection.rpr.RprConnection;
import org.openlvc.disco.connection.rpr.objects.PhysicalEntity;
import org.openlvc.disco.connection.rpr.objects.RadioTransmitter;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.field.EncodingClass;
import org.openlvc.disco.pdu.field.EncodingType;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.ParameterTypeDesignator;
import org.openlvc.disco.pdu.field.appearance.GroundPlatformAppearance;
import org.openlvc.disco.pdu.field.appearance.enums.CamouflageType;
import org.openlvc.disco.pdu.field.appearance.enums.DamageState;
import org.openlvc.disco.pdu.field.appearance.enums.HatchState;
import org.openlvc.disco.pdu.field.appearance.enums.TrailingEffects;
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
	private TestPduListener leftListener;
	private TestPduListener rightListener;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	///////////////////////////////////////////////////////////////////////////////////
	/// Test Class Setup/Tear Down   //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@BeforeClass(alwaysRun=true,enabled=false)
	public void beforeClass()
	{
		// NOTE: Portico Specific Code
		// if we are using Portico, we can control the log level through system properties
		System.setProperty( "portico.loglevel", CommonSetup.CONSOLE_LOG_LEVEL );
		System.setProperty( "portico.connection", "jvm" );
	}
	
	@BeforeMethod(alwaysRun=true,enabled=false)
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
		this.leftListener = (TestPduListener)left.getPduListener();
		this.rightListener = (TestPduListener)right.getPduListener();

		this.left.open();
		this.right.open();
	}

	@AfterMethod(alwaysRun=true,enabled=false)
	public void afterMethod()
	{
		this.left.close();
		this.right.close();
	}
	
	@AfterClass(alwaysRun=true,enabled=false)
	public void afterClass()
	{
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// RPR FOM Testing Methods   /////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test(enabled=false)
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
	@Test(enabled=false)
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
	
	@Test(enabled=false)
	public void testRprTransmitterAttachedToPlatform()
	{
		// Radio transmitters can be attached to platforms. To achieve this in DIS, the transmitter
		// will set its Site/App/Entity ID to mirror that of the platform it is attached to. In RPR
		// however there is a separate "HostObjectIdentifer" field that points to the RTIobjectId
		// of the platform the transmitter is attached to.
		//
		// We can't fully test this from the DIS level, because the HLA information we want to
		// inspect will be wiped away in the bridge. We'll have to set up a situation where an
		// entity should appear at attached in DIS, and then dig into the receiving HLA side and
		// make sure that the host identifier is being properly identified and associatd.
		//
		// To do this we will create two platforms and a radio. The radio will start attached to
		// one platform and then we'll update it to be attached to another. As we dig under the
		// covers, we should see that the HostObjectIdentifier of the radio points to the
		// RTIobjectId of the first platform, and then changes to the second.
		
		EntityId id1 = new EntityId(1,1,1);
		EntityId id2 = new EntityId(7,7,8);
		
		// 1. Create the platforms we'll attached to
		EntityStatePdu platform1 = new EntityStatePdu();
		platform1.setEntityType( EntityType.fromString("1.1.1.1.2.3.4") );
		platform1.setEntityID( id1 );
		platform1.setMarking( "1/A" );

		EntityStatePdu platform2 = new EntityStatePdu();
		platform2.setEntityType( EntityType.fromString("1.1.1.1.2.3.4") );
		platform2.setEntityID( id2 );
		platform2.setMarking( "2/A" );

		// 2. Create the transmitter
		TransmitterPdu transmitter = new TransmitterPdu();
		transmitter.setEntityId( platform1.getEntityID() ); // attached to platform1 initially
		transmitter.setRadioID( 1 );
		
		// 3. Send the PDUs and wait for reception
		left.send( platform1 );
		left.send( platform2 );
		left.send( transmitter );
		rightListener.waitForEntityState( "1/A" );
		rightListener.waitForEntityState( "2/A" );
		rightListener.waitForTransmitter( new EntityId(1,1,1) );
		
		// 4. Fetch the internal HLA state from within the connection
		RprConnection rprConnection = (RprConnection)right.getConnection();
		
		RadioTransmitter rprTransmitter = rprConnection.getObjectStore().getHlaRadioTransmitter(
		    radio -> radio.getDisId().equals(id1) );
		Assert.assertNotNull( rprTransmitter );
		
		PhysicalEntity rprPlatform1 = rprConnection.getObjectStore().getHlaPhysicalEntity(
		    entity -> entity.getDisId().equals(id1) );
		Assert.assertNotNull( rprPlatform1 );

		PhysicalEntity rprPlatform2 = rprConnection.getObjectStore().getHlaPhysicalEntity(
		    entity -> entity.getDisId().equals(id2) );
		Assert.assertNotNull( rprPlatform2 );

		// 5. Make sure the radio has the HostObjectIdentifer and that it points to the right entity
		Assert.assertEquals( rprTransmitter.getHostObjectIdentifier(), rprPlatform1.getRtiObjectId() );
		Assert.assertNotEquals( rprTransmitter.getHostObjectIdentifier(), rprPlatform2.getRtiObjectId() );

		// 6. On the DIS sender, udpate the radio to be attached to the other platform and push
		//    that update out to the network 
		transmitter.setEntityId( platform2.getEntityID() );
		left.send( transmitter );
		rightListener.waitForTransmitter( platform2.getEntityID() );
		
		// 7. Make sure the underlying HLA data received by the receiver has changed
		Assert.assertNotEquals( rprTransmitter.getHostObjectIdentifier(), rprPlatform1.getRtiObjectId() );
		Assert.assertEquals( rprTransmitter.getHostObjectIdentifier(), rprPlatform2.getRtiObjectId() );
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Signal PDU Tests   ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test(enabled=false)
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
	
		// 3. Send it from the left
		left.send( signal );

		// 4. See if the right side picks it up
		SignalPdu received = rightListener.waitForSignal( new EntityId(2,3,4) );
		Assert.assertEquals( received.getEntityIdentifier(), new EntityId(2,3,4) );
		Assert.assertEquals( received.getEncodingScheme().getEncodingType(), EncodingType.Mulaw8 );
		Assert.assertEquals( received.getEncodingScheme().getEncodingClass(), EncodingClass.EncodedVoice );
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
	//
	// Entity State Basic Values
	//
	@Test(groups={"rpr-espdu"},enabled=false)
	public void testRprEntityStateBasics()
	{
		// 1. Prepare the values we will use
		EntityId entityId = new EntityId( 12, 13, 14 );
		EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		EntityType alternateType = new EntityType( 3, 3, 13, 1, 3, 5, 7 );
		EntityCapabilities capabilities = new EntityCapabilities( true, true, true, true, false );
		WorldCoordinate location = new WorldCoordinate( 31.9505, 115.8605, 100 );
		EulerAngles orientation = new EulerAngles( 1.0f, 2.0f, 3.0f );
		VectorRecord velocity = new VectorRecord( 1.0f, 2.0f, 3.0f );


		// 2. Create the Entity State PDU
		EntityStatePdu espdu = new EntityStatePdu();
		espdu.setEntityID( entityId );
		espdu.setEntityType( entityType );
		espdu.setAlternativeEntityType( alternateType );
		espdu.setCapabilities( capabilities );
		espdu.setForceID( ForceId.Opposing );
		espdu.setLocation( location );
		espdu.setMarking( "EntityBasic" );
		espdu.setOrientation( orientation );
		espdu.setLinearVelocity( velocity );
		
		// 3. Send it from the left
		left.send( espdu );

		// 4. See if the right side picks it up
		EntityStatePdu received = rightListener.waitForEntityState( "EntityBasic" );
		
		// EntityId
		Assert.assertEquals( received.getEntityID().getSiteId(), 12 );
		Assert.assertEquals( received.getEntityID().getAppId(), 13 );
		Assert.assertEquals( received.getEntityID().getEntityIdentity(), 14 );
		// EntityType
		Assert.assertEquals( received.getEntityType().getKind(), 1 );
		Assert.assertEquals( received.getEntityType().getDomain(), 1 );
		Assert.assertEquals( received.getEntityType().getCountry(), 225 );
		Assert.assertEquals( received.getEntityType().getCategory(), 2 );
		Assert.assertEquals( received.getEntityType().getSubcategory(), 4 );
		Assert.assertEquals( received.getEntityType().getSpecific(), 6 );
		Assert.assertEquals( received.getEntityType().getExtra(), 8 );
		// Alternate Entity Type
		Assert.assertEquals( received.getAlternativeEntityType().getKind(), 3 );
		Assert.assertEquals( received.getAlternativeEntityType().getDomain(), 3 );
		Assert.assertEquals( received.getAlternativeEntityType().getCountry(), 13 );
		Assert.assertEquals( received.getAlternativeEntityType().getCategory(), 1 );
		Assert.assertEquals( received.getAlternativeEntityType().getSubcategory(), 3 );
		Assert.assertEquals( received.getAlternativeEntityType().getSpecific(), 5 );
		Assert.assertEquals( received.getAlternativeEntityType().getExtra(), 7 );
		// Capabilities
		Assert.assertEquals( received.getCapabilities(), capabilities );
		// ForceId
		Assert.assertEquals( received.getForceID(), ForceId.Opposing );
		// LinearVelocity -- Doesn't seem to work, because in RPR it's all tied up in DR params
		//Assert.assertEquals( received.getLinearVelocity(), velocity );
		// Location
		Assert.assertEquals( received.getLocation(), location );
		// Marking
		Assert.assertEquals( received.getMarking(), "EntityBasic" );
		// Orientation
		Assert.assertEquals( received.getOrientation(), orientation );
	}

	//
	// Ground Platform Appearance
	//
	@Test(groups={"rpr-espdu"},enabled=false)
	public void testRprEntityStateGroundAppearance()
	{
		// 1. Prepare the values we will use
		EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		EntityCapabilities capabilities = new EntityCapabilities( true, true, true, true, false );

		// Appearance
		GroundPlatformAppearance appearance = new GroundPlatformAppearance();
		appearance.setActive( true );
		appearance.setConcealed( true );
		appearance.setBlackoutBreakLightsOn( true );
		appearance.setBlackOutLightsOn( true );
		appearance.setBreakLightsOn( true );
		appearance.setCamouflageType( CamouflageType.Other );
		appearance.setDamageState( DamageState.Destroyed );
		appearance.setEngineSmoking( true );
		appearance.setFirepowerKilled( true );
		appearance.setFlaming( true );
		appearance.setHatchState( HatchState.OpenAndPersonVisible );
		appearance.setHeadLightsOn( true );
		appearance.setInteriorLightsOn( true );
		appearance.setLauncherRaised( true );
		appearance.setMobilityKilled( true );
		appearance.setPowerplantOn( true );
		appearance.setRampDeployed( true );
		appearance.setSmokeEmanating( true );
		appearance.setSpotLightsOn( true );
		appearance.setTailLightsOn( true );
		appearance.setDustTrail( TrailingEffects.Large );
		int appearanceBitMask = appearance.getBits();
		
		// 2. Create the Entity State PDU
		EntityStatePdu espdu = new EntityStatePdu();
		espdu.setEntityType( entityType );
		espdu.setAppearance( appearanceBitMask );
		espdu.setCapabilities( capabilities );
		espdu.setMarking( "RprGroundAp" );
		
		// 3. Send it from the left
		left.send( espdu );

		// 4. See if the right side picks it up
		EntityStatePdu received = rightListener.waitForEntityState( "RprGroundAp" );
		
		// EntityType
		Assert.assertEquals( received.getEntityType().getKind(), 1 );
		Assert.assertEquals( received.getEntityType().getDomain(), 1 );
		Assert.assertEquals( received.getEntityType().getCountry(), 225 );
		Assert.assertEquals( received.getEntityType().getCategory(), 2 );
		Assert.assertEquals( received.getEntityType().getSubcategory(), 4 );
		Assert.assertEquals( received.getEntityType().getSpecific(), 6 );
		Assert.assertEquals( received.getEntityType().getExtra(), 8 );
		// Appearance
		Assert.assertEquals( received.getAppearance(), appearanceBitMask );
	}

	//
	// Dead Reckoning Values
	//
	@Test(groups={"rpr-espdu"},enabled=false)
	public void testRprEntityStateDeakReckoningReflection()
	{
		// 1. Prepare the values we will use
		EntityId entityId = new EntityId( 12, 13, 14 );
		EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		WorldCoordinate location = new WorldCoordinate( 31.9505, 115.8605, 100 );
		EulerAngles orientation = new EulerAngles( 1.0f, 2.0f, 3.0f );

		// Dead Reckoning
		byte[] drData = new byte[]{ 33, 34, -35, 68, 72, 12, 104, -13, 7, 1, 2, 3, 4, 5, 6 };
		VectorRecord vectorRecord = new VectorRecord( 1.0f, 2.0f, 3.0f );
		AngularVelocityVector angularVelocity = new AngularVelocityVector( 4, 5, 6 );
		DeadReckoningParameter drParameter = new DeadReckoningParameter( DeadReckoningAlgorithm.RPW,
		                                                                 drData,
		                                                                 null,
		                                                                 angularVelocity );

		// 2. Create the Entity State PDU
		EntityStatePdu espdu = new EntityStatePdu();
		espdu.setEntityID( entityId );
		espdu.setMarking( "RprDeadReck" );
		espdu.setEntityType( entityType );
		espdu.setLocation( location );
		espdu.setOrientation( orientation );
		espdu.setDeadReckoningParams( drParameter );
		espdu.setLinearVelocity( vectorRecord );
		
		// 3. Send it from the left
		left.send( espdu );

		// 4. See if the right side picks it up
		EntityStatePdu received = rightListener.waitForEntityState( "RprDeadReck" );
		
		// LinearVelocity
		Assert.assertEquals( received.getLinearVelocity(), vectorRecord );
		// Location
		Assert.assertEquals( received.getLocation(), location );
		// Dead Reckoning Param
		Assert.assertEquals( received.getDeadReckoningParams().getDeadReckoningAlgorithm(), DeadReckoningAlgorithm.RPW );
		Assert.assertEquals( received.getDeadReckoningParams().getEntityAngularVelocity(), angularVelocity );
	}

	//
	// Entity State Articulated Parts
	//
	@Test(groups={"rpr-espdu"},enabled=false)
	public void testRprEntityStateArticulations()
	{
		// 1. Prepare the values we will use
		EntityId entityId = new EntityId( 12, 13, 14 );
		EntityType entityType = new EntityType( 1, 1, 225, 2, 4, 6, 8 );
		WorldCoordinate location = new WorldCoordinate( 31.9505, 115.8605, 100 );
		EulerAngles orientation = new EulerAngles( 1.0f, 2.0f, 3.0f );
		// Articulations
		short typeMetric = 1;
		short typeClass = 4096;
		float parameterValue = 3.14f;
		//int parameterType = (int)articulationParameterTypeMetric << 32 | (int)articulationPartType << 48;
		ArrayList<ArticulationParameter> articulations = new ArrayList<>();
		ArticulationParameter parameter = ArticulationParameter.newArticulatedPart( typeMetric,
		                                                                            typeClass,
		                                                                            parameterValue );

		parameter.setChangeIndicator( (short)77 );
		parameter.setAttachedTo( 55 );
		articulations.add( parameter );

		// 2. Create the Entity State PDU
		EntityStatePdu espdu = new EntityStatePdu();
		espdu.setEntityID( entityId );
		espdu.setEntityType( entityType );
		espdu.setArticulationParameters( articulations );
		espdu.setLocation( location );
		espdu.setMarking( "RprArticula" );
		espdu.setOrientation( orientation );
		
		// 3. Send it from the left
		left.send( espdu );

		// 4. See if the right side picks it up
		EntityStatePdu received = rightListener.waitForEntityState( "RprArticula" );
		
		// EntityId
		Assert.assertEquals( received.getEntityID().getSiteId(), 12 );
		Assert.assertEquals( received.getEntityID().getAppId(), 13 );
		Assert.assertEquals( received.getEntityID().getEntityIdentity(), 14 );
		// EntityType
		Assert.assertEquals( received.getEntityType().getKind(), 1 );
		Assert.assertEquals( received.getEntityType().getDomain(), 1 );
		Assert.assertEquals( received.getEntityType().getCountry(), 225 );
		Assert.assertEquals( received.getEntityType().getCategory(), 2 );
		Assert.assertEquals( received.getEntityType().getSubcategory(), 4 );
		Assert.assertEquals( received.getEntityType().getSpecific(), 6 );
		Assert.assertEquals( received.getEntityType().getExtra(), 8 );
		// Articulation
		Assert.assertEquals( received.getArticulationParameter().size(), 1 );
		ArticulationParameter receivedParam = received.getArticulationParameter().get(0);
		Assert.assertEquals( receivedParam.getTypeDesignator(), ParameterTypeDesignator.ArticulatedPart );
		Assert.assertEquals( receivedParam.getChangeIndicator(), (short)77 );
		Assert.assertEquals( receivedParam.getAttachedTo(), 55 );
		Assert.assertEquals( receivedParam.getArticulatedPartTypeMetric(), typeMetric );
		Assert.assertEquals( receivedParam.getArticulatedPartTypeClass(), typeClass );
		Assert.assertEquals( receivedParam.getArticulatedPartParameterValue(), parameterValue );
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
//	// Run some stuff standalone, without the TestNG harness
//	public static void main( String[] args ) throws Exception
//	{
//		CommonSetup.commonBeforeSuiteSetup();
//		RprFomTest test = new RprFomTest();
//		test.beforeClass();
//		test.beforeMethod();
//		test.testRprEntityStateArticulations();
//		test.afterMethod();
//		test.afterClass();
//	}
}
