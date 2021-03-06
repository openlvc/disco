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

/**
 * Transport protocol used by a relay.
 */
public enum TransportType
{
	TCP,
	UDP;

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

	public static TransportType valueOfIgnoreCase( String string )
	{
		for( TransportType transport : TransportType.values() )
			if( transport.name().equalsIgnoreCase(string) )
				return transport;
		
		throw new IllegalArgumentException( "Unknown Transport: "+string );
	}
}
