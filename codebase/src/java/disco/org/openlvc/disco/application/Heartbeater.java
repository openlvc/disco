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
package org.openlvc.disco.application;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.pdu.PDU;

/**
 * The Heartbeater will periodically sweep over all registered PDUs, and if their timestamp is
 * older than the designated heartbeat period it will serialize and send them to the network as
 * a heartbeat. The two settings that matter for the heartbeater are:
 * <ul>
 *   <li>Test Period: How often the heartbeater will assess its set of PDUs to decide whether
 *                    a heartbeat is required (default: 5 seconds).</li>
 *   <li>Heartbeat Period: How long it must have been since a PDU was last updated before a
 *                         heartbeat is generated (default: 10 seconds).</li>
 * </ul>
 *
 * <b>Registered PDUs</b><br/>
 * <p>For a PDU to be managed by the heartbeater, it must be registered with it. Typically you will
 * do this for PDUs that represent persistent objects. As long as a PDU is registered with the
 * heartbeater, it will continue to be resent. If the object should be removed from the simulation,
 * it should be unregistered with the heartbeater.</p>
 * 
 * <b>Updates outside the Heartbeater</b><br/>
 * <p>If a PDU is updated outside the heartbeater, this will cause its local timestamp to be
 * updated. This in turn will cause the heartbeat period calculation to readjust for that PDU,
 * resulting in a being unlikely that a heartbeat will be needed.</p> 
 */
public class Heartbeater implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DisApplication app;
	private Set<PDU> registeredPdus;
	private volatile long testPeriod;
	private volatile long heartbeatPeriod;
	private Logger logger;
	
	private Thread thread;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Heartbeater( DisApplication app )
	{
		this.app = app;
		this.registeredPdus = new HashSet<>();
		this.testPeriod = 5000;
		this.heartbeatPeriod = 10000;
		this.logger = null; // set in run()
		this.thread = null; // set in start()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void start()
	{
		this.thread = new Thread( this, "Heartbeater" );
		this.thread.start();
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
		this.logger = app.getLogger();
		if( this.heartbeatPeriod == 0 || this.testPeriod == 0 )
		{
			logger.info( "Disabling heartbeater: Heartbeat or test period is 0" );
			return;
		}
		else
		{
			logger.info( "Starting heartbeater: Max age %d millis", heartbeatPeriod );
		}

		while( Thread.interrupted() == false )
		{
			// Sleep for a while; typically a period 1/5th the delete timeout
			try
			{
				Thread.sleep( testPeriod );
			}
			catch( InterruptedException ie )
			{
				return;
			}
			
			// Collect all the souls
			heartbeat();
		}
	}
	
	private void heartbeat()
	{
		long oldestAllowable = System.currentTimeMillis()-heartbeatPeriod;
		logger.trace( "Heartbeating PDUs not updated since %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", oldestAllowable );

		int heartbeats = 0;
		for( PDU pdu : registeredPdus )
		{
			if( oldestAllowable >= pdu.getLocalTimestamp() )
			{
				app.send( pdu );
				heartbeats++;
			}
		}
		
		logger.trace( "Sent [%d] heartbeats", heartbeats );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void registerPdu( PDU pdu )
	{
		this.registeredPdus.add( pdu );
	}
	
	public void unregisterPdu( PDU pdu )
	{
		this.registeredPdus.remove( pdu );
	}
	
	public boolean isRegistered( PDU pdu )
	{
		return this.registeredPdus.contains( pdu );
	}

	/**
	 * @return The period in milliseconds between test sweeps over the set of registered PDUs
	 */
	public long getTestPeriod()
	{
		return this.testPeriod;
	}

	/**
	 * Set how often we will sweep over the set of registered PDUs to determine whether any of
	 * them need to be sent as a heartbeat. If this is set to 0 the heartbeater will be disabled.
	 * 
	 * @param period The period in milliseconds between heartbeat sweep checks. Make sure not to
	 *               set this to longer than the heartbeat period. Typically a 1/2 or 1/3rd ratio
	 *               is enough.
	 */
	public void setTestPeriod( long period )
	{
		this.testPeriod = period;
	}

	/**
	 * @return How long it must have been since a PDU was sent to the network before we flush out
	 *         a heartbeat (in millis).
	 */
	public long getHeartbeatPeriod()
	{
		return this.heartbeatPeriod;
	}

	/**
	 * Set the minimum period it must have been since the last time a PDU was send before it will
	 * be sent again.
	 * 
	 * @param period The period in milliseconds between when heartbeat PDUs will be issued.
	 */
	public void setHeartbeatPeriod( long period )
	{
		this.heartbeatPeriod = period;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
