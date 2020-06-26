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

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.ArticulatedPartsTypeEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.ArticulatedTypeMetricEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.pdu.record.ArticulationParameter;

public class ArticulatedPartsStruct extends DiscoHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumHolder<ArticulatedTypeMetricEnum32> typeMetric;
	private EnumHolder<ArticulatedPartsTypeEnum32> theClass;
	private HLAfloat32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ArticulatedPartsStruct()
	{
		this.theClass = new EnumHolder<>( ArticulatedPartsTypeEnum32.Other );
		this.typeMetric = new EnumHolder<>( ArticulatedTypeMetricEnum32.Position );
		this.value = new HLAfloat32BE( 0 );
		
		// Add to the elements in the parent so that it can do its generic fixed-record stuff
		super.add( typeMetric );
		super.add( theClass );
		super.add( value );
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
	public void setValue( ArticulationParameter articulation )
	{
		this.typeMetric.setEnum( ArticulatedTypeMetricEnum32.valueOf(articulation.getArticulatedPartTypeMetric()) );
		this.theClass.setEnum( ArticulatedPartsTypeEnum32.valueOf(articulation.getArticulatedPartTypeClass()) );
		this.value.setValue( articulation.getArticulatedPartParameterValue() );
	}

	public ArticulationParameter getDisValue()
	{
		ArticulationParameter parameter = new ArticulationParameter();
		parameter.setArticulatedPartTypeMetric( (short)this.typeMetric.getEnum().getValue() );
		parameter.setArticulatedPartTypeClass( (int)this.theClass.getEnum().getValue() );
		parameter.setArticulatedPartParameterValue( this.value.getValue() );
		return parameter;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
