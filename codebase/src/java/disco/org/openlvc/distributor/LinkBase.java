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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.distributor.configuration.LinkConfiguration;

public abstract class LinkBase
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected LinkConfiguration linkConfiguration;
	protected Logger logger;
	protected boolean linkUp;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LinkBase( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
		this.logger = LogManager.getFormatterLogger( "distributor."+linkConfiguration.getName() );
		this.linkUp = false;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public abstract void up();
	public abstract void down();
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return linkConfiguration.getName();
	}
	
	public LinkConfiguration getConfiguration()
	{
		return this.linkConfiguration;
	}
	
	public boolean isUp()
	{
		return this.linkUp;
	}
	
	public boolean isDown()
	{
		return this.linkUp == false;
	}
	
	public String getLinkStatus()
	{
		return this.linkUp ? "up" : "down";
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
