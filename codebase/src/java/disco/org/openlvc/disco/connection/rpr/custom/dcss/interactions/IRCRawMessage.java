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
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.custom.IrcRawMessagePdu;

import hla.rti1516e.encoding.HLAASCIIstring;

/**
 * Wrapper class for an IRC command message that isn't part of general chat.
 */
public class IRCRawMessage extends InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAASCIIstring prefix;
	private HLAASCIIstring command;
	private HLAASCIIstring commandParameters;
	private RPRunsignedInteger64BE timeReceived;
	private HLAASCIIstring sender;
	private HLAASCIIstring origin;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public IRCRawMessage()
	{
		super();
		
		this.prefix       = EncoderFactory.createHLAASCIIstring();
		this.command      = EncoderFactory.createHLAASCIIstring();
		this.commandParameters = EncoderFactory.createHLAASCIIstring();
		this.timeReceived = new RPRunsignedInteger64BE();
		this.sender       = EncoderFactory.createHLAASCIIstring();
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
		IrcRawMessagePdu pdu = incoming.as( IrcRawMessagePdu.class );

		// Prefix
		prefix.setValue( pdu.getPrefix() );
		
		// Command
		command.setValue( pdu.getCommand() );
		
		// CommandParameters
		commandParameters.setValue( pdu.getCommandParameters() );
		
		// TimeReceived
		timeReceived.setValue( pdu.getTimeReceived() );

		// Sender
		sender.setValue( pdu.getSender() );

		// Origin
		origin.setValue( pdu.getOrigin() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Encoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PDU toPdu()
	{
		IrcRawMessagePdu pdu = new IrcRawMessagePdu();
		
		// Prefix
		pdu.setPrefix( prefix.getValue() );
		
		// Command
		pdu.setCommand( command.getValue() );
		
		// CommandParameters
		pdu.setCommandParameters( commandParameters.getValue() );
		
		// TimeReceived
		pdu.setTimeReceived( timeReceived.getValue() );
		
		// Sender
		pdu.setSender( sender.toString() );

		// Origin
		pdu.setOrigin( origin.getValue() );
		
		return pdu;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAASCIIstring getPrefix()
	{
		return prefix;
	}

	public HLAASCIIstring getCommand()
	{
		return command;
	}

	public HLAASCIIstring getCommandParameters()
	{
		return commandParameters;
	}

	public RPRunsignedInteger64BE getTimeReceived()
	{
		return timeReceived;
	}

	public HLAASCIIstring getSender()
	{
		return sender;
	}

	public HLAASCIIstring getOrigin()
	{
		return origin;
	}	

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
