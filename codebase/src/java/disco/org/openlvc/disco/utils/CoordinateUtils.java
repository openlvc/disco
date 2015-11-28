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

public class CoordinateUtils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// WGS84 ellipsoid constants
	private static final double a = 6378137; // radius
	private static final double e = 8.1819190842622e-2;  // eccentricity
	private static final double asq = Math.pow(a,2);
	private static final double esq = Math.pow(e,2);

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static WorldCoordinate llaToEcef( LLA coordinate )
	{
        double lat = Math.toRadians(coordinate.getLatitude());
        double lon = Math.toRadians(coordinate.getLongitude());
        double alt = Math.toRadians(coordinate.getAltitude());

        //intermediate calculation (prime vertical radius of curvature)
        double N = a / Math.sqrt(1 - (e * e) * (Math.sin(lat) * Math.sin(lat)));

        double x = (N + alt) * Math.cos(lat) * Math.cos(lon);
        double y = (N + alt) * Math.cos(lat) * Math.sin(lon);
        double z = ((1 - (e * e)) * N + alt) * Math.sin(lat);

        return new WorldCoordinate( x, y, z );
	}

	public static LLA eceftolla( WorldCoordinate ecef )
	{
		double x = ecef.getX();
		double y = ecef.getY();
		double z = ecef.getZ();

		double b = Math.sqrt( asq * (1-esq) );
		double bsq = Math.pow(b,2);
		double ep = Math.sqrt( (asq - bsq)/bsq);
		double p = Math.sqrt( Math.pow(x,2) + Math.pow(y,2) );
		double th = Math.atan2(a*z, b*p);
		
		double lon = Math.atan2(y,x);
		double lat = Math.atan2( (z + Math.pow(ep,2)*b*Math.pow(Math.sin(th),3) ), (p - esq*a*Math.pow(Math.cos(th),3)) );
		double N = a/( Math.sqrt(1-esq*Math.pow(Math.sin(lat),2)) );
		double alt = p / Math.cos(lat) - N;

		// mod lat to 0-2pi
		lon = lon % (2*Math.PI);
		
		// correction for altitude near poles left out.
		return new LLA( Math.toDegrees(lat), Math.toDegrees(lon), alt );
	}	
}
