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
package org.openlvc.disruptor;

import org.openlvc.disruptor.configuration.Arguments;
import org.openlvc.disruptor.configuration.Configuration;

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
		// Read the command line
		Arguments commandline = new Arguments( args );

		// Load configuration
		Configuration configuration = new Configuration( commandline.getConfigFile() );

		// Override settings with any command line args
		configuration.override( commandline );
		
		// Run the load master
		Disruptor loadmaster = new Disruptor( configuration );
		loadmaster.execute();
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
				Arguments.printHelp();
				return;
			}
		}
		
		new Main().run( args );
	}
}
