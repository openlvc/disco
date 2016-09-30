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

import java.util.List;
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
	private List<ILink> links;
	private BlockingQueue<Message> queue;
	private Thread workerThread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Reflector( Distributor distributor )
	{
		this.distributor = distributor;
		this.links = distributor.links;
		this.queue = new LinkedBlockingQueue<>();
		this.workerThread = null; // set when brought online
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Queue the message for reflection to all other attached links, but first check to see if
	 * it passes the outbound filter of the link that is sending it to us.
	 * <p/>
	 * 
	 * We do this explicitly here, rather than leaving it to each of the Link implementations
	 * so that it is applied in a consistent manner. This method is typically called from the
	 * primary processing thread used by the link, so the cost of executing it is loaded onto
	 * the link and not the reflector.
	 * <p/>
	 * 
	 * An internal queue is maintained by the reflector. If this queue is out of space, calls
	 * to this method will block until the message can be inserted.
	 * 
	 * @param message The message to queue for reflection
	 * @throws InterruptedException If the thread was interrupted while waiting as part of the
	 *                              queue submission operation.
	 */
	public void reflect( Message message ) throws InterruptedException
	{
		if( message.getSouce().passesInboundFilter(message.getPdu()) )
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
	/**
	 * @return A reference back to the core Distributor instance that this reflector is servicing.
	 */
	public Distributor getDistributor()
	{
		return this.distributor;
	}

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
					links.stream().filter( link -> link.isUp() )
					              .filter( link -> link != message.getSouce() )
					              .filter( link -> link.passesOutboundFilter(message.getPdu()) )
					              .forEach( link -> link.reflect(message) );
				}
				catch( InterruptedException ie )
				{
					return;
				}
				catch( Exception e )
				{
					distributor.getLogger().warn( "Exception in reflector, skipping PDU. Message: "+
					                              e.getMessage(), e );
				}
			}
		}
	}

}
