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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

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

	// PDU Storage and Management
	private PduStore pduStore;
	private ConcurrentMap<PduType,List<IPduSubscriber>> pduSubscribers;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DisApplication()
	{
		this.configuration = new DiscoConfiguration();
		this.opscenter = null; // set in start()
		
		// PDU Storage and Management
		this.pduStore = new PduStore();
		this.pduSubscribers = new ConcurrentHashMap<>();
		// initialize the subscriber lists so that we don't have to check each time
		for( PduType type : PduType.getSupportedPdus() )
			pduSubscribers.put( type, new CopyOnWriteArrayList<>() );
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
		// burn the existing pdu store to avoid stale data when re-openings happening
		this.pduStore.clear();
		
		// create the pieces that we need
		this.opscenter = new OpsCenter( this.configuration );
		this.opscenter.setListener( new PduListener() );
		
		// open the connection up
		this.opscenter.open();
	}
	
	public void stop()
	{
		// close off the stream of data
		this.opscenter.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Subscriber Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void addSubscription( PduType pduType, IPduSubscriber... subscribers )
	{
		// check to make sure we're tracking a subscriber set for this PDU type
		if( this.pduSubscribers.containsKey(pduType) == false )
			this.pduSubscribers.put( pduType, new CopyOnWriteArrayList<>() );

		pduSubscribers.get(pduType).addAll( Arrays.asList(subscribers) );
	}
	
	public void removeSubscription( PduType pduType, IPduSubscriber... subscribers )
	{
		if( pduSubscribers.containsKey(pduType) )
			pduSubscribers.get(pduType).removeAll( Arrays.asList(subscribers) );
	}
	
	public void removeAllSubscriptions( IPduSubscriber subscriber )
	{
		pduSubscribers.forEach( (k,v) -> v.remove(subscriber) );
	}
	
	public boolean isSubscribedTo( IPduSubscriber subscriber, PduType pduType )
	{
		return pduSubscribers.containsKey(pduType) &&
		       pduSubscribers.get(pduType).contains(subscriber);
	}


	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public PduStore getPduStore()
	{
		return this.pduStore;
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
			PduType type = pdu.getType();
			pduSubscribers.get(type).forEach( subscriber -> subscriber.pduReceived(type,pdu) );
		}
	}

}
