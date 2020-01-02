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
package org.openlvc.distributor.links.pulse;

import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.LLA;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;

/**
 * Generates a pulse PDU on the reflector at a regular interval.
 * <p/>
 * Pulse is an EntityStatePdu. A manned space platform of unknown origin sitting at 200km
 * (the leading edge of Medium Earth Orbit).
 */
public class PulseLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;

	private Pulser pulserThread;
	private EntityStatePdu pdu;
	
	private long interval = 5000;
	private String enumeration = "1.5.0.1.0.0.0"; 
	private String marking     = "RELAY";
	private short exerciseId   = 0;
	private int siteId         = 0;
	private int appId          = 0;
	private int entityId       = 1;
	private LLA entityLocation = LLA.fromDegrees( 0.0, 0.0, 2000000.0 ); // 2000km above, medium earth orbit edge
	
	private int pulseCount = 0;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PulseLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		// Loaded when link is brought up
		this.reflector = null;
		this.pdu = null;       // set in up()
		this.pulseCount = 0;   // reset in up()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		if( isUp() )
			return;

		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );
		
		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: Heartbeat" );
		
		// fetch our configuration properties
		this.interval    = linkConfiguration.getPulseInterval();
		this.marking     = linkConfiguration.getPulseMarking();
		this.enumeration = linkConfiguration.getPulseEnumeration();
		this.exerciseId  = linkConfiguration.getPulseExerciseId();
		this.siteId      = linkConfiguration.getPulseSiteId();
		this.appId       = linkConfiguration.getPulseAppId();

		// create the PDU that we'll pulse out
		this.pdu = new EntityStatePdu();
		this.pdu.getHeader().setExerciseId( this.exerciseId );
		this.pdu.setMarking( this.marking );
		this.pdu.setEntityType( EntityType.fromString(this.enumeration) );
		this.pdu.setEntityID( this.siteId, this.appId, this.entityId );
		this.pdu.setLocation( new WorldCoordinate(entityLocation) );
		this.pdu.setForceID( ForceId.Other );
		
		// bring the pulser thread up
		this.pulseCount = 0;
		this.pulserThread = new Pulser();
		this.pulserThread.start();
		
		logger.debug( "Link is up" );
		super.linkUp = true;
	}
	
	public void down()
	{
		if( isDown() )
			return;

		logger.debug( "Taking down link: "+super.getName() );
		
		// shut the pulser thread down
		this.pulserThread.interrupt();
		ThreadUtils.exceptionlessThreadJoin( pulserThread, 3000 );
		
		logger.debug( "Link is down" );

		super.linkUp = false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		// disregard anything incoming
		
		//logger.log( Level.INFO,
		//            "PDU from [%s] >> type=%s",
		//            message.getSouce().getName(),
		//            message.getPdu().getType() );
	}

	public void setReflector( Reflector reflector )
	{
		this.reflector = reflector;
	}
	
	private void pulse() throws InterruptedException
	{
		this.reflector.reflect( new Message(this,pdu) );
		logger.debug( "Pulsed out heartbeat %d for %s (%s)",
		              ++this.pulseCount,
		              pdu.getMarking(),
		              pdu.getExerciseId()+"-"+pdu.getEntityID() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getStatusSummary()
	{
		// return live, resolved connection information
		return String.format( "{ Pulse, count: %d, interval:%s, marking:%s, id:%s }",
		                      this.pulseCount,
		                      this.interval,
		                      this.marking,
		                      this.exerciseId+"-"+this.siteId+"-"+this.appId+"-"+this.entityId );
	}
	
	public String getConfigSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			// return live, resolved connection information
			return String.format( "{ Pulse, interval:%s, marking:%s, id:%s }",
			                      this.interval,
			                      this.marking,
			                      this.exerciseId+"-"+this.siteId+"-"+this.appId+"-"+this.entityId );
		}
		else
		{
			// return raw configuration information
			return String.format( "{ Pulse, interval:%s, marking:%s, id:%s }",
			                      linkConfiguration.getPulseInterval(),
			                      linkConfiguration.getPulseMarking(),
			                      linkConfiguration.getPulseExerciseId()+"-"+
			                      linkConfiguration.getPulseSiteId()+"-"+
			                      linkConfiguration.getPulseAppId()+"-"+
			                      this.entityId );
		}
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/////////////////////////////////////////////////////////////////////////////////////
	/// Pulse Triggering  ///////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	private class Pulser extends Thread
	{
		private Pulser()
		{
			super( "Pulser" );
			super.setDaemon( true );
		}

		public void run()
		{
			try
			{
				while( !Thread.interrupted() )
				{
					pulse();
					Thread.sleep( 5000 );
				}
			}
			catch( InterruptedException ie )
			{
				// ignore - shutdown time
			}
		}
	}
}