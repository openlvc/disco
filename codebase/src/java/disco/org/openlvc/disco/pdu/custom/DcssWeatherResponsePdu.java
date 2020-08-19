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
package org.openlvc.disco.pdu.custom;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.UuidArrayOfHLAbyte16;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.DateTimeStruct;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint3D;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;

public class DcssWeatherResponsePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long instanceId;
	private Date dateTime;
	private double timeOffset;
	private double[] lla;
	private byte weatherResponseType;
	private UUID uuid;
	private EntityId entityId;
	
	private float precipitationRate;
	private float temperature;
	private float humidity;
	private float pressure;
	
	private float totalCloudCover;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DcssWeatherResponsePdu()
	{
		super( PduType.DcssWeatherResponse );
		this.instanceId = 0L;
		this.dateTime = new Date( 0L );
		this.timeOffset = 0.0;
		this.lla = new double[3];
		this.weatherResponseType = 0;
		this.uuid = new UUID( 0L, 0L );
		this.entityId = new EntityId();
		
		this.precipitationRate = 0.0f;
		this.temperature = 0.0f;
		this.humidity = 0.0f;
		this.pressure = 0.0f;
		
		this.totalCloudCover = 0.0f;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.entityId.from( dis );
		this.weatherResponseType = dis.readByte();
		dis.skipBytes( 1 );
		this.instanceId = dis.readLong();
		this.dateTime = DateTimeStruct.readDisDateTime( dis );
		this.timeOffset = dis.readDouble();
		GeoPoint3D.readDisGeopoint3D( dis, this.lla );
		this.uuid = UuidArrayOfHLAbyte16.readDisUuid( dis );
		
		this.precipitationRate = dis.readFloat();
		this.temperature = dis.readFloat();
		this.humidity = dis.readFloat();
		this.pressure = dis.readFloat();
		
		this.totalCloudCover = dis.readFloat();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.entityId.to( dos );
		dos.writeByte( this.weatherResponseType );
		dos.writePadding( 1 );
		dos.writeLong( this.instanceId );
		DateTimeStruct.writeDisDateTime( this.dateTime, dos );
		dos.writeDouble( this.timeOffset );
		GeoPoint3D.writeDisGeopoint3D( this.lla, dos );
		UuidArrayOfHLAbyte16.writeDisUuid( this.uuid, dos );
		
		dos.writeFloat( this.precipitationRate );
		dos.writeFloat( this.temperature );
		dos.writeFloat( this.humidity );
		dos.writeFloat( this.pressure );
		
		dos.writeFloat( this.totalCloudCover );
	}

	@Override
	public int getContentLength()
	{
		// EntityId:           6 bytes +
		// WeatherResponeType: 1 byte +
		// (Padding):          1 byte +
		// InstanceId:         8 bytes +
		// DateTime:           8 bytes +
		// TimeOffset:         8 bytes +
		// Latitude:           8 bytes +
		// Longitude:          8 bytes +
		// Altitude:           8 bytes +
		// UuidHi:             8 bytes +
		// UuidLo:             8 bytes +
		// PrecipitationRate:  4 bytes +
		// Temperature:        4 bytes +
		// Humidity:           4 bytes +
		// Pressure:           4 bytes +
		// TotalCloudCover:    4 bytes
		// --------------------------
		//                     92 bytes
		
		return 72;
	}

	@Override
	public int getSiteId()
	{
		return this.entityId.getSiteId();
	}

	@Override
	public int getAppId()
	{
		return this.entityId.getAppId();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getInstanceId()
	{
		return this.instanceId;
	}
	
	public void setInstanceId( long id )
	{
		this.instanceId = id;
	}
	
	public Date getDateTime()
	{
		return this.dateTime;
	}
	
	public void setDateTime( Date dateTime )
	{
		this.dateTime = dateTime;
	}
	
	public double getTimeOffset()
	{
		return this.timeOffset;
	}
	
	public void setTimeOffset( double offset )
	{
		this.timeOffset = offset;
	}
	
	public double getLatitude()
	{
		return this.lla[0];
	}
	
	public void setLatitude( double lat )
	{
		this.lla[0] = lat;
	}
	
	public double getLongitude()
	{
		return this.lla[1];
	}
	
	public void setLongitude( double lon )
	{
		this.lla[1] = lon;
	}
	
	public double getAltitude()
	{
		return this.lla[2];
	}
	
	public void setAltitude( double alt )
	{
		this.lla[2] = alt;
	}
	
	public byte getWeatherResponseType()
	{
		return this.weatherResponseType;
	}
	
	public void setWeatherResponseType( byte type )
	{
		this.weatherResponseType = type;
	}
	
	public UUID getUuid()
	{
		return this.uuid;
	}
	
	public void setUuid( UUID uuid )
	{
		this.uuid = uuid;
	}
	
	public EntityId getEntityId()
	{
		return this.entityId;
	}
	
	public void setEntityId( EntityId id )
	{
		this.entityId = id;
	}
	
	public float getPrecipitationRate()
	{
		return this.precipitationRate;
	}
	
	public void setPrecipitationRate( float rate )
	{
		this.precipitationRate = rate;
	}

	public float getTemperature()
	{
		return temperature;
	}

	public void setTemperature( float temperature )
	{
		this.temperature = temperature;
	}

	public float getHumidity()
	{
		return humidity;
	}

	public void setHumidity( float humidity )
	{
		this.humidity = humidity;
	}

	public float getPressure()
	{
		return pressure;
	}

	public void setPressure( float pressure )
	{
		this.pressure = pressure;
	}

	public float getTotalCloudCover()
	{
		return totalCloudCover;
	}

	public void setTotalCloudCover( float totalCloudCover )
	{
		this.totalCloudCover = totalCloudCover;
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
