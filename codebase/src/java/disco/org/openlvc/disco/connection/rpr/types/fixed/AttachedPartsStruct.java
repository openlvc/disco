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

import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.StationEnum32;
import org.openlvc.disco.pdu.record.ArticulationParameter;

public class AttachedPartsStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumHolder<StationEnum32> station;
	private EntityTypeStruct storeType; // Enumeration of the part that is attached

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AttachedPartsStruct()
	{
		this.station = new EnumHolder<>( StationEnum32.Nothing_Empty );
		this.storeType = new EntityTypeStruct();
		
		// Add to the elements in the parent so that it can do its generic fixed-record stuff
		super.add( station );
		super.add( storeType );
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
	public void setValue( ArticulationParameter parameter )
	{
		this.station.setEnum( StationEnum32.valueOf(parameter.getAttachedPartStationId()) );
		this.storeType.setLongValue( Double.doubleToLongBits(parameter.getAttachedPartParameterValue()) );
	}

	public ArticulationParameter getDisValue()
	{
		ArticulationParameter parameter = new ArticulationParameter();
		parameter.setAttachedPartStationId( (int)station.getEnum().getValue() );
		parameter.setAttachedPartParameterValue( storeType.getLongValue() );
		return parameter;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
