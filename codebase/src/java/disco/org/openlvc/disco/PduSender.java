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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
	protected OpsCenter opscenter;
	protected IConnection connection;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduSender( OpsCenter opscenter )
	{
		this.logger = opscenter.getLogger();
		this.opscenter = opscenter;
		this.connection = opscenter.getConnection();
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
	 * <ul>
	 *   <li>single-thread creates {@link SingleThreadSender}
	 *   <li>thread-pool   creates {@link ThreadPoolSender}
	 *   <li>simple        creates {@link SimpleSender}
	 * </ul>
	 * 
	 * If the name is not any of these, we will treat it as a class name, trying to find the class
	 * and instantiate it. Should that also fail, we will throw an exception as the receiver type
	 * is not supported.
	 * <p/>
	 * Note: To be a valid sender that is defined by class name, it must have a constructor that
	 * takes a single parameter of type {@link OpsCenter}.
	 * 
	 * @param name  The name of the sender to create. Either one of the symbolic names, or a
	 *              fully qualified class name.
	 * @param opscenter The {@link OpsCenter} the sender will be deployed into
	 * @return A new instance of the {@link PduSender} specified by <code>name</code>
	 * @throws DiscoException If there is a problem either finding or creating the sender
	 */
	public static PduSender create( String name, OpsCenter opscenter ) throws DiscoException
	{
		// Check to see if the type is known, and if so, create and return
		switch( name )
		{
			case "simple"       : return new SimpleSender( opscenter );
			case "single-thread": return new SingleThreadSender( opscenter );
			case "thread-pool"  : return new ThreadPoolSender( opscenter );
			default: break;
		}
		
		// Type isn't known, check to see if it is an appropriate class name
		try
		{
			// find the class
			Class<?> clazz = Class.forName( name );
			// find the necessary constructor
			Constructor<?> constructor = clazz.getConstructor( OpsCenter.class );
			// instantiate the class
			Object instance = constructor.newInstance( opscenter );
			// check to make sure it is a PDU PduSender
			if( instance instanceof PduSender )
				return (PduSender)instance;
			else
				throw new DiscoException( "Class is not instance of PduSender: "+name );
		}
		catch( ClassNotFoundException e )
		{
			// yup, thoughts so, let's bounce
			throw new DiscoException( "Unknown PDU Sender: "+name );
		}
		catch( NoSuchMethodException nsme )
		{
			// constructor doesn't exist
			throw new DiscoException( "PDU Sender requires 1-arg constructor (OpsCenter): "+name, nsme );
		}
		catch( DiscoException de )
		{
			// not an instance of PduSender
			throw de;
		}
		catch( InvocationTargetException | IllegalAccessException | InstantiationException ie )
		{
			// could not create a new instance of the object
			throw new DiscoException( "Could not instantiate PDU Sender class: "+name );
		}
	}
}
