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

import org.openlvc.distributor.configuration.SiteConfiguration;

/**
 * Represents a connection to a particular site/network.
 * 
 * Sites are purely logical constructors. Just a name used to tag a configuration set.
 * Sites represent a conncetion that the Distributor will attempt to bring online. It will
 * take input from the site and reflect it back out to all others, subject to any defined
 * filtering rules.
 */
public interface ISite
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	// Properties
	public String getName();
	public SiteConfiguration getConfiguration();
	public String getLinkStatus();
	public boolean isLinkUp();

	// Lifecycle
	public void up();      // bring the connection online
	public void down();    // close the connection

}
