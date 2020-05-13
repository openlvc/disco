/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.distributor.links.hla;

import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class HlaLink extends LinkBase implements ILink, IPduListener
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private OpsCenter opsCenter;
	private Reflector reflector;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HlaLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		// Loaded when link is brought up
		this.opsCenter = null;
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
		logger.debug( "Link Mode: HLA" );
		logger.debug( "Inbound Filtering:  "+super.getReceiveFilterDesc() );
		logger.debug( "Outbound Filtering: "+super.getSendFilterDesc() );
		
		// Create the Disco configuration from our link configuration
		this.opsCenter = new OpsCenter( turnIntoDiscoConfiguration(linkConfiguration) );
		this.opsCenter.setListener( this );
		this.opsCenter.open();

		logger.debug( "Link is up" );
		super.linkUp = true;
	}
	
	public void down()
	{
		if( isDown() )
			return;

		logger.debug( "Taking down link: "+super.getName() );
		
		this.opsCenter.close();
		logger.debug( "Link is down" );

		super.linkUp = false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Reception Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void receive( PDU pdu )
	{
		try
		{
			reflector.reflect( new Message(this,pdu) );
		}
		catch( InterruptedException ie )
		{
			logger.warn( "PDU dropped, interrupted while offering to reflector: "+ie.getMessage() );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void reflect( Message message )
	{
		try
		{
			this.opsCenter.sendRaw( message.getPdu() );
		}
		catch( Exception e )
		{
			logger.error( "Problem sending PDU, dropped. Reason: "+e.getMessage() );
			logger.trace( e.getMessage(), e );
		}
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
		// We don't check for up/down status. If the OpsCenter has even been initialized
		// we want to get info from it so that when we bring the link down, we can see the
		// last metrics values that were present
		if( opsCenter != null )
		{
			String metrics = opsCenter.getMetrics().getSummaryString();
			// put out special marker on the front so that if fits with all the
			// other distributor summary strings
			return metrics.replaceFirst( "\\{ ", "\\{ HLA, " );
		}
		else
		{
			return getConfigSummary();
		}
	}
	
	public String getConfigSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			// return live, resolved connection information
			return String.format( "{ HLA, federation:%s, federate:%s, rti:%s }",
			                      opsCenter.getConfiguration().getRprConfiguration().getFederationName(),
			                      opsCenter.getConfiguration().getRprConfiguration().getFederateName(),
			                      opsCenter.getConfiguration().getRprConfiguration().getRtiProvider().name() );
		}
		else
		{
			// return raw configuration information
			return String.format( "{ HLA, federation:%s, federate:%s, rti:%s }",
			                      linkConfiguration.getHlaFederationName(),
			                      linkConfiguration.getHlaFederateName(),
			                      linkConfiguration.getHlaRtiProvider().name() );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private DiscoConfiguration turnIntoDiscoConfiguration( LinkConfiguration link )
	{
		DiscoConfiguration disco = new DiscoConfiguration();
		disco.setConnection( "rpr" );
		disco.getRprConfiguration().setRtiProvider( link.getHlaRtiProvider() );
		disco.getRprConfiguration().setRtiInstallDir( link.getHlaRtiInstallDir() );
		disco.getRprConfiguration().setLocalSettings( link.getHlaRtiLocalSettings() );
		
		disco.getRprConfiguration().setFederationName( link.getHlaFederationName() );
		disco.getRprConfiguration().setFederateName( link.getHlaFederateName() );
		disco.getRprConfiguration().setCreateFederation( true );
		
		disco.getLoggingConfiguration().setAppName( link.getName() );
		disco.getLoggingConfiguration().setLevel( link.getHlaLogLevel() );
		disco.getLoggingConfiguration().setFile( link.getHlaLogFile() );
		disco.getLoggingConfiguration().setFileOn( link.getHlaLogToFile() );
		return disco;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
