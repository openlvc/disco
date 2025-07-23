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
package org.openlvc.disco.pdu.field.appearance.enums;

import org.openlvc.disco.utils.EnumLookup;

/**
 * Hatch state appearance values.
 * 
 * @see "SISO-REF-10 [UID 382]"
 */
public enum HatchState
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	NotApplicable         ( (byte)0 ),
	Closed                ( (byte)1 ),
	Popped                ( (byte)2 ),
	PoppedAndPersonVisible( (byte)3 ),
	Open                  ( (byte)4 ),
	OpenAndPersonVisible  ( (byte)5 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<HatchState> DISVALUE_LOOKUP = 
		new EnumLookup<>( HatchState.class, HatchState::value, HatchState.NotApplicable );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private HatchState( byte value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public byte value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static HatchState fromValue( byte value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
