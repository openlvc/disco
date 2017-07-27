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
package org.openlvc.disassembler.analyzers.enums;

import org.apache.logging.log4j.Logger;
import org.openlvc.disassembler.analyzers.IAnalyzer;
import org.openlvc.disassembler.analyzers.IResultSet;
import org.openlvc.disassembler.configuration.AnalyzerMode;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disco.DiscoException;

public class EnumerationAnalyzer implements IAnalyzer
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

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public IResultSet execute( Configuration configuration ) throws DiscoException
	{
		// initialize outselves from the configuration
		initialize( configuration );
		
		logger.info( "Running enumerations analysis on Duplicator session: "+configuration.getInFile().getAbsolutePath() );
		
		return new EnumerationResultSet();
	}
	
	private void initialize( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getDisassemblerLogger();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public AnalyzerMode getMode()
	{
		return AnalyzerMode.Enumeration;
	}

	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
