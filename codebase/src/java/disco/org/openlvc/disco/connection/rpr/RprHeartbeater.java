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

import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;

import hla.rti1516e.AttributeHandleValueMap;

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
		this.heartbeatPeriod = 10000;
		this.thread = null; // set in start()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void start()
	{
		this.thread = new Thread( this, "RprHeartbeater" );
		this.thread.start();
	}
	
	public void stop()
	{
		if( this.thread == null )
			return;
		
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
		
		// Get discovered objects not updated since the timeout time
		Collection<ObjectInstance> oldies =
			connection.getObjectStore().getDiscoveredHlaObjectsMatching( oi -> oi.getLastUpdatedTime() < staleTime );
		
		// If there is nothing to do, do nothing
		if( oldies.isEmpty() )
			return;

		// Create an empty attribute value set we'll use in the synthesized reflection calls
		AttributeHandleValueMap empty = getEmptyAttributes();
		
		int updateCount = 0;
		for( ObjectInstance hlaObject : oldies )
		{
			if( hlaObject.isReady() == false )
				continue;
			
			// Generate a fake reflection event so that we can stimulate a PDU
			connection.receiveHlaReflection( hlaObject.getObjectHandle(), empty );
			++updateCount;
		}
		
		logger.trace( "hla >> dis (Heartbeat) Generated %d heartbeat events", updateCount );
	}

	private AttributeHandleValueMap getEmptyAttributes() throws DiscoException
	{
		// Is the HLA API the most terrible API to work with? Yes/Yes?
		try
		{
			return connection.getRtiAmb().getAttributeHandleValueMapFactory().create( 0 );
		}
		catch( Exception e )
		{
			// won't happen... or WILL it!? Duh duh DUH.
			throw new DiscoException( e.getMessage(), e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
