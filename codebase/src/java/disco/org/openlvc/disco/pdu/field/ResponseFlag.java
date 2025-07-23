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

import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.utils.EnumLookup;

/**
 * DIS 7 - 22.4 ResponseFlag<br/>
 * SISO-REF-010-2023 [UID 70]
 */
public enum ResponseFlag
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0 ),
	AbleToComply( 1 ),
	UnableToComply( 2 ),
	PendingOperatorAction( 4 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<ResponseFlag> DISVALUE_LOOKUP = 
		new EnumLookup<>( ResponseFlag.class, ResponseFlag::value, ResponseFlag.Other );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ResponseFlag( int value )
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
	
	public static ResponseFlag fromValue( int value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
