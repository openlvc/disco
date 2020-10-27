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

import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.PrecipitationType;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class GroundResponseData extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE temperature;
	private HLAfloat32BE pressure;
	private HLAfloat32BE mslPressure;
	private HLAfloat32BE humidity;
	private HLAfloat32BE windDirection;
	private HLAfloat32BE windSpeed;
	private HLAfloat32BE windGust;
	private HLAfloat32BE totalCloudCover;
	private HLAfloat32BE precipitationRate;
	private HLAfloat32BE convecPrecipitationRate;
	private EnumHolder<PrecipitationType> precipitationType;
	private HLAfloat32BE visibility;
	private HLAfloat32BE lightning;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public GroundResponseData()
	{
		this.temperature = new HLAfloat32BE();
		this.pressure = new HLAfloat32BE();
		this.mslPressure = new HLAfloat32BE();
		this.humidity = new HLAfloat32BE();
		this.windDirection = new HLAfloat32BE();
		this.windSpeed = new HLAfloat32BE();
		this.windGust = new HLAfloat32BE();
		this.totalCloudCover = new HLAfloat32BE();
		this.precipitationRate = new HLAfloat32BE();
		this.convecPrecipitationRate = new HLAfloat32BE();
		this.precipitationType = new EnumHolder<>( PrecipitationType.InvalidPrecipitationType );
		this.visibility = new HLAfloat32BE();
		this.lightning = new HLAfloat32BE();
		
		this.add( this.temperature, 
		          this.pressure,
		          this.mslPressure,
		          this.humidity,
		          this.windDirection,
		          this.windSpeed,
		          this.windGust,
		          this.totalCloudCover,
		          this.precipitationRate,
		          this.convecPrecipitationRate,
		          this.precipitationType,
		          this.visibility,
		          this.lightning );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAfloat32BE getTemperature() { return this.temperature; }
	public HLAfloat32BE getPressure() { return this.pressure; }
	public HLAfloat32BE getMslPressure() { return this.mslPressure; }
	public HLAfloat32BE getHumidity() { return this.humidity; }
	public HLAfloat32BE getWindDirection() { return this.windDirection; }
	public HLAfloat32BE getWindSpeed() { return this.windSpeed; }
	public HLAfloat32BE getWindGust() { return this.windGust; }
	public HLAfloat32BE getTotalCloudCover() { return this.totalCloudCover; }
	public HLAfloat32BE getPrecipitationRate() { return this.precipitationRate; }
	public HLAfloat32BE getConvecPrecipitationRate() { return this.convecPrecipitationRate; }
	public EnumHolder<PrecipitationType> getPrecipitationType() { return this.precipitationType; }
	public HLAfloat32BE getVisibility() { return this.visibility; }
	public HLAfloat32BE getLightning() { return this.lightning; }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
