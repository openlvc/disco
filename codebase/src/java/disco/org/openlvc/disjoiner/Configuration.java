/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.disjoiner;

import java.io.File;
import java.util.Properties;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;
import org.openlvc.disco.utils.CommandList;

public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String KEY_LOG_LEVEL       = "disjoiner.loglevel";
	public static final String KEY_LOG_FILE        = "disjoiner.logfile";

	// DIS Settings
	public static final String KEY_DIS_NIC         = "disjoiner.dis.nic";
	public static final String KEY_DIS_ADDRESS     = "disjoiner.dis.address";
	public static final String KEY_DIS_PORT        = "disjoiner.dis.port";
	public static final String KEY_DIS_EXID        = "disjoiner.dis.exerciseId";
	public static final String KEY_DIS_LOGLEVEL    = "disjoiner.dis.loglevel";
	public static final String KEY_DIS_LOGFILE     = "disjoiner.dis.logfile";

	// HLA Settings
	public static final String KEY_HLA_RTI_PROVIDER = "disjoiner.hla.rti.provider";
	public static final String KEY_HLA_RTI_DIR      = "disjoiner.hla.rti.installdir";
	public static final String KEY_HLA_RTI_LOCAL    = "disjoiner.hla.rti.localSettings";
	public static final String KEY_HLA_FEDERATION   = "disjoiner.hla.federationName";
	public static final String KEY_HLA_FEDERATE     = "disjoiner.hla.federateName";
	public static final String KEY_HLA_FEDERATE_RND = "disjoiner.hla.randomizeFederateName";
	public static final String KEY_HLA_CREATE_FED   = "disjoiner.hla.createFederation";
	
	public static final String KEY_HLA_LOGLEVEL    = "disjoiner.hla.loglevel";
	public static final String KEY_HLA_LOGFILE     = "disjoiner.hla.logfile";
	

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;
	private String configFile = "etc/disjoiner.config";
	
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
		this.configFile = "etc/disjoiner.config";

		// see if the user specified a config file on the command line before we process it
		this.checkArgsForConfigFile( args );
		this.loadConfigFile();
		
		// pull out any command line args and use them to override all values
		this.applyCommandLine( args );
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////
	// DIS Settings          ////////
	/////////////////////////////////
	public String getDisNic()
	{
		return properties.getProperty( KEY_DIS_NIC, "SITE_LOCAL" );
	}
	
	public void setDisNic( String iface )
	{
		this.properties.put( KEY_DIS_NIC, iface );
	}
	
	public String getDisAddress()
	{
		return properties.getProperty( KEY_DIS_ADDRESS, "BROADCAST" );
	}
	
	public void setDisAddress( String address )
	{
		this.properties.put( KEY_DIS_ADDRESS, address );
	}
	
	public int getDisPort()
	{
		return Integer.parseUnsignedInt( properties.getProperty(KEY_DIS_PORT,"3000") );
	}

	public void setDisPort( int port )
	{
		this.properties.put( KEY_DIS_PORT, ""+port );
	}

	public void setDisExerciseId( String id )
	{
		this.properties.put( KEY_DIS_EXID, id );
	}
	
	public short getDisExerciseId()
	{
		return Short.parseShort( properties.getProperty(KEY_DIS_EXID,"1") );
	}
	
	public String getDisLogLevel()
	{
		return this.properties.getProperty( KEY_DIS_LOGLEVEL, "INFO" );
	}
	
	public void setDisLogLevel( String level )
	{
		if( level == null )
			throw new IllegalArgumentException( "Log level cannot be null" );
		
		level = level.trim();
		if( level.equals("OFF") ||
			level.equals("FATAL") ||
			level.equals("WARN") ||
			level.equals("INFO") ||
			level.equals("DEBUG") ||
			level.equals("TRACE") )
		{
			this.properties.setProperty( KEY_DIS_LOGLEVEL, level );
		}
		else
		{
			throw new IllegalArgumentException( level+" is not a valid log level" );
		}
	}
	
	public String getDisLogFile()
	{
		return this.properties.getProperty( KEY_DIS_LOGFILE, "logs/disjoiner.dis.log" );
	}
	
	public void setDisLogFile( File path )
	{
		if( path != null )
			this.properties.setProperty( KEY_DIS_LOGFILE, path.getAbsolutePath() );
	}
	
	protected DiscoConfiguration getDisConfiguration()
	{
		DiscoConfiguration dis = new DiscoConfiguration();
		dis.setConnection( "udp" );
		dis.getUdpConfiguration().setNetworkInterface( getDisNic() );
		dis.getUdpConfiguration().setAddress( getDisAddress() );
		dis.getUdpConfiguration().setPort( getDisPort() );
		dis.getDisConfiguration().setExerciseId( getDisExerciseId() );
		
		dis.getLoggingConfiguration().setAppName( "dis>>hla" );
		dis.getLoggingConfiguration().setLevel( getDisLogLevel() );
		dis.getLoggingConfiguration().setFile( getDisLogFile() );
		
		return dis;
	}
	

	/////////////////////////////////
	// HLA Settings          ////////
	/////////////////////////////////
	public void setHlaRtiProvider( RtiProvider provider )
	{
		this.properties.put( KEY_HLA_RTI_PROVIDER, provider.name() );
	}

	public RtiProvider getHlaRtiProvider()
	{
		return RtiProvider.valueOf( properties.getProperty(KEY_HLA_RTI_PROVIDER,"Portico") );
	}
	
	public String getHlaRtiInstallDir()
	{
		return this.properties.getProperty( KEY_HLA_RTI_DIR, "./" );
	}
	
	public String getHlaRtiLocalSettings()
	{
		return this.properties.getProperty( KEY_HLA_RTI_LOCAL, "" );
	}
	
	
	public String getHlaFederationName()
	{
		return this.properties.getProperty( KEY_HLA_FEDERATION, "Disjoiner" );
	}
	
	public String getHlaFederateName()
	{
		return this.properties.getProperty( KEY_HLA_FEDERATE, "Disjoiner" );
	}

	public boolean isHlaRandomizeFederateName()
	{
		return Boolean.valueOf( properties.getProperty(KEY_HLA_FEDERATE_RND,"true") );
	}
	
	public boolean isHlaCreateFederation()
	{
		return Boolean.valueOf( properties.getProperty(KEY_HLA_CREATE_FED,"true") );
	}
	
	public String getHlaLogLevel()
	{
		return this.properties.getProperty( KEY_HLA_LOGLEVEL, "INFO" );
	}
	
	public void setHlaLogLevel( String level )
	{
		if( level == null )
			throw new IllegalArgumentException( "Log level cannot be null" );
		
		level = level.trim();
		if( level.equals("OFF") ||
			level.equals("FATAL") ||
			level.equals("WARN") ||
			level.equals("INFO") ||
			level.equals("DEBUG") ||
			level.equals("TRACE") )
		{
			this.properties.setProperty( KEY_HLA_LOGLEVEL, level );
		}
		else
		{
			throw new IllegalArgumentException( level+" is not a valid log level" );
		}
	}
	
	public String getHlaLogFile()
	{
		return this.properties.getProperty( KEY_HLA_LOGFILE, "logs/disjoiner.hla.log" );
	}
	
	public void setHlaLogFile( File path )
	{
		if( path != null )
			this.properties.setProperty( KEY_HLA_LOGFILE, path.getAbsolutePath() );
	}
	
	protected DiscoConfiguration getHlaConfiguration()
	{
		DiscoConfiguration hla = new DiscoConfiguration();
		hla.setConnection( "rpr" );
		hla.getRprConfiguration().setRtiProvider( getHlaRtiProvider() );
		hla.getRprConfiguration().setRtiInstallDir( getHlaRtiInstallDir() );
		hla.getRprConfiguration().setLocalSettings( getHlaRtiLocalSettings() );

		hla.getRprConfiguration().setFederationName( getHlaFederationName() );
		hla.getRprConfiguration().setFederateName( getHlaFederateName() );
		hla.getRprConfiguration().setCreateFederation( isHlaCreateFederation() );
		hla.getRprConfiguration().setRandomizeFedName( isHlaRandomizeFederateName() );

		hla.getLoggingConfiguration().setAppName( "hla>>dis" );
		hla.getLoggingConfiguration().setLevel( getHlaLogLevel() );
		hla.getLoggingConfiguration().setFile( getHlaLogFile() );
		
		return hla;
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
	 * Apply the given command line args to override any defaults that we have
	 */
	private void applyCommandLine( String[] args ) throws DiscoException
	{
		CommandList list = new CommandList( args );
		while( list.hasMore() )
		{
			String argument = list.next();
			if( argument.equalsIgnoreCase("--config-file") )
				this.configFile = list.next();
			// DIS
			else if( argument.equalsIgnoreCase("--dis-address") )
				this.setDisAddress( list.next() );
			else if( argument.equalsIgnoreCase("--dis-port") )
				this.setDisPort( Integer.parseInt(list.next()) );
			else if( argument.equalsIgnoreCase("--dis-nic") || argument.equalsIgnoreCase("--dis-interface") )
				this.setDisNic( list.next() );
			else if( argument.equalsIgnoreCase("--dis-exerciseId")  || argument.equalsIgnoreCase("--dis-exercise-id") )
				this.setDisExerciseId( list.next() );
			else if( argument.equalsIgnoreCase("--dis-log-level") )
				this.setDisLogLevel( list.next() );
			else if( argument.equalsIgnoreCase("--dis-log-file") )
				this.setDisLogFile( new File(list.next()) );
			// HLA
			
			// BOTH
			else if( argument.equalsIgnoreCase("--log-level") )
			{
				String temp = list.next();
				this.setDisLogLevel( temp );
				this.setHlaLogLevel( temp );
			}
			else if( argument.equalsIgnoreCase("--log-file") )
			{
				File temp = new File( list.next() );
				this.setDisLogFile( temp );
				this.setHlaLogFile( temp );
			}
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "disjoiner - Bridge DIS and HLA/RPR Networks" );
		System.out.println( "Usage: bin/disjoiner [--args]" );
		System.out.println( "" );

		System.out.println( "  --config-file         string   (optional)  Location of config file                    (default: etc/disjoiner.config)" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE] for both DIS/HLA sides" );
		System.out.println( "  --log-file            string   (optional)  Location of log file for both DIS/HLA sides" );
		System.out.println( "" );
		System.out.println( "DIS Settings" );
		System.out.println( "  --dis-exercise-id     short    (optional)  Ex ID to send in outgoing and only recv on (default: 1)" );
		System.out.println( "  --dis-address         string   (optional)  Where to send DIS traffic, or BROADCAST    (default: BROADCAST)" );
		System.out.println( "  --dis-port            integer  (optional)  Port for DIS traffic                       (default: 3000)" );
		System.out.println( "  --dis-interface       string   (optional)  NIC to use. Address or a special symbol:   (default: SITE_LOCAL)" );
		System.out.println( "                                             LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL" );
		System.out.println( "  --dis-log-level       string   (optional)  Set log level for DIS side only            (default: INFO)" );
		System.out.println( "  --dis-log-file        string   (optional)  Set log file for DIS side only             (default: logs/disjoiner.dis.log)" );
		System.out.println( "" );
		System.out.println( "HLA Network Settings" );
		System.out.println( "  --hla-rti-provider    string   (optional)  Portico or Pitch                           (default: Portico)" );
		System.out.println( "  --hla-rti-dir         string   (optional)  Directory where RTI is installed           (default: RTI Specific)" );
		System.out.println( "  --hla-local-settings  string   (optional)  Setting string given to RTI on connection" );
		System.out.println( "  --hla-federation      string   (optional)  Name of federation to join                 (default: Disjoiner)" );
		System.out.println( "  --hla-federate        string   (optional)  Name of federate to join as                (default: Disjoiner)" );
		System.out.println( "  --hla-log-level       string   (optional)  Set log level for HLA side only            (default: INFO)" );
		System.out.println( "  --hla-log-file        string   (optional)  Set log file for HLA side only             (default: logs/disjoiner.hla.log)" );
		System.out.println( "" );
	}
}

