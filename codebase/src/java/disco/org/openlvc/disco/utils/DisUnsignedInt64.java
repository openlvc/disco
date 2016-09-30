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

import java.io.IOException;
import java.util.Arrays;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * The DISUnsignedInt64 represents an 64-bit unsigned integer, used throughout the DIS spec
 * 
 * TODO: Implement the various xxxValue() methods, and provide equals(), lessThan(), greaterThan()
 */
public class DisUnsignedInt64 extends Number implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
    private static final long serialVersionUID = -7912714492712731131L;
    private static final byte[] MAX_VALUE_BYTES = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
                                                    (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
    
    public static final DisUnsignedInt64 ZERO      = new DisUnsignedInt64();
    public static final DisUnsignedInt64 MAX_VALUE = new DisUnsignedInt64( MAX_VALUE_BYTES );
    
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte[] value;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DisUnsignedInt64()
	{
		this.value = new byte[DisSizes.UI64_SIZE];
	}
	
	public DisUnsignedInt64( byte[] value )
	{
		this();
		
		if ( value.length > DisSizes.UI64_SIZE )
			throw new IllegalArgumentException( "Value must not be more than 8 bytes long" );
		
		int offset = DisSizes.UI64_SIZE - value.length;
		for ( int i = 0 ; i < value.length ; ++i )
			this.value[offset + i] = value[i];
	}

	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if ( this == other )
			return true;
		
		if ( other instanceof DisUnsignedInt64 )
		{
			DisUnsignedInt64 asUI64 = (DisUnsignedInt64)other;
			return Arrays.equals( this.value, asUI64.value );
		}
		
		return false;
	}
	
	@Override
	public DisUnsignedInt64 clone()
	{
		// The constructor takes a copy for itself, so this is safe for cloning
		return new DisUnsignedInt64( value );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
	    dis.readFully( value );   
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.write( value );
    }
	
	@Override
	public final int getByteLength()
	{
		return DisSizes.UI64_SIZE;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int intValue()
	{
		return 0;
	}

	@Override
	public long longValue()
	{
		return 0;
	}

	@Override
	public float floatValue()
	{
		return 0;
	}

	@Override
	public double doubleValue()
	{
		return 0;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
