/*
 *   Copyright 2017 Open LVC Project.
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

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.PduHeader;

public class CommentPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId originatingEntity;
	private EntityId receivingEntity;
	private long fixedRecordCount;
	private long variableRecordCount;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public CommentPdu( PduHeader header )
	{
		super( header );
		
		this.originatingEntity = new EntityId();
		this.receivingEntity = new EntityId();
		this.fixedRecordCount = 0;
		this.variableRecordCount = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Serializer and Deserialize Methods   ///////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void from( DisInputStream dis ) throws IOException
	{
		this.originatingEntity.from( dis );
		this.receivingEntity.from( dis );
		this.fixedRecordCount = dis.readUI32();
		this.variableRecordCount = dis.readUI32();
	}
	
	public void to( DisOutputStream dos ) throws IOException
	{
		originatingEntity.to( dos );
		receivingEntity.to( dos );
		dos.writeUI32( fixedRecordCount );
		dos.writeUI32( variableRecordCount );
	}

	public int getContentLength()
	{
		/*
		int size = originatingEntity.getByteLength();            // 6
		size += receivingEntity.getByteLength();                 // 6
		size += DisSizes.UI32_SIZE; // FixedCount                // 4
		size += DisSizes.UI32_SIZE; // VariableCount             // 4
		size += FIXED & VARIABLE LENGTH
		return size;
		*/
		
		return 20 + /*FIXED & VAR PARAMS*/0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Misc PDU Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getSiteId()
	{
		return originatingEntity.getSiteId();
	}
	
	public int getAppId()
	{
		return originatingEntity.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
