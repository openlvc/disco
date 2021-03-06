/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.disassembler.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.Log4jConfiguration;

/**
 * Build up the configuration for this Disassembler run.
 * 
 * This class contains all the global configuration options that apply to the Disassembler.
 * Each analyzer may have specific configuration properties that it supports. If so, it will
 * support a sub-class of {@link Configuration} that will pass general calls up to here for
 * processing but will cut in to grab its specific properties where it can.
 * 
 * For example, it will override {@link #applyCommandLine(String[])} to first check for
 * its own properties before deferring up to the method in the parent if there is no
 * analyzer-specific match.
 * 
 * There are a number of global options to be aware of, including:
 * 
 *  - analyzer   : the particular analyzer we want to run
 *  - infile     : the duplicator session file to read
 *  - output     : the output format
 *  - The normals: config file, log level, log file
 * 
 * These can all be set from the command line, with the important note that if the first
 * argument on the command line is not "--" prefixed it is _assumed_ to be the analyzer
 * mode and will be parsed as such.
 * 
 * ## Additional Command Line Args
 * There are some command linen args that can be specified under many names:
 * 
 *  - `--analyzer`, `[ANALYZER]`    -- First argument (non -- prefixed) or the prefixed version
 *  - `--enum`, `--enumeration`     -- Run the enumeration analyzer
 *  - `--text`, `--json`, `--csv`   -- Output format
 *
 * The above is not an exhaustive list, but just an example. For any of these sorts, the value
 * that is used is the one that appeared last in the list (overriding all that came before).
 */
public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String KEY_CONFIG_FILE     = "disassembler.configfile";
	public static final String KEY_LOG_LEVEL       = "disassembler.loglevel";
	public static final String KEY_LOG_FILE        = "disassembler.logfile";
	
	public static final String KEY_ANALYZER_TYPE   = "disassembler.analyzer";
	public static final String KEY_INFILE          = "disassembler.infile";  // duplicator.session
	public static final String KEY_OUTFILE         = "disassembler.outfile"; // STDOUT or file path
	public static final String KEY_OUTPUT_FORMAT   = "disassembler.output";  // text, json, csv

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;

	private Logger applicationLogger;
	private Log4jConfiguration loggingConfiguration;
	
	private String configFile = "etc/disassembler.config";
	

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration( String[] args )
	{
		//
		// default configuration
		//
		// place we store all the base properties
		this.properties = new Properties();

		// logging configuration
		this.applicationLogger = null; // set on first access
		this.loggingConfiguration = new Log4jConfiguration( "disassembler" );
		this.loggingConfiguration.setConsoleOn( true );
		this.loggingConfiguration.setFileOn( false );
		this.loggingConfiguration.setLevel( "INFO" );

		// see if the user specified a config file on the command line before we process it
		checkArgsForConfigFile( args );
		loadConfigFile();
		
		// pull out any command line args and use them to override all values
		applyCommandLine( args );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private void loadConfigFile()
	{
		File configurationFile = new File( this.configFile );
		if( configurationFile.exists() )
		{
    		// configuration file exists, load the properties into it
    		Properties fileProperties = new Properties();
    		try
    		{
    			fileProperties.load( configurationFile.toURI().toURL().openStream() );
    		}
    		catch( Exception e )
    		{
    			throw new RuntimeException( "Problem parsing config file: "+e.getMessage(), e );
    		}
    		
    		// store the loaded configuration
    		this.properties.putAll( fileProperties );
		}
	}

	@Override
	public String toString()
	{
		return properties.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Global Configuration Options   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Get the Disruptor application logger. Will lazy-load configuration. */
	public Logger getDisassemblerLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;
		
		// check for any properties that may have been specified on command line to override
		loggingConfiguration.setLevel( properties.getProperty(KEY_LOG_LEVEL,"INFO") );
		
		this.loggingConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "disassembler" );
		return applicationLogger;
	}

	/**
	 * Return the type of analyzer we are going to run
	 */
	public AnalyzerType getAnalyzerType()
	{
		return AnalyzerType.fromValue( properties.getProperty(KEY_ANALYZER_TYPE,"none") );
	}
	
	public void setAnalyzerMode( AnalyzerType analyzer )
	{
		properties.setProperty( KEY_ANALYZER_TYPE, analyzer.name().toLowerCase() );
	}
	
	public void setAnalyzerMode( String analyzer ) throws IllegalArgumentException
	{
		// validate the given mode by turning in into its enumerated version first
		setAnalyzerMode( AnalyzerType.fromValue(analyzer) );
	}

	/**
	 * Get the file that contains the recording we are going to analyze
	 */
	public File getInFile()
	{
		return new File( properties.getProperty(KEY_INFILE,"duplicator.session") );
	}
	
	/**
	 * Set the recording file we should read from.
	 * 
	 * @param infilePath Path to the file
	 * @throws IllegalArgumentException If the file does not exist
	 */
	public void setInFile( String infilePath ) throws IllegalArgumentException
	{
		setInFile( new File(infilePath) );
	}

	/**
	 * Set the recording file we should read from.
	 * 
	 * @throws IllegalArgumentException If the file does not exist
	 */
	public void setInFile( File file ) throws IllegalArgumentException
	{
		if( file.exists() == false )
			throw new IllegalArgumentException( "Input file doesn't exist: "+file.getAbsolutePath() );

		properties.setProperty( KEY_INFILE, file.getAbsolutePath() );		
	}

	/**
	 * Get the file we'll write output to. By default we only write to STDOUT. If this is
	 * the case, null will be returned.
	 */
	public File getOutFile()
	{
		String path = properties.getProperty( KEY_OUTFILE, "STDOUT" );
		if( path.equalsIgnoreCase("STDOUT") )
			return null;
		else
			return new File( path );
	}

	/**
	 * Set the file to write output to. If the given value is `null`, we will flick back
	 * to `STDOUT` only.
	 */
	public void setOutFile( File file )
	{
		this.setOutFile( file.getAbsolutePath() );
	}

	/**
	 * Set the file to write output to. If the given value is `STDOUT` we will only write to
	 * standard out; otherwise we'll write to the given file.
	 */
	public void setOutFile( String outfile )
	{
		if( outfile.equalsIgnoreCase("STDOUT") )
			properties.remove( KEY_OUTFILE );
		else
			properties.setProperty( KEY_OUTFILE, outfile );
	}

	/**
	 * Return the format we should generate the output in
	 */
	public OutputFormat getOutputFormat()
	{
		return OutputFormat.fromValue( properties.getProperty(KEY_OUTPUT_FORMAT,"text") );
	}

	public void setOutputFormat( OutputFormat format )
	{
		properties.setProperty( KEY_OUTPUT_FORMAT, format.name().toLowerCase() );
	}

	//
	// Logging Settings
	//
	/**
	 * Set the log level to use. --NOTE-- Must be set before logger is first used as
	 * configuration is set at this time.
	 */
	public void setLogLevel( String level )
	{
		properties.setProperty( KEY_LOG_LEVEL, level );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Property Get & Set Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return _a copy_ of the underlying property set that is serving this configuration.
	 * Any changes to this set will not affect the configuration. 
	 */
	public final Properties getProperties()
	{
		return new Properties( this.properties );
	}
	
	protected void setProperty( String key, String value )
	{
		this.properties.setProperty( key, value );
	}
	
	protected String getProperty( String key, String defaultValue )
	{
		return this.properties.getProperty( key, defaultValue );
	}
	
	protected boolean hasProperty( String key )
	{
		return this.properties.containsKey( key );
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Command Line Argument Methods   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** If --config-file is in the args, load it into the local var for processing. We do
	    this separately so we can load the config file before the command line args, which
	    we do later so that they override all other values */
	private void checkArgsForConfigFile( String[] args )
	{
		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("--config-file") )
			{
				this.configFile = args[++i];
				return;
			}
		}
	}

	/**
	 * Process the whole command line and extract configuration information from it.
	 * 
	 * @throws DiscoException If any of the arguments are unknown or have missing params
	 */
	private final void applyCommandLine( String[] commandline ) throws DiscoException
	{
		// read the arguments into a queue so we can process them
		Queue<String> arguments = new LinkedList<>();
		for( String argument : commandline )
			arguments.add( argument );
		
		while( arguments.isEmpty() == false )
		{
			String argument = arguments.remove();

			try
			{
				// give the sub-class a chance to handle this
				if( applyCommandLineArgument(argument,arguments) )
					continue;

				// maybe it's the definition of an analyzer
				AnalyzerType type = AnalyzerType.fromArg( argument );
				if( type == null )
					throw new DiscoException( "Unknown argument: "+argument );
				else
					setAnalyzerMode( type );
			}
			catch( NoSuchElementException nse )
			{
				throw new DiscoException( "Argument ["+argument+"] missing a parameter" );
			}
		}
	}

	/**
	 * Assess the given command line argument and extract the configuration is represents.
	 * Return `true` if we processed it, `false` if it is unknown.
	 * 
	 * _NOTE: We expect sub-classes to override this so they can search for their own arguments._
	 * 
	 * @param argument  The argument under consideration
	 * @param arguments The rest of the command line, with anything up to this point stripped
	 * @return `true` if the argument was processed, `false` otherwise
	 */
	protected boolean applyCommandLineArgument( String argument, Queue<String> arguments )
	{
		if( argument.equalsIgnoreCase("--config-file") )
			this.configFile = arguments.remove();
		else if( argument.equalsIgnoreCase("--log-level") )
			this.setLogLevel( arguments.remove() );
		else if( argument.equalsIgnoreCase("--infile") )
			this.setInFile( arguments.remove() );
		else if( argument.equalsIgnoreCase("--outfile") )
			this.setOutFile( arguments.remove() );
		// Analyzer Mode
		else if( argument.equalsIgnoreCase("--analyzer") )
			this.setAnalyzerMode( arguments.remove() );
		else if( argument.equalsIgnoreCase("--enum") || argument.equalsIgnoreCase("--enumeration") )
			this.setAnalyzerMode( AnalyzerType.EnumUsage );
		else if( argument.equalsIgnoreCase("--tba") )
			;
		// Output Formats
		else if( argument.equalsIgnoreCase("--text") )
			this.setOutputFormat( OutputFormat.TEXT );
		else if( argument.equalsIgnoreCase("--json") )
			this.setOutputFormat( OutputFormat.JSON );
		else if( argument.equalsIgnoreCase("--csv") )
			this.setOutputFormat( OutputFormat.CSV );
		else
			return false;
		
		return true;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Usage Printing Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public static void printUsage()
	{
		System.out.println( "Disassembler - Packet analysis for DIS recordings" );
		System.out.println( "Usage: bin/disassembler [--args]" );
		System.out.println( "" );
		System.out.println( "  You can specify the type of analyzer to run using either the explicit" );
		System.out.println( "  --analyzer [value] command, or the shortcuts like --enum" );
		System.out.println( "" );

		System.out.println( "  --config-file         string   (optional)  Path to config file                        (default: etc/disassembler.config)" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]    (default: INFO)" );
		System.out.println( "  --infile              string   (optional)  Duplicator session file to analyze         (default: ./duplicator.session)" );
		System.out.println( "  --outfile             string   (optional)  STDOUT or path to file to output to        (default: STDOUT)" );
		System.out.println( "  --text                         (optional)  Print output as pretty-printed text (default)" );
		System.out.println( "  --json                         (optional)  Print output as JSON text" );
		System.out.println( "  --csv                          (optional)  Print output as CSV text" );
		System.out.println( "" );
		System.out.println( "  --analyzer            string   (required)  Analyzer module to run. Required only if not using options below." );
		System.out.println( "  --enum | --enumeration         (optional)  Run the Enumeration Summary Analyzer" );
		System.out.println( "  --pdu-count                    (optional)  Run the PDU Count Analyzer" );
		System.out.println( "" );

		System.out.println( "Enumerations Summary Arguments" );
		System.out.println( "  --order-by            string   (optional)  enumeration | site-id | pdu-count | obj-count (default: pdu-count)" );
		System.out.println( "  --ascending                    (optional)  results values should be in ascending order   (default: no)" );
		System.out.println( "  --descending                   (optional)  results values should be in descending order  (default: yes)" );
		System.out.println( "" );	
		System.out.println( "PDU Count Analyzer Arguments" );
		System.out.println( "  --order-by            string   (optional)  type | pdu-type | count | pdu-count           (default: pdu-count)" );
		System.out.println( "  --ascending                    (optional)  results values should be in ascending order   (default: no)" );
		System.out.println( "  --descending                   (optional)  results values should be in descending order  (default: yes)" );

		
		System.out.println( "" );	
	}
	

}
