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
import org.openlvc.disco.utils.BitField32;

/**
 * The specification of articulation parameters for movable parts and attached parts of an entity
 * shall be represented by an Articulation Parameter Record. This record shall specify whether or
 * not a change has occurred, the Part ID of the articulated part to which it is attached, and the
 * type and value of each parameter.
 * <p/>
 * 
 * The ArticulationParameter record wraps up data for both Articulated and Attached parts, even
 * though they want something slightly different. There are a common set of attributes, and then
 * the spec treats the paramter type and value differently depending on which type is being
 * represented.
 * <p/>
 * 
 * To try and make this as easy to work with as possible, the {@link ArticulationParameter} class
 * provides overloaded methods for managing the various supported values of data required to
 * support either Articulated or Attached parts. Look to the method and parameter names for more
 * details on which is intended for which situation.
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
	private BitField32 parameterType;
	private long parameterValue; // bitfield

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ArticulationParameter()
	{
		this( ParameterTypeDesignator.ArticulatedPart );
	}
	
	public ArticulationParameter( ParameterTypeDesignator typeDesignator )
	{
		this.typeDesignator = typeDesignator;
		this.changeIndicator = 0;
		this.attachedTo = 0;
		this.parameterType = new BitField32(0);
		this.parameterValue = 0;
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
			    otherParam.parameterType.getInt() == this.parameterType.getInt() &&
			    otherParam.parameterValue == this.parameterValue )
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ArticulationParameter clone()
	{
		ArticulationParameter param = new ArticulationParameter( typeDesignator );
		param.changeIndicator = changeIndicator;
		param.attachedTo = attachedTo;
		param.parameterType.setInt( parameterType.getInt() );
		param.parameterValue = parameterValue;
		return param;
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
		parameterType.setInt( dis.readInt() );
		parameterValue = dis.readLong();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI8( typeDesignator.value() );
		dos.writeUI8( changeIndicator );
		dos.writeUI16( attachedTo );
		dos.writeInt( parameterType.getInt() );
		dos.writeLong( parameterValue );
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

	
	//////////////////////////////////////////////
	// Articulated Part Specific Methods   ///////
	//////////////////////////////////////////////
	/**
	 * Should only be used for ARTICULATED PARTS. Will mess with any values for attached parts. 
	 * @param value The value of the type metric. Only the lowest 5-bits used.
	 */
	public void setArticulatedPartTypeMetric( short value )
	{
		//int mask = 31; // 00000000 00000000 00000000 00011111
		this.parameterType.setBits( 1, 5, value );
	}

	public short getArticulatedPartTypeMetric()
	{
		return (short)this.parameterType.getBits( 1, 5 );
	}
	
	/**
	 * Should only be used for ARTICULATED PARTS. Will mess with any values for attached parts
	 * by copying the given value into the high-order 27-bits of the type field. 
	 * @param value The value for the type class. Only the lowest 27-bits will be used.
	 */
	public void setArticulatedPartTypeClass( int value )
	{
		//int mask = -32; // 11111111 11111111 11111111 11100000
		this.parameterType.setBits( 6, 32, value );
	}
	
	public int getArticulatedPartTypeClass()
	{
		return this.parameterType.getBits( 6, 32 );
	}

	/**
	 * Should only be used for ARTICULATED PARTS. Will put the given float value into the
	 * high-order 32-bits of the parameter value field as per DIS spec.
	 * @param value
	 */
	public void setArticulatedPartParameterValue( float value )
	{
		int bits = Float.floatToIntBits( value );
		this.parameterValue = ((long)bits) << 32;
	}
	
	public float getArticulatedPartParameterValue()
	{
		return Float.intBitsToFloat( (int)(this.parameterValue >> 32) );
	}

	
	//////////////////////////////////////////////
	// Attached Part Specific Methods      ///////
	//////////////////////////////////////////////
	/**
	 * Should only be used for ATTACHED PARTS. Will set the parameter type to the given station id.
	 * Will overwrite whatever exists for the station id.
	 * @param stationId The station id to use.
	 */
	public void setAttachedPartStationId( int stationId )
	{
		this.parameterType.setInt( stationId );
	}

	public int getAttachedPartStationId()
	{
		return parameterType.getInt();
	}

	public long getAttachedPartParameterValue()
	{
		return this.parameterValue;
	}
	
	public void setAttachedPartParameterValue( long value )
	{
		this.parameterValue = value;
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static ArticulationParameter newArticulatedPart( short typeMetric, int typeClass, float value )
	{
		ArticulationParameter parameter = new ArticulationParameter( ParameterTypeDesignator.ArticulatedPart );
		parameter.setArticulatedPartTypeMetric( typeMetric );
		parameter.setArticulatedPartTypeClass( typeClass );
		parameter.setArticulatedPartParameterValue( value );
		return parameter;
	}
	
	public static ArticulationParameter newAttachedPart( int stationId, long value )
	{
		ArticulationParameter parameter = new ArticulationParameter( ParameterTypeDesignator.AttachedPart );
		parameter.setAttachedPartStationId( stationId );
		parameter.setAttachedPartParameterValue( value );
		return parameter;
	}
	
}
