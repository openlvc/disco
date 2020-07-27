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
package org.openlvc.disco.pdu.record;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

public class ClockTime implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int hours;
	private long timePastTheHour; 

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ClockTime()
	{
		this( 0, 0L );
	}
	
	public ClockTime( int hours, long timePastTheHour )
	{
		this.hours = hours;
		this.timePastTheHour = timePastTheHour;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public ClockTime clone()
	{
		return new ClockTime( this.hours, this.timePastTheHour );
	}
	
	@Override
	public boolean equals( Object other )
	{
		boolean equal = false;
		if( other instanceof ClockTime )
		{
			ClockTime asClock = (ClockTime)other;
			equal = asClock.hours == this.hours && 
			        asClock.timePastTheHour == this.timePastTheHour;
		}
		
		return equal;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.hours = dis.readInt();
		this.timePastTheHour = dis.readUI32();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeInt( this.hours );
		dos.writeUI32( this.timePastTheHour );
	}

	@Override
	public final int getByteLength()
	{
		return 8;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getHours()
	{
		return this.hours;
	}
	
	public void setHours( int hours )
	{
		this.hours = hours;
	}
	
	public long getTimePastTheHour()
	{
		return this.timePastTheHour;
	}
	
	public void setTimePastTheHour( long timePastTheHour )
	{
		this.timePastTheHour = timePastTheHour;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static ClockTime fromJavaTime( long time )
	{
		final long RANGE = 0xFFFFFFFFL;
		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		cal.setTimeInMillis( time );
		Instant instant = cal.toInstant();
		long epochSeconds = instant.getEpochSecond();
		double epochHours = epochSeconds / 60.0;
		int epochHourIntegral = (int)Math.floor( epochHours );
		double timePastHourFraction = epochHours - epochHourIntegral;
		long timePastHour = Math.round( RANGE * timePastHourFraction );
		
		return new ClockTime( epochHourIntegral, timePastHour );
	}
}
