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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.PduSender;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.utils.ThreadUtils;

public class ThreadPoolSender extends PduSender implements RejectedExecutionHandler
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BlockingQueue<Runnable> sendQueue;
	private ThreadPoolExecutor sendExecutor;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ThreadPoolSender( OpsCenter opscenter )
	{
		super( opscenter );
		
		this.sendQueue = new LinkedBlockingQueue<>(100000);
		this.sendExecutor = new ThreadPoolExecutor( 2, 2, 60, TimeUnit.SECONDS, sendQueue );
		this.sendExecutor.setRejectedExecutionHandler( this );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void send( PDU pdu )
	{
		sendExecutor.execute( new SendTask(pdu) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void open() throws DiscoException
	{
		// nothing to do here - the sender should already be ready
	}
	
	public void close() throws DiscoException
	{
		// try to flush the queue
		logger.info( "Received shutdown notice -- flushing send queue (%d pdus)", sendQueue.size() );
		long startWait = System.currentTimeMillis();
		while( sendQueue.isEmpty() == false )
			ThreadUtils.exceptionlessSleep(500);

		logger.info( "Queue has been flushed. Took %d ms", System.currentTimeMillis()-startWait );

		// shut down the executors
		this.sendExecutor.shutdown();
		while( this.sendExecutor.isShutdown() == false )
		{
			try { Thread.sleep( 1000 ); }catch( InterruptedException ie ) { return; }
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// ThreadPoolExecutor Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void rejectedExecution( Runnable task, ThreadPoolExecutor executor )
	{
		// run ourselves
		task.run();
		//logger.warn( "(PDU Send) Conversion task was rejected for PDU: "+((ConversionTask)task).pdu );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Class: SendTask   //////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class SendTask implements Runnable
	{
		public PDU pdu;
		public SendTask( PDU pdu ) { this.pdu = pdu; }
		
		public void run()
		{
			try
			{
				connection.send( pdu.toByteArray() );
			}
			catch( DiscoException ioex )
			{
				logger.warn( "Error trying to serialize PDU ("+pdu+"): "+ioex.getMessage(), ioex );
			}
		}
	}
}
