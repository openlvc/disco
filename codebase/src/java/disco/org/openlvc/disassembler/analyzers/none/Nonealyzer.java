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
package org.openlvc.disassembler.analyzers.none;

import org.openlvc.disassembler.analyzers.IAnalyzer;
import org.openlvc.disassembler.analyzers.IResults;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disco.DiscoException;

/**
 * Fallback that is only called if no analyzer is specified in a configuration.
 * Throws exceptions for execution and results calls.
 */
public class Nonealyzer implements IAnalyzer
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

	/**
	 * Return the mode type for this analyzer
	 */
	@Override
	public AnalyzerType getAnalyzerType()
	{
		return AnalyzerType.None;
	}

	/**
	 * Run the analyzer over the given configuration. Return the results wrapped up
	 * in a results interface.
	 */
	@Override
	public IResults execute( Configuration configuration ) throws DiscoException
	{
		throw new DiscoException( "Analyzer mode is set to None." );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
