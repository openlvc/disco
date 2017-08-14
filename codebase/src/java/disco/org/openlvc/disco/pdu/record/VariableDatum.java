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
package org.openlvc.disco.pdu.record;

import java.io.IOException;
import java.util.Arrays;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * Represents a DIS Variable Datum Record. The VD Record contains three fields.
 * A UINT32 Datum ID, a UINT32 representing the length of the stored value in _bits_,
 * and the value itself (which must be padded out to the nearest 64-bit boundary).
 */
public class VariableDatum implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int datumId;
	private byte[] datumValue;
	private byte[] datumPadding; // auto-calculated

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public VariableDatum()
	{
		this( 0, new byte[]{} );
	}

	public VariableDatum( int datumId, byte[] datumValue )
	{
		this.datumId = datumId;
		this.setDatumValue( datumValue );
		//this.datumValue = ...    -- set int setDatumValue()
		//this.datumLength = ...   -- set int setDatumValue()
		//this.datumPadding = ...  -- set int setDatumValue()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof VariableDatum )
		{
			VariableDatum asVariableDatum = (VariableDatum)other;
			if( datumId == asVariableDatum.datumId &&
				Arrays.equals(datumValue,asVariableDatum.datumValue) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public VariableDatum clone()
	{
		return new VariableDatum( datumId, datumValue );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		this.datumId = (int)dis.readUI32();
		
		int lengthInBits = (int)dis.readUI32();
		int lengthInBytes = lengthInBits / 8;
		byte[] value = new byte[lengthInBytes];
		dis.readFully( value );
		setDatumValue( value );
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI32( datumId );
		dos.writeUI32( getDatumLengthInBits() );

		// write the value and padding to round it out to 64-bit boundary
		dos.write( this.datumValue );
		dos.write( this.datumPadding );
    }
	
	@Override
    public final int getByteLength()
	{
		return 8 + datumValue.length + datumPadding.length;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getDatumId()
	{
		return datumId;
	}

	public void setDatumId( int datumId )
	{
		this.datumId = datumId;
	}

	public final int getDatumLengthInBits()
	{
		return datumValue.length * 8;
	}
	
	public final int getDatumLengthInBytes()
	{
		return datumValue.length;
	}
	
	public final int getPaddingLengthInBits()
	{
		return datumPadding.length * 8;
	}

	public final int getPaddingLengthInBytes()
	{
		return datumPadding.length;
	}
	
	public void setDatumValue( byte[] value )
	{
		this.datumValue = value;
		
		// figure out how much padding we need to push it out to 64-bit boundary
		int padding = 8 - value.length % 8;
		if( padding == 8 )
			padding = 0;
		
		this.datumPadding = new byte[padding];
	}

	public byte[] getDatumValue()
	{
		return this.datumValue;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/*
	public static void main( String[] args ) throws Exception
	{
		for( int i = 0; i <= 64; i++ )
		{
			VariableDatum temp = new VariableDatum( 1, new byte[i] );
			System.out.println( String.format( "[%d]: length=%d bytes (%d bits), padding=%d bytes (%d bits) ... %d",
			                                   i,
			                                   temp.getDatumLengthInBytes(),
			                                   temp.getDatumLengthInBits(),
			                                   temp.getPaddingLengthInBytes(),
			                                   temp.getPaddingLengthInBits(),
			                                   temp.getByteLength()) );
		}
	}
	*/
}
