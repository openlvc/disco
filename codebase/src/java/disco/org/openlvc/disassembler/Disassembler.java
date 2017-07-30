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
package org.openlvc.disassembler;

import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.openlvc.disassembler.analyzers.IResults;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.DiscoConfiguration;

/**
 * Main gateway class used to run the Disassembler. Call {@link Disassembler#execute(Configuration)}
 * to generate and run a disassembler/analyzer for a given configuration and return a result set.
 */
public class Disassembler
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Disassembler()
	{
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Run the Disassembler with the given command line. We will extract the analyzer to run
	 * from the command line and return the results for further processing.
	 */
	public static IResults execute( String[] commandline ) throws DiscoException
	{
		// Figure out which Analyzer we want
		AnalyzerType analyzerType = AnalyzerType.fromArgs( commandline );
		
		// Create the configuration object
		Configuration configuration = analyzerType.newConfiguration( commandline );
		
		// Print a welcome message
		printWelcome( configuration );
		
		// Run the Disassembler
		return analyzerType.newInstance().execute( configuration );
	}

	/**
	 * Run the Disassembler using the given Configuration instance (could be a sub-class).
	 * We will extract the analyzer type to run from within the configuration object.
	 */
	public static IResults execute( Configuration configuration ) throws DiscoException
	{
		// Get the Analyzer we are using
		AnalyzerType analyzerType = configuration.getAnalyzerType();
		
		// Print a welcome message
		printWelcome( configuration );
		
		// Run the Disassembler
		return analyzerType.newInstance().execute( configuration );
	}

	/**
	 * Run the Disassembler with a specific Analyzer and using the given configuration.
	 */
	public static IResults execute( AnalyzerType analyzerType, Configuration configuration )
		throws DiscoException
	{
		// Print a welcome message
		printWelcome( configuration );
		
		// Run the Disassembler
		return analyzerType.newInstance().execute( configuration );
	}
	
	
	private static void printWelcome( Configuration configuration )
	{
		Logger logger = configuration.getDisassemblerLogger();
		
		logger.info( "Welcome to the OpenLVC DISassembler" );
		logger.info( "" );
		logger.info( "8888888b. 8888888 .d8888b.                                                    888      888" );                  
		logger.info( "888  \"Y88b  888  d88P  Y88b                                                   888      888                  " );
		logger.info( "888    888  888  Y88b.                                                        888      888                  " );
		logger.info( "888    888  888   \"Y888b.    8888b.  .d8888b  .d8888b   .d88b.  88888b.d88b.  88888b.  888  .d88b.  888d888 " );
		logger.info( "888    888  888      \"Y88b.     \"88b 88K      88K      d8P  Y8b 888 \"888 \"88b 888 \"88b 888 d8P  Y8b 888P\"   " );
		logger.info( "888    888  888        \"888 .d888888 \"Y8888b. \"Y8888b. 88888888 888  888  888 888  888 888 88888888 888     " );
		logger.info( "888  .d88P  888  Y88b  d88P 888  888      X88      X88 Y8b.     888  888  888 888 d88P 888 Y8b.     888     " );
		logger.info( "8888888P\" 8888888 \"Y8888P\"  \"Y888888  88888P'  88888P'  \"Y8888  888  888  888 88888P\"  888  \"Y8888  888     " );
		logger.info( "" );
		logger.info( "Version: "+DiscoConfiguration.getVersion() );
		logger.info( "" );

		// log the configuration information for later use
		logger.debug( "Loading..." );
		logger.debug( "Configuration:" );
		Properties properties = configuration.getProperties();
		for( String key : properties.stringPropertyNames() )
			logger.debug( "\t%s = %s", key, properties.getProperty(key) );

		// log startup information
		logger.info( "Running analyzer: "+configuration.getAnalyzerType() );
	}

	
}
