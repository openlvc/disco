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
 * An abstract Vector record with 3 dimensions
 */
public class VectorRecord implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private float firstComponent;
	private float secondComponent;
	private float thirdComponent;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public VectorRecord()
	{
		this( 0f, 0f, 0f );
	}
	
	public VectorRecord( float firstComponent,
	                     float secondComponent,
	                     float thirdComponent )
	{
		this.firstComponent = firstComponent;
		this.secondComponent = secondComponent;
		this.thirdComponent = thirdComponent;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof VectorRecord )
		{
			VectorRecord otherVector = (VectorRecord)other;
			if( FloatingPointUtils.floatEqual(otherVector.firstComponent, this.firstComponent) &&
			    FloatingPointUtils.floatEqual(otherVector.secondComponent, this.secondComponent) &&
			    FloatingPointUtils.floatEqual(otherVector.thirdComponent, this.thirdComponent) )
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public VectorRecord clone()
	{
		return new VectorRecord( firstComponent, secondComponent, thirdComponent );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		firstComponent = dis.readFloat();
		secondComponent = dis.readFloat();
		thirdComponent = dis.readFloat();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeFloat( firstComponent );
		dos.writeFloat( secondComponent );
		dos.writeFloat( thirdComponent );
    }
	
	@Override
	public int getByteLength()
	{
		return DisSizes.FLOAT32_SIZE * 3;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public float getfirstComponent()
	{
		return firstComponent;
	}

	public void setfirstComponent( float firstComponent )
	{
		this.firstComponent = firstComponent;
	}

	public float getsecondComponent()
	{
		return secondComponent;
	}

	public void setsecondComponent( float secondComponent )
	{
		this.secondComponent = secondComponent;
	}

	public float getthirdComponent()
	{
		return thirdComponent;
	}

	public void setthirdComponent( float thirdComponent )
	{
		this.thirdComponent = thirdComponent;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
