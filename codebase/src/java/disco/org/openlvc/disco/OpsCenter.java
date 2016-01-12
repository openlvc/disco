/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.datasource.DatasourceFactory;
import org.openlvc.disco.pdu.PDU;

public class OpsCenter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean open;
	private DiscoConfiguration configuration;
	private Logger logger;
	private PduSource pduSource;
	private PduSink pduSink;

	private IDatasource provider;
	private IPduReceiver pduReceiver;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public OpsCenter()
	{
		this.open = false;
		this.configuration = new DiscoConfiguration();
		this.logger = null;
		this.pduSource = null;
		this.pduSink = null;
		this.provider = null;
		this.pduReceiver = null;
	}

	public OpsCenter( DiscoConfiguration configuration )
	{
		this();
		this.configuration = configuration;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   /////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	public void open() throws DiscoException
	{
		if( open )
			return;
		
		// check to make sure we have everything
		if( this.pduReceiver == null )
			throw new DiscoException( "Cannot open connection without PDU Receiver: null" );
		
		// activate logging
		if( this.logger == null )
		{
			this.configuration.getLoggingConfiguration().activateConfiguration();
			this.logger = configuration.getDiscoLogger();
		}

		welcomeMessage();
		
		// create the underlying provider
		if( this.provider == null )
		{
			this.logger.debug( "Creating provider: "+configuration.getProvider() );
			this.provider = DatasourceFactory.getDatasource( configuration.getProvider() );
			this.provider.configure( this );
		}
		
		// wire up the source and sink to the provider and receiver
		this.pduSource = new PduSource( this, this.provider, this.pduReceiver );
		this.pduSink = new PduSink( this, this.provider );
		
		// open the flood gates!
		this.pduSource.open();
		
		this.open = true;
		logger.info( "OpsCenter is up and running... Can you dig it?" );
	}
	
	public void close() throws DiscoException
	{
		if( !open )
			return;
		
		this.logger.info( "Closing OpsCenter" );
		this.logger.debug( "Closing provider: "+provider.getName() );
		this.provider.close();

		this.logger.info( "OpsCenter has closed" );
		this.open = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Handling Methods   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Set the {@link IPduReceiver} that incoming messages will be routed to. Has no effect
	 * after {@link #open()} has been called.
	 */
	public void setReceiver( IPduReceiver receiver )
	{
		this.pduReceiver = receiver;
	}

	public void setProvider( IDatasource provider )
	{
		this.provider = provider;
	}

	public void send( PDU pdu ) throws DiscoException
	{
		this.pduSink.send( pdu );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Logger getLogger()
	{
		return this.logger;
	}
	
	public DiscoConfiguration getConfiguration()
	{
		return this.configuration;
	}
	
	public PduSource getPduSource()
	{
		return this.pduSource;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Print a welcome message to the logger */
	private void welcomeMessage()
	{
		logger.info( "       Welcome to Open LVC Disco" );
		logger.info( "        .___.__                     " );
		logger.info( "      __| _/|__| ______ ____  ____  " );
		logger.info( "     / __ | |  |/  ___// ___\\/  _ \\ " );
		logger.info( "    / /_/ | |  |\\___ \\\\  \\__(  ( ) )" );
		logger.info( "    \\____ | |__/____  >\\___  >____/ " );
		logger.info( "         \\/         \\/     \\/       " );
		logger.info( "" );
		logger.info( "Version: "+DiscoConfiguration.getVersion() );
		logger.info( "" );
		
		if( logger.isDebugEnabled() )
		{
			// print debug information like number of threads, or settings from config file?
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
