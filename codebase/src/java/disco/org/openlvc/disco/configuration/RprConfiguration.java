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
package org.openlvc.disco.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RprConfiguration
{
	//----------------------------------------------------------
	//                       ENUMERATIONS
	//----------------------------------------------------------
	public enum RtiProvider
	{
		Portico("C:\\Program Files\\Portico"),
		Pitch("C:\\Program Files\\prti1516e");
		
		private String defaultInstallDir;
		private RtiProvider( String defaultDir )
		{
			this.defaultInstallDir = defaultDir;
		}
		
		public String getDefaultInstallDirectory()
		{
			return this.defaultInstallDir;
		}
	}

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Keys for properties file
	private static final String PROP_RTI_PROVIDER       = "disco.rpr.rti.provider";
	private static final String PROP_RTI_INSTALL_DIR    = "disco.rpr.rti.installdir";
	private static final String PROP_FEDERATION         = "disco.rpr.federation";
	private static final String PROP_FEDERATE           = "disco.rpr.federate";
	private static final String PROP_FEDERATE_TYPE      = "disco.rpr.federateType";
	private static final String PROP_CREATE_FEDERATION  = "disco.rpr.createFederation";
	private static final String PROP_RANDOMIZE_FED_NAME = "disco.rpr.randomizeFedName";
	private static final String PROP_LOCAL_SETTINGS     = "disco.rpr.localSettings";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration parent;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected RprConfiguration( DiscoConfiguration parent )
	{
		this.parent = parent;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public List<File> getRtiPathExtension()
	{
		List<File> paths = new ArrayList<>();

		// Add the directory as configured by the user
		String rtihome = getRtiInstallDir();
		if( rtihome.equals("") )
			rtihome = ".";
		
		paths.add( new File(rtihome) );
		

		// Add in some defaults for when we're trying to embed the RTI in our local distribution
		switch( getRtiProvider() )
		{
			case Portico:
				paths.add( new File(rtihome+"/lib","portico.jar") );
				break;
			case Pitch:
				paths.add( new File(rtihome+"/lib","prtifull.jar") );
				break;
		}

		// Add in the RTI provider default
		paths.add( new File(getRtiProvider().getDefaultInstallDirectory()) );
		
		return paths;
	}
	
	public void setRtiProvider( RtiProvider provider )
	{
		parent.setProperty( PROP_RTI_PROVIDER, provider.name() );
	}
	
	public void setRtiProvider( String provider )
	{
		setRtiProvider( RtiProvider.valueOf(provider) ); 
	}
	
	public RtiProvider getRtiProvider()
	{
		return RtiProvider.valueOf( parent.getProperty(PROP_RTI_PROVIDER,RtiProvider.Portico.name()) );
	}

	public void setRtiInstallDir( String rtidir )
	{
		if( rtidir == null || rtidir.trim().equals("") )
			return;

		parent.setProperty( PROP_RTI_INSTALL_DIR, rtidir );
	}

	/**
	 * @return The path to the RTI install directory, defaulting to the default directory of the
	 *         provider if no explicit location is set.
	 */
	public String getRtiInstallDir()
	{
		// Extract data from the various locations that we might use
		String userPath = parent.getProperty( PROP_RTI_INSTALL_DIR, "" ).trim();
		String envPath = System.getenv( "RTI_HOME" );
		String providerPath = getRtiProvider().getDefaultInstallDirectory();
		
		
		// Step 1. If the user has given us a path, use it (UNLESS it is ./)
		if( userPath.equals("") == false &&
			userPath.equals("./") == false &&
			userPath.equals(".") == false )
		{
			return userPath;
		}
		
		// Step 2. If the environment variable is set, fall back to it
		if( envPath != null && !envPath.trim().equals("") )
			return envPath;
		
		// Step 3. If no other value is provided, fall back to the provider path but ONLY
		//         if it exists on the file system
		File providerFile = new File( providerPath );
		if( providerFile.exists() )
			return providerPath;
		
		// Step 4. If the provider default  doesn't exist, and the user HAS provided a value
		//         (even if it's ./), just use that, otherwise fall back to the non-existant
		//         provider path.
		return parent.getProperty( PROP_RTI_INSTALL_DIR, providerFile.getAbsolutePath() );
	}

	/**
	 * @return The value of {@link #getRtiInstallDir()} as a file.
	 */
	public File getRtiInstallDirFile()
	{
		return new File( getRtiInstallDir() );
	}
	
	//
	// General HLA Properties
	//
	public String getFederationName()
	{
		return parent.getProperty( PROP_FEDERATION, "Disco" );
	}
	
	public void setFederationName( String federationName )
	{
		parent.setProperty( PROP_FEDERATION, federationName );
	}

	public String getFederateName()
	{
		return parent.getProperty( PROP_FEDERATE, "Disco" );
	}

	public void setFederateName( String federateName )
	{
		parent.setProperty( PROP_FEDERATE, federateName );
	}
	
	public String getFederateType()
	{
		return parent.getProperty( PROP_FEDERATE_TYPE, "OpenLVC Disco Federate" );
	}

	public void setFederateType( String federateType )
	{
		parent.setProperty( PROP_FEDERATE_TYPE, federateType );
	}

	public boolean isCreateFederation()
	{
		return Boolean.valueOf( parent.getProperty(PROP_CREATE_FEDERATION,"true") );
	}
	
	public void setCreateFederation( boolean createFederation )
	{
		parent.setProperty( PROP_CREATE_FEDERATION, ""+createFederation );
	}

	/**
	 * If this is true, the federate name will be randomized ONLY if we fail to join
	 * the federation on the first go. It will be randomized by appending a 5-digit
	 * number to the back of the name.
	 * 
	 * @return True if we should randomize the fed name on join failure, false otherwise
	 */
	public boolean isRandomizeFedName()
	{
		return parent.isProperty( PROP_RANDOMIZE_FED_NAME, false );
	}

	/**
	 * Set to true if you want to randomize the fed name if a federate fails to join
	 * a federation because the name is taken. If true, and we fail to join the federation
	 * because of a name clash, we append a 5-digit random number to the end of the name
	 * and keep trying until we get in.
	 * 
	 * @param value True if we should randomize the federate name when needed. False to always
	 *              use the name that is configured.
	 */
	public void setRandomizeFedName( boolean value )
	{
		parent.setProperty( PROP_RANDOMIZE_FED_NAME, ""+value );
	}

	/**
	 * The local settings designator can be used to pass initization information to an LRC
	 * at the time of connection. In some RTI implementations this can be used to specify
	 * the network location of the RTI or other important bootstarpping configuration options.
	 * 
	 * @return The local settings to pass to the RTI at connection time. Defaults to an empty string.
	 */
	public String getLocalSettings()
	{
		return parent.getProperty( PROP_LOCAL_SETTINGS, "" );
	}
	
	/**
	 * The local settings designator can be used to pass initization information to an LRC
	 * at the time of connection. In some RTI implementations this can be used to specify
	 * the network location of the RTI or other important bootstarpping configuration options.
	 * 
	 * @param localSettings The local settings string to pass when connecting to the RTI.
	 */
	public void setLocalSettings( String localSettings )
	{
		if( localSettings != null )
			parent.setProperty( PROP_LOCAL_SETTINGS, localSettings );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
