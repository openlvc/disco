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
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.NomenclatureEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.NomenclatureVersionEnum8;
import org.openlvc.disco.pdu.field.Country;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.EntityKind;
import org.openlvc.disco.pdu.record.RadioEntityType;

public class RadioTypeStruct extends DiscoHlaFixedRecord
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
	private EnumHolder<NomenclatureVersionEnum8> nomenclatureVersion;
	private EnumHolder<NomenclatureEnum16> nomenclature;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RadioTypeStruct()
	{
		this.kind = new HLAoctet( 0 );
		this.domain = new HLAoctet( 0 );
		this.countryCode = new RPRunsignedInteger16BE( 0 );
		this.category = new HLAoctet( 0 );
		this.nomenclatureVersion = new EnumHolder<>( NomenclatureVersionEnum8.Other );
		this.nomenclature = new EnumHolder<>( NomenclatureEnum16.Other );

		// Add to the elements in the parent so that it can do its generic fixed-record stuff
		super.add( this.kind );
		super.add( this.domain );
		super.add( this.countryCode );
		super.add( this.category );
		super.add( this.nomenclatureVersion );
		super.add( this.nomenclature );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue( RadioEntityType type )
	{
		this.kind.setUnsignedValue( type.getEntityKind().value() );
		this.domain.setUnsignedValue( type.getDomain().value() );
		this.countryCode.setValue( type.getCountry().value() );
		this.category.setUnsignedValue( type.getCategory() );
		this.nomenclatureVersion.setEnum( NomenclatureVersionEnum8.valueOf(type.getNomenclatureVersion()) );
		this.nomenclature.setEnum( NomenclatureEnum16.valueOf(type.getNomenclature()) );
	}

	public RadioEntityType getDisValue()
	{
		RadioEntityType type = new RadioEntityType();
		type.setEntityKind( EntityKind.fromValue(kind.getUnsignedValue()) );
		type.setDomain( Domain.fromValue(domain.getUnsignedValue()) );
		type.setCountry( Country.fromValue(countryCode.getValue()) );
		type.setCategory( category.getUnsignedValue() );
		type.setNomenclatureVersion( nomenclatureVersion.getEnum().getUnsignedValue() );
		type.setNomenclature( nomenclature.getEnum().getValue() );
		return type;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
