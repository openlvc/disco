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
import java.util.LinkedHashSet;
import java.util.Set;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.utils.StringUtils;

public class IrcUserPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Max PDU length less field values
	public static final int MAX_MESSAGE_LENGTH = DisSizes.PDU_MAX_SIZE-6-32-32-8;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId userId;
	private String userNick;
	private String server;
	private Set<String> channels;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IrcUserPdu()
	{
		super( PduType.IRCUser );
		
		this.userId = new EntityId();
		this.userNick = "Unknown";
		this.server = "Unknown";
		this.channels = new LinkedHashSet<>();
	}
	
	public IrcUserPdu( EntityId id, String userNick, String server, String... channels )
	{
		this();
		this.userId = id;
		this.userNick = userNick;
		this.server = server;
		for( String name : channels )
			this.channels.add( name );
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
		this.userId.from( dis );
		this.userNick = dis.readVariableString256();
		this.server = dis.readVariableString256();
		byte channelCount = dis.readByte();
		for( int i = 0; i < channelCount; i++ )
			this.channels.add( dis.readVariableString256() );
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.userId.to( dos );
		dos.writeVariableStringMax256( userNick ); // truncated to 32 on set
		dos.writeVariableStringMax256( server );   // truncated to 32 on set
		dos.writeByte( channels.size() );
		for( String channel : this.channels )
			dos.writeVariableStringMax256( channel );
	}
	
	@Override
	public final int getContentLength()
	{
		int size = userId.getByteLength();
		size += userNick.length()+1;
		size += server.length()+1;
		size += 1; // Channel name array length
		for( String channel : this.channels )
			size += Math.min( channel.length(), 256 )+1;
		
		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filtering Support Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getSiteId()
	{
		return userId.getSiteId();
	}
	
	public int getAppId()
	{
		return userId.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getId()
	{
		return userId;
	}

	public void setId( EntityId userId )
	{
		if( userId != null )
			this.userId = userId;
	}

	public String getNick()
	{
		return userNick;
	}

	public void setNick( String userNick )
	{
		if( userNick != null )
			this.userNick = StringUtils.truncate( userNick, 32 );
	}

	public String getServer()
	{
		return server;
	}

	public void setServer( String server )
	{
		if( server != null )
			this.server = StringUtils.truncate( server, 32 );
	}
	
	public Set<String> getChannels()
	{
		return this.channels;
	}
	
	public void joinChannel( String channel )
	{
		this.channels.add( channel );
	}
	
	public void leaveChannel( String channel )
	{
		this.channels.remove( channel );
	}
	
	public boolean isInChannel( String channel )
	{
		return this.channels.contains( channel );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
