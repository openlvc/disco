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

import org.openlvc.disassembler.analyzers.IAnalyzer;
import org.openlvc.disassembler.analyzers.enums.EnumerationAnalyzer;
import org.openlvc.disassembler.analyzers.none.Nonealyzer;
import org.openlvc.disco.DiscoException;

/**
 * Represents the particular disassembly mode/analyzer we are going to run.
 */
public enum AnalyzerMode
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	None(Nonealyzer.class),                       // no mode specified - wot!?
	Enumeration(EnumerationAnalyzer.class);       // analysis of enumerations in recording

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Class<? extends IAnalyzer> clazz;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private AnalyzerMode( Class<? extends IAnalyzer> clazz )
	{
		this.clazz = clazz;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Create a new instance of the {@link IAnalyzer} represented by this {@link AnalyzerMode}.
	 * We REQUIRE that the referenced type has a public, no-arg constructor, or else throw an
	 * exception. If there is any other error or internal exception while creating the new
	 * instance, we throw an exception.
	 */
	public IAnalyzer newInstance() throws DiscoException
	{
		try
		{
			return this.clazz.newInstance();
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error creating new analyzer of type "+name(), e );
		}
	}

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
