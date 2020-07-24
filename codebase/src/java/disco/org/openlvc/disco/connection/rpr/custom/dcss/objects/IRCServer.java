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

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.IRCChannelArray;
import org.openlvc.disco.connection.rpr.custom.dcss.types.array.IRCNickArray;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.types.EncoderFactory;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.simman.DataPdu;

import hla.rti1516e.encoding.HLAASCIIstring;

public class IRCServer extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAASCIIstring serverId;
	private IRCChannelArray channels;
	private IRCNickArray connectedNicks;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IRCServer()
	{
		this.serverId = EncoderFactory.createHLAASCIIstring();
		//this.serverId = new HLAASCIIstring( "IRC-"+StringUtils.generateRandomString(6) );
		this.channels = new IRCChannelArray();
		this.connectedNicks = new IRCNickArray();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	protected boolean checkReady()
	{
		return !this.serverId.getValue().equals("");
	}
	
	@Override
	public EntityId getDisId()
	{
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		DataPdu pdu = incoming.as( DataPdu.class );

		// ServerId
		this.serverId.setValue( pdu.toString() );
		
		// Channels
		
		
		// ConnectedNicks
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		DataPdu pdu = new DataPdu();

		// ServerId
		
		// Channels
		
		// Connected Nicks
		
		return pdu;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAASCIIstring getServerId()
	{
		return this.serverId;
	}
	
	public IRCChannelArray getChannels()
	{
		return this.channels;
	}
	
	public IRCNickArray getConnectedNicks()
	{
		return this.connectedNicks;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
