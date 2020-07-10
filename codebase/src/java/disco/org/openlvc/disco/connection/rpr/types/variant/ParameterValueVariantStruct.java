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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.enumerated.ParameterTypeEnum32;
import org.openlvc.disco.connection.rpr.types.fixed.ArticulatedPartsStruct;
import org.openlvc.disco.connection.rpr.types.fixed.AttachedPartsStruct;
import org.openlvc.disco.pdu.field.ParameterTypeDesignator;
import org.openlvc.disco.pdu.record.ArticulationParameter;

public class ParameterValueVariantStruct extends WrappedHlaVariantRecord<ParameterTypeEnum32>
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
	public ParameterValueVariantStruct()
	{
		super( ParameterTypeEnum32.ArticulatedPart );
		
		super.setVariant( ParameterTypeEnum32.ArticulatedPart, new ArticulatedPartsStruct() );
		super.setVariant( ParameterTypeEnum32.AttachedPart, new AttachedPartsStruct() );
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
	public void setValue( ArticulationParameter disValue )
	{
		if( disValue.getTypeDesignator() == ParameterTypeDesignator.ArticulatedPart )
		{
			ArticulatedPartsStruct struct = new ArticulatedPartsStruct();
			struct.setValue( disValue );
			this.setVariant( ParameterTypeEnum32.ArticulatedPart, struct );
			this.setDiscriminant( ParameterTypeEnum32.ArticulatedPart );
		}
		else if( disValue.getTypeDesignator() == ParameterTypeDesignator.AttachedPart )
		{
			AttachedPartsStruct struct = new AttachedPartsStruct();
			struct.setValue( disValue );
			this.setVariant( ParameterTypeEnum32.AttachedPart, struct );
			this.setDiscriminant( ParameterTypeEnum32.AttachedPart );
		}
		else
		{
			throw new DiscoException( "Unknown Articulated Parameter Type: "+disValue.getTypeDesignator() );
		}
	}

	/**
	 * @return The attached parts variant struct but only if it is active, otherwise null
	 */
	public AttachedPartsStruct getAttachedParts()
	{
		if( getDiscriminant() != ParameterTypeEnum32.AttachedPart )
			return null;
		else
			return (AttachedPartsStruct)getValue();
	}
	
	/**
	 * @return The articulated parts variant struct but only if it is active, otherwise null
	 */
	public ArticulatedPartsStruct getArticulatedParts()
	{
		if( getDiscriminant() != ParameterTypeEnum32.ArticulatedPart )
			return null;
		else
			return (ArticulatedPartsStruct)getValue();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
