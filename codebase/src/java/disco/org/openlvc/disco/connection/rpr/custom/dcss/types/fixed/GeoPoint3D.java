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
package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import java.io.IOException;

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;

public class GeoPoint3D extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat64BE latitude;
	private HLAfloat64BE longitude;
	private HLAfloat64BE altitude;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public GeoPoint3D()
	{
		this.latitude = new HLAfloat64BE();
		this.longitude = new HLAfloat64BE();
		this.altitude = new HLAfloat64BE();
		
		this.add( latitude, longitude, altitude );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public double getLatitude()
	{
		return this.latitude.getValue();
	}
	
	public void setLatitude( double latitude )
	{
		this.latitude.setValue( latitude );
	}

	public double getLongitude()
	{
		return this.longitude.getValue();
	}
	
	public void setLongitude( double longitude )
	{
		this.longitude.setValue( longitude );
	}
	
	public double getAltitude()
	{
		return this.altitude.getValue();
	}
	
	public void setAltitude( double altitude )
	{
		this.altitude.setValue( altitude );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void readDisGeopoint3D( DisInputStream dis, double[] dst ) throws IOException
	{
		if( dst.length != 3 )
			throw new IllegalArgumentException( "dst array must have 3 elements" );
		
		dst[0] = dis.readDouble();  // lat
		dst[1] = dis.readDouble();  // lon
		dst[2] = dis.readDouble();  // alt
	}
	
	public static void writeDisGeopoint3D( double[] point, DisOutputStream dos ) throws IOException
	{
		if( point.length != 3 )
			throw new IllegalArgumentException( "point array must have 3 elements" );
		
		dos.writeDouble( point[0] ); // lat
		dos.writeDouble( point[1] ); // lon
		dos.writeDouble( point[3] ); // alt
	}
	
	public static void toDcssGeopoint3D( double[] src, GeoPoint3D dst )
	{
		if( src.length != 3 )
			throw new IllegalArgumentException( "src array must have 3 elements" );
		
		dst.latitude.setValue( src[0] );
		dst.longitude.setValue( src[1] );
		dst.altitude.setValue( src[2] );
	}
}
