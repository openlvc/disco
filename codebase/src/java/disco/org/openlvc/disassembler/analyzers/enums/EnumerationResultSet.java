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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONObject;
import org.openlvc.disassembler.analyzers.IResultSet;
import org.openlvc.disassembler.configuration.AnalyzerMode;
import org.openlvc.disassembler.configuration.OutputFormat;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityType;

public class EnumerationResultSet implements IResultSet
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected Map<PduType,List<ObservedEnumeration>> pdumap;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EnumerationResultSet()
	{
		this.pdumap = new HashMap<PduType,List<ObservedEnumeration>>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------


	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the analyzer mode that this result set came from.
	 */
	public AnalyzerMode getMode()
	{
		return AnalyzerMode.Enumeration;
	}
	
	/**
	 * Convert the results to a pretty-printed string that can be dumped to the command line.
	 */
	public String toPrintableString() throws DiscoException
	{
		return "Enumerations Analysis";
	}
	
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
	public JSONObject toJson() throws DiscoException
	{
		return new JSONObject();
	}

	/**
	 * Write the result to the specified file using the specified output format.
	 */
	public void dumpTo( File file, OutputFormat asFormat ) throws DiscoException
	{
		
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	///                           Inner Class: ObservedEnumeration                           /// 
	////////////////////////////////////////////////////////////////////////////////////////////
	public class ObservedEnumeration
	{
		protected EntityType enumeration;
		protected AtomicLong count;
		protected AtomicLong lastSeen;
	}
}
