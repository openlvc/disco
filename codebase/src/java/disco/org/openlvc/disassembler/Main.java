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

import org.apache.logging.log4j.Logger;
import org.openlvc.disassembler.analyzers.IResults;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.Configuration;

/**
 * Run the Disassembler using the given command line.
 * 
 * For people who want to call this programatically there are a number of options. Some of
 * these are shown below:
 * 
 * ```
 *  // Call with a set of command line args
 *  IResults results = Disassembler.execute( new String[]{...} );
 *  
 *  // You know which analyzer you want and have a configuration instance (generic like shown
 *  // here, or a subclass)
 *  IResults results = Disassembler.execute( AnalyzerType.Enumeration, new Configuration(...) );
 *  
 *  // An alternate to the above which calls directly into the analyzer-specific classes
 *  EnumUsageResults results = (EnumUsageRestuls)new EnumUsageAnalyzer().execute( new EnumUsageConfiguration(...) );
 * ```
 */
public class Main
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

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private void run( String[] args ) throws Exception
	{
		// Run the Disassembler
		IResults results = Disassembler.execute( args );
		
		// Get the root configuration so we know what to output and where
		Configuration configuration = results.getConfiguration();
		Logger logger = configuration.getDisassemblerLogger();
		
		// Output the results
		switch( configuration.getOutputFormat() )
		{
			case TEXT:
				logger.info( results.toPrintableString() );
				break;
			case JSON:
				logger.info( results.toJson().toJSONString() );
				break;
			case CSV:
				results.dumpTo( configuration.getOutFile(), configuration.getOutputFormat() );
				break;
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args ) throws Exception
	{
		// Do they want us to print some help?
		boolean printHelp = false;
		for( String argument : args )
		{
			if( argument.equalsIgnoreCase("--help") )
			{
				printHelp = true;
				break;
			}
		}
		
		// If they want us to print help, do it
		if( printHelp )
		{
			// load the analyzer and create a config so we can ask it for analyzer-specific usage
			AnalyzerType.fromArgs(args).newConfiguration(args).printUsage();
			return;
		}
		else
		{
			new Main().run( args );
		}
	}
}
