/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.distributor.links.wan.udp.msg;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.StringUtils;

/**
 * Represents specific message types that are passed back and forward over the UDP link.
 * Each type has a header that is embedded in the packets. See {@link UdpMessage} for full
 * details on how packets are constructed.
 */
public enum MessageType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	// Lifecycle Management
	Join           ( 0xaa01, UdpJoin.class ),
	Configure      ( 0xaa02, UdpConfigure.class ),
	KeepAlive      ( 0xaa03, UdpKeepAlive.class ),
	Goodbye        ( 0xaaff, UdpGoodbye.class ),

	// Message Exchange
	Bundle         ( 0xbbbb, UdpBundle.class ),

	// Response Messages
	OkResponse     ( 0xdd01, UdpResponse.class ),
	ErrorResponse  ( 0xdd02, UdpResponse.class );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;
	private Class<? extends UdpMessage> clazz;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private MessageType( int value, Class<? extends UdpMessage> clazz )
	{
		this.value = value;
		this.clazz = clazz;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int getValue()
	{
		return this.value;
	}
	
	public UdpMessage newInstance() throws DiscoException
	{
		try
		{
			return clazz.newInstance();
		}
		catch( Exception e )
		{
			throw new DiscoException( e.getMessage(), e );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Returns the {@link MessageType} represented by the given value.
	 * An exception is thrown if the value is not known.
	 */
	public static final MessageType getType( int value ) throws DiscoException
	{
		// done in order of which are the most common so that the short-circuit happens faster
		if( value == Bundle.value )
			return Bundle;
		else if( value == KeepAlive.value )
			return KeepAlive;
		else if( value == OkResponse.value )
			return OkResponse;
		else if( value == ErrorResponse.value )
			return MessageType.ErrorResponse;
		else if( value == Join.value )
			return MessageType.Join;
		else if( value == Configure.value )
			return MessageType.Configure;
		else if( value == Goodbye.value )
			return MessageType.Goodbye;
		
		// Nope. NFI what it is
		throw new DiscoException( "Unknown UDP message type (id=%h)"+StringUtils.toHex(value) );
	}
}
