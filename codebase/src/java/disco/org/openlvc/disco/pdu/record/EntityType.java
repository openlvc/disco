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
import java.util.StringTokenizer;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.Country;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.EntityKind;
import org.openlvc.disco.pdu.field.Kind;

/**
 * The type of entity in a DIS exercise shall be specified by an Entity Type record. 
 * This record shall specify the kind of entity, the country of design, the domain, 
 * the specific identification of the entity, and any extra information necessary for 
 * describing the entity. Fields not used shall contain the value zero.
 */
public class EntityType implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short kind;
	private short domain;
	private int country;
	private short category;
	private short subcategory;
	private short specific;
	private short extra;
	
	private String cached;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityType()
	{
		this( (short)0, (short)0, 0, (short)0, (short)0, (short)0, (short)0 );
	}
	
	public EntityType( short entityKind,
	                   short domain,
	                   int country, 
	                   short category,
	                   short subcategory,
	                   short specific,
	                   short extra )
	{
		this.kind = entityKind;
		this.domain = domain;
		this.country = country;
		this.category = category;
		this.subcategory = subcategory;
		this.specific = specific;
		this.extra = extra;
		
		this.cached = toString();
	}

	/**
	 * Convenience constructor so that you don't have to cast every `int` you pass in as a
	 * parameter. Note that all fields except country will be cast down to a `short`, so
	 * ensure they are in range.
	 */
	public EntityType( int entityKind,
	                   int domain,
	                   int country,
	                   int category,
	                   int subcategory,
	                   int specific,
	                   int extra )
	{
		this( (short)entityKind,
		      (short)domain,
		      country,
		      (short)category,
		      (short)subcategory,
		      (short)specific,
		      (short)extra );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof EntityType )
		{
			EntityType otherType = (EntityType)other;
			if( otherType.kind == kind &&
				otherType.domain == domain &&
				otherType.country == country &&
				otherType.category == category &&
				otherType.subcategory == subcategory &&
				otherType.specific == specific &&
				otherType.extra == extra )
			{
				return false;
			}
		}

		return false;
	}
	
	@Override
	public EntityType clone()
	{
		return new EntityType( kind, domain, country, category, subcategory, specific, extra );
	}

	public String toString()
	{
		if( cached != null )
			return cached;

		StringBuilder builder = new StringBuilder();
		builder.append( kind );
		builder.append( "." );
		builder.append( domain );
		builder.append( "." );
		builder.append( country );
		builder.append( "." );
		builder.append( category );
		builder.append( "." );
		builder.append( subcategory );
		builder.append( "." );
		builder.append( specific );
		builder.append( "." );
		builder.append( extra );
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		kind = dis.readUI8();
		domain = dis.readUI8();
		country = dis.readUI16();
		category = dis.readUI8();
		subcategory = dis.readUI8();
		specific = dis.readUI8();
		extra = dis.readUI8();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUI8( kind );
		dos.writeUI8( domain );
		dos.writeUI16( country );
		dos.writeUI8( category );
		dos.writeUI8( subcategory );
		dos.writeUI8( specific );
		dos.writeUI8( extra );
	}

	@Override
	public int getByteLength()
	{
		int size = EntityKind.getByteLength();
		size += Domain.getByteLength();
		size += Country.getByteLength();
		size += DisSizes.UI8_SIZE; // Category
		size += DisSizes.UI8_SIZE; // Sub Category
		size += DisSizes.UI8_SIZE; // Specific
		size += DisSizes.UI8_SIZE; // Extra

		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public short getKind()
	{
		return kind;
	}

	public void setKind( short kind )
	{
		this.kind = kind;
	}

	public short getDomain()
	{
		return domain;
	}

	public void setDomain( short domain )
	{
		this.domain = domain;
	}

	public int getCountry()
	{
		return country;
	}

	public void setCountry( int country )
	{
		this.country = country;
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

	public short getSpecific()
	{
		return specific;
	}

	public void setSpecific( short specific )
	{
		this.specific = specific;
	}

	public short getExtra()
	{
		return extra;
	}

	public void setExtra( short extra )
	{
		this.extra = extra;
	}
	
	//
	// Convenience Methods
	//
	public Kind getKindEnum()
	{
		return Kind.fromValue( this.kind );
	}
	
	public void setKind( Kind kind )
	{
		this.kind = kind.value();
	}
	
	public Domain getDomainEnum()
	{
		return Domain.fromValue( this.domain );
	}
	
	public void setDomain( Domain domain )
	{
		this.domain = domain.value();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Turn the given String into an EntityType. Will take any in the form:
	 * 
	 * <ul>
	 *   <li>1 2 3 4 5 6 7</li>
	 *   <li>1.2.3.4.5.6.7</li>
	 *   <li>1-2-3-4-5-6-7</li>
	 * </ul>
	 * 
	 * @param string
	 * @return
	 */
	public static EntityType fromString( String string )
	{
		string = string.trim().replace( "-", "." ).replace( " ", "." );
		StringTokenizer tokenizer = new StringTokenizer( string, "." );
		int[] values = new int[7];
		for( int i = 0; i < values.length; i++ )
		{
			if( tokenizer.hasMoreTokens() )
				values[i] = Integer.parseInt(tokenizer.nextToken());
			else
				values[i] = 0;
		}
		
		return new EntityType( values[0], values[1], values[2], values[3], values[4], values[5], values[6] );
	}

}

