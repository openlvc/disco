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

import org.openlvc.disco.connection.rpr.types.enumerated.ConstituentPartNatureEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.ConstituentPartPositionEnum16;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;

public class ConstituentPartRelationshipStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumHolder<ConstituentPartNatureEnum16> nature;
	private EnumHolder<ConstituentPartPositionEnum16> position;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ConstituentPartRelationshipStruct()
	{
		this.nature = new EnumHolder<>( ConstituentPartNatureEnum16.Other );
		this.position = new EnumHolder<>( ConstituentPartPositionEnum16.Other );
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.nature );
		super.add( this.position );
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

	public void getDisValue()
	{
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
