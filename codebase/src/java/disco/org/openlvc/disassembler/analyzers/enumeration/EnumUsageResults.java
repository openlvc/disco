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
package org.openlvc.disassembler.analyzers.enumeration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openlvc.disassembler.analyzers.IResults;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.OutputFormat;
import org.openlvc.disassembler.utils.CollectionUtils;
import org.openlvc.disassembler.utils.FieldComparable;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;

public class EnumUsageResults implements IResults
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumUsageConfiguration configuration;

	private Map<EntityType,EnumerationSummary> espdus;
	private Map<EntityType,EnumerationSummary> firepdus;
	private Map<EntityType,EnumerationSummary> detpdus;

	private long totalPdus;
	private long benchmarkMillis;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EnumUsageResults( EnumUsageConfiguration configuration )
	{
		this.configuration = configuration;

		this.espdus   = new HashMap<>();
		this.firepdus = new HashMap<>();
		this.detpdus  = new HashMap<>();
		
		this.totalPdus = 0;
		this.benchmarkMillis = -1;
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
	@Override
	public AnalyzerType getAnalyzerType()
	{
		return AnalyzerType.EnumUsage;
	}

	@Override
	public long getBenchmarkTime()
	{
		return this.benchmarkMillis;
	}
	
	public void setBenchmarkTime( long millis )
	{
		this.benchmarkMillis = millis;
	}
	
	@Override
	public EnumUsageConfiguration getConfiguration()
	{
		return this.configuration;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Recording Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void add( PDU pdu )
	{
		// Record details of the PDU based on its type.
		// This is a bit of overkill at the moment because we store the same information
		// for all records, so there is some duplication. However, I anticipate shortly
		// doing more PDU-specific things, so the duplication in the observe() methods will
		// be OK for now.
		switch( pdu.getType() )
		{
			case EntityState:
				observe( pdu.as(EntityStatePdu.class) );
				break;
			case Fire:
				observe( pdu.as(FirePdu.class) );
				break;
			case Detonation:
				observe( pdu.as(DetonationPdu.class) );
				break;
			default:
				break;
		}
		
		++totalPdus;
	}

	private void observe( EntityStatePdu pdu )
	{
		// get the record for this ENUMERATION
		EntityType enumeration = pdu.getEntityType();
		
		// Summary Recording
		EnumerationSummary summary = getOrCreate( espdus, enumeration );
		summary.observe( pdu.getEntityID() );
	}

	private void observe( FirePdu pdu )
	{
		// get the record for the enumeration
		EntityType enumeration = pdu.getBurstDescriptor().getMunition();
		EnumerationSummary summary = getOrCreate( firepdus, enumeration );
		summary.observe( pdu.getFiringEntityID() );
	}

	private void observe( DetonationPdu pdu )
	{
		EntityType enumeration = pdu.getBurstDescriptor().getMunition();
		EnumerationSummary summary = getOrCreate( detpdus, enumeration );
		summary.observe( pdu.getFiringEntityID() );
	}

	private EnumerationSummary getOrCreate( Map<EntityType,EnumerationSummary> store,
	                                        EntityType enumeration )
	{
		EnumerationSummary summary = store.get( enumeration );
		if( summary == null )
		{
			summary = new EnumerationSummary();
			summary.enumeration = enumeration;
			store.put( enumeration, summary );
		}

		return summary;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Ouput Generation Methods   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////
	/// Plain Text Generation Methods                    ///
	////////////////////////////////////////////////////////
	/**
	 * Convert the results to a pretty-printed string that can be dumped to the command line.
	 */
	public String toPrintableString() throws DiscoException
	{
		StringBuilder builder = new StringBuilder();
		buildTextSummary( builder, "Entity State PDU Usage Summary", espdus.values() );
		buildTextSummary( builder, "Fire PDU Usage Summary", firepdus.values() );
		buildTextSummary( builder, "Detonation PDU Usage Summary", detpdus.values() );
		return builder.toString();
	}
	
	private void buildTextSummary( StringBuilder builder, String prefix, Collection<EnumerationSummary> summaries )
	{
		// 1. Order the PDUs by the field we want to
		summaries = CollectionUtils.sort( summaries, configuration.getOrderBy(), false );

		// 2. Build prefix
		builder.append( "\n" );
		builder.append( " -- "+prefix+" --\n" );
		builder.append( "\n" );
		builder.append( " -----------------------------------------------------------------\n" );
		builder.append( "     Enumeration      |  PDU Count  | Obj Count | Site-App IDs\n" );
		builder.append( " -----------------------------------------------------------------\n" );

		// 3. Write table of results
		for( EnumerationSummary summary : summaries )
		{
			String line = String.format( "  %19s | %,11d | %,9d | %s \n",
			                             summary.enumeration.toString(),
			                             summary.pduCount.get(),
			                             summary.entities.size(),
			                             summary.appIds );
			builder.append( line );
		}
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
	@SuppressWarnings("unchecked")
	public JSONObject toJson() throws DiscoException
	{
		JSONObject rootObject = new JSONObject();
		
		// Metadata object
		double seconds = benchmarkMillis / 1000;
		String pps = String.format( "%,d pps", (long)((double)totalPdus/seconds) );
		
		JSONObject metadata = new JSONObject();
		metadata.put( "analyzer", "enum" );
		metadata.put( "benchmark_millis", benchmarkMillis );
		metadata.put( "pdus_per_second", pps );
		metadata.put( "pdu_count", String.format("%,d",totalPdus) );
		metadata.put( "summary", "Successful" );
		rootObject.put( "metadata", metadata );
		
		// Resutls body
		JSONArray results = new JSONArray();
		results.add( toJson(PduType.EntityState,espdus.values()) );
		results.add( toJson(PduType.Fire,firepdus.values()) );
		results.add( toJson(PduType.Detonation,detpdus.values()) );
		rootObject.put( "results", results );
		
		return rootObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject toJson( PduType pduType, Collection<EnumerationSummary> summaries )
	{
		JSONObject rootObject = new JSONObject();
		rootObject.put( "type", pduType );
		
		// sort the enumerations first
		summaries = CollectionUtils.sort( summaries, configuration.getOrderBy(), false );
		
		// add the enumeration array
		JSONArray enumerations = new JSONArray();
		for( EnumerationSummary summary : summaries )
		{
			JSONObject json = new JSONObject();
			json.put( "enumeration", summary.enumeration.toString() );
			json.put( "pdu_count", summary.pduCount.get() );
			json.put( "obj_count", summary.entities.size() );
			json.put( "app_ids", summary.entities.toString() );
			enumerations.add( json );
		}
		
		rootObject.put( "enumerations", enumerations );
		return rootObject;
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
	public class EnumerationSummary implements FieldComparable<EnumerationSummary>
	{
		protected EntityType enumeration  = null;
		protected AtomicLong pduCount     = new AtomicLong(0);
		protected Set<EntityId> entities  = new HashSet<>();
		protected Set<String> appIds      = new HashSet<>();
		
		public void observe( EntityId entityId )
		{
			pduCount.incrementAndGet();
			entities.add( entityId );
			appIds.add( entityId.getSiteAndAppId() );
		}
		
		@Override
		public int compareTo( EnumerationSummary other, String field )
		{
			if( field.equals("enumeration") )
				return enumeration.toString().compareTo( other.toString() );
			else if( field.equals("site-id") )
				return appIds.size() > other.appIds.size() ? 1 : -1;
			else if( field.equals("pdu-count") )
				return pduCount.get() > other.pduCount.get() ? 1 : -1;
			else if( field.equals("obj-count") )
				return entities.size() > other.entities.size() ? 1 : -1;
			else
				throw new DiscoException( "Unknown field for order-by: "+field );
		}
	}
}
