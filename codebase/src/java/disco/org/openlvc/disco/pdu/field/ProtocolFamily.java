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

import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.utils.EnumLookup;

public enum ProtocolFamily
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other         ( 0 ),
	Entity        ( 1 ),
	Warfare       ( 2 ),
	Logistics     ( 3 ),
	Radio         ( 4 ),
	SimMgmt       ( 5 ),
	Emission      ( 6 ),
	EntityMgmt    ( 7 ),
	Minefield     ( 8 ),
	SyntheticEnv  ( 9 ),
	SimMgmt_R     ( 10 ),
	LiveEntity    ( 11 ),
	NonRealTime   ( 12 ),
	InformationOps( 13 ),
	// Custom
	DiscoCustom   ( 221 ); // 0xdd

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<ProtocolFamily> DISVALUE_LOOKUP = 
		new EnumLookup<>( ProtocolFamily.class, ProtocolFamily::value, ProtocolFamily.Other );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ProtocolFamily( int value )
	{
		this.value = (short)value;
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
		return DISVALUE_LOOKUP.fromValue( value );
	}
	
}
