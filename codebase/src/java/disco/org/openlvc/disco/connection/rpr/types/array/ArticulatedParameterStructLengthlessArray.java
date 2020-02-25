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
package org.openlvc.disco.connection.rpr.types.array;

import org.openlvc.disco.connection.rpr.types.fixed.ArticulatedParameterStruct;

import hla.rti1516e.encoding.DataElementFactory;

public class ArticulatedParameterStructLengthlessArray
	extends RPRlengthlessArray<ArticulatedParameterStruct>
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
	public ArticulatedParameterStructLengthlessArray()
	{
		super( new ArticulatedParameterStructLengthlessArray.Factory() );
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
	
	public static ArticulatedParameterStructLengthlessArray from( ArticulatedParameterStruct... values )
	{
		ArticulatedParameterStructLengthlessArray array =
			new ArticulatedParameterStructLengthlessArray();

		for( ArticulatedParameterStruct value : values )
			array.addElement( value );
		
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Factory Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private static class Factory implements DataElementFactory<ArticulatedParameterStruct>
	{
    	@Override
    	public ArticulatedParameterStruct createElement( int index )
    	{
    		return new ArticulatedParameterStruct();
    	}
	}
}
