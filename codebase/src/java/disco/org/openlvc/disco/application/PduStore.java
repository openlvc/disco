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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.radio.TransmitterPdu;

/**
 * The {@link PduStore} is the core PDU repository for a Disco {@link DisApplication}.
 * The store itself is an aggregator of a number of sub-stores that focus on methods for
 * particular PDU types. Sub-stores can be accessed through getters.
 */
public class PduStore
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityStateStore entityStore;
	private TransmitterStore transmitterStore;
	private EmitterStore     emitterStore;
	private Map<PduType,Consumer<PDU>> handlers;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduStore( DisApplication app )
	{
		this.entityStore = new EntityStateStore( this );
		this.transmitterStore = new TransmitterStore( this ); // depends on EntityStore
		this.emitterStore = new EmitterStore( this );         // depends on EntityStore
		
		app.getDeleteReaper().registerTarget( entityStore );
		app.getDeleteReaper().registerTarget( transmitterStore );
		app.getDeleteReaper().registerTarget( emitterStore );
		
		this.handlers = new HashMap<>();
		handlers.put( PduType.EntityState, (p) -> this.entityStore.receivePdu(p.as(EntityStatePdu.class)) );
		handlers.put( PduType.Transmitter, (p) -> this.transmitterStore.receivePdu(p.as(TransmitterPdu.class)) );
		handlers.put( PduType.Emission,    (p) -> this.emitterStore.receivePdu(p.as(EmissionPdu.class)) );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	protected void pduReceived( PDU pdu )
	{
		Consumer<PDU> handler = handlers.get( pdu.getType() );
		if( handler != null )
			handler.accept( pdu );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		this.entityStore.clear();
		this.transmitterStore.clear();
		this.emitterStore.clear();
	}

	public EntityStateStore getEntityStore()
	{
		return this.entityStore;
	}
	
	public TransmitterStore getTransmitterStore()
	{
		return this.transmitterStore;
	}
	
	public EmitterStore getEmitterStore()
	{
		return this.emitterStore;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
