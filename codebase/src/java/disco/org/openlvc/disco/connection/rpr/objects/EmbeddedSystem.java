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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.connection.rpr.types.fixed.RelativePositionStruct;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;

public abstract class EmbeddedSystem extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected EntityIdentifierStruct entityIdentifier;
	protected RTIobjectId hostObjectIdentifier;
	protected RelativePositionStruct relativePosition;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EmbeddedSystem()
	{
		this.entityIdentifier = new EntityIdentifierStruct();
		this.hostObjectIdentifier = new RTIobjectId();
		this.relativePosition = new RelativePositionStruct();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public EntityId getDisId()
	{
		return this.entityIdentifier.getDisValue();
	}

	@Override
	protected boolean checkLoaded()
	{
		return entityIdentifier.isDecodeCalled(); // Entity ID initialized
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityIdentifierStruct getEntityIdentifier()
	{
		return entityIdentifier;
	}

	public RTIobjectId getHostObjectIdentifier()
	{
		return hostObjectIdentifier;
	}

	/**
	 * Set the local host object identifier to <i>match</i> the given {@link RTIobjectId}.
	 * This won't store a reference to the ID, but rather make the local one use the same
	 * string. This way, we are safe to modify the local without affecting the original.
	 * 
	 * @param hostIdentifier The ID of the host object that this platform is attached to
	 *                       and for which we should copy the value from.
	 */
	public void setHostObjectIdentifier( RTIobjectId hostIdentifier )
	{
		if( hostIdentifier == null )
			this.hostObjectIdentifier.setValue( "" );
		else
			this.hostObjectIdentifier.setValue( hostIdentifier.getValue() );
	}

	public RelativePositionStruct getRelativePosition()
	{
		return relativePosition;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Transmitters
	//
	protected void fromPdu( TransmitterPdu incoming )
	{
		this.entityIdentifier.setValue( incoming.getEntityId() );
		this.relativePosition.setValue( incoming.getAntennaLocation().getRelativeAntennaLocation() );

		// Do in Mapper - Required access to RTIobjectId <> DIS EntityId cache
		//this.hostObjectIdentifier.setValue( incoming.getEntityId().toString() );
	}
	
	protected void toPdu( TransmitterPdu pdu )
	{
		pdu.setEntityId( entityIdentifier.getDisValue() );
		pdu.getAntennaLocation().setRelativeAntennaLocation( relativePosition.getDisValue() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
