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
package org.openlvc.disco.pdu.custom;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.ClockTime;

public class DcssWallclockTimePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ClockTime zuluTime;            /** Zulu wallclock time **/
	private ClockTime simulationTime;      /** Simulation time of day. **/
	private long simulationElapsedTime;    /** Time in milliseconds since the start of the simulation. **/
	private float scalingFactor;           /** Multiplier for clock time, e.g. 2x speed, 0.5x speed. **/
	private ClockTime simulationStartTime; /** Clock time when the simulation started. **/
	private byte clockState;               /** State of clock - started, paused or in an error state. **/

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DcssWallclockTimePdu()
	{
		super( PduType.DcssWallclockTime );
		this.zuluTime = new ClockTime();
		this.simulationTime = new ClockTime();
		this.simulationElapsedTime = 0;
		this.scalingFactor = 0.0f;
		this.simulationStartTime = new ClockTime();
		this.clockState = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.zuluTime.from( dis );
		this.simulationTime.from( dis );
		this.simulationElapsedTime = dis.readUI32();
		this.scalingFactor = dis.readFloat();
		this.simulationStartTime.from( dis );
		this.clockState = dis.readByte();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.zuluTime.to( dos );
		this.simulationTime.to( dos );
		dos.writeUI32( this.simulationElapsedTime );
		dos.writeFloat( this.scalingFactor );
		this.simulationStartTime.to( dos );
		dos.writeByte( this.clockState );
	}

	@Override
	public final int getContentLength()
	{
		return 33;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filtering Support Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return 0;
	}

	@Override
	public int getAppId()
	{
		return 0;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ClockTime getZuluTime()
	{
		return this.zuluTime;
	}
	
	public void setZuluTime( ClockTime zuluTime )
	{
		this.zuluTime = zuluTime;
	}
	
	public ClockTime getSimulationTime()
	{
		return this.simulationTime;
	}
	
	public void setSimulationTime( ClockTime simulationTime )
	{
		this.simulationTime = simulationTime;
	}
	
	public long getSimulationElapsedTime()
	{
		return this.simulationElapsedTime;
	}
	
	public void setSimulationElapsedTime( long elapsedTime )
	{
		this.simulationElapsedTime = elapsedTime;
	}
	
	public float getScalingFactor()
	{
		return this.scalingFactor;
	}
	
	public void setScalingFactor( float scalingFactor )
	{
		this.scalingFactor = scalingFactor;
	}
	
	public ClockTime getSimulationStartTime()
	{
		return this.simulationStartTime;
	}
	
	public void setSimulationStartTime( ClockTime startTime )
	{
		this.simulationStartTime = startTime;
	}
	
	public byte getClockState()
	{
		return this.clockState;
	}
	
	public void setClockState( byte state )
	{
		this.clockState = state;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
