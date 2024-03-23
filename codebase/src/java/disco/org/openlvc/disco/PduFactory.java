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
package org.openlvc.disco;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Set;

import org.openlvc.disco.configuration.DisConfiguration;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.UnparsedPdu;
import org.openlvc.disco.pdu.UnsupportedPDU;
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
	/**
	 * The default {@link PduFactory} that does not support <i>any</i> customer registered types.
	 * Sometimes you just need a simple factory that doesn't support extensions (like our test
	 * suite often does). Access via the singleton method {@link #getDefaultFactory()}.
	 */
	private static final PduFactory DEFAULT = new PduFactory();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumMap<PduType,Class<? extends PDU>> typeMap;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PduFactory()
	{
		// create an empty type map
		this.typeMap = new EnumMap<>( PduType.class );
		
		// populate it for all the default types which are referenced in the PduType enum
		for( PduType type : PduType.values() )
		{
			if( type.getImplementationClass() != null )
				typeMap.put( type, type.getImplementationClass() );
		}
	}

	public PduFactory( EnumMap<PduType,Class<? extends PDU>> typeMap )
	{
		this();
		if( typeMap != null )
			this.typeMap.putAll( typeMap );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
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
	public PDU create( PduHeader header ) throws UnsupportedPDU, DiscoException
	{
		// If the configuration it set to use Unparsed PDUs exclusively, just do that
		if( DiscoConfiguration.isSet(Flag.UnparsedExclusive) )
			return new UnparsedPdu().setHeader(header);

		// Get the implementation class for this PDU type
		PduType type = header.getPduType();
		//Class<? extends PDU> implementationClass = type.getImplementationClass();
		Class<? extends PDU> implementationClass = typeMap.get( type );
		
		// If we don't have an implementation class, the PDU is unsupported
		if( implementationClass == null )
		{
			if( DiscoConfiguration.isSet(Flag.Unparsed) )
				return new UnparsedPdu().setHeader(header);
			else
				throw new UnsupportedPDU( "PDU Type not supported: "+type.name() );
			
		}
		
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
	public PDU create( byte[] pdubytes ) throws IOException, UnsupportedPDU, DiscoException
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
	public PDU create( byte[] buffer, int offset, int length )
		throws IOException, UnsupportedPDU, DiscoException
	{
		// wrap the buffer in a stream we can read from
		DisInputStream instream = new DisInputStream( buffer, offset, length );
		
		// 1. Read off the header first
		PduHeader header = new PduHeader();
		header.from( instream );

		// 2. Read in the body
		PDU pdu = create( header );
		pdu.from( instream );
		
		return pdu;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/// Custom PDU Registration Methods      //////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Currently set to protected -- use the DIS Configuration to register custom PDU types.
	 * <p/>
	 * 
	 * Registers a custom PDU implementation with the factory. This method will attempt to:
	 * <ul>
	 *   <li>Create an instance of the class to ensure we can</li>
	 *   <li>Get the desired {@link PduType} from that test instance</li>
	 *   <li>Associate the given class with the {@link PduType} in our internal map so that
	 *       we create and return instances of the custom class with PDUs of that type arrive.</li>
	 * </ul>
	 * 
	 * This can only be done for types which don't already have a registration (overriding not yet
	 * supported).
	 * 
	 * @param pduClass The {@link PDU} subclass that provides the implementation for the PDU.
	 * @throws DiscoException If we can't instantiate the PDU class for any reason, or if there
	 *                        is already an implementation class registered for the type.
	 * @see DisConfiguration#registerCustomPdu(Class)
	 */
	protected final void registerCustomPdu( Class<? extends PDU> pduClass )
	{
		PduType pduType = null;
		
		// create an instance of the PDU to make sure that we can
		try
		{
			// create a test instance
			PDU test = pduClass.newInstance();
			
			// get the PDU type it wants from the test instance
			pduType = test.getType();
		}
		catch( IllegalAccessException | InstantiationException e )
		{
			throw new DiscoException( e, "Cannot register custom PDU (%s). Instantiation error: %s",
			                          pduClass.getSimpleName(),
			                          e.getMessage() );
		}

		// did we even get a type from the test pdu?
		if( pduType == null )
		{
			throw new DiscoException( "Cannot register custom PDU (%s): PDU.getType() was null",
			                          pduClass.getSimpleName() );
		}

		// make sure there isn't another registration in place already (unless it is of this
		// type and someone is just double registering)
		Class<? extends PDU> registered = typeMap.get( pduType );
		if( registered != null && registered != pduClass )
		{
			String s = "Cannot register custom PDU (%s): PduType (%s) already has registration: %s";
			throw new DiscoException( s,
			                          pduClass.getSimpleName(),
			                          pduType,
			                          typeMap.get(pduType).getSimpleName() );
		}
		
		// register the mapping
		this.typeMap.put( pduType, pduClass );
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	///  PDU Support Methods      /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the set of PDU Types that we currently support decoding.
	 */
	public Set<PduType> getSupportedPduTypes()
	{
		return typeMap.keySet();
	}

	/**
	 * Return a string listing the set of PDU types that we currently support decoding.
	 */
	public String getSupportedPduTypesString()
	{
		return getSupportedPduTypes().toString();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * @return The "default" PDU factory which can only ever support basic PDU types - not any
	 *         customer or extended types. Use of this factory is <b>not encouraged</b>, with
	 *         preference given to the {@link PduFactory} available via the OpsCenter. But if
	 *         you just need a simple, default factory - this will give it to you.
	 */
	public static final PduFactory getDefaultFactory()
	{
		return DEFAULT;
	}

}
