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
	 * Write the given string to the stream, limited to the given number of characters.
	 */
	public void writeString( String string, int limit ) throws IOException
	{	
		byte[] bytes = new byte[limit+1];
		bytes[0] = 1;
		int length = string.length();
		if( length > limit )
		{
			string = string.substring( 0, limit );
			for( int i = 0; i < limit; i++ )
				bytes[i+1] = (byte)string.charAt(i);
		}
		else
		{
			for( int i = 0; i < length; i++ )
				bytes[i+1] = (byte)string.charAt(i);
		}
		
		super.write( bytes );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
