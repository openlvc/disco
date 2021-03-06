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

import org.openlvc.disco.connection.rpr.types.basic.HLAinteger16BE;
import org.openlvc.disco.connection.rpr.types.variant.StationNameLocationVariantStruct;

public class NamedLocationStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger16BE stationNumber;
	private StationNameLocationVariantStruct stationName;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public NamedLocationStruct()
	{
		this.stationNumber = new HLAinteger16BE();
		this.stationName = new StationNameLocationVariantStruct();

		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( stationNumber );
		super.add( stationName );
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
	public void setValue()
	{
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
