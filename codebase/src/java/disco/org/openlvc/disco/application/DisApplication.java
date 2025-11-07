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

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.bus.ErrorHandler;
import org.openlvc.disco.bus.MessageBus;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.record.EntityId;

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
	
	// State Management Services and Helpers
	private Heartbeater heartbeater;
	private DeleteReaper deleteReaper;
	private AtomicInteger entityCounter;
	

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DisApplication( DiscoConfiguration configuration )
	{
		this.configuration = configuration;
		this.opscenter = null; // set in start()
		
		// State Management Services and Helpers
		this.heartbeater = new Heartbeater( this );
		this.deleteReaper = new DeleteReaper( this );
		this.entityCounter = new AtomicInteger(0);

		// PDU Storage and Management
		this.pduStore = new PduStore( this );
		this.pduBus = new MessageBus<>();
		this.pduBus.subscribe( new ApplicationBusErrorReporter() );
	}

	public DisApplication()
	{
		this( new DiscoConfiguration() );
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
		this.heartbeater.start();
		this.deleteReaper.start();
		this.entityCounter.set(0);
	}
	
	public void stop()
	{
		// close off the recurring tasks
		this.heartbeater.stop();
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
	
	/**
	 * Sends the given PDU out to the network through whatever connection Disco is configured for without 
	 * making any modifications or additions (such as settings its exercise/site/app ids or anything like 
	 * that). Just send the PDU straight through as it is.
	 * 
	 * @param pdu The PDU to serialize and send
	 */
	public void sendRaw( PDU pdu )
	{
		opscenter.sendRaw( pdu );
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

	public Heartbeater getHeartbeater()
	{
		return this.heartbeater;
	}

	public DeleteReaper getDeleteReaper()
	{
		return this.deleteReaper;
	}

	/**
	 * The {@link DisApplication} maintains a counter for locally created/managed objects.
	 * You can combine this with the site/app ids to generate a unique identifier on the network.
	 * This method will instantiate and return the next available entity number.
	 * <p/>
	 * The entity id is defined as a Uint16 in the spec, so the maximum range you have to work with
	 * is 1-65535. If you exceed the upper bound, an exception will be thrown.
	 * 
	 * @return The next available entity number for the simulation application
	 * @throws DiscoException If you exceed the upper bound that an entity ID can be in DIS
	 */
	public int getNextEntityNumber() throws DiscoException
	{
		int next = this.entityCounter.incrementAndGet();
		if( next > DisSizes.UI16_MAX_VALUE )
			throw new DiscoException( "You have exhausted all available ids [max: 65535]" );
		else
			return next;
	}

	/**
	 * The {@link DisApplication} maintains an entity counter you can use to generate new entity
	 * ids on the network. This method will generate a new {@link EntityId} that combines the site
	 * and app ids from the DIS configuration with the next available entity number.
	 * <p/>
	 * Entity numbers are just sequentially incremented. The spec defines them as a uint16, so you
	 * only have 65535 to work with - be careful! If you exceed this threshold, an exception will
	 * be thrown when you try to generate a new ID
	 * 
	 * @return A new {@link EntityId} whose application number is the next avaialable
	 * @throws DiscoException If you exceed the threshold for the max number of entities that can
	 *                        be uniquely identified within the DIS spec.
	 */
	public EntityId getNextEntityId() throws DiscoException
	{
		return new EntityId( configuration.getDisConfiguration().getSiteId(),
		                     configuration.getDisConfiguration().getAppId(),
		                     getNextEntityNumber() );
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Class: BusErrorReporter   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public class ApplicationBusErrorReporter
	{
		@ErrorHandler
		public void receive( Throwable throwable, Object target )
		{
			logger.error( throwable );
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
