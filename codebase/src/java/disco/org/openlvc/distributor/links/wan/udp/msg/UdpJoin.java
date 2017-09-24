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
import org.openlvc.disco.utils.SerializationUtils;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class UdpJoin extends UdpMessage
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
	public UdpJoin()
	{
		this.linkConfiguration = null;
	}

	public UdpJoin( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public MessageType getType()
	{
		return MessageType.Join;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Serialization Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void writeTo( DisOutputStream stream ) throws IOException
	{
		// convert the configuration into a byte[]
		byte[] bytes = SerializationUtils.objectToBytes( linkConfiguration );
		if( bytes.length > UdpMessage.MAX_PAYLOAD_SIZE )
			throw new DiscoException( "Configuration too large for datagram (%d bytes)", bytes.length ); 

		// write the contents
		stream.writeShort( bytes.length );
		stream.write( bytes, 0, bytes.length );
	}

	@Override
	public void loadFrom( DisInputStream stream ) throws DiscoException, IOException
	{
		// read in the size
		short payloadSize = stream.readShort();
		
		// read in the configuration payload
		byte[] bytes = new byte[payloadSize];
		stream.read( bytes, 0, payloadSize );
		
		// turn the payload into a link configuration
		this.linkConfiguration = SerializationUtils.bytesToObject( bytes,
		                                                           0,
		                                                           payloadSize,
		                                                           LinkConfiguration.class );
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
	
	public String getSiteName()
	{
		return this.linkConfiguration == null ? "<auto>" : linkConfiguration.getWanSiteName();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
