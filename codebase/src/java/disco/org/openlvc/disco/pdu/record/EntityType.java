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
import org.openlvc.disco.pdu.field.Country;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.EntityKind;

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
	private short entityKind;
	private short domain;
	private int country;
	private short category;
	private short subcategory;
	private short specific;
	private short extra; 
	
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
		this.entityKind = entityKind;
		this.domain = domain;
		this.country = country;
		this.category = category;
		this.subcategory = subcategory;
		this.specific = specific;
		this.extra = extra;
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
			if( otherType.entityKind == entityKind &&
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
		return new EntityType( entityKind, domain, country, category, subcategory, specific, extra );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		entityKind = dis.readUI8();
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
		dos.writeUI8( entityKind );
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
	public short getEntityKind()
	{
		return entityKind;
	}

	public void setEntityKind( short entityKind )
	{
		this.entityKind = entityKind;
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}

