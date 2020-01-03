/*
 *   Copyright 2019 Open LVC Project.
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
package org.openlvc.disruptor.paths;

import java.util.Map;

import org.json.simple.JSONObject;
import org.openlvc.disco.utils.JsonUtils;
import org.openlvc.disco.utils.LLA;

/**
 * A simple linear movement path. Specify the origin, 
 * distance to travel, heading, and speed.
 * 
 * Things which follow this path will "ping-pong" back
 * and forth between the origin and destination. 
 */
public class LinePath implements IPath
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// JSON configuration keys
	private static final String START_PARAM    = "start";
	private static final String HEADING_PARAM  = "heading";
	private static final String DISTANCE_PARAM = "distance";
	private static final String SPEED_PARAM    = "speed";
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private double length;
	private double speed;
	private double headingRad;

	private LLA start;
	private LLA end;
	private double oneLapDistance;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Construct a linear path
	 * 
	 * @param start the starting point of the path
	 * @param heading the compass heading to travel from the start, in degrees
	 * @param distance the radius of the path in meters
	 * @param speed the speed at which entities should move along the path in meters per second
	 */
	public LinePath( LLA start, double heading, double distance, double speed )
	{
		this.start = start;
		this.end = start.destination( heading, distance );
		this.length = distance;
		this.speed = speed;

		this.headingRad = Math.toRadians( heading );
		this.oneLapDistance = distance * 2.0;
	}

	/**
	 * Construct a linear path from a JSON definition
	 * 
	 * @param json the JSON definition
	 * @param locationLookup a {@link Map} which contains well-known location aliases for lookup
	 * @return a {@link LinePath} instance
	 */
	public static LinePath fromJson(JSONObject json, Map<String,LLA> locationLookup )
	{
		LLA start = null;
		
		if( locationLookup != null )
		{
			// see if this refers to a "shortcut" entry (i.e., a place name) in the lookup 
			// table for well-known locations
			start = locationLookup.get( json.get( START_PARAM ) );
		}
		
		if(start == null)
		{
			// no shortcut entry - try to parse as lat/long/altitude
			start = LLA.fromJSON( JsonUtils.getJSONObject( json, START_PARAM, null ) );
		}
		
		double heading              = JsonUtils.getDouble( json, HEADING_PARAM,  0.0 );
		double distance             = JsonUtils.getDouble( json, DISTANCE_PARAM, 500.0 );
		double speed                = JsonUtils.getDouble( json, SPEED_PARAM,    10.0 );

		return new LinePath( start, heading, distance, speed );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public LLA getLLA( long time )
	{
		return getLLA( time, 0.0 );
	}

	@Override
	public LLA getLLA( long time, double offset )
	{
		double seconds = time / 1000.0;

		// how far have we travelled?
		double distance = (seconds * this.speed) + offset;

		return getLLA( distance );
	}

	@Override
	public LLA getLLA(double distance)
	{
		// normalise to the return journey length
		while( distance < 0 )
			distance += this.oneLapDistance;
		while( distance > this.oneLapDistance )
			distance -= this.oneLapDistance;

		// which segment are we currently in?
		int segmentIdx = (int)Math.floor( distance / this.length );
		boolean isStart = segmentIdx % 2 == 0;
		LLA origin = isStart ? start : end;
		// which direction are we heading?
		double bearing = isStart ? headingRad : headingRad + Math.PI;
		// how far are we into the segment?
		double remainingDistance = distance - (segmentIdx * this.length);

		return origin.destination( Math.toDegrees( bearing ), remainingDistance );
	}
	
	@Override
	public double getHeadingRad( long time )
	{
		return getHeadingRad( time, 0.0 );
	}
	
	@Override
	public double getHeadingRad( long time, double offset )
	{
		double seconds = time / 1000.0;
		// how far have we travelled?
		double distance = (seconds * this.speed) + offset;
		return getHeadingRad( distance );
	}
	
	@Override
	public double getHeadingRad( double distance )
	{
		// which segment are we currently in?
		int segmentIdx = (int)Math.floor( distance / this.length );
		boolean isStart = segmentIdx % 2 == 0;
		// which direction are we heading?
		return (isStart ? headingRad : headingRad + Math.PI) - _90DEGREES;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
