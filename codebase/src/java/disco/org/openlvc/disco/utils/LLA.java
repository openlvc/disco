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
	private static final double vincentry_a = 6378137;
	private static final double vincentry_b = 6356752.314245;
	private static final double vincentry_f = 1 / 298.257223563; 

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private double lat;
	private double lon;
	private double alt;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LLA()
	{
		this.lat = 0.0;
		this.lon = 0.0;
		this.alt = 0.0;
	}

	public LLA( double lat, double lon, double alt )
	{
		this();
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
	}
	
	public LLA( WorldCoordinate ecef )
	{
		LLA other = CoordinateUtils.toLLA( ecef );
		this.lat = other.lat;
		this.lon = other.lon;
		this.alt = other.alt;
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
	public double getLatitude()
	{
		return this.lat;
	}
	
	public double getLongitude()
	{
		return this.lon;
	}
	
	public double getAltitude()
	{
		return this.alt;
	}

	public void setLatitude( double lat )
	{
		if( lat > 90.0 || lat < -90.0 )
		{
			if( lat == 180.0 || lat == -180.0 )
				lat = 0.0;
			else
				throw new IllegalArgumentException( lat+" not a valid latitude. Must be between +-90" );
		}
		
		this.lat = lat;
	}
	
	public void setLongitude( double lon )
	{
		if( lon > 180.0 || lon < -180.0 )
			throw new IllegalArgumentException( lon+" not a valid latitude. Must be between +-180" );
		
		this.lon = lon;
	}
	
	public void setAltitude( double alt )
	{
		this.alt = alt;
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		if( lat >= 0.0 )
			builder.append( lat+"N" );
		else
			builder.append( (lat*-1.0)+"S" );
		
		if( lon >= 0.0 )
			builder.append( ", "+lon+"E" );
		else
			builder.append( ", "+(lon*-1.0)+"W" );
		
		builder.append( ", "+alt );
		return builder.toString();
	}
	
	
	//////////////////////////////////////////////////////////////////
	/// Distance Calculations   //////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	public double distanceBetween( LLA other )
	{
		double theta = lon - other.lon;
		double dist = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(other.lat)) +
		              Math.cos(deg2rad(lat)) * Math.cos(deg2rad(other.lat)) * Math.cos(deg2rad(theta));
		dist = Math.acos( dist );
		dist = (dist * 180.0 / Math.PI); // radios to degrees
		dist = dist * 60 * 1.1515 * 1609.344; // last bit convert miles to meters
		return dist;
	}

	private double deg2rad( double degrees )
	{
		return( degrees * Math.PI / 180.0 );
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

	    Double latDistance = Math.toRadians(other.lat - lat);
	    Double lonDistance = Math.toRadians(other.lon - lon);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(other.lat))
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
		double L = Math.toRadians( other.lon - this.lon );
		double U1 = Math.atan( (1-vincentry_f) * Math.tan(Math.toRadians(lat)) );
		double U2 = Math.atan( (1-vincentry_f) * Math.tan(Math.toRadians(other.lat)) );
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
			double C = vincentry_f / 16 * cosSqAlpha * (4+vincentry_f * (4-3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L + (1 - C) * vincentry_f * sinAlpha *
			             (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1+2*cos2SigmaM*cos2SigmaM)));
		}
		while( Math.abs( lambda - lambdaP ) > 1e-12 && --iterLimit > 0 );
		
		if( iterLimit == 0 )
			return Double.NaN; // formula failed to converge

		double uSq = cosSqAlpha * (vincentry_a * vincentry_a - vincentry_b * vincentry_b) / (vincentry_b * vincentry_b);
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
		double dist = vincentry_b * A * (sigma - deltaSigma);
		return dist;
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
