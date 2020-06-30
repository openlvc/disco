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
package org.openlvc.disco.pdu.field;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;

public enum RequestStatus
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0 ),
	Pending( 1 ),
	Executing( 2 ),
	PartiallyComplete( 3 ),
	Complete( 4 ),
	RequestRejected( 5 ),
	RetransmitRequestNow( 6 ),
	RetransmitRequestLater( 7 ),
	InvalidTimeParameters( 8 ),
	SimulationTimeExceeded( 9 ),
	RequestDone( 10 ),
	TaccsfLosReplyType1( 100 ),
	TaccsfLosReplyType2( 101 ),
	JointExerciseRequestRejected( 201 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long value;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private RequestStatus( long value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public long value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI32_SIZE;
	}
	
	public static RequestStatus fromValue( long value )
	{
		for( RequestStatus status : RequestStatus.values() )
		{
			if( status.value == value )
				return status;
		}

		// Missing
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid RequestStatus" );
		else
			return Other;
	}
}
