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
package org.openlvc.distributor.links.wan;

import java.io.IOException;

import org.openlvc.distributor.configuration.LinkConfiguration;

public class UdpTransport //implements ITransport
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
	protected UdpTransport( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		
	}
	
	public void down()
	{
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( byte[] message ) throws IOException
	{
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
