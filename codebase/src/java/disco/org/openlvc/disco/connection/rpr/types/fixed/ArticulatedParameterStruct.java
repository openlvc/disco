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

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.variant.ParameterValueVariantStruct;

public class ArticulatedParameterStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet articulatedParameterChange;
	private RPRunsignedInteger16BE partAttachedTo;
	private ParameterValueVariantStruct parameterValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ArticulatedParameterStruct()
	{
		this.articulatedParameterChange = new HLAoctet( 0 );
		this.partAttachedTo = new RPRunsignedInteger16BE( 0 );
		this.parameterValue = new ParameterValueVariantStruct();
		
		// Add to the elements in the parent so that it can do its generic fixed-record stuff
		super.add( articulatedParameterChange );
		super.add( partAttachedTo );
		super.add( parameterValue );
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
