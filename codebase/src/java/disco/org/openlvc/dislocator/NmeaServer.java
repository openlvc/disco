/*
 *   Copyright 2018 Open LVC Project.
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
package org.openlvc.dislocator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.utils.LLA;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Datum;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;
import net.sf.marineapi.nmea.util.Units;

/**
 * This class provides a TCP/IP server that remote clients can connect to and receive
 * location information about the tracking entity from in NMEA format. Whenever we
 * receive a location update from the DIS network, it is sent here where it is converted
 * into a NMEA 0183 "GGA" sentence and sent to all connected clients.
 * <p/>
 * 
 * The structure of a GGA sentence is:
 * <p/>
 * <code></code>
 * 
 *
 */
public class NmeaServer
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private Logger logger;
	private LLA lastKnown;
	
	// NMEA Converters
	// We just have one for all the supported versions ready to go.
	private SentenceId nmeaFormat;
	private GGASentence ggaParser;
	private GLLSentence gllParser;
	private RMCSentence rmcParser;

	// Connection Properties
	private InetSocketAddress socketAddress;

	// Runtime Properties
	private boolean isConnected;
	private ServerSocket serverSocket;
	private ConnectionAcceptor connectionAcceptor;
	private List<Connection> connections;
	private List<Connection> dead; // used each update to keep list of dead connections for deferred removal

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected NmeaServer( Configuration configuration )
	{
		this.configuration = configuration;
		this.logger = LogManager.getFormatterLogger( configuration.getDislocatorLogger().getName()+".tcp" );
		this.lastKnown = null;

		// NMEA Converters
		this.nmeaFormat = configuration.getNmeaOutputFormat();
		SentenceFactory sf = SentenceFactory.getInstance();
		// GGA
		this.ggaParser = (GGASentence)sf.createParser( TalkerId.GP, SentenceId.GGA );
		this.ggaParser.setFixQuality( GpsFixQuality.SIMULATED );
		this.ggaParser.setSatelliteCount( 4 );
		this.ggaParser.setAltitudeUnits( Units.METER );
		this.ggaParser.setHorizontalDOP( 0.0 );
		// GLL
		this.gllParser = (GLLSentence)sf.createParser( TalkerId.GP, SentenceId.GLL );
		this.gllParser.setStatus( DataStatus.ACTIVE );
		// RMC
		this.rmcParser = (RMCSentence)sf.createParser( TalkerId.GP, SentenceId.RMC );
		this.rmcParser.setMode( FaaMode.SIMULATED );


		// Connection Properties
		this.socketAddress = new InetSocketAddress( configuration.getTcpServerAddress(),
		                                            configuration.getTcpServerPort() );
		
		// Runtime Properties
		this.isConnected = false;  // set in open()
		this.serverSocket = null;  // set in open()
		this.connectionAcceptor = new ConnectionAcceptor();
		this.connections = new ArrayList<>();
		this.dead = new ArrayList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// TCP Server Management Methods   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void open()
	{
		if( this.isConnected )
			return;
		
		try
		{
			logger.debug( "--- TCP Server Configuration ---" );
			logger.debug( "  >> Listen Address: "+this.configuration.getTcpServerAddress() );
			logger.debug( "  >> Listen Port   : "+this.configuration.getTcpServerPort() );
			logger.debug( "" );
			logger.debug( "Opening server socket and listening for new connection requests" );

			// re-initialize the socket address just in case
			this.socketAddress = new InetSocketAddress( this.configuration.getTcpServerAddress(),
			                                            this.configuration.getTcpServerPort() );
	
			// open the server socket
			this.serverSocket = new ServerSocket();
			this.serverSocket.bind( this.socketAddress );
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Error starting NMEA TCP Server: "+ioex.getMessage(), ioex );
		}

		this.connectionAcceptor = new ConnectionAcceptor();
		this.connectionAcceptor.start();

		this.isConnected = true;
		logger.info( "NMEA TCP Server connection is open (%s)",
		             serverSocket.getLocalSocketAddress() );
	}
	
	public void close()
	{
		if( this.isConnected == false )
			return;

		try
		{
			logger.trace( "Closing server socket and refusing any new connections" );
			// kill the connection acceptor
			this.serverSocket.close();
			
			// stop the acceptor and wait for it to wrap up
			this.connectionAcceptor.interrupt();
			this.connectionAcceptor.join();
			
			// close all the child connections
			for( Connection connection : connections )
				connection.close();
		}
		catch( IOException ioex )
		{
			throw new DiscoException( "Error stopping NMEA TCP Server: "+ioex.getMessage(), ioex );
		}
		catch( InterruptedException ie )
		{
			// it's time to go anyway; just let it go man
		}
		finally
		{
			this.isConnected = false;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected synchronized void updateLocation( LLA location )
	{
		this.lastKnown = location;
		dead.clear();
		
		String nmeaString = toNmea( location );
		logger.info( "Updating location to: "+location );
		
		for( Connection connection : connections )
		{
			try
			{
				if( connection.socket.isConnected() )
					connection.updateLocation( nmeaString );
				else
					dead.add( connection );
			}
			catch( Exception e )
			{
				logger.info( "Error sending update to connection; disconnecting remote client" );
				dead.add( connection );
			}
		}
		
		for( Connection connection : dead )
			connections.remove( connection );
	}

	private String toNmea( LLA lla )
	{
		switch( nmeaFormat )
		{
			case GGA: return llaToGga( lla );
			case GLL: return llaToGll( lla );
			case RMC: return llaToRmc( lla );
			default: throw new DiscoException( "Unsupported NMEA foramt: "+nmeaFormat );
		}
	}
	
	private String llaToGga( LLA lla )
	{
		Position position = new Position( lla.getLatitude(),
		                                  lla.getLongitude(),
		                                  lla.getAltitude(),
		                                  Datum.WGS84 );
		ggaParser.setPosition( position );
		ggaParser.setTime( new Time() );
		return ggaParser.toSentence();
	}

	private String llaToGll( LLA lla )
	{
		Position pos = new Position( lla.getLatitude(), lla.getLongitude(), lla.getAltitude(), Datum.WGS84 );
		gllParser.setPosition( pos );
		gllParser.setTime( new Time() );		
		return gllParser.toSentence();
	}

	private String llaToRmc( LLA lla )
	{
		Position pos = new Position( lla.getLatitude(), lla.getLongitude(), lla.getAltitude(), Datum.WGS84 );
		rmcParser.setPosition( pos );
		rmcParser.setCourse( 0.0 ); // FIXME
		rmcParser.setSpeed( 0.0 );  // FIXME
		rmcParser.setDate( new Date() );
		rmcParser.setTime( new Time() );
		return rmcParser.toSentence();
	}
	
	private synchronized void addConnection( Connection connection )
	{
		this.connections.add( connection );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	//////////////////////////////////////////////////////////////////////////////////////////
	///  Private Inner Class: ConnectionAcceptor   ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private class ConnectionAcceptor extends Thread
	{
		public ConnectionAcceptor()
		{
			super( "TCP Connection Acceptor" );
		}
	
		public void run()
		{
			logger.trace( "Connection acceptor is open; listening for incoming connections" );
			while( Thread.interrupted() == false )
			{
				try
				{
					Socket socket = serverSocket.accept();
					Connection connection = new Connection( socket );
					if( lastKnown != null )
						connection.updateLocation( toNmea(lastKnown) );
					addConnection( connection );

					logger.info( "(Accepted) Connection from ip=%s",
					             socket.getRemoteSocketAddress() );
				}
				catch( Exception e )
				{
					if( Thread.interrupted() )
						break;
					else
						logger.error( "Error accepting connection: "+e.getMessage(), e );
				}
			}

			logger.info( "Stopped accepting new connections, shutting down" );
		}
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////
	///  Private Inner Class: Connection   ///////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Represents a connection to a remote client.
	 */
	private class Connection
	{
		private Socket socket;
		private PrintWriter writer;
		public Connection( Socket socket ) throws IOException
		{
			this.socket = socket;
			this.writer = new PrintWriter( socket.getOutputStream() );
		}
		
		public void updateLocation( String ggaSentence )
		{
			writer.println( ggaSentence );
			writer.flush();
		}
		
		public void close()
		{
			try
			{
				this.socket.close();
			}
			catch( IOException ioex )
			{}
		}
	}

}