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
package org.openlvc.disco.connection.rpr.custom.dcss.objects;

import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.ClockStateEnum;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.fixed.ClockTimeStruct;
import org.openlvc.disco.connection.rpr.types.simple.Float32;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.DcssWallclockTimePdu;
import org.openlvc.disco.pdu.record.EntityId;

public class WallclockTime extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ClockTimeStruct zuluTime;
	private ClockTimeStruct simulationTime;
	private RPRunsignedInteger32BE simulationElapsedTime;
	private Float32 scalingFactor;
	private ClockTimeStruct simulationStartTime;
	private EnumHolder<ClockStateEnum> clockState;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WallclockTime()
	{
		this.zuluTime = new ClockTimeStruct();
		this.simulationTime = new ClockTimeStruct();
		this.simulationElapsedTime = new RPRunsignedInteger32BE();
		this.scalingFactor = new Float32();
		this.simulationStartTime = new ClockTimeStruct();
		this.clockState = new EnumHolder<>( ClockStateEnum.Paused );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public void fromPdu( PDU pdu )
	{
		DcssWallclockTimePdu asWallclock = pdu.as( DcssWallclockTimePdu.class );
		this.zuluTime.setDisValue( asWallclock.getZuluTime() );
		this.simulationTime.setDisValue( asWallclock.getSimulationTime() );
		this.simulationElapsedTime.setValue( asWallclock.getSimulationElapsedTime() );
		this.scalingFactor.setValue( asWallclock.getScalingFactor() );
		this.simulationStartTime.setDisValue( asWallclock.getSimulationStartTime() );
		this.clockState.setEnum( ClockStateEnum.valueOf(asWallclock.getClockState()) );
	}
	
	@Override
	public DcssWallclockTimePdu toPdu()
	{
		DcssWallclockTimePdu pdu = new DcssWallclockTimePdu();
		pdu.setZuluTime( this.zuluTime.getDisValue() );
		pdu.setSimulationTime( this.simulationTime.getDisValue() );
		pdu.setSimulationElapsedTime( this.simulationElapsedTime.getValue() );
		pdu.setScalingFactor( this.scalingFactor.getValue() );
		pdu.setSimulationStartTime( this.simulationStartTime.getDisValue() );
		pdu.setClockState( this.clockState.getEnum().getValue() );
		
		return pdu;
	}
	@Override
	public EntityId getDisId()
	{
		return null;
	}
	
	@Override
	protected boolean checkReady()
	{
		return simulationTime.isDecodeCalled();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ClockTimeStruct getZuluTime()
	{
		return this.zuluTime;
	}
	
	public ClockTimeStruct getSimulationTime()
	{
		return this.simulationTime;
	}
	
	public RPRunsignedInteger32BE getSimulationElapsedTime()
	{
		return this.simulationElapsedTime;
	}
	
	public Float32 getScalingFactor()
	{
		return this.scalingFactor;
	}
	
	public ClockTimeStruct getSimulationStartTime()
	{
		return this.simulationStartTime;
	}
	
	public EnumHolder<ClockStateEnum> getClockState()
	{
		return this.clockState;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
