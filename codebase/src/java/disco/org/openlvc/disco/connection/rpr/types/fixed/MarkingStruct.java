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

import java.nio.charset.StandardCharsets;

import org.openlvc.disco.connection.rpr.types.array.MarkingArray11;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.MarkingEncodingEnum8;

public class MarkingStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumHolder<MarkingEncodingEnum8> markingEncodingType;
	private MarkingArray11 markingData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public MarkingStruct()
	{
		this.markingEncodingType = new EnumHolder<>( MarkingEncodingEnum8.ASCII );
		this.markingData = new MarkingArray11();
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.markingEncodingType );
		super.add( this.markingData );
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
	public void setValue( String value )
	{
		byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
		for( int i = 0; i < 11; i++ )
			this.markingData.get(i).setValue( bytes[i] );
	}
	
	public String getDisValue()
	{
		return new String( markingData.toByteArray(), StandardCharsets.US_ASCII );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
