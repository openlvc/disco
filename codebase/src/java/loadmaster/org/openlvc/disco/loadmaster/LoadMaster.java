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
package org.openlvc.disco.loadmaster;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.loadmaster.configuration.Configuration;

public class LoadMaster
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected LoadMaster( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getLMLogger();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void execute()
	{
		printWelcome();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	private void printWelcome()
	{
		logger.info("");
		logger.info("   (                          *                               ");
		logger.info("   )\\ )              (      (  `                 )            ");
		logger.info("  (()/(          )   )\\ )   )\\))(      )      ( /(   (   (    ");
		logger.info("   /(_))  (   ( /(  (()/(  ((_)()\\  ( /(  (   )\\()) ))\\  )(   ");
		logger.info("  (_))    )\\  )(_))  ((_)) (_()((_) )(_)) )\\ (_))/ /((_)(()\\  ");
		logger.info("  | |    ((_)((_)_   _| |  |  \\/  |((_)_ ((_)| |_ (_))   ((_) ");
		logger.info("  | |__ / _ \\/ _` |/ _` |  | |\\/| |/ _` |(_-<|  _|/ -_) | '_| ");
		logger.info("  |____|\\___/\\__,_|\\__,_|  |_|  |_|\\__,_|/__/ \\__|\\___| |_|   ");
		logger.info("");
		logger.info( "Welcome to the Load Master - Breaking Sims since Two-Oh-One-Six" );
		logger.info("");
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
