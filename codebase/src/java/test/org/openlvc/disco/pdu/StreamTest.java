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

import java.io.ByteArrayOutputStream;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.ParameterTypeDesignator;
import org.openlvc.disco.pdu.record.ArticulationParameter;
import org.openlvc.disco.utils.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"stream","streams"})
public class StreamTest extends AbstractTest
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
	@Test
	public void testDisStreamSerialize() throws Exception
	{
		// Create a PDU to send
		EntityStatePdu before = new EntityStatePdu();
		
		// Create the output stream and write the PDU
		ByteArrayOutputStream baos = new ByteArrayOutputStream( PDU.MAX_SIZE );
		DisOutputStream dos = new DisOutputStream( baos );
		before.writePdu( dos );
		dos.flush();
		
		// Read the PDU back in
		// This call will create a DisInputStream and read through it for us
		EntityStatePdu after = (EntityStatePdu)PduFactory.create( baos.toByteArray() );	
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream( PDU.MAX_SIZE );
		dos = new DisOutputStream( baos2 );
		after.writePdu( dos );
		dos.flush();
		
		Assert.assertEquals( baos2.toByteArray(), baos.toByteArray() );
	}
	
	@Test
	public void testDisStreamSerializeWithArticulations() throws Exception
	{
		// Create a PDU to send
		EntityStatePdu before = new EntityStatePdu();
		ArticulationParameter param1 = new ArticulationParameter();
		param1.setTypeDesignator( ParameterTypeDesignator.ArticulatedPart );
		param1.setChangeIndicator( (short)0 );
		param1.setAttachedTo( 0 );
		param1.setArticulatedPartTypeMetric( (short)7 );
		param1.setArticulatedPartTypeClass( 33 );
		param1.setArticulatedPartParameterValue( 3.14f );
		before.getArticulationParameter().add( param1 );
		
		// Create the output stream and write the PDU
		ByteArrayOutputStream baos = new ByteArrayOutputStream( PDU.MAX_SIZE );
		DisOutputStream dos = new DisOutputStream( baos );
		before.writePdu( dos );
		dos.flush();

		System.out.println(StringUtils.formatAsWireshark(baos.toByteArray()) );
		
		// Read the PDU back in
		// This call will create a DisInputStream and read through it for us
		EntityStatePdu after = (EntityStatePdu)PduFactory.create( baos.toByteArray() );	
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream( PDU.MAX_SIZE );
		dos = new DisOutputStream( baos2 );
		after.writePdu( dos );
		dos.flush();
		
		Assert.assertEquals( baos2.toByteArray(), baos.toByteArray() );
		Assert.assertEquals( after.getPduLength(), before.getPduLength() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
}
