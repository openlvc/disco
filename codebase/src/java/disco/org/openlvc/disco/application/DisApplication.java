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

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.bus.MessageBus;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;

/**
 * For applications that don't want to work directly with the complete stream of PDUs, or want
 * additional management services on top of a raw DIS connection, the {@link DisApplication}
 * class should be the main entry point. It provides a set of building blocks that sit above
 * the raw connection elements and provide facilities to:
 * 
 * <ul>
 *   <li>Store and retrieve last known status of entities, transmitters, emitters, ...</li>
 *   <li>Automatically handle common tasks like issuing of heartbeats for locally created entities</li>
 *   <li>Subscribe to receive updates only for particular types of PDUs</li>
 *   <li>...</li>
 * </ul>
 * 
 * To use this capability, create a {@link DisApplication} rather than an {@link OpsCenter} and
 * start to use the components it provides.
 */
public class DisApplication
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration configuration;
	private OpsCenter opscenter;
	private Logger logger;

	// PDU Storage and Management
	private PduStore pduStore;
	private MessageBus<PDU> pduBus;
	
	// Heartbeats and Delete Timeouts
	private DeleteReaper deleteReaper;
	

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DisApplication()
	{
		this.configuration = new DiscoConfiguration();
		this.opscenter = null; // set in start()
		
		// Heartbeats and Delete Timeouts
		this.deleteReaper = new DeleteReaper( this );

		// PDU Storage and Management
		this.pduStore = new PduStore( this );
		this.pduBus = new MessageBus<>();
	}

	public DisApplication( DiscoConfiguration configuration )
	{
		this();
		this.configuration = configuration;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Management Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void start()
	{
		// fetch our logger so that we're ready
		this.logger = configuration.getDiscoLogger();
		
		// burn the existing pdu store to avoid stale data when re-openings happening
		this.pduStore.clear();
		
		// create the pieces that we need
		this.opscenter = new OpsCenter( this.configuration );
		this.opscenter.setPduListener( new PduListener() );
		
		// open the connection up
		this.opscenter.open();
		
		// start the recurring tasks
		this.deleteReaper.start();
	}
	
	public void stop()
	{
		// close off the recurring tasks
		this.deleteReaper.stop();
		
		// close off the stream of data
		this.opscenter.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Sender Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Send the given PDU out to the network through whatever connection Disco is configured for.
	 * 
	 * @param pdu The PDU to serialize and send
	 */
	public void send( PDU pdu )
	{
		opscenter.send( pdu );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Subscriber Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Add a PDU subscriber. This subscriber should contain methods that carry the
	 * {@link EventHandler} annotation. As PDUs are received, they will be passed
	 * through to all subscribers with event handlers methods that match the PDU type
	 * (or any parent type).
	 * 
	 * @param subscriber The subscriber to call
	 * @throws DiscoException If the subscriber incorrectly uses the message bus annotations
	 * @see org.openlvc.disco.bus.MessageBus
	 * @see org.openlvc.disco.bus.EventHandler
	 * @see org.openlvc.disco.bus.ErrorHandler
	 */
	public void addSubscriber( Object subscriber )
	{
		this.pduBus.subscribe( subscriber );
	}

	/**
	 * Remove any subscriptions that the provided subscriber object has. This will only work
	 * for this specific instance.
	 * 
	 * @param subscriber The instance to remove subscriptions for.
	 */
	public void removeSubscriber( Object subscriber )
	{
		this.pduBus.unsubscribe( subscriber );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected Logger getLogger()
	{
		return this.logger;
	}
	
	public DiscoConfiguration getConfiguration()
	{
		return this.configuration;
	}
	
	public PduStore getPduStore()
	{
		return this.pduStore;
	}

	///////////////////////////////////
	/// Delete Reaper Methods   ///////
	///////////////////////////////////
	protected DeleteReaper getDeleteReaper()
	{
		return this.deleteReaper;
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
		this.deleteReaper.setDeleteTimeout( millis );
	}

	/**
	 * @return The max period of time between updates that associated data will be considered
	 *         valid for. If the last time we received a PDU was beyond this many milliseconds
	 *         ago, that data will be removed.
	 */
	public long getDeleteTimeout()
	{
		return this.deleteReaper.getDeleteTimeout();
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Class: PduListener   /////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class PduListener implements IPduListener
	{
		@Override
		public void receive( PDU pdu )
		{
			// Step 1: Pass the update to the store
			pduStore.pduReceived( pdu );
			
			// Step 2: Notify all subscribers
			pduBus.publish( pdu );
		}
	}

	
	public static void main( String[] args ) throws Exception
	{
		DisApplication app = new DisApplication();
		//app.getConfiguration().getLoggingConfiguration().setLevel( "TRACE" );
		//app.getConfiguration().getRprConfiguration().setRtiProvider( "Pitch" );
		//app.getConfiguration().getRprConfiguration().setRtiInstallDir( "C:\\Program Files\\prti1516e" );
		//app.getConfiguration().setConnection( "rpr" );
		app.start();
		
		Thread.sleep( 30000 );
		app.stop();
	}
	
}
