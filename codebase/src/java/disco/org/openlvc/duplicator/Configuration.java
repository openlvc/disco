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
package org.openlvc.duplicator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.Log4jConfiguration;

public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final int LOOP_INFINITELY = 0;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean recording;      // recording or replaying?
	private String filename;        // file to read/write

	// Logging
	private Log4jConfiguration appLoggerConfiguration;
	private Logger applicationLogger;
	
	// DIS Settings
	private String disAddress;      // address or "BROADCAST"
	private int    disPort;         // port to listen on
	private String disInterface;    // IP or nic to use or one of the symbols "LOOPBACK",
	                                // "LINK_LOCAL", "SITE_LOCAL", "GLOBAL"
	private short disExerciseId;
	
	// PDU Processing
	private String pduSender;
	private String pduReceiver;

	// Recording Settings
	private long recordLogIntervalMs;
	
	// Replay Settings
	private boolean replayRealtime; // Should replay proceed in real time (with waits between PDUs)
	private int loopCount;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration()
	{
		this.recording = true;
		this.filename = "duplicator.session";
		
		// Logging
		this.appLoggerConfiguration = new Log4jConfiguration( "duplicator" );
		this.appLoggerConfiguration.setFileOn( false );
		this.appLoggerConfiguration.setConsoleOn( true );
		this.appLoggerConfiguration.setLevel( "INFO" );
		this.applicationLogger = null; // set on first access
		
		// DIS Settings
		this.disAddress    = "BROADCAST";
		this.disPort       = 3000;
		this.disInterface  = "SITE_LOCAL";
		this.disExerciseId = 1;
		
		// PDU Processing
		this.pduSender = null;
		this.pduReceiver = null;
		
		// Recordings Settings
		this.recordLogIntervalMs = 30000; // how frequently to log status info during recording
		
		// Replay Settings
		this.replayRealtime = true;
		this.loopCount = 1;
	}

	public Configuration( String[] commandline ) throws DiscoException
	{
		this();
		applyCommandLine( commandline );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Apply the given command line args to override any defaults that we have
	 */
	public void applyCommandLine( String[] args ) throws DiscoException
	{
		for( int i = 0; i < args.length; i++ )
		{
			String argument = args[i];
			if( argument.equalsIgnoreCase("--record") )
				this.setToRecording();
			else if( argument.equalsIgnoreCase("--replay") )
				this.setToReplaying();
			else if( argument.equalsIgnoreCase("--file") )
				this.setFilename( args[++i] );
			else if( argument.equalsIgnoreCase("--dis-address") )
				this.setDisAddress( args[++i] );
			else if( argument.equalsIgnoreCase("--dis-port") )
				this.setDisPort( Integer.parseInt(args[++i]) );
			else if( argument.equalsIgnoreCase("--dis-interface") )
				this.setDisInterface( args[++i] );
			else if( argument.equalsIgnoreCase("--dis-exercise-id") || argument.equalsIgnoreCase("--dis-exerciseId") )
				this.setDisExerciseId(Short.parseShort(args[++i]) );
			else if( argument.equalsIgnoreCase("--log-interval") )
				this.setRecordLogInterval( Long.parseLong(args[++i]) * 1000 ); // value in seconds
			else if( argument.equalsIgnoreCase("--replay-realtime") ) // 
				this.setReplayRealtime( true );
			else if( argument.equalsIgnoreCase("--replay-fast") )
				this.setReplayRealtime( false );
			else if( argument.equalsIgnoreCase("--log-level") )
				this.setLogLevel( args[++i] );
			else if( argument.equalsIgnoreCase("--pdu-sender") )
				this.setPduSender( args[++i] );
			else if( argument.equalsIgnoreCase("--pdu-receiver") )
				this.setPduReceiver( args[++i] );
			else if( argument.equalsIgnoreCase("--loop") )
			{
				// test to see if next arg is an integer
				// if this is the end of the args or the next isn't an integer, we assume
				// it isn't for us and that they supplied --loop standalone (do infinite)
				try
				{
					Integer.parseInt( args[i+1] ); // make sure it is cool before incrementing
					this.setLoopCount( Integer.parseInt(args[++i]) ); // we're good, increment
				}
				catch( Exception e )
				{
					this.setLoopCount( 0 );	
				}
			}
			else
			{
				throw new DiscoException( "Unknown argument: "+argument );
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Logger getLogger()
	{
		if( this.applicationLogger == null )
		{
			this.appLoggerConfiguration.activateConfiguration();
			this.applicationLogger = LogManager.getFormatterLogger( "duplicator" );
		}

		return this.applicationLogger;
	}
	
	public boolean isRecording()
	{
		return recording;
	}

	public boolean isReplaying()
	{
		return this.recording == false;
	}

	public void setToRecording()
	{
		this.recording = true;
	}
	
	public void setToReplaying()
	{
		this.recording = false;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename( String filename )
	{
		this.filename = filename;
	}

	public String getDisAddress()
	{
		return disAddress;
	}

	public void setDisAddress( String disAddress )
	{
		this.disAddress = disAddress;
	}

	public int getDisPort()
	{
		return disPort;
	}

	public void setDisPort( int disPort )
	{
		this.disPort = disPort;
	}

	public String getDisInterface()
	{
		return disInterface;
	}

	public void setDisInterface( String disInterface )
	{
		this.disInterface = disInterface;
	}
	
	public void setDisExerciseId( short id )
	{
		this.disExerciseId = id;
	}
	
	public short getDisExerciseId()
	{
		return this.disExerciseId;
	}

	public void setPduSender( String pduSender )
	{
		this.pduSender = pduSender;
	}

	public String getPduSender()
	{
		return this.pduSender;
	}

	public void setPduReceiver( String pduReceiver )
	{
		this.pduReceiver = pduReceiver;
	}

	public String getPduReceiver()
	{
		return this.pduReceiver;
	}

	//////////////////////////////////////////////////////////////////
	/// Recording Settings ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	public void setRecordLogInterval( long milliseconds )
	{
		// Just making sure it is positive, but really, if it's less than 100ms it's just stupid
		// Even less than 1000 is stupid
		if( milliseconds < 100 )
			return;

		this.recordLogIntervalMs = milliseconds;
	}
	
	public long getRecordLogInterval()
	{
		return this.recordLogIntervalMs;
	}

	//////////////////////////////////////////////////////////////////
	/// Replay Settings //////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	public void setReplayRealtime( boolean replayRealtime )
	{
		this.replayRealtime = replayRealtime;
		this.setToReplaying();
	}

	/**
	 * If true, replay should be "real time", which means that any wait between PDUs that we
	 * experienced should be faithfully maintained. The alternate is to try and replay PDUs as
	 * fast as possible. If `true`, the replay should be real time. Otherwise, `false` menas
	 * the replay should be as fast as possible.
	 */
	public boolean isReplayRealtime()
	{
		return this.replayRealtime;
	}
	
	public void setLogLevel( String level )
	{
		this.appLoggerConfiguration.setLevel( level );
	}

	/**
	 * @return `true` if we've been asked to loop the replay of a session more than once
	 */
	public boolean isLooping()
	{
		return this.loopCount != 1;
	}

	/**
	 * @return `true` if we are looping indefinitely, `false` otherwise.
	 */
	public boolean isLoopingIndefinitely()
	{
		return this.loopCount == LOOP_INFINITELY;
	}

	/**
	 * Set the number of times we should loop a replay. Setting to `0` ({@link #LOOP_INFINITELY})
	 * will cause us to loop forever. Setting to a negative number will generate an exception.
	 */
	public void setLoopCount( int number )
	{
		if( number < 0 )
			throw new IllegalArgumentException( "Cannot set a loop count less than 0" );
		else
			this.loopCount = number;
	}

	/**
	 * Get the number of times we will replay a session in succession. Default it to play the
	 * session once. If `0` is returned ({@link #LOOP_INFINITELY}) we will keep looping until
	 * someone shuts us down.
	 */
	public int getLoopCount()
	{
		return this.loopCount;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "Duplicator - Record or replay traffic from a DIS network\n" );
		builder.append( "Usage: bin/duplicator [--args]\n" );
		builder.append( "\n" );
		builder.append( "   --record                    Make a recording (default)\n" );
		builder.append( "   --replay                    Replay from a previous recording\n" );
		builder.append( "   --file            (string)  File name to read from/write to\n" );
		builder.append( "   --dis-interface   (string)  NIC to use. LOOPBACK, LINK_LOCAL, SITE_LOCAL*, GLOBAL\n" );
		builder.append( "   --dis-address     (string)  Multicast address to use or BROADCAST (default)\n" );
		builder.append( "   --dis-port        (number)  DIS port to listen/send on. Default: 3000\n" );
		builder.append( "   --dis-exercise-id (number)  Exercise ID to send in outgoing and only recv on (default: 1)\n" );
		builder.append( "   --log-interval    (seconds) When recording, log stats ever x seconds (default: 30)\n" );
		builder.append( "   --replay-realtime           Replay as PDus happened. Delay PDUs if there was receive delay\n" );
		builder.append( "   --replay-fast               Replay all stored PDUs as fast as possible\n" );
		builder.append( "   --loop            (number)  Number of times to replay a session. 0 for infinite (default: 1)\n" );
		builder.append( "   --log-level       (string)  Set the log level: OFF, ERROR, WARN, INFO(default), DEBUG, TRACE\n" );
		builder.append( "   --pdu-receiver    (string)  PDU receive processor: single-thread (def), thread-pool, simple\n" );
		builder.append( "   --pdu-sender      (string)  PDU send processor: single-thread (def), thread-pool, simple\n" );
		builder.append( "\n" );
		System.out.println( builder.toString() );
	}
}
