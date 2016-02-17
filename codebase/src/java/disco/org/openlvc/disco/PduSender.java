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
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.senders.SimpleSender;
import org.openlvc.disco.senders.SingleThreadSender;
import org.openlvc.disco.senders.ThreadPoolSender;

/**
 * Networking is hard. There are a bunch of approaches to doing something as simple as PDU
 * serialization and sending. Do we block for every send request? Do we queue them all up
 * and flush on a different thread? Do we serialize on one thread and send on another to keep
 * the sender focused on one this? So many choices!
 * 
 * Networking across platforms in Java is also not as "Write Once, Run Anywhere" as it may
 * first appear. As such, despite a strong desire to avoid over abstraction, the sending of
 * PDUs will be pushed behind an interface so that we can quickly and easily swap the underlying
 * implementation via configuration or code.
 */
public abstract class PduSender
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected Logger logger;
	protected IConnection connection;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduSender( OpsCenter opscenter, IConnection connection )
	{
		this.logger = opscenter.getLogger();
		this.connection = connection;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Send the given PDU to the network. Depending on the implementation this may block,
	 * queue the method for later sending or do something else entirely (who knows!?).
	 */
	public abstract void send( PDU pdu );

	/**
	 * You may proceed. Sender should now accept and act on requests to send PDUs
	 */
	public abstract void open() throws DiscoException;
	
	/**
	 * Time to shut down. Stop processing packets. Flush any queues, do any cleanup work, etc...
	 */
	public abstract void close() throws DiscoException;

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Creates a new sender based on the name. Valid values are:
	 * 
	 *   - single-thread    -> SingleThreadSender
	 *   - thread-pool      -> ThreadPoolSender
	 *   - simple           -> SimpleSender
	 */
	public static PduSender create( String name, OpsCenter opscenter, IConnection connection )
		throws DiscoException
	{
		if( name.equalsIgnoreCase("single-thread") )
			return new SingleThreadSender( opscenter, connection );
		if( name.equalsIgnoreCase("thread-pool") )
			return new ThreadPoolSender( opscenter, connection );
		else if( name.equals("simple") )
			return new SimpleSender( opscenter, connection );
		else
			throw new DiscoException( "Unknown PDU Sender: "+name );
	}
}
