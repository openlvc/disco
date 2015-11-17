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
package org.openlvc.disco.pdu.record;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * The event identification shall be specified by the Event Identifier Record. The record shall
 * consist of a Simulation Address Record and an Event Number. The latter is uniquely assigned
 * within the host by the simulation application that initiates the sequence of events. The Event
 * Identifier Record shall be set to one for each exercise and incremented by one for each fire
 * event or collision event. In the case where all possible values are exhausted, the numbers may
 * be reused, beginning at one.
 */
public class EventIdentifier implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private SimulationAddress simulationAddress;
	private int eventID;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EventIdentifier()
	{
		this( new SimulationAddress(), 0 );
	}
	
	public EventIdentifier( SimulationAddress simulationAddress, int eventID )
	{
		this.simulationAddress = simulationAddress;
		this.eventID = eventID;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;

		if( other instanceof EventIdentifier )
		{
			EventIdentifier otherEvent = (EventIdentifier)other;
			if( otherEvent.simulationAddress.equals(this.simulationAddress) &&
			    otherEvent.eventID == this.eventID )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public EventIdentifier clone()
	{
		SimulationAddress addressClone = simulationAddress.clone();
		return new EventIdentifier( addressClone, eventID );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		simulationAddress.from( dis );
		eventID = dis.readUI16();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		simulationAddress.to( dos );
		dos.writeUI16( eventID );
	}

	@Override
	public int getByteLength()
	{
		int size = simulationAddress.getByteLength();
		size += DisSizes.UI16_SIZE;
		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public SimulationAddress getSimulationAddress()
    {
    	return simulationAddress;
    }

	public void setSimulationAddress( SimulationAddress simulationAddress )
    {
    	this.simulationAddress = simulationAddress;
    }

	public int getEventID()
    {
    	return eventID;
    }

	public void setEventID( int eventID )
    {
    	this.eventID = eventID;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
