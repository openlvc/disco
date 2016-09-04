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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.distributor.configuration.Configuration;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class Distributor
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;
	private Map<String,ILink> links;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Distributor( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getApplicationLogger();
		this.links = new HashMap<>();
		for( LinkConfiguration linkConfiguration : configuration.getLinks().values() )
		{
			ILink link = LinkFactory.createLink( linkConfiguration );
			this.links.put( link.getName(), link );
		}
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		printWelcome();
		
		//long downCount = links.values().stream().filter( link -> !link.isLinkUp() ).count();
		//logger.info( "Brining "+downCount+" links up:" );
		logger.info( "" );
		logger.info( "Brining all links up:" );

		for( ILink link : links.values() )
		{
			try
			{
				if( link.isUp() == false )
					link.up();
				
				logger.info( getLinkSummary(link) );
			}
			catch( Exception e )
			{
				logger.error( "  %-8s [ err] { Exception: %s }", link.getName(), e.getMessage() );
				logger.debug( e.getMessage(), e );
			}
		}
		
	}
	
	public void down()
	{
		//long upCount = links.values().stream().filter( link -> link.isLinkUp() ).count();
		//logger.info( "Brining "+upCount+" links down:" );
		logger.info( "" );
		logger.info( "Brining all links down:" );
		
		for( ILink link : links.values() )
		{
			try
			{
				if( link.isUp() )
					link.down();
				logger.info( getLinkSummary(link) );
			}
			catch( Exception e )
			{
				logger.error( "  %-8s [err ] { Exception: %s }", link.getName(), e.getMessage() );
				logger.debug( e.getMessage(), e );
			}
		}
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void printWelcome()
	{
		logger.info("");
		logger.info( "        ___      __       _ __          __             " );            
		logger.info( "   ____/ (_)____/ /______(_) /_  __  __/ /_____  _____ " );
		logger.info( "  / __  / / ___/ __/ ___/ / __ \\/ / / / __/ __ \\/ ___/ " );
		logger.info( " / /_/ / (__  ) /_/ /  / / /_/ / /_/ / /_/ /_/ / /     " );
		logger.info( " \\__,_/_/____/\\__/_/  /_/_.___/\\__,_/\\__/\\____/_/      " );
		logger.info("");
		logger.info( "Welcome to the Distributor - DIS Bridging and Filtering" );
		logger.info( "Version "+DiscoConfiguration.getVersion() );
		logger.info("");

		logger.info( links.size()+" links configured: "+links.keySet() );
		for( ILink link : links.values() )
			logger.info( getLinkSummary(link) );

	}

	private String getLinkSummary( ILink link )
	{
		LinkConfiguration linkConfig = link.getConfiguration();
		String linkName = linkConfig.getName();
		if( linkConfig.getMode() == Mode.DIS )
		{
			return String.format( "  %-8s [%4s] { DIS, address:%s, port:%d, nic:%s }",
			                      linkName,
			                      link.getLinkStatus(),
			                      linkConfig.getDisAddress(),
			                      linkConfig.getDisPort(),
			                      linkConfig.getDisNic() );
		}
		else
		{
			return String.format( "  %-8s [%4s] { WAN, address:%s, port:%d }",
			                      linkName,
			                      link.getLinkStatus(),
			                      linkConfig.getWanAddress(),
			                      linkConfig.getWanPort() );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
