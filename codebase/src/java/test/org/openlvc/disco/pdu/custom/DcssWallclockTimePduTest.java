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

import java.time.Month;
import java.util.Calendar;
import java.util.TimeZone;

import org.openlvc.disco.AbstractTest;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.ClockStateEnum;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.pdu.record.ClockTime;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups= {"pdu","custom","dcss"})
public class DcssWallclockTimePduTest extends AbstractTest
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
	public void testDcssWallclockTimePduPduSerialize() throws Exception
	{
		DcssWallclockTimePdu before = new DcssWallclockTimePdu();
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		
		// Zulu Time
		calendar.set( 2020, Month.AUGUST.getValue()-1, 13, 14, 15, 16 );
		long zuluTimeMillis = calendar.getTimeInMillis();
		before.setZuluTime( ClockTime.fromJavaTime(zuluTimeMillis) );
		
		// Simulation Time
		calendar.set( 2010, Month.JULY.getValue()-1, 8, 19, 20, 21 );
		long simTimeMillis = calendar.getTimeInMillis();
		before.setSimulationTime( ClockTime.fromJavaTime(simTimeMillis) );
		
		// ElapsedTime - 1hr 43m 23s 567ms
		long elapsedTime = 1*60*60*1000 + 43*60*1000 + 23*1000 + 567;
		before.setSimulationElapsedTime( elapsedTime );
		
		// Scaling Factor
		before.setScalingFactor( 1.0f );

		// Simulation Start Time (FOM says clock time at start of simulation, but doesn't say which clock!
		// assuming its the wallclock, as you could easily interpolate the simulation clock start time
		// by SimulationTime - ElapsedTime. 
		long startTimeMillis = zuluTimeMillis - elapsedTime;
		before.setSimulationStartTime( ClockTime.fromJavaTime(startTimeMillis) );
		
		before.setClockState( ClockStateEnum.Running.getValue() );
		
		// turn the PDU into a byte[]
		byte[] beforeArray = before.toByteArray();
		
		// convert it back
		DcssWallclockTimePdu after = PduFactory.create( beforeArray );
		
		// compare the pair!
		Assert.assertEquals( after.getZuluTime(),              before.getZuluTime() );
		Assert.assertEquals( after.getSimulationTime(),        before.getSimulationTime() );
		Assert.assertEquals( after.getSimulationElapsedTime(), before.getSimulationElapsedTime() );
		Assert.assertEquals( after.getSimulationStartTime(),   before.getSimulationStartTime() );
		Assert.assertEquals( after.getClockState(),            before.getClockState() );
	}
	
	@Test
	public void testToFromJavaTime()
	{
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		calendar.set( 1980, Month.AUGUST.getValue()-1, 13, 14, 15, 16 );
		long epochMillis = calendar.getTimeInMillis();
		
		ClockTime clockTime = ClockTime.fromJavaTime( epochMillis );
		Assert.assertEquals( ClockTime.toJavaTime(clockTime), epochMillis );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
