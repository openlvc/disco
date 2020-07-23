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
package org.openlvc.disco.connection.rpr.objects.custom;

import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.connection.rpr.types.EncoderFactory;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE;
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
	private HLAASCIIstring channelName;
	private HLAASCIIstring sender;
	private HLAASCIIstring message;
	private HLAfloat64BE   timeReceived;
	private HLAASCIIstring origin;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IRCChannelMessage()
	{
		super();
		
		this.channelName  = EncoderFactory.createHLAASCIIstring();
		this.sender       = EncoderFactory.createHLAASCIIstring();
		this.message      = EncoderFactory.createHLAASCIIstring();
		this.timeReceived = new HLAfloat64BE(); // shouldn't this be long?
		this.origin       = EncoderFactory.createHLAASCIIstring();
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

		// ChannelName
		channelName.setValue( pdu.getChannelName() );
		
		// Sender
		sender.setValue( pdu.getSender() );
		
		// Message
		message.setValue( pdu.getMessage() );
		
		// TimeReceived
		timeReceived.setValue( pdu.getTimeReceived() );
		
		// Origin
		origin.setValue( pdu.getOrigin() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		IrcMessagePdu pdu = new IrcMessagePdu();
		
		// ChannelName
		pdu.setChannelName( channelName.getValue() );
		
		// Sender
		pdu.setSender( sender.getValue() );
		
		// Message
		pdu.setMessage( message.getValue() );
		
		// TimeReceived
		pdu.setTimeReceived( (float)timeReceived.getValue() );
		
		// Origin
		pdu.setOrigin( origin.getValue() );
		
		return pdu;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAASCIIstring getChannelName()
	{
		return channelName;
	}

	public HLAASCIIstring getSender()
	{
		return sender;
	}

	public HLAASCIIstring getMessage()
	{
		return message;
	}

	public HLAfloat64BE getTimeReceived()
	{
		return timeReceived;
	}

	public HLAASCIIstring getOrigin()
	{
		return origin;
	}
	

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
