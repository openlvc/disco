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
package org.openlvc.disco.rpr.types.fixed;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.connection.rpr.types.fixed.EntityTypeStruct;
import org.openlvc.disco.pdu.record.EntityType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"hla","rpr","rpr-datatypes"})
public class EntityTypeStructTest extends AbstractTest
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

	///////////////////////////////////////////////////////////////////////////////////
	/// RPR FOM Testing Methods   /////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testEntityTypeBitsConversion() throws Exception
	{
		// Test with a static number
		EntityTypeStruct a = new EntityTypeStruct();
		a.setLongValue( 289077004414812417L );
		long result = a.getLongValue();
		Assert.assertEquals( result, 289077004414812417L, "Long Test" );
		
		// Test pushing it through a DIS type
		EntityType before = new EntityType( 1, 1, 225, 10, 20, 30, 40 );
		EntityTypeStruct struct = new EntityTypeStruct();
		struct.setValue( before );
		long longvalue = struct.getLongValue();

		EntityTypeStruct struct2 = new EntityTypeStruct();
		struct2.setLongValue( longvalue );
		EntityType after = struct2.getDisValue();
		
		Assert.assertEquals( after.getKind(), before.getKind(), "Kind was different" );
		Assert.assertEquals( after.getDomain(), before.getDomain(), "Domain was different"  );
		Assert.assertEquals( after.getCountry(), before.getCountry(), "Country Code was different"  );
		Assert.assertEquals( after.getCategory(), before.getCategory(), "Category was different"  );
		Assert.assertEquals( after.getSubcategory(), before.getSubcategory(), "Subcategory was different"  );
		Assert.assertEquals( after.getSpecific(), before.getSpecific(), "Specific was different"  );
		Assert.assertEquals( after.getExtra(), before.getExtra(), "Extra was different"  );
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Transmitter PDU Tests   ///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
