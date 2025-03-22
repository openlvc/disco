/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.connection.rpr.interactions;

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.enumerated.StopFreezeReasonEnum8;
import org.openlvc.disco.connection.rpr.types.fixed.ClockTimeStruct;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.StopFreezeReason;
import org.openlvc.disco.pdu.record.FrozenBehavior;
import org.openlvc.disco.pdu.simman.StopFreezePdu;

public class StopFreeze extends InteractionInstance
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
	private ClockTimeStruct realWorldTime;
	private EnumHolder<StopFreezeReasonEnum8> reason;
	private RPRboolean reflectValues;
	private RPRboolean runInternalSimulationClock;
	private RPRboolean updateAttributes;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public StopFreeze()
	{
		super();
		this.originatingEntity = new EntityIdentifierStruct();
		this.receivingEntity = new EntityIdentifierStruct();
		this.requestIdentifier = new RPRunsignedInteger32BE();
		this.realWorldTime = new ClockTimeStruct();
		this.reason = new EnumHolder<>( StopFreezeReasonEnum8.Other );
		this.reflectValues = new RPRboolean();
		this.runInternalSimulationClock = new RPRboolean();
		this.updateAttributes = new RPRboolean();
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
		StopFreezePdu pdu = incoming.as( StopFreezePdu.class );

		// OriginatingEntity
		this.originatingEntity.setValue( pdu.getOriginatingEntity() );
		
		// ReceivingEntity
		this.receivingEntity.setValue( pdu.getReceivingEntity() );
		
		// RequestIdentifier
		this.requestIdentifier.setValue( pdu.getRequestId() );
		
		// Real World Time
		this.realWorldTime.setDisValue( pdu.getRealWorldTime() );
		
		// Reason
		this.reason.setEnum( StopFreezeReasonEnum8.valueOf( pdu.getReason().value()) );
		
		// Frozen Behavior
		FrozenBehavior frozenBehavior = pdu.getFrozenBehavior();
		this.reflectValues.setValue( frozenBehavior.isProcessUpdates() );
		this.runInternalSimulationClock.setValue( frozenBehavior.isRunSimulationClock() );
		this.updateAttributes.setValue( frozenBehavior.isTransmitUpdates() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		StopFreezePdu pdu = new StopFreezePdu();
		
		// OriginatingEntity
		pdu.setOriginatingEntity( this.originatingEntity.getDisValue() );
		
		// ReceivingEntity
		pdu.setReceivingEntity( this.receivingEntity.getDisValue() );
		
		// RequestIdentifier
		pdu.setRequestId( this.requestIdentifier.getValue() );
		
		// Real World Time
		pdu.setRealWorldTime( this.realWorldTime.getDisValue() );
		
		// Reason
		pdu.setReason( StopFreezeReason.fromValue( (short)this.reason.getEnum().getValue()) );
		
		// Frozen Behavior
		FrozenBehavior behavior = new FrozenBehavior();
		behavior.setProcessUpdates( this.reflectValues.getValue() );
		behavior.setRunSimulationClock( this.runInternalSimulationClock.getValue() );
		behavior.setTransmitUpdates( this.updateAttributes.getValue() );
		pdu.setFrozenBehavior( behavior );
		
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

	public ClockTimeStruct getRealWorldTime()
	{
		return realWorldTime;
	}
	
	public EnumHolder<StopFreezeReasonEnum8> getReason()
	{
		return reason;
	}
	
	public RPRboolean getReflectValues()
	{
		return reflectValues;
	}
	
	public RPRboolean getRunInternalSimulationClock()
	{
		return runInternalSimulationClock;
	}
	
	public RPRboolean getUpdateAttributes()
	{
		return updateAttributes;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
