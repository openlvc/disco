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
package org.openlvc.disco.pdu.synthenv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.appearance.GeneralObjectApperance;
import org.openlvc.disco.pdu.field.appearance.enums.IedPresent;
import org.openlvc.disco.pdu.field.appearance.enums.ObjectDamage;
import org.openlvc.disco.pdu.record.ObjectId;
import org.openlvc.disco.pdu.record.ObjectType;
import org.openlvc.disco.pdu.record.SimulationAddress;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups={"pdu","syntheticenvironment","ArealObjectState"})
public class ArealObjectStatePduTest extends AbstractTest
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
	public void testArealObjectStatePduSerialize() throws IOException, DiscoException
	{
		// turn an ArealObjectState into a byte[]
		GeneralObjectApperance generalApperance = new GeneralObjectApperance();
		generalApperance.setPercentComplete( 100 );
		generalApperance.setDamage( ObjectDamage.Damaged );
		generalApperance.setPredistributed( true );
		generalApperance.setActive( true );
		generalApperance.setSmoking( true );
		generalApperance.setFlaming( false );
		generalApperance.setIedPresent( IedPresent.PartiallyHidden );
		
		
		ArealObjectStatePdu before = new ArealObjectStatePdu();
		before.setObjectId( new ObjectId(1, 2, 3) );
		before.setReferencedObjectId( new ObjectId(4, 5, 6) );
		before.setUpdateNumber( 7 );
		before.setForceId( ForceId.Neutral );
		before.setModifications( (byte)8 );
		before.setObjectType( new ObjectType(9, 10, 11, 12) );
		before.setSpecificAppearance( new byte[] { 13, 14, 15, 16 } );
		before.setGeneralApperance( generalApperance );
		before.setRequestorId( new SimulationAddress(19, 20) );
		before.setReceivingId( new SimulationAddress(21, 22) );
		
		List<WorldCoordinate> points = new ArrayList<>();
		points.add( new WorldCoordinate(23.0, 24.0, 25.0) );
		points.add( new WorldCoordinate(26.0, 27.0, 28.0) );
		before.setPoints( points );
		
		byte[] beforeArray = before.toByteArray();

		// convert it back
		ArealObjectStatePdu after = (ArealObjectStatePdu)PduFactory.getDefaultFactory()
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
