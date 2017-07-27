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
import org.openlvc.disassembler.analyzers.IResultSet;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disassembler.configuration.Disassembler;
import org.openlvc.disco.configuration.DiscoConfiguration;

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
	private Logger logger;

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	private void run( String[] args ) throws Exception
	{
		// Load configuration
		Configuration configuration = new Configuration( args );

		// Print a welcome message
		this.logger = configuration.getDisassemblerLogger();
		printWelcome();
		
		// Run the Disassembler
		IResultSet results = Disassembler.execute( configuration );
		
		// Output the results
		switch( configuration.getOutputFormat() )
		{
			case TEXT:
				printResults( results.toPrintableString() );
				break;
			case JSON:
				printResults( results.toJson().toJSONString() );
				break;
			case CSV:
				results.dumpTo( configuration.getOutFile(), configuration.getOutputFormat() );
				break;
		}
	}

	private void printResults( String results )
	{
		logger.info( results );
	}

	private void printWelcome()
	{
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
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args ) throws Exception
	{
		for( String string : args )
		{
			if( string.equalsIgnoreCase("--help") )
			{
				Configuration.printHelp();
				return;
			}
		}
		
		new Main().run( args );
	}
	
}
