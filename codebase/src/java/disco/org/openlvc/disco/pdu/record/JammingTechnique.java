/*
 *   Copyright 2020 Open LVC Project.
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
import org.openlvc.disco.pdu.IPduComponent;

/**
 * DIS 7 - 6.2.49: Jamming Technique Record
 */
public class JammingTechnique implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int MAX_SIZE = 255;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short kind;
	private short category;
	private short subcategory;
	private short specific;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public JammingTechnique()
	{
		this.kind = 0;
		this.category = 0;
		this.subcategory = 0;
		this.specific = 0;
	}
	
	public JammingTechnique( int kind, int category, int subcategory, int specific )
	{
		setKind( kind );
		setCategory( category );
		setSubcategory( subcategory );
		setSpecific( specific );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public boolean equals( Object object )
	{
		if( this == object )
			return true;

		if( object instanceof JammingTechnique )
		{
			JammingTechnique other = (JammingTechnique)object;
			if( other.kind == this.kind &&
				other.category == this.category &&
				other.subcategory == this.subcategory &&
				other.specific == this.specific )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public JammingTechnique clone()
	{
		return new JammingTechnique( kind, category, subcategory, specific );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		this.kind = dis.readUI8();
		this.category = dis.readUI8();
		this.subcategory = dis.readUI8();
		this.specific = dis.readUI8();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI8( this.kind );
		dos.writeUI8( this.category );
		dos.writeUI8( this.subcategory );
		dos.writeUI8( this.specific );
    }
	
	@Override
    public final int getByteLength()
	{
		return 4;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getKind()
	{
		return kind;
	}

	public void setKind( int kind )
	{
		if( kind > MAX_SIZE )
			throw new IllegalArgumentException( "Value exceeds threshold: field=kind, max=256, found="+kind );
		
		this.kind = (short)kind;
	}

	public int getCategory()
	{
		return category;
	}

	public void setCategory( int category )
	{
		if( category > MAX_SIZE )
			throw new IllegalArgumentException( "Value exceeds threshold: field=category, max=256, found="+category );
		
		this.category = (short)category;
	}

	public int getSubcategory()
	{
		return subcategory;
	}

	public void setSubcategory( int subcategory )
	{
		if( subcategory > MAX_SIZE )
			throw new IllegalArgumentException( "Value exceeds threshold: field=subcategory, max=256, found="+subcategory );
		
		this.subcategory = (short)subcategory;
	}

	public int getSpecific()
	{
		return specific;
	}

	public void setSpecific( int specific )
	{
		if( specific > MAX_SIZE )
			throw new IllegalArgumentException( "Value exceeds threshold: field=specific, max=256, found="+specific );
		
		this.specific = (short)specific;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
