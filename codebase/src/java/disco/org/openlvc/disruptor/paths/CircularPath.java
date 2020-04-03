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
 * A simple circular movement path. Specify the center, 
 * radius and speed.
 * 
 * Each time something has completed a "lap" of the circle it
 * simply continues around again.
 */
public class CircularPath implements IPath
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static double _2PI = Math.PI * 2;

	// JSON configuration keys
	private static final String CENTER_PARAM = "center";
	private static final String RADIUS_PARAM = "radius";
	private static final String SPEED_PARAM = "speed";
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// the center of the circular path
	private LLA center;
	// the diameter of the circular path
	private double radius;
	private double circumference;
	// the angular rotation speed about the center (it's easier to
	// deal with this value than the original speed)
	private double angularSpeed;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Construct a circular path
	 * 
	 * @param center the center of the path
	 * @param radius the radius of the path in meters
	 * @param speed the speed at which entities should move along the path in meters per second
	 */
	public CircularPath( LLA center, double radius, double speed )
	{
		this.center = center;
		this.radius = radius;

		this.circumference = radius * _2PI;
		this.angularSpeed = _2PI / (this.circumference / speed);
	}

	/**
	 * Construct a circular path from a JSON definition
	 * 
	 * @param json the JSON definition
	 * @param locationLookup a {@link Map} which contains well-known location aliases for lookup
	 * @return a {@link CircularPath} instance
	 */
	public static CircularPath fromJson(JSONObject json, Map<String,LLA> locationLookup )
	{
		LLA center = null;
		
		if( locationLookup != null )
		{
			// see if this refers to a "shortcut" entry (i.e., a place name) in the lookup 
			// table for well-known locations
			center = locationLookup.get( json.get( CENTER_PARAM ) );
		}
		
		if(center == null)
		{
			// no shortcut entry - try to parse as lat/long/altitude
			center = LLA.fromJSON( JsonUtils.getJSONObject( json, CENTER_PARAM, null ) );
		}
		
		double radius                = JsonUtils.getDouble( json, RADIUS_PARAM, 500.0 );
		double speed                 = JsonUtils.getDouble( json, SPEED_PARAM,  10.0 );

		return new CircularPath( center, radius, speed );
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
		double currentAngle = this.angularSpeed * seconds;
		if( offset != 0 )
		{
			double angularOffset = (offset / this.circumference) * _2PI;
			currentAngle += angularOffset;
		}
		return this.center.destination( Math.toDegrees( currentAngle ), this.radius );
	}

	@Override
	public LLA getLLA( double distance )
	{
		double angularOffset = (distance / this.circumference) * _2PI;
		return this.center.destination( Math.toDegrees( angularOffset ), this.radius );
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
		double currentAngle = this.angularSpeed * seconds;
		if( offset != 0 )
		{
			double angularOffset = (offset / this.circumference) * _2PI;
			currentAngle += angularOffset;
		}
		return currentAngle;
	}
	
	@Override
	public double getHeadingRad( double distance )
	{
		return (distance / this.circumference) * _2PI;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
