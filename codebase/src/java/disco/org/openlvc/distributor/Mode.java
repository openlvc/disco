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
 * Represents the mode that a Link connection can have:
 * <ul>
 *   <li>DIS: Standard DIS network connection</li>
 *   <li>WAN: Point-to-point connection to a relaying node</li>
 *   <li>RELAY: External hub for relaying WAN messages through</li>
 * </ul>
 */
public enum Mode
{
	DIS,
	WAN,
	RELAY,
	LOGGING;
	
	public static Mode valueOfIgnoreCase( String string )
	{
		for( Mode mode : Mode.values() )
			if( mode.name().equalsIgnoreCase(string) )
				return mode;
		
		throw new IllegalArgumentException( "Unknown Mode: "+string );
	}
}
