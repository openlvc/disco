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

import java.util.HashSet;
import java.util.Set;

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.UuidArrayOfHLAbyte16;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.Domain;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint3D;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.RequestIdentifier;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.WeatherRequestData;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.types.basic.HLAinteger64BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.DcssWeatherRequestPdu;
import org.openlvc.disco.pdu.custom.field.DcssWeatherDomain;

public class WeatherRequestLocation extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger64BE instanceId;
	private WeatherRequestData weatherData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherRequestLocation()
	{
		super();
		this.instanceId = new HLAinteger64BE();
		this.weatherData = new WeatherRequestData();
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
		DcssWeatherRequestPdu pdu = incoming.as( DcssWeatherRequestPdu.class );
		
		//
		// Instance ID
		//
		this.instanceId.setValue( pdu.getInstanceId() );
		
		//
		// Weather Data
		//
		// ID
		RequestIdentifier requestId = this.weatherData.getId();
		requestId.getEntityIdentifier().setValue( pdu.getEntityId() );
		UuidArrayOfHLAbyte16.toDcssUuid( pdu.getUuid(), requestId.getUuid() );
		
		// Time Offset
		this.weatherData.getTimeOffset().setValue( pdu.getTimeOffset() );
		
		// Location
		this.weatherData.getLocation().setAltitude( pdu.getAltitude() );
		this.weatherData.getLocation().setLatitude( pdu.getLatitude() );
		this.weatherData.getLocation().setLongitude( pdu.getLongitude() );
		
		// Domains
		Set<Domain> hlaDomains = new HashSet<Domain>();
		for( DcssWeatherDomain disDomain : pdu.getDomains() )
		{
			byte code = disDomain.getValue();
			Domain hlaDomain = Domain.valueOf( code );
			hlaDomains.add( hlaDomain );
		}
		this.weatherData.setDomains( hlaDomains );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		DcssWeatherRequestPdu pdu = new DcssWeatherRequestPdu();
		
		//
		// Instance ID
		//
		pdu.setInstanceId( this.instanceId.getValue() );
		
		//
		// Weather Data
		//
		// ID
		RequestIdentifier requestId = this.weatherData.getId();
		pdu.setUuid( requestId.getUuid().getDisValue() );
		pdu.setEntityId( requestId.getEntityIdentifier().getDisValue() );
		
		// Time Offset
		pdu.setTimeOffset( this.weatherData.getTimeOffset().getValue() );
		
		// Location
		GeoPoint3D location = this.weatherData.getLocation();
		pdu.setLatitude( location.getLatitude() );
		pdu.setLongitude( location.getLongitude() );
		pdu.setAltitude( location.getAltitude() );
		
		// Domains
		Set<DcssWeatherDomain> disDomains = new HashSet<>();
		for( EnumHolder<Domain> hlaDomain : this.weatherData.getDomains() )
		{
			byte code = hlaDomain.getEnum().getValue();
			DcssWeatherDomain disDomain = DcssWeatherDomain.valueOf( code );
			disDomains.add( disDomain );
		}
		
		pdu.setDomains( disDomains );

		return pdu;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAinteger64BE getInstanceId()
	{
		return this.instanceId;
	}
	
	public WeatherRequestData getWeatherData()
	{
		return this.weatherData;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
