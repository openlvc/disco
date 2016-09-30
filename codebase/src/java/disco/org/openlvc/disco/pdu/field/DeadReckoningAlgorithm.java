/*
 *   Copyright 2015 Open LVC Project.
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
import org.openlvc.disco.pdu.DisSizes;

public enum DeadReckoningAlgorithm
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other ( (short)0 ),
	Static( (short)1 ),
	FPW   ( (short)2 ),
	RPW   ( (short)3 ),
	RVW   ( (short)4 ),
	FVW   ( (short)5 ),
	FPB   ( (short)6 ),
	RPB   ( (short)7 ),
	RVB   ( (short)8 ),
	FVB   ( (short)9 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private DeadReckoningAlgorithm( short value )
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
	public static final int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static DeadReckoningAlgorithm fromValue( short value )
	{
		switch( value )
		{
			 case 2: return FPW;
			 case 4: return RVW;
			 case 1: return Static;
			 case 8: return RVB;
			 case 3: return RPW;
			 case 5: return FVW;
			 case 6: return FPB;
			 case 7: return RPB;
			 case 9: return FVB;
			default: // drop through
		}
		
		// Missing
		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" not a valid Dead Reckoning Algorithm" );
		else
			return Other;
	}

}
