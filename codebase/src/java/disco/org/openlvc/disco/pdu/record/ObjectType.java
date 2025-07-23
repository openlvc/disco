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
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.ObjectKind;

/**
 * The type of synthetic environment point, linear object, and areal object in a DIS exercise shall 
 * be specified by an Object Type record. This record shall specify the domain of the object, the 
 * kind of object, and the specific identification of the entity. Fields not used shall contain the 
 * value zero.
 */
public class ObjectType implements IPduComponent
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short domain;
	private short kind;
	private short category;
	private short subcategory;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectType()
	{
		this( (short)0, (short)0, (short)0, (short)0 );
	}
	
	public ObjectType( short domain,
	                   short kind, 
	                   short category,
	                   short subcategory )
	{
		this.domain = domain;
		this.kind = kind;
		this.category = category;
		this.subcategory = subcategory;
	}

	/**
	 * Convenience constructor so that you don't have to cast every `int` you pass in as a
	 * parameter. Note that all fields except country will be cast down to a `short`, so
	 * ensure they are in range.
	 */
	public ObjectType( int domain,
	                   int kind,
	                   int category,
	                   int subcategory )
	{
		this( (short)domain,
		      (short)kind,
		      (short)category,
		      (short)subcategory );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof ObjectType )
		{
			ObjectType otherType = (ObjectType)other;
			if( otherType.domain == domain &&
				otherType.kind == kind &&
				otherType.category == category &&
				otherType.subcategory == subcategory )
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append( domain );
		builder.append( "." );
		builder.append( kind );
		builder.append( "." );
		builder.append( category );
		builder.append( "." );
		builder.append( subcategory );
		
		return builder.toString();
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		domain = dis.readUI8();
		kind = dis.readUI8();
		category = dis.readUI8();
		subcategory = dis.readUI8();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUI8( domain );
		dos.writeUI8( kind );
		dos.writeUI8( category );
		dos.writeUI8( subcategory );
	}

	@Override
	public final int getByteLength()
	{
		return 4;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public short getDomain()
	{
		return domain;
	}

	public void setDomain( short domain )
	{
		this.domain = domain;
	}

	public short getKind()
	{
		return kind;
	}

	public void setKind( short kind )
	{
		this.kind = kind;
	}

	public short getCategory()
	{
		return category;
	}

	public void setCategory( short category )
	{
		this.category = category;
	}

	public short getSubcategory()
	{
		return subcategory;
	}

	public void setSubcategory( short subcategory )
	{
		this.subcategory = subcategory;
	}
	
	public Domain getDomainEnum()
	{
		return Domain.fromValue( this.domain );
	}
	
	public void setDomain( Domain domain )
	{
		this.domain = domain.value();
	}

	public ObjectKind getKindEnum()
	{
		return ObjectKind.fromValue( this.kind );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

