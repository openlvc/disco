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

public enum ProtocolFamily
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other        ( (short)0 ),
	Entity       ( (short)1 ),
	Warfare      ( (short)2 ),
	Logistics    ( (short)3 ),
	Radio        ( (short)4 ),
	SimManagement( (short)5 ),
	Emission     ( (short)6 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ProtocolFamily( short value )
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

	public static ProtocolFamily fromValue( short value )
	{
		switch( value )
		{
			 case 1: return Entity;
			 case 2: return Warfare;
			 case 3: return Logistics;
			 case 4: return Radio;
			 case 5: return SimManagement;
			 case 6: return Emission;
			default: // drop through
		}

		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" is not a valid value for ProtocolFamily" );
		else
			return Other;
	}
	
}
