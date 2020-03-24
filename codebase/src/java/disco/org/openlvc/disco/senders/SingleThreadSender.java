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
package org.openlvc.disco.senders;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.PduSender;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.utils.ThreadUtils;

/**
 * Sender will queue messages for later sending and conversion on a different thread.
 * Sender contains a single thread to do that conversion and sending work.
 * Calls to {@link #send(PDU)} will block when the queue is full.
 */
public class SingleThreadSender extends PduSender
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BlockingQueue<PDU> sendQueue;
	private Thread sendThread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SingleThreadSender( OpsCenter opscenter, IConnection connection )
	{
		super( opscenter, connection );
		
		this.sendQueue = new LinkedBlockingQueue<>(100000);
		this.sendThread = new SendThread();
		this.sendThread.start();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void send( PDU pdu )
	{
		try
		{
			sendQueue.put( pdu );
		}
		catch( InterruptedException ie )
		{}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void open() throws DiscoException
	{
		// nothing to do here - the sender should already be ready
		this.sendThread.start();
	}
	
	public void close() throws DiscoException
	{
		// try to flush the queue
		logger.info( "Received shutdown notice -- flushing send queue (%d pdus)", sendQueue.size() );
		long startWait = System.currentTimeMillis();
		while( sendQueue.isEmpty() == false )
			ThreadUtils.exceptionlessSleep(500);

		logger.info( "Queue has been flushed. Took %d ms", System.currentTimeMillis()-startWait );

		try
		{
			this.sendThread.interrupt();
			this.sendThread.join();
		}
		catch( InterruptedException ie )
		{ /*ignore*/ }
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Class: SendThread   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class SendThread extends Thread
	{
		public SendThread()
		{
			super( "Disco-SendThread" );
		}
		
		public void run()
		{
			while( Thread.interrupted() == false )
			{
				PDU pdu = null;
				try
				{
					pdu = sendQueue.take();
					
					// Set the stream up
					ByteArrayOutputStream baos = new ByteArrayOutputStream( 2048 );
					DisOutputStream dos = new DisOutputStream( baos );

					// Write the PDU header to the stream
					pdu.writeHeader( dos );
					
					// Write the body content
					pdu.to( dos );
					
					// Send it off to the network
					connection.send( baos.toByteArray() );
				}
				catch( InterruptedException ie )
				{
					// time to shut down
					return;
				}
				catch( Exception ex )
				{
					// warn about the error and move on to the next PDU
					logger.warn( "Error trying to serialize PDU ("+pdu+"): "+ex.getMessage(), ex );
				}
			}
		}
	}
}
