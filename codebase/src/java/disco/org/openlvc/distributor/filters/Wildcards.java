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

import java.util.regex.Pattern;

/**
 * This class provides utilities for converting values that a filter should match (as specified
 * by a user) into regex Patterns. It has methods that do appropriate conversions between a
 * simplified "*" wildcard matching scheme and proper regex syntax for various common DIS usages
 * (such as entity ids, enumerations and so forth).
 */
public class Wildcards
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final Pattern forMarking( String input )
	{
		String converted = input.replace( "*", ".*" );
		return Pattern.compile( converted );
	}
	
	public static final Pattern forEnumeration( String input )
	{
		input = input.replace( ".", "\\." );
		input = input.replace( "-", "\\." );
		input = input.replace( " ", "\\." );
		input = input.replace( "*", "[0-9]*" );
		// Could replace with these to capture any separator on incoming value, but
		// we know the format we'll get it from PDU as, so this extra complication just
		// makes the regex slower for no real other gain.
		//input = input.replace( ".", "(\\.|\\s|-)" );
		//input = input.replace( "-", "(\\.|\\s|-)" );
		//input = input.replace( " ", "(\\.|\\s|-)" );
		//input = input.replace( "*", "[0-9]*" );

		return Pattern.compile( input );
	}
	
	public static final Pattern forEntityId( String input )
	{
		// id (number) match
		//input = "1-*-7";
		input = input.replace( "*", "[0-9]*" );
		return Pattern.compile( input );
	}
}
