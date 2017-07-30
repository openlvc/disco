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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disassembler.analyzers.IAnalyzer;
import org.openlvc.disassembler.analyzers.enumeration.EnumUsageAnalyzer;
import org.openlvc.disassembler.analyzers.enumeration.EnumUsageConfiguration;
import org.openlvc.disassembler.analyzers.none.Nonealyzer;
import org.openlvc.disco.DiscoException;

public enum AnalyzerType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	None("--none",Nonealyzer.class,Configuration.class),                        // no mode specified - wot!?
	EnumUsage("--enum",EnumUsageAnalyzer.class,EnumUsageConfiguration.class);   // analysis of enumerations used in recording

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String argument;
	private Class<? extends IAnalyzer> analyzerClass;
	private Class<? extends Configuration> configClass;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private AnalyzerType( String argument,
	                      Class<? extends IAnalyzer> analyzerClass,
	                      Class<? extends Configuration> configClass )
	{
		this.argument = argument;
		this.analyzerClass = analyzerClass;
		this.configClass = configClass;
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
			return this.analyzerClass.newInstance();
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error creating new analyzer for type "+name(), e );
		}
	}

	/**
	 * As with {@link #newInstance()}, this generates a new analyzer-specific class. In this
	 * case we are generating an analyzer specific configuration class, which may or may not
	 * be the generic {@link Configuration} class.
	 * 
	 * Analyzer-specific configuration classes are expected to extend {@link Configuration}
	 * and to override any argument processing methods or provide additional configuration
	 * attributes for use by the analyzer itself.
	 * 
	 * _NOTE: The implementing class *must* have a constructor that takes a String[] (only)._
	 * 
	 * @return An instance of the analyzer-specific configuration class. The analyzer itself
	 *         will know how to deal with this and what to down-cast it to. This is done so
	 *         that we can fit it into a standard framework/api for all analyzers.
	 * @throws DiscoException If there is a problem instantiating the configuration type.
	 */
	public Configuration newConfiguration( String[] args ) throws DiscoException
	{
		try
		{
			Constructor<? extends Configuration> ctr = configClass.getConstructor( String[].class );
			return ctr.newInstance( new Object[]{ args } );
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error creating new analyzer config for type "+name(), e );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static AnalyzerType fromValue( String string ) throws IllegalArgumentException
	{
		// check for short-form options
		if( string.equalsIgnoreCase("enum") )
			return EnumUsage;
		
		// loop through all and try to match against name
		for( AnalyzerType type : AnalyzerType.values() )
		{
			if( string.equalsIgnoreCase(type.name()) )
				return type;
		}

		// give up; go home
		throw new IllegalArgumentException( "Unknown analyzer mode: "+string );
	}

	/**
	 * Parse the given command line for a definition of the analyzer to load and return it. 
	 * 
	 * @throws DiscoException If none of the args match an analyzer
	 */
	public static AnalyzerType fromArgs( String[] args ) throws DiscoException
	{
		// List of supported arguments. If we don't find a match we'll return this
		// in the exception message as it will be helpful for debugging
		List<String> supported = new ArrayList<>();

		for( AnalyzerType type : AnalyzerType.values() )
		{
			// store in-case we don't find a match
			supported.add( type.argument );
			
			// check the args to see if we have a match
			for( String givenArgument : args )
			{
				if( type.argument.equalsIgnoreCase(givenArgument) )
					return type;
			}
		}
		
		throw new DiscoException( "Analyzer not specified; supported="+supported );
	}

}
