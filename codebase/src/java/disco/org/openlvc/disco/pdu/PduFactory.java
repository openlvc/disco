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
package org.openlvc.disco.pdu;

import java.io.IOException;

import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.radio.ReceiverPdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.PduHeader;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;

/**
 * Methods to help quickly create certain types of PDU's, or to create PDUs from an
 * incoming stream/source.
 */
public class PduFactory
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

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static PDU create( PduHeader header )
	{
		switch( header.getPduType() )
		{
			case EntityState:
				return new EntityStatePdu( header );
			case Fire:
				return new FirePdu( header );
			case Detonation:
				return new DetonationPdu( header );
			case Transmitter:
				return new TransmitterPdu( header );
			case Signal:
				return new SignalPdu( header );
			case Receiver:
				return new ReceiverPdu( header );
			default:
				throw new UnsupportedPDU( "PDU Type not currently supported: "+header.getPduType() );
		}
	}

	/**
	 * Turn the given byte[] into a PDU and return it.
	 */
	public static PDU create( byte[] pdubytes ) throws IOException
	{
		// wrap the buffer in a stream we can read from
		DisInputStream instream = new DisInputStream( pdubytes );
		
		// 1. Read off the header first
		PduHeader header = new PduHeader();
		header.from( instream );

		// 2. Read in the body
		PDU pdu = PduFactory.create( header );
		pdu.from( instream );
		
		return pdu;
	}

}
