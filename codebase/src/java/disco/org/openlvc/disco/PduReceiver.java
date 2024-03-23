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
	protected OpsCenter opscenter;
	protected PduFactory pduFactory;
	protected IPduListener clientListener;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduReceiver( OpsCenter opscenter )
	{
		this.logger = opscenter.getLogger();
		this.opscenter = opscenter;
		this.pduFactory = opscenter.getPduFactory();
		this.clientListener = opscenter.getPduListener();
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

	public long getProcessedPacketCount()
	{
		return 0; // not implemented - can be overriden by child
	}

	//////////////////////////////////////////////////////////////////////////////////
	/// Accessors and Mutators   /////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	/**
	 * Creates a new receiver based on the name. Valid values are:
	 * 
	 * <ul>
	 *   <li>single-thread creates {@link SingleThreadReceiver}
	 *   <li>thread-pool   creates {@link ThreadPoolReceiver}
	 *   <li>simple        creates {@link SimpleReceiver}
	 * </ul>
	 * 
	 * If the name is not any of these, we will treat it as a class name, trying to find the class
	 * and instantiate it. Should that also fail, we will throw an exception as the receiver type
	 * is not supported.
	 * <p/>
	 * Note: To be a valid receiver that is defined by class name, it must have a constructor that
	 * takes a single parameter of type {@link OpsCenter}.
	 * 
	 * @param name  The name of the receiver to create. Either one of the symbolic names, or a
	 *              fully qualified class name.
	 * @param opscenter The {@link OpsCenter} the receiver will be deployed into
	 * @return A new instance of the {@link PduReceiver} specified by <code>name</code>
	 * @throws DiscoException If there is a problem either finding or creating the receiver
	 */
	public static PduReceiver create( String name, OpsCenter opscenter ) throws DiscoException
	{
		// Check to see if the type is known, and if so, create and return a receiver
		switch( name )
		{
			case "simple"       : return new SimpleReceiver( opscenter );
			case "single-thread": return new SingleThreadReceiver( opscenter );
			case "thread-pool"  : return new ThreadPoolReceiver( opscenter );
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
			// check to make sure it is a PDU Receiver
			if( instance instanceof PduReceiver )
				return (PduReceiver)instance;
			else
				throw new DiscoException( "Class is not instance of PduReceiver: "+name );
		}
		catch( ClassNotFoundException e )
		{
			// yup, thoughts so, let's bounce
			throw new DiscoException( "Unknown PDU Receiver: "+name );
		}
		catch( NoSuchMethodException nsme )
		{
			// constructor doesn't exist
			throw new DiscoException( "PDU Receiver requires 1-arg constructor (OpsCenter): "+name, nsme );
		}
		catch( DiscoException de )
		{
			// not an instance of PduReceiver
			throw de;
		}
		catch( InvocationTargetException | IllegalAccessException | InstantiationException ie )
		{
			// could not create a new instance of the object
			throw new DiscoException( "Could not instantiate PDU Receiver class: "+name );
		}
	}

}
