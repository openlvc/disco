/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.duplicator.readers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.openlvc.disco.PduFactory;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.ProtocolVersion;
import org.openlvc.duplicator.Track;
import org.openlvc.sombrero.PcapException;
import org.openlvc.sombrero.block.EnhancedPacketBlock;
import org.openlvc.sombrero.block.IPcapBlock;
import org.openlvc.sombrero.interpreter.PacketInterpreter;
import org.openlvc.sombrero.reader.IPcapReader;

/**
 * Wrapper for a PCAP stream that will parse and return PDU data.
 */
public class PcapDisStream
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InputStream inputStream;
	private IPcapReader pcapParser;
	private PduFactory pduFactory;
	private Instant firstPduTime;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Create a new stream around the given input stream containing PCAP data.
	 * 
	 * @param inputStream The stream we need to parse and pull DIS data from
	 * @throws PcapException If the stream is null
	 * @throws IOException If there is a problem opening the stream.
	 */
	public PcapDisStream( InputStream inputStream ) throws PcapException, IOException
	{
		if( inputStream == null )
			throw new PcapException( "Input stream is null - no contents to read" );
		
		this.pcapParser = IPcapReader.createFor( inputStream );
		this.pduFactory = PduFactory.getDefaultFactory();
		
		this.firstPduTime = null; // set when first record is pulled
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void close() throws IOException
	{
		this.firstPduTime = null;
		if( inputStream != null )
			inputStream.close();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return The next PDU in the stream, packaged up as a {@link Track}. If there is no more
	 *         data in the stream, null is returned.
	 */
	public Track readTrack()
	{
		try
		{
			// Get the next PDU
			PduPackage pduPackage = getNextPduInStream();
			
			// Calculate the offset from the start of the session until this PDU
			if( firstPduTime == null )
				firstPduTime = pduPackage.timestamp;
			
			long timeOffset = pduPackage.timestamp.toEpochMilli() - firstPduTime.toEpochMilli();
			
			// Return a new Duplicator Track
			return new Track( pduPackage.pdu, timeOffset );
		}
		catch( Exception e )
		{
			return null;
		}
	}

	private PduPackage getNextPduInStream() throws PcapException, IOException
	{
		// Somewhere to store data we have located
		PduPackage pduPackage = new PduPackage();

		// Look through each new block that the parser will give us
		do
		{
			// Get the next block of packet data
			IPcapBlock nextBlock = this.pcapParser.nextBlock();
			
			// no more data :(
			if( nextBlock == null )
				break;

			// make sure it's data that we potentially care about
			if( nextBlock instanceof EnhancedPacketBlock == false )
				continue;
			
			EnhancedPacketBlock packet = (EnhancedPacketBlock)nextBlock;
			
			// Construct a packet interpreter pull UDP deatils out, if this is a UDP packet
			PacketInterpreter interpreter = new PacketInterpreter();
			interpreter.onUdp( (udpFrame) -> {

				// get the raw data and see if we have a valid PDU Header
				PDU pdu = getPduFromBytesSafe( udpFrame.getData() );
				if( pdu != null )
				{
					// we found a valid looking PDU, store it so we break out of the loop
					pduPackage.pdu = pdu;
					pduPackage.timestamp = packet.getTimestamp();
					pduPackage.port = udpFrame.getDestPort();
				}
			});
			
			// Process the data through the interpreter
			interpreter.process( packet );
		}
		while( !pduPackage.hasPdu() );
		
		// Give back the PDU (if we found it). Will give back null if we ran out of data.
		return pduPackage;
	}

	/**
	 * If the given bytes contain a valid PDU, parse and return it.
	 * If the bytes do _not_ contain a PDU, return null.
	 * 
	 * We determine validity by first looking for a valid PDU Header with a valid DIS 
	 * version in it. The version value must be listed in {@link ProtocolVersion} _except_ for 
	 * "Other". Other is considered invalid for our purposes.
	 * 
	 * If the version is valid, and the rest of the block parses successfully, we return a PDU.
	 * Otherwise we return null.
	 * 
	 * @param bytes The block of data we suspect has a PDU
	 * @return A PDU, if there is a valid one inside the block, null otherwise
	 */
	private PDU getPduFromBytesSafe( byte[] bytes )
	{
		// In the Duplicator, back at startup, we put Disco into "unpased exclusive" mode.
		// This means that PDU bodies will remain unparsed (so that the Duplicator can still
		// replay PDUs it doesn't know anything about, which in a stricter mode would cause
		// a parsing failure). Only the header is parsed. See NetworkReplayer:115 (approx)
		//
		// The result of this is that we just blow through and try to parse a PDU. If the
		// data doesn't represent a PDU then one of two things will happen:
		//   1. (Strict Mode): An exception will be generated when parsing the header
		//   2. (Not Strict):  The header will parse, but the values are likely junk and
		//                     values like the ProtocolVersion will default to fallback value
		//                     like "Other"
		//
		// We can an exception and return null for the first situation. We check for a valid
		// protocol version for the second. There is a _small_ chance that a packet have magic
		// contents in the right location and gets through this check, but it's highly unlikely
		// and the PDU will be junk, so we'll have to rely on other systems ignoring it.
		try
		{
			PDU pdu = this.pduFactory.create( bytes );
			if( pdu.getHeader().getVersion() == ProtocolVersion.Other )
				return null;
			else
				return pdu;
		}
		catch( Exception e )
		{
			// nope :(
			return null;
		}
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	private class PduPackage
	{
		private Instant timestamp = null;
		private int port = -1; // we don't use this now, but we may want to later
		private PDU pdu = null;
		
		public boolean hasPdu()
		{
			return timestamp != null && pdu != null && port != -1;
		}
		
		@SuppressWarnings("unused")
		public void clear()
		{
			this.timestamp = null;
			this.port = -1;
			this.pdu = null;
		}
	}
	
}
