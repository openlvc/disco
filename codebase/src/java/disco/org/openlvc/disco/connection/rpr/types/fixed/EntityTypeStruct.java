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
package org.openlvc.disco.connection.rpr.types.fixed;

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.EntityKind;
import org.openlvc.disco.pdu.record.EntityType;

public class EntityTypeStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet kind;
	private HLAoctet domain;
	private RPRunsignedInteger16BE countryCode;
	private HLAoctet category;
	private HLAoctet subcategory;
	private HLAoctet specific;
	private HLAoctet extra;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityTypeStruct()
	{
		this.kind = new HLAoctet( 0 );
		this.domain = new HLAoctet( 0 );
		this.countryCode = new RPRunsignedInteger16BE( 0 );
		this.category = new HLAoctet( 0 );
		this.subcategory = new HLAoctet( 0 );
		this.specific = new HLAoctet( 0 );
		this.extra = new HLAoctet( 0 );

		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.kind );
		super.add( this.domain );
		super.add( this.countryCode );
		super.add( this.category );
		super.add( this.subcategory );
		super.add( this.specific );
		super.add( this.extra );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString()
	{
		return this.kind.getValue()+"."+this.domain.getValue()+"."+this.countryCode.getValue()+"."+
		       this.category.getValue()+"."+this.subcategory.getValue()+"."+
		       this.specific.getValue()+"."+this.extra.getValue();
	}
	
	public boolean isDefaults()
	{
		return this.kind.getValue()        == 0 &&
		       this.domain.getValue()      == 0 &&
		       this.countryCode.getValue() == 0 &&
		       this.category.getValue()    == 0 &&
		       this.subcategory.getValue() == 0 &&
		       this.specific.getValue()    == 0 &&
		       this.extra.getValue()       == 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setLongValue( long bits )
	{
		kind.setValue( (byte)(bits) );
		domain.setValue( (byte)(bits >> 8) );
		countryCode.setValue( (short)(bits >> 16) );
		category.setValue( (byte)(bits >> 32) );
		subcategory.setValue( (byte)(bits >> 40) );
		specific.setValue( (byte)(bits >> 48) );
		extra.setValue( (byte)(bits >> 56) );
	}
	
	public long getLongValue()
	{
		return ((long)kind.getValue())               |
		       ((long)domain.getValue()      << 8)   |
		       ((long)countryCode.getValue() << 16)  |
		       ((long)category.getValue()    << 32)  |
		       ((long)subcategory.getValue() << 40)  |
		       ((long)specific.getValue()    << 48)  |
		       ((long)extra.getValue()       << 56);
	}

	public void setValue( EntityType type )
	{
		this.kind.setUnsignedValue( type.getKind() );
		this.domain.setUnsignedValue( type.getDomain() );
		this.countryCode.setValue( type.getCountry() );
		this.category.setUnsignedValue( type.getCategory() );
		this.subcategory.setUnsignedValue( type.getSubcategory() );
		this.specific.setUnsignedValue( type.getSpecific() );
		this.extra.setUnsignedValue( type.getExtra() );
	}

	public EntityType getDisValue()
	{
		EntityType type = new EntityType();
		type.setKind( EntityKind.fromValue(kind.getUnsignedValue()) );
		type.setDomain( Domain.fromValue(domain.getUnsignedValue()) );
		type.setCountry( countryCode.getValue() );
		type.setCategory( category.getUnsignedValue() );
		type.setSubcategory( subcategory.getUnsignedValue() );
		type.setSpecific( specific.getUnsignedValue());
		type.setExtra( extra.getUnsignedValue() );
		return type;
	}

	public EntityKind getDisKind()
	{
		return EntityKind.fromValue( kind.getUnsignedValue() );
	}

	public Domain getDisDomain()
	{
		return Domain.fromValue( domain.getUnsignedValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
