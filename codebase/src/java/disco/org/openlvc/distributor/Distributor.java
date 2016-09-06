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
	protected Map<String,ILink> links;
	
	private Reflector reflector;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Distributor( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getApplicationLogger();
		this.links = new HashMap<String,ILink>();
		for( LinkConfiguration linkConfiguration : configuration.getLinks().values() )
		{
			ILink link = LinkFactory.createLink( linkConfiguration );
			this.links.put( link.getName(), link );
		}

		this.reflector = new Reflector( this );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Bring all links in the Distributor up. Any links that are up already are skipped. If
	 * all links are already up, this is a no-op.
	 */
	public void up()
	{
		// do we even have anything to bring up?
		if( areAnyLinksDown() == false )
			return;

		// 1. Print welcome information -- remember to introduce yo'self, fool
		printWelcome();
		
		// 2. Bring the Reflector online before we open the flood gates
		logger.info( "Starting Reflector" );
		reflector.up();
		
		// 3. Bring the links up. Brace yoursef; the PDUs are coming.
		logger.info( "Bringing all links up:" );

		for( ILink link : links.values() )
		{
			try
			{
				if( link.isUp() == false )
				{
					link.setReflector( reflector );
					link.up();
				}
				
				logger.info( getConfigSummary(link) );
			}
			catch( Exception e )
			{
				logger.error( "  %-8s [ err] { Exception: %s }", link.getName(), e.getMessage() );
				logger.debug( e.getMessage(), e );
			}
		}
		
		logger.info( "" );
		
	}

	/**
	 * Bring all links in the Distributor down. Any links that are down already are skipped. If
	 * all links are already down, this is a no-op.
	 */
	public void down()
	{
		// do we even have anything to bring up?
		if( areAnyLinksUp() == false )
			return;

		// 1. Bring down the reflector first
		logger.info( "Brining reflector down" );
		reflector.down();
		
		// 2. Bring down all the separate links
		logger.info( "Brining all links down:" );
		for( ILink link : links.values() )
		{
			try
			{
				if( link.isUp() )
					link.down();

				logger.info( getConfigSummary(link) );
			}
			catch( Exception e )
			{
				logger.error( "  %-8s [err ] { Exception: %s }", link.getName(), e.getMessage() );
				logger.debug( e.getMessage(), e );
			}
		}
		
	}

	/** @return true if any contained links are up */
	public boolean areAnyLinksUp()
	{
		return links.values().stream().anyMatch( link -> link.isUp() );
	}
	
	/** @return true if any contained links are down */
	public boolean areAnyLinksDown()
	{
		return links.values().stream().anyMatch( link -> link.isDown() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
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
			logger.info( getConfigSummary(link) );

		logger.info( "" );
	}

	private String getConfigSummary( ILink link )
	{
		return String.format( "  %-8s [%4s] %s",
		                      link.getName(),
		                      link.getLinkStatus(),
		                      link.getConfigSummary() );
	}
	
	private String getStatusSummary( ILink link )
	{
		return String.format( "  %-8s [%4s] %s",
		                      link.getName(),
		                      link.getLinkStatus(),
		                      link.getStatusSummary() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}