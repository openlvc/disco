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

	public String toString()
	{
		return String.format("%32s", Integer.toBinaryString(bits)).replace( ' ', '.' ) ;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
