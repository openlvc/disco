/*
 *   Copyright 2025 Open LVC Project.
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

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"hla","rpr"})
public class RprFomLifecycleTest extends AbstractTest
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
	public void testRprLoadFomOverrideDirectory()
	{
		DiscoConfiguration config = new DiscoConfiguration();
		config.setConnection( "rpr" );
		config.getRprConfiguration().setFomOverridePath( "resources/testdata/hla/fomOverride" );
		// Also add a custom extension, to make sure they can be used with overrides path
		config.getRprConfiguration().registerExtensionModules( "resources/testdata/hla/Custom-FOM-Module.xml" );
		
		// Create Opscenter with configuration
		// We probably don't need to do this, but should we? It will help confirm that the
		// modules flow through to the actualy running Disco
		//OpsCenter opsCenter = new OpsCenter( config );
		//opsCenter.open();
		//opsCenter.close();
		
		// Check that the FOM modules are loaded
		URL[] modules = config.getRprConfiguration().getRegisteredFomModules();
		Assert.assertEquals( modules.length, 5 );
		
		// Check the names of the modules
		Set<String> expectedNames = new HashSet<>();
		expectedNames.add( "HLAstandardMIM.xml" );
		expectedNames.add( "RPR-Base_v2.0.xml" );
		expectedNames.add( "RPR-Physical_v2.0.xml" );
		expectedNames.add( "RPR-Switches_v2.0.xml" );
		expectedNames.add( "Custom-FOM-Module.xml" );
		Set<String> actualNames = new HashSet<>();
		for( URL module : modules )
			actualNames.add( new File(module.getPath()).getName() );
		Assert.assertEquals( actualNames, expectedNames );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
