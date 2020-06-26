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
package org.openlvc.disco.connection.rpr.types.variant;

import org.openlvc.disco.connection.rpr.types.enumerated.ConstituentPartStationNameEnum16;
import org.openlvc.disco.connection.rpr.types.fixed.RelativePositionStruct;
import org.openlvc.disco.connection.rpr.types.fixed.RelativeRangeBearingStruct;

public class StationNameLocationVariantStruct extends DiscoHlaVariantRecord<ConstituentPartStationNameEnum16>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public StationNameLocationVariantStruct()
	{
		super( ConstituentPartStationNameEnum16.Other );
		
		super.setVariant( ConstituentPartStationNameEnum16.OnStationXYZ, new RelativePositionStruct() );
		super.setVariant( ConstituentPartStationNameEnum16.OnStationRangeBearing, new RelativeRangeBearingStruct() );
	}

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue()
	{
		
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
