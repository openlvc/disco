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
import org.openlvc.disco.connection.rpr.types.enumerated.AntennaPatternEnum32;
import org.openlvc.disco.connection.rpr.types.fixed.BeamAntennaStruct;
import org.openlvc.disco.connection.rpr.types.fixed.SphericalHarmonicAntennaStruct;
import org.openlvc.disco.pdu.field.AntennaPatternType;

public class AntennaPatternVariantStruct extends DiscoHlaVariantRecord<AntennaPatternEnum32>
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
	public AntennaPatternVariantStruct()
	{
		super( AntennaPatternEnum32.OmniDirectional );
		
		super.setVariant( AntennaPatternEnum32.OmniDirectional, new HLAinteger32BE()/*dummy*/ );
		super.setVariant( AntennaPatternEnum32.Beam, new BeamAntennaStruct() );
		super.setVariant( AntennaPatternEnum32.SphericalHarmonic, new SphericalHarmonicAntennaStruct() );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public void setBeamAntennaParams()
	{
		super.setVariant( AntennaPatternEnum32.Beam, new BeamAntennaStruct()/*replace with passed in param*/ );
	}
	
	public void setSphericalHarmonicParams()
	{
		super.setVariant( AntennaPatternEnum32.SphericalHarmonic,
		                  new SphericalHarmonicAntennaStruct()/*replace with passed in param*/ );
	}
	
	public void setOmniDirectionalParams()
	{
		super.setVariant( AntennaPatternEnum32.OmniDirectional, new HLAinteger32BE()/*dummy*/ );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue( AntennaPatternType type )
	{
		// FIXME -- Add in antenna pattern defaults as param to pass through
		switch( type )
		{
			case OmniDirectional:
				setOmniDirectionalParams();
			case Beam: 
				setBeamAntennaParams();
			case SphericalHarmonic:
				setSphericalHarmonicParams();
		}
	}

	public AntennaPatternType getDisDiscriminant()
	{
		return AntennaPatternType.fromValue( (int)getDiscriminant().getValue() );
	}

	public byte[] getDisValue()
	{
		return new byte[64]; // FIXME - Need better support in Disco for this generally.
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
