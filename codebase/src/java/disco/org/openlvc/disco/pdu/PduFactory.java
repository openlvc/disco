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

	///////////////////////////////////////////////////////////////////////////////////////
	/// PDU Creation - Header   ///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new, empty PDU from the given header. This will look in the header for the 
	 * {@link PduType} and attempt to create our implementation of that. When returned, the
	 * PDU will contain default values. The given header will also be placed into the PDU.
	 * 
	 * @param header The header to create a new empty PDU for.
	 * @return A new, empty/default values version of the PDU with the given header.
	 * @throws UnsupportedPDU If Disco does not currently support PDUs of this type.
	 * @throws DiscoException If there is an internal problem instantiating the PDU type.
	 */
	public static PDU create( PduHeader header ) throws UnsupportedPDU, DiscoException
	{
		// Get the implementation class for this PDU type
		PduType type = header.getPduType();
		Class<? extends PDU> implementationClass = type.getImplementationClass();
		
		// If we don't have an implementation class, the PDU is unsupported
		if( implementationClass == null )
			throw new UnsupportedPDU( "PDU Type not supported: "+type.name() );
		
		// Create a new PDU instance from the type and return it
		try
		{
			PDU pdu = implementationClass.newInstance();
			pdu.setHeader( header );
			return pdu;
		}
		catch( IllegalAccessException | InstantiationException e )
		{
			throw new DiscoException( "Error creating PDU ["+type.name()+"]: "+e.getMessage(), e );
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/// PDU Creation - Buffer   ///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Turn the complete given byte[] into a PDU and return it. Parses the header to determine
	 * the type of PDU to create, then populates the PDU properties from the rest of the buffer. 
	 * 
	 * @param pdubytes The byte buffer to create the PDU from. Will use buffer in entirety.
	 * @return A new {@link PDU} child that corresponds to the PDU type identified in the header
	 * @throws IOException     Problem reading from the byte[] (e.g. underflow)
	 * @throws UnsupportedPDU  The PDU type identified in the header isn't supported by Disco yet
	 * @throws DiscoException  There was a problem instanting a new instance of the desired PDU
	 */
	public static PDU create( byte[] pdubytes ) throws IOException, UnsupportedPDU, DiscoException
	{
		return create( pdubytes, 0, pdubytes.length );
	}

	/**
	 * Create a new PDU from the given buffer, starting from <code>offset</code> and extending
	 * <code>length</code> bytes through the buffer.
	 * <p/>
	 * 
	 * The header will be parsed to determine the type of PDU to create. A new instances will be
	 * created and populated with the remainder of the buffer contents.
	 *  
	 * @param buffer The buffer to read from
	 * @param offset The offset into the buffer to start reading from
	 * @param length The number of bytes to read from the offset
	 * @return       A new instance of the appropriate {@link PDU} subclass, populated from
	 *               the contents of the buffer
	 * @throws IOException     Problem parsing the buffer (e.g. underflow)
	 * @throws UnsupportedPDU  The PDU type identified in the header is not supported by Disco yet
	 * @throws DiscoException  Problem instantiating a new instances of the desired PDU
	 */
	public static PDU create( byte[] buffer, int offset, int length ) throws IOException,
	                                                                         UnsupportedPDU,
	                                                                         DiscoException
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
	
	///////////////////////////////////////////////////////////////////////////////////////
	///  PDU Support Methods      /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the set of PDU Types that we currently support decoding.
	 */
	public static List<PduType> getSupportedPduTypes()
	{
		return PduType.getSupportedPduTypes();
	}

	/**
	 * Return a string listing the set of PDU types that we currently support decoding.
	 */
	public static String getSupportedPduTypesString()
	{
		return getSupportedPduTypes().toString();
	}

}
