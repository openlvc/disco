/*
 *   Copyright 2016 Open LVC Project.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"stream","streams"})
public class StreamTests extends AbstractTest
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
	/// Stream Serialization Testing Methods   ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	public void testDisStreamSerialize()
	{
		// Create a PDU to send
		EntityStatePdu before = new EntityStatePdu();
		
		// Create the output stream and write the PDU
		ByteArrayOutputStream baos = new ByteArrayOutputStream( PDU.MAX_SIZE );
		DisOutputStream dos = new DisOutputStream( baos );
		before.to( dos );
		
		// Read the PDU back in
		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		DisInputStream dis = 
		
		before.t
	}
	

	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
