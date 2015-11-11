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
 * Location of the origin of the entity's coordinate system shall be specified 
 * by a set of three coordinates: X, Y, and Z. The shape of the earth shall be 
 * specified using WGS 84.<br/>
 * <br/>
 * The origin of the world coordinate system shall be the centroid of the earth, 
 * with the X-axis passing through the Prime Meridian at the Equator, the Y-axis 
 * passing through 90 degrees East longitude at the Equator, and the Z-axis 
 * passing through the North pole<br/>
 * <br/> 
 * These coordinates shall represent meters from the centroid of the earth. A 
 * 64-bit double precision floating point number shall represent the location 
 * for each coordinate.
 */
public class WorldCoordinate implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private double x;
	private double y;
	private double z;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WorldCoordinate()
	{
		this( 0d, 0d, 0d );
	}
	
	public WorldCoordinate( double x, double y, double z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof WorldCoordinate )
		{
			WorldCoordinate asWorldCoordinate = (WorldCoordinate)other;
			if( FloatingPointUtils.doubleEqual(asWorldCoordinate.x,this.x) &&
				FloatingPointUtils.doubleEqual(asWorldCoordinate.y,this.y) &&
				FloatingPointUtils.doubleEqual(asWorldCoordinate.z,this.z) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorldCoordinate clone()
	{
		return new WorldCoordinate( x, y, z );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		x = dis.readDouble();
		y = dis.readDouble();
		z = dis.readDouble();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeDouble( x );
		dos.writeDouble( y );
		dos.writeDouble( z );
	}
	
	@Override
	public int getByteLength()
	{
		return DisSizes.FLOAT64_SIZE * 3;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public double getX()
	{
		return x;
	}
	
	public void setX( double x )
	{
		this.x = x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void setY( double y )
	{
		this.y = y;
	}
	
	public double getZ()
	{
		return z;
	}
	
	public void setZ( double z )
	{
		this.z = z;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

