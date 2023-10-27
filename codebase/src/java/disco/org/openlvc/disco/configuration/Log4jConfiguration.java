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
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
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
	private static final String DEFAULT_PATTERN_REG  = "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36}: %msg%n";
	private static final String DEFAULT_PATTERN_DATE = "%d{dd MMM yyyy @ HH:mm:ss.SSS} [%t] %-5level %logger{36}: %msg%n";

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
			this.appName = LogManager.ROOT_LOGGER_NAME;
		
		this.pattern = DEFAULT_PATTERN_REG;
		this.level = Level.INFO;
		this.consoleOn = true;
		this.fileOn = true;
		this.file = new File( "logs/disco.log" );
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
		
		// remove any existing appenders
		Map<String,Appender> appenders = new HashMap<>( config.getAppenders() );
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
		
		return ConsoleAppender.newBuilder()
		                      .setLayout( layout )
		                      .setTarget( ConsoleAppender.Target.SYSTEM_OUT )
		                      .setName( appName + "-Console" )
		                      .setIgnoreExceptions( false )
		                      .build();
	}
	
	private FileAppender createFileAppender( LoggerContext context )
	{
		PatternLayout layout = PatternLayout.newBuilder().withPattern(this.pattern).build();
		
		return FileAppender.newBuilder()
		                   .withFileName( file.getAbsolutePath() )
		                   .withAppend( false )
		                   .withLocking(false)
		                   .setName( appName + "-File" )
		                   .setImmediateFlush( true )
		                   .setIgnoreExceptions( false )
		                   .setBufferedIo( true )
		                   .setBufferSize( 8192 )
		                   .setLayout( layout )
		                   .withAdvertise( false )
		                   .setConfiguration( context.getConfiguration() )
		                   .build();
	}
	
	@SuppressWarnings("unused")
	private RollingFileAppender createRollingFileAppender( )
	{
		SizeBasedTriggeringPolicy sizePolicy = SizeBasedTriggeringPolicy.createPolicy( "10MB" );
		PatternLayout layout = PatternLayout.newBuilder().withPattern(this.pattern).build();
		
		return RollingFileAppender.newBuilder()
		                          .withPolicy( sizePolicy )
		                          .setName( "RollingFile" )
		                          .withFileName( file.getAbsolutePath() )
		                          .withFilePattern( file.getName()+"-%d{MM-dd-yy-HH-mm-ss}.log" )
		                          .setLayout( layout )
		                          .build();
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
	
	public boolean isLogWithDate() { return this.pattern.equals(DEFAULT_PATTERN_DATE); }
	/** This will cause the logger to include the date in each message prefix. Note that if you
	    are using a custom pattern, or this value is set after the configuration is applied, this
	    call will have no effect */
	public void setLogWithDate( boolean extended )
	{
		// if we are using a custom pattern, just ignore this
		if( this.pattern.equals(DEFAULT_PATTERN_REG) == false &&
			this.pattern.equals(DEFAULT_PATTERN_DATE) == false )
			return;
		
		if( extended )
			this.pattern = DEFAULT_PATTERN_DATE;
		else
			this.pattern = DEFAULT_PATTERN_REG;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
