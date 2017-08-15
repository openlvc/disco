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
import java.util.Arrays;
import java.util.List;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.PduHeader;

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
	/**
	 * Return the set of PDU Types that we currently support decoding.
	 */
	public static List<PduType> getSupportedPduTypes()
	{
		PduType[] array = { PduType.EntityState, PduType.Fire, PduType.Detonation,
		                    PduType.Transmitter, PduType.Signal, PduType.Receiver };
		
		return Arrays.asList( array );
	}

	/**
	 * Return a string listing the set of PDU types that we currently support decoding.
	 */
	public static String getSupportedPduTypesString()
	{
		return getSupportedPduTypes().toString();
	}
	
	public static PDU create( PduHeader header )
	{
		// Get the implementation class for this PDU type, thorwing an exception if we don't support it
		PduType pduType = header.getPduType();
		Class<? extends PDU> pduClass = pduType.getImplementationClass();
		if( pduClass == null )
			throw new UnsupportedPDU( "PDU Type not currently supported: "+pduType );
		
		// turn the class into an instance and return it
		try
		{
			PDU pdu = pduClass.newInstance();
			pdu.setHeader( header );
			return pdu;
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error creating PDU of type: "+pduType, e );
		}
		
		// Old hard-coded way of supporting PDU instantiation. Still open question on whether
		// this is much (if at all) faster than the .newInstance() approach above. Leaving here
		// to remind me to check
		/*
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
		}*/
	}

	/**
	 * Turn the given byte[] into a PDU and return it.
	 */
	public static PDU create( byte[] pdubytes ) throws IOException
	{
		return create( pdubytes, 0, pdubytes.length );
	}

	/**
	 * Turn the given byte[] into a PDU and return it. Starting from <code>offset</code>
	 * and extending <code>length</code> bytes.
	 */
	public static PDU create( byte[] buffer, int offset, int length ) throws IOException
	{
		// wrap the buffer in a stream we can read from
		DisInputStream instream = new DisInputStream( buffer, offset, length );
		
		// 1. Read off the header first
		PduHeader header = new PduHeader();
		header.from( instream );

		// 2. Read in the body
		PDU pdu = PduFactory.create( header );
		pdu.from( instream );
		
		return pdu;
	}

}
