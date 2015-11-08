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
package org.openlvc.disco.provider;

import org.openlvc.disco.IProvider;
import org.openlvc.disco.provider.udp.UdpProvider;

public class ProviderFactory
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
	public static IProvider getProvider( String name )
	{
		name = name.trim();
		if( name.equalsIgnoreCase("network.udp") )
			return new UdpProvider();
		else if( name.equalsIgnoreCase("file") )
			throw new IllegalArgumentException( "Provider is not supported: "+name );
		else if( name.equalsIgnoreCase("network.tcp") )
			throw new IllegalArgumentException( "Provider is not supported: "+name );
		else
			throw new IllegalArgumentException( "Provider is not supported: "+name );
	}
}
