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

public enum MarkingCharSet
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Unused( (short)0 ),
	ASCII( (short)1 ),
	ArmyMarking( (short)2 ),
	DigitChevron( (short)3 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private MarkingCharSet( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int getValue()
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

	public static MarkingCharSet fromValue( short value )
	{
		if( value == ASCII.value ) return ASCII;
		if( value == Unused.value ) return Unused;
		if( value == ArmyMarking.value ) return ArmyMarking;
		if( value == DigitChevron.value ) return DigitChevron;
		else throw new IllegalArgumentException( value+" is not a valid ForceId" );
	}
	
}
