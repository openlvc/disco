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
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;

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
		this.hostObjectIdentifier.setValue( incoming.getEntityId().toString() );
		this.relativePosition.setValue( incoming.getAntennaLocation().getRelativeAntennaLocation() );
	}
	
	protected void toPdu( TransmitterPdu pdu )
	{
		pdu.setEntityId( entityIdentifier.getDisValue() );
		pdu.getAntennaLocation().setRelativeAntennaLocation( relativePosition.getDisValue() );
	}
	
	//
	// Emitter Systems
	//
	protected void fromPdu( EmissionPdu incoming )
	{
		this.entityIdentifier.setValue( incoming.getEmittingEntityId() );
		this.hostObjectIdentifier.setValue( incoming.getEmittingEntityId().toString() );
//		this.relativePosition.setValue( incoming.getEmitterSystems().get(0).get );
		
		throw new RuntimeException( "Not Yet Supported" );
	}
	
	protected void toPdu( EmissionPdu pdu )
	{
		throw new RuntimeException( "Not Yet Supported" );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
