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

import org.openlvc.disco.connection.rpr.types.variant.AntennaPatternVariantStruct;

import hla.rti1516e.encoding.DataElementFactory;

public class AntennaPatternVariantStructLengthlessArray
       extends RPRlengthlessArray<AntennaPatternVariantStruct>
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
	public AntennaPatternVariantStructLengthlessArray()
	{
		super( new AntennaPatternVariantStructLengthlessArray.Factory() );
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
	
	public static AntennaPatternVariantStructLengthlessArray from( AntennaPatternVariantStruct... values )
	{
		AntennaPatternVariantStructLengthlessArray array =
			new AntennaPatternVariantStructLengthlessArray();

		for( AntennaPatternVariantStruct value : values )
			array.addElement( value );
		
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Factory Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private static class Factory implements DataElementFactory<AntennaPatternVariantStruct>
	{
    	@Override
    	public AntennaPatternVariantStruct createElement( int index )
    	{
    		return new AntennaPatternVariantStruct();
    	}
	}
}
