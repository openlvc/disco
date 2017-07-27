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
package org.openlvc.disassembler.analyzers;

import org.openlvc.disassembler.configuration.AnalyzerMode;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disco.DiscoException;

/**
 * Interface representings a particular analyzer process that can be run over a configuration.
 * Puts some uniform lifecycle and control around the various disassemblers that can be run.
 * 
 * For the most part it is expected that analyzers are stateless. Although it is conceivable
 * that an analyzer might be able to calculate results over the course of a number of runs.
 * 
 * *Constructor Requirements*
 * It is a REQUIREMENT that all {@link IAnalyzer} implementations have a public, no-arg constructor
 */
public interface IAnalyzer
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Return the mode type for this analyzer
	 */
	public AnalyzerMode getMode();

	/**
	 * Run the analyzer over the given configuration. Return the results wrapped up
	 * in a results interface.
	 */
	public IResultSet execute( Configuration configuration ) throws DiscoException;
	
}
