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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.utils.CoordinateUtils;
import org.openlvc.disco.utils.LLA;
import org.openlvc.disruptor.configuration.Configuration;

public class Disruptor
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;
	private OpsCenter opscenter;

	private List<EntityStatePdu> entities;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Disruptor( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getDisruptorLogger();
		this.opscenter = null; // set in execute()
		
		this.entities = new ArrayList<>();
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
		opscenter.setReceiver( new PduReceiver() );
		opscenter.open();

		try
		{
    		// Do our work
    		for( int i = 1; i <= configuration.getLoops(); i++ )
    		{
    			tick( i );
    			sleep( configuration.getTickInterval() );
    		}
    
    		logger.info( "Execution over - you can breathe again" );
		}
		finally
		{
			opscenter.close();
		}
	}

	
	private void tick( int count )
	{
		long startTime = System.currentTimeMillis();
		int espdus = 0;
		int firepdus = 0;
		int detpdus = 0;
		
		for( EntityStatePdu entity : entities )
		{
			opscenter.send( entity );
			++espdus;
		}

		long endTime = System.currentTimeMillis();
		int totalpdus = espdus + firepdus + detpdus;
		
		
//		"[     1] Tick Completed -- 100,000pdu in 875ms (102,444/s)
//		"[     1]   Entity State -- 100,000            | "
//		"[     1]           Fire -- 0"
//		"[     1]     Detonation -- 0"

		// do some precalculation on some fields
		long periodMillis = (endTime - startTime);
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
		
		for( int i = 1; i <= configuration.getObjectCount(); i++ )
		{
			// roll the siteID over if we start pushing out too many entities
			// to assign them all unique ids
			if( (i % 65535) == 0 )
			{
				siteId = (int)(i / 65535.0)+1;
				entityId = 0;
			}
			
			EntityStatePdu pdu = new EntityStatePdu();
			pdu.setEntityID( siteId, 20913, ++entityId );
			pdu.setEntityType( new EntityType(1, 1, 225, 1, 2, 3, 4) );
			pdu.setMarking( "DSRPT"+i );
			pdu.setLocation( CoordinateUtils.toECEF(new LLA(-31.95224,115.8614,0)) );
			
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
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
