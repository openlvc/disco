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
package org.openlvc.disco;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Log4jConfiguration;

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
	private void run()
	{
		////////////////////////////////////////////////////////////
		// initialize the logging and tell it what args we loaded //
		////////////////////////////////////////////////////////////
		Log4jConfiguration logConfiguration = new Log4jConfiguration( "disco" );
		logConfiguration.activateConfiguration();
		Logger logger = LogManager.getFormatterLogger( "disco" );
		logger.info( "      Welcome to Open LVC Disco" );
		logger.info( "        .___.__                     " );
		logger.info( "      __| _/|__| ______ ____  ____  " );
		logger.info( "     / __ | |  |/  ___// ___\\/  _ \\ " );
		logger.info( "    / /_/ | |  |\\___ \\\\  \\__(  ( ) )" );
		logger.info( "    \\____ | |__/____  >\\___  >____/ " );
		logger.info( "         \\/         \\/     \\/       " );
		logger.info( "Version: "+DiscoConfiguration.getVersion() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args ) throws Exception
	{
		// Are there any special commands to tell us to run on of the child apps?
		if( args.length > 0 )
		{
			String firstArg = args[0];
			String[] remainingArgs = Arrays.copyOfRange(args,1,args.length);
			if( firstArg.equalsIgnoreCase("--app:duplicator") )
			{
				org.openlvc.duplicator.Main.main( remainingArgs );
				return;
			}
			else if( firstArg.equalsIgnoreCase("--app:disruptor") )
			{
				org.openlvc.disruptor.Main.main( remainingArgs );
				return;
			}
			else if( firstArg.equalsIgnoreCase("--app:distributor") )
			{
				org.openlvc.distributor.Main.main( remainingArgs );
				return;
			}
			else if( firstArg.equalsIgnoreCase("--app:disassembler") )
			{
				org.openlvc.disassembler.Main.main( remainingArgs );
				return;
			}
			else if( firstArg.equalsIgnoreCase("--app:dislocator") )
			{
				org.openlvc.dislocator.Main.main( remainingArgs );
				return;
			}
			else if( firstArg.equalsIgnoreCase("--app:disjoiner") )
			{
				org.openlvc.disrespector.Main.main( remainingArgs );
				return;
			}
		}

		// print out some information about us
		new Main().run();
	}
}
