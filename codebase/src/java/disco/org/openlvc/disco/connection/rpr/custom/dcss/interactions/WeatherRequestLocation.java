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

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.UuidArrayOfHLAbyte16;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.WeatherType;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.DateTimeStruct;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint3D;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.types.basic.HLAinteger64BE;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.DcssWeatherRequestPdu;

public class WeatherRequestLocation extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger64BE instanceId;
	private DateTimeStruct dateTime;
	private GeoPoint3D location;
	private WeatherType weatherReqType;
	private UuidArrayOfHLAbyte16 uuid;
	private EntityIdentifierStruct entityId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherRequestLocation()
	{
		super();
		this.instanceId = new HLAinteger64BE();
		this.dateTime = new DateTimeStruct();
		this.location = new GeoPoint3D();
		this.weatherReqType = WeatherType.Atmospheric;
		this.uuid = new UuidArrayOfHLAbyte16();
		this.entityId = new EntityIdentifierStruct();
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
		this.instanceId.setValue( pdu.getInstanceId() );
		DateTimeStruct.toDcssDateTime( pdu.getDateTime(), this.dateTime );
		this.location.setLatitude( pdu.getLatitude() );
		this.location.setLongitude( pdu.getLongitude() );
		this.location.setAltitude( pdu.getAltitude() );
		this.weatherReqType = WeatherType.valueOf( pdu.getWeatherReqType() );
		UuidArrayOfHLAbyte16.toDcssUuid( pdu.getUuid(), this.uuid );
		this.entityId.setValue( pdu.getEntityId() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		DcssWeatherRequestPdu pdu = new DcssWeatherRequestPdu();
		pdu.setAltitude( this.location.getAltitude() );
		pdu.setDateTime( this.dateTime.getDisValue() );
		pdu.setEntityId( this.entityId.getDisValue() );
		pdu.setInstanceId( this.instanceId.getValue() );
		pdu.setLatitude( this.location.getLatitude() );
		pdu.setLongitude( this.location.getLongitude() );
		pdu.setUuid( this.uuid.getDisValue() );
		pdu.setWeatherReqType( this.weatherReqType.getValue() );

		return pdu;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAinteger64BE getInstanceId()
	{
		return this.instanceId;
	}
	
	public DateTimeStruct getDateTime()
	{
		return this.dateTime;
	}
	
	public GeoPoint3D getLocation()
	{
		return this.location;
	}
	
	public WeatherType getWeatherReqType()
	{
		return this.weatherReqType;
	}
	
	public UuidArrayOfHLAbyte16 getUuid()
	{
		return this.uuid;
	}
	
	public EntityIdentifierStruct getEntityId()
	{
		return this.entityId;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
