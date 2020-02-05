/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.disco.utils;

/**
 * Class to clearly encapsulate the set of various socket options we might want to apply
 * to a socket.
 */
public class SocketOptions
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public int recvBufferSize  = (int)ByteUnit.MEGABYTES.toBytes( 4 );
	public int sendBufferSize  = (int)ByteUnit.MEGABYTES.toBytes( 4 );
	public int timeToLive      = 254;
	public int trafficClass    = 0;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setSendBufferSize( int value )
	{
		this.sendBufferSize = value;
	}
	
	public void setSendBufferSize( int value, ByteUnit unit )
	{
		this.sendBufferSize = (int)unit.toBytes( value );
	}
	
	public int getSendBufferSize()
	{
		return this.sendBufferSize;
	}
	
	public void setRecvBufferSize( int value )
	{
		this.recvBufferSize = value;
	}
	
	public void setRecvBufferSize( int value, ByteUnit unit )
	{
		this.recvBufferSize = (int)unit.toBytes( value );
	}
	
	public int getRecvBufferSize()
	{
		return this.recvBufferSize;
	}
	
	public int getTimeToLive()
	{
		return this.timeToLive;
	}
	
	public void setTimeToLive( int ttl )
	{
		this.timeToLive = ttl;
	}
	
	public int getTrafficClass()
	{
		return this.trafficClass;
	}
	
	public void setTrafficClass( int clasz )
	{
		this.trafficClass = clasz;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
