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
package org.openlvc.disco.pdu.field;

import java.util.HashMap;
import java.util.Map;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;

/**
 * 2019-SISO-REF-010-v27 [UID 78]
 */
public enum BeamFunction
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other(0,true),
	Search(1),
	HeightFinding(2),
	Acquisition(3),
	Tracking(4),
	AcquisitionAndTracking(5),
	CommandGuidance(6),
	Illumination(7),
	Ranging(8),
	MissileBeacon(9),
	MissileFusing(10),
	ActiveRadarMissileSeeker(11),
	Jamming(12,true),
	IFF(13),
	NavigationWeather(14),
	Meteorological(15),
	DataTransmission(16),
	NavigationalDirectionalBeacon(17),
	TimeSharedSearch(20),
	TimeSharedAcquisition(21),
	TimeSharedTrack(22),
	TimeSharedCommandGuidance(23),
	TimeSharedIllumination(24),
	TimeSharedJamming(25,true);

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static Map<Short,BeamFunction> CACHE = new HashMap<>();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;
	private boolean jamming; // This is a Jammer beam; otherwise it's a Radar beam

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private BeamFunction( int value, boolean jamming )
	{
		this.value = (short)value;
		this.jamming = jamming;
	}
	
	private BeamFunction( int value  )
	{
		this( value, false );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	public boolean isJamming()
	{
		return this.jamming;
	}
	
	public boolean isTracking()
	{
		return this.jamming == false;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}
	
	public static BeamFunction fromValue( short value )
	{
		// lazy-load
		if( CACHE.isEmpty() )
		{
			for( BeamFunction temp : BeamFunction.values() )
				CACHE.put( temp.value(), temp );
		}

		BeamFunction found = CACHE.get( value );
		if( found != null )
			return found;
		else if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" is not a valid ForceId" );
		else
			return Other;
	}
}
