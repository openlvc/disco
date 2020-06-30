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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.connection.rpr.types.array.DatumIdentifierLengthlessArray;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.simman.DataQueryPdu;

public class DataQuery extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityIdentifierStruct originatingEntity;
	private EntityIdentifierStruct receivingEntity;
	private RPRunsignedInteger32BE requestIdentifier;
	private RPRunsignedInteger32BE timeInterval;
	private DatumIdentifierLengthlessArray fixedDatumIdentifiers;
	private DatumIdentifierLengthlessArray variableDatumIdentifiers;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DataQuery()
	{
		super();
		this.originatingEntity = new EntityIdentifierStruct();
		this.receivingEntity   = new EntityIdentifierStruct();
		this.requestIdentifier = new RPRunsignedInteger32BE();
		this.timeInterval      = new RPRunsignedInteger32BE();
		this.fixedDatumIdentifiers = new DatumIdentifierLengthlessArray();
		this.variableDatumIdentifiers = new DatumIdentifierLengthlessArray();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		DataQueryPdu pdu = incoming.as( DataQueryPdu.class );

		// OriginatingEntity
		originatingEntity.setValue( pdu.getOriginatingEntity() );

		// ReceivingEntity
		receivingEntity.setValue( pdu.getReceivingEntity() );
		
		// RequestIdentifier
		requestIdentifier.setValue( pdu.getRequestId() );
		
		// TimeInterval
		timeInterval.setValue( pdu.getTimeInterval() );
		
		// FixedDatums
		fixedDatumIdentifiers.clear();
		for( Long fixed : pdu.getFixedDatumIds() )
			fixedDatumIdentifiers.add( new RPRunsignedInteger32BE(fixed) );

		// VariableDatumSet
		variableDatumIdentifiers.clear();
		for( Long variable : pdu.getVariableDatumIds() )
			variableDatumIdentifiers.add( new RPRunsignedInteger32BE(variable) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		DataQueryPdu pdu = new DataQueryPdu();
		
		// OriginatingEntity
		pdu.setOriginatingEntity( originatingEntity.getDisValue() );
		
		// ReceivingEntity
		pdu.setReceivingEntity( receivingEntity.getDisValue() );
		
		// RequestIdentifier
		pdu.setRequestId( requestIdentifier.getValue() );
		
		// TimeInterval
		pdu.setTimeInterval( timeInterval.getValue() );
		
		// FixedDatums
		for( RPRunsignedInteger32BE value : fixedDatumIdentifiers )
			pdu.addFixedRecordId( value.getValue() );
		
		// VariableDatumSet
		for( RPRunsignedInteger32BE value : variableDatumIdentifiers )
			pdu.addVariableRecordId( value.getValue() );
		
		return pdu;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityIdentifierStruct getOriginatingEntity()
	{
		return originatingEntity;
	}

	public EntityIdentifierStruct getReceivingEntity()
	{
		return receivingEntity;
	}

	public RPRunsignedInteger32BE getRequestIdentifier()
	{
		return requestIdentifier;
	}

	public RPRunsignedInteger32BE getTimeInterval()
	{
		return timeInterval;
	}

	public DatumIdentifierLengthlessArray getFixedDatumIdentifiers()
	{
		return fixedDatumIdentifiers;
	}

	public DatumIdentifierLengthlessArray getVariableDatumIdentifiers()
	{
		return variableDatumIdentifiers;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
