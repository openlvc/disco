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

import org.openlvc.disco.connection.rpr.types.basic.Empty;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.enumerated.SpreadSpectrumEnum16;
import org.openlvc.disco.connection.rpr.types.fixed.SINCGARSModulationStruct;
import org.openlvc.disco.pdu.field.SpreadSpectrum;

public class SpreadSpectrumVariantStruct extends WrappedHlaVariantRecord<SpreadSpectrumEnum16>
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
	public SpreadSpectrumVariantStruct()
	{
		super( SpreadSpectrumEnum16.None );
		
		super.setVariant( SpreadSpectrumEnum16.None,                    new Empty()/*dummy*/ );
		super.setVariant( SpreadSpectrumEnum16.SINCGARSFrequencyHop,    new SINCGARSModulationStruct() );
		super.setVariant( SpreadSpectrumEnum16.JTIDS_MIDS_SpectrumType, new RPRunsignedInteger16BE()/*dummy*/ );
		
		super.setDiscriminant( SpreadSpectrumEnum16.None );
	}
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Method Overrides   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue( SpreadSpectrum spreadSpectrum )
	{
		if( spreadSpectrum.isFrequencyHopping() )
		{
			super.setDiscriminant( SpreadSpectrumEnum16.SINCGARSFrequencyHop );
		}
		else
		{
			super.setDiscriminant( SpreadSpectrumEnum16.None );
		}
	}

	public void setSINCGARSModulation()
	{
		
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
