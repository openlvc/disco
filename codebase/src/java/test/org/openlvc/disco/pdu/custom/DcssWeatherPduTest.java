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

import static org.testng.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.WeatherType;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.pdu.record.EntityId;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups= {"pdu","custom","dcss"})
public class DcssWeatherPduTest extends AbstractTest
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	///////////////////////////////////////////////////////////////////////////////////
	/// Test Class Setup/Tear Down   //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@BeforeClass(alwaysRun=true)
	public void beforeClass()
	{
	}
	
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod()
	{
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod()
	{
	}
	
	@AfterClass(alwaysRun=true)
	public void afterClass()
	{
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Testing Methods   /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testDcssWeatherRequestPduSerialize() throws Exception
	{
		long instanceId = 1337;
		double lat = -37.5622;
		double lon = 143.8503;
		double alt = 435.0;
		
		byte requestType = WeatherType.Ground.getValue();
		UUID uuid = UUID.randomUUID();
		EntityId selfId = new EntityId( 1, 2, 3 );
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		calendar.clear();
		calendar.set( Calendar.YEAR, 2020 );
		calendar.set( Calendar.MONTH, Calendar.AUGUST );
		calendar.set( Calendar.DAY_OF_MONTH, 13 );
		calendar.set( Calendar.HOUR, 16 );
		calendar.set( Calendar.MINUTE, 30 );
		calendar.set( Calendar.SECOND, 15 );
		Date time = calendar.getTime();
		
		DcssWeatherRequestPdu before = new DcssWeatherRequestPdu();
		before.setAltitude( alt );
		before.setDateTime( time );
		before.setEntityId( selfId );
		before.setInstanceId( instanceId );
		before.setLatitude( lat );
		before.setLongitude( lon );
		before.setUuid( uuid );
		before.setWeatherReqType( requestType );
		
		// turn the PDU into a byte[]
		byte[] beforeArray = before.toByteArray();
		
		// convert it back
		DcssWeatherRequestPdu after = PduFactory.create( beforeArray );
		
		assertEquals( after.getAltitude(), alt );
		assertEquals( after.getDateTime(), time );
		assertEquals( after.getEntityId(), selfId );
		assertEquals( after.getInstanceId(), instanceId );
		assertEquals( after.getLatitude(), lat );
		assertEquals( after.getLongitude(), lon );
		assertEquals( after.getUuid(), uuid );
		assertEquals( after.getWeatherReqType(), requestType );
	}

	@Test
	public void testDcssWeatherResponsePduSerialize() throws Exception
	{
		long instanceId = 1337;
		double lat = -37.5622;
		double lon = 143.8503;
		double alt = 435.0;
		double offset = 10;
		float precipitationRate = 5.0f;
		float temperature = 25.0f;
		float humidity = 0.5f;
		float pressure = 1013f;
		float totalCloudCover = 0.3f;
		
		byte responseType = WeatherType.Ground.getValue();
		UUID uuid = UUID.randomUUID();
		EntityId selfId = new EntityId( 1, 2, 3 );
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		calendar.clear();
		calendar.set( Calendar.YEAR, 2020 );
		calendar.set( Calendar.MONTH, Calendar.AUGUST );
		calendar.set( Calendar.DAY_OF_MONTH, 13 );
		calendar.set( Calendar.HOUR, 16 );
		calendar.set( Calendar.MINUTE, 30 );
		calendar.set( Calendar.SECOND, 15 );
		Date time = calendar.getTime();
		
		DcssWeatherResponsePdu before = new DcssWeatherResponsePdu();
		before.setAltitude( alt );
		before.setDateTime( time );
		before.setEntityId( selfId );
		before.setHumidity( humidity );
		before.setInstanceId( instanceId );
		before.setLatitude( lat );
		before.setLongitude( lon );
		before.setPrecipitationRate( precipitationRate );
		before.setPressure( pressure );
		before.setTemperature( temperature );
		before.setTimeOffset( offset );
		before.setTotalCloudCover( totalCloudCover );
		before.setUuid( uuid );
		before.setWeatherResponseType( responseType );
		
		// turn the PDU into a byte[]
		byte[] beforeArray = before.toByteArray();
		
		// convert it back
		DcssWeatherResponsePdu after = PduFactory.create( beforeArray );
		
		assertEquals( after.getAltitude(), alt );
		assertEquals( after.getDateTime(), time );
		assertEquals( after.getEntityId(), selfId );
		assertEquals( after.getHumidity(), humidity );
		assertEquals( after.getInstanceId(), instanceId );
		assertEquals( after.getLatitude(), lat );
		assertEquals( after.getLongitude(), lon );
		assertEquals( after.getPrecipitationRate(), precipitationRate );
		assertEquals( after.getPressure(), pressure );
		assertEquals( after.getTemperature(), temperature );
		assertEquals( after.getTotalCloudCover(), totalCloudCover );
		assertEquals( after.getUuid(), uuid );
		assertEquals( after.getWeatherResponseType(), responseType );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
