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

import java.io.File;

import org.json.simple.JSONObject;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disassembler.configuration.OutputFormat;
import org.openlvc.disco.DiscoException;

/**
 * Represents a set of results returned by an {@link IAnalyzer} after it has finished working
 * on a data set. It is expected that each analyzer will have its own implementation of this
 * interface structured in a way that makes sense for it based on the processes it run.
 *  
 * There are some basic top-level methods to generate summary output in different types, but
 * for access to analyzer specific members programmatically you will have to cast this down to
 * subtype that supports a particular analyzer.
 */
public interface IResults
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Get the type of analyzer that this result set came from.
	 */
	public AnalyzerType getAnalyzerType();

	/**
	 * Get access to the root configuration that was used to feed this analyzer run
	 */
	public Configuration getConfiguration();
	
	/**
	 * Convert the results to a pretty-printed string that can be dumped to the command line.
	 */
	public String toPrintableString() throws DiscoException;
	
	/**
	 * Convert the results to a JSON object. It it expected that object will have a top-level
	 * metadata property providing some common information conforming to the following format:
	 * 
	 * ```
	 * {
	 *     "metadata": { "analyzer": "enum", "benchmark_millis": 1234, "summary": "Brief Summary" }
	 * }
	 * ```
	 */
	public JSONObject toJson() throws DiscoException;

	/**
	 * Write the result to the specified file using the specified output format.
	 */
	public void dumpTo( File file, OutputFormat asFormat ) throws DiscoException;

	/**
	 * Return how long it took to run the analyzer
	 */
	public long getBenchmarkTime();

}
