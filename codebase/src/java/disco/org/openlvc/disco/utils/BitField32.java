/*
 *   Copyright 2020 Open LVC Project.
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

public class BitField32
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int bits;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public BitField32( int bits )
	{
		this.bits = bits;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public boolean isSet( int position )
	{
		return (bits & (1 << position)) != 0;
	}
	
	public void setBit( int position, boolean value )
	{
		if( value )
			setBit(position);
		else
			clearBit(position);
	}

	public void setBit( int position )
	{
		bits = bits | (1 << position);
	}
	
	public void clearBit( int position )
	{
		bits = bits & ~(1 << position);
	}
	
	public int getInt()
	{
		return this.bits;
	}
	
	public void setInt( int value )
	{
		this.bits = value;
	}

	public void setBits( int start, int end, int value )
	{
		this.bits = setSubfield( this.bits, value, start, end );
	}
	
	public int getBits( int start, int end )
	{
		return getSubfield( this.bits, start, end );
	}

	public String toString()
	{
		return String.format("%32s", Integer.toBinaryString(bits)).replace( ' ', '.' ) ;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Return true if the given bits field (first param) has the bit at the given position (second
	 * param) set to true.
	 * 
	 * @param field     The int to look at the bits for
	 * @param position  The position to look at bits for
	 * @return          True if the bit is 1, false otherwise.
	 */
	public static boolean isSet( int field, int position )
	{
		return (field & (1 << position)) != 0; 
	}

	/**
	 * In the given field (first param), set the bit at the given position (second param) to 1 if
	 * the given boolean flag is true, or 0 if it is false. Return the resulting int.
	 * 
	 * @param field    The int field representing the bits
	 * @param position The position in the field to set
	 * @param on       The value to set the field to. 1 for true, 0 for false.
	 */
	public static int set( int field, int position, boolean on )
	{
		if( on )
			return field | (1 << position);
		else
			return field & ~(1 << position);
	}

	/**
	 * Get the value of a subset of the bitfield. The value will be an int, shifted so that 
	 * the bits from start (lowest index) to end (highest index) occupy the start of the
	 * returned field.
	 * <p/>
	 * For example (using a byte rather than an int), given "11111111" and a start/end of 2/3, you
	 * would get the value "00000011".
	 * 
	 * @param field The field we want to extract the subfield from
	 * @param start The start of the range to extract (index starting at 0)
	 * @param end   The end of the range to extract (index max 31)
	 * @return      The resulting value, with the extracted range shifted to the bottom.
	 */
	public static int getSubfield( int field, int start, int end )
	{
		int fieldSize = (end-start)+1;
		int mask = (1 << fieldSize)-1;
		int shifted = (field >> start);
		return mask & shifted;
	}

	/**
	 * In the given bitfield, set the bits in the range defined by start (third param) and end
	 * (fourth param) to the given value (second param).
	 * <p/>
	 * For example setSubfield(field, value, 3, 5) would set bits 3, 4 and 5 in the given field
	 * to the first three bits from the given value.
	 * 
	 * @param field The field to do the operation on
	 * @param value The value to get the incoming bits from
	 * @param start The first bit in the range to set
	 * @param end   The last bit in the range to set
	 * @return      The adjusted bit field
	 */
	public static int setSubfield( int field, int value, int start, int end )
	{
		int bitsLength = (end-start)+1;
		
		// 1. Trip down the incoming value so we only have the first x bits (where x is the length
		//    of the bits we want to set)
		int mask = 0;
		for( int i = 0; i < bitsLength; i++ )
			mask += (1 << i);
		
		int stripped = value & mask;
		
		// 2. Move the stripped bits into the place we want them, not all stacked in the low end
		stripped = stripped << start;

		// 3. Prepare the field by blanking out the bits we want to set (so that none are 1 and
		//    we can OR with the stripped, shifted field last
		mask = mask << start;
		
		// 4. Clear the space in the field
		field = field & ~mask; // flip the mask so that we can OR it safely next
		
		// 3. OR the stripped, moved value with the original field to 
		return field | stripped;
	}
}
