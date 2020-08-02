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

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.IRCRoomArray;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.types.EncoderFactory;
import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.IrcUserPdu;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.encoding.HLAASCIIstring;

public class IRCUser extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityIdentifierStruct userId;
	private HLAASCIIstring userNick;
	private IRCRoomArray rooms;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IRCUser()
	{
		this.userId = new EntityIdentifierStruct();
		this.userNick = EncoderFactory.createHLAASCIIstring();
		this.rooms = new IRCRoomArray();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	protected boolean checkReady()
	{
		return this.userId.isDecodeCalled();
		//return !this.userId.isDefaults();
	}
	
	@Override
	public EntityId getDisId()
	{
		return this.userId.getDisValue();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		IrcUserPdu pdu = incoming.as( IrcUserPdu.class );

		// UserId
		this.userId.setValue( pdu.getId() );
		
		// UserNick
		this.userNick.setValue( pdu.getNick() );
		
		// Rooms
		this.rooms = new IRCRoomArray();
		for( String room : pdu.getChannels() )
			this.rooms.addElement( EncoderFactory.createHLAASCIIstring(room) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		IrcUserPdu pdu = new IrcUserPdu();

		// UserId
		pdu.setId( userId.getDisValue() );
		
		// UserNick
		pdu.setNick( userNick.getValue() );
		
		// Rooms
		for( HLAASCIIstring room : this.rooms )
			pdu.getChannels().add( room.getValue() );
		
		return pdu;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityIdentifierStruct getUserId()
	{
		return this.userId;
	}
	
	public HLAASCIIstring getUserNick()
	{
		return this.userNick;
	}
	
	public IRCRoomArray getRooms()
	{
		return this.rooms;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
