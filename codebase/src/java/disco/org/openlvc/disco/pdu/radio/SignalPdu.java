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
package org.openlvc.disco.pdu.radio;

import java.io.IOException;
import java.util.Arrays;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.TdlType;
import org.openlvc.disco.pdu.record.EncodingScheme;
import org.openlvc.disco.pdu.record.EntityId;

/**
 * This class represents a Receiver PDU.
 * <p/>
 * PDUs of this type contain information about...
 * 
 * @see "IEEE Std 1278.1-1995 section 4.5.7.4"
 */
public class SignalPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId entityID;
	private int radioID;
	private EncodingScheme encodingScheme;
	private TdlType tdlType;
	private long sampleRate;
	private int dataLength;
	private int samples;
	private byte[] data;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SignalPdu()
	{
		super( PduType.Signal );

		this.entityID = new EntityId();
		this.radioID = 0;
		this.encodingScheme = new EncodingScheme();
		this.tdlType = TdlType.Other;
		this.sampleRate = 0;
		this.samples = 0;
		setData( new byte[0] );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		entityID.from( dis );
		radioID = dis.readUI16();
		encodingScheme.from( dis );
		tdlType = TdlType.fromValue( dis.readUI16() );
		sampleRate = dis.readUI32();
		int dataLength = dis.readUI16();
		samples = dis.readUI16();
		
		boolean lengthAligned = dataLength % 8 == 0;
		int lengthBytes = dataLength / 8;
		if( !lengthAligned )
			++lengthBytes;
		
		byte[] data = new byte[lengthBytes];
		dis.readFully( data );
		
		setData( dataLength, data );
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		entityID.to( dos );
		dos.writeUI16( radioID );
		encodingScheme.to( dos );
		dos.writeUI16( tdlType.value() );
		dos.writeUI32( sampleRate );
		dos.writeUI16( dataLength );
		dos.writeUI16( samples );
		dos.write( data );
	}
	
	@Override
	public final int getContentLength()
	{
		return 20 + data.length;
		
		// int size = entityID.getByteLength();              // 6
		// size += DisSizes.UI16_SIZE;	// Radio ID          // 2

		// size += encodingScheme.getByteLength();           // 2
		// size += TdlType.getByteLength();                  // 2
		// size += DisSizes.UI32_SIZE;		// Sample Rate   // 4
		// size += DisSizes.UI16_SIZE;		// Data Length   // 2
		// size += DisSizes.UI16_SIZE;		// Samples       // 2
		// size += data.length;                              // Variable
		// return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getEntityIdentifier()
	{
		return entityID;
	}
	
	public EntityId getEntityId()
	{
		return entityID;
	}
	
	public void setEntityIdentifier( EntityId id )
	{
		entityID = id;
	}
	
	public void setEntityId( EntityId id )
	{
		this.entityID = id;
	}
	
	public int getRadioID()
	{
		return radioID;
	}
	
	public void setRadioID( int radioID )
	{
		this.radioID = radioID;
	}

	public EncodingScheme getEncodingScheme()
	{
		return encodingScheme;
	}
	
	public void setEncodingScheme( EncodingScheme encodingScheme )
	{
		this.encodingScheme = encodingScheme;
	}
	
	public TdlType getTDLType()
	{
		return tdlType;
	}
	
	public void setTDLType( TdlType tdlType )
	{
		this.tdlType = tdlType;
	}
	
	public long getSampleRate()
	{
		return sampleRate;
	}
	
	public void setSampleRate( long sampleRate )
	{
		this.sampleRate = sampleRate;
	}
	
	public int getDataLength()
	{
		return dataLength;
	}
	
	public int getSamples()
	{
		return samples;
	}
	
	public void setSamples( int samples )
	{
		this.samples = samples;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public void setData( byte[] data )
	{
		int dataLength = data.length * 8;
		setData( dataLength, data );
	}
		
	public void setData( int dataLength, byte[] data )
	{
		// Ensure the length of the data section is aligned as per the spec
		boolean lengthAligned = dataLength % 8 == 0;
		int requiredBytes = dataLength / 8;
		if( !lengthAligned )
			++requiredBytes;
		
		if( data.length != requiredBytes )
			throw new IllegalArgumentException( "Data size mismatch" );
		
		this.dataLength = dataLength;
		
		// Important to take a copy of the data here, the caller may have passed in their working buffer
		// which can subsequently be modified in the time that we're waiting for the PDU to be sent
		this.data = Arrays.copyOf( data, data.length );
	}
	
	@Override
	public int getSiteId()
	{
		return entityID.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return entityID.getAppId();
	}

	
	@Override
	public boolean equals( Object other )
	{
		return false;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
