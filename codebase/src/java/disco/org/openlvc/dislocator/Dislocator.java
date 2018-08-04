/*
 *   Copyright 2018 Open LVC Project.
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
package org.openlvc.dislocator;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.duplicator.Replayer;

public class Dislocator implements IPduListener
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;
	
	// DIS Properties 
	private String trackingEntityMarking;	
	private OpsCenter opscenter;  // use this if mode is Network
	private Replayer replayer;    // use this if mode is File
	
	// NMEA Server
	private NmeaServer nmeaServer;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Dislocator( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getDislocatorLogger();
		this.opscenter = null; // set in execute()
		this.replayer = null;  // set in execute()
		
		// Get the entity marking we're tracking and make sure we reduce it to the
		// maximum size the marking can be
		if( configuration.hasTrackingEntity() == false )
			throw new DiscoException( "Tracking entity has not been set; no entity to locate" );

		this.trackingEntityMarking = configuration.getTrackingEntity();
		int maxSize = trackingEntityMarking.length() > 11 ? 11 : trackingEntityMarking.length(); 
		this.trackingEntityMarking = trackingEntityMarking.substring( 0, maxSize );
		
		// NMEA 0183 Server
		this.nmeaServer = new NmeaServer( configuration );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void execute()
	{
		printWelcome();
		
		//
		// Set up DIS feed
		//
		try
		{
    		if( configuration.isModeNetwork() )
    		{
        		// Open up the Disco Operations Centre
    			logger.info( "(Mode:Network) Connecting to DIS network" );
        		opscenter = new OpsCenter( configuration.getDiscoConfiguration() );
        		opscenter.setListener( this );
        		opscenter.open();
        		logger.info( "Connected to DIS network" );
    		}
    		else
    		{
    			logger.info( "(Mode:File) Session File: "+configuration.getSessionFile().getAbsolutePath() );
    			replayer = new Replayer( configuration.getSessionFile() );
    			replayer.setLogger( logger );
    			replayer.setListener( this );
    			replayer.startReplay();
    			logger.info( "File replay is active" );
    		}
		}
		catch( RuntimeException e )
		{
			try { nmeaServer.close(); } catch( Exception | Error ex ) {}
			throw e;
		}

		logger.info( "Locating and tracking marking: "+this.trackingEntityMarking );

		// Open the NMEA Server first
		nmeaServer.open();
	}

	public void shutdown()
	{
		logger.info( "Shutting the Dislocator down" );
		try { nmeaServer.close();    } catch( Exception | Error e ) {}
		try { opscenter.close();     } catch( Exception | Error e ) {}
		try { replayer.stopReplay(); } catch( Exception | Error e ) {}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Processing Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void receiver( PDU pdu )
	{
		if( pdu.getType() != PduType.EntityState )
			return;
		
		EntityStatePdu espdu = (EntityStatePdu)pdu;
		String marking = espdu.getMarking();

		if( marking.length() != 11 )
			System.out.println( "Marking != 11: ["+marking+"]" );
		
		// is this the marking we want?
		if( marking == null || marking.trim().equalsIgnoreCase(trackingEntityMarking) == false )
			return;

		// this it the one we want!
		nmeaServer.updateLocation( espdu.getLocation().toLLA() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void printWelcome()
	{
		logger.info( "" );
		logger.info( "    ____  _      __                 __              " );
		logger.info( "   / __ \\(_)____/ /___  _________ _/ /_____  _____  " );
		logger.info( "  / / / / / ___/ / __ \\/ ___/ __ `/ __/ __ \\/ ___/  " );
		logger.info( " / /_/ / (__  ) / /_/ / /__/ /_/ / /_/ /_/ / /      " );
		logger.info( "/_____/_/____/_/\\____/\\___/\\__,_/\\__/\\____/_/       " );
		logger.info( "" );
		logger.info( "Welcome to the Dislocator - Locating your DIS entity for all your tracking needs" );
		logger.info("");
		
		// Log information about the available network interfaces
		NetworkUtils.logNetworkInterfaceInformation( logger );
	}

	public String toString()
	{
		return "Dislocator";
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
