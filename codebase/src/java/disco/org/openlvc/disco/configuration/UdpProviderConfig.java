/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.configuration;

import java.net.InetAddress;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.NetworkUtils;

public class UdpProviderConfig
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Keys for properties file
	private static final String PROP_ADDRESS = "disco.provider.udp.address";
	private static final String PROP_PORT = "disco.provider.udp.port";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InetAddress address;
	private int port;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpProviderConfig()
	{
		this.address = NetworkUtils.getByName( System.getProperty(PROP_ADDRESS,"127.0.0.1") );
		this.port = Integer.parseInt( System.getProperty(PROP_PORT,"3000") );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public InetAddress getAddress()
	{
		return this.address;
	}
	
	public void setAddress( InetAddress address )
	{
		this.address = address;
	}

	/**
	 * This can be a domain name or an actual IP address, or alternatively it can be one
	 * of the special symbols accepted by {@link NetworkUtils#getByName(String)}.
	 */
	public void setAddress( String address ) throws DiscoException
	{
		this.address = NetworkUtils.getByName( address );
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public void setPort( int port )
	{
		this.port = port;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
