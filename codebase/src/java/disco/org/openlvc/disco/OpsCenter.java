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
import org.openlvc.disco.connection.ConnectionFactory;
import org.openlvc.disco.connection.IConnection;
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
	private PduReceiver pduReceiver;   // where we receive byte[]'s from the network    (incoming)
	private PduSender pduSender;       // where we send byte[]'s to the network         (outgoing)
	private IPduListener pduListener;  // where we send PDU's received from the network (incoming)

	private IConnection connection;    // source and destiantion for PDUs - typically network

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public OpsCenter()
	{
		this.open = false;
		this.configuration = new DiscoConfiguration();
		this.logger = null;
		this.pduReceiver = null;
		this.pduSender = null;
		this.pduListener = null;
		this.connection = null;
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
		
		// activate logging - fetching the logger will cause the configuration to be lazy loaded
		this.logger = configuration.getDiscoLogger();
		welcomeMessage();

		// enable networking
		if( this.connection == null )
		{
			this.logger.debug( "Creating connection: "+configuration.getConnection() );
			this.connection = ConnectionFactory.getConnection( configuration.getConnection() );
			this.connection.configure( this );
		}
		
		// wire up the sender and receiver to the connection and listener
		this.pduReceiver = new PduReceiver( this, this.connection, this.pduListener );
		this.pduSender = new PduSender( this, this.connection );
		
		// open the flood gates!
		this.pduReceiver.open();
		
		this.open = true;
		logger.info( "OpsCenter is up and running... Can you dig it?" );
	}
	
	public void close() throws DiscoException
	{
		if( !open )
			return;
		
		this.logger.info( "Closing OpsCenter" );
		this.logger.debug( "Closing connection: "+connection.getName() );
		this.connection.close();
		this.pduReceiver.close();  // has executors we need to shut down
		this.pduSender.close();    // has executors we need to shut down

		this.logger.info( "OpsCenter has closed" );
		this.open = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Handling Methods   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Set the {@link IPduListener} that incoming messages will be routed to. Has no effect
	 * after {@link #open()} has been called.
	 */
	public void setListener( IPduListener receiver )
	{
		this.pduListener = receiver;
	}

	/**
	 * Hand the given PDU off to the {@link PduSender} to process and forward to the network
	 */
	public void send( PDU pdu ) throws DiscoException
	{
		this.pduSender.send( pdu );
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
	
	public PduReceiver getPduReceiver()
	{
		return this.pduReceiver;
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
