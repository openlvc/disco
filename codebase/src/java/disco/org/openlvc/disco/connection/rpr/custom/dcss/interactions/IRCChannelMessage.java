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
package org.openlvc.disco.connection.rpr.custom.dcss.interactions;

import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.types.EncoderFactory;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger64BE;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.IrcMessagePdu;

import hla.rti1516e.encoding.HLAASCIIstring;

/**
 * Simple IRC message wrapper interaction. Should look at making this a proper "Signal" interaction,
 * but for now the simple approach will work.
 */
public class IRCChannelMessage extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAASCIIstring roomName;
	private EntityIdentifierStruct senderId;
	private HLAASCIIstring senderNick;
	private HLAASCIIstring message;
	private RPRunsignedInteger64BE timeReceived;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IRCChannelMessage()
	{
		super();
		
		this.roomName     = EncoderFactory.createHLAASCIIstring();
		this.senderId     = new EntityIdentifierStruct();
		this.senderNick   = EncoderFactory.createHLAASCIIstring();
		this.message      = EncoderFactory.createHLAASCIIstring();
		this.timeReceived = new RPRunsignedInteger64BE();
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
		IrcMessagePdu pdu = incoming.as( IrcMessagePdu.class );

		// RoomName
		roomName.setValue( pdu.getRoomName() );
		
		// SenderId
		senderId.setValue( pdu.getSenderId() );
		
		// SenderNick
		senderNick.setValue( pdu.getSenderNick() );
		
		// Message
		message.setValue( pdu.getMessage() );
		
		// TimeReceived
		//timeReceived.setValue( pdu.getTimeReceived() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		IrcMessagePdu pdu = new IrcMessagePdu();
		
		// RoomName
		pdu.setRoomName( roomName.getValue() );
		
		// SenderId
		pdu.setSenderId( senderId.getDisValue() );
		
		// SenderNick
		pdu.setSenderNick( senderNick.getValue() );
		
		// Message
		pdu.setMessage( message.getValue() );
		
		// TimeReceived
		//pdu.setTimeReceived( timeReceived.getValue() );
		
		return pdu;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAASCIIstring getRoomName()
	{
		return roomName;
	}

	public EntityIdentifierStruct getSenderId()
	{
		return senderId;
	}

	public HLAASCIIstring getSenderNick()
	{
		return senderNick;
	}

	public HLAASCIIstring getMessage()
	{
		return message;
	}

	public RPRunsignedInteger64BE getTimeReceived()
	{
		return timeReceived;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
