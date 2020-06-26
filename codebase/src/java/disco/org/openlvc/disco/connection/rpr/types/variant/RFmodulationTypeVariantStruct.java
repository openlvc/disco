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

import org.openlvc.disco.connection.rpr.types.enumerated.AmplitudeAngleModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.AmplitudeModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.AngleModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.CombinationModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.MajorRFModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.PulseModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.UnmodulatedTypeEnum16;
import org.openlvc.disco.pdu.field.MajorModulationType;

public class RFmodulationTypeVariantStruct extends DiscoHlaVariantRecord<MajorRFModulationTypeEnum16>
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
	public RFmodulationTypeVariantStruct()
	{
		super( MajorRFModulationTypeEnum16.Other );
		
		super.setVariant( MajorRFModulationTypeEnum16.Other, EnumHolder.from(MajorRFModulationTypeEnum16.Other) );
		super.setVariant( MajorRFModulationTypeEnum16.Amplitude, EnumHolder.from(AmplitudeModulationTypeEnum16.Other) );
		super.setVariant( MajorRFModulationTypeEnum16.AmplitudeAndAngle, EnumHolder.from(AmplitudeAngleModulationTypeEnum16.Other) );
		super.setVariant( MajorRFModulationTypeEnum16.Angle, EnumHolder.from(AngleModulationTypeEnum16.Other) );
		super.setVariant( MajorRFModulationTypeEnum16.Combination, EnumHolder.from(CombinationModulationTypeEnum16.Other) );
		super.setVariant( MajorRFModulationTypeEnum16.Pulse, EnumHolder.from(PulseModulationTypeEnum16.Other) );
		super.setVariant( MajorRFModulationTypeEnum16.Unmodulated, EnumHolder.from(UnmodulatedTypeEnum16.Other) );
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
	public void setValue( MajorModulationType type )
	{
		super.setDiscriminant( MajorRFModulationTypeEnum16.valueOf(type.value()) );
	}
	
	public void setAmplitudeModulationType()
	{
		// FIXME
	}
	
	public void setAmplitudeAngleModulationType()
	{
		// FIXME		
	}
	
	public void setAngleModulationType()
	{
		// FIXME
	}

	public void setCombinationModulationType()
	{
		// FIXME
	}

	public void setPulseModulationType()
	{
		// FIXME
	}
	
	public void setUnmodulatedType()
	{
		// FIXME
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
