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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * This class is responsible for writing types specified in the DIS 
 * specification to the provided OutputStream 
 */
public class DisOutputStream extends DataOutputStream
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
	public DisOutputStream( OutputStream ostream )
	{
		super( ostream );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	
	/**
	 * Writes the specified 8-bit unsigned integer value to the stream. 
	 * 
	 * @param value The value to write to the stream. The value must be within the range 0 to 255 
	 * inclusive
	 * 
	 * @throws IOException thrown if there was a problem writing the value to the stream
	 * @throws IllegalArgumentException If the value was outside the range of a 8-bit unsigned 
	 * integer
	 */
	public void writeUI8( short value ) throws IOException
	{
		if( value < 0 || value > DisSizes.UI8_MAX_VALUE )
		{
			String message = "Out of range ("+value+"): Expecting number between 0 and "+DisSizes.UI8_MAX_VALUE;
			throw new IllegalArgumentException( message );
		}
		
		writeByte( value );
	}
	
	/**
	 * Writes the specified 16-bit unsigned integer value to the stream. 
	 * 
	 * @param value The value to write to the stream. The value must be within the range 0 to 65,535 
	 * inclusive
	 * 
	 * @throws IOException thrown if there was a problem writing the value to the stream
	 * @throws IllegalArgumentException If the value was outside the range of a 16-bit unsigned 
	 * integer
	 */
	public void writeUI16( int value ) throws IOException
	{
		if( value < 0 || value > DisSizes.UI16_MAX_VALUE )
		{
			String message = "Out of range ("+value+"): Expecting number between 0 and "+DisSizes.UI16_MAX_VALUE;
			throw new IllegalArgumentException( message );
		}

		writeShort( value );		
	}
	
	/**
	 * Writes the specified 32-bit unsigned integer value to the stream. 
	 * 
	 * @param value The value to write to the stream. The value must be within the range 0 to 
	 * 4,294,967,295 inclusive
	 * 
	 * @throws IOException thrown if there was a problem writing the value to the stream
	 * @throws IllegalArgumentException If the value was outside the range of a 32-bit unsigned 
	 * integer
	 */
	public void writeUI32( long value ) throws IOException
	{
		if( value < 0 || value > DisSizes.UI32_MAX_VALUE )
		{
			String msg = "Out of range ("+value+"): Expecting number between 0 and "+DisSizes.UI32_MAX_VALUE;
			throw new IllegalArgumentException( msg );
		}
		
		write( (int)((value >>> 24) & 0xFF) );
		write( (int)((value >>> 16) & 0xFF) );
		write( (int)((value >>> 8) & 0xFF) );
		write( (int)((value >>> 0) & 0xFF) );
	}
	
	public void writeUI64( BigInteger value ) throws IOException
	{
		if( value.compareTo( BigInteger.ZERO ) < 0 ||
			value.compareTo( DisSizes.UI64_MAX_VALUE ) > 0 )
		{
			String msg = "Out of range ("+value.toString()+"): Expecting number between 0 and "+DisSizes.UI64_MAX_VALUE.toString();
			throw new IllegalArgumentException( msg );
		}
		
		// Write any zero padding required
		byte[] valueAsBytes = value.toByteArray();
		int paddingRequired = 8 - valueAsBytes.length;
		for( int i = 0 ; i < paddingRequired ; ++i )
			write( 0 );
		
		// Write value
		write( valueAsBytes );
	}

	/**
	 * Writes the bits of the given interger to the stream. Unlike {@link #writeUI64(BigInteger)},
	 * there is no range check. We just write 64-bits to the stream.
	 * 
	 * @param value The value to write
	 */
	public void writeBits64( BigInteger value ) throws IOException
	{
		// Write any zero padding required
		byte[] valueAsBytes = value.toByteArray();
		int paddingRequired = Math.max( 0, (8-valueAsBytes.length) ); // only write 8 bytes
		for( int i = 0 ; i < paddingRequired ; ++i )
			write( 0 );
		
		// Write value
		write( valueAsBytes );
	}
	
	/**
	 * Writes <code>count</code> bytes of padding zeros to the stream.
	 * 
	 * @param count The amount of padding bytes to write
	 * 
	 * @throws IOException thrown if there was a problem writing the padding bytes to the stream
	 */
	public void writePadding( int count ) throws IOException
	{
		super.write( new byte[count] );
	}
	
	/**
	 * Writes 16 bits of padding zeros to the stream.
	 * 
	 * @throws IOException thrown if there was a problem writing the padding bytes to the stream
	 */
	public void writePadding16() throws IOException
	{
		writePadding( 2 );
	}
	
	/**
	 * Writes 24 bits of padding zeros to the stream.
	 * 
	 * @throws IOException thrown if there was a problem writing the padding bytes to the stream
	 */
	public void writePadding24() throws IOException
	{
		writePadding( 3 );
	}
	
	/**
	 * Writes 32 bits of padding zeros to the stream.
	 * 
	 * @throws IOException thrown if there was a problem writing the padding bytes to the stream
	 */
	public void writePadding32() throws IOException
	{
		writePadding( 4 );
	}

	/**
	 * Write the given string to the stream in an array of the given number of characters.
	 * Shorter values will be tailed with '0's up to the size. Longer strings will be trimmed.
	 * Will write the values as ASCII encoded.
	 * 
	 * @param string The string to write
	 * @param size The number of bytes to write. If the string is shorter, we still get this many
	 *             bytes. If the string is longer, it gets trimmed.
	 */
	public void writeFixedString( String string, int size ) throws IOException
	{	
		byte[] bytes = new byte[size+1];
		bytes[0] = 1; // DIS Indicator of character set. Just defaulting to ASCII
		
		// trim the string down to size
		if( string.length() > size )
			string = string.substring( 0, size );

		// convert the string to ascii bytes and copy into array
		byte[] stringBytes = string.getBytes( StandardCharsets.US_ASCII );
		int max = Math.min( stringBytes.length, size );
		System.arraycopy( stringBytes, 0, bytes, 1, max );

		super.write( bytes );
	}

	/**
	 * Write the given string to the stream, up to a max of the given length. 
	 * The length CANNOT be longer than 255 (length is encoded as a byte) or an exception
	 * will be thrown.
	 * 
	 * @param string The string to write
	 * @param max The max length to write (no larger than 255)
	 * @throws IOException If the max length is too long or there is a problem during write
	 */
	public void writeVariableString( String string, int max ) throws IOException
	{
		if( max > 255 )
			throw new IOException( "Max length can not be longer than 255. Found "+max );
		
		// determine the max string length (smaller of length or max)
		int cap = Math.min( string.length(), max );
		
		// write the length as a byte
		super.writeByte( cap );
		
		// write the content
		byte[] ascii = string.getBytes( StandardCharsets.US_ASCII );
		super.write( ascii, 0, cap );
	}
	
	/**
	 * WARNING: This is not a standard DIS representation of a string. It is something we
	 *          have created to write string values to into custom PDUs.
	 * <p/>
	 * This will write the string to the stream with a length of up to 255 characters.
	 * The first byte is used to write the size of the string. The remainder is used for the
	 * content. Any content over the size is discarded.
	 * 
	 * @param string The string to write.
	 * @throws IOException If there is a problem writing to the stream
	 */
	public void writeVariableStringMax256( String string ) throws IOException
	{
		// determine the length we need
		int cap = Math.min( 255, string.length() );
		assert cap <= 255;
		
		// write the length
		super.writeByte( cap );
		
		// write the content
		byte[] ascii = string.getBytes( StandardCharsets.US_ASCII );
		super.write( ascii, 0, cap );
	}
	
	
	/**
	 * Writes the given string to the stream. Total space taken will be the length of the string
	 * plus 2-bytes, which will be used to encode the length. Max size is 65,533 characters (the
	 * other two are taken up by the size).
	 * 
	 * @param string The string to write, truncated after 65K
	 * @throws IOException If there is a problem writing to the stream
	 */
	public void writeVariableStringMax65K( String string ) throws IOException
	{
		// determine the length we need
		int cap = Math.min( 65533, string.length() );
		assert cap <= 65533;
		
		// write the length
		super.writeShort( cap );
		
		// write the content
		byte[] ascii = string.getBytes( StandardCharsets.US_ASCII );
		super.write( ascii, 0, cap );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
