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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.UuidArrayOfHLAbyte16;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint3D;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.field.DcssWeatherDomain;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;

public class DcssWeatherRequestPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long instanceId;
	private double timeOffset;
	private double[] lla;
	private Set<DcssWeatherDomain> domains;
	private UUID uuid;
	private EntityId entityId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DcssWeatherRequestPdu()
	{
		super( PduType.DcssWeatherRequest );
		this.instanceId = 0L;
		this.timeOffset = 0.0;
		this.lla = new double[3];
		this.domains = new HashSet<>();
		this.uuid = new UUID( 0L, 0L );
		this.entityId = new EntityId();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.entityId.from( dis );
		byte domainBitField = dis.readByte();
		this.setDomains( DcssWeatherDomain.fromBitField(domainBitField) );
		dis.skipBytes( 1 );
		this.instanceId = dis.readLong();
		this.timeOffset = dis.readDouble();
		GeoPoint3D.readDisGeopoint3D( dis, this.lla );
		this.uuid = UuidArrayOfHLAbyte16.readDisUuid( dis );
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.entityId.to( dos );
		dos.writeByte( DcssWeatherDomain.toBitField(this.domains) );
		dos.writePadding( 1 );
		dos.writeLong( this.instanceId );
		dos.writeDouble( this.timeOffset );
		GeoPoint3D.writeDisGeopoint3D( this.lla, dos );
		UuidArrayOfHLAbyte16.writeDisUuid( this.uuid, dos );
	}

	@Override
	public int getContentLength()
	{
		// EntityId:            6 bytes +
		// Domains (bit field): 1 byte +
		// (Padding):           1 byte +
		// InstanceId:          8 bytes +
		// TimeOffset:          8 bytes +
		// Latitude:            8 bytes +
		// Longitude:           8 bytes +
		// Altitude:            8 bytes +
		// UuidHi:              8 bytes +
		// UuidLo:              8 bytes +
		// -------------------------------
		//                     64 bytes
		
		return 64;
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
	
	public double getTimeOffset()
	{
		return this.timeOffset;
	}
	
	public void setTimeOffset( double timeOffset )
	{
		this.timeOffset = timeOffset;
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
	
	public Set<DcssWeatherDomain> getDomains()
	{
		return EnumSet.copyOf( this.domains );
	}
	
	public void setDomains( Collection<? extends DcssWeatherDomain> domains )
	{
		this.domains.clear();
		this.domains.addAll( domains );
	}
	
	public void setDomains( DcssWeatherDomain... domains )
	{
		this.domains.clear();
		for( DcssWeatherDomain domain : domains )
			this.domains.add( domain );
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
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
