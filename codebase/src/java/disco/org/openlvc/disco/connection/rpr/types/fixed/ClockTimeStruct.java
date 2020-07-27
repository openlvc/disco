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
package org.openlvc.disco.connection.rpr.types.fixed;

import org.openlvc.disco.connection.rpr.types.basic.HLAinteger32BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.pdu.record.ClockTime;

public class ClockTimeStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger32BE hours;
	private RPRunsignedInteger32BE timePastTheHour;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ClockTimeStruct()
	{
		this.hours = new HLAinteger32BE();
		this.timePastTheHour = new RPRunsignedInteger32BE();
		
		super.add( this.hours );
		super.add( this.timePastTheHour );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getHours()
	{
		return this.hours.getValue();
	}
	
	public void setHours( int hours )
	{
		this.hours.setValue( hours );
	}
	
	public long getTimePastHour()
	{
		return this.timePastTheHour.getValue();
	}
	
	public void setTimePastHour( long time )
	{
		this.timePastTheHour.setValue( time );
	}
	
	public ClockTime getDisValue()
	{
		return new ClockTime( this.hours.getValue(), 
		                      this.timePastTheHour.getValue() );
	}
	
	public void setDisValue( ClockTime value )
	{
		this.hours.setValue( value.getHours() );
		this.timePastTheHour.setValue( value.getTimePastTheHour() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
