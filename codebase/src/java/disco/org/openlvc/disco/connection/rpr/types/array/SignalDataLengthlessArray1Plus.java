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

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import hla.rti1516e.encoding.DataElementFactory;

public class SignalDataLengthlessArray1Plus extends RPRlengthlessArray<HLAoctet>
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
	public SignalDataLengthlessArray1Plus()
	{
		super( new SignalDataLengthlessArray1Plus.Factory() );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void setValue( byte[] values )
	{
		clear();
		for( byte temp : values )
			this.addElement( new HLAoctet(temp) );
	}

	public byte[] getDisValue()
	{
		byte[] bytes = new byte[this.size()];
		for( int i = 0; i < this.size(); i++ )
			bytes[i] = super.get(i).getValue();
		
		return bytes;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		super.elements.clear();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	public static SignalDataLengthlessArray1Plus from( HLAoctet... values )
	{
		SignalDataLengthlessArray1Plus array = new SignalDataLengthlessArray1Plus();

		for( HLAoctet value : values )
			array.addElement( value );
		
		return array;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Factory Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private static class Factory implements DataElementFactory<HLAoctet>
	{
    	@Override
    	public HLAoctet createElement( int index )
    	{
    		return new HLAoctet();
    	}
	}
}
