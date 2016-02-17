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
package org.openlvc.disco.receivers;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.PduReceiver;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.PduFactory;
import org.openlvc.disco.pdu.UnsupportedPDU;
import org.openlvc.disco.pdu.record.PduHeader;

/**
 * Incoming packets are placed on a queue for later processing. The deserialization of these
 * packets is handled by one executor (with two threads working on it), while the delivery to the
 * client application is handled by a separate executor with a single thread.
 * 
 * Queue sizes are limited to 100,000 packets to cap memory usage.
 */
public class ThreadPoolReceiver extends PduReceiver implements RejectedExecutionHandler
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean open;
	
	// Processing
	private BlockingQueue<Runnable> ingestqueue;  // ingest converts byte[] -> PDU and filters
	private ThreadPoolExecutor ingestExecutor;

	private BlockingQueue<Runnable> inqueue;      // passes to IPduReceiver for processing
	private ThreadPoolExecutor inqueueExecutor;
	
	// Metrics
	private AtomicLong metricsTotalPdusReceived;
	private AtomicLong metricsTotalPdusReceivedSize;
	private AtomicLong metricsTotalPdusDropped;
//	private AtomicLong metricsTotalPdusFiltered;
//	private AtomicLong metricsTotalPdusFilteredSize;
	private AtomicLong metricsTotalPdusDelivered;
	private AtomicLong metricsTotalPdusDeliveredSize;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ThreadPoolReceiver( OpsCenter opscenter, IConnection connection, IPduListener listener )
	{
		super( opscenter, connection, listener );
		this.open = false;

		// Processing
		this.ingestqueue = new LinkedBlockingQueue<Runnable>(100000);
		this.ingestExecutor = new ThreadPoolExecutor( 2, 2, 60, TimeUnit.SECONDS, ingestqueue );
		this.ingestExecutor.setRejectedExecutionHandler( this );
		
		this.inqueue = new LinkedBlockingQueue<Runnable>(100000);
		this.inqueueExecutor = new ThreadPoolExecutor( 1, 1, 60, TimeUnit.SECONDS, inqueue );
		this.inqueueExecutor.setRejectedExecutionHandler( this );
		
		// Metrics
		this.metricsTotalPdusReceived      = new AtomicLong( 0 );
		this.metricsTotalPdusReceivedSize  = new AtomicLong( 0 );
		this.metricsTotalPdusDropped       = new AtomicLong( 0 );
//		this.metricsTotalPdusFiltered      = new AtomicLong( 0 );
//		this.metricsTotalPdusFilteredSize  = new AtomicLong( 0 );
		this.metricsTotalPdusDelivered     = new AtomicLong( 0 );
		this.metricsTotalPdusDeliveredSize = new AtomicLong( 0 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void open() throws DiscoException
	{
		// Open the connection - our executors should already be ready
		this.connection.open();
	}
	
	public void close() throws DiscoException
	{
		if( !open )
			return;

		this.connection.close();
		this.ingestExecutor.shutdown();
		this.inqueueExecutor.shutdown();
		
		while( this.ingestExecutor.isShutdown() == false ||
		       this.inqueueExecutor.isShutdown() == false )
		{
			try { Thread.sleep( 1000 ); }catch( InterruptedException ie ) { return; }
		}
	}

	/**
	 * The following has been received from the {@link IConnection} for processing.
	 */
	public void receive( byte[] array )
	{
		this.metricsTotalPdusReceived.incrementAndGet();
		this.metricsTotalPdusReceivedSize.addAndGet( array.length );
		
		// create the task and submit to the executor
		IngestTask ingestTask = new IngestTask( array );
		ingestExecutor.execute( ingestTask );
	}

	/**
	 * Catch when one of the ingest or delivery queue drops a packet because it is full.
	 */
	public void rejectedExecution( Runnable task, ThreadPoolExecutor executor )
	{
		if( executor == this.ingestExecutor )
			metricsTotalPdusDropped.incrementAndGet();
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Incoming Message Processing   /////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	private class IngestTask implements Runnable
	{
		public byte[] buffer;
		public IngestTask( byte[] buffer ) { this.buffer = buffer; }

		
		public void run()
		{
			// wrap the buffer in a stream we can read from
			DisInputStream instream = new DisInputStream( buffer );
			
			// 1. Read off the header first
			PduHeader header = new PduHeader();
			try
			{
				header.from( instream );
			}
			catch( IOException ioex )
			{
				logger.error( "Error reading PDU Header, discarding packet", ioex );
				return;
			}
			
			// 2. First Filter
			// Do we want to filter this based on any header values?
			// TODO Implement
			
			// 3. Turn the bytes into a PDU
			PDU pdu = null;
			try
			{
				pdu = PduFactory.create( header );
				pdu.from( instream );
			}
			catch( UnsupportedPDU up )
			{
				if( logger.isTraceEnabled() )
					logger.trace( "Received unsupported PDU Type: "+up.getMessage() );
				return;
			}
			catch( IOException ioex )
			{
				logger.error( "Error reading PDU Body, discarding packet", ioex );
				return;
			}
			
			// 4. Full Filters
			// TODO Not implemented yet

			// 5. Hand off to receiver
			inqueueExecutor.execute( new HandoffTask(pdu) );
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Outgoing Message Processing   /////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	private class HandoffTask implements Runnable
	{
		public PDU pdu;
		public HandoffTask( PDU pdu ) { this.pdu = pdu; }
		public void run()
		{
			if( clientListener != null )
				clientListener.receiver( pdu );

			metricsTotalPdusDelivered.incrementAndGet();
			metricsTotalPdusDeliveredSize.addAndGet( pdu.getContentLength() );
		}
	}

}

