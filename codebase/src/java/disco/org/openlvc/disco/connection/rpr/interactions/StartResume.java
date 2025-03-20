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
import org.openlvc.disco.connection.rpr.types.fixed.ClockTimeStruct;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.simman.StartResumePdu;

public class StartResume extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityIdentifierStruct originatingEntity;
	private EntityIdentifierStruct receivingEntity;
	private ClockTimeStruct realWorldTime;
	private RPRunsignedInteger32BE requestIdentifier;
	private ClockTimeStruct simulationTime;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public StartResume()
	{
		super();
		this.originatingEntity = new EntityIdentifierStruct();
		this.receivingEntity = new EntityIdentifierStruct();
		this.realWorldTime = new ClockTimeStruct();
		this.requestIdentifier = new RPRunsignedInteger32BE();
		this.simulationTime = new ClockTimeStruct();
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
		StartResumePdu pdu = incoming.as( StartResumePdu.class );

		// OriginatingEntity
		this.originatingEntity.setValue( pdu.getOriginatingEntity() );
		
		// ReceivingEntity
		this.receivingEntity.setValue( pdu.getReceivingEntity() );
		
		// RealWorldTime
		this.realWorldTime.setDisValue( pdu.getRealWorldTime() );
		
		// RequestIdentifier
		this.requestIdentifier.setValue( pdu.getRequestId() );
		
		// SimulationTime
		this.simulationTime.setDisValue( pdu.getSimulationTime() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		StartResumePdu pdu = new StartResumePdu();
		
		// OriginatingEntity
		pdu.setOriginatingEntity( this.originatingEntity.getDisValue() );
		
		// ReceivingEntity
		pdu.setReceivingEntity( this.receivingEntity.getDisValue() );
		
		// RealWorldTime
		pdu.setRealWorldTime( this.realWorldTime.getDisValue() );
		
		// RequestIdentifier
		pdu.setRequestId( this.requestIdentifier.getValue() );
		
		// SimulationTime
		pdu.setSimulationTime( this.simulationTime.getDisValue() );
		
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

	public ClockTimeStruct getRealWorldTime()
	{
		return realWorldTime;
	}
	
	public RPRunsignedInteger32BE getRequestIdentifier()
	{
		return requestIdentifier;
	}

	public ClockTimeStruct getSimulationTime()
	{
		return simulationTime;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
