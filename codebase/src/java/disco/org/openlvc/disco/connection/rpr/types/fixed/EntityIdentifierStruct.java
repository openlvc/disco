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

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.pdu.record.EntityId;

public class EntityIdentifierStruct extends DiscoHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private FederateIdentifierStruct federateIdentifier;
	private RPRunsignedInteger16BE entityNumber;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityIdentifierStruct()
	{
		this.federateIdentifier = new FederateIdentifierStruct();
		this.entityNumber = new RPRunsignedInteger16BE( 0 );
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.federateIdentifier );
		super.add( this.entityNumber );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isDefaults()
	{
		return federateIdentifier.getSiteId() == 0 &&
		       federateIdentifier.getApplicationId() == 0 &&
		       entityNumber.getValue() == 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue( EntityId entityId )
	{
		this.federateIdentifier.setValue( entityId );
		this.entityNumber.setValue( entityId.getEntityIdentity() );
	}

	public EntityId getDisValue()
	{
		return new EntityId( federateIdentifier.getSiteId(),
		                     federateIdentifier.getApplicationId(),
		                     entityNumber.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
