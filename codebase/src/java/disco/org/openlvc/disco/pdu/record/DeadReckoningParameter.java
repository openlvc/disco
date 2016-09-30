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
import java.util.Arrays;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;

/**
 * Used to provide the parameters for dead reckoning the position and orientation of the entity.
 * Dead Reckoning Algorithm in use, Entity Acceleration and Angular velocity shall be included as
 * a part of the dead reckoning parameters. 120 bits are reserved for other parameters that are
 * currently undefined.
 */
public class DeadReckoningParameter implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int OTHER_PARAMETERS_ARRAY_SIZE = 15;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DeadReckoningAlgorithm deadReckoningAlgorithm;
	private byte[] deadReckoningOtherParameters;
	private VectorRecord entityLinearAcceleration;
	private AngularVelocityVector entityAngularVelocity;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DeadReckoningParameter()
	{
		this( DeadReckoningAlgorithm.Other, 
		      new byte[OTHER_PARAMETERS_ARRAY_SIZE], 
		      new VectorRecord(),
		      new AngularVelocityVector() );
	}
	
	public DeadReckoningParameter( DeadReckoningAlgorithm deadReckoningAlgorithm,
	                               byte[] deadReckoningOtherParameters,
	                               VectorRecord entityLinearAcceleration,
	                               AngularVelocityVector entityAngularVelocity )
	{
		this.deadReckoningAlgorithm = deadReckoningAlgorithm;
		this.entityLinearAcceleration = entityLinearAcceleration;
		this.entityAngularVelocity = entityAngularVelocity;
		
		setDeadReckoningOtherParameters( deadReckoningOtherParameters );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;

		if( other instanceof DeadReckoningParameter )
		{
			DeadReckoningParameter asParam = (DeadReckoningParameter)other;
			if( asParam.deadReckoningAlgorithm == this.deadReckoningAlgorithm && 
				Arrays.equals(asParam.deadReckoningOtherParameters, this.deadReckoningOtherParameters) &&
			    asParam.entityLinearAcceleration.equals(this.entityLinearAcceleration) &&
			    asParam.entityAngularVelocity.equals(this.entityAngularVelocity) )
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public DeadReckoningParameter clone()
	{
		VectorRecord linearAccelerationClone = entityLinearAcceleration.clone();
		AngularVelocityVector angularVelocityClone = entityAngularVelocity.clone();
		byte[] otherParametersClone = Arrays.copyOf( deadReckoningOtherParameters, deadReckoningOtherParameters.length );
		
		return new DeadReckoningParameter( deadReckoningAlgorithm, 
		                                   otherParametersClone, 
		                                   linearAccelerationClone, 
		                                   angularVelocityClone );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		deadReckoningAlgorithm = DeadReckoningAlgorithm.fromValue( dis.readUI8() );
		dis.readFully( deadReckoningOtherParameters );
		entityLinearAcceleration.from( dis );
		entityAngularVelocity.from( dis );
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUI8( deadReckoningAlgorithm.value() );
		dos.write( deadReckoningOtherParameters );
		entityLinearAcceleration.to( dos );
		entityAngularVelocity.to( dos );
	}
	
	@Override
	public final int getByteLength()
	{
		return 40; // make it fast
		
		/*
		int size = DeadReckoningAlgorithm.getByteLength();
		size += OTHER_PARAMETERS_ARRAY_SIZE;
		size += entityLinearAcceleration.getByteLength();
		size += entityAngularVelocity.getByteLength();
		return size;
		*/
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public DeadReckoningAlgorithm getDeadReckoningAlgorithm()
    {
    	return deadReckoningAlgorithm;
    }

	public void setDeadReckoningAlgorithm( DeadReckoningAlgorithm deadReckoningAlgorithm )
    {
    	this.deadReckoningAlgorithm = deadReckoningAlgorithm;
    }

	public VectorRecord getEntityLinearAcceleration()
    {
    	return entityLinearAcceleration;
    }

	public void setEntityLinearAcceleration( VectorRecord entityLinearAcceleration )
    {
    	this.entityLinearAcceleration = entityLinearAcceleration;
    }

	public AngularVelocityVector getEntityAngularVelocity()
    {
    	return entityAngularVelocity;
    }

	public void setEntityAngularVelocity( AngularVelocityVector entityAngularVelocity )
    {
    	this.entityAngularVelocity = entityAngularVelocity;
    }
	
	public byte[] getDeadReckoningOtherParamaters()
    {
    	return deadReckoningOtherParameters;
    }

	public void setDeadReckoningOtherParameters( byte[] deadReckoningOtherParameters )
    {
		if( deadReckoningOtherParameters.length != OTHER_PARAMETERS_ARRAY_SIZE )
			throw new IllegalArgumentException( "Dead Reckoning Other Parameters BLOB must be 120-bits in size" );

    	this.deadReckoningOtherParameters = deadReckoningOtherParameters;
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
