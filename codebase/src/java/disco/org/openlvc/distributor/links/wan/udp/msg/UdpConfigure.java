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

import java.io.IOException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class UdpConfigure extends UdpMessage
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private LinkConfiguration linkConfiguration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpConfigure()
	{
		this.linkConfiguration= null;
	}

	public UdpConfigure( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
		//byte[] configurationBytes = SerializationUtils.objectToBytes( linkConfiguration );

	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public MessageType getType()
	{
		return MessageType.Configure;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Serialization Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void writeTo( DisOutputStream stream ) throws IOException
	{
	}

	@Override
	public void loadFrom( DisInputStream stream ) throws DiscoException, IOException
	{
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public LinkConfiguration getLinkConfiguration()
	{
		return this.linkConfiguration;
	}
	
	public void setLinkConfiguration( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
