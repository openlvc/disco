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
package org.openlvc.disco.pdu.record;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * This record represents one of the variants of the Parameter Type Variant, its values are used
 * only when the Parameter Type is Articulated Part (0) rather than Attached Part (1).
 */
public class ParameterTypeArticulatedParts implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int lowBits;
	private int highBits;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ParameterTypeArticulatedParts()
	{
		this( 0, 0 );
	}
	
	public ParameterTypeArticulatedParts( int lowBits, int highBits )
	{
		this.lowBits = lowBits;
		this.highBits = highBits;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof ParameterTypeArticulatedParts )
		{
			ParameterTypeArticulatedParts otherParts = (ParameterTypeArticulatedParts)other;
			if( otherParts.lowBits == this.lowBits && otherParts.highBits == this.highBits )
				return true;
		}

		return false;
	}

	@Override
	public ParameterTypeArticulatedParts clone()
	{
		return new ParameterTypeArticulatedParts( lowBits, highBits );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		lowBits = dis.readUI16();
		highBits = dis.readUI16();
    }
	
	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI16( lowBits );
		dos.writeUI16( highBits );
    }
	
	@Override
    public int getByteLength()
	{
		return DisSizes.UI16_SIZE * 2;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getLowBits()
    {
    	return lowBits;
    }

	public void setLowBits( int lowBits )
    {
    	this.lowBits = lowBits;
    }

	public int getHighBits()
    {
    	return highBits;
    }

	public void setHighBits( int highBits )
    {
    	this.highBits = highBits;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
