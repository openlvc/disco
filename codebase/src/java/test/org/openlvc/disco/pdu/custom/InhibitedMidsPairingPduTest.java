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
package org.openlvc.disco.pdu.custom;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.pdu.record.EntityId;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups= {"pdu","custom","tdl"})
public class InhibitedMidsPairingPduTest extends AbstractTest
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
	public void testInhibitedMidsPairingPduSerialize() throws Exception
	{
		// turn the PDU into a byte[]
		InhibitedMidsPairingPdu before = new InhibitedMidsPairingPdu();
		before.setTdlType( 123 );
		before.setSourceEntityId( new EntityId(1,2,3) );
		before.setDestinationEntityId( new EntityId(4,5,6) );
		before.setMidsTerminalEnabled( true );
		byte[] beforeArray = before.toByteArray();

		// convert it back
		InhibitedMidsPairingPdu after = PduFactory.create( beforeArray );

		// compare the pair!
		Assert.assertEquals( after.getTdlType(),             before.getTdlType() );
		Assert.assertEquals( after.getSourceEntityId(),      before.getSourceEntityId() );
		Assert.assertEquals( after.getDestinationEntityId(), before.getDestinationEntityId() );
		Assert.assertEquals( after.isMidsTerminalEnabled(),  before.isMidsTerminalEnabled() );

		// turn the PDU into a byte[]
		before = new InhibitedMidsPairingPdu();
		before.setTdlType( 321 );
		before.setSourceEntityId( new EntityId(3,2,1) );
		before.setDestinationEntityId( new EntityId(6,5,4) );
		before.setMidsTerminalEnabled( false );
		beforeArray = before.toByteArray();

		// convert it back
		after = PduFactory.create( beforeArray );

		// compare the pair!
		Assert.assertEquals( after.getTdlType(),             before.getTdlType() );
		Assert.assertEquals( after.getSourceEntityId(),      before.getSourceEntityId() );
		Assert.assertEquals( after.getDestinationEntityId(), before.getDestinationEntityId() );
		Assert.assertEquals( after.isMidsTerminalEnabled(),  before.isMidsTerminalEnabled() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
