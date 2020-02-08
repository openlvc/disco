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

public enum InputSource
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other            ( (short)0 ),
	Pilot            ( (short)1 ),
	Copilot          ( (short)2 ),
	FirstOfficer     ( (short)3 ),
	Driver           ( (short)4 ),
	Loader           ( (short)5 ),
	Gunner           ( (short)6 ),
	Commander        ( (short)7 ),
	DigitalDataDevice( (short)8 ),
	Intercom         ( (short)9 ),
	AudioJammer      ( (short)10 ),
	DataJammer       ( (short)11 ),
	GpsJammer        ( (short)12 ),
	GpsMeaconer      ( (short)13 );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private InputSource( short value )
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
	
	public static InputSource fromValue( short value )
	{
		for( InputSource source : values() )
			if( source.value == value )
				return source;

		if( DiscoConfiguration.STRICT_MODE )		
			throw new IllegalArgumentException( value+" is not a valid value for InputSource" );
		else
			return Other;
	}
}
