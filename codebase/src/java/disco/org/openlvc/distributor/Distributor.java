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
import org.openlvc.distributor.configuration.SiteConfiguration;

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
	private Map<String,ISite> sites;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Distributor( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getApplicationLogger();
		this.sites = new HashMap<>();
		for( SiteConfiguration siteConfiguration : configuration.getSites().values() )
		{
			ISite site = SiteFactory.createSite( siteConfiguration );
			this.sites.put( site.getName(), site );
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
		
		//long downCount = sites.values().stream().filter( site -> !site.isLinkUp() ).count();
		//logger.info( "Brining "+downCount+" sites up:" );
		logger.info( "" );
		logger.info( "Brining all sites up:" );

		for( ISite site : sites.values() )
		{
			try
			{
				if( site.isLinkUp() == false )
					site.up();
				
				logger.info( getSiteSummary(site) );
			}
			catch( Exception e )
			{
				logger.error( "  %-8s [ err] { Exception: %s }", site.getName(), e.getMessage() );
				logger.debug( e.getMessage(), e );
			}
		}
		
	}
	
	public void down()
	{
		//long upCount = sites.values().stream().filter( site -> site.isLinkUp() ).count();
		//logger.info( "Brining "+upCount+" sites down:" );
		logger.info( "" );
		logger.info( "Brining all sites down:" );
		
		for( ISite site : sites.values() )
		{
			try
			{
				if( site.isLinkUp() )
					site.down();
				logger.info( getSiteSummary(site) );
			}
			catch( Exception e )
			{
				logger.error( "  %-8s [err ] { Exception: %s }", site.getName(), e.getMessage() );
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

		logger.info( sites.size()+" sites configured: "+sites.keySet() );
		for( ISite site : sites.values() )
			logger.info( getSiteSummary(site) );

	}

	private String getSiteSummary( ISite site )
	{
		SiteConfiguration siteConfig = site.getConfiguration();
		String siteName = siteConfig.getName();
		if( siteConfig.getMode() == Mode.DIS )
		{
			return String.format( "  %-8s [%4s] { DIS, address:%s, port:%d, nic:%s }",
			                      siteName,
			                      site.getLinkStatus(),
			                      siteConfig.getDisAddress(),
			                      siteConfig.getDisPort(),
			                      siteConfig.getDisNic() );
		}
		else
		{
			return String.format( "  %-8s [%4s] { WAN, address:%s, port:%d }",
			                      siteName,
			                      site.getLinkStatus(),
			                      siteConfig.getWanAddress(),
			                      siteConfig.getWanPort() );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
