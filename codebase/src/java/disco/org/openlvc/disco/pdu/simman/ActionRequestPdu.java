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
import org.openlvc.disco.pdu.field.ActionId;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.FixedDatum;
import org.openlvc.disco.pdu.record.VariableDatum;

public class ActionRequestPdu extends PDU
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
	private ActionId actionId;
	private List<FixedDatum> fixedRecords;
	private List<VariableDatum> variableRecords;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ActionRequestPdu()
	{
		super( PduType.ActionRequest );
		this.requestId = 0;
		this.actionId = ActionId.Other;
		this.originatingEntity = new EntityId();
		this.receivingEntity = new EntityId();
		this.fixedRecords = new ArrayList<>();
		this.variableRecords = new ArrayList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void from( DisInputStream dis ) throws IOException
	{
		this.originatingEntity.from( dis );
		this.receivingEntity.from( dis );
		this.requestId = dis.readUI32();
		this.actionId = ActionId.fromValue( dis.readUI32() );
		
		int fixedCount = (int)dis.readUI32();
		int variableCount = (int)dis.readUI32();
		
		fixedRecords.clear();
		for( int i = 0; i < fixedCount; i++ )
		{
			FixedDatum record = new FixedDatum();
			record.from( dis );
			fixedRecords.add( record );
		}

		variableRecords.clear();
		for( int i = 0; i < variableCount; i++ )
		{
			VariableDatum record = new VariableDatum();
			record.from( dis );
			variableRecords.add( record );
		}
	}
	
	public void to( DisOutputStream dos ) throws IOException
	{
		originatingEntity.to( dos );
		receivingEntity.to( dos );
		
		dos.writeUI32( this.requestId );
		dos.writeUI32( this.actionId.value() );
		
		// write the number of fixed and variable datums
		dos.writeUI32( this.fixedRecords.size() );
		dos.writeUI32( this.variableRecords.size() );
		
		// write the actual records
		for( FixedDatum record : this.fixedRecords )
			record.to( dos );
		
		for( VariableDatum record : this.variableRecords )
			record.to( dos );
	}

	@Override
	public int getContentLength()
	{
		/*
		int size = originatingEntity.getByteLength();            // 6
		size += receivingEntity.getByteLength();                 // 6
		size += DisSizes.UI32_SIZE; // RequestId                 // 4
		size += DisSizes.UI32_SIZE; // ActionId                  // 4
		size += DisSizes.UI32_SIZE; // FixedCount                // 4
		size += DisSizes.UI32_SIZE; // VariableCount             // 4

		size += 64-bit * FIXED COUNT                             // (8*FIXED)
		size += forEach( VARIABLE.byteLength )                   // NFI
		return size;
		*/
		
		int size = 28 + (8*fixedRecords.size());
		for( VariableDatum record : variableRecords )
			size += record.getByteLength();
		
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
	
	public ActionId getActionId()
	{
		return this.actionId;
	}
	
	public void setActionId( ActionId actionId )
	{
		this.actionId = actionId;
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

	public int getFixedDatumCount()
	{
		return this.fixedRecords.size();
	}
	
	public int getVariableDatumCount()
	{
		return this.variableRecords.size();
	}

	public void add( FixedDatum fixedRecord )
	{
		if( fixedRecord != null )
			fixedRecords.add( fixedRecord );
	}
	
	public void add( VariableDatum variableRecord )
	{
		if( variableRecord != null )
			variableRecords.add( variableRecord );
	}

	/**
	 * @return Remove the datum at the given index and return, or null if there wasn't one there
	 * @throws ArrayIndexOutOfBoundsException if the index is beyond stored bounds for the list
	 */
	public FixedDatum removeFixedDatum( int index ) throws ArrayIndexOutOfBoundsException
	{
		return fixedRecords.remove( index );
	}

	/**
	 * @return Remove the datum at the given index and return, or null if there wasn't one there
	 * @throws ArrayIndexOutOfBoundsException if the index is beyond stored bounds for the list
	 */
	public VariableDatum removeVariableDatum( int index ) throws ArrayIndexOutOfBoundsException
	{
		return variableRecords.remove( index );
	}

	/**
	 * @return Unmodifiable list of all contained fixed datum records
	 */
	public List<FixedDatum> getFixedDatumRecords()
	{
		return Collections.unmodifiableList( this.fixedRecords );
	}
	
	/**
	 * @return Unmodifiable list of all contained variable datum records
	 */
	public List<VariableDatum> getVariableDatumRecords()
	{
		return Collections.unmodifiableList( this.variableRecords );
	}

	/**
	 * Get the contained {@link FixedDatum} record that has the given Datum ID and return it.
	 * If there is one, it will be returned. If there isn't one, <code>null</code> will be
	 * returned.
	 * 
	 * @param datumId The ID of the datum to return
	 * @return The FixedDatum with the given ID, or null if there is none
	 */
	public FixedDatum getFixedDatum( long datumId )
	{
		for( FixedDatum datum : this.fixedRecords )
			if( datum.getDatumId() == datumId )
				return datum;

		return null;
	}
	
	/**
	 * Get the contained {@link VariableDatum} record that has the given Datum ID and return it.
	 * If there is one, it will be returned. If there isn't one, <code>null</code> will be
	 * returned.
	 * 
	 * @param datumId The ID of the datum to return
	 * @return The VariableDatum with the given ID, or null if there is none
	 */
	public VariableDatum getVariableDatum( long datumId )
	{
		for( VariableDatum datum : this.variableRecords )
			if( datum.getDatumId() == datumId )
				return datum;

		return null;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
