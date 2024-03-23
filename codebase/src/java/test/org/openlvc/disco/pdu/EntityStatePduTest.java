/*
 *   Copyright 2015 Open LVC Project.
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

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.appearance.GroundPlatformAppearance;
import org.openlvc.disco.pdu.field.appearance.enums.CamouflageType;
import org.openlvc.disco.pdu.field.appearance.enums.HatchState;
import org.openlvc.disco.pdu.field.appearance.enums.PaintScheme;
import org.openlvc.disco.pdu.field.appearance.enums.TrailingEffects;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"pdu","espdu","EntityState"})
public class EntityStatePduTest extends AbstractTest
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

	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testEntityStatePduSerialize() throws Exception
	{
		// turn an ESPDU into a byte[]
		EntityStatePdu before = new EntityStatePdu();
		byte[] beforeArray = before.toByteArray();
		
		// convert it back
		EntityStatePdu after = (EntityStatePdu)PduFactory.getDefaultFactory().create( beforeArray );
		//Assert.assertEquals( after, before );
		
		// turn this one into a byte[]
		byte[] afterArray = after.toByteArray();
		
		// make sure the lengths are the same
		Assert.assertEquals( afterArray.length, beforeArray.length, "Lengths do not match" );
		Assert.assertEquals( afterArray, beforeArray );
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Appearance Testing Method   ///////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testGroundPlatformAppearance()
	{
		GroundPlatformAppearance before = new GroundPlatformAppearance();
		
		// Set the values
		before.setPaintScheme( PaintScheme.Camouflage );
		before.setMobilityKilled( true );
		before.setFirepowerKilled( true );
		before.setDustTrail( TrailingEffects.Large );
		before.setHatchState( HatchState.OpenAndPersonVisible );
		before.setHeadLightsOn( true );
		before.setTailLightsOn( true );
		before.setBreakLightsOn( true );
		before.setLauncherRaised( true );
		before.setCamouflageType( CamouflageType.Other );
		before.setConcealed( true );
		before.setTentExtended( true );
		before.setRampDeployed( true );
		before.setBlackOutLightsOn( true );
		before.setBlackoutBreakLightsOn( true );
		before.setSpotLightsOn( true );
		before.setInteriorLightsOn( true );
		before.setOccupantsSurrendered( true );
		before.setMasked( true );
		
		// Convert to int
		int value = before.getBits();
		
		// Convert back
		GroundPlatformAppearance after = new GroundPlatformAppearance( value );
		Assert.assertEquals( after.getPaintSchemeValue(), PaintScheme.Camouflage.value() );
		Assert.assertTrue( after.isMobilityKilled() );
		Assert.assertTrue( after.isFirepowerKilled() );
		Assert.assertEquals( after.getDustTrailValue(), TrailingEffects.Large.value() );
		Assert.assertEquals( after.getHatchStateValue(), HatchState.OpenAndPersonVisible.value() );
		Assert.assertTrue( after.isHeadLightsOn() );
		Assert.assertTrue( after.isTailLightsOn() );
		Assert.assertTrue( after.isBreakLightsOn() );
		Assert.assertTrue( after.isLauncherRaised() );
		Assert.assertEquals( after.getCamouflageTypeValue(), CamouflageType.Other.value() );
		Assert.assertTrue( after.isConcealed() );
		Assert.assertTrue( after.isTentExtended() );
		Assert.assertTrue( after.isRampDeployed() );
		Assert.assertTrue( after.isBlackOutLightsOn() );
		Assert.assertTrue( after.isBlackoutBreakLightsOn() );
		Assert.assertTrue( after.isSpotLightsOn() );
		Assert.assertTrue( after.isInteriorLightsOn() );
		Assert.assertTrue( after.isOccupantsSurrendered() );
		Assert.assertTrue( after.isMasked() );
	}

	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
