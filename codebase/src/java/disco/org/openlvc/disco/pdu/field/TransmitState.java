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

public enum TransmitState
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Off                 ( 0 ),
	OnButNotTransmitting( 1 ),
	OnAndTransmitting   ( 2 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<TransmitState> DISVALUE_LOOKUP = 
		new EnumLookup<>( TransmitState.class, TransmitState::value, TransmitState.Off );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private TransmitState( int value )
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
	
	public static TransmitState fromValue( short value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
