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
package org.openlvc.disco.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Allows a user to define the basic properties for a Log4j configuration and then tell it
 * to apply itself to the logger with the same name as defined in the "appName".
 */
public class Log4jConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String appName;
	private String pattern;
	private Level level;
	private boolean consoleOn;
	private boolean fileOn;
	private File file;
	private Set<Appender> additionalAppenders;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Log4jConfiguration( String appName )
	{
		this.appName = appName;
		if( appName == null )
			this.appName = "disco"; //this.appName = LogManager.ROOT_LOGGER_NAME;
		
		this.pattern = "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36}: %msg%n";
		this.level = Level.INFO;
		this.consoleOn = true;
		this.fileOn = false;
		this.file = new File( "disco.log" );
		this.additionalAppenders = new HashSet<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Configuration Activation Methods    ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Take the configuration values and make them active for the specified root logger */
	public void activateConfiguration()
	{
		// create a new config object and get the context (we'll need it later)
		// once we're done configuring the config we'll add it to the context and then
		// tell it to refresh itself
		LoggerConfig config = new LoggerConfig( appName, Level.OFF, true );
		LoggerContext context = (LoggerContext)LogManager.getContext( false );
		
		// remove any existing appenaders
		Map<String,Appender> appenders = new HashMap<String,Appender>( config.getAppenders() );
		for( String key : appenders.keySet() )
			config.removeAppender( key );

		// create the new appenders and attach them
		if( this.consoleOn )
		{
			ConsoleAppender appender = createConsoleAppender();
			appender.start();
			config.addAppender( appender, level, null );
		}
		
		if( this.fileOn )
		{
			FileAppender appender = createFileAppender( context );
			appender.start();
			config.addAppender( appender, level, null );
		}

		for( Appender appender : this.additionalAppenders )
		{
			appender.start();
			config.addAppender( appender, level, null );
		}
		
		// set the logger level
		config.setLevel( this.level );
		
		// flush the update
		context.getConfiguration().addLogger( appName, config );
		context.updateLoggers();
	}

	/**
	 * Adds an additional appender to disco's logger.
	 * <p/>
	 * By default, Disco provides console and file appenders which are activated according to the 
	 * configuration within this object. Additional appenders may be added through this method.
	 * <p/>
	 * NOTE: Appenders added by this method will not be activated until {@link #activateConfiguration()}
	 * is called.
	 * 
	 * @param appender the appender to add to disco's logger
	 */
	public void addAdditionalAppender( Appender appender )
	{
		this.additionalAppenders.add( appender );
	}
	
	public void removeAdditionalAppender( Appender appender )
	{
		this.additionalAppenders.remove( appender );
	}

	private ConsoleAppender createConsoleAppender()
	{
		PatternLayout layout = PatternLayout.newBuilder().withPattern(this.pattern).build();
		return ConsoleAppender.createAppender( layout,
		                                       null,                      // filter
		                                       "SYSTEM_OUT",              // targetString
		                                       appName+"-"+"Console",     // name
		                                       null,                      // "follow"
		                                       "false" );                 // ignoreExceptions
	}
	
	private FileAppender createFileAppender( LoggerContext context )
	{
		PatternLayout layout = PatternLayout.newBuilder().withPattern(this.pattern).build();
		return FileAppender.createAppender( file.getAbsolutePath(),       // fileName
		                                    "false",                      // append
		                                    "false",                      // locking
		                                    appName+"-"+"File",           // name
		                                    "true",                       // immediateFlush
		                                    "false",                      // ignoreExceptions
		                                    "true",                       // bufferedIo
		                                    "8192",                       // bufferSizeStr
		                                    layout,                       // layout
		                                    null,                         // filter
		                                    "false",                      // advertise
		                                    null,                         // advertiseUri
		                                    context.getConfiguration() ); // configuration
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Disable this configuration entirely. Sets the log level to OFF and disables file and
	 * console logging.
	 */
	public void disable()
	{
		this.setLevel( "OFF" );
		this.setFileOn( false );
		this.setConsoleOn( false );
	}
	
	public String getAppName() { return this.appName; }
	public void setAppName( String name ) { this.appName = name; }
	
	public boolean isConsoleOn() { return this.consoleOn; }
	public void setConsoleOn( boolean on ) { this.consoleOn = on; }
	
	public boolean isFileOn() { return this.fileOn; }
	public void setFileOn( boolean on ) { this.fileOn = on; }
	
	public File getFile() { return this.file; }
	public void setFile( File file ) { this.file = file; }
	public void setFile( String path ) { this.file = new File(path); }
	
	public String getLevel() { return this.level.name(); }
	public void setLevel( String level ) { this.level = Level.valueOf(level); }

	public String getPattern() { return this.pattern; }
	public void setPattern( String pattern ) { this.pattern = pattern; }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
