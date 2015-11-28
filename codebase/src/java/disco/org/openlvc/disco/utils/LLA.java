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

/**
 * Utility class representing Lat, Lon, Altitude.
 */
public class LLA
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

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

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

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
			throw new IllegalArgumentException( lat+" not a valid latitude. Must be between +-90" );
	}
	
	public void setLongitude( double lon )
	{
		if( lon > 180.0 || lon < -180.0 )
			throw new IllegalArgumentException( lon+" not a valid latitude. Must be between +-180" );
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
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
