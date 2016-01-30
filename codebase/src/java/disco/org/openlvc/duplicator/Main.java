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
package org.openlvc.duplicator;

/**
 * The DIS Recorder will gather up all DIS traffic on its configured network and save it to
 * a file for later use, or can read from recorded files and replay the traffic to the network,
 * rewriting some of the header information to suit local needs.
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			Configuration.printHelp();
			return;
		}
		
		// check the args for the --help command
		for( String temp : args )
		{
			if( temp.equalsIgnoreCase("--help") )
			{
				Configuration.printHelp();
				return;
			}
		}
		
		// parse the full command line and start a recorder
		Configuration configuration = new Configuration( args );

		if( configuration.isRecording() )
			new Recorder(configuration).execute();
		else
			new Replay(configuration).execute();
	}

}
