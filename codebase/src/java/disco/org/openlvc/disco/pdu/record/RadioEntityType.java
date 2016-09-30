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
import org.openlvc.disco.pdu.field.Country;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.EntityKind;

/**
 * The type of radio in a DIS exercise shall be specified by a Radio Entity Type 
 * Record. This record shall specify the kind of entity, the domain, the country 
 * of design, and specific information about the radio.
 */
public class RadioEntityType implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityKind entityKind;
	private Domain domain;
	private Country country;
	private short category;
	private short nomenclatureVersion;
	private int nomenclature;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RadioEntityType()
	{
		this( EntityKind.Radio, Domain.Other, Country.Other, (short)0, (short)0, 0 );
	}
	
	public RadioEntityType( EntityKind entityKind,
	                        Domain domain,
	                        Country country, 
	                        short category,
	                        short nomenclatureVersion, 
	                        int nomenclature )
	{
		this.entityKind = entityKind;
		this.domain = domain;
		this.country = country;
		this.category = category;
		this.nomenclatureVersion = nomenclatureVersion;
		this.nomenclature = nomenclature;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof RadioEntityType )
		{
			RadioEntityType asRadioEntityType = (RadioEntityType)other;
			if( asRadioEntityType.entityKind == this.entityKind &&
				asRadioEntityType.domain == this.domain &&
				asRadioEntityType.country == this.country &&
				asRadioEntityType.category == this.category &&
				asRadioEntityType.nomenclatureVersion == this.nomenclatureVersion &&
				asRadioEntityType.nomenclature == this.nomenclature )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public RadioEntityType clone()
	{
		return new RadioEntityType( entityKind, 
		                            domain, 
		                            country, 
		                            category, 
		                            nomenclatureVersion, 
		                            nomenclature );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		entityKind = EntityKind.fromValue( dis.readUI8() );
		domain = Domain.fromValue( dis.readUI8() );
		country = Country.fromValue( dis.readUI16() );
        category = dis.readUI8();
        nomenclatureVersion = dis.readUI8();
        nomenclature = dis.readUI16();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI8( entityKind.value() );
		dos.writeUI8( domain.value() );
		dos.writeUI16( country.value() );
		dos.writeUI8( category );
		dos.writeUI8( nomenclatureVersion );
		dos.writeUI16( nomenclature );
    }
	
	@Override
    public final int getByteLength()
	{
		return 8;
		
		// int size = EntityKind.getByteLength();
		// size += Domain.getByteLength();
		// size += Country.getByteLength();
		// size += DisSizes.UI8_SIZE;		// Category
		// size += DisSizes.UI8_SIZE;		// Nomenclature Version
		// size += DisSizes.UI16_SIZE;		// Nomenclature
		// return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityKind getEntityKind()
	{
		return entityKind;
	}
	
	public void setEntityKind( EntityKind entityKind )
	{
		this.entityKind = entityKind;
	}
	
	public Domain getDomain()
	{
		return domain;
	}
	
	public void setDomain( Domain domain )
	{
		this.domain = domain;
	}
	
	public Country getCountry()
	{
		return country;
	}
	
	public void setCountry( Country country )
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
	
	public short getNomenclatureVersion()
	{
		return nomenclatureVersion;
	}
	
	public void setNomenclatureVersion( short nomenclatureVersion )
	{
		this.nomenclatureVersion = nomenclatureVersion;
	}
	
	public int getNomenclature()
	{
		return nomenclature;
	}
	
	public void setNomenclature( int nomenclature )
	{
		this.nomenclature = nomenclature;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
