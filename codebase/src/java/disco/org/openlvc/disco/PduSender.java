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
package org.openlvc.disco;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;

public class PduSender implements RejectedExecutionHandler
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger logger;
	private IConnection connection;
	
	private BlockingQueue<Runnable> conversionQueue;
	private ThreadPoolExecutor conversionExecutor;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduSender( OpsCenter opscenter, IConnection connection )
	{
		this.logger = opscenter.getLogger();
		this.connection = connection;
		
		this.conversionQueue = new LinkedBlockingQueue<>(100000);
		this.conversionExecutor = new ThreadPoolExecutor( 2, 2, 60, TimeUnit.SECONDS, conversionQueue );
		this.conversionExecutor.setRejectedExecutionHandler( this );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void send( PDU pdu )
	{
		conversionExecutor.execute( new ConversionTask(pdu) );
		//connection.send( pdu );

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
		this.conversionExecutor.shutdown();
		while( this.conversionExecutor.isShutdown() == false )
		{
			try { Thread.sleep( 1000 ); }catch( InterruptedException ie ) { return; }
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// ThreadPoolExecutor Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void rejectedExecution( Runnable task, ThreadPoolExecutor executor )
	{
		logger.warn( "(PDU Send) Conversion task was rejected for PDU: "+((ConversionTask)task).pdu );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	///
	private class ConversionTask implements Runnable
	{
		public PDU pdu;
		public ConversionTask( PDU pdu ) { this.pdu = pdu; }
		
		public void run()
		{
			try
			{
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
			catch( IOException ioex )
			{
				logger.warn( "Error trying to serialize PDU ("+pdu+"): "+ioex.getMessage(), ioex );
			}
		}
	}
}
