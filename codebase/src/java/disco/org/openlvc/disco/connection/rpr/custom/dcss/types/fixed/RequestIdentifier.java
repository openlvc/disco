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
package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.Callsign;
import org.openlvc.disco.connection.rpr.custom.dcss.types.array.UuidArrayOfHLAbyte16;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class RequestIdentifier extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private UuidArrayOfHLAbyte16 uuid;
	private EntityIdentifierStruct entityIdentifier;
	private Callsign callsign;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RequestIdentifier()
	{
		super();
		this.uuid = new UuidArrayOfHLAbyte16();
		this.entityIdentifier = new EntityIdentifierStruct();
		this.callsign = new Callsign();
		this.add( this.uuid, 
		          this.entityIdentifier, 
		          this.callsign );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public UuidArrayOfHLAbyte16 getUuid()
	{
		return this.uuid;
	}
	
	public EntityIdentifierStruct getEntityIdentifier()
	{
		return this.entityIdentifier;
	}
	
	public Callsign getCallsign()
	{
		return this.callsign;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
