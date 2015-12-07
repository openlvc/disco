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
package org.openlvc.disco.utils;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"utils","bitfield"})
public class BitField32Test
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
	public void testBitField()
	{
		BitField32 field = new BitField32( 0 );

		field.setBit( 22 );
		int expected = (int)Math.pow(2,22);
		Assert.assertEquals( field.getInt(), expected );

		field.setBit( 2 );
		expected = (int)Math.pow(2,22) | (int)Math.pow(2,2);
		Assert.assertEquals( field.getInt(), expected );
		
		field.clearBit( 22 );
		expected = (int)Math.pow(2,2);
		Assert.assertEquals( field.getInt(), expected );

		field.clearBit( 22 );
		Assert.assertEquals( field.getInt(), expected );
		
		field.clearBit( 2 );
		expected = 0;
		Assert.assertEquals( field.getInt(), expected );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
