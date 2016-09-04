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
package org.openlvc.distributor;

import org.openlvc.distributor.configuration.Configuration;

public class Main
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Distributor distributor;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Main()
	{
		this.configuration = null;
		this.distributor = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private void run( String[] args ) throws Exception
	{
		// register the shutdown hook so we can gracefully exit
		Runtime.getRuntime().addShutdownHook( new ShutdownHook() );

		// parse teh configuration and command line args
		configuration = new Configuration( args );
		
		// construct and start a new distributor
		distributor = new Distributor( configuration );
		distributor.up();
		
		// tear down
		try{ Thread.sleep(10000000); } catch(Exception e) {e.printStackTrace();}
		distributor.down();
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
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Shutdown Hook   ///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	public class ShutdownHook extends Thread
	{
		@Override
		public void run()
		{
			if( distributor == null )
				return;
			else
				distributor.down();
		}
	}

}
