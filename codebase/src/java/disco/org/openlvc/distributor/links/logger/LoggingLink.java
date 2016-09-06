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
package org.openlvc.distributor.links.logger;

import org.apache.logging.log4j.Level;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class LoggingLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LoggingLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		// Loaded when link is brought up
		this.reflector = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		if( isUp() )
			return;

		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );
		
		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: Logging" );
		
		logger.debug( "Link is up" );
		super.linkUp = true;
	}
	
	public void down()
	{
		if( isDown() )
			return;

		logger.debug( "Taking down link: "+super.getName() );
		
		logger.debug( "Link is down" );

		super.linkUp = false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		logger.log( Level.INFO,
		            "PDU from [%s] >> type=%s",
		            message.getSouce().getName(),
		            message.getPdu().getType() );
	}

	public void setReflector( Reflector reflector )
	{
		this.reflector = reflector;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getStatusSummary()
	{
		return getConfigSummary();
	}
	
	public String getConfigSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			// return live, resolved connection information
			return String.format( "{ Logging, level:%s, usefile:%s, file:%s }",
			                      "TBA",
			                      "TBA",
			                      "TBA" );
		}
		else
		{
			// return raw configuration information
			return String.format( "{ Logging, level:%s, usefile:%s, file:%s }",
			                      "TBA",
			                      "TBA",
			                      "TBA" );
		}
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
