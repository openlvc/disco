package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class AtmosphereResponseData extends WrappedHlaFixedRecord
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
	private HLAfloat32BE windDirection;
	private HLAfloat32BE windSpeed;
	private HLAfloat32BE humidity;
	private HLAfloat32BE verticalWind;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AtmosphereResponseData()
	{
		this.temperature = new HLAfloat32BE();
		this.pressure = new HLAfloat32BE();
		this.mslPressure = new HLAfloat32BE();
		this.windDirection = new HLAfloat32BE();
		this.windSpeed = new HLAfloat32BE();
		this.humidity = new HLAfloat32BE();
		this.verticalWind = new HLAfloat32BE();
		
		this.add( this.temperature,
		          this.pressure,
		          this.mslPressure,
		          this.windDirection,
		          this.windSpeed,
		          this.humidity,
		          this.verticalWind );
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
	public HLAfloat32BE getWindDirection() { return this.windDirection; }
	public HLAfloat32BE getWindSpeed() { return this.windSpeed; }
	public HLAfloat32BE getHumidity() { return this.humidity; }
	public HLAfloat32BE getVerticalWind() { return this.verticalWind; }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
