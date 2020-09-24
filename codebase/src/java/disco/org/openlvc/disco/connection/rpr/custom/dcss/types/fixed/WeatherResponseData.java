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

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.ArrayOfWeatherResponseDataVariant;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class WeatherResponseData extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RequestIdentifier id;
	private DateTimeStruct time;
	private HLAfloat64BE timeOffset;
	private GeoPoint3D location;
	private ArrayOfWeatherResponseDataVariant responseDataVariant;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherResponseData()
	{
		this.id = new RequestIdentifier();
		this.time = new DateTimeStruct();
		this.timeOffset = new HLAfloat64BE();
		this.location = new GeoPoint3D();
		this.responseDataVariant = new ArrayOfWeatherResponseDataVariant();
		
		this.add( this.id, 
		          this.time,
		          this.timeOffset,
		          this.location,
		          this.responseDataVariant );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RequestIdentifier getId() { return this.id; }
	public DateTimeStruct getTime() { return this.time; }
	public HLAfloat64BE getTimeOffset() { return this.timeOffset; }
	public GeoPoint3D getLocation() { return this.location; }
	public ArrayOfWeatherResponseDataVariant getResponseDataVariant() { return this.responseDataVariant; }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
