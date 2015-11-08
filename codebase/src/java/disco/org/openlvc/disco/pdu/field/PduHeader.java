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
package org.openlvc.disco.pdu.field;

public class PduHeader
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private PduVersion version;
	private short exerciseId;
	private PduType pduType;
	private ProtocolFamily family;
	private long timestamp;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PduHeader()
	{
		this( PduVersion.Version6, (short)0, PduType.Other, ProtocolFamily.Other, 0 );
	}
	
	public PduHeader( PduVersion version,
	                  short exerciseId, 
	                  PduType pduType,
	                  ProtocolFamily family,
	                  long timestamp )
	{
		this.version = version;
		this.exerciseId = exerciseId;
		this.pduType = pduType;
		this.family = family;
		this.timestamp = timestamp;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public PduVersion getVersion() { return this.version; }
	public void setVersion( PduVersion version ) { this.version = version; }
	
	public PduType getPDUType() { return this.pduType; }
	public void setPDUType( PduType type ) { this.pduType = type; }
	
	public short getExerciseId() { return this.exerciseId; }
	public void setExerciseId( short id ) { this.exerciseId = id; }
	
	public ProtocolFamily getProtocolFamily() { return this.family; }
	public void setProtocolFamily( ProtocolFamily family ) { this.family = family; }
	
	public long getTimestamp() { return this.timestamp; }
	public void setTimestamp( long timestamp ) { this.timestamp = timestamp; }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
