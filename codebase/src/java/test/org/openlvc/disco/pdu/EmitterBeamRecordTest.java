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
package org.openlvc.disco.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.field.BeamFunction;
import org.openlvc.disco.pdu.field.HighDensityTrackJam;
import org.openlvc.disco.pdu.record.BeamData;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.FundamentalParameterData;
import org.openlvc.disco.pdu.record.JammingTechnique;
import org.openlvc.disco.pdu.record.TrackJamData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"record","EmitterBeam"})
public class EmitterBeamRecordTest extends AbstractTest
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
		// no-op
	}
	
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod()
	{
		// no-op
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
		// no-op
	}
	
	@AfterClass(alwaysRun=true)
	public void afterClass()
	{
		// no-op
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testEmitterBeamSerialize() throws IOException
	{
		BeamData beamData = new BeamData(0.1f, 0.2f, 0.3f, 0.4f, 0.5f );
		JammingTechnique jammingTechnique = new JammingTechnique( 1, 2, 3, 4 );
		FundamentalParameterData fundamentalData = new FundamentalParameterData( 30000000f, 
		                                                                         5000000f, 
		                                                                         30f, 
		                                                                         2000000f, 
		                                                                         44000f ); 
		EntityId targetId = new EntityId( 5, 6, 7 );
		TrackJamData target = new TrackJamData( targetId, (short)8, (short)9 );
		List<TrackJamData> targets = List.of( target );
		
		EmitterBeam beamRecord = new EmitterBeam();
		beamRecord.setBeamActive( true );
		beamRecord.setBeamData( beamData );
		beamRecord.setBeamFunction( BeamFunction.Jamming );
		beamRecord.setBeamNumber( (short)10 );
		beamRecord.setHighDensity( HighDensityTrackJam.Selected );
		beamRecord.setJammingTechnique( jammingTechnique );
		beamRecord.setParameterData( fundamentalData );
		beamRecord.setParameterIndex( 5 );
		beamRecord.setTargets( targets );
		
		// Test byte length 
		int expectedByteLength = 60; // 52 fixed + 8 for one target
		int actualByteLength = beamRecord.getByteLength();
		Assert.assertEquals( actualByteLength, expectedByteLength );
		
		// And also test data length (length in 32-bit words)
		int expectedDataLength = 15; // byte length / 4
		int actualDataLength = beamRecord.getDataLength();
		Assert.assertEquals( actualDataLength, expectedDataLength );
		
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DisOutputStream dos = new DisOutputStream( bytesOut );
		beamRecord.to( dos );
		
		byte[] dataRaw = bytesOut.toByteArray();
		try( DisInputStream dis = new DisInputStream(dataRaw) )
		{
			// We want to ensure that the data length is written out correctly
			int deserializedDataLength = dis.readUI8();
			Assert.assertEquals( deserializedDataLength, expectedDataLength );

			// And ensure that reading the whole structure back in produces the same result
			dis.reset();
			EmitterBeam deserialized = new EmitterBeam();
			deserialized.from( dis );

			// Ensure result is equal at the field level
			Assert.assertEquals( deserialized.isBeamActive(), true );
			Assert.assertEquals( deserialized.getBeamData(), beamData );
			Assert.assertEquals( deserialized.getBeamFunction(), BeamFunction.Jamming );
			Assert.assertEquals( deserialized.getBeamNumber(), (short)10 );
			Assert.assertEquals( deserialized.getHighDensity(), HighDensityTrackJam.Selected );
			Assert.assertEquals( deserialized.getJammingTechnique(), jammingTechnique );
			Assert.assertEquals( deserialized.getParameterData(), fundamentalData );
			Assert.assertEquals( deserialized.getParameterIndex(), 5 );
			Assert.assertEquals( deserialized.getTargets(), targets );

			// Ensure result is equal via equals()
			Assert.assertEquals( deserialized, beamRecord );
		}
	}
	
	@Test
	public void testEmitterBeamSerializeOversize() throws IOException
	{
		BeamData beamData = new BeamData(0.1f, 0.2f, 0.3f, 0.4f, 0.5f );
		JammingTechnique jammingTechnique = new JammingTechnique( 1, 2, 3, 4 );
		FundamentalParameterData fundamentalData = new FundamentalParameterData( 30000000f, 
		                                                                         5000000f, 
		                                                                         30f, 
		                                                                         2000000f, 
		                                                                         44000f );
		
		// Pump this baby full of targets so that its data length exceeds 255 32-bit words
		Set<TrackJamData> targets = new HashSet<>();
		for( int i = 0 ; i < 128 ; ++i )
		{
			EntityId entity = new EntityId( 1, 1, i+1 );
			TrackJamData jamData = new TrackJamData( entity );
			targets.add( jamData );
		}
		
		EmitterBeam beamRecord = new EmitterBeam();
		beamRecord.setBeamActive( true );
		beamRecord.setBeamData( beamData );
		beamRecord.setBeamFunction( BeamFunction.Jamming );
		beamRecord.setBeamNumber( (short)10 );
		beamRecord.setHighDensity( HighDensityTrackJam.Selected );
		beamRecord.setJammingTechnique( jammingTechnique );
		beamRecord.setParameterData( fundamentalData );
		beamRecord.setParameterIndex( 5 );
		beamRecord.setTargets( targets );
		
		// Test byte length 
		int expectedByteLength = 1076; // 52 fixed + 8*128 targets
		int actualByteLength = beamRecord.getByteLength();
		Assert.assertEquals( actualByteLength, expectedByteLength );
		
		// And also test data length (length in 32-bit words)
		int expectedDataLength = 0; // Would be 269 which exceeds 255, so will be set to 0
		int actualDataLength = beamRecord.getDataLength();
		Assert.assertEquals( actualDataLength, expectedDataLength );
		
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DisOutputStream dos = new DisOutputStream( bytesOut );
		beamRecord.to( dos );
		
		byte[] dataRaw = bytesOut.toByteArray();
		try( DisInputStream dis = new DisInputStream(dataRaw) )
		{
			// We want to ensure that the data length is written out correctly
			int deserializedDataLength = dis.readUI8();
			Assert.assertEquals( deserializedDataLength, expectedDataLength );

			// And ensure that reading the whole structure back in produces the same result
			dis.reset();
			EmitterBeam deserialized = new EmitterBeam();
			deserialized.from( dis );

			// Ensure result is equal at the field level
			Assert.assertEquals( deserialized.isBeamActive(), true );
			Assert.assertEquals( deserialized.getBeamData(), beamData );
			Assert.assertEquals( deserialized.getBeamFunction(), BeamFunction.Jamming );
			Assert.assertEquals( deserialized.getBeamNumber(), (short)10 );
			Assert.assertEquals( deserialized.getHighDensity(), HighDensityTrackJam.Selected );
			Assert.assertEquals( deserialized.getJammingTechnique(), jammingTechnique );
			Assert.assertEquals( deserialized.getParameterData(), fundamentalData );
			Assert.assertEquals( deserialized.getParameterIndex(), 5 );
			Assert.assertEquals( deserialized.getTargets(), targets );

			// Ensure result is equal via equals()
			Assert.assertEquals( deserialized, beamRecord );
		}
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
