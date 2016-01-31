/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.disco.utils;

/**
 * A group of static utility methods to help with various bit manipulation tasks.
 */
public class BitHelpers
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
	public static void main( String[] args ) throws Exception
    {
        int beforeInt = Integer.MAX_VALUE;
        long beforeLong = Long.MAX_VALUE;
        float beforeFloat = Float.MAX_VALUE;
        double beforeDouble = Double.MAX_VALUE;

        byte[] buffer = new byte[8];
        BitHelpers.putIntBE( beforeInt, buffer, 0 );
        int afterInt = BitHelpers.readIntBE( buffer, 0 );

        BitHelpers.putLongBE( beforeLong, buffer, 0 );
        long afterLong = BitHelpers.readLongBE( buffer, 0 );
        
        BitHelpers.putFloatBE( beforeFloat, buffer, 0 );
        float afterFloat = BitHelpers.readFloatBE( buffer, 0 );
        
        BitHelpers.putDoubleBE( beforeDouble, buffer, 0 );
        double afterDouble = BitHelpers.readDoubleBE( buffer, 0 );
        
        System.out.println( "   (int) before: "+beforeInt+", after: "+afterInt );
        System.out.println( "  (long) before: "+beforeLong+", after: "+afterLong );
        System.out.println( " (float) before: "+beforeFloat+", after: "+afterFloat );
        System.out.println( "(double) before: "+beforeDouble+", after: "+afterDouble );
        
        assert beforeInt == afterInt;
        assert beforeLong == afterLong;
        assert beforeFloat == afterFloat;
        assert beforeDouble == afterDouble;
    }

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Helper Methods //////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Check that the required number of bytes are present in the array, starting from
	 * the provided offset. If not, throw a {@link BufferOverflowException}.
	 * 
	 * @param required The number of bytes we expect to write
	 * @param buffer The buffer to check
	 * @param offset The offset to start from
	 */
	public static void checkOverflow( int required, byte[] buffer, int offset )
		throws BufferOverflowException
	{
		int length = buffer.length - offset;
		if( length < required )
		{
			throw new BufferOverflowException( "Buffer overflow. Tried to write "+required+
			                                   " bytes to buffer, only "+length+" bytes available" );
		}
	}

	/**
	 * Check that the required number of bytes are present in the array, starting from
	 * the provided offset. If not, throw a {@link BufferUnderflowException}.
	 * 
	 * @param required The number of bytes we expect to read
	 * @param buffer The buffer to check
	 * @param offset The offset to start from
	 */
	public static void checkUnderflow( int required, byte[] buffer, int offset )
	{
		int length = buffer.length - offset;
		if( length < required )
		{
			throw new BufferUnderflowException( "Buffer underflow. Tried to read "+required+
			                                    " bytes from buffer, found "+length );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Byte[] Methods //////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies the provided byte[] value into the buffer starting from the specified offset.
	 * Checks to ensure there is enough room to write into first, throwing a
	 * {@link BufferOverflowException} if there is not.
	 * 
	 * @param value The byte[] to write into the buffer
	 * @param buffer The buffer to write into
	 * @param offset The offset to commence at
	 * @throws BufferOverflowException If there is not enough space to write the value into
	 */
	public static void putByteArray( byte[] value, byte[] buffer, int offset )
		throws BufferOverflowException
	{
		checkOverflow( value.length, buffer, offset );
		System.arraycopy( value, 0, buffer, offset, value.length );
	}

	/**
	 * Reads and returns a sub-byte[] from the given buffer. Starting at the offset position
	 * and extending for <code>length</code> bytes. If there is not enough information in the
	 * buffer to satisfy the request, a {@link BufferUnderflowException} is thrown.
	 * 
	 * @param buffer The buffer to read from
	 * @param offset The position to start reading from
	 * @param length The number of bytes to read
	 * @return A new byte[] representing the sub-view of the buffer 
	 */
	public static byte[] readByteArray( byte[] buffer, int offset, int length )
	{
		checkUnderflow( length, buffer, offset );
		byte[] target = new byte[length];
		System.arraycopy( buffer, offset, target, 0, length );
		return target;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// Floating Point Methods //////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Convert the given float to a byte[]. Will be converted as Big Endian.
	 */
	public static byte[] floatToBytes( float value )
	{
		byte[] buffer = new byte[4];
		putFloatBE( value, buffer, 0 );
		return buffer;
	}

	/**
	 * Convert the given double to a byte[]. Will be converted as Big Endian.
	 */
	public static byte[] doubleToBytes( double value )
	{
		byte[] buffer = new byte[8];
		putDoubleBE( value, buffer, 0 );
		return buffer;
	}
	
	/**
	 * Write the given float value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putFloatBE( float value, byte[] buffer, int offset )
	{
		checkOverflow( 4, buffer, offset );

		int rawbits = Float.floatToIntBits( value );
		buffer[offset]   = (byte)(rawbits >>> 24);
		buffer[offset+1] = (byte)(rawbits >>> 16);
		buffer[offset+2] = (byte)(rawbits >>>  8);
		buffer[offset+3] = (byte)(rawbits >>>  0);
	}

	/**
	 * Write the given float value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putFloatLE( float value, byte[] buffer, int offset )
	{
		checkOverflow( 4, buffer, offset );

		int rawbits = Float.floatToIntBits( value );
		buffer[offset+3] = (byte)(rawbits >>> 24);
		buffer[offset+2] = (byte)(rawbits >>> 16);
		buffer[offset+1] = (byte)(rawbits >>>  8);
		buffer[offset]   = (byte)(rawbits >>>  0);
	}

	
	/**
	 * Read and return a float value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static float readFloatBE( byte[] buffer, int offset )
	{
		checkUnderflow( 4, buffer, offset );

		int temp = ((buffer[0] << 24) +
		           ((buffer[1] & 255) << 16) +
		           ((buffer[2] & 255) << 8) +
		           ((buffer[3] & 255) << 0));

		return Float.intBitsToFloat( temp );
	}

	/**
	 * Read and return a float value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static float readFloatLE( byte[] buffer, int offset )
	{
		checkUnderflow( 4, buffer, offset );

		int temp = ((buffer[3] << 24) +
		           ((buffer[2] & 255) << 16) +
		           ((buffer[1] & 255) << 8) +
		           ((buffer[0] & 255) << 0));

		return Float.intBitsToFloat( temp );
	}

	/**
	 * Write the given double value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putDoubleBE( double value, byte[] buffer, int offset )
	{
		checkOverflow( 8, buffer, offset );

		long rawbits = Double.doubleToLongBits( value );
		buffer[offset]   = (byte)(rawbits >>> 56);
		buffer[offset+1] = (byte)(rawbits >>> 48);
		buffer[offset+2] = (byte)(rawbits >>> 40);
		buffer[offset+3] = (byte)(rawbits >>> 32);
		buffer[offset+4] = (byte)(rawbits >>> 24);
		buffer[offset+5] = (byte)(rawbits >>> 16);
		buffer[offset+6] = (byte)(rawbits >>>  8);
		buffer[offset+7] = (byte)(rawbits >>>  0);
	}

	/**
	 * Write the given double value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putDoubleLE( double value, byte[] buffer, int offset )
	{
		checkOverflow( 8, buffer, offset );

		long rawbits = Double.doubleToLongBits( value );
		buffer[offset+7] = (byte)(rawbits >>> 56);
		buffer[offset+6] = (byte)(rawbits >>> 48);
		buffer[offset+5] = (byte)(rawbits >>> 40);
		buffer[offset+4] = (byte)(rawbits >>> 32);
		buffer[offset+3] = (byte)(rawbits >>> 24);
		buffer[offset+2] = (byte)(rawbits >>> 16);
		buffer[offset+1] = (byte)(rawbits >>>  8);
		buffer[offset]   = (byte)(rawbits >>>  0);
	}

	/**
	 * Read and return a double value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static double readDoubleBE( byte[] buffer, int offset )
	{
		checkUnderflow( 8, buffer, offset );

		long temp = (((long)buffer[0] << 56) +
		             ((long)(buffer[1] & 255) << 48) +
		             ((long)(buffer[2] & 255) << 40) +
		             ((long)(buffer[3] & 255) << 32) +
		             ((long)(buffer[4] & 255) << 24) +
		             ((buffer[5] & 255) << 16) +
		             ((buffer[6] & 255) <<  8) +
		             ((buffer[7] & 255) <<  0));

		return Double.longBitsToDouble( temp );
	}

	/**
	 * Read and return a double value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static double readDoubleLE( byte[] buffer, int offset )
	{
		checkUnderflow( 8, buffer, offset );

		long temp = (((long)buffer[7] << 56) +
		             ((long)(buffer[6] & 255) << 48) +
		             ((long)(buffer[5] & 255) << 40) +
		             ((long)(buffer[4] & 255) << 32) +
		             ((long)(buffer[3] & 255) << 24) +
		             ((buffer[2] & 255) << 16) +
		             ((buffer[1] & 255) <<  8) +
		             ((buffer[0] & 255) <<  0));

		return Double.longBitsToDouble( temp );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Short Methods ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Convert the given short to a byte[] as Big Endian.
	 */
	public static byte[] shortToBytes( short value )
	{
		byte[] buffer = new byte[2];
		putShortBE( value, buffer, 0 );
		return buffer;
	}

	/**
	 * Write the given int value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 2 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putShortBE( short value, byte[] buffer, int offset )
	{
		checkOverflow( 2, buffer, offset );

		buffer[offset]   = (byte)(value >> 8);
		buffer[offset+1] = (byte)(value /*>> 0*/);
	}

	/**
	 * Write the given int value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 2 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putShortLE( short value, byte[] buffer, int offset )
	{
		checkOverflow( 2, buffer, offset );

		buffer[offset+1] = (byte)(value >> 8);
		buffer[offset]   = (byte)(value /*>> 0*/);
	}

	/**
	 * Read and return an int value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 2 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static short readShortBE( byte[] buffer, int offset )
	{
		checkUnderflow( 2, buffer, offset );
		
		return (short)(((buffer[offset]   & 255) << 8) +
			           ((buffer[offset+1] & 255) /*<< 0*/));
	}

	/**
	 * Read and return an int value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 2 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static short readShortLE( byte[] buffer, int offset )
	{
		checkUnderflow( 2, buffer, offset );
		
		return (short)(((buffer[offset+1] & 255) << 8)  +
		               ((buffer[offset]   & 255) /*<< 0*/));
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// Int Methods ////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Convert the given int to a byte[] as Big Endian.
	 */
	public static byte[] intToBytes( int value )
	{
		byte[] buffer = new byte[4];
		putIntBE( value, buffer, 0 );
		return buffer;
	}

	/**
	 * Write the given int value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putIntBE( int value, byte[] buffer, int offset )
	{
		checkOverflow( 4, buffer, offset );

		buffer[offset]   = (byte)(value >> 24);
		buffer[offset+1] = (byte)(value >> 16);
		buffer[offset+2] = (byte)(value >> 8);
		buffer[offset+3] = (byte)(value /*>> 0*/);
	}

	/**
	 * Write the given int value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putIntLE( int value, byte[] buffer, int offset )
	{
		checkOverflow( 4, buffer, offset );

		buffer[offset+3] = (byte)(value >> 24);
		buffer[offset+2] = (byte)(value >> 16);
		buffer[offset+1] = (byte)(value >> 8);
		buffer[offset]   = (byte)(value /*>> 0*/);
	}

	/**
	 * Read and return an int value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static int readIntBE( byte[] buffer, int offset )
	{
		checkUnderflow( 4, buffer, offset );
		
		return ((buffer[offset]   & 255) << 24) +
		       ((buffer[offset+1] & 255) << 16) +
		       ((buffer[offset+2] & 255) << 8)  +
		       ((buffer[offset+3] & 255) /*<< 0*/);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Long Methods ////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Convert the given long to a byte[] as Big Endian.
	 */
	public static byte[] longToBytes( long value )
	{
		byte[] buffer = new byte[8];
		putLongBE( value, buffer, 0 );
		return buffer;
	}

	/**
	 * Read and return an int value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 4 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static int readIntLE( byte[] buffer, int offset )
	{
		checkUnderflow( 4, buffer, offset );
		
		return ((buffer[offset+3] & 255) << 24) +
		       ((buffer[offset+2] & 255) << 16) +
		       ((buffer[offset+1] & 255) << 8)  +
		       ((buffer[offset]   & 255) /*<< 0*/);
	}

	/**
	 * Write the given long value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putLongBE( long value, byte[] buffer, int offset )
	{
		checkOverflow( 8, buffer, offset );

		buffer[offset]   = (byte)(value >>> 56);
		buffer[offset+1] = (byte)(value >>> 48);
		buffer[offset+2] = (byte)(value >>> 40);
		buffer[offset+3] = (byte)(value >>> 32);
		buffer[offset+4] = (byte)(value >>> 24);
		buffer[offset+5] = (byte)(value >>> 16);
		buffer[offset+6] = (byte)(value >>> 8);
		buffer[offset+7] = (byte)(value >>> 0);
	}

	/**
	 * Write the given long value into the buffer, starting at the offset value.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferOverflowException} will be thrown for the buffer overflow.
	 */
	public static void putLongLE( long value, byte[] buffer, int offset )
	{
		checkOverflow( 8, buffer, offset );

		buffer[offset+7] = (byte)(value >>> 56);
		buffer[offset+6] = (byte)(value >>> 48);
		buffer[offset+5] = (byte)(value >>> 40);
		buffer[offset+4] = (byte)(value >>> 32);
		buffer[offset+3] = (byte)(value >>> 24);
		buffer[offset+2] = (byte)(value >>> 16);
		buffer[offset+1] = (byte)(value >>> 8);
		buffer[offset]   = (byte)(value >>> 0);
	}

	/**
	 * Read and return a long value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static long readLongBE( byte[] buffer, int offset )
	{
		checkUnderflow( 8, buffer, offset );

		return (((long)buffer[0] << 56) +
		        ((long)(buffer[1] & 255) << 48) +
		        ((long)(buffer[2] & 255) << 40) +
		        ((long)(buffer[3] & 255) << 32) +
		        ((long)(buffer[4] & 255) << 24) +
		        ((buffer[5] & 255) << 16) +
		        ((buffer[6] & 255) <<  8) +
		        ((buffer[7] & 255) <<  0));
	}

	/**
	 * Read and return a long value from the given buffer, starting at the given offset.
	 * <p/>
	 * If there are less than 8 bytes from the offset to the length of the array, a
	 * {@link BufferUnderflowException} will be thrown for the buffer underflow.
	 */
	public static long readLongLE( byte[] buffer, int offset )
	{
		checkUnderflow( 8, buffer, offset );

		return (((long)buffer[7] << 56) +
		        ((long)(buffer[6] & 255) << 48) +
		        ((long)(buffer[5] & 255) << 40) +
		        ((long)(buffer[4] & 255) << 32) +
		        ((long)(buffer[3] & 255) << 24) +
		        ((buffer[2] & 255) << 16) +
		        ((buffer[1] & 255) <<  8) +
		        ((buffer[0] & 255) <<  0));
	}
}
