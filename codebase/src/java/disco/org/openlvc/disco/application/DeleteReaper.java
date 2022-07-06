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

public class DeleteReaper implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DisApplication app;
	private Set<IDeleteReaperManaged> targets;
	private volatile long deleteTimeout;
	private Logger logger;
	
	private Thread thread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected DeleteReaper( DisApplication app )
	{
		this.app = app;
		this.targets = new HashSet<>();
		this.deleteTimeout = 60000;
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
		this.thread = new Thread( this, "DeleteReaper" );
		this.thread.start();
	}
	
	public void stop()
	{
		if( this.thread == null )
		{
			return;
		}
		
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
		if( this.deleteTimeout == 0 )
		{
			logger.info( "Disabling delete timeout reaper: Delete timeout is 0" );
			return;
		}
		else
		{
			logger.info( "Starting delete timeout reaper: Max age %d millis", deleteTimeout );
		}

		while( Thread.interrupted() == false )
		{
			// Sleep for a while; typically a period 1/5th the delete timeout
			try
			{
				long sleepTime = Math.max( 3000, deleteTimeout/5 ); // wait at least 3 seconds
				Thread.sleep( sleepTime );
			}
			catch( InterruptedException ie )
			{
				return;
			}
			
			// Collect all the souls
			harvest();
		}
	}
	
	private void harvest()
	{
		long timeOfDeath = System.currentTimeMillis()-deleteTimeout;
		logger.trace( "Removing data not updated since %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", timeOfDeath );
		
		for( IDeleteReaperManaged target : this.targets )
		{
			int removed = target.removeStaleData( timeOfDeath );
			if( removed > 0 )
				logger.debug( "Removed [%d] records from [%s]", removed, target.getClass().getSimpleName() );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void registerTarget( IDeleteReaperManaged target )
	{
		this.targets.add( target );
	}
	
	/**
	 * Specify the application delete timeout.
	 * <p/>
	 * 
	 * Periodically (typically 1/5th of the given value) a thread will loop over all the data
	 * we have collected and remove any that has not been updated within the last x milliseconds
	 * (as given in the argument).
	 * 
	 * @param millis How long it can be between updates before data is considered stale and removed.
	 *               Time in milliseconds.
	 */
	public void setDeleteTimeout( long millis )
	{
		this.deleteTimeout = millis;
	}
	
	/**
	 * @return The max period of time between updates that associated data will be considered
	 *         valid for. If the last time we received a PDU was beyond this many milliseconds
	 *         ago, that data will be removed.
	 */
	public long getDeleteTimeout()
	{
		return this.deleteTimeout;
	}
		
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
