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

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;
import org.openlvc.disco.connection.ConnectionFactory;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.connection.Metrics;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.utils.ClassLoaderUtils;

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
	private PduSender pduSender;       // where we send PDUs to the network             (outgoing)
	private IPduListener pduListener;  // where we send PDU's received from the network (incoming)

	private IConnection connection;    // source and destiantion for PDUs - typically network

	// Cached configuration settings for quick access
	private short exerciseId;
	
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
		
		this.exerciseId = 1;
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
		
		// get cached values -- for SPEEEED
		this.exerciseId = configuration.getDisConfiguration().getExerciseId();
		
		// activate logging - fetching the logger will cause the configuration to be lazy loaded
		this.logger = configuration.getDiscoLogger();
		welcomeMessage();

		// enable networking
		if( this.connection == null )
		{
			// Note: We need to do this now, because it's too late once the RPR connection
			//       class tries to load up.
			if( configuration.getConnection().equals("rpr") )
				applyRprClasspathHack();
			
			this.logger.info( "Creating connection: "+configuration.getConnection() );
			this.connection = ConnectionFactory.getConnection( configuration.getConnection() );
			this.connection.configure( this );

			// tell people what PDUs this connection supports
			this.logger.info( "Supported PDU Types: "+connection.getSupportedPduTypes().toString() );
		}
		
		// wire up the sender and receiver to the connection and listener
		if( this.pduReceiver == null )
			this.pduReceiver = PduReceiver.create( configuration.getPduReceiver(), this );
		if( this.pduSender == null )
			this.pduSender = PduSender.create( configuration.getPduSender(), this );
		
		// log out the active PDU processing flags
		logger.debug( "Flags: "+DiscoConfiguration.getFlags() );
		
		// open the flood gates!
		try
		{
			this.pduReceiver.open();
			this.connection.open();
		}
		catch( DiscoException de )
		{
			this.connection.close();
			this.pduSender.close();
			this.pduReceiver.close();
			throw de;
		}
		
		this.open = true;
		logger.info( "OpsCenter is up and running... Can you dig it?" );
	}
	
	public void close() throws DiscoException
	{
		if( !open )
			return;
		
		this.logger.info( "Closing OpsCenter" );
		this.logger.debug( "Closing connection: "+connection.getName() );
		
		// flush any messages in the sender and close
		this.pduSender.close();
		
		// kill the connection to stop incoming packets
		this.connection.close();
		
		// flush any messages in the receiver and close
		this.pduReceiver.close();

		this.open = false;
		this.logger.info( "OpsCenter has closed" );
	}
	
	/**
	 * If we are using the RPR connection, we need to extend the classpath before we can
	 * even have a go at loading it. Because it references HLA classes, those have to be
	 * on the classpath from the time that the RPR connection loads. Extending the classpath
	 * on open is too late.
	 */
	private void applyRprClasspathHack()
	{
		List<File> paths = configuration.getRprConfiguration().getRtiPathExtension();
		for( File file : paths )
		{
			if( file.exists() == false )
				logger.warn( "Missing jar file: "+file.getAbsolutePath() );
		}
		
		ClassLoaderUtils.extendClasspath( paths );
		logger.debug( "Extended classpath to include HLA libraries; added: "+paths );
		
		// Mak is too cool for the classpath, it needs to be put on the library path
		if( configuration.getRprConfiguration().getRtiProvider() == RtiProvider.Mak )
		{
			ClassLoaderUtils.extendLibraryPath( paths );
			logger.debug( "Extended Java library path to include HLA libraries; added: "+paths );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Handling Methods   /////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Set the {@link IPduListener} that incoming messages will be routed to. Has no effect
	 * after {@link #open()} has been called.
	 */
	public void setPduListener( IPduListener receiver )
	{
		this.pduListener = receiver;
	}
	
	public IPduListener getPduListener()
	{
		return this.pduListener;
	}

	/**
	 * Hand the given PDU off to the {@link PduSender} to process and forward to the network
	 */
	public void send( PDU pdu ) throws DiscoException
	{
		if( this.open == false )
			throw new DiscoException( "OpsCenter is not open yet" );

		pdu.setExerciseId( this.exerciseId );
		pdu.setLocalTimestamp( System.currentTimeMillis() );
		this.pduSender.send( pdu );
	}

	/**
	 * Sends the given PDU without making any modifications or additions (such as settings its
	 * exercise/site/app ids or anything like that). Just send the PDU straight through as it is.
	 */
	public void sendRaw( PDU pdu ) throws DiscoException
	{
		if( this.open == false )
			throw new DiscoException( "OpsCenter is not open yet" );

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
	
	public void setConfiguration( DiscoConfiguration configuration )
	{
		if( configuration == null )
			return;

		if( this.open )
			throw new DiscoException( "Cannot change the configuration of an open OpsCenter" );
		else
			this.configuration = configuration;
	}
	
	public IConnection getConnection()
	{
		return this.connection;
	}

	/**
	 * Set the {@link PduReceiver} to use. This will override any receiver information from the
	 * configuration data. This must be called _before_ the center is opened, otherwise it will
	 * be ignored.
	 * 
	 * @param receiver The {@link PduReceiver} that should be used.
	 */
	public void setPduReceiver( PduReceiver receiver )
	{
		this.pduReceiver = receiver;
	}

	public PduReceiver getPduReceiver()
	{
		return this.pduReceiver;
	}
	
	/**
	 * Set the {@link PduSender} to use. This will override any receiver information from the
	 * configuration data. This must be called _before_ the center is opened, otherwise it will
	 * be ignored.
	 * 
	 * @param sender The {@link PduSender} that should be used.
	 */
	public void setPduSender( PduSender sender )
	{
		this.pduSender = sender;
	}
	
	// Not used yet - so let's now open any more tendrils if we don't need to
	//public PduSender getPduSender()
	//{
	//	return this.pduSender;
	//}
	
	public Metrics getMetrics()
	{
		return this.connection.getMetrics();
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
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
