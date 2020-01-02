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
package org.openlvc.disillusion.paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.openlvc.disco.utils.JsonUtils;
import org.openlvc.disco.utils.LLA;

/**
 * A simple regular polygon movement path. Specify the center, 
 * radius (distance from the center to the polygon corners), 
 * number of sides, orientation (angle offset from North in 
 * degrees) and speed.
 * 
 * Each time something has completed a "lap" of the polygon it
 * simply continues around again.
 */
public class PolygonPath implements IPath
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static double _2PI = Math.PI * 2;

	// JSON configuration keys
	private static final String CENTER_PARAM      = "center";
	private static final String RADIUS_PARAM      = "radius";
	private static final String SIDES_PARAM       = "sides";
	private static final String SPEED_PARAM       = "speed";
	private static final String ORIENTATION_PARAM = "orientation";
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// the center of the circular path
	private LLA center;
	// the length of the side of the square
	private double radius;
	private double speed;
	private double orientationRad;

	private List<LLA> corners;
	private double sideLength;
	private int cornerCount;
	private double circumference;
	private double centerAngle;
	private double halfCornerAngle;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Construct a polygonal path
	 * 
	 * @param center the center of the path
	 * @param sideCount the number of sides for the polygon
	 * @param radius the radius of the path in meters
	 * @param orientation the compass heading to orient the polygon towards, in degrees
	 * @param speed the speed at which entities should move along the path in meters per second
	 */
	public PolygonPath( LLA center, int sideCount, double radius, double orientation, double speed )
	{
		this.center = center;
		this.radius = radius;
		this.speed = speed;
		this.cornerCount = sideCount;
		this.orientationRad = Math.toRadians( orientation );
		
		// angles for triangle from center to ends of one
		// side of the polygon - fore example, consider this
		// square, where `X` is the center, and the diagonals
		// to the corners are the radius length (R), 
		//    a       b
		//     +-----+
		//     |   R/|
		//     |   / |
		//     |  X  |L
		//     |   \ |
		//     |   R\|
		//     +-----+
		//    d       c
		
		// The angle for triangle from center to the ends of 
		// one side of the polygon. for example, the angle 
		// at `X` between `b` and `c`
		this.centerAngle = _2PI / sideCount;
		// angle of other two angles for triangle from 
		// center to ends of one side of the polygon - for
		// example, the angle at `b` between `X` and `c`
		this.halfCornerAngle = (Math.PI - centerAngle) / 2.0;

		// from Law of sines: a/sin(A) = b/sin(B) = c/sin(C), we can
		// now work out the length of each side of the polygon (L)
		this.sideLength = this.radius * (Math.sin( centerAngle ) / Math.sin( halfCornerAngle ));
		// now we can work out the circumference along the edges the polygon
		this.circumference = this.sideLength * sideCount;

		this.corners = new ArrayList<>();
		for( int idx = 0; idx < this.cornerCount; idx++ )
		{
			double heading = Math.toDegrees( centerAngle * idx ) + orientationRad;
			LLA corner = this.center.destination( heading, radius );
			this.corners.add( corner );
		}
	}
	
	/**
	 * Construct a polygonal path from a JSON definition
	 * 
	 * @param json the JSON definition
	 * @param locationLookup a {@link Map} which contains well-known location aliases for lookup
	 * @return a {@link PolygonPath} instance
	 */
	public static PolygonPath fromJson(JSONObject json, Map<String,LLA> locationLookup )
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
		
		int sideCount               = JsonUtils.getInteger( json, SIDES_PARAM,       3 );
		double radius               = JsonUtils.getDouble(  json, RADIUS_PARAM,      500.0 );
		double orientation          = JsonUtils.getDouble(  json, ORIENTATION_PARAM, 0.0 );
		double speed                = JsonUtils.getDouble(  json, SPEED_PARAM,       10.0 );
		
		return new PolygonPath( center, sideCount, radius, orientation, speed );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public LLA getLLA(long time)
	{
		return getLLA(time, 0.0);
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
	public LLA getLLA( double distance )
	{
		// normalise to the polygon's total circumference
		while( distance < 0 )
			distance += this.circumference;
		while( distance > this.circumference )
			distance -= this.circumference;

		// which segment are we currently in?
		int segmentIdx = (int)Math.floor( distance / this.sideLength );
		// OK, so start from that segment's corner
		LLA corner = this.corners.get( segmentIdx );

		// how far are we into the segment?
		double remainingDistance = distance - (segmentIdx * sideLength);

		// what direction do we need to be heading from the corner?
		double bearing = (segmentIdx * this.centerAngle) + this.orientationRad + Math.PI - this.halfCornerAngle;

		return corner.destination( Math.toDegrees( bearing ), remainingDistance );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
