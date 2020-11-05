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

import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.Domain;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint3D;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GroundResponseData;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.RequestIdentifier;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.WeatherResponseData;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.DcssWeatherResponsePdu;
import org.openlvc.disco.pdu.custom.field.DcssWeatherDomain;

public class WeatherResponse extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private WeatherResponseData responseData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherResponse()
	{
		super();
		this.responseData = new WeatherResponseData();
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
		throw new UnsupportedOperationException( "DcssWeatherResponsePdu -> HLA Interaction not supported" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DcssWeatherResponsePdu toPdu()
	{
		DcssWeatherResponsePdu pdu = new DcssWeatherResponsePdu();
		
		//
		// ID
		//
		RequestIdentifier id = responseData.getId();
		pdu.setUuid( id.getUuid().getDisValue() );
		pdu.setEntityId( id.getEntityIdentifier().getDisValue() );
		
		//
		// Time
		//
		pdu.setDateTime( responseData.getTime().getDisValue() );
		
		//
		// Time Offset
		// 
		pdu.setTimeOffset( responseData.getTimeOffset().getValue() );
		
		//
		// Location
		//
		GeoPoint3D location = responseData.getLocation();
		pdu.setLatitude( location.getLatitude() );
		pdu.setLongitude( location.getLongitude() );
		pdu.setAltitude( location.getAltitude() );
		
		//
		// Response Data Variant
		//
		responseData.getResponseDataVariant().forEach( (v) -> {
			
			// The ground layer gives us all we need for the moment, but if you need data from the other
			// layers, then handle it here...
			if( v.getDiscriminant() == Domain.Ground )
			{
				pdu.addDomain( DcssWeatherDomain.Ground );
				GroundResponseData data = (GroundResponseData)v.getValue();
				pdu.setHumidity( data.getHumidity().getValue() );
				pdu.setPrecipitationRate( data.getPrecipitationRate().getValue() );
				pdu.setConvectionalPrecipitationRate( data.getConvecPrecipitationRate().getValue() );
				pdu.setPressure( data.getPressure().getValue() );
				pdu.setTemperature( data.getTemperature().getValue() );
				pdu.setTotalCloudCover( data.getTotalCloudCover().getValue() );
			}
		});
		

		return pdu;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public WeatherResponseData getResponseData()
	{
		return this.responseData;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
