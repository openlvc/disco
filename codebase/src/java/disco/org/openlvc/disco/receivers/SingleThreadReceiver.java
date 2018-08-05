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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.PduReceiver;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.UnsupportedPDU;
import org.openlvc.disco.utils.ThreadUtils;

/**
 * Places all incoming packets on a queue and processes them in a single, separate thread.
 */
public class SingleThreadReceiver extends PduReceiver
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BlockingQueue<byte[]> receiveQueue;
	private ReceiverThread receiveThread;
	private long droppedPackets;

	// Monitoring
	private long totalProcessNanos;
	private long avgProcessNanos;
	private long packetsProcessed;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SingleThreadReceiver( OpsCenter opscenter, IConnection connection, IPduListener listener )
	{
		super( opscenter, connection, listener );
		
		this.receiveQueue = new LinkedBlockingQueue<>();
		this.receiveThread = null;   // set in open()
		this.droppedPackets = 0;     // reset in open()

		this.totalProcessNanos = 0;
		this.avgProcessNanos = 0;
		this.packetsProcessed = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Immediately deserialize the packet and hand over to the client application for processing.
	 * This will block until the processing is done (the Simple in SimpleReceiver stands more for
	 * Simple/Stupid than Simple/Easy).
	 */
	public void receive( byte[] array )
	{
		boolean result = receiveQueue.offer( array );
		if( !result )
			++droppedPackets;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void open() throws DiscoException
	{
		this.droppedPackets = 0;
		this.receiveThread = new ReceiverThread();
		this.receiveThread.start();
	}
	
	public void close() throws DiscoException
	{
		// try to flush the queue
		logger.info( "Received shutdown notice -- flushing recv queue (%d pdus)", receiveQueue.size() );
		long startWait = System.currentTimeMillis();
		while( receiveQueue.isEmpty() == false )
			ThreadUtils.exceptionlessSleep(500);

		logger.info( "Queue has been flushed. Took %d ms", System.currentTimeMillis()-startWait );

		try
		{
			this.receiveThread.interrupt();
			this.receiveThread.join();
		}
		catch( InterruptedException ie )
		{ /*ignore*/ }
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getDroppedPacketCount()
	{
		return droppedPackets;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Monitoring Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public long getQueuedPacketCount()
	{
		return receiveQueue.size();
	}

	@Override
	public long getAvgProcessTimeNanos()
	{
		return avgProcessNanos;
	}

	@Override
	public long getProcessedPacketCount()
	{
		return packetsProcessed;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Class: ReceiverThread   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class ReceiverThread extends Thread
	{
		public ReceiverThread()
		{
			super( "Disco-RecvThread" );
		}
		
		public void run()
		{
			while( Thread.interrupted() == false )
			{
				byte[] packet = null;
				try
				{
					packet = receiveQueue.take();
					
					long nanoStart = System.nanoTime();
					clientListener.receive( PDU.fromByteArray(packet) );
					long nanoTime = System.nanoTime() - nanoStart;

					// take our metrics
					++packetsProcessed;
					totalProcessNanos += nanoTime;
					avgProcessNanos = totalProcessNanos / packetsProcessed;
				}
				catch( IOException ioex )
				{
					// warn about the error and move on to the next PDU
					logger.warn( "(PduRecv) Problem deserializing PDU: "+ioex.getMessage(), ioex );
				}
				catch( InterruptedException ie )
				{
					// time to shut down
					return;
				}
				catch( UnsupportedPDU up )
				{
					// log and continue
					if( logger.isTraceEnabled() )
						logger.trace( "(PduRecv) Received unsupported PDU, skipping it: "+up.getMessage() );					
				}
				catch( DiscoException de )
				{
					// log and continue
					if( logger.isDebugEnabled() )
						logger.debug( "(PduRecv) Problem deserializing PDU, skipping it: "+de.getMessage(), de );
				}
				catch( Exception e )
				{
					logger.warn( "(PduRecv) Unknown exception while processing PDU, skipping it: "+e.getMessage(), e );
				}
			}
		}
	}
}
