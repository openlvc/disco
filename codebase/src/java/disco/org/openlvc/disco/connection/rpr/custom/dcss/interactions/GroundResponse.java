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
package org.openlvc.disco.connection.rpr.custom.dcss.interactions;

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.DcssWeatherResponsePdu;

public class GroundResponse extends WeatherResponse
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE precipitationRate;
	private HLAfloat32BE temperature;
	private HLAfloat32BE humidity;
	private HLAfloat32BE pressure;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public GroundResponse()
	{
		super();
		this.precipitationRate = new HLAfloat32BE();
		this.temperature = new HLAfloat32BE();
		this.humidity = new HLAfloat32BE();
		this.pressure = new HLAfloat32BE();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		super.fromPdu( incoming );
		DcssWeatherResponsePdu pdu = incoming.as( DcssWeatherResponsePdu.class );
		
		this.precipitationRate.setValue( pdu.getPrecipitationRate() );
		this.temperature.setValue( pdu.getTemperature() );
		this.humidity.setValue( pdu.getHumidity() );
		this.pressure.setValue( pdu.getPressure() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DcssWeatherResponsePdu toPdu()
	{
		DcssWeatherResponsePdu pdu = super.toPdu();
		
		pdu.setPrecipitationRate( this.precipitationRate.getValue() );
		pdu.setTemperature( this.temperature.getValue() );
		pdu.setHumidity( this.humidity.getValue() );
		pdu.setPressure( this.pressure.getValue() );
		
		return pdu;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAfloat32BE getPrecipitationRate()
	{
		return this.precipitationRate;
	}
	
	public HLAfloat32BE getTemperate()
	{
		return this.temperature;
	}
	
	public HLAfloat32BE getHumidity()
	{
		return this.humidity;
	}
	
	public HLAfloat32BE getPressure()
	{
		return this.pressure;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
