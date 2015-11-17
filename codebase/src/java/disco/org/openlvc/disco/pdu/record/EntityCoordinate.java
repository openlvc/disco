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
import org.openlvc.disco.utils.FloatingPointUtils;

/**
 * Location with respect to a particular entity shall be specified with respect 
 * to three orthogonal axes whose origin shall be the center of the bounding 
 * volume of the entity excluding its articulated and attached parts.<br/>
 * <br/>
 * The x-axis extends in the positive direction out the front of the entity. The 
 * y-axis extends in the positive direction out the right side of the entity as 
 * viewed from above, facing in the direction of the positive x-axis. The z-axis 
 * extends in the positive direction out the bottom of the entity.
 */
public class EntityCoordinate implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float x;
	private float y;
	private float z;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityCoordinate()
	{
		this( 0f, 0f, 0f );
	}
	
	public EntityCoordinate( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof EntityCoordinate )
		{
			EntityCoordinate asEntityCoordinate = (EntityCoordinate)other;
			if( FloatingPointUtils.floatEqual(asEntityCoordinate.x,this.x) &&
				FloatingPointUtils.floatEqual(asEntityCoordinate.y,this.y) &&
				FloatingPointUtils.floatEqual(asEntityCoordinate.z,this.z) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public EntityCoordinate clone()
	{
		return new EntityCoordinate( x, y, z );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		x = dis.readFloat();
		y = dis.readFloat();
		z = dis.readFloat();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeFloat( x );
		dos.writeFloat( y );
		dos.writeFloat( z );
	}

	@Override
	public int getByteLength()
	{
		return DisSizes.FLOAT32_SIZE * 3;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public float getX()
	{
		return x;
	}
	
	public void setX( float x )
	{
		this.x = x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setY( float y )
	{
		this.y = y;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public void setZ( float z )
	{
		this.z = z;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
