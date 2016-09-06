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
package org.openlvc.distributor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link Reflector} is the central clearing house for all incoming messages.
 * Incoming packets are offered to the reflector, which will then queue and send
 * them out to all the other links.
 */
public class Reflector
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Distributor distributor;
	private BlockingQueue<Message> queue;
	private Thread workerThread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Reflector( Distributor distributor )
	{
		this.distributor = distributor;
		this.queue = new LinkedBlockingQueue<>();
		this.workerThread = null; // set when brought online
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void reflect( Message message ) throws InterruptedException
	{
		this.queue.put( message );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void up()
	{
		// make sure we are not already up
		if( isUp() )
			return;
		
		this.workerThread = new Thread( new Worker(), "Reflector" );
		this.workerThread.start();
	}
	
	protected void down()
	{
		// make sure we are not already down
		if( isDown() )
			return;
		
		// interrupt the thread and wait for it to finish
		try
		{
			this.workerThread.interrupt();
			this.workerThread.join();
		}
		catch( InterruptedException ie )
		{
			// nothing to do
		}
		finally
		{
			this.workerThread = null;
		}
	}

	public final boolean isUp()
	{
		return this.workerThread != null;
	}
	
	public final boolean isDown()
	{
		return this.workerThread == null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Class: Reflector   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class Worker implements Runnable
	{
		public void run()
		{
			while( Thread.interrupted() == false )
			{
				try
				{
					Message message = queue.take();
					distributor.links.values()
					                 .stream()
					                 .filter( link -> link.isUp() )
					                 .filter( link -> link != message.getSouce() )
					                 .forEach( link -> link.reflect(message) );
				}
				catch( InterruptedException ie )
				{
					return;
				}
			}
		}
	}

}
