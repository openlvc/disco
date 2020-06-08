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
package org.openlvc.disco.pdu.record;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * DIS 7 - 6.2.90: Track/Jam Data Record
 */
public class TrackJamData implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId target;
	private short emitterNumber;
	private short beamNumber;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public TrackJamData()
	{
		this( new EntityId(0,0,0) );
	}

	public TrackJamData( EntityId target )
	{
		this( target, (short)0, (short)0 );
	}
	
	public TrackJamData( EntityId target, short emitterNumber, short beamNumber )
	{
		this.target = target;
		this.emitterNumber = emitterNumber;
		this.beamNumber = beamNumber;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;

		if( other instanceof TrackJamData )
		{
			TrackJamData otherTrack = (TrackJamData)other;
			if( otherTrack.target.equals(this.target) &&
				otherTrack.emitterNumber == this.emitterNumber &&
			    otherTrack.beamNumber == this.beamNumber )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public TrackJamData clone()
	{
		return new TrackJamData( target.clone(), emitterNumber, beamNumber );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		target.from( dis );
		this.emitterNumber = dis.readUI8();
		this.beamNumber = dis.readUI8();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		target.to( dos );
		dos.writeUI8( this.emitterNumber );
		dos.writeUI8( this.beamNumber );
    }
	
	@Override
    public final int getByteLength()
	{
		return 8;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getTarget()
	{
		return target;
	}

	public void setTarget( EntityId target )
	{
		this.target = target;
	}

	public short getEmitterNumber()
	{
		return emitterNumber;
	}

	public void setEmitterNumber( short emitterNumber )
	{
		this.emitterNumber = emitterNumber;
	}

	public short getBeamNumber()
	{
		return beamNumber;
	}

	public void setBeamNumber( short beamNumber )
	{
		this.beamNumber = beamNumber;
	}
	

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
