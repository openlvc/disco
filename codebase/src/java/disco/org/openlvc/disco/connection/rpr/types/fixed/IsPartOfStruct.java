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

import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;

/**
 * The IsPartOf record is not supported yet. It is designed to somewhat mimic the capabilities
 * of the IsPartOf PDU (DIS v6) which we don't have support for yet either. For now, this will
 * simply not be sent with any update.
 */
public class IsPartOfStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityIdentifierStruct hostEntityIdentifier;
	private RTIobjectId hostRTIObjectIdentifier;
	private ConstituentPartRelationshipStruct relationship;
	private NamedLocationStruct namedLocation;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IsPartOfStruct()
	{
		this.hostEntityIdentifier = new EntityIdentifierStruct();
		this.hostRTIObjectIdentifier = new RTIobjectId();
		this.relationship = new ConstituentPartRelationshipStruct();
		this.namedLocation = new NamedLocationStruct();
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( hostEntityIdentifier );
		super.add( hostRTIObjectIdentifier );
		super.add( relationship );
		super.add( namedLocation );
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
