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
package org.openlvc.disco.application;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;

/**
 * The {@link PduStore} is the core PDU repository for a Disco {@link DisApplication}.
 * The store itself is an aggregator of a number of sub-stores that focus on methods for
 * particular PDU types. Sub-stores can be accessed through getters.
 */
public class PduStore
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityStateStore entityStore;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduStore()
	{
		this.entityStore = new EntityStateStore();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	protected void pduReceived( PDU pdu )
	{
		switch( pdu.getType() )
		{
			case EntityState:
				entityStore.receivePdu( (EntityStatePdu)pdu );
				break;

			// PDUs to support next
			case Transmitter:
				break;
			case Signal:
				break;
			case Fire:
				break;
			case Detonation:
				break;
			case DataQuery:
				break;
			case Data:
				break;
			case SetData:
				break;
			case Emission:
				break;
			case Designator:
				break;
			default:
				break;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		this.entityStore.clear();
	}
	
	public EntityStateStore getEntityStore()
	{
		return this.entityStore;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
