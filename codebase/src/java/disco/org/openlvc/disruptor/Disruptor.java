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
package org.openlvc.disruptor;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.Metrics;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.appearance.CommonAppearance;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.CoordinateUtils;
import org.openlvc.disco.utils.JsonUtils;
import org.openlvc.disco.utils.LLA;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disruptor.paths.IPath;
import org.openlvc.disruptor.paths.PathFactory;

public class Disruptor
{

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// JSON configuration keys
	private static final String ENTITIES_CONFIG = "entities";
	private static final String LOCATIONS_CONFIG = "locations";
	private static final String PATHS_CONFIG = "paths";
	private static final String ENTITY_COUNT = "count";
	private static final String ENTITY_SPACING = "spacing";
	private static final String PATH_DEFINITION = "path";
	private static final String PATH_ENTITY_TYPE = "entityType";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;
	private OpsCenter opscenter;

	private long simStart;

	private ArrayList<PDUAndPathContainer> pduAndPathContainers;
	private Map<String,EntityType> entityDisEnumLookup;
	private Map<String,LLA> locationLookup;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Disruptor( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getDisruptorLogger();
		this.opscenter = null; // set in execute()

		this.pduAndPathContainers = new ArrayList<>();
		this.entityDisEnumLookup = Collections.emptyMap();
		this.locationLookup = Collections.emptyMap();

		populateEntityStore();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Execution Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void execute()
	{
		printWelcome();

		// Open up the Disco Operations Centre
		opscenter = new OpsCenter( configuration.getDiscoConfiguration() );
		opscenter.setListener( new PduListener() );
		opscenter.open();

		long benchmarkStart = System.currentTimeMillis();
		simStart = benchmarkStart;
		try
		{
    		// Do our work
    		for( int i = 1; i <= configuration.getLoops(); i++ )
    		{
    			tick( i );
    			sleep( configuration.getTickInterval() );
    		}
		}
		finally
		{
			opscenter.close();
		}

		logger.info( "DIS illusions have been dispelled - normality has been restored." );

		Metrics metrics = opscenter.getMetrics();
		long sent = metrics.getPdusSent();
		long received = metrics.getPdusReceived();
		float percentage = (float)received / (float)sent;
		long time = System.currentTimeMillis() - benchmarkStart;
		long perSecond = (long)(((double)sent / (double)time) * 1000.0);
		logger.info( "Sent %s in %d ms (%d/s), Received %s (%.2f%%).",
		             NumberFormat.getNumberInstance().format(sent),
		             time,
		             perSecond,
		             NumberFormat.getNumberInstance().format(received),
		             percentage*100.0 );
	}

	private void tick( int count )
	{
		long now = System.currentTimeMillis();
		long msSinceStart = now - this.simStart;
		int espdus = 0;
		int firepdus = 0;
		int detpdus = 0;

		for( int i = 0; i < pduAndPathContainers.size(); i++ )
		{
			PDUAndPathContainer container = pduAndPathContainers.get( i );
			IPath path = container.getPath();
			double entitySpacing = container.getSpacing();
			List<EntityStatePdu> entityPdus = container.getEntityStatePdus();
			for( int j = 0; j < entityPdus.size(); j++ )
			{
				EntityStatePdu entityPdu = entityPdus.get( j );
				LLA currentLLA = path.getLLA( msSinceStart, entitySpacing * j );
				double headingRad = path.getHeadingRad( msSinceStart, entitySpacing * j );
				entityPdu.setLocation( CoordinateUtils.toECEF( currentLLA ) );
				WorldCoordinate ecefLocation = CoordinateUtils.toECEF(currentLLA);
				entityPdu.setLocation( ecefLocation );
				entityPdu.setOrientation( CoordinateUtils.getPduEulerAngles( ecefLocation, headingRad ));
				opscenter.send( entityPdu );
				++espdus;
			}
		}

		long endTime = System.currentTimeMillis();
		int totalpdus = espdus + firepdus + detpdus;

//		"[     1] Tick Completed -- 100,000pdu in 875ms (102,444/s)
//		"[     1]   Entity State -- 100,000            | "
//		"[     1]           Fire -- 0"
//		"[     1]     Detonation -- 0"

		// do some precalculation on some fields
		long periodMillis = (endTime - now);
		double periodSeconds = periodMillis / 1000.0;
		int pdusPerSecond = (int)(totalpdus / periodSeconds);

		String sTotal   = NumberFormat.getNumberInstance().format( totalpdus );
		String sEspdu   = NumberFormat.getNumberInstance().format( espdus );
		String sFirepdu = NumberFormat.getNumberInstance().format( firepdus );
		String sDetpdu  = NumberFormat.getNumberInstance().format( detpdus );
		String sPduPerSecond = NumberFormat.getNumberInstance().format( pdusPerSecond );

		logger.info( String.format("[%7d] Tick Completed: %s PDUs in %dms (%s/s)",
		                           count,
		                           sTotal,
		                           periodMillis,
		                           sPduPerSecond) );
		//logger.info( String.format("[%7d] Tick completed: Sent %d PDUs to network in %dms",count,totalpdus,(endTime-startTime)) );
		//gger.info( "[     1] Tick completed: Sent "+totalpdus+" PDUs to network in "+(endTime-startTime)+"ms" );
		logger.info( "            Entity State: "+sEspdu );
		logger.info( "                    Fire: "+sFirepdu );
		logger.info( "              Detonation: "+sDetpdu );
		logger.info( "" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Object Management Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void populateEntityStore()
	{
		int siteId = 1;
		int entityId = 0;
		
		File planFile = new File( configuration.getPlanFile() );
		JSONObject sessionConfig;
		if( !planFile.exists() )
		{
			throw new RuntimeException( "Could not find DISillusion plan configuration file: '" +
			                            planFile.getAbsoluteFile() + "'" );
		}
		{
    		// configuration file exists, load the properties into it
    		try
    		{
    			sessionConfig = JsonUtils.readObjectFromFile( planFile );
    		}
    		catch( Exception e )
    		{
				throw new RuntimeException( "Problem parsing DISillusion plan configuration file: '" +
				                            planFile.getAbsoluteFile() + "'" + e.getMessage(),
				                            e );
    		}
		}
		
		// process entity type aliasing lookups
		entityDisEnumLookup = processEntitiesConfig(sessionConfig);
		// process well known location aliasing lookups
		locationLookup = processLocationsConfig(sessionConfig);

		// process path definitions
		JSONArray pathDefs = JsonUtils.getJSONArray( sessionConfig, PATHS_CONFIG, new JSONArray() );
		for( int idx = 0; idx < pathDefs.size(); idx++ )
		{
			JSONObject entry = JsonUtils.getJSONObject( pathDefs, idx, null );
			if( entry == null )
			{
				throw new RuntimeException( "Entry " + idx + " of plan configuration file '" +
				                            planFile.getAbsoluteFile() +
				                            "' is not a JSON object." );
			}

			EntityType entityType = processPathEntityType( entry );
			int entityCount = JsonUtils.getInteger( entry, ENTITY_COUNT, 0 );
			double entitySpacing = JsonUtils.getDouble( entry, ENTITY_SPACING, 10.0 );
			JSONObject pathDefinition = JsonUtils.getJSONObject( entry, PATH_DEFINITION, null );
			IPath entityPath = PathFactory.fromJson( pathDefinition, this.locationLookup );
			PDUAndPathContainer container = new PDUAndPathContainer( entityPath, entitySpacing );
			for( int i = 1; i <= entityCount; i++ )
			{
				// roll the siteID over if we start pushing out too many entities
				// to assign them all unique IDs
				if( (i % 65535) == 0 )
				{
					siteId = (int)(i / 65535.0)+1;
					entityId = 0;
				}
				
				EntityStatePdu pdu = new EntityStatePdu();
				pdu.setEntityID( siteId, 20913, ++entityId );
				pdu.setEntityType( entityType );

				pdu.setMarking( "D"+(idx+1)+"E"+i );
				pdu.setAppearance( new PlatformAppearance().setPowerplantOn(true).getBits() );
				LLA startLocation = entityPath.getLLA( 0, entitySpacing*(i-1) );
				WorldCoordinate ecefLocation = CoordinateUtils.toECEF(startLocation);
				pdu.setLocation( ecefLocation );
				pdu.setOrientation( CoordinateUtils.getPduEulerAngles( ecefLocation, 0 ));

				container.addEntityStatePdu( pdu );
			}
			
			EntityStatePdu pdu = new EntityStatePdu();
			pdu.setEntityID( siteId, 20913, ++entityId );
			pdu.setEntityType( new EntityType(1, 1, 225, 1, 2, 3, 4) );
			
			//pdu.setMarking( "DSRPT"+i );
			pdu.setMarking( "ER"+i );
			pdu.setAppearance( new CommonAppearance().setPowerplantOn(true).getBits() );
			pdu.setLocation( CoordinateUtils.toECEF(new LLA(-31.9522,115.8589,0)) );
			
			entities.add( pdu );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void printWelcome()
	{
		logger.info("");
		logger.info( " (                                              " );
		logger.info( " )\\ )                               )           " );
		logger.info( "(()/(  (       (      (          ( /(      (    " );
		logger.info( " /(_)) )\\  (   )(    ))\\  `  )   )\\()) (   )(   " );
		logger.info( "(_))_ ((_) )\\ (()\\  /((_) /(/(  (_))/  )\\ (()\\  " );
		logger.info( " |   \\ (_)((_) ((_)(_))( ((_)_\\ | |_  ((_) ((_) " );
		logger.info( " | |) || |(_-<| '_|| || || '_ \\)|  _|/ _ \\| '_| " );
		logger.info( " |___/ |_|/__/|_|   \\_,_|| .__/  \\__|\\___/|_|   " );
		logger.info( "                         |_|                    " );
		logger.info("");
		logger.info( "Welcome to the Disruptor - Breaking things since Two-Oh-One-Six" );
		logger.info("");

		// Log information about the available network interfaces
		NetworkUtils.logNetworkInterfaceInformation( logger );
	}

	private void sleep( long ms )
	{
		try
		{
			Thread.sleep( ms );
		}
		catch( InterruptedException ie )
		{ /*no-op*/ }
	}
	
	/**
	 * Extracts configuration specifying human readable aliases for DIS enumerations in the
	 * configuration file.
	 * 
	 * Entity definitions should be under the ENTITIES_CONFIG key, and consist of a human readable
	 * name with a DIS enumeration.
	 * 
	 * The DIS enumeration must be specified as *either*...
	 * <ul>
	 * <li>a JSONArray consisting of the DIS enumeration values</li>
	 * <li>a {@link String} value containing the DIS enumeration values</li>
	 * </ul>
	 * 
	 * In the case that the DIS enumeration is specified as a {@link String}, any non-numeric
	 * characters are treated as delimiters. This means that the following are all equivalent DIS
	 * enumerations when expressed as {@link String}s:
	 * <ul>
	 * <li>"1 1 13 3 2 1 0"</li>
	 * <li>"1.1.13.3.2.1.0"</li>
	 * <li>"1,1,13,3,2,1,0"</li>
	 * <li>"1-1-13-3-2-1-0"</li>
	 * <li>"1 1.13,3-2x1?0"</li>
	 * </ul>
	 * 
	 * NOTE:
	 * <ul>
	 * <li>"Missing" DIS enumeration values will be treated as `0`.</li> 
	 * <li>"Extra" DIS enumeration values will be ignored.</li>
	 * </ul>
	 *
	 * Example configuration segment:
	 * 
	 *     "entities":
	 * 	   {
	 *         "chinook": [1, 2, 225, 23, 1, 9, 0],
	 *         "bushmaster": "1.1.13.3.2.1.0",
	 *         "land": [1, 1],
	 *         "air": "1.2"
	 *     }
	 * 
	 * @param configRoot the root JSONObject for the configuration
	 * @return a {@link Map} of aliases to corresponding {@link EntityType}s
	 */
	private Map<String,EntityType> processEntitiesConfig( JSONObject configRoot )
	{
		Map<String,EntityType> entityDisEnumLookup = new HashMap<>();
		JSONObject entitiesJSON = JsonUtils.getJSONObject( configRoot, ENTITIES_CONFIG, new JSONObject() );
		for( Object key : entitiesJSON.keySet() )
		{
			if( key instanceof String )
			{
				String entityKey = key.toString();
				try
				{
					int[] dis = JsonUtils.getDISEnumeration( entitiesJSON, entityKey );
					EntityType entityType = new EntityType( dis );
					entityDisEnumLookup.put( entityKey, entityType );
				}
				catch( Exception e )
				{
					throw new RuntimeException( "Invalid DIS enumeration for entity type '" +
					                            entityKey + "'" );
				}
			}
			else
			{
				throw new RuntimeException( "Entity type lookup keys must be strings, but found '" +
				                            key.toString() + "'" );
			}
			
		}
		return entityDisEnumLookup;
	}

	/**
	 * Extracts configuration specifying human readable aliases for locations in the
	 * configuration file.
	 * 
	 * Location definitions should be under the LOCATIONS_CONFIG key, and consist of a human readable
	 * name with a latitude/longitude/altitude definition.
	 * 
	 * Latitude and longitude values are in degrees.
	 * 
	 * Altitude values are in meters.
	 * 
	 * Example configuration segment:
	 * 
	 *     "locations":
	 *     {
	 *         "perth": {"lat": -31.9522, "lon": 115.8589, "alt": 0},
	 *         "new york": {"lat": 40.6611, "lon": -73.9438, "alt": 0},
	 *         "orbit-center": {"lat": -31.9522, "lon": 115.8589, "alt": 5000}
	 *     }
	 * 
	 * @param configRoot the root JSONObject for the configuration
	 * @return a {@link Map} of aliases to corresponding {@link LLA}s
	 */
	private Map<String,LLA> processLocationsConfig( JSONObject configRoot )
	{
		Map<String,LLA> locationLookup = new HashMap<>();
		JSONObject locationsJSON =
		    JsonUtils.getJSONObject( configRoot, LOCATIONS_CONFIG, new JSONObject() );
		for( Object key : locationsJSON.keySet() )
		{
			if( key instanceof String )
			{
				String locationKey = key.toString();
				try
				{
    				JSONObject locationJSON = JsonUtils.getJSONObject( locationsJSON, locationKey, null );
    				LLA location = LLA.fromJSON( locationJSON );
    				locationLookup.put( locationKey, location );
				}
				catch( Exception e )
				{
					throw new RuntimeException( "Invalid LLA for location '" + locationKey + "'" );
				}
			}
			else
			{
				throw new RuntimeException( "Location lookup keys must be strings, but found '" +
				                            key.toString() + "'" );
			}
		}
		return locationLookup;
	}
	
	/**
	 * Utility method to extract the entity type for a path from either the `entityDisEnumLookup`
	 * alias lookup table or as a "raw" DIS enumeration.
	 * 
	 * @param pathRoot the root JSONObject for the path configuration
	 * @return an {@link EntityType}
	 */
	private EntityType processPathEntityType( JSONObject pathRoot )
	{
		Object entityTypeDef = pathRoot.get( PATH_ENTITY_TYPE );
		EntityType entityType = this.entityDisEnumLookup.get( entityTypeDef );
		
		if( entityType != null )
			return entityType;

		int[] dis = JsonUtils.getDISEnumeration( pathRoot, PATH_ENTITY_TYPE, null );
		return new EntityType( dis );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
