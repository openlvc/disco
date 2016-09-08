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
package org.openlvc.distributor.links.dis;

import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class DisLink extends LinkBase implements ILink, IPduListener
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
	public DisLink( LinkConfiguration linkConfiguration )
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
		logger.debug( "Link Mode: DIS" );
		
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
	public void receiver( PDU pdu )
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
			return opsCenter.getMetrics().getSummaryString();
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
			return String.format( "{ DIS, address:%s, port:%d, nic:%s }",
			                      opsCenter.getConfiguration().getUdpConfiguration().getAddress(),
			                      opsCenter.getConfiguration().getUdpConfiguration().getPort(),
			                      opsCenter.getConfiguration().getUdpConfiguration().getNetworkInterface().getDisplayName() );
		}
		else
		{
			// return raw configuration information
			return String.format( "{ DIS, address:%s, port:%d, nic:%s }",
			                      linkConfiguration.getDisAddress(),
			                      linkConfiguration.getDisPort(),
			                      linkConfiguration.getDisNic() );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private DiscoConfiguration turnIntoDiscoConfiguration( LinkConfiguration link )
	{
		DiscoConfiguration disco = new DiscoConfiguration();
		disco.getUdpConfiguration().setAddress( link.getDisAddress() );
		disco.getUdpConfiguration().setPort( link.getDisPort() );
		disco.getUdpConfiguration().setNetworkInterface( link.getDisNic() );

		disco.getDisConfiguration().setExerciseId( link.getDisExerciseId() );

		disco.getLoggingConfiguration().setAppName( link.getName() );
		disco.getLoggingConfiguration().setLevel( link.getDisLogLevel() );
		disco.getLoggingConfiguration().setFile( link.getDisLogFile() );
		disco.getLoggingConfiguration().setFileOn( link.getDisLogToFile() );
		return disco;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
