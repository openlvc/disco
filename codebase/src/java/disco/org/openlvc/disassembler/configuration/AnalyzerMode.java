/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.disassembler.configuration;

/**
 * Represents the particular disassembly mode/analyzer we are going to run.
 */
public enum AnalyzerMode
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	None,              // no mode specified - wot!?
	Enumeration;       // analysis of enumerations in recording

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static AnalyzerMode fromValue( String string ) throws IllegalArgumentException
	{
		// check for short-form options
		if( string.equalsIgnoreCase("enum") )
			return Enumeration;
		
		// loop through all and try to match against name
		for( AnalyzerMode mode : AnalyzerMode.values() )
		{
			if( string.equalsIgnoreCase(mode.name()) )
				return mode;
		}

		// give up; go home
		throw new IllegalArgumentException( "Unknown analyzer mode: "+string );
	}
}
