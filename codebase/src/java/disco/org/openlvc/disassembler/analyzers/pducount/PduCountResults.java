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
package org.openlvc.disassembler.analyzers.pducount;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONObject;
import org.openlvc.disassembler.analyzers.IResults;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disassembler.configuration.OutputFormat;
import org.openlvc.disassembler.utils.CollectionUtils;
import org.openlvc.disassembler.utils.FieldComparable;
import org.openlvc.disassembler.utils.Searchable;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

public class PduCountResults implements IResults
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private PduCountConfiguration configuration;
	private long benchmarkMillis;

	private Map<PduType,PduCount> pduCounts;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduCountResults( PduCountConfiguration configuration )
	{
		this.configuration = configuration;
		this.benchmarkMillis = -1;
		
		this.pduCounts = new HashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IResults Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the analyzer type that this result set came from.
	 */
	public AnalyzerType getAnalyzerType()
	{
		return AnalyzerType.PduCount;
	}

	public Configuration getConfiguration()
	{
		return this.configuration;
	}

	/**
	 * Return how long it took to run the analyzer
	 */
	public long getBenchmarkTime()
	{
		return this.benchmarkMillis;
	}

	protected void setBenchmarkTime( long millis )
	{
		this.benchmarkMillis = millis;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Usage Tracking   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void add( PDU pdu ) throws DiscoException
	{
		PduType type = pdu.getType();
		PduCount count = pduCounts.get( type );
		if( count == null )
		{
			count = new PduCount( type, 0 );
			pduCounts.put( type, count );
		}
		
		count.pduCount.incrementAndGet();
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Ouput Generation Methods   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////
	/// Plain Text Generation Methods                    ///
	////////////////////////////////////////////////////////
	public String toPrintableString() throws DiscoException
	{
		// 1. Order and filter our result set
		Collection<PduCount> summaries = CollectionUtils.sort( pduCounts.values(), 
		                                                       configuration.getOrderBy(), 
		                                                       configuration.getAscending() );

		if( configuration.hasFilterBy() )
			summaries = CollectionUtils.search( summaries, configuration.getFilterBy() );

		// 2. Generate the table header
		StringBuilder builder = new StringBuilder();
		builder.append( "\n" );
		builder.append( " -----------------------------------------------------------------\n" );
		builder.append( "                          PDU Type Usage                          \n" );
		builder.append( " -----------------------------------------------------------------\n" );
		builder.append( "       PDU Type       |  PDU Count  | xxxxxxxxx | xxxxxxxxxxxx\n" );
		builder.append( " -----------------------------------------------------------------\n" );

		// 4. Write the results
		for( PduCount summary : summaries )
		{
			String line = String.format( "  %19s | %,11d | %,9d | %s \n",
			                             summary.type,
			                             summary.pduCount.get(),
			                             0,
			                             "" );
			builder.append( line );
		}
		builder.append( "\n" );
		
		return builder.toString();
	}

	////////////////////////////////////////////////////////
	/// JSON Generation Methods                          ///
	////////////////////////////////////////////////////////
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
		return null;
	}

	////////////////////////////////////////////////////////
	/// File Dump Generation Methods                     ///
	////////////////////////////////////////////////////////

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
	///                           Inner Class: EnumerationSummary                            /// 
	////////////////////////////////////////////////////////////////////////////////////////////
	public class PduCount implements FieldComparable<PduCount>,
	                                        Searchable<PduCount>
	{
		protected PduType type            = null;
		protected AtomicLong pduCount     = new AtomicLong(0);
		
		protected PduCount( PduType type, long count )
		{
			this.type = type;
			this.pduCount = new AtomicLong(count);
		}
		
		@Override
		public boolean matches( String searchString )
		{
			StringTokenizer tokens = new StringTokenizer( searchString, " " );
			if( tokens.countTokens() != 2 )
				throw new DiscoException( "Filter-by requires 2 values: <field-id> <match-value>: "+searchString );

			String field = tokens.nextToken();
			String value = tokens.nextToken();
			
			if( field.equals("pdu-type") || field.equals("type") )
			{
				return type.name().contains( value );
			}
			else if( field.equals("pdu-count") || field.equals("count") )
			{
				return pduCount.get() > Long.parseLong(value);
			}
			else
			{
				throw new DiscoException( "Unknown field for filter-by: "+field );
			}
		}
		
		@Override
		public int compareTo( PduCount other, String field )
		{
			if( field.equals("pdu-type") || field.equals("type") )
				return type.toString().compareTo( other.type.toString() );
			else if( field.equals("pdu-count") || field.equals("count") )
				return pduCount.get() > other.pduCount.get() ? 1 : -1;
			else
				throw new DiscoException( "Unknown field for order-by: "+field );
		}
	}
}
