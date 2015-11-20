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

/**
 * This field shall specify the TDL Type as a 16-bit enumeration field when the 
 * encoding class is the raw binary, audio, application - specific, or database 
 * index representation of a TDL Message. When the data field is not 
 * representing a TDL Message, this field shall be set to zero (see Section 9 
 * in EBV-DOC for enumeration of the TDL Type field).
 * 
 * @see "Section 9 in EBV-DOC"
 */
public enum TdlType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other         ( 0 ),
	AbbreviatedC2 ( 15 ),
	AIS           ( 67 ),
	AIM_120       ( 68 ),
	GC3           ( 99 ),
	Link16        ( 100 ),
	Link16EDR     ( 101 ),
	JTIDS         ( 102 ),
	Link22        ( 103 ),
	AFIWC_IADS    ( 104 ),
	F22_IFDL      ( 105 ),
	LBandSATCOM   ( 106 ),
	TSAFCommsLink ( 107 ) ,
	Enhanced_SINCGARS( 108 ),
	F35MADL       ( 109 ),
	CursorOnTarget( 110 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private TdlType( int value )
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
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static TdlType fromValue( int value )
	{
		for( TdlType type : values() )
		{
			if( type.value == value )
				return type;
		}
		
		throw new IllegalArgumentException( value+" not a valid TDLType" );
	}
}
