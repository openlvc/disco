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

import org.openlvc.disco.connection.rpr.types.enumerated.DeadReckoningAlgorithmEnum8;
import org.openlvc.disco.connection.rpr.types.fixed.SpatialFPStruct;
import org.openlvc.disco.connection.rpr.types.fixed.SpatialFVStruct;
import org.openlvc.disco.connection.rpr.types.fixed.SpatialRPStruct;
import org.openlvc.disco.connection.rpr.types.fixed.SpatialRVStruct;
import org.openlvc.disco.connection.rpr.types.fixed.SpatialStaticStruct;

public class SpatialVariantStruct extends HLAvariantRecord<DeadReckoningAlgorithmEnum8>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public SpatialVariantStruct()
	{
		super();
		
		super.setVariant( DeadReckoningAlgorithmEnum8.Static, new SpatialStaticStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_FPW, new SpatialFPStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_RPW, new SpatialRPStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_RVW, new SpatialRVStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_FVW, new SpatialFVStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_FPB, new SpatialFPStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_RPB, new SpatialRPStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_RVB, new SpatialRVStruct() );
		super.setVariant( DeadReckoningAlgorithmEnum8.DRM_FVB, new SpatialFVStruct() );
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
