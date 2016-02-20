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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.PduReceiver;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.pdu.PDU;

public class SimpleReceiver extends PduReceiver
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
	public SimpleReceiver( OpsCenter opscenter, IConnection connection, IPduListener listener )
	{
		super( opscenter, connection, listener );
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
		try
		{
			clientListener.receiver( PDU.fromByteArray(array) );
		}
		catch( IOException ioex )
		{
			logger.warn( "(PduRecv) Problem deserializing PDU: "+ioex.getMessage(), ioex );
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