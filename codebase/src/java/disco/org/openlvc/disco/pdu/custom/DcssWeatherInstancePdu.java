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
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.DateTimeStruct;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint2D;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.field.DcssWeatherDomain;
import org.openlvc.disco.pdu.field.PduType;

public class DcssWeatherInstancePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long id;
	private byte instanceType;
	private double[] lowerLeftLatLon;
	private double[] upperRightLatLon;
	private byte geoAreaType;
	private Date startTime;
	private Date endTime;
	private Set<DcssWeatherDomain> domains;
	private boolean active;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DcssWeatherInstancePdu()
	{
		super( PduType.DcssWeatherInstance );
		this.id = 0;
		this.instanceType = 0;
		this.lowerLeftLatLon = new double[2];
		this.upperRightLatLon = new double[2];
		this.geoAreaType = (byte)0;
		this.startTime = new Date( 0L );
		this.endTime = new Date( 0L );
		this.domains = new HashSet<>();
		this.active = false;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.id = dis.readLong();
		this.instanceType = dis.readByte();
		this.geoAreaType = dis.readByte();
		this.domains = DcssWeatherDomain.fromBitField( dis.readByte() );
		this.active = dis.readByte() != 0;
		GeoPoint2D.readDisGeopoint2D( dis, this.lowerLeftLatLon  );
		GeoPoint2D.readDisGeopoint2D( dis, this.upperRightLatLon  );
		this.startTime = DateTimeStruct.readDisDateTime( dis );
		this.endTime = DateTimeStruct.readDisDateTime( dis );
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeLong( this.id );
		dos.writeByte( this.instanceType );
		dos.writeByte( this.geoAreaType );
		dos.writeByte( DcssWeatherDomain.toBitField(this.domains) );
		dos.writeByte( this.active ? 1 : 0 );
		GeoPoint2D.writeDisGeopoint2D( this.lowerLeftLatLon, dos );
		GeoPoint2D.writeDisGeopoint2D( this.upperRightLatLon, dos );
		DateTimeStruct.writeDisDateTime( this.startTime, dos );
		DateTimeStruct.writeDisDateTime( this.endTime, dos );
	}

	@Override
	public int getContentLength()
	{
		// long id                          8 bytes
		// byte instanceType                1 byte
		// byte geoAreaType                 1 byte
		// Set<DcssWeatherDomain> domains   1 byte
		// boolean active                   1 byte
		// GeoPoint2D lowerLeftLatLon      16 bytes
		// GeoPoint2D upperRightLatLon     16 bytes
		// Date startTime                   8 bytes
		// Date endTime                     8 bytes
		// ----------------------------------------
		//                                 60 bytes
		return 60;
	}

	@Override
	public int getSiteId()
	{
		return 0;
	}

	@Override
	public int getAppId()
	{
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getId() 
	{
		return this.id;
	}
	
	public void setId( long id )
	{
		this.id = id;
	}
	
	public byte getInstanceType()
	{
		return this.instanceType;
	}
	
	public void setInstanceType( byte instanceType )
	{
		this.instanceType = instanceType;
	}
	
	public double getLowerLeftLatitude() 
	{
		return this.lowerLeftLatLon[0];
	}
	
	public double getLowerLeftLongitude()
	{
		return this.lowerLeftLatLon[1];
	}
	
	public void setLowerLeftLatLon( double lat, double lon )
	{
		this.lowerLeftLatLon[0] = lat;
		this.lowerLeftLatLon[1] = lon;
	}
	
	public double getUpperRightLatitude()
	{
		return this.upperRightLatLon[0];
	}
	
	public double getUpperRightLongitude()
	{
		return this.upperRightLatLon[1];
	}
	
	public void setUpperRightLatLon( double lat, double lon )
	{
		this.upperRightLatLon[0] = lat;
		this.upperRightLatLon[1] = lon;
	}
	
	public byte getGeoAreaType()
	{
		return this.geoAreaType;
	}
	
	public void setGeoAreaType( byte type )
	{
		this.geoAreaType = type;
	}
	
	public Date getStartTime()
	{
		return this.startTime;
	}
	
	public void setStartTime( Date startTime )
	{
		this.startTime = startTime;
	}
	
	public Date getEndTime()
	{
		return this.endTime;
	}
	
	public void setEndTime( Date endTime )
	{
		this.endTime = endTime;
	}
	
	public Set<DcssWeatherDomain> getDomains() 
	{ 
		return EnumSet.copyOf( this.domains ); 
	}
	
	public void setDomains( Collection<? extends DcssWeatherDomain> domains )
	{
		this.domains.clear();
		this.domains.addAll( domains );
	}
	
	public boolean isActive()
	{ 
		return this.active;
	}
	
	public void setActive( boolean active )
	{
		this.active = active;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
