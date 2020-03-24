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
import java.math.BigInteger;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.ParameterTypeDesignator;

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
	private ParameterTypeDesignator typeDesignator;
	private short changeIndicator;
	private int attachedTo;
	private long parameterType;
	private BigInteger parameterValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ArticulationParameter()
	{
		this( ParameterTypeDesignator.ArticulatedPart,
		      (short)0, 
		      0, 
		      0L,
		      BigInteger.ZERO );
	}
	
	public ArticulationParameter( ParameterTypeDesignator typeDesignator,
	                              short changeIndicator,
	                              int attachedTo,
	                              long parameterType,
	                              BigInteger parameterValue )
	{
		this.typeDesignator = typeDesignator;
		this.changeIndicator = changeIndicator;
		this.attachedTo = attachedTo;
		this.parameterType = parameterType;
		this.parameterValue = parameterValue;
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
			if( otherParam.typeDesignator == this.typeDesignator && 
			    otherParam.changeIndicator == this.changeIndicator &&
			    otherParam.attachedTo == this.attachedTo &&
			    otherParam.parameterType == this.parameterType &&
			    otherParam.parameterValue.equals(this.parameterValue) )
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ArticulationParameter clone()
	{
		return new ArticulationParameter( typeDesignator, 
		                                  changeIndicator, 
		                                  attachedTo, 
		                                  parameterType, 
		                                  parameterValue );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		typeDesignator = ParameterTypeDesignator.fromValue( dis.readUI8() );
		changeIndicator = dis.readUI8();
		attachedTo = dis.readUI16();
		parameterType = dis.readUI32();
		parameterValue = dis.readUI64();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI8( typeDesignator.value() );
		dos.writeUI8( changeIndicator );
		dos.writeUI16( attachedTo );
		dos.writeUI32( parameterType );
		dos.writeBits64( parameterValue );
    }
	
	@Override
    public int getByteLength()
	{
		int size = DisSizes.UI8_SIZE * 2;
		size += DisSizes.UI16_SIZE;
		size += DisSizes.UI32_SIZE;
		size += DisSizes.UI64_SIZE;
		
		return size;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ParameterTypeDesignator getTypeDesignator()
    {
    	return typeDesignator;
    }

	public void setTypeDesignator( ParameterTypeDesignator typeDesignator )
    {
    	this.typeDesignator = typeDesignator;
    }

	public short getChangeIndicator()
    {
    	return changeIndicator;
    }

	public void setChangeIndicator( short changeIndicator )
    {
    	this.changeIndicator = changeIndicator;
    }

	public int getAttachedTo()
    {
    	return attachedTo;
    }

	public void setAttachedTo( int attachedTo )
    {
    	this.attachedTo = attachedTo;
    }

	public long getParameterType()
    {
    	return parameterType;
    }

	public void setParameterType( long parameterType )
    {
    	this.parameterType = parameterType;
    }

	public BigInteger getParameterValue()
    {
    	return parameterValue;
    }

	public void setParameterValue( BigInteger parameterValue )
    {
    	this.parameterValue = parameterValue;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
