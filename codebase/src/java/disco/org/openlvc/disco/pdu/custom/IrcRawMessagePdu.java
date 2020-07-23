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
	private String sender;
	private String origin;

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
		this.sender = "Unknown";
		this.origin = "Unknown";
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
		this.sender = dis.readUTF();
		this.origin = dis.readUTF();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUTF( prefix );
		dos.writeUTF( command );
		dos.writeUTF( commandParameters );
		dos.writeUI64( timeReceived );
		dos.writeUTF( sender );
		dos.writeUTF( origin );
	}
	
	@Override
	public final int getContentLength()
	{
		return prefix.length() +
		       command.length() +
		       commandParameters.length() +
		       // 8   -- timeReceived
		       sender.length() +
		       origin.length() +
		       18;     // sum of extra 2 bytes on length of each UTF string + timeReceived
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filtering Support Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getSiteId()
	{
		return 0;
	}
	
	public int getAppId()
	{
		return 0;
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
	
	public String getSender()
	{
		return this.sender;
	}
	
	public void setSender( String sender )
	{
		if( sender != null )
			this.sender = sender;
	}

	public String getOrigin()
	{
		return this.origin;
	}

	public void setOrigin( String origin )
	{
		if( origin != null )
			this.origin = origin;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
