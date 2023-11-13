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

import java.util.Set;

import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.utils.ValueLookup;

public class ProtocolFamily
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static ValueLookup<Short> StandardValueLookup;
	
	public static final short Other            = 0;
	public static final short Entity           = 1;
	public static final short Warfare          = 2;
	public static final short Logistics        = 3;
	public static final short Radio            = 4;
	public static final short SimMgmt          = 5;
	public static final short Emission         = 6;
	public static final short EntityMgmt       = 7;
	public static final short Minefield        = 8;
	public static final short SyntheticEnv     = 9;
	public static final short SimMgmt_R        = 10;
	public static final short LiveEntity       = 11;
	public static final short NonRealTime      = 12;
	public static final short InformationOps   = 13;
	
	static
	{
		StandardValueLookup = new ValueLookup<Short>();
		
		StandardValueLookup.addNamedValue( "Other", Other );
		StandardValueLookup.addNamedValue( "Entity", Entity );
		StandardValueLookup.addNamedValue( "Warfare", Warfare );
		StandardValueLookup.addNamedValue( "Logistics", Logistics );
		StandardValueLookup.addNamedValue( "Radio", Radio );
		StandardValueLookup.addNamedValue( "SimMgmt", SimMgmt );
		StandardValueLookup.addNamedValue( "Emission", Emission );
		StandardValueLookup.addNamedValue( "EntityMgmt", EntityMgmt );
		StandardValueLookup.addNamedValue( "Minefield", Minefield );
		StandardValueLookup.addNamedValue( "SyntheticEnv", SyntheticEnv );
		StandardValueLookup.addNamedValue( "SimMgmt_R", SimMgmt_R );
		StandardValueLookup.addNamedValue( "LiveEntity", LiveEntity );
		StandardValueLookup.addNamedValue( "NonRealTime", NonRealTime );
		StandardValueLookup.addNamedValue( "InformationOps", InformationOps );
		
	}

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ProtocolFamily() {}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Set<Short> getStandardValues()
	{
		return StandardValueLookup.getValues();
	}
	
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}
	
	public static short fromName( String name )
	{
		Short value = StandardValueLookup.getValueForName( name );
		if( value == null )
			throw new IllegalArgumentException( name+" is not a valid name for ProtocolFamily" );
		
		return value.shortValue();
	}
	
	public static String describe( Number value )
	{
		String name = StandardValueLookup.getNameForValue( value.shortValue() );
		return name != null ? name : String.format( "Unknown (%d)", value.shortValue() );
	}
}
