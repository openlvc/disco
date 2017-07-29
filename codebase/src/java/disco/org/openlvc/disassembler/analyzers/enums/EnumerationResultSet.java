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
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openlvc.disassembler.analyzers.IResultSet;
import org.openlvc.disassembler.configuration.AnalyzerMode;
import org.openlvc.disassembler.configuration.OutputFormat;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;

public class EnumerationResultSet implements IResultSet
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// EntityStatePDU
	//   1.2.3.4.5.6.7
	//     EntityID
	//       count, lastSeen, ...
	//
	private Map<EntityType,EnumerationRecord> espdus;
	private Map<EntityType,EnumerationRecord> firepdus;
	private Map<EntityType,EnumerationRecord> detpdus;

	private long totalPdus;
	private long benchmarkMillis;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EnumerationResultSet()
	{
		this.espdus   = new HashMap<>();
		this.firepdus = new HashMap<>();
		this.detpdus  = new HashMap<>();

		this.totalPdus = 0;
		this.benchmarkMillis = -1;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Get the analyzer mode that this result set came from.
	 */
	public AnalyzerMode getMode()
	{
		return AnalyzerMode.Enumeration;
	}
	
	public long getBenchmarkTime()
	{
		return this.benchmarkMillis;
	}
	
	public void setBenchmarkTime( long millis )
	{
		this.benchmarkMillis = millis;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
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
		EnumerationRecord enumerationRecord = getOrCreate(enumeration,espdus);
		// get the record under this enumeration for the particular ENTITY
		EntityRecord entityRecord = enumerationRecord.getOrCreate( pdu.getEntityID() );
		
		// update the stats
		entityRecord.count.incrementAndGet();
	}
	
	private void observe( FirePdu pdu )
	{
		// get the record for this ENUMERATION
		EntityType enumeration = pdu.getBurstDescriptor().getMunition();
		EnumerationRecord enumerationRecord = getOrCreate(enumeration,firepdus);
		// get the record for the FIRER under this enumeration
		EntityRecord entityRecord = enumerationRecord.getOrCreate( pdu.getFiringEntityID() );

		// update the stats
		entityRecord.count.incrementAndGet();
	}

	private void observe( DetonationPdu pdu )
	{
		// get the record for this ENUMERATION
		EntityType enumeration = pdu.getBurstDescriptor().getMunition();
		EnumerationRecord enumerationRecord = getOrCreate(enumeration,detpdus);
		// get the record for the FIRER under this enumeration
		EntityRecord entityRecord = enumerationRecord.getOrCreate( pdu.getFiringEntityID() );

		// update the stats
		entityRecord.count.incrementAndGet();
	}

	private EnumerationRecord getOrCreate( EntityType enumeration, Map<EntityType,EnumerationRecord> map )
	{
		EnumerationRecord record = map.get( enumeration );
		if( record == null )
		{
			record = new EnumerationRecord();
			record.enumeration = enumeration;
			map.put( enumeration, record );
		}
		
		return record;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
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
		
		JSONArray results = new JSONArray();
		results.add( toJson(PduType.EntityState,espdus) );
		results.add( toJson(PduType.Fire,firepdus) );
		results.add( toJson(PduType.Detonation,detpdus) );
		rootObject.put( "results", results );
		
		return rootObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject toJson( PduType pduType, Map<EntityType,EnumerationRecord> map )
	{
		JSONObject rootObject = new JSONObject();
		rootObject.put( "type", pduType );
		
		JSONArray enumerations = new JSONArray();
		map.forEach( (enumeration,record) -> enumerations.add( toJson(enumeration,record) ) );
		
		rootObject.put( "enumerations", enumerations );
		return rootObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject toJson( EntityType enumeration, EnumerationRecord enumerationRecord )
	{
		JSONObject jsonRecord = new JSONObject();
		jsonRecord.put( "enumeration", enumeration.toString() );
		jsonRecord.put( "pduCount", enumerationRecord.getPduCount() );
		
		JSONArray jsonEntities = new JSONArray();
		enumerationRecord.records.values().forEach( record -> jsonEntities.add( toJson(record)) );
		
		jsonRecord.put( "entities", jsonEntities );
		return jsonRecord;
	}

	@SuppressWarnings("unchecked")
	private JSONObject toJson( EntityRecord entityRecord )
	{
		JSONObject jsonRecord = new JSONObject();
		jsonRecord.put( "entityId", entityRecord.entityId.toString() );
		jsonRecord.put( "count", entityRecord.getPduCount() );
		return jsonRecord;
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
	public class EnumerationRecord
	{
		protected EntityType enumeration;
		protected Map<EntityId,EntityRecord> records = new HashMap<EntityId,EntityRecord>();

		public EntityRecord getOrCreate( EntityId entity )
		{
			EntityRecord record = records.get( entity );
			if( record == null )
			{
				record = new EntityRecord();
				record.entityId = entity;
				records.put( entity, record );
			}
			
			return record;
		}
		
		public long getPduCount()
		{
			return records.values().stream().mapToLong( record -> record.count.get() ).sum();
		}
	}
	
	public class EntityRecord
	{
		protected EntityId entityId;
		protected AtomicLong count = new AtomicLong(0);
		
		public long getPduCount()
		{
			return count.get();
		}
	}
}
