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
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
import org.openlvc.disco.pdu.emissions.EmitterSystem;
import org.openlvc.disco.pdu.field.BeamFunction;
import org.openlvc.disco.pdu.field.EmitterSystemFunction;
import org.openlvc.disco.pdu.field.HighDensityTrackJam;
import org.openlvc.disco.pdu.record.BeamData;
import org.openlvc.disco.pdu.record.EmitterSystemType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.FundamentalParameterData;
import org.openlvc.disco.pdu.record.JammingTechnique;
import org.openlvc.disco.pdu.record.TrackJamData;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"record","EmitterSystem"})
public class EmitterSystemRecordTest extends AbstractTest
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
	private EmitterBeam quickCreateBeam( int emitterNumber, int beamNumber, int targetCount )
	{
		final float FORIGIN = 20000;
		final float FBOOUND = 40000;
		final int UBYTEORIGIN = 128;
		final int UBYTEBOUND = 255;
		RandomGenerator gen = RandomGeneratorFactory.getDefault().create();
		
		
		BeamData beamData = new BeamData( gen.nextFloat(FORIGIN, FBOOUND), 
		                                  gen.nextFloat(FORIGIN, FBOOUND), 
		                                  gen.nextFloat(FORIGIN, FBOOUND), 
		                                  gen.nextFloat(FORIGIN, FBOOUND), 
		                                  gen.nextFloat(FORIGIN, FBOOUND) );
		JammingTechnique jammingTechnique = new JammingTechnique( gen.nextInt(UBYTEORIGIN, UBYTEBOUND), 
		                                                          gen.nextInt(UBYTEORIGIN, UBYTEBOUND), 
		                                                          gen.nextInt(UBYTEORIGIN, UBYTEBOUND), 
		                                                          gen.nextInt(UBYTEORIGIN, UBYTEBOUND) );
		FundamentalParameterData fundamentalData = new FundamentalParameterData( gen.nextFloat(FORIGIN, FBOOUND), 
		                                                                         gen.nextFloat(FORIGIN, FBOOUND), 
		                                                                         gen.nextFloat(FORIGIN, FBOOUND), 
		                                                                         gen.nextFloat(FORIGIN, FBOOUND), 
		                                                                         gen.nextFloat(FORIGIN, FBOOUND) ); 
		
		Set<TrackJamData> targets = new HashSet<>(); 
		for( int i = 0 ; i < targetCount ; ++i )
		{
			EntityId targetId = new EntityId( gen.nextInt(UBYTEORIGIN, UBYTEBOUND), 
			                                  gen.nextInt(UBYTEORIGIN, UBYTEBOUND), 
			                                  gen.nextInt(UBYTEORIGIN, UBYTEBOUND) );
			
			TrackJamData target = new TrackJamData( targetId, 
			                                        (short)emitterNumber, 
			                                        (short)beamNumber );
			targets.add( target );
		}
		
		EmitterBeam beamRecord = new EmitterBeam();
		beamRecord.setBeamActive( true );
		beamRecord.setBeamData( beamData );
		beamRecord.setBeamFunction( BeamFunction.Jamming );
		beamRecord.setBeamNumber( (short)beamNumber );
		beamRecord.setHighDensity( HighDensityTrackJam.Selected );
		beamRecord.setJammingTechnique( jammingTechnique );
		beamRecord.setParameterData( fundamentalData );
		beamRecord.setParameterIndex( 0 );
		beamRecord.setTargets( targets );
		
		return beamRecord;
	}

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
	public void testEmitterSystemSerialize() throws IOException
	{
		short emitterNumber = 2;
		EmitterSystemType systemType = new EmitterSystemType( 1, 
		                                                      EmitterSystemFunction.Jammer, 
		                                                      emitterNumber );
		
		VectorRecord location = new VectorRecord( 3f, 4f, 5f );
		
		EmitterSystem systemRecord = new EmitterSystem();
		systemRecord.setSystemType( systemType );
		systemRecord.setLocation( location );
		
		EmitterBeam beam = quickCreateBeam( emitterNumber, 1, 1 );
		systemRecord.addBeam( beam );
		
		// Test byte and data lengths
		int expectedByteLength = 20 + 60; // 20 fixed portion + 60 for beam with single target
		int actualByteLength = systemRecord.getByteLength();
		Assert.assertEquals( actualByteLength, expectedByteLength );
		
		int expectedDataLength = 20; // byteLength / 4 (no of 32-bit words)
		int actualDataLength = systemRecord.getDataLength();
		Assert.assertEquals( actualDataLength, expectedDataLength );
		
		// Serialize record
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DisOutputStream dos = new DisOutputStream( bytesOut );
		systemRecord.to( dos );
		
		byte[] dataRaw = bytesOut.toByteArray();
		try( DisInputStream dis = new DisInputStream(dataRaw) )
		{
			// We want to ensure that the data length is written out correctly
			int deserializedDataLength = dis.readUI8();
			Assert.assertEquals( deserializedDataLength, expectedDataLength );
			
			// And ensure that reading the whole structure back in produces the same result
			dis.reset();
			EmitterSystem deserialized = new EmitterSystem();
			deserialized.from( dis );
			
			// Ensure result is equal at the field level
			Assert.assertEquals( deserialized.getSystemType(), systemRecord.getSystemType() );
			Assert.assertEquals( deserialized.getLocation(), systemRecord.getLocation() );
			Assert.assertEquals( deserialized.getBeams(), systemRecord.getBeams() );
			
			// Ensure result is equal by equals()
			Assert.assertEquals( deserialized, systemRecord );
		}
	}
	
	@Test
	public void testEmitterSystemSerializeOversize() throws IOException
	{
		short emitterNumber = 2;
		EmitterSystemType systemType = new EmitterSystemType( 1, 
		                                                      EmitterSystemFunction.Jammer, 
		                                                      emitterNumber );
		
		VectorRecord location = new VectorRecord( 3f, 4f, 5f );
		
		EmitterSystem systemRecord = new EmitterSystem();
		systemRecord.setSystemType( systemType );
		systemRecord.setLocation( location );
		
		// Pump this baby full of beams so that its data length exceeds 255 32-bit words
		for( int i = 0 ; i < 18 ; ++i )
		{
			EmitterBeam beam = quickCreateBeam( emitterNumber, i+1, 1 );
			systemRecord.addBeam( beam );
		}
		
		// Test byte and data lengths
		int expectedByteLength = 1100; // 20 fixed portion + (18 * 60) for 18 beams with single target
		int actualByteLength = systemRecord.getByteLength();
		Assert.assertEquals( actualByteLength, expectedByteLength );
		
		int expectedDataLength = 0; // Would be 275, which is greater than 255, so set to 0
		int actualDataLength = systemRecord.getDataLength();
		Assert.assertEquals( actualDataLength, expectedDataLength );
		
		// Serialize record
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DisOutputStream dos = new DisOutputStream( bytesOut );
		systemRecord.to( dos );
		
		byte[] dataRaw = bytesOut.toByteArray();
		try( DisInputStream dis = new DisInputStream(dataRaw) )
		{
			// We want to ensure that the data length is written out correctly
			int deserializedDataLength = dis.readUI8();
			Assert.assertEquals( deserializedDataLength, expectedDataLength );
			
			// And ensure that reading the whole structure back in produces the same result
			dis.reset();
			EmitterSystem deserialized = new EmitterSystem();
			deserialized.from( dis );
			
			// Ensure result is equal at the field level
			Assert.assertEquals( deserialized.getSystemType(), systemRecord.getSystemType() );
			Assert.assertEquals( deserialized.getLocation(), systemRecord.getLocation() );
			Assert.assertEquals( deserialized.getBeams(), systemRecord.getBeams() );
			
			// Ensure result is equal by equals()
			Assert.assertEquals( deserialized, systemRecord );
		}
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
