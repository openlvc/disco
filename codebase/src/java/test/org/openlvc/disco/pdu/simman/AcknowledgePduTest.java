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
package org.openlvc.disco.pdu.simman;

import java.io.IOException;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.field.AcknowledgeFlag;
import org.openlvc.disco.pdu.field.ResponseFlag;
import org.openlvc.disco.pdu.record.EntityId;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups={"pdu","simman","Acknowledge"})
public class AcknowledgePduTest extends AbstractTest
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
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testAcknowledgePduSerialize() throws IOException, DiscoException
	{
		// turn an AcknowledgePdu into a byte[]
		AcknowledgePdu before = new AcknowledgePdu();
		before.setOriginatingEntity( new EntityId(1,2,3) );
		before.setReceivingEntity( new EntityId(4,5,6) );
		before.setAcknowledgeFlag( AcknowledgeFlag.StartResume );
		before.setResponseFlag( ResponseFlag.PendingOperatorAction );
		before.setRequestId( 7 );
		
		byte[] beforeArray = before.toByteArray();

		// convert it back
		AcknowledgePdu after = (AcknowledgePdu)PduFactory.getDefaultFactory()
		                                                 .create( beforeArray );
		Assert.assertEquals( after, before );

		// turn this one into a byte[]
		byte[] afterArray = after.toByteArray();

		// make sure the lengths are the same
		Assert.assertEquals( afterArray.length, beforeArray.length, "Lengths do not match" );
		Assert.assertEquals( afterArray, beforeArray );
		
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
