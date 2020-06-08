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
package org.openlvc.disco.pdu.record;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.ProtocolVersion;
import org.openlvc.disco.pdu.field.ProtocolFamily;

public class PduHeader
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ProtocolVersion version;
	private short exerciseId;
	private PduType pduType;
	private ProtocolFamily family;
	private long timestamp;
	private int pduLength;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PduHeader()
	{
		this.version = ProtocolVersion.Version6;
		this.exerciseId = (short)1;
		this.pduType = PduType.Other;
		this.family = this.pduType.getProtocolFamily();
		this.timestamp = 0;
		
		// PDU Length
		// A PDU header is only created in one of two circumstance:
		//   1. We have received a PDU from an external application and it has filled the header in
		//   2. We have created the PDU locally, and we need to fill the header in
		//
		// In Case 1 - This value shall be populated in the from(DisInputStream) method.
		// In Case 2 - This value shall be populated in the to(DisInputStream,int) method.
		//             This does mean that prior to being serialized, the header will _NOT_
		//             contain a valid PDU length.
		this.pduLength = -1;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public PduHeader from( DisInputStream dis ) throws IOException
	{
		this.version = ProtocolVersion.fromValue( dis.readUI8() );
		this.exerciseId = dis.readUI8();
		this.pduType = PduType.fromValue( dis.readUI8() );
		this.family = ProtocolFamily.fromValue( dis.readUI8() );
		this.timestamp = dis.readUI32();
		this.pduLength = dis.readUI16(); // Length
		dis.readUI16(); // padding bytes
		
		return this; // return ourselves so the method can be chained
	}

	/**
	 * Write the contents of the PDU to the given output stream. We include the content
	 * length of the 
	 * @param dos
	 * @param contentLength
	 * @throws IOException
	 */
	public void to( DisOutputStream dos, int contentLength ) throws IOException
	{
		int totalLength = getHeaderLength() + contentLength;
		
		dos.writeUI8( this.version.value() );
		dos.writeUI8( exerciseId );
		dos.writeUI8( pduType.value() );
		dos.writeUI8( family.value() );
		dos.writeUI32( timestamp );

		dos.writeUI16( totalLength );
		dos.writePadding( 2 );
	}

	public final int getHeaderLength()
	{
		return 12;
	}

	@Override
	public String toString()
	{
		return "("+version+") "+family+"/"+pduType+", ExerciseID="+exerciseId;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ProtocolVersion getVersion() { return this.version; }
	public void setVersion( ProtocolVersion version ) { this.version = version; }
	
	public PduType getPduType() { return this.pduType; }
	public PduHeader setPduType( PduType type )
	{
		this.pduType = type;
		this.family = type.getProtocolFamily();
		return this;
	}
	
	public short getExerciseId() { return this.exerciseId; }
	public void setExerciseId( short id ) { this.exerciseId = id; }
	
	public ProtocolFamily getProtocolFamily() { return this.family; }
	public void setProtocolFamily( ProtocolFamily family ) { this.family = family; }
	
	public long getTimestamp() { return this.timestamp; }
	public void setTimestamp( long timestamp ) { this.timestamp = timestamp; }
	
	/**
	 * @return The length of the PDU, including the header, as defined in the "Length" field of
	 *         the header. <b>WARNING:</b> This will only be set properly on PDUs that are read
	 *         in from a stream, or ones that have 
	 */
	public int getPduLength()
	{
		return this.pduLength;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
