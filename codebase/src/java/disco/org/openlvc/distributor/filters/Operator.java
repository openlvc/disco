/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.distributor.filters;

public enum Operator
{
	//----------------------------------------------------------
	//                    ENUMERATED VALUES
	//----------------------------------------------------------
	Equals( "==" )
	{
		public final boolean compare( Object expected, Object received )
		{
			return received.equals( expected );
		}
	},
	DoesNotEqual( "!=" )
	{
		public final boolean compare( Object expected, Object received )
		{
			return received.equals(expected) == false;
		}
	},
	Contains( "<>" )
	{
		public final boolean compare( Object expected, Object received )
		{
			return received.toString().contains( expected.toString() );
		}
	},
	DoesNotContain( "><" )
	{
		public final boolean compare( Object expected, Object received )
		{
			return received.toString().contains( expected.toString() ) == false;
		}
	};

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public String text;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Operator( String text )
	{
		this.text = text;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public abstract boolean compare( Object expected, Object received );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Operator fromValue( String type )
	{
		type = type.trim();
		for( Operator value : Operator.values() )
		{
			if( value.text.equals(type) )
				return value;
		}
		
		throw new IllegalArgumentException( "Unknown comparision type: "+type );
	}
	
	public static Operator fromString( String filterString )
	{
		for( Operator value : Operator.values() )
		{
			if( filterString.contains(value.text) )
				return value;
		}
		
		throw new IllegalArgumentException( "Could not determine comparision type in: "+filterString );
	}
}
