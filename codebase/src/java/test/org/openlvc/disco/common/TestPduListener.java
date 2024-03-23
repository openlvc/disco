/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openlvc.disco.IPduListener;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;

public class TestPduListener implements IPduListener
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Map<Class<? extends PDU>,LinkedList<PDU>> receivedPdus;
	private Map<String,EntityStatePdu> espdus; 
	private Map<EntityId,TransmitterPdu> transmitterPdus;
	private Map<EntityId,SignalPdu> signalPdus;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public TestPduListener()
	{
		this.receivedPdus = new HashMap<>();
		this.espdus = new HashMap<String,EntityStatePdu>();
		this.transmitterPdus = new HashMap<>();
		this.signalPdus = new HashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	///////////////////////////////////////////////////////////////////////////////////
	/// PDU Reception Methods   ///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	@Override
	public void receive( PDU pdu )
	{
		// store the PDU in the general "all PDUs" array
		this.storePdu( pdu );

		// store special PDUs under special stores because they're so special to us
		switch( pdu.getType() )
		{
			case EntityState:
				EntityStatePdu espdu = (EntityStatePdu)pdu;
				espdus.put( espdu.getMarking(), espdu );
				break;

			case Transmitter:
				TransmitterPdu trpdu = pdu.as( TransmitterPdu.class );
				transmitterPdus.put( trpdu.getEntityId(), trpdu );
				break;

			case Signal:
				SignalPdu spdu = pdu.as( SignalPdu.class );
				signalPdus.put( spdu.getEntityIdentifier(), spdu );
				break;

			default:
				break;
		}

		synchronized( this ) { this.notifyAll(); }
	}

	private synchronized void storePdu( PDU pdu )
	{
		LinkedList<PDU> pdus = this.receivedPdus.get( pdu.getClass() );
		if( pdus == null )
		{
			pdus = new LinkedList<>();
			this.receivedPdus.put( pdu.getClass(), pdus );
		}
		
		pdus.add( pdu );
	}
	
	private synchronized PDU fetchPdu( Class<? extends PDU> type )
	{
		if( this.receivedPdus.containsKey(type) == false )
			return null;
		
		try
		{
			return this.receivedPdus.get(type).removeFirst();
		}
		catch( NoSuchElementException nse )
		{
			// list is empty
			return null;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Wait For Methods   ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	public EntityStatePdu waitForEntityState( String marking )
	{
		if( marking.length() > 11 )
			throw new RuntimeException( "Marking cannot be longer than 11 characters ["+marking+"]" );
		
		long finishTime = getWaitUntilTime();
		while( finishTime > System.currentTimeMillis() )
		{
			waitForPdu();

			if( espdus.containsKey(marking) )
				return espdus.get(marking);
		}
		
		// no update received in time
		throw new TimeoutException( "Timeout waiting for ESPDU with marking: "+marking );
	}
	
	public TransmitterPdu waitForTransmitter( EntityId id )
	{
		long finishTime = getWaitUntilTime();
		while( finishTime > System.currentTimeMillis() )
		{
			waitForPdu();

			if( transmitterPdus.containsKey(id) )
				return transmitterPdus.remove(id);
		}
		
		// no update received in time
		throw new TimeoutException( "Timeout waiting for Transmitter with ID: "+id );
	}
	
	public SignalPdu waitForSignal( EntityId id )
	{
		long finishTime = getWaitUntilTime();
		while( finishTime > System.currentTimeMillis() )
		{
			waitForPdu();

			if( signalPdus.containsKey(id) )
				return signalPdus.remove(id);
		}
		
		// no update received in time
		throw new TimeoutException( "Timeout waiting for Transmitter with ID: "+id );
	}

	/**
	 * Wait for this listener to receive a PDU of the given PDU class type, or for a timeout.
	 * 
	 * @param <X> 
	 * @param type The class of the PDU to wait for
	 * @return
	 */
	public <X extends PDU> X waitForPdu( Class<X> type )
	{
		long finishTime = getWaitUntilTime();
		while( finishTime > System.currentTimeMillis() )
		{
			PDU temp = fetchPdu( type );
			if( temp == null )
			{
        		waitForPdu();
        		temp = fetchPdu( type );
			}
			
			if( temp != null )
				return temp.as( type );
		}

		// no update received in time
		throw new TimeoutException( "Timeout waiting for PDU of type: "+type.getSimpleName() );
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   //////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	private long getWaitUntilTime()
	{
		return System.currentTimeMillis() + CommonSetup.TIMEOUT+10;
	}
	
	private void waitForPdu()
	{
		waitForPdu( CommonSetup.TIMEOUT );
	}
	
	//private void waitForPduUntil( long time )
	//{
	//	waitForPdu( time - System.currentTimeMillis() );
	//}
	
	private void waitForPdu( long timeout )
	{
		synchronized( this )
		{
			try
			{
				this.wait( timeout );
			}
			catch( InterruptedException ie )
			{
				throw new TimeoutException( "Interrupted waiting for pdu. Can't determine if test passed" );
			}
		}		
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
