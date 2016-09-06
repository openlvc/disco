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
package org.openlvc.distributor;

import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.dis.DisLink;
import org.openlvc.distributor.links.relay.RelayLink;

public class LinkFactory
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static ILink createLink( LinkConfiguration linkConfiguration )
	{
		switch( linkConfiguration.getMode() )
		{
			case DIS:
				return new DisLink( linkConfiguration );
			case RELAY:
				return new RelayLink( linkConfiguration );
			default:
				throw new IllegalArgumentException( "Unknown mode: "+linkConfiguration.getMode() );
		}
	}
}
