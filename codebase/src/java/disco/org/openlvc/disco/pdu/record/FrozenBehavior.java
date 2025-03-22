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
package org.openlvc.disco.pdu.record;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * A bit field where any individual bit set to one indicates that the simulation application is to 
 * continue the corresponding activity in the frozen state; a bit set to zero indicates that the 
 * activity is to cease in the frozen state
 */
public class FrozenBehavior implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean runSimulationClock;
	private boolean transmitUpdates;
	private boolean processUpdates;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FrozenBehavior()
	{
		this( false, false, false );
	}
	
	public FrozenBehavior( boolean runSimulationClock,
	                       boolean transmitUpdates,
	                       boolean processUpdates )
	{
		this.runSimulationClock = runSimulationClock;
		this.transmitUpdates = transmitUpdates;
		this.processUpdates = processUpdates;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof FrozenBehavior )
		{
			FrozenBehavior otherBehavior = (FrozenBehavior)other;
			return otherBehavior.runSimulationClock == this.runSimulationClock &&
			       otherBehavior.transmitUpdates == this.transmitUpdates &&
			       otherBehavior.processUpdates == this.processUpdates;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( this.runSimulationClock, 
		                     this.transmitUpdates,
		                     this.processUpdates );
	}
	
	@Override
	public FrozenBehavior clone()
	{
		return new FrozenBehavior( runSimulationClock, 
		                           transmitUpdates, 
		                           processUpdates );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		short ch1 = dis.readUI8();
		
		if( ch1 < 0 )
			throw new EOFException();
		
		runSimulationClock = (ch1 & 0x01) != 0;
		transmitUpdates = (ch1 & 0x02) != 0;
		processUpdates = (ch1 & 0x04) != 0;
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		short ch1 = 0;
		
		if( runSimulationClock )
			ch1 |= 0x01;
		
		if( transmitUpdates )
			ch1 |= 0x02;
		
		if( processUpdates )
			ch1 |= 0x04;
		
		dos.writeUI8( ch1 );
	}

	@Override
    public final int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isRunSimulationClock()
	{
		return this.runSimulationClock;
	}

	public void setRunSimulationClock( boolean runSimulationClock )
	{
		this.runSimulationClock = runSimulationClock;
	}

	public boolean isTransmitUpdates()
	{
		return this.transmitUpdates;
	}

	public void setTransmitUpdates( boolean transmitUpdates )
	{
		this.transmitUpdates = transmitUpdates;
	}
	
	public boolean isProcessUpdates()
	{
		return this.processUpdates;
	}

	public void setProcessUpdates( boolean processUpdates )
	{
		this.processUpdates = processUpdates;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
