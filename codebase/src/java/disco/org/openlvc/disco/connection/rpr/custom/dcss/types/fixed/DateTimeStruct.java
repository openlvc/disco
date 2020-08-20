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
package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.openlvc.disco.connection.rpr.types.basic.HLAinteger16BE;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;

public class DateTimeStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger16BE year;
	private HLAinteger16BE month;
	private HLAinteger16BE day;
	private HLAinteger16BE hour;
	private HLAinteger16BE minute;
	private HLAinteger16BE second;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DateTimeStruct()
	{
		super();
		this.year = new HLAinteger16BE();
		this.month = new HLAinteger16BE();
		this.day = new HLAinteger16BE();
		this.hour = new HLAinteger16BE();
		this.minute = new HLAinteger16BE();
		this.second = new HLAinteger16BE();
		
		this.add( this.year, 
		          this.month, 
		          this.day, 
		          this.hour, 
		          this.minute, 
		          this.second );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public Date getDisValue()
	{
		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		cal.clear();
		
		cal.set( Calendar.YEAR, this.year.getValue() );
		cal.set( Calendar.MONTH, this.month.getValue()-1 );
		cal.set( Calendar.DAY_OF_MONTH, this.day.getValue() );
		cal.set( Calendar.HOUR_OF_DAY, this.hour.getValue() );
		cal.set( Calendar.MINUTE, this.minute.getValue() );
		cal.set( Calendar.SECOND, this.second.getValue() );
		
		return cal.getTime();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Date readDisDateTime( DisInputStream dis ) throws IOException
	{
		long time = dis.readLong();
		return new Date( time );
	}
	
	public static void writeDisDateTime( Date dateTime, DisOutputStream dos ) throws IOException
	{
		long time = dateTime.getTime();
		dos.writeLong( time );
	}
	
	public static void toDcssDateTime( Date src, DateTimeStruct dst )
	{
		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone("UTC") );
		cal.setTime( src );
		
		dst.year.setValue( (short)cal.get(Calendar.YEAR) );
		dst.month.setValue( (short)(cal.get(Calendar.MONTH)+1) ); // Month is zero-based
		dst.day.setValue( (short)cal.get(Calendar.DAY_OF_MONTH) );
		dst.hour.setValue( (short)cal.get(Calendar.HOUR_OF_DAY) ); // 24-hour format
		dst.minute.setValue( (short)cal.get(Calendar.MINUTE) );
		dst.second.setValue( (short)cal.get(Calendar.SECOND) );
	}
}
