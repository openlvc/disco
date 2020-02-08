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

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"hla","rpr"})
public class ObjectClassTest extends AbstractTest
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
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void testObjectClassAddRemoteAttributes()
	{
		ObjectModel model = new ObjectModel();
		
		// Create an object class to work with
		ObjectClass testClass = model.createObjectClass( "testClass", model.getObjectRoot() );
		
		// Create an attribute and add to the class
		AttributeClass testAttribute = new AttributeClass( "testAttribute" );
		testClass.addAttribute( testAttribute );
		Assert.assertNotNull( testClass.getAttribute("testAttribute") );
		
		// Create another attribute with different properties, but the same name and add to class
		AttributeClass dupeAttribute = new AttributeClass( "testAttribute" );
		super.assertException( () -> { testClass.addAttribute(dupeAttribute); }, DiscoException.class );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
