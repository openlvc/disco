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
import org.openlvc.disco.pdu.field.ParameterTypeDesignator;
import org.openlvc.disco.utils.DisUnsignedInt64;

/**
 * The specification of articulation parameters for movable parts and attached parts of an entity
 * shall be represented by an Articulation Parameter Record. This record shall specify whether or
 * not a change has occurred, the Part ID of the articulated part to which it is attached, and the
 * type and value of each parameter.
 */
public class ArticulationParameter implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ParameterTypeDesignator parameterTypeDesignator;
	private short parameterChangeIndicator;
	private int articulationAttachmentID;
	private ParameterType parameterTypeVariant;
	private DisUnsignedInt64 articulationParameterValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ArticulationParameter()
	{
		this( ParameterTypeDesignator.ArticulatedPart,
		      (short)0, 
		      0, 
		      new ParameterType(),
		      new DisUnsignedInt64() );
	}
	
	public ArticulationParameter( ParameterTypeDesignator parameterTypeDesignator,
	                              short parameterChangeIndicator,
	                              int articulationAttachmentID,
	                              ParameterType parameterTypeVariant,
	                              DisUnsignedInt64 articulationParameterValue )
	{
		this.parameterTypeDesignator = parameterTypeDesignator;
		this.parameterChangeIndicator = parameterChangeIndicator;
		this.articulationAttachmentID = articulationAttachmentID;
		this.parameterTypeVariant = parameterTypeVariant;
		this.articulationParameterValue = articulationParameterValue;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;

		if( other instanceof ArticulationParameter )
		{
			ArticulationParameter otherParam = (ArticulationParameter)other;
			if( otherParam.parameterTypeDesignator == this.parameterTypeDesignator && 
			    otherParam.parameterChangeIndicator == this.parameterChangeIndicator &&
			    otherParam.articulationAttachmentID == this.articulationAttachmentID &&
			    otherParam.parameterTypeVariant.equals(this.parameterTypeVariant) &&
			    otherParam.articulationParameterValue.equals(this.articulationParameterValue) )
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ArticulationParameter clone()
	{
		ParameterType parameterTypeClone = parameterTypeVariant.clone();
		DisUnsignedInt64 parameterValueClone = articulationParameterValue.clone();
		
		return new ArticulationParameter( parameterTypeDesignator, 
		                                  parameterChangeIndicator, 
		                                  articulationAttachmentID, 
		                                  parameterTypeClone, 
		                                  parameterValueClone );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		parameterTypeDesignator = ParameterTypeDesignator.fromValue( dis.readUI8() );
		parameterChangeIndicator = dis.readUI8();
		articulationAttachmentID = dis.readUI16();
		parameterTypeVariant.from( dis );
		articulationParameterValue.from( dis );
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI8( parameterTypeDesignator.value() );
		dos.writeUI8( parameterChangeIndicator );
		dos.writeUI16( articulationAttachmentID );
		parameterTypeVariant.to( dos );
		articulationParameterValue.to( dos );
    }
	
	@Override
    public int getByteLength()
	{
		int size = DisSizes.UI8_SIZE * 2;
		size += DisSizes.UI16_SIZE;
		size += parameterTypeVariant.getByteLength();
		size += articulationParameterValue.getByteLength();
		
		return size;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ParameterTypeDesignator getParameterTypeDesignator()
    {
    	return parameterTypeDesignator;
    }

	public void setParameterTypeDesignator( ParameterTypeDesignator parameterTypeDesignator )
    {
    	this.parameterTypeDesignator = parameterTypeDesignator;
    }

	public short getParameterChangeIndicator()
    {
    	return parameterChangeIndicator;
    }

	public void setParameterChangeIndicator( short parameterChangeIndicator )
    {
    	this.parameterChangeIndicator = parameterChangeIndicator;
    }

	public int getArticulationAttachmentID()
    {
    	return articulationAttachmentID;
    }

	public void setArticulationAttachmentID( int articulationAttachmentID )
    {
    	this.articulationAttachmentID = articulationAttachmentID;
    }

	public ParameterType getParameterTypeVariant()
    {
    	return parameterTypeVariant;
    }

	public void setParameterTypeVariant( ParameterType parameterTypeVariant )
    {
    	this.parameterTypeVariant = parameterTypeVariant;
    }

	public DisUnsignedInt64 getArticulationParameterValue()
    {
    	return articulationParameterValue;
    }

	public void setArticulationParameterValue( DisUnsignedInt64 articulationParameterValue )
    {
    	this.articulationParameterValue = articulationParameterValue;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
