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
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;

public enum ProtocolVersion
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other   ( (short)0 ),
	Version1( (short)1 ),    // DIS PDU version 1.0 (May 92)
	Version2( (short)2 ),    // IEEE 1278-1993
	Version3( (short)3 ),    // DIS PDU version 2.0 - third draft (May 93)
	Version4( (short)4 ),    // DIS PDU version 2.0 - fourth draft (revised) March 16, 1994
	Version5( (short)5 ),    // IEEE 1278.1-1995
	Version6( (short)6 ),    // IEEE 1278.1A-1998
	Version7( (short)7 );    // IEEE 1278.1-2012
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ProtocolVersion( short value )
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

	public static ProtocolVersion fromValue( short value )
	{
		switch( value )
		{
			case 7: return Version7;
			case 6: return Version6;
			case 5: return Version5;
			case 4: return Version4;
			case 3: return Version3;
			case 2: return Version2;
			case 1: return Version1;
			default: break;
		}

		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid Protocol Version number" );
		else
			return Other;
	}
}
