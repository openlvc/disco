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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

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
		this.file = new File( "logs/bts.log" );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Configuration Activation Methods    ////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private void attachConsoleAppender( ConfigurationBuilder<BuiltConfiguration> rootBuilder,
	                                    RootLoggerComponentBuilder rootLogger )
	{
		AppenderComponentBuilder appender = rootBuilder.newAppender( "Console", "CONSOLE" );
		appender.addAttribute( "target", ConsoleAppender.Target.SYSTEM_OUT );
		appender.add( getLayout(rootBuilder) );
		rootLogger.add( rootBuilder.newAppenderRef("Console") );
		rootBuilder.add( appender );
	}

	private void attachFileAppender( ConfigurationBuilder<BuiltConfiguration> rootBuilder,
	                                 RootLoggerComponentBuilder rootLogger )
	{
		// create the appender builder - arguments are (name, type) where type is a magic string
		// that references a file-type appender
		AppenderComponentBuilder appender = rootBuilder.newAppender( "File", "File" );
		appender.addAttribute( "fileName", file.getAbsolutePath() )
		        .addAttribute( "append", false )
		        .addAttribute( "ignoreExceptions", false )
		        .addAttribute( "bufferedIo", true )
		        .addAttribute( "bufferSize", 8192 )
		        .add( getLayout(rootBuilder) );
		
		rootLogger.add( rootBuilder.newAppenderRef("File") );
		rootBuilder.add( appender );
	}

	@SuppressWarnings("unused")
	private void attachRollingFileAppender( ConfigurationBuilder<BuiltConfiguration> rootBuilder,
	                                        RootLoggerComponentBuilder rootLogger )
	{
		// create a rollover trigger
		@SuppressWarnings("rawtypes")
		ComponentBuilder rolloverTrigger =
			rootBuilder.newComponent( "Policies" )
			           .addComponent( rootBuilder.newComponent("SizeBasedTriggeringPolicy")
			                                     .addAttribute( "size", "100MB" ) );

		AppenderComponentBuilder appender = rootBuilder.newAppender( "File", "RollingFile" );
		appender.addAttribute( "fileName", file.getAbsolutePath() )
		        .addAttribute( "filePattern", file.getName()+"-%d{MM-dd-yy-HH-mm-ss}.log" )
		        .add( getLayout(rootBuilder) )
		        .addComponent( rolloverTrigger );
		
		rootLogger.add( rootBuilder.newAppenderRef("File") );
		rootBuilder.add( appender );
	}

	private LayoutComponentBuilder getLayout( ConfigurationBuilder<BuiltConfiguration> builder )
	{
		return builder.newLayout( "PatternLayout" ).addAttribute( "pattern", this.pattern )
		                                           .addAttribute( "charset", "UTF-8" );
	}
	
	/** Take the configuration values and make them active for the specified root logger */
	public void activateConfiguration()
	{
		// What an absolute pile of trash. This is apparently the way you are meant to build
		// configurations in code. With a bunch of magic strings pointing at plugins for what
		// are standard parts of the Log4j core. What enterprise pattern abstract factory crack
		// were these guys smoking?
		
		// Create the main configuration builder
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel( Level.OFF );
		builder.setConfigurationName( appName );
		
		// Specify the root logger configuration
		RootLoggerComponentBuilder rootLogger = builder.newRootLogger( this.level );
		
		// Create the appenders and add them
		if( this.consoleOn )
			attachConsoleAppender( builder, rootLogger );
		
		if( this.fileOn )
			attachFileAppender( builder, rootLogger );
		
		// Add the root logger and configure
		builder.add( rootLogger );
		Configurator.reconfigure( builder.build() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods ///////////////////////////////////////////////////////////
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
