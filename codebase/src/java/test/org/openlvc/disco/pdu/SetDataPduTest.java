/*
 *   Copyright 2024 Open LVC Project.
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
import java.util.Random;

import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.VariableDatum;
import org.openlvc.disco.pdu.simman.SetDataPdu;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SetDataPduTest
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private PduFactory factory;
	private Random random;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	///////////////////////////////////////////////////////////////////////////////////
	/// Test Class Setup/Tear Down   //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod()
	{
		this.factory = new PduFactory();
		this.random = new Random();
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
		this.factory = null;
		this.random = null;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   //////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	private SetDataPdu quickCreateSetDataPdu()
	{
		EntityId senderId = new EntityId( 1, 2, 3 );
		EntityId receiverId = new EntityId( 4, 5, 6 );
		SetDataPdu prototype = new SetDataPdu();
		prototype.setRequestId( this.random.nextInt(Short.MAX_VALUE) );
		prototype.setExerciseId( (short)1 );
		prototype.setOriginatingEntity( senderId );
		prototype.setReceivingEntity( receiverId );
		
		return prototype;
	}
	
	private VariableDatum quickCreateVdr( long id, int length )
	{
		byte[] value = new byte[length];
		this.random.nextBytes( value );
		
		return new VariableDatum( id, value );
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testDeserializeSingleVdrAlignedValue() throws Exception
	{
		//   12 bytes Pdu Header
		// + 28 bytes SetDataPdu Body 
		// +  8 bytes VDR Header 
		// +  8 bytes VDR Content
		// -----------
		//   56 bytes
		final int EXPECTED_BINARY_LENGTH = 56; 
		
		// Construct Data PDU with single VDR (value aligned to 64-bit boundary)
		SetDataPdu prototype = quickCreateSetDataPdu();
		VariableDatum protoVdr1 = quickCreateVdr( 1, 8 );
		
		// Datum Padding should be valid
		Assert.assertEquals( protoVdr1.getDatumLengthInBytes(), 8 );
		Assert.assertEquals( protoVdr1.getPaddingLengthInBytes(), 0 );
		
		prototype.add( protoVdr1 );
		
		// Write to binary blob
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		DisOutputStream outDisStream = new DisOutputStream( outByteStream );
		prototype.getHeader().to( outDisStream, prototype.getContentLength() );
		prototype.to( outDisStream );
		byte[] serializedBlob = outByteStream.toByteArray();
		
		// Size should be what we expect
		Assert.assertEquals( serializedBlob.length, EXPECTED_BINARY_LENGTH );
		
		// Deserialize from binary
		PDU deserializedGeneric = factory.create( serializedBlob );
		Assert.assertTrue( deserializedGeneric instanceof SetDataPdu );
		SetDataPdu deserialized = (SetDataPdu)deserializedGeneric;
		
		// Make sure deserialized SetDataPdu is the same
		Assert.assertEquals( deserialized.getRequestId(), prototype.getRequestId() );
		Assert.assertEquals( deserialized.getOriginatingEntity(), 
		                     prototype.getOriginatingEntity() );
		Assert.assertEquals( deserialized.getReceivingEntity(), prototype.getReceivingEntity() );
		Assert.assertEquals( deserialized.getVariableDatumCount(), 
		                     prototype.getVariableDatumCount() );
		
		// Make sure deserialized VDR is the same
		VariableDatum deserializedVdr1 = deserialized.getVariableDatumRecords().get( 0 );
		Assert.assertEquals( deserializedVdr1.getDatumId(), protoVdr1.getDatumId() );
		Assert.assertEquals( deserializedVdr1.getDatumValue(), protoVdr1.getDatumValue() );
		Assert.assertEquals( deserializedVdr1.getDatumLengthInBytes(), 8 );
		Assert.assertEquals( deserializedVdr1.getPaddingLengthInBytes(), 0 );
	}
	
	@Test
	public void testDeserializeSingleVdrUnalignedValue() throws Exception
	{
		//   12 bytes Pdu Header
		// + 28 bytes SetDataPdu Body 
		// +  8 bytes VDR Header 
		// +  6 bytes VDR Content
		// +  2 bytes VDR Padding
		// -----------
		//   56 bytes
		final int EXPECTED_BINARY_LENGTH = 56; 
		
		// Construct Data PDU with single VDR (value aligned to 64-bit boundary)
		SetDataPdu prototype = quickCreateSetDataPdu();
		VariableDatum protoVdr1 = quickCreateVdr( 1, 6 );
		
		Assert.assertEquals( protoVdr1.getDatumLengthInBytes(), 6 );
		Assert.assertEquals( protoVdr1.getPaddingLengthInBytes(), 2 );
		
		prototype.add( protoVdr1 );
		
		// Write to binary blob
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		DisOutputStream outDisStream = new DisOutputStream( outByteStream );
		prototype.getHeader().to( outDisStream, prototype.getContentLength() );
		prototype.to( outDisStream );
		byte[] serializedBlob = outByteStream.toByteArray();
		
		// Size should be what we expect
		Assert.assertEquals( serializedBlob.length, EXPECTED_BINARY_LENGTH );
		
		// Deserialize from binary
		PDU deserializedGeneric = factory.create( serializedBlob );
		Assert.assertTrue( deserializedGeneric instanceof SetDataPdu );
		SetDataPdu deserialized = (SetDataPdu)deserializedGeneric;
		
		// Make sure deserialized SetDataPdu is the same
		Assert.assertEquals( deserialized.getRequestId(), prototype.getRequestId() );
		Assert.assertEquals( deserialized.getOriginatingEntity(), 
		                     prototype.getOriginatingEntity() );
		Assert.assertEquals( deserialized.getReceivingEntity(), prototype.getReceivingEntity() );
		Assert.assertEquals( deserialized.getVariableDatumCount(), 
		                     prototype.getVariableDatumCount() );
		
		// Make sure deserialized VDR is the same
		VariableDatum deserializedVdr1 = deserialized.getVariableDatumRecords().get( 0 );
		Assert.assertEquals( deserializedVdr1.getDatumId(), protoVdr1.getDatumId() );
		Assert.assertEquals( deserializedVdr1.getDatumValue(), protoVdr1.getDatumValue() );
		Assert.assertEquals( deserializedVdr1.getDatumLengthInBytes(), 6 );
		Assert.assertEquals( deserializedVdr1.getPaddingLengthInBytes(), 2 );
	}
	
	public void testDeserializeMultipleVdr() throws Exception
	{
		//   12 bytes Pdu Header
		// + 28 bytes SetDataPdu Body 
		// +  8 bytes VDR1 Header 
		// +  8 bytes VDR1 Content
		// +  8 bytes VDR2 Header
		// +  6 bytes VDR2 Content
		// +  2 bytes VDR2 Padding
		// -----------
		//   72 bytes
		final int EXPECTED_BINARY_LENGTH = 72; 
		
		// Construct Data PDU with multiple VDRs
		SetDataPdu prototype = quickCreateSetDataPdu();
		VariableDatum protoVdr1 = quickCreateVdr( 1, 8 );
		VariableDatum protoVdr2 = quickCreateVdr( 2, 6 );
		
		Assert.assertEquals( protoVdr1.getDatumLengthInBytes(), 8 );
		Assert.assertEquals( protoVdr1.getPaddingLengthInBytes(), 0 );
		Assert.assertEquals( protoVdr2.getDatumLengthInBytes(), 6 );
		Assert.assertEquals( protoVdr2.getPaddingLengthInBytes(), 2 );
		
		prototype.add( protoVdr1 );
		prototype.add( protoVdr2 );
		
		// Write to binary blob
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		DisOutputStream outDisStream = new DisOutputStream( outByteStream );
		prototype.getHeader().to( outDisStream, prototype.getContentLength() );
		prototype.to( outDisStream );
		byte[] serializedBlob = outByteStream.toByteArray();
		
		// Size should be what we expect
		Assert.assertEquals( serializedBlob.length, EXPECTED_BINARY_LENGTH );
		
		// Deserialize from binary
		PDU deserializedGeneric = factory.create( serializedBlob );
		Assert.assertTrue( deserializedGeneric instanceof SetDataPdu );
		SetDataPdu deserialized = (SetDataPdu)deserializedGeneric;

		// Make sure deserialized SetDataPdu is the same
		Assert.assertEquals( deserialized.getRequestId(), prototype.getRequestId() );
		Assert.assertEquals( deserialized.getOriginatingEntity(), 
		                     prototype.getOriginatingEntity() );
		Assert.assertEquals( deserialized.getReceivingEntity(), prototype.getReceivingEntity() );
		Assert.assertEquals( deserialized.getVariableDatumCount(), 
		                     prototype.getVariableDatumCount() );
		
		// Make sure deserialized VDR1 is the same
		VariableDatum deserializedVdr1 = deserialized.getVariableDatumRecords().get( 0 );
		Assert.assertEquals( deserializedVdr1.getDatumId(), protoVdr1.getDatumId() );
		Assert.assertEquals( deserializedVdr1.getDatumValue(), protoVdr1.getDatumValue() );
		Assert.assertEquals( deserializedVdr1.getDatumLengthInBytes(), 8 );
		Assert.assertEquals( deserializedVdr1.getPaddingLengthInBytes(), 0 );
		
		// Make sure deserialized VDR2 is the same
		VariableDatum deserializedVdr2 = deserialized.getVariableDatumRecords().get( 1 );
		Assert.assertEquals( deserializedVdr2.getDatumId(), protoVdr2.getDatumId() );
		Assert.assertEquals( deserializedVdr2.getDatumValue(), protoVdr2.getDatumValue() );
		Assert.assertEquals( deserializedVdr2.getDatumLengthInBytes(), 6 );
		Assert.assertEquals( deserializedVdr2.getPaddingLengthInBytes(), 2 );
		
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
