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
 * Indicates the type of Parameter used in an Articulation Parameter Record. This Variant is
 * represented as either an Attached part or Articulated part depending upon the Parameter Type
 * Designator.
 */
public class ParameterType implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long attachedParts;
	private ParameterTypeArticulatedParts articulatedParts;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ParameterType()
	{
		this( 0, new ParameterTypeArticulatedParts() );
	}
	
	public ParameterType( long attachedParts, ParameterTypeArticulatedParts articulatedParts )
	{
		this.attachedParts = attachedParts;
		this.articulatedParts = articulatedParts;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof ParameterType )
		{
			ParameterType otherParameter = (ParameterType)other;
			if( otherParameter.attachedParts == this.attachedParts &&
				otherParameter.articulatedParts.equals(this.articulatedParts) )
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public ParameterType clone()
	{
		ParameterTypeArticulatedParts clonedArticulatedParts = articulatedParts.clone();
		return new ParameterType( attachedParts, clonedArticulatedParts );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		attachedParts = dis.readUI32();
		articulatedParts.from( dis );
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI32( attachedParts );
		articulatedParts.to( dos );
    }
	
	@Override
    public int getByteLength()
	{
		int size = DisSizes.UI32_SIZE;
		size += articulatedParts.getByteLength();
		
		return size;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getAttachedParts()
    {
    	return attachedParts;
    }

	public void setAttachedParts( long attachedParts )
    {
    	this.attachedParts = attachedParts;
    }

	public ParameterTypeArticulatedParts getArticulatedParts()
    {
    	return articulatedParts;
    }

	public void setArticulatedParts( ParameterTypeArticulatedParts articulatedParts )
    {
    	this.articulatedParts = articulatedParts;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
