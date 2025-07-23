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
 * IED presence appearance values for objects [UID 411]
 * 
 * @see "SISO-REF-10 s17.11.3.26"
 */
public enum IedPresent
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	None              ( 0 ),
	Visible           ( 1 ),
	PartiallyHidden   ( 2 ),
	CompletelyHidden  ( 3 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<IedPresent> DISVALUE_LOOKUP = 
		new EnumLookup<>( IedPresent.class, IedPresent::value );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private IedPresent( int value )
	{
		this.value = (byte)value;
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
	public static IedPresent fromValue( byte value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
