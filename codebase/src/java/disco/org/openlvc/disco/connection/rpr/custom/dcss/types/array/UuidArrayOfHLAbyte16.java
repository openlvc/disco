/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.disco.connection.rpr.custom.dcss.types.array;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.openlvc.disco.connection.rpr.types.EncoderFactory;
import org.openlvc.disco.connection.rpr.types.array.WrappedHlaFixedArray;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;

import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.HLAoctet;

public class UuidArrayOfHLAbyte16 extends WrappedHlaFixedArray<HLAoctet>
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
	public UuidArrayOfHLAbyte16()
	{
		super( new UuidArrayOfHLAbyte16.Factory(), 16 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public UUID getDisValue()
	{
		byte[] raw = this.toByteArray();
		ByteBuffer buffer = ByteBuffer.wrap( raw );
		long hi = buffer.getLong();
		long lo = buffer.getLong();
		return new UUID( hi, lo );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	private static class Factory implements DataElementFactory<HLAoctet>
	{
		@Override
		public HLAoctet createElement( int index )
		{
			return EncoderFactory.createHLAoctet();
		}
	}
	
	public static UUID readDisUuid( DisInputStream dis ) throws IOException
	{
		long uuidHi = dis.readLong();
		long uuidLo = dis.readLong();
		return new UUID( uuidHi, uuidLo );
	}
	
	public static void writeDisUuid( UUID uuid, DisOutputStream dos ) throws IOException
	{
		dos.writeLong( uuid.getMostSignificantBits() );
		dos.writeLong( uuid.getLeastSignificantBits() );
	}
	
	public static void toDcssUuid( UUID src, UuidArrayOfHLAbyte16 dst )
	{
		ByteBuffer buffer = ByteBuffer.wrap( new byte[16] );
		buffer.putLong( src.getMostSignificantBits() );
		buffer.putLong( src.getLeastSignificantBits() );
		for( int i = 0 ; i < 16 ; ++i )
			dst.get( i ).setValue( buffer.get(i) );
	}
}
