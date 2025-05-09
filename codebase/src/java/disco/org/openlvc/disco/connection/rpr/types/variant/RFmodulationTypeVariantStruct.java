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

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.enumerated.AmplitudeAngleModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.AmplitudeModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.AngleModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.CombinationModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.MajorRFModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.PulseModulationTypeEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.UnmodulatedTypeEnum16;
import org.openlvc.disco.pdu.field.MajorModulationType;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class RFmodulationTypeVariantStruct extends WrappedHlaVariantRecord<MajorRFModulationTypeEnum16>
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
		super( MajorRFModulationTypeEnum16.Amplitude );

		// These are technically enum types (see commented block below), but to handle them 
		// generically in RadioTransmitter#toPdu() we just shortcut to the raw representation of
		// RPRunsignedInteger16BE
		super.setVariant( MajorRFModulationTypeEnum16.Amplitude,
		                  new RPRunsignedInteger16BE(AmplitudeModulationTypeEnum16.Other.getValue()) );
		super.setVariant( MajorRFModulationTypeEnum16.AmplitudeAndAngle, 
		                  new RPRunsignedInteger16BE(AmplitudeAngleModulationTypeEnum16.Other.getValue()) );
		super.setVariant( MajorRFModulationTypeEnum16.Angle,
		                  new RPRunsignedInteger16BE(AngleModulationTypeEnum16.Other.getValue()) );
		super.setVariant( MajorRFModulationTypeEnum16.Combination,
		                  new RPRunsignedInteger16BE(CombinationModulationTypeEnum16.Other.getValue()) );
		super.setVariant( MajorRFModulationTypeEnum16.Pulse,
		                  new RPRunsignedInteger16BE(PulseModulationTypeEnum16.Other.getValue()) );
		super.setVariant( MajorRFModulationTypeEnum16.Unmodulated,
		                  new RPRunsignedInteger16BE(UnmodulatedTypeEnum16.Other.getValue()) );
		
		// Non-RPR standard values (they're in the DIS standard)
		super.setVariant( MajorRFModulationTypeEnum16.CPSM,
		                  new RPRunsignedInteger16BE(0) );
		super.setVariant( MajorRFModulationTypeEnum16.SATCOM,
		                  new RPRunsignedInteger16BE(0) );

		// Historical
//		super.setVariant( MajorRFModulationTypeEnum16.Other, EnumHolder.from(MajorRFModulationTypeEnum16.Other) );
//		super.setVariant( MajorRFModulationTypeEnum16.Amplitude, EnumHolder.from(AmplitudeModulationTypeEnum16.Other) );
//		super.setVariant( MajorRFModulationTypeEnum16.AmplitudeAndAngle, EnumHolder.from(AmplitudeAngleModulationTypeEnum16.Other) );
//		super.setVariant( MajorRFModulationTypeEnum16.Angle, EnumHolder.from(AngleModulationTypeEnum16.Other) );
//		super.setVariant( MajorRFModulationTypeEnum16.Combination, EnumHolder.from(CombinationModulationTypeEnum16.Other) );
//		super.setVariant( MajorRFModulationTypeEnum16.Pulse, EnumHolder.from(PulseModulationTypeEnum16.Other) );
//		super.setVariant( MajorRFModulationTypeEnum16.Unmodulated, EnumHolder.from(UnmodulatedTypeEnum16.Other) );
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
	
	public void setValue( MajorModulationType type, int detail )
	{
		MajorRFModulationTypeEnum16 key = MajorRFModulationTypeEnum16.valueOf( type.value() );
		super.setVariant( key, new RPRunsignedInteger16BE(detail) );
		super.setDiscriminant( key );
	}

	public int getDetail()
	{
		return ((RPRunsignedInteger16BE)super.getValue()).getValue();
	}
	
	public void decode( ByteWrapper wrapper ) throws DecoderException
	{
		super.decode( wrapper );
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
