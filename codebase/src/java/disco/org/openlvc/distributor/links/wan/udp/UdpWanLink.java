/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.distributor.links.wan.udp;

import java.io.IOException;

import org.openlvc.disco.connection.Metrics;
import org.openlvc.disco.utils.ThreadUtils;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.Message;
import org.openlvc.distributor.Reflector;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.links.wan.Bundler;
import org.openlvc.distributor.links.wan.udp.msg.UdpBundle;

public class UdpWanLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Reflector reflector;
	
	private UdpConnection connection;
	private Bundler bundler;
	
	// metrics gathering
	private Metrics metrics;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UdpWanLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		this.reflector     = null;   // set in setReflector() prior to call to up()

		this.connection    = null;   // set in up()
//		this.bundler       = new Bundler( this, logger );
		
		// metrics
		this.metrics       = new Metrics();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods: Bring Connection Up   ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		// Remember - Keep-Alive
		// Remember - First-Message Block
		
		if( isUp() )
			return;
		
		//
		// 1. Make sure we have everything we need
		//
		if( this.reflector == null )
			throw new RuntimeException( "Nobody has told us where the reflector is yet" );

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: WAN-UDP" );

		//
		// 2. Socket Connection
		//
		try
		{
			this.connection = new UdpConnection( linkConfiguration, logger );
			this.connection.up();
		}
		catch( Exception e )
		{
			// It will have logged itself
			if( linkConfiguration.isWanAutoReconnect() )
				scheduleReconnect();
			
			return;
		}
		
		//
		// 3. Bring the message processing infrastructure up
		//
//		this.bundler.up( outstream );
		
		// We're now good to go
		super.linkUp = true;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods: Take Connection Down   //////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void down()
	{
		if( isDown() )
			return;

		logger.debug( "Taking down link: "+super.getName() );
		
		//
		// 1. Flush the bundler
		//
//		if( bundler.isUp() )
//			bundler.down();
		
		//
		// 2. Close the connection
		//
		try
		{
			this.connection.down();
		}
		finally
		{
			this.connection = null;
		}

		logger.trace( "Link is down" );
		super.linkUp = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Processing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Submit the message to be sent down the point-to-point connection to the relay so that it
	 * can be re-sent to other connections from there. We submit everything to the {@link Bundler}
	 * for processing. If enabled, it will batch messages, otherwise it will send immediately.
	 */
	public void reflect( Message message )
	{
		try
		{
			// all sending goes via the bundler, even if bundling isn't enabled (in which
			// case it will just be flushed immediately)
			bundler.submit( message.getPdu() );
			metrics.pduSent( message.getPdu().getPduLength() );
		}
		catch( IOException ioex )
		{
			logger.error( "Failed converting/writing PDU: "+ioex.getMessage(), ioex );
		}
	}

	protected void incomingBundle( UdpBundle bundle )
	{
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void scheduleReconnect()
	{
		// Only schedule a reconnect if we are not transient
		// When a UDP link connects to a relay, the relay creates another UDP Link instance
		// that is designed to talk back on the reciprocal socket settings. So on the relay
		// end we don't want to auto-connect, as that is only something the client will do
		// when reaching out to the relay.
		//
		// These special link objects inside the relay will be marked as "transient". Don't
		// attempt anything if the link is transient.
		if( this.isTransient() )
		{
			logger.debug( "[%s] is transient, not scheduling a reconnect", getName() );
			return;
		}
		
		// Only schedule a reconnect if we are configured to
		if( linkConfiguration.isWanAutoReconnect() == false )
		{
			logger.error( "Attempted to schedule a reconnect, but reconnect not configured - ignoring" );
			return;
		}
		
		// Schedule the reconnect to happen a bit later
		Runnable reconnector = new Runnable()
		{
			public void run()
			{
				try
				{
					ThreadUtils.exceptionlessSleep(10000);
					logger.debug( "Attempting Reconnect "+getConfigSummary() );
					up();
				}
				catch( Exception e )
				{
					logger.debug( "Auto-reconnect for %s failed: %s", getName(), e.getMessage() );
				}
			}
		};
		
		new Thread(reconnector,getName()+"-reconnect").start();
	}

	protected void takeDownAndRemove()
	{
		reflector.getDistributor().takeDown( this );
	}


	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getStatusSummary()
	{
		// if link has never been up, return configuration information
		if( isUp() )
		{
			String string = metrics.getSummaryString();
			// put out special marker on the front so that if fits with all the
			// other distributor summary strings
			string = string.replaceFirst( "\\{ ", "\\{ WAN, udp/%s:%d, " );
			//string = string.replaceFirst( "\\{ ", "\\{ WAN, " );
			string = string.replaceFirst( "\\ }", "\\, %s }" );
			return String.format( string,
			                      connection.getRelayAddress().getHostAddress(),
			                      connection.getRelayPort() );
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
			return String.format( "{ WAN, address:%s, port:%d, transport:udp }",
			                      connection.getRelayAddress(),
			                      connection.getRelayPort() );
		}
		else
		{
			// return raw configuration data
			return String.format( "{ WAN, address:%s, port:%d, transport:udp }",
			                      linkConfiguration.getWanAddress(),
			                      linkConfiguration.getWanPort() );
		}
	}

	/**
	 * Set the location that we should submit received packets to
	 */
	@Override
	public void setReflector( Reflector reflector )
	{
		// store to hand off to incoming connections
		this.reflector = reflector;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
