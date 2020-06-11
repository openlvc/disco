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
import org.openlvc.disco.pdu.radio.TransmitterPdu;

/**
 * This field shall specify the interpretation of the modulation parameter field(s) in the 
 * {@link TransmitterPdu}
 * 
 * @see "Section 9 in EBV-DOC"
 */
public enum ModulationSystem
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other             ( 0 ),
	Generic           ( 1 ),
	Hq                ( 2 ),
	HqII              ( 3 ),
	HqIIa             ( 4 ),
	Sincgars          ( 5 ),
	CcttSincgars      ( 6 ),
	Eplrs             ( 7 ),
	JtidsMids         ( 8 ),
	Link11            ( 9 ),
	Link11b           ( 10 ),
	LbandSatcom       ( 11 ),
	EnhancedSincgars73( 12 ),
	NavigationAid     ( 13 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ModulationSystem( int value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI16_SIZE;
	}
	
	public static ModulationSystem fromValue( int value )
	{
		switch( value )
		{
			case 0: return Other;
			case 1: return Generic;
			case 2: return Hq;
			case 3: return HqII;
			case 4: return HqIIa;
			case 5: return Sincgars;
			case 6: return CcttSincgars;
			case 7: return Eplrs;
			case 8: return JtidsMids;
			case 9: return Link11;
			case 10: return Link11b;
			case 11: return LbandSatcom;
			case 12: return EnhancedSincgars73;
			case 13: return NavigationAid;
			default: // drop through
		}
		
		// Missing
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid ModulationSystem" );
		else
			return Other;
	}
}
