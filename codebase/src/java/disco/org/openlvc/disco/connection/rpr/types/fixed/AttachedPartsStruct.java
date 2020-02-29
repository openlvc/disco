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

public class AttachedPartsStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumHolder<StationEnum32> station;
	private EntityTypeStruct storeType;

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
	public void setValue( ArticulationParameter articulation )
	{
		long parameterType = articulation.getParameterType();
		// Bottom 32 - Attached Part
			// 0-31  - Station
		// Upper 32  - Articulated Part Values
		// Value     - Value (Entity Type)
		int narrow = (int)parameterType;
		this.station.setEnum( StationEnum32.valueOf(narrow) );
		this.storeType.setLongValue( articulation.getParameterValue().longValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
