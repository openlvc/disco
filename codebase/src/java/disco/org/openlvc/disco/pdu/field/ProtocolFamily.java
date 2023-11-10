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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;

public class ProtocolFamily
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final Map<Short,ProtocolFamily> StandardValues = new HashMap<>();
	
	public static final ProtocolFamily Other            = registerStandardValue( 0, "Other" );
	public static final ProtocolFamily Entity           = registerStandardValue( 1, "Entity" );
	public static final ProtocolFamily Warfare          = registerStandardValue( 2, "Warfare" );
	public static final ProtocolFamily Logistics        = registerStandardValue( 3, "Logistics" );
	public static final ProtocolFamily Radio            = registerStandardValue( 4, "Radio" );
	public static final ProtocolFamily SimMgmt          = registerStandardValue( 5, "SimMgmt" );
	public static final ProtocolFamily Emission         = registerStandardValue( 6, "Emission" );
	public static final ProtocolFamily EntityMgmt       = registerStandardValue( 7, "EntityMgmt" );
	public static final ProtocolFamily Minefield        = registerStandardValue( 8, "Minefield" );
	public static final ProtocolFamily SyntheticEnv     = registerStandardValue( 9, "SyntheticEnv" );
	public static final ProtocolFamily SimMgmt_R        = registerStandardValue( 10, "SimMgmt_R" );
	public static final ProtocolFamily LiveEntity       = registerStandardValue( 11, "LiveEntity" );
	public static final ProtocolFamily NonRealTime      = registerStandardValue( 12, "NonRealTime" );
	public static final ProtocolFamily InformationOps   = registerStandardValue( 13, "InformationOps" );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;
	private String name;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ProtocolFamily( short value )
	{
		this( value, null );
	}
	
	private ProtocolFamily( short value, String name )
	{
		this.value = value;
		this.name = name;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	public String name()
	{
		return this.name;
	}
	
	@Override
	public String toString()
	{
		if( this.name != null )
			return String.format( "%s (%d)", this.name, this.value );
		else
			return String.valueOf( this.value );
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( !(other instanceof ProtocolFamily) )
			return false;
		
		ProtocolFamily otherFamily = (ProtocolFamily)other;
		return otherFamily.value == this.value;
	}
	
	@Override
	public int hashCode()
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
		ProtocolFamily result = StandardValues.get( value );
		if( result == null )
		{
			if( DiscoConfiguration.isSet(Flag.Strict) )
				throw new IllegalArgumentException( value+" is not a valid value for ProtocolFamily" );
			
			result = new ProtocolFamily( value );
		}

		return result;
	}
	
	public static ProtocolFamily fromName( String name )
	{
		Optional<ProtocolFamily> result = StandardValues.values().stream()
		                                                         .filter( v -> v.name.equalsIgnoreCase(name) )
		                                                         .findFirst();
		
		if( result.isPresent() )
			return result.get();
		else
			throw new IllegalArgumentException( name+" is not a valid name for ProtocolFamily" );
	}
	
	private static ProtocolFamily registerStandardValue( Number value, String name )
	{
		ProtocolFamily family = new ProtocolFamily( value.shortValue(), name );
		StandardValues.put( value.shortValue(), family );
		return family;
	}
}
