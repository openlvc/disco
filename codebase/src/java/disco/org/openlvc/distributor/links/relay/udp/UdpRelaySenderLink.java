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
package org.openlvc.distributor.links.relay.udp;

import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class UdpRelaySenderLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector exchange;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpRelaySenderLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		this.exchange = null;          // set in setReflector()

	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void up()
	{
	}

	public void down()
	{
		
	}
	
	public void setReflector( Reflector exchange )
	{
		this.exchange = exchange;
	}

	public void reflect( Message message )
	{
		
	}

	///////////////////////////////////////////////////////////////////////
	public String getStatusSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			// TODO Replace with metrics
			return getConfigSummary();
		}
		else
		{
			return getConfigSummary();
		}
	}
	
	public String getConfigSummary()
	{
		return "NFI";
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
