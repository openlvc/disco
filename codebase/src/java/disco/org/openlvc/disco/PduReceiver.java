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
package org.openlvc.disco;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.receivers.SimpleReceiver;
import org.openlvc.disco.receivers.SingleThreadReceiver;
import org.openlvc.disco.receivers.ThreadPoolReceiver;

/**
 * Networking is hard. There are a bunch of approaches to doing something as simple as PDU
 * receiving and processing of a PDU. Do we just immediately deserialize and hand off for
 * processing? Do we queue them up for a separate thread to deserialize and hand-off?
 * If we do that, do we use more than one thread to notify clients? Do we separately handle
 * the deserialization and notification? Where do we do filtering? After we've deserialized
 * the PDU? After the header? etc... 
 * 
 * Networking across platforms in Java is also not as "Write Once, Run Anywhere" as it may
 * first appear. As such, despite a strong desire to avoid over abstraction, the receiving of
 * PDUs will be pushed behind an interface so that we can quickly and easily swap the underlying
 * implementation via configuration or code.
 */
public abstract class PduReceiver
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected Logger logger;
	protected IConnection connection;
	protected IPduListener clientListener;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduReceiver( OpsCenter opscenter, IConnection connection, IPduListener clientListener )
	{
		this.logger = opscenter.getLogger();
		this.connection = connection;
		this.clientListener = clientListener;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * A packet has been received from the network. Depending on the implmentation this method
	 * may do some work on it, throw it away, block until it is processed, queue for later, or
	 * any of these in some sort of combination (who knows!?).
	 */
	public abstract void receive( byte[] packet );

	/**
	 * You may proceed. Receiver should now accept and act on incoming PDUs.
	 */
	public abstract void open() throws DiscoException;
	
	/**
	 * Time to shut down. Stop processing packets. Flush any queues, do any cleanup work, etc...
	 */
	public abstract void close() throws DiscoException;

	//////////////////////////////////////////////////////////////////////////////////
	/// Monitoring Methods   /////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	public long getQueuedPacketCount()
	{
		return 0; // not implemented - can be overriden by child
	}
	
	public long getAvgProcessTimeNanos()
	{
		return 0; // not implemented - can be overriden by child
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	/**
	 * Creates a new receiver based on the name. Valid values are:
	 * 
	 *   - single-thread    -> SingleThreadReceiver
	 *   - thread-pool      -> ThreadPoolReceiver
	 *   - simple           -> SimpleReceiver
	 */
	public static PduReceiver create( String name,
	                                  OpsCenter opscenter,
	                                  IConnection connection,
	                                  IPduListener client )
		throws DiscoException
	{
		if( name.equalsIgnoreCase("single-thread") )
			return new SingleThreadReceiver( opscenter, connection, client );
		if( name.equalsIgnoreCase("thread-pool") )
			return new ThreadPoolReceiver( opscenter, connection, client );
		else if( name.equals("simple") )
			return new SimpleReceiver( opscenter, connection, client );
		else
			throw new DiscoException( "Unknown PDU Receiver: "+name );
	}

}
