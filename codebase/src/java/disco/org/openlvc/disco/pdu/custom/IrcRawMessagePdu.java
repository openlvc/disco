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
import java.math.BigInteger;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;

public class IrcRawMessagePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String prefix;
	private String command;
	private String commandParameters;
	private BigInteger timeReceived;
	private EntityId senderId;
	private String senderNick;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IrcRawMessagePdu()
	{
		super( PduType.IRCMessage );
		
		this.prefix = "";
		this.command = "";
		this.commandParameters = "";
		this.timeReceived = BigInteger.ZERO;
		this.senderId = new EntityId();
		this.senderNick = "Unknown";
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
		this.prefix = dis.readUTF();
		this.command = dis.readUTF();
		this.commandParameters = dis.readUTF();
		this.timeReceived = dis.readUI64();
		this.senderId.from( dis );
		this.senderNick = dis.readUTF();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeVariableStringMax256( prefix );
		dos.writeVariableStringMax256( command );
		dos.writeVariableStringMax256( commandParameters );
		dos.writeUI64( timeReceived );
		this.senderId.to( dos );
		dos.writeVariableStringMax256( senderNick );
	}
	
	@Override
	public final int getContentLength()
	{
		return prefix.length() +
		       command.length() +
		       commandParameters.length() +
		       // 8   -- timeReceived
		       senderId.getByteLength() +
		       senderNick.length() +
		       4;     // sum of extra 1 byte on each string field
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
	public String getPrefix()
	{
		return this.prefix;
	}
	
	public void setPrefix( String prefix )
	{
		if( prefix != null )
			this.prefix = prefix;
	}
	
	public String getCommand()
	{
		return this.command;
	}
	
	public void setCommand( String command )
	{
		if( command != null )
			this.command = command;
	}
	
	public String getCommandParameters()
	{
		return this.commandParameters;
	}
	
	public void setCommandParameters( String commandParameters )
	{
		if( commandParameters != null )
			this.commandParameters = commandParameters;
	}
	
	public BigInteger getTimeReceived()
	{
		return this.timeReceived;
	}
	
	public long getTimeReceivedAsLong()
	{
		return this.timeReceived.longValue();
	}
	
	public void setTimeReceived( BigInteger timeReceived )
	{
		this.timeReceived = timeReceived;
	}
	
	public void setTimeReceived( long timeReceived )
	{
		this.timeReceived = BigInteger.valueOf( timeReceived );
	}
	
	public EntityId getSenderId()
	{
		return this.senderId;
	}

	public void setSenderId( EntityId id )
	{
		if( id != null )
			this.senderId = id;
	}
	
	public String getSenderNick()
	{
		return this.senderNick;
	}
	
	public void setSenderNick( String sender )
	{
		if( sender != null )
			this.senderNick = sender;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
