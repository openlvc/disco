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
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.pdu.record.EventIdentifier;
import org.openlvc.disco.pdu.record.SimulationAddress;

public class EventIdentifierStruct extends DiscoHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger16BE eventCount;
	private RTIobjectId issuingObjectIdentifier;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EventIdentifierStruct()
	{
		this.eventCount = new RPRunsignedInteger16BE( 0 );
		this.issuingObjectIdentifier = new RTIobjectId();
		
		// Add to elements in parent so that it can do its generic fixed-record encoding/decoding
		super.add( this.eventCount );
		super.add( this.issuingObjectIdentifier );
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
	public void setValue( EventIdentifier event )
	{
		this.eventCount.setValue( event.getEventID() );
		this.issuingObjectIdentifier.setValue( event.getSimulationAddress().toString() );
	}
	
	public EventIdentifier getDisValue()
	{
		EventIdentifier id = new EventIdentifier();
		id.setEventID( eventCount.getValue() );
		id.setSimulationAddress( SimulationAddress.fromString(issuingObjectIdentifier.getValue()) );
		return id;
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
