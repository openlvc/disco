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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups= {"pdu","custom","irc"})
public class IRCChannelMessagePduTest extends AbstractTest
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
	public void testIrcChannelMessagePduSerialize() throws Exception
	{
		// turn the PDU into a byte[]
		IrcMessagePdu before = new IrcMessagePdu();
		before.setChannelName( "test-channel" );
		before.setSender( "test-sender" );
		before.setMessage( "test-message" );
		before.setOrigin( "test-origin" );
		byte[] beforeArray = before.toByteArray();
		
		// convert it back
		IrcMessagePdu after = PduFactory.create( beforeArray );
		
		// compare the pair!
		Assert.assertEquals( after.getChannelName(),  before.getChannelName() );
		Assert.assertEquals( after.getSender(),       before.getSender() );
		Assert.assertEquals( after.getMessage(),      before.getMessage() );
		Assert.assertEquals( after.getTimeReceived(), before.getTimeReceived() );
		Assert.assertEquals( after.getOrigin(),       before.getOrigin() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
