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
package org.openlvc.disco.pdu.simman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.ProtocolFamily;
import org.openlvc.disco.pdu.record.EntityId;

public class DataQueryPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId originatingEntity;
	private EntityId receivingEntity;
	private long requestId;
	private long timeInterval;
	private List<Long> fixedRecordIds;
	private List<Long> variableRecordIds;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DataQueryPdu()
	{
		super( PduType.DataQuery, ProtocolFamily.SimMgmt );
		this.originatingEntity = new EntityId();
		this.receivingEntity = new EntityId();
		this.requestId = 0;
		this.timeInterval = 0;
		this.fixedRecordIds = new ArrayList<>();
		this.variableRecordIds = new ArrayList<>();
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
		this.requestId = dis.readUI32();
		this.timeInterval = dis.readUI32();
		
		int fixedCount = (int)dis.readUI32();
		int variableCount = (int)dis.readUI32();
		
		fixedRecordIds.clear();
		for( int i = 0; i < fixedCount; i++ )
			fixedRecordIds.add( dis.readUI32() );

		variableRecordIds.clear();
		for( int i = 0; i < variableCount; i++ )
			variableRecordIds.add( dis.readUI32() );
	}
	
	public void to( DisOutputStream dos ) throws IOException
	{
		originatingEntity.to( dos );
		receivingEntity.to( dos );
		
		dos.writeUI32( this.requestId );
		dos.writeUI32( this.timeInterval );
		
		// write the number of fixed and variable datums
		dos.writeUI32( this.fixedRecordIds.size() );
		dos.writeUI32( this.variableRecordIds.size() );
		
		// write the actual records
		for( Long id : this.fixedRecordIds )
			dos.writeUI32( id );
		
		for( Long id : this.variableRecordIds )
			dos.writeUI32( id );
	}

	@Override
	public int getContentLength()
	{
		/*
		int size = originatingEntity.getByteLength();            // 6
		size += receivingEntity.getByteLength();                 // 6
		size += DisSizes.UI32_SIZE; // RequestId                 // 4
		size += DisSizes.UI32_SIZE; // Time Interval             // 4
		size += DisSizes.UI32_SIZE; // FixedCount                // 4
		size += DisSizes.UI32_SIZE; // VariableCount             // 4

		size += 64-bit * FIXED COUNT                             // (8*FIXED)
		size += 64-bit * VARIABLE COUNT                          // (8*VARIABLE)
		return size;
		*/
		
		int size = 28 + (8*fixedRecordIds.size()) + (8*variableRecordIds.size());
		
		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Misc PDU Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return originatingEntity.getSiteId();
	}

	@Override
	public int getAppId()
	{
		return originatingEntity.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getRequestId()
	{
		return this.requestId;
	}
	
	public void setRequestId( long requestId )
	{
		this.requestId = requestId;
	}
	
	public long getTimeInterval()
	{
		return this.timeInterval;
	}
	
	public void setTimeInterval( long timeInterval )
	{
		this.timeInterval = timeInterval;
	}

	public EntityId getOriginatingEntity()
	{
		return this.originatingEntity;
	}
	
	public void setOriginatingEntity( EntityId id )
	{
		this.originatingEntity = id;
	}
	
	public EntityId getReceivingEntity()
	{
		return this.receivingEntity;
	}
	
	public void setReceivingEntity( EntityId id )
	{
		this.receivingEntity = id;
	}

	public int getFixedDatumIdCount()
	{
		return this.fixedRecordIds.size();
	}
	
	public int getVariableDatumIdCount()
	{
		return this.variableRecordIds.size();
	}

	public void addFixedRecordId( long fixedRecordId )
	{
		this.fixedRecordIds.add( fixedRecordId );
	}
	
	public void addVariableRecordId( long variableRecordId )
	{
		this.variableRecordIds.add( variableRecordId );
	}

	/**
	 * @return Unmodifiable list of all contained fixed datum records
	 */
	public List<Long> getFixedDatumIds()
	{
		return Collections.unmodifiableList( this.fixedRecordIds );
	}
	
	/**
	 * @return Unmodifiable list of all contained variable datum records
	 */
	public List<Long> getVariableDatumIds()
	{
		return Collections.unmodifiableList( this.variableRecordIds );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
