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

import org.openlvc.disco.connection.rpr.types.basic.HLAinteger32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.SpreadSpectrumEnum16;
import org.openlvc.disco.connection.rpr.types.fixed.SINCGARSModulationStruct;
import org.openlvc.disco.pdu.field.SpreadSpectrum;

public class SpreadSpectrumVariantStruct extends DiscoHlaVariantRecord<SpreadSpectrumEnum16>
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
		
		super.setVariant( SpreadSpectrumEnum16.None,                    new HLAinteger32BE()/*dummy*/ );
		super.setVariant( SpreadSpectrumEnum16.SINCGARSFrequencyHop,    new SINCGARSModulationStruct() );
		super.setVariant( SpreadSpectrumEnum16.JTIDS_MIDS_SpectrumType, new HLAinteger32BE()/*dummy*/ );
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
	}

	public void setSINCGARSModulation()
	{
		
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
