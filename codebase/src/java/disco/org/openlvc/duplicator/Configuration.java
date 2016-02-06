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
	
	// Replay Settings
	private boolean replayRealtime; // Should replay proceed in real time (with waits between PDUs)

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
		this.disAddress   = "BROADCAST";
		this.disPort      = 3000;
		this.disInterface = "SITE_LOCAL";
		
		// Replay Settings
		this.replayRealtime = true;
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
	 * @param args
	 * @throws DiscoException
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
			else if( argument.equalsIgnoreCase("--replay-realtime") )
				this.setReplayRealtime( true );
			else if( argument.equalsIgnoreCase("--replay-fast") )
				this.setReplayRealtime( false );
			else if( argument.equalsIgnoreCase("--log-level") )
				this.setLogLevel( args[++i] );
			else
				throw new DiscoException( "Unknown argument: "+argument );
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
	
	public void setReplayRealtime( boolean replayRealtime )
	{
		this.replayRealtime = replayRealtime;
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
		builder.append( "   --dis-address     (string)  Multicast address to use or BROADCAST (default)\n" );
		builder.append( "   --dis-port        (int)     DIS port to listen/send on. Default: 3000\n" );
		builder.append( "   --dis-interface   (string)  NIC to use. LOOPBACK, LINK_LOCAL, SITE_LOCAL*, GLOBAL\n" );
		builder.append( "   --replay-realtime           Replay as PDus happened. Delay PDUs if there was receive delay\n" );
		builder.append( "   --replay-fast               Replay all stored PDUs as fast as possible\n" );
		builder.append( "   --log-level                 Set the log level: OFF, ERROR, WARN, INFO(default), DEBUG, TRACE\n" );
		builder.append( "\n" );
		System.out.println( builder.toString() );
	}
}
