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
import org.openlvc.disco.pdu.IPduComponent;

/**
 * The location of a radio transmitter's antenna shall be represented using an 
 * Antenna Location Record. This record shall specify the location of the 
 * radiating portion of the antenna. The antenna's location is specified in two 
 * different coordinate systems world coordinates and entity coordinates. The 
 * fields of this record are described in the paragraphs that follow.
 * 
 *  - *Antenna Location* This field shall specify the location of the 
 * 		radiating  portion of the antenna. This field shall be represented by a 
 * 		World Coordinates Record (see 5.3.33).
 * 	- *Relative Antenna Location* This field shall specify the location 
 * 		of the radiating portion of the antenna. This field shall be represented 
 * 		by an Entity Coordinate Vector Record (see 5.3.32.1)
 * 
 * @see "IEEE Std 1278.1-1995 section 5.3.33"
 * @see "IEEE Std 1278.1-1995 section 5.3.32.1"
 */
public class AntennaLocation implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private WorldCoordinate antennaLocation;
	private EntityCoordinate relativeAntennaLocation;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AntennaLocation()
	{
		this( new WorldCoordinate(), new EntityCoordinate() );
	}
	
	public AntennaLocation( WorldCoordinate antennaLocation,
	                        EntityCoordinate relativeAntennaLocation )
	{
		this.antennaLocation = antennaLocation;
		this.relativeAntennaLocation = relativeAntennaLocation;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof AntennaLocation )
		{
			AntennaLocation asAntennaLocation = (AntennaLocation)other;
			if( asAntennaLocation.antennaLocation.equals(this.antennaLocation) &&
				asAntennaLocation.relativeAntennaLocation.equals(this.relativeAntennaLocation) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public AntennaLocation clone()
	{
		WorldCoordinate antennaLocationClone = antennaLocation.clone();
		EntityCoordinate relativeAntennaLocationClone = relativeAntennaLocation.clone();
		
		return new AntennaLocation( antennaLocationClone, relativeAntennaLocationClone );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		antennaLocation.from( dis );
		relativeAntennaLocation.from( dis );
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		antennaLocation.to( dos );
		relativeAntennaLocation.to( dos );
    }
	
	@Override
    public final int getByteLength()
	{
		return 36;
		
		// int size = antennaLocation.getByteLength();       // 24
		// size += relativeAntennaLocation.getByteLength();  // 12
		// return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public WorldCoordinate getAntennaLocation()
	{
		return antennaLocation;
	}
	
	public void setAntennaLocation( WorldCoordinate antennaLocation )
	{
		this.antennaLocation = antennaLocation;
	}
	
	public EntityCoordinate getRelativeAntennaLocation()
	{
		return relativeAntennaLocation;
	}
	
	public void setRelativeAntennaLocation( EntityCoordinate relativeAntennaLocation )
	{
		this.relativeAntennaLocation = relativeAntennaLocation;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
