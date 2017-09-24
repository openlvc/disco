/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.distributor.links.wan.udp.msg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Random;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;

/**
 * Type representing control messages that are sent across the UDP WAN link for the Distributor.
 * Each type of message is represented as a subclass of this one, with its own set of serialization
 * methods to/from byte[]'s.
 * 
 * The structure of a Distributor UDP message consists of a 10-byte header followed by the actual
 * payload of the message. It's base structure is laid out as follows:
 *
 * ```
 *      | -------------------------------------------------------- |  
 *      |           14-byte Header                   |             /
 *      | -------------------------------------------------------- |  
 *      |  4-bytes  |  4-bytes  |  4-bytes  | 2-byte |             /
 *      | -------------------------------------------------------- |  
 * Bit: | 01234567  | 01234567  | 01234567  |  0123  | ... ... ... /
 * Val: | MAGIC_NUM | MSG_TYPE  |  MSG_ID   |  SIZE  | Payload ... /
 * ```
 * 
 * 
 * ```
 *      | -------------------------------------------- |  
 *      |           12-byte Header       |             /
 *      | -------------------------------------------- |  
 *      |  4-bytes  |  4-bytes  | 2-byte |             /
 *      | -------------------------------------------- |  
 * Bit: | 01234567  | 01234567  |  0123  | ... ... ... /
 * Val: | MAGIC_NUM | MSG_TYPE  |  SIZE  | Payload ... /
 * ```
 */
public abstract class UdpMessage
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final int MAGIC_NUMBER = 0xc0fe;

	public static final int MAX_PACKET_SIZE = 1500 - 28; // MTU - UDP/IP Header
	                                                     // Our header included in payload
	
	public static final int HEADER_SIZE = 14; // MAGIC_NUMBER(int) + MSG_TYPE(int) + MSG_ID(int) + LENGTH(short)
	
	public static final int MAX_PAYLOAD_SIZE = MAX_PACKET_SIZE - HEADER_SIZE;

	public static final int MIN_PACKET_SIZE = HEADER_SIZE; 

	// Used to calculate random message ids
	private static final Random RANDOM_GENERATOR = new Random( System.currentTimeMillis() );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected int messageId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected UdpMessage()
	{
		this.messageId = 0;
	}
	
	protected UdpMessage( int mesageId )
	{
		this.messageId = mesageId;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	public abstract MessageType getType();

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Byte[] Conversion Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Populate this message from the given stream */
	protected abstract void loadFrom( DisInputStream stream ) throws DiscoException, IOException;

	/** Write the given message to the output stream */
	protected abstract void writeTo( DisOutputStream stream ) throws IOException;

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Datagram Conversion Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private byte[] toByteArray() throws IOException
	{
		try( ByteArrayOutputStream baos = new ByteArrayOutputStream(MAX_PACKET_SIZE);
		     DisOutputStream stream = new DisOutputStream(baos) )
		{
			// write the header
			stream.writeInt( MAGIC_NUMBER );
			stream.writeInt( getType().getValue() );
			stream.writeInt( messageId );

			// write the payload
			writeTo( stream );
			
			return baos.toByteArray();
		}
	}
	
	/** Convert the given message to a Datagram and return. Caller will need to fill in
	    important information such as the target. This only populates the data */
	public DatagramPacket toDatagram()
	{
		try
		{
    		byte[] contents = toByteArray();
    		return new DatagramPacket( contents, contents.length );
		}
		catch( IOException ioex )
		{
			throw new DiscoException( ioex.getMessage(), ioex );
		}
	}

	/** Set the data for the given packet to the contents of this message */
	public void toDatagram( DatagramPacket packet )
	{
		try
		{
			packet.setData( toByteArray() );
		}
		catch( IOException ioex )
		{
			throw new DiscoException( ioex.getMessage(), ioex );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getMessageId()
	{
		return this.messageId;
	}
	
	public void setMessageId( int messageId )
	{
		this.messageId = messageId;
	}
	
	public void setRandomMessageId()
	{
		this.messageId = RANDOM_GENERATOR.nextInt();
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Create a new {@link UdpMessage} from the given byte[] (starting at the specified offset).
	 * The returned type will be of the appropriate class depending on the {@link MessageType}
	 * contained inside the header of the package data.
	 * 
	 * @param buffer Contents of the buffer to read from
	 * @param offset The offset to start from within the buffer
	 * @return A new instance of a subclass of {@link UdpMessage}
	 * @throws IOException If there is a problem reading the message
	 * @throws DiscoException If there is some problem with the integrity of the data
	 */
	public static UdpMessage fromByteArray( byte[] buffer, int offset )
		throws IOException, DiscoException
	{
		try( DisInputStream stream = new DisInputStream(buffer,offset) )
		{		
    		// read the header
    		if( stream.readInt() != MAGIC_NUMBER )
    			throw new DiscoException( "Packet is not a UDP WAN control message (Missing 0xc0fe)" );
    		
    		// read the message type
    		MessageType messageType = MessageType.getType( stream.readInt() );
    		UdpMessage message = messageType.newInstance();
    		
    		// read the message id
    		message.messageId = stream.readInt();
    		
    		// read the body of the message
    		message.loadFrom( stream );
    		
    		return message;
		}
	}
	
}
