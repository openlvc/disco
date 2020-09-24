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
package org.openlvc.disco.connection.rpr.custom.dcss.objects;

import java.util.HashSet;
import java.util.Set;

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.ArrayOfDomain;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.Domain;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.GeoAreaType;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.WeatherInstanceType;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.DateTimeStruct;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoArea;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GeoPoint2D;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.types.EncoderFactory;
import org.openlvc.disco.connection.rpr.types.basic.HLAinteger64BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.HLAboolean;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.DcssWeatherInstancePdu;
import org.openlvc.disco.pdu.custom.field.DcssWeatherDomain;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.encoding.HLAASCIIstring;

public class WeatherInstance extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger64BE id;
	private HLAASCIIstring name;
	private HLAASCIIstring description;
	private EnumHolder<WeatherInstanceType> instanceType;
	private GeoArea geoArea;
	private EnumHolder<GeoAreaType> geoAreaType;
	private DateTimeStruct startTime;
	private DateTimeStruct endTime;
	private ArrayOfDomain domains;
	private HLAboolean active;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherInstance()
	{
		this.id = new HLAinteger64BE();
		this.name = EncoderFactory.createHLAASCIIstring();
		this.description = EncoderFactory.createHLAASCIIstring();
		this.instanceType = new EnumHolder<WeatherInstanceType>( WeatherInstanceType.InvalidInstanceType );
		this.geoArea = new GeoArea();
		this.geoAreaType = new EnumHolder<GeoAreaType>( GeoAreaType.Polygon );
		this.startTime = new DateTimeStruct();
		this.endTime = new DateTimeStruct();
		this.domains = new ArrayOfDomain();
		this.active = new HLAboolean( false );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	protected boolean checkReady()
	{
		return id.getValue() != 0 && 
		       geoArea.isDecodeCalled() && 
		       startTime.isDecodeCalled() && 
		       endTime.isDecodeCalled() && 
		       domains.isDecodeCalled();
	}
	
	@Override
	public EntityId getDisId()
	{
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		throw new UnsupportedOperationException( "DcssWeatherInstancePDU -> HLA object not supported" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		DcssWeatherInstancePdu pdu = new DcssWeatherInstancePdu();
		GeoPoint2D lowerLeft = geoArea.getLowerLeft();
		GeoPoint2D upperRight = geoArea.getUpperRight();
		
		pdu.setId( this.id.getValue() );
		pdu.setInstanceType( this.instanceType.getEnum().getValue() );
		pdu.setLowerLeftLatLon( lowerLeft.getLatitude(), lowerLeft.getLongitude() );
		pdu.setUpperRightLatLon( upperRight.getLatitude(), upperRight.getLongitude() );
		pdu.setGeoAreaType( this.geoAreaType.getEnum().getValue() );
		pdu.setStartTime( this.startTime.getDisValue() );
		pdu.setEndTime( this.endTime.getDisValue() );
		
		Set<DcssWeatherDomain> disDomains = new HashSet<>();
		for( EnumHolder<Domain> hlaDomain : this.domains )
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
	public HLAinteger64BE getId() { return this.id; }
	public HLAASCIIstring getName() { return this.name; }
	public HLAASCIIstring getDescription() { return this.description; }
	public EnumHolder<WeatherInstanceType> getInstanceType() { return this.instanceType; }
	public GeoArea getGeoArea() { return this.geoArea; }
	public EnumHolder<GeoAreaType> getGeoAreaType() { return this.geoAreaType; }
	public DateTimeStruct getStartTime() { return this.startTime; }
	public DateTimeStruct getEndTime() { return this.endTime; }
	public ArrayOfDomain getDomains() { return this.domains; }
	public HLAboolean getActive() { return this.active; }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
