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
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.utils.FloatingPointUtils;

/**
 * 
 */
public class PointCoordinate implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float x;
	private float y;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PointCoordinate()
	{
		this( 0.0f, 0.0f );
	}
	
	public PointCoordinate( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof PointCoordinate )
		{
			PointCoordinate asPointCoordinate = (PointCoordinate)other;
			if( FloatingPointUtils.floatEqual(asPointCoordinate.x,this.x) &&
				FloatingPointUtils.floatEqual(asPointCoordinate.y,this.y) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( this.x, this.y );
	}
	
	@Override
	public PointCoordinate clone()
	{
		return new PointCoordinate( x, y );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		x = dis.readFloat();
		y = dis.readFloat();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeDouble( x );
		dos.writeDouble( y );
	}
	
	@Override
	public final int getByteLength()
	{
		return 4;
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
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

