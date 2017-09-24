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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.utils.NetworkUtils;
import org.openlvc.disco.utils.StringUtils;
import org.openlvc.disco.utils.ThreadUtils;
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
	protected List<ILink> links;
	
	private Reflector reflector;
	private StatusLogger statusLogThread;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Distributor( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = this.configuration.getApplicationLogger();
		this.links = new CopyOnWriteArrayList<ILink>();
		for( LinkConfiguration linkConfiguration : configuration.getLinks().values() )
		{
			ILink link = LinkFactory.createLink( linkConfiguration );
			this.links.add( link );
		}

		this.reflector = new Reflector( this );
		this.statusLogThread = null; // set in up()
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

		for( ILink link : links )
			bringUp( link );
		
		logger.info( "" );
		
		// 4. Bring up the status logger if configured
		if( configuration.isStatusLoggingEnabled() )
		{
			this.statusLogThread = new StatusLogger();
			this.statusLogThread.start();
		}
		
	}

	/**
	 * This is called in a couple of spots. Put here so it is in one location.
	 * 
	 * @return <code>true</code> if connection came up successfully, <code>false</code> otherwise
	 */
	public boolean bringUp( ILink link )
	{
		// 2. Bring the links down
		try
		{
			if( link.isUp() == false )
			{
				link.setReflector( reflector );
				link.up();
			}
			
			logger.info( getConfigSummary(link) );
			return true;
		}
		catch( Exception e )
		{
			logger.error( "  %-8s [ err] { Exception: %s }",
			              StringUtils.max(link.getName(),8), e.getMessage() );
			logger.debug( e.getMessage(), e );
			return false;
		}
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

		// 0. Bring the status logger down
		if( this.statusLogThread != null )
		{
			this.statusLogThread.interrupt();
			ThreadUtils.exceptionlessThreadJoin( statusLogThread, 500 );
		}

		// 1. Bring down the reflector first
		logger.info( "Brining reflector down" );
		reflector.down();
		
		// 2. Bring down all the separate links
		//    Some may be removed because they are transient - can't iterate on
		//    collection direct without concurrent mod, so make a copy
		logger.info( "Brining all links down:" );
		List<ILink> tempList = new ArrayList<>( links );
		for( ILink link : tempList )
			takeDown( link );
	}

	/**
	 * Takes a link down and prints summary information. Will remove the link from our store if
	 * it has been marked as transient.
	 */
	public void takeDown( ILink link )
	{
		try
		{
			// bring the link down if it is running
			if( link.isUp() )
				link.down();

			// print some summary stats
			logger.info( getStatusSummary(link) );
			
			// if transient, dump it
			if( link.isTransient() )
			{
				links.remove( link );
				logger.debug( "Removed transient link "+link.getName() );
			}
		}
		catch( Exception e )
		{
			logger.error( "  %-8s [err ] { Exception: %s }",
			              StringUtils.max(link.getName(),8), e.getMessage() );
			logger.debug( e.getMessage(), e );
		}		
	}

	/** @return true if any contained links are up */
	public boolean areAnyLinksUp()
	{
		return links.stream().anyMatch( link -> link.isUp() );
	}
	
	/** @return true if any contained links are down */
	public boolean areAnyLinksDown()
	{
		return links.stream().anyMatch( link -> link.isDown() );
	}

	/**
	 * Returns true if there is a link with the given name contained in the distributor, false otherwise.
	 */
	public boolean containsLinkWithName( String name )
	{
		return links.stream().anyMatch( link -> link.getName().equals(name) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean addAndBringUp( ILink link )
	{
		links.add( link );
		return bringUp( link );
	}

	public Logger getLogger()
	{
		return this.logger;
	}

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

		// Log some NIC information
		NetworkUtils.logNetworkInterfaceInformation( logger );

		// Log information about each of the links we have
		logger.info( links.size()+" links configured: "+links );
		for( ILink link : links )
			logger.info( getConfigSummary(link) );

		logger.info( "" );
	}

	private String getConfigSummary( ILink link )
	{
		return String.format( "  %-8s [%4s] %s",
		                      StringUtils.max(link.getName(),8),
		                      link.getLinkStatus(),
		                      link.getConfigSummary() );
	}
	
	private String getStatusSummary( ILink link )
	{
		return String.format( "  %-8s [%4s] %s",
		                      StringUtils.max(link.getName(),8),
		                      link.getLinkStatus(),
		                      link.getStatusSummary() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Status Summary Logger   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class StatusLogger extends Thread
	{
		private StatusLogger()
		{
			super( "tick" );
			super.setDaemon( true );
		}
		
		public void run()
		{
			while( !Thread.interrupted() )
			{
				try
				{
					// Sleep first
					// We sleep for whatever the configured interval time is, or 10s.
					// If the configured time is 0, we sleep for 10s and then don't print.
					// That way, if it changes, we are already running.
					
					// sleep first
					int sleepTime = configuration.getStatusLogInterval();
					sleepTime = sleepTime == 0 ? 10 : sleepTime;
					Thread.sleep( sleepTime * 1000 );

					if( configuration.isStatusLoggingEnabled() == false )
						continue;
					
					// print link status -- links could change, so take a copy to avoid CME
					List<ILink> temp = new ArrayList<ILink>( links );
					long linksUp = temp.stream().filter(link -> link.isUp()).count();
					logger.info( ">>> links status: %d up, %d down", linksUp, temp.size()-linksUp );
					for( ILink link : temp )
						logger.info( getStatusSummary(link) );
				}
				catch( InterruptedException ie )
				{
					
				}
			}
		}
	}


}
