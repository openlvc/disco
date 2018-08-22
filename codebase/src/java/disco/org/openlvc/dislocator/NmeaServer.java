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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.utils.LLA;
import org.openlvc.disco.utils.ThreadUtils;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VTGSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
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
 * location information about the tracking entity from in NMEA format. There are actually
 * a number of NMEA different formats. This server currently supports three, and will output
 * one of them depending on what the {@link Configuration} object specified. The supported
 * formats are:
 * 
 * <ul>
 *   <li>GGA - Global Positioning System Fix Data</li>
 *   <li>RMC - Recommended minimum specific GPS/Transit data</li>
 *   <li>GLL - Geographic position, latitude / longitude</li>
 * </ul>
 * 
 * For more information on message structure, see one of the following links:
 * <ul>
 *   <li>http://aprs.gids.nl/nmea/</li>
 *   <li>http://www.gpsinformation.org/dale/nmea.htm</li>
 * </ul>
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
	private LLA lastKnownLocation;
	private EulerAngles lastKnownOrientation;
	
	// NMEA Converters
	// We just have one for all the supported versions ready to go.
	private GGASentence ggaParser;
	private GLLSentence gllParser;
	private RMCSentence rmcParser;
	private VTGSentence vtgParser;

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
		this.lastKnownLocation = null;
		this.lastKnownOrientation = null;

		// NMEA Converters - Prime everything for later use
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
		// Vector Track Good
		this.vtgParser = (VTGSentence)sf.createParser( TalkerId.GP, SentenceId.VTG );
		this.vtgParser.setMode( FaaMode.SIMULATED );


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
			{
				if( connection.isAlive() )
				{
					if( connection.socket.isConnected() )
						connection.close();
					
					// kill the thread
					connection.interrupt();
					ThreadUtils.exceptionlessThreadJoin( connection, 500 );
				}
			}
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
	private synchronized void addConnection( Connection connection )
	{
		this.connections.add( connection );
	}

	/**
	 * The DIS receiver has found an update and wishes to notify us about it. We'll convert
	 * this into the appropriate NMEA sentences and hand them off to each connection.
	 * 
	 * @param location The new location of the entity
	 * @param orientation The new orientation of the entity
	 */
	protected synchronized void updateLocation( LLA location, EulerAngles orientation )
	{
		this.lastKnownLocation = location;
		this.lastKnownOrientation = orientation;
		logger.debug( "(DIS Update) Updating location to: "+location );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// NMEA Conversion Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return VTG NMEA string for the last recorded orientation
	 */
	private String getLastKnownAsVTG()
	{
		vtgParser.setSpeedKnots( 100.0 );      // FIXME
		vtgParser.setSpeedKmh( 100/0.539957 ); // FIXME  (0.539957 knots-to-km)
		vtgParser.setMagneticCourse( 10.0 );   // FIXME  
		vtgParser.setTrueCourse( 10.0 );       // FIXME
		return vtgParser.toSentence();
	}

	public String getLastKnownAsGGA()
	{
		LLA lla = lastKnownLocation;
		Position position = new Position( lla.getLatitude(),
		                                  lla.getLongitude(),
		                                  lla.getAltitude(),
		                                  Datum.WGS84 );
		ggaParser.setPosition( position );
		ggaParser.setTime( getCurrentTime() );
		ggaParser.setGeoidalHeightUnits( Units.METER );
		return ggaParser.toSentence();
	}

	public String getLastKnownAsGLL()
	{
		LLA lla = lastKnownLocation;
		Position pos = new Position( lla.getLatitude(), lla.getLongitude(), lla.getAltitude(), Datum.WGS84 );
		gllParser.setPosition( pos );
		gllParser.setTime( getCurrentTime() );
		return gllParser.toSentence();
	}

	public String getLastKnownAsRMC()
	{
		LLA lla = lastKnownLocation;
		Position pos = new Position( lla.getLatitude(), lla.getLongitude(), lla.getAltitude(), Datum.WGS84 );
		rmcParser.setPosition( pos );
		rmcParser.setCourse( 10.0 ); // FIXME
		rmcParser.setSpeed( 100.0 );  // FIXME
		rmcParser.setDate( getCurrentDate() );
		rmcParser.setTime( getCurrentTime() );
		rmcParser.setStatus( DataStatus.ACTIVE );
		rmcParser.setVariation( 0.1 );
		rmcParser.setDirectionOfVariation( CompassPoint.WEST );
		return rmcParser.toSentence();
	}
	
	private Time getCurrentTime()
	{
		OffsetDateTime time = configuration.useUtcTime() ? OffsetDateTime.now( ZoneOffset.UTC ) :
		                                                   OffsetDateTime.now();
		return new Time( time.getHour(), time.getMinute(), time.getSecond() );
	}
	
	private Date getCurrentDate()
	{
		OffsetDateTime time = configuration.useUtcTime() ? OffsetDateTime.now( ZoneOffset.UTC ) :
		                                                   OffsetDateTime.now();
		return new Date( time.getYear(), time.getMonthValue(), time.getDayOfMonth() );
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
					addConnection( connection );
					connection.start();

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
	private class Connection extends Thread
	{
		private Socket socket;
		private PrintWriter writer;
		public Connection( Socket socket ) throws IOException
		{
			this.socket = socket;
			this.writer = new PrintWriter( socket.getOutputStream() );
		}
		
		public void run()
		{
			while( Thread.interrupted() == false )
			{
				if( lastKnownLocation != null || lastKnownOrientation != null )
				{
    				// flush the last known status
					writer.println( getLastKnownAsVTG() );
					writer.println( getLastKnownAsGGA() );
					writer.println( getLastKnownAsRMC() );
    				writer.flush();
				}
				
				// sleep for a little bit
				ThreadUtils.exceptionlessSleep( configuration.getNmeaPingInterval() );
			}
			
			// it's time to bug out!
			close();
		}
		
		public void close()
		{
			try
			{
				logger.info( "(Closed) Connection with ip=%s", socket.getRemoteSocketAddress() );
				this.socket.close();
				dead.add( this );
			}
			catch( IOException ioex )
			{}
		}
	}

}
