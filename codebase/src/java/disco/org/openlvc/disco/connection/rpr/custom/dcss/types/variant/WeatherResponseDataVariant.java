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
package org.openlvc.disco.connection.rpr.custom.dcss.types.variant;

import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.Domain;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.AtmosphereResponseData;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.CloudResponseData;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.GroundResponseData;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.SubsurfaceResponseData;
import org.openlvc.disco.connection.rpr.custom.dcss.types.fixed.SurfaceResponseData;
import org.openlvc.disco.connection.rpr.types.variant.WrappedHlaVariantRecord;

public class WeatherResponseDataVariant extends WrappedHlaVariantRecord<Domain>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherResponseDataVariant()
	{
		super( Domain.InvalidDomain );
		this.setVariant( Domain.Atmosphere, new AtmosphereResponseData() );
		this.setVariant( Domain.Ground, new GroundResponseData() );
		this.setVariant( Domain.Cloud, new CloudResponseData() );
		this.setVariant( Domain.Surface, new SurfaceResponseData() );
		this.setVariant( Domain.Subsurface, new SubsurfaceResponseData() );
	}

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
