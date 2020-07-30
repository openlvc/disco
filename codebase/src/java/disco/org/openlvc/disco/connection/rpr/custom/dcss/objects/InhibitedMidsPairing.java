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
package org.openlvc.disco.connection.rpr.custom.dcss.objects;

import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.InhibitedMidsPairingPdu;

public class InhibitedMidsPairing extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE tdlType;
	private EntityIdentifierStruct sourceEntityId;
	private EntityIdentifierStruct destinationEntityId;
	private RPRboolean isMidsTerminalEnabled;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public InhibitedMidsPairing()
	{
		super();

		this.tdlType = new RPRunsignedInteger32BE( 0L );
		this.sourceEntityId = new EntityIdentifierStruct();
		this.destinationEntityId = new EntityIdentifierStruct();
		this.isMidsTerminalEnabled = new RPRboolean( true );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		InhibitedMidsPairingPdu pdu = incoming.as( InhibitedMidsPairingPdu.class );

		// TDL type
		this.tdlType.setValue( pdu.getTdlType() );

		// source entity ID
		this.sourceEntityId.setValue( pdu.getSourceEntityId() );

		// destination entity ID
		this.destinationEntityId.setValue( pdu.getDestinationEntityId() );

		// is MIDS terminal enabled?
		this.isMidsTerminalEnabled.setValue( pdu.isMidsTerminalEnabled() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		InhibitedMidsPairingPdu pdu = new InhibitedMidsPairingPdu();

		// TDL type
		pdu.setTdlType( (int)tdlType.getValue() );

		// source entity ID
		pdu.setSourceEntityId( sourceEntityId.getDisValue() );

		// destination entity ID
		pdu.setDestinationEntityId( destinationEntityId.getDisValue() );

		// is MIDS terminal enabled?
		pdu.setMidsTerminalEnabled( isMidsTerminalEnabled.getValue() );

		return pdu;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RPRunsignedInteger32BE getTdlType()
	{
		return this.tdlType;
	}

	public EntityIdentifierStruct getSourceEntityId()
	{
		return this.sourceEntityId;
	}

	public EntityIdentifierStruct getDestinationEntityId()
	{
		return this.destinationEntityId;
	}

	public RPRboolean isMidsTerminalEnabled()
	{
		return this.isMidsTerminalEnabled;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
