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
package org.openlvc.disco.connection;

import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

/**
 * Represents a connection to an underlying source of incoming PDUs and a target for
 * outgoing PDUs. Typically this will be a network connection, but it would well be
 * some other form of data source.
 */
public interface IConnection
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public String getName();

	/**
	 * @return The PDUs that this connection supports
	 */
	public Collection<PduType> getSupportedPduTypes();	
	
	/**
	 * Configure the provider as it is being deployed into the given {@link OpsCenter}.
	 */
	public void configure( OpsCenter opscenter ) throws DiscoException;
	
	/**
	 * Open a connection to this provider and start it receiving.
	 * 
	 * This method cannot block.
	 */
	public void open() throws DiscoException;

	/**
	 * Close out the connection to this provider.
	 */
	public void close() throws DiscoException;

	/**
	 * Send the given DIS PDU to the network.
	 */
	public void send( PDU pdu ) throws DiscoException;

	/**
	 * Send the given PDU bytes to the network.
	 */
	public void send( byte[] pdubytes ) throws DiscoException;

	/**
	 * Return the {@link Metrics} gathered for this data source.
	 */
	public Metrics getMetrics();

}
