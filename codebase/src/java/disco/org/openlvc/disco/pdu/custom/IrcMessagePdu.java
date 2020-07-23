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
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

public class IrcMessagePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String channelName;
	private String sender;
	private String message;
	private float  timeReceived;
	private String origin;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IrcMessagePdu()
	{
		super( PduType.IRCMessage );
		
		this.channelName = "Unknown";
		this.sender = "Unknown";
		this.message = "";
		this.timeReceived = 0;
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
		this.channelName = dis.readUTF();
		this.sender = dis.readUTF();
		this.message = dis.readUTF();
		this.timeReceived = dis.readFloat();
		this.origin = dis.readUTF();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUTF( channelName );
		dos.writeUTF( sender );
		dos.writeUTF( message );
		dos.writeFloat( timeReceived );
		dos.writeUTF( origin );
	}
	
	@Override
	public final int getContentLength()
	{
		return channelName.length() +
		       sender.length() +
		       message.length() +
		       // 4   -- timeReceived
		       origin.length() +
		       8;     // sum of extra 2 bytes on length of each UTF string
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
	public String getSender()
	{
		return this.sender;
	}
	
	public void setSender( String sender )
	{
		if( sender != null )
			this.sender = sender;
	}
	
	public String getChannelName()
	{
		return this.channelName;
	}
	
	public void setChannelName( String channelName )
	{
		if( channelName != null )
			this.channelName = channelName;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setMessage( String message )
	{
		if( message != null )
			this.message = message;
	}
	
	public float getTimeReceived()
	{
		return this.timeReceived;
	}
	
	public void setTimeReceived( float timeReceived )
	{
		this.timeReceived = timeReceived;
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
