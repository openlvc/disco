/*
 *   Copyright 2025 Open LVC Project.
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

public enum StopFreezeReason
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( (short)0 ),
	Recess( (short)1 ),
	Termination( (short)2 ),
	SystemFailure( (short)3 ),
	SecurityViolation( (short)4 ),
	EntityReconstitution( (short)5 ),
	StopForReset( (short)6 ),
	StopForRestart( (short)7 ),
	AbortAndReturnToTacticalOperations( (short)8 );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private StopFreezeReason( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}
	
	public static StopFreezeReason fromValue( short value )
	{
		for( StopFreezeReason reason : values() )
		{
			if( reason.value == value )
				return reason;
		}

		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" is not a valid value for StopFreezeReason" );
		else
			return Other;
	}
}
