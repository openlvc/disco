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
package org.openlvc.disco.utils;

import org.json.simple.JSONObject;
import org.openlvc.disco.pdu.record.WorldCoordinate;

/**
 * Utility class representing Lat, Lon, Altitude.
 */
public class LLA
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// WGS-84 ellipsoid params
	private static final double vincenty_a = 6378137;            // Earth equatorial radius (meters)
	private static final double vincenty_b = 6356752.314245;     // Earth polar radius (meters)
	private static final double vincenty_f = 1 / 298.257223563;  // ellipsoid flattening

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private double latDeg;
	private double lonDeg;
	private double alt;
	private double latRad;
	private double lonRad;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LLA()
	{
		this( 0.0, 0.0, 0.0 );
	}

	public static LLA fromDegrees( double lat, double lon, double alt )
	{
		return new LLA( lat, lon, alt );
	}

	public static LLA fromRadians( double lat, double lon, double alt )
	{
		return new LLA( Math.toDegrees( lat ), Math.toDegrees( lon ), alt );
	}

	public static LLA fromECEF( WorldCoordinate ecef )
	{
		return CoordinateUtils.toLLA( ecef );
	}

	public static LLA fromLLA( LLA other )
	{
		return new LLA( other.latDeg, other.lonDeg, other.alt );
	}

	public static LLA fromJSON( JSONObject json )
	{
		double lat = JsonUtils.getDouble( json, "lat", 0.0 );
		double lon = JsonUtils.getDouble( json, "lon", 0.0 );
		double alt = JsonUtils.getDouble( json, "alt", 0.0 );
		return LLA.fromDegrees( lat, lon, alt );
	}

	private LLA( double lat, double lon, double alt )
	{
		this.latDeg = lat;
		this.lonDeg = lon;
		this.latRad = Math.toRadians( lat );
		this.lonRad = Math.toRadians( lon );
		this.alt = alt;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public WorldCoordinate toWorldCoordinate()
	{
		return CoordinateUtils.toECEF( this );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public double getLatitudeDegrees()
	{
		return this.latDeg;
	}
	
	public double getLongitudeDegrees()
	{
		return this.lonDeg;
	}
	
	public double getLatitudeRadians()
	{
		return this.latRad;
	}
	
	public double getLongitudeRadians()
	{
		return this.lonRad;
	}
	
	public double getAltitude()
	{
		return this.alt;
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		if( latDeg >= 0.0 )
			builder.append( latDeg+"N" );
		else
			builder.append( (latDeg*-1.0)+"S" );
		
		if( lonDeg >= 0.0 )
			builder.append( ", "+lonDeg+"E" );
		else
			builder.append( ", "+(lonDeg*-1.0)+"W" );
		
		builder.append( ", "+alt );
		return builder.toString();
	}
	
	//////////////////////////////////////////////////////////////////
	/// Distance Calculations   //////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	public double distanceBetween( LLA other )
	{
		double theta = lonRad - other.lonRad;
		double dist = Math.sin(latRad) * Math.sin(other.latRad) +
		              Math.cos(latRad) * Math.cos(other.latRad) * Math.cos(theta);
		dist = Math.acos( dist );
		dist = Math.toDegrees(dist); // radios to degrees
		dist = dist * 60 * 1.1515 * 1609.344; // last bit convert miles to meters
		return dist;
	}

	/*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * @returns Distance in Meters
	 */
	public double distanceBetweenHavershine( LLA other )
	{
	    final int R = 6371; // Radius of the earth

	    Double latDistance = Math.toRadians(other.latDeg - latDeg);
	    Double lonDistance = Math.toRadians(other.lonDeg - lonDeg);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(latDeg)) * Math.cos(Math.toRadians(other.latDeg))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = alt - other.alt;
	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}
	
	/**
	 * Calculates geodetic distance between two points specified by latitude/longitude using
	 * Vincenty inverse formula for ellipsoids.
	 * 
	 * @param lat1 first point latitude in decimal degrees
	 * @param lon1 first point longitude in decimal degrees
	 * @param lat2 second point latitude in decimal degrees
	 * @param lon2 second point longitude in decimal degrees
	 * @returns distance in meters between points with 5.10<sup>-4</sup> precision
	 * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Originally
	 *      posted here</a>
	 */
	public double distanceBetweenUsingVincentry( LLA other )
	{
		double L = other.lonRad - this.lonRad;
		double U1 = Math.atan( (1-vincenty_f) * Math.tan(latRad) );
		double U2 = Math.atan( (1-vincenty_f) * Math.tan(other.latRad) );
		double sinU1 = Math.sin( U1 ), cosU1 = Math.cos( U1 );
		double sinU2 = Math.sin( U2 ), cosU2 = Math.cos( U2 );
		
		double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
		double lambda = L, lambdaP, iterLimit = 100;

		do
		{
			sinLambda = Math.sin( lambda );
			cosLambda = Math.cos( lambda );
			sinSigma = Math.sqrt( (cosU2 * sinLambda) * (cosU2 * sinLambda) +
			                      (cosU1 * sinU2 - sinU1*cosU2*cosLambda) * (cosU1*sinU2 - sinU1*cosU2*cosLambda) );

			if( sinSigma == 0 )
				return 0; // co-incident points

			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2( sinSigma, cosSigma );
			sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
			if( Double.isNaN( cos2SigmaM ) )
				cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (6)
			double C = vincenty_f / 16 * cosSqAlpha * (4+vincenty_f * (4-3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L + (1 - C) * vincenty_f * sinAlpha *
			             (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1+2*cos2SigmaM*cos2SigmaM)));
		}
		while( Math.abs( lambda - lambdaP ) > 1e-12 && --iterLimit > 0 );
		
		if( iterLimit == 0 )
			return Double.NaN; // formula failed to converge

		double uSq = cosSqAlpha * (vincenty_a * vincenty_a - vincenty_b * vincenty_b) / (vincenty_b * vincenty_b);
		double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = B * sinSigma *
		                    (cos2SigmaM + B / 4 *
		                                  (cosSigma *
		                                   (-1 + 2 *
		                                         cos2SigmaM * cos2SigmaM) -
		                                   B / 6 * cos2SigmaM * (-3 + 4 * sinSigma *
		                                                              sinSigma) * (-3 +
		                                                                           4 * cos2SigmaM *
		                                                                                cos2SigmaM)));
		double dist = vincenty_b * A * (sigma - deltaSigma);
		return dist;
	}
	
	/**
	 * Return the destination LLA reached by starting from this LLA and
	 * traveling in the given heading (in degrees) the given distance (in meters)
	 * 
	 * @param bearing the compass heading to travel (in degrees)
	 * @param distance the distance to travel (in meters)
	 * @return the LLA of the destination point reached
	 */
	public LLA destination( double bearing, double distance )
	{
		double bearingRad = Math.toRadians(bearing);
		
		// calculate values which are used multiple times once here
		double EARTH_MEAN_RADIUS = 6371000;
		double dR = distance / EARTH_MEAN_RADIUS;  // angular distance
		double cosDR = Math.cos( dR );
		double sinDR = Math.sin( dR );
		double cosLat = Math.cos( this.latRad );
		double sinLat = Math.sin( this.latRad );
		
		// calculate the destination
		double dstLat = Math.asin( sinLat * cosDR + cosLat * sinDR * Math.cos( bearingRad ) );
		double dstLon = this.lonRad + Math.atan2( Math.sin( bearingRad ) * sinDR * cosLat,
		                                     cosDR - sinLat * Math.sin( dstLat ) );
		return LLA.fromRadians( dstLat, dstLon, this.alt );
	}	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
