/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.disco.connection.rpr;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.connection.rpr.objects.BaseEntity;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;

/**
 * This class generates artifical heartbeat PDUs for objects that have been discovered from
 * the HLA. It will loop over the object store and for appropriate types (PhysicalEntity for
 * example) it will generate a heartbeat PDU if there has not been one on a configurable period
 * of time.
 */
public class RprHeartbeater implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RprConnection connection;
	private Logger logger;
	private volatile long heartbeatPeriod;
	private Thread thread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected RprHeartbeater( RprConnection connection )
	{
		this.connection = connection;
		this.logger = null; // set in run()
		this.heartbeatPeriod = 60000;
		this.thread = null; // set in start()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void start()
	{
		this.thread = new Thread( this, "RprHeartbeater" );
//		this.thread.start();
	}
	
	public void stop()
	{
		this.thread.interrupt();
		try
		{
			this.thread.join( 1000 );
		}
		catch( InterruptedException ie )
		{ /*no-op*/ }
	}
	
	public void run()
	{
		this.logger = this.connection.getLogger();
		if( this.heartbeatPeriod == 0 )
		{
			logger.info( "Disabling RPR Heartbeater: Heartbeat period set to 0" );
			return;
		}
		else
		{
			logger.info( "Starting RPR Heartbeater: Max age %d millis", heartbeatPeriod );
		}

		while( Thread.interrupted() == false )
		{
			// Sleep for a while; typically a period 1/5th the delete timeout
			try
			{
				long sleepTime = Math.max( 3000, heartbeatPeriod/5 ); // run at least every 3s
				Thread.sleep( sleepTime );
			}
			catch( InterruptedException ie )
			{
				return;
			}
			
			// Process all objects and announce
			flush();
		}
	}

	private void flush()
	{
		long staleTime = System.currentTimeMillis()-heartbeatPeriod;
		logger.trace( "hla >> dis (Heartbeat) Check for objects to heartbeat (not updated since %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS)",
		              staleTime );
		
		// Entity States
		List<ObjectInstance> oldies =
			connection.getObjectStore().getDiscoveredHlaObjectsNotUpdatedSince( staleTime );
		
		int pduCount = 0;
		for( ObjectInstance hlaObject : oldies )
		{
//			if( hlaObject instanceof BaseEntity == false )
//				continue;

			// This is an entity state; flush it
			connection.getOpsCenter().getPduReceiver().receive( hlaObject.toPdu().toByteArray() );
			++pduCount;
			if( logger.isTraceEnabled() )
			{
				logger.trace( "hla >> dis (Heartbeat) Sent heartbeat EntityState for %s",
				              ((BaseEntity)hlaObject).getEntityIdentifier().toString() );
			}
		}
		
		logger.trace( "hla >> dis (Heartbeat) Sent %d heartbeat PDUs", pduCount );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
