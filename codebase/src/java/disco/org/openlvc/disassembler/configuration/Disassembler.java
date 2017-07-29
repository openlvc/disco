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

import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.openlvc.disassembler.analyzers.IAnalyzer;
import org.openlvc.disassembler.analyzers.IResultSet;
import org.openlvc.disco.DiscoException;

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
	public static IResultSet execute( Configuration configuration ) throws DiscoException
	{
		Logger logger = configuration.getDisassemblerLogger();
		
		// log the configuration information for later use
		logger.debug( "Loading..." );
		logger.debug( "Configuration:" );
		Properties properties = configuration.getProperties();
		for( String key : properties.stringPropertyNames() )
			logger.debug( "\t%s = %s", key, properties.getProperty(key) );

		// log startup information
		logger.info( "Running analyzer: "+configuration.getAnalyzerMode() );

		// create and run the analyzer
		IAnalyzer analyzer = configuration.getAnalyzerMode().newInstance();
		IResultSet results = analyzer.execute( configuration );
		return results;
	}
	
}
