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
package org.openlvc.disco.loadmaster.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.Log4jConfiguration;

public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger applicationLogger;
	private Log4jConfiguration appLoggerConfiguration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration( Arguments commandline )
	{
		this.applicationLogger = null; // set on first access
		this.appLoggerConfiguration = new Log4jConfiguration( "lm" );
		this.appLoggerConfiguration.setConsoleOn( true );
		this.appLoggerConfiguration.setFileOn( false );
		this.appLoggerConfiguration.setLevel( commandline.getLogLevel() );

	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Get the Load Master application logger. Will lazy-load configuration. */
	public Logger getLMLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;
		
		this.appLoggerConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "lm" );
		return applicationLogger;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
