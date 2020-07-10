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

import org.openlvc.disco.connection.rpr.types.array.FixedDatumStructLengthlessArray;
import org.openlvc.disco.connection.rpr.types.array.VariableDatumStructArray;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.RawEnumValue32;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.connection.rpr.types.fixed.FixedDatumStruct;
import org.openlvc.disco.connection.rpr.types.fixed.VariableDatumStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.ActionId;
import org.openlvc.disco.pdu.record.FixedDatum;
import org.openlvc.disco.pdu.record.VariableDatum;
import org.openlvc.disco.pdu.simman.ActionRequestPdu;

public class ActionRequest extends InteractionInstance
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
	private RawEnumValue32 actionRequestCode;
	private FixedDatumStructLengthlessArray fixedDatums;
	private VariableDatumStructArray variableDatumSet;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ActionRequest()
	{
		super();
		this.originatingEntity = new EntityIdentifierStruct();
		this.receivingEntity = new EntityIdentifierStruct();
		this.requestIdentifier = new RPRunsignedInteger32BE();
		this.actionRequestCode = new RawEnumValue32();
		this.fixedDatums = new FixedDatumStructLengthlessArray();
		this.variableDatumSet = new VariableDatumStructArray();
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
		ActionRequestPdu pdu = incoming.as( ActionRequestPdu.class );

		// OriginatingEntity
		this.originatingEntity.setValue( pdu.getOriginatingEntity() );
		
		// ReceivingEntity
		this.receivingEntity.setValue( pdu.getReceivingEntity() );
		
		// RequestIdentifier
		this.requestIdentifier.setValue( pdu.getRequestId() );
		
		// ActionRequestCode
		this.actionRequestCode.setValue( pdu.getActionId().value() );
		
		// FixedDatums
		fixedDatums.clear();
		for( FixedDatum fd : pdu.getFixedDatumRecords() )
			fixedDatums.add( new FixedDatumStruct(fd.getDatumId(),fd.getDatumValue()) );

		// VariableDatumSet
		variableDatumSet.resize( pdu.getVariableDatumCount() );
		for( VariableDatum vd : pdu.getVariableDatumRecords() )
			variableDatumSet.addElement( new VariableDatumStruct(vd) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		ActionRequestPdu pdu = new ActionRequestPdu();
		
		// OriginatingEntity
		pdu.setOriginatingEntity( this.originatingEntity.getDisValue() );
		
		// ReceivingEntity
		pdu.setReceivingEntity( this.receivingEntity.getDisValue() );
		
		// RequestIdentifier
		pdu.setRequestId( this.requestIdentifier.getValue() );
		
		// ActionRequestCode
		pdu.setActionId( ActionId.fromValue(this.actionRequestCode.getValue()) );

		// FixedDatums
		for( FixedDatumStruct struct : fixedDatums )
			pdu.add( struct.getDisValue() );
		
		// VariableDatumSet
		for( VariableDatumStruct struct : variableDatumSet )
			pdu.add( struct.getDisValue() );
		
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

	public RawEnumValue32 getActionRequestCode()
	{
		return actionRequestCode;
	}

	public FixedDatumStructLengthlessArray getFixedDatums()
	{
		return fixedDatums;
	}

	public VariableDatumStructArray getVariableDatumSet()
	{
		return variableDatumSet;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
