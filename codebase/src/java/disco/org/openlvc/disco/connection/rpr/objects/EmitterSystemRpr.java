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

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import org.openlvc.disco.connection.rpr.types.enumerated.RawEnumValue16;
import org.openlvc.disco.connection.rpr.types.enumerated.RawEnumValue8;
import org.openlvc.disco.connection.rpr.types.fixed.EventIdentifierStruct;
import org.openlvc.disco.pdu.PDU;

public class EmitterSystemRpr extends EmbeddedSystem
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RawEnumValue8  emitterFunctionCode;    // EmitterFunctionEnum8
	private RawEnumValue16 emitterType;            // EmitterTypeEnum16
	private HLAoctet       emitterIndex;
	private EventIdentifierStruct eventIdentifier;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EmitterSystemRpr()
	{
		super();
		
		this.emitterFunctionCode = new RawEnumValue8();
		this.emitterType = new RawEnumValue16();
		this.emitterIndex = new HLAoctet();
		this.eventIdentifier = new EventIdentifierStruct();
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
		throw new DiscoException( "EmitterSystems are not deserialized directly from PDUs" );
	}

	@Override
	public PDU toPdu()
	{
		throw new DiscoException( "EmitterSystems are not serialized directly to PDUs" );
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RawEnumValue8 getEmitterFunctionCode()
	{
		return emitterFunctionCode;
	}

	public RawEnumValue16 getEmitterType()
	{
		return emitterType;
	}

	public HLAoctet getEmitterIndex()
	{
		return emitterIndex;
	}

	public EventIdentifierStruct getEventIdentifier()
	{
		return eventIdentifier;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
