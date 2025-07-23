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
import java.util.Arrays;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.appearance.enums.IedPresent;
import org.openlvc.disco.pdu.field.appearance.enums.ObjectDamage;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.LinearSegmentParameter;
import org.openlvc.disco.pdu.record.ObjectId;
import org.openlvc.disco.pdu.record.ObjectType;
import org.openlvc.disco.pdu.record.SimulationAddress;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups={"pdu","syntheticenvironment","LinearObjectState"})
public class LinearObjectStatePduTest extends AbstractTest
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
	public void testLinearObjectStatePduSerialize() throws IOException, DiscoException
	{
		// turn an LinearObjectStatePdu into a byte[]
		LinearObjectStatePdu before = new LinearObjectStatePdu();
		before.setObjectId( new ObjectId(1, 2, 3) );
		before.setReferencedObjectId( new ObjectId(4, 5, 6) );
		before.setUpdateNumber( 7 );
		before.setForceId( ForceId.Neutral );
		before.setRequestorId( new SimulationAddress(8, 9) );
		before.setReceivingId( new SimulationAddress(10, 11) );
		before.setObjectType( new ObjectType(12, 13, 14, 15) );
		
		LinearSegmentParameter segment0 = new LinearSegmentParameter();
		segment0.setNumber( (short)0 );
		segment0.setModifications( (byte)0 );
		segment0.getGeneralApperance().setActive( true );
		segment0.getGeneralApperance().setDamage( ObjectDamage.NoDamage );
		segment0.getGeneralApperance().setFlaming( false );
		segment0.getGeneralApperance().setIedPresent( IedPresent.None );
		segment0.getGeneralApperance().setPercentComplete( 55 );
		segment0.getGeneralApperance().setPredistributed( false );
		segment0.getGeneralApperance().setSmoking( false );
		segment0.setSpecificApperance( new byte[] {16, 17, 18, 19} );
		segment0.setLocation( new WorldCoordinate(20.0, 21.0, 22.0) );
		segment0.setOrientation( new EulerAngles(23.0f, 24.0f, 25.0f) );
		segment0.setHeight( 26 );
		segment0.setWidth( 27 );
		segment0.setHeight( 28 );
		segment0.setDepth( 29 );
		
		LinearSegmentParameter segment1 = new LinearSegmentParameter();
		segment1.setNumber( (short)1 );
		segment1.setModifications( (byte)0 );
		segment1.getGeneralApperance().setActive( false );
		segment1.getGeneralApperance().setDamage( ObjectDamage.Destroyed );
		segment1.getGeneralApperance().setFlaming( true );
		segment1.getGeneralApperance().setIedPresent( IedPresent.CompletelyHidden );
		segment1.getGeneralApperance().setPercentComplete( 100 );
		segment1.getGeneralApperance().setPredistributed( true );
		segment1.getGeneralApperance().setSmoking( true );
		segment1.setSpecificApperance( new byte[] {30, 31, 32, 33} );
		segment1.setLocation( new WorldCoordinate(34.0, 35.0, 36.0) );
		segment1.setOrientation( new EulerAngles(37.0f, 38.0f, 39.0f) );
		segment1.setHeight( 40 );
		segment1.setWidth( 41 );
		segment1.setHeight( 42 );
		segment1.setDepth( 43 );
		
		before.setSegments( Arrays.asList(segment0, segment1) );
		
		byte[] beforeArray = before.toByteArray();

		// convert it back
		LinearObjectStatePdu after = (LinearObjectStatePdu)PduFactory.getDefaultFactory()
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
