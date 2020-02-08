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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.PduSender;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.pdu.PDU;

/**
 * Very basic sender. Will serialize and send all messages on the calling thread. Calls to
 * {@link #send(PDU)} will block until the message has been pushed to the connection.
 */
public class SimpleSender extends PduSender
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SimpleSender( OpsCenter opscenter, IConnection connection )
	{
		super( opscenter, connection );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void send( PDU pdu )
	{
		try
		{
			this.connection.send( pdu.toByteArray() );
		}
		catch( DiscoException ioex )
		{
			logger.warn( "(PduSend) Problem serializing PDU for sending: "+ioex.getMessage(), ioex );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void open() throws DiscoException
	{
	}
	
	public void close() throws DiscoException
	{
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
