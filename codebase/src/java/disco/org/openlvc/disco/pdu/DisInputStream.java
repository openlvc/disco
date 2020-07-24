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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * This class is responsible for reading types specified in the DIS specification from the
 * provided InputStream
 */
public class DisInputStream extends DataInputStream
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
	/**
	 * Consturct a new DisInputStream around the given byte[] (fully).
	 */
	public DisInputStream( byte[] bytes )
	{
		super( new ByteArrayInputStream(bytes) );
	}

	/**
	 * Construct a new DisInputStream around the given byte[], starting at the offset
	 * and spanning the given length. This is useful for reusing a larger buffer rather
	 * than having to allocate a new byte[] to feed to the stream.
	 * 
	 * @param buffer  byte[] to read from 
	 * @param offset  index to start reading at
	 * @param length  length of the stream to cover
	 */
	public DisInputStream( byte[] buffer, int offset, int length )
	{
		super( new ByteArrayInputStream(buffer,offset,length) );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Reads the next byte from the stream as an 8-bit Unsigned Integer and returns the value as a
	 * short.
	 * 
	 * @return A short representing the 8-bit Unsigned Integer value
	 * @throws IOException thrown if the value could not be read from the stream
	 */
	public short readUI8() throws IOException
	{
		return (short)readUnsignedByte();
	}

	/**
	 * Reads the next two bytes from the stream as an 16-bit Unsigned Integer and returns the
	 * value as an int.
	 * 
	 * @return An int representing the 16-bit Unsigned Integer value
	 * @throws IOException thrown if the value could not be read from the stream
	 */
	public int readUI16() throws IOException
	{
		return readUnsignedShort();
	}

	/**
	 * Reads the next four bytes from the stream as a 32-bit Unsigned Integer and returns the
	 * value as a long.
	 * 
	 * @return A long representing the 32-bit Unsigned Integer value
	 * @throws IOException thrown if the value could not be read from the stream
	 */
	public long readUI32() throws IOException
	{
		// Get the next 4 bytes from the stream
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();

		// Check that we haven't gone beyond the stream bounds
		if( (ch1 | ch2 | ch3 | ch4) < 0 )
			throw new EOFException();

		// Assemble the value and return
		return ((long)(ch1 << 24 | ch2 << 16 | ch3 << 8 | ch4)) & 0xFFFFFFFFL;
	}

	/**
	 * Reads the next eight bytes from the stream as a 64-bit Unsigned Integer and returns the
	 * value as a {@link BigInteger}.
	 * 
	 * @return A {@link BigInteger} representing the 64-bit Unsigned Integer value
	 * @throws IOException thrown if the value could not be read from the stream
	 */
	public BigInteger readUI64() throws IOException
	{
		byte[] buffer = new byte[DisSizes.UI64_SIZE];
		int read = in.read( buffer );
		if( read < 0 )
			throw new EOFException();
		
		return new BigInteger( buffer );
	}
	
	/**
	 * Reads the specified number of bytes from the stream as a String
	 * 
	 * @param len the number of bytes to read from the stream
	 * @return A String representing the bytes read in String form
	 * @throws IOException thrown if the value could not be read from the stream
	 */
	public String readFixedString( int len ) throws IOException
	{
		// read in the character set -- ignore for now
		readUI8();

		// read the string
		byte[] buffer = new byte[len];
		readFully( buffer );

		return new String( buffer );
	}

	/**
	 * Read a string from the stream. The first byte is read as the size. The length of the
	 * string is then determined by that value. Max size is 254 characters (with one byte for
	 * the size).
	 * 
	 * @return The string that was read
	 * @throws IOException If there is a problem reading the full data from the stream
	 */
	public String readVariableString256() throws IOException
	{
		// read in the size
		int size = readUI8();
		
		// read the contents
		byte[] ascii = new byte[size];
		readFully( ascii );
		
		// return as a string
		return new String( ascii, StandardCharsets.US_ASCII );
	}
	
	/**
	 * Read a string from the stream. The first two bytes are read as the size. The length of the
	 * string is then determined by that value. Max size is 65,533 characters (with two bytes for
	 * the size).
	 * 
	 * @return The string that was read
	 * @throws IOException If there is a problem reading the full data from the stream
	 */
	public String readVariableString65K() throws IOException
	{
		// read in the size
		int size = readUI16();
		
		// read the contents
		byte[] ascii = new byte[size];
		readFully( ascii );
		
		// return as a string
		return new String( ascii, StandardCharsets.US_ASCII );
	}
	
	/**
	 * Skips the next specified number og bytes in the stream.
	 * 
	 * @throws IOException thrown if the stream does not contain enough bytes beyond the read
	 *                        marker to skip over
	 */
	private void checkedSkipBytes( int bytes ) throws IOException
	{
		int skipAmount = skipBytes( bytes );
		if( skipAmount < bytes )
			throw new EOFException();
	}

	/**
	 * Skips the next byte in the stream. This method is useful for skipping over 8 bit
	 * padding fields.
	 * 
	 * @throws IOException thrown if the stream does not contain enough bytes beyond the read
	 *                        marker to skip over
	 */
	public void skip8() throws IOException
	{
		checkedSkipBytes( 1 );
	}
	
	/**
	 * Skips the next two bytes in the stream. This method is useful for skipping over 16 bit
	 * padding fields.
	 * 
	 * @throws IOException thrown if the stream does not contain enough bytes beyond the read
	 *                        marker to skip over
	 */
	public void skip16() throws IOException
	{
		checkedSkipBytes( 2 );
	}

	/**
	 * Skips the next three bytes in the stream. This method is useful for skipping over 24 bit
	 * padding fields.
	 * 
	 * @throws IOException thrown if the stream does not contain enough bytes beyond the read
	 *                        marker to skip over
	 */
	public void skip24() throws IOException
	{
		checkedSkipBytes( 3 );
	}

	/**
	 * Skips the next four bytes in the stream. This method is useful for skipping over 32 bit
	 * padding fields.
	 * 
	 * @throws IOException thrown if the stream does not contain enough bytes beyond the read
	 *                        marker to skip over
	 */
	public void skip32() throws IOException
	{
		checkedSkipBytes( 4 );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
