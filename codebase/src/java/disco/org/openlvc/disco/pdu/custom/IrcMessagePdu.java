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
package org.openlvc.disco.pdu.custom;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.utils.StringUtils;

public class IrcMessagePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Max PDU length less field values
	public static final int MAX_MESSAGE_LENGTH = DisSizes.PDU_MAX_SIZE-6-32-32-8;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId senderId;
	private String   senderNick;
	private String   roomName;
	private String   message;
	// TODO Add Timestamp

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IrcMessagePdu()
	{
		super( PduType.IRCMessage );
		
		this.senderId = new EntityId();
		this.senderNick = "Unknown";
		this.roomName   = "Unknown";
		this.message    = "Unknown";
	}

	public IrcMessagePdu( EntityId senderId, String senderNick, String room, String message )
	{
		this();
		this.senderId = senderId;
		this.senderNick = senderNick;
		this.roomName = room;
		this.message = message;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.senderId.from( dis );
		this.senderNick = dis.readVariableString256();
		this.roomName = dis.readVariableString256();
		this.message = dis.readVariableString65K(); // trimmed on the serialization side
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.senderId.to( dos );
		dos.writeVariableStringMax256( senderNick ); // truncated to 32 on set
		dos.writeVariableStringMax256( roomName );   // truncated to 32 on set
		dos.writeVariableStringMax65K( message );    // truncated to MAX_MESSAGE_LENGTH on set
	}
	
	@Override
	public final int getContentLength()
	{
		return senderId.getByteLength() +
		       senderNick.length() + 1 +
		       roomName.length() + 1 +
		       message.length() + 2;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filtering Support Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getSiteId()
	{
		return senderId.getSiteId();
	}
	
	public int getAppId()
	{
		return senderId.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getSenderId()
	{
		return senderId;
	}

	public void setSenderId( EntityId senderId )
	{
		if( senderId != null )
			this.senderId = senderId;
	}

	public String getSenderNick()
	{
		return senderNick;
	}

	public void setSenderNick( String senderNick )
	{
		if( senderNick != null )
			this.senderNick = StringUtils.truncate( senderNick, 32 );
	}

	public String getRoomName()
	{
		return roomName;
	}

	public void setRoomName( String roomName )
	{
		if( roomName != null )
			this.roomName = StringUtils.truncate( roomName, 32 );
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String message )
	{
		if( message == null )
			this.message = "";
		else
			this.message = StringUtils.truncate( message, MAX_MESSAGE_LENGTH );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
