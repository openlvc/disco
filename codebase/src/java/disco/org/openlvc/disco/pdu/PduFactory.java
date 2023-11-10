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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.emissions.DesignatorPdu;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.radio.ReceiverPdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.PduHeader;
import org.openlvc.disco.pdu.simman.ActionRequestPdu;
import org.openlvc.disco.pdu.simman.ActionResponsePdu;
import org.openlvc.disco.pdu.simman.CommentPdu;
import org.openlvc.disco.pdu.simman.DataPdu;
import org.openlvc.disco.pdu.simman.SetDataPdu;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;

/**
 * Methods to help quickly create certain types of PDUs, or to create PDUs from an
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
	private Map<PduType,Supplier<? extends PDU>> typeSuppliers;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PduFactory()
	{
		this.typeSuppliers = getDefaultPduSuppliers();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void registerSupplier( PduType type, 
	                              Supplier<? extends PDU> supplier )
	{
		this.typeSuppliers.put( type, supplier );
	}

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
	public PDU create( PduHeader header ) throws UnsupportedPDU
	{
		// If the configuration it set to use Unparsed PDUs exclusively, just do that
		if( DiscoConfiguration.isSet(Flag.UnparsedExclusive) )
			return new UnparsedPdu().setHeader(header);

		// Get the implementation class for this PDU type
		PduType type = header.getPduType();
		Supplier<? extends PDU> supplier = typeSuppliers.get( type );
		
		// If we don't have an implementation class, the PDU is unsupported
		if( supplier == null )
		{
			if( DiscoConfiguration.isSet(Flag.Unparsed) )
				return new UnparsedPdu().setHeader(header);
			else
				throw new UnsupportedPDU( "PDU Type not supported: "+type.name() );
		}
		
		// Create a new PDU instance from the type and return it
		PDU pdu = supplier.get();
		pdu.setHeader( header );
		return pdu;
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
	 */
	public <T extends PDU> T create( byte[] pdubytes ) throws IOException,
	                                                          UnsupportedPDU
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
	 */
	@SuppressWarnings("unchecked")
	public <T extends PDU> T create( byte[] buffer, int offset, int length )
		throws IOException, UnsupportedPDU
	{
		// wrap the buffer in a stream we can read from
		DisInputStream instream = new DisInputStream( buffer, offset, length );
		
		// 1. Read off the header first
		PduHeader header = new PduHeader();
		header.from( instream );

		// 2. Read in the body
		PDU pdu = create( header );
		pdu.from( instream );
		
		return (T)pdu;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	///  PDU Support Methods      /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the set of PDU Types that we currently support decoding.
	 */
	public Set<PduType> getSupportedPduTypes()
	{
		Set<PduType> types = new HashSet<>();
		types.addAll( typeSuppliers.keySet() );
		return types;
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
	public static final Map<PduType,Supplier<? extends PDU>> getDefaultPduSuppliers()
	{
		Map<PduType,Supplier<? extends PDU>> suppliers = new HashMap<>();
		
		suppliers.put( PduType.EntityState,    () -> new EntityStatePdu() );
		suppliers.put( PduType.Fire,           () -> new FirePdu() );
		suppliers.put( PduType.Detonation,     () -> new DetonationPdu() );
		suppliers.put( PduType.ActionRequest,  () -> new ActionRequestPdu() );
		suppliers.put( PduType.ActionResponse, () -> new ActionResponsePdu() );
		suppliers.put( PduType.SetData,        () -> new SetDataPdu() );
		suppliers.put( PduType.Data,           () -> new DataPdu() );
		suppliers.put( PduType.Comment,        () -> new CommentPdu() );
		suppliers.put( PduType.Emission,       () -> new EmissionPdu() );
		suppliers.put( PduType.Designator,     () -> new DesignatorPdu() );
		suppliers.put( PduType.Transmitter,    () -> new TransmitterPdu() );
		suppliers.put( PduType.Signal,         () -> new SignalPdu() );
		suppliers.put( PduType.Receiver,       () -> new ReceiverPdu() );
		
		return suppliers;
	}
}
