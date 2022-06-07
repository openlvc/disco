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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.ActionRequestMapper;
import org.openlvc.disco.connection.rpr.mappers.ActionResponseMapper;
import org.openlvc.disco.connection.rpr.mappers.DataMapper;
import org.openlvc.disco.connection.rpr.mappers.DataQueryMapper;
import org.openlvc.disco.connection.rpr.mappers.EmitterBeamMapper;
import org.openlvc.disco.connection.rpr.mappers.EmitterSystemMapper;
import org.openlvc.disco.connection.rpr.mappers.EntityStateMapper;
import org.openlvc.disco.connection.rpr.mappers.SetDataMapper;
import org.openlvc.disco.connection.rpr.mappers.SignalMapper;
import org.openlvc.disco.connection.rpr.mappers.TransmitterMapper;
import org.openlvc.disco.utils.FileUtils;

/**
 * Contains the core configuration data for the RPR connection type that will translate between
 * native Disco DIS and HLA, currently based on RPR.
 */
public class RprConfiguration
{
	//----------------------------------------------------------
	//                       ENUMERATIONS
	//----------------------------------------------------------
	public enum RtiProvider
	{
		Portico("C:/Program Files/Portico", "lib/portico.jar" ),
		Pitch("C:/Program Files/prti1516e", "lib/prtifull.jar" ),
		Mak("C:/MAK", "lib/hla.jar" );
		
		private String defaultInstallDir;
		private String defaultJarPath;
		private RtiProvider( String defaultDir, String defaultJarPath )
		{
			this.defaultInstallDir = defaultDir;
			this.defaultJarPath = defaultJarPath;
		}

		/**
		 * @return The path to the default location that the RTI is installed in to.
		 */
		public String getDefaultInstallDirectory()
		{
			return this.defaultInstallDir;
		}

		/**
		 * @return The relative path from the RTI install directory to where the main jar file
		 *         for the RTI.
		 */
		public String getDefaultJarPath()
		{
			return this.defaultJarPath;
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
	private static final String PROP_RPR_HEARTBEAT_TIME = "disco.rpr.heartbeatPeriod";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration parent;
	
	// FOM-specific values. Careful to manage with DIS properties so they don't ignore each other
	private List<URL> fomModules;
	private List<AbstractMapper> fomMappers;
	
	private List<String> extensionModules;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected RprConfiguration( DiscoConfiguration parent )
	{
		this.parent = parent;
		
		this.fomModules = new ArrayList<>();
		this.fomMappers = new ArrayList<>();
		
		this.extensionModules = new ArrayList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a set of paths that should reference the RTI install directory/jars. We take a bit of
	 * a sledgehammer approach and include a bunch of _possible_ locations on here, just to try and
	 * be sure. These include:
	 * 
	 * <ol>
	 *   <li>Any value from the RTI_HOME environment variable, extended with a relative path to
	 *       a jar file based on the RTI provider.</li>
	 *   <li>Any explicitly provided RTI Install Dir from the user</li>
	 *   <li>Any explicitly provided RTI Install Dir from the user, with an additional path relative
	 *       to that location that points to the RTI jar (based on the Provider)</li>
	 *   <li>The default installation directory as according to the Provider</li>
	 *   <li>The current working directory</li>
	 * </ol>
	 * 
	 * @return List of locations to use to extend classpath to try and make sure we get the RTI
	 *         libraries available to the classloader
	 */
	public List<File> getRtiPathExtension()
	{
		List<File> paths = new ArrayList<>();

		// 0. Get our RTI provider
		RtiProvider provider = getRtiProvider();
		String jarpath = provider.getDefaultJarPath();
		
		// 1. Check for the RTI_HOME environment variable
		String rtihome = System.getenv( "RTI_HOME" );
		if( rtihome != null )
			paths.add( new File(rtihome,jarpath) );

		// 2. Add the user provided directory
		String userdir = getRtiInstallDir();
		paths.add( new File(userdir,jarpath) );
		
		// 3. Add the RTI's default install directory
		String defaultdir = provider.getDefaultInstallDirectory();
		paths.add( new File(defaultdir,jarpath) );
		
		// 4. Add the current working directory
		paths.add( new File("./",jarpath) );
		
		// 5. Add Mak libraries if installed
		String makHome = System.getenv( "Mak_RTIDIR" );
		if( makHome != null )
		{
			paths.add( new File(rtihome, "lib/java"));
			paths.add( new File(rtihome, "lib"));
			paths.add( new File(rtihome, "bin"));
		}
		
		return paths;
	}
	
	public void setRtiProvider( RtiProvider provider )
	{
		parent.setProperty( PROP_RTI_PROVIDER, provider.name() );
		
		// Mak is picky, best to clear and reload the FOMs - now that Mak is set
		// they will be extracted out from the jar so it can load them
		if( provider == RtiProvider.Mak )
		{
			this.fomModules.clear();
			loadDefaultModules();
			
			// re-add extension modules
			registerExtensionModules( this.extensionModules.toArray(new String[0]) );
		}
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

		if( rtidir.equalsIgnoreCase("<default>") )
			parent.setProperty( PROP_RTI_INSTALL_DIR, getRtiProvider().getDefaultInstallDirectory() );
		else
			parent.setProperty( PROP_RTI_INSTALL_DIR, rtidir );
	}

	/**
	 * @return The path to the RTI install directory, defaulting to the default directory of the
	 *         provider if no explicit location is set.
	 */
	public String getRtiInstallDir()
	{
		String value = parent.getProperty( PROP_RTI_INSTALL_DIR,
		                                   getRtiProvider().getDefaultInstallDirectory() );
		
		if( value.equalsIgnoreCase("<default>") )
			return getRtiProvider().getDefaultInstallDirectory();
		else
			return value;
	}
	
	
	/**
	 * @return The path to the RTI install directory, defaulting to the default directory of the
	 *         provider if no explicit location is set.
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private String getRtiInstallDirOld()
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

	/**
	 * DIS requires heartbeats, HLA does not. This value is the max period of time we will allow
	 * a PDU to _not_ be sent for an HLA discovered object before we artifically generate one.
	 * This only impacts objects discovered from/originating from the HLA. If 0, the heartbeater
	 * is disabled.
	 * 
	 * @return The max period between PDU heartbeats in milliseconds
	 */
	public long getHeartbeatPeriod()
	{
		return Long.parseLong(parent.getProperty(PROP_RPR_HEARTBEAT_TIME,"30000") );
	}

	/**
	 * DIS requires heartbeats, HLA does not. This value is the max period of time we will allow
	 * a PDU to _not_ be sent for an HLA discovered object before we artifically generate one.
	 * This only impacts objects discovered from/originating from the HLA. If set to 0, the
	 * heartbeater is disabled.
	 * 
	 * @param period The max period between PDUs before we generate an artifical update. Milliseconds.
	 */
	public void setHeartbeatPeriod( long period )
	{
		parent.setProperty( PROP_RPR_HEARTBEAT_TIME, ""+period );
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////
	/// FOM/Mapper Extensions   /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the list of all registered FOM modules. This is the union of the set of default
	 * modules plus any added through {@link #registerExtensionModules(File...)}.
	 * <p/>
	 * If you have a custom FOM Mapper, remember to make sure any required FOM Module is also
	 * getting loaded.
	 * 
	 * @return URL[] pointing to all FOM modules that should be loaded and provided when creating
	 *         or joining a federation.
	 */
	public URL[] getRegisteredFomModules()
	{
		if( fomModules.isEmpty() )
			loadDefaultModules();
		
		return this.fomModules.toArray( new URL[]{} );
	}
	
	/**
	 * Register the given paths as additional FOM modules that should be loaded in addition to
	 * the default set. If you have custom FOM Mappers that want to deal with custom HLA types,
	 * make sure you have already registered the FOM modules they need.
	 * <p/>
	 * This method will check each path to make sure it is valid before proceeding. We first look
	 * for the path as a file, and if it exists then we turn it into an absolute path and use that
	 * (if it isn't already). If the path cannot be resolved as a file, we try to resolve it as a
	 * system resource on the classpath.
	 * <p/>
	 * If the path does not resolve under either option, an exception is thrown.
	 * 
	 * @param paths The list of paths to add as extension modules.
	 * @throws DiscoException If the path does not point to a file or a system resource
	 */
	public void registerExtensionModules( String... paths )
	{
		if( fomModules.isEmpty() )
			loadDefaultModules();
		
		ClassLoader loader = getClass().getClassLoader();
		for( String path : paths )
		{
			File file = new File( path );
			if( file.exists() )
			{
				try
				{
					fomModules.add( file.toURI().toURL() );
				}
				catch( Exception e )
				{
					throw new DiscoException( e.getMessage(), e );
				}
			}
			else if( getRtiProvider() == RtiProvider.Mak )
			{
				// if we're using Mak and it's not an external module, must be dcss
				createMakFomFiles( new String[]{path}, new File("hla/dcss") );
			}
			else
			{
				URL temp = loader.getResource( path );
				if( temp != null )
					fomModules.add( temp );
				else
					// can't find it!
					throw new DiscoException( "Cannot find extension module: "+path );
			}
			
			//keeping track of these helps if at some point we switch to using Mak
			if( !this.extensionModules.contains(path) )
			{
				this.extensionModules.add( path );
			}
		}
	}

	/**
	 * Load the default set of FOM Modules that should always be present.
	 */
	private void loadDefaultModules()
	{
		// List of defaults
		String[] defaultModules = new String[] {
			"hla/rpr2/HLAstandardMIM.xml",
			"hla/rpr2/RPR-Foundation_v2.0.xml",
			"hla/rpr2/RPR-Base_v2.0.xml",
			"hla/rpr2/RPR-Communication_v2.0.xml",
			"hla/rpr2/RPR-DER_v2.0.xml",
			"hla/rpr2/RPR-Enumerations_v2.0.xml",
			"hla/rpr2/RPR-Physical_v2.0.xml",
			"hla/rpr2/RPR-SIMAN_v2.0.xml",
			"hla/rpr2/RPR-Warfare_v2.0.xml"
		};
		
		if( getRtiProvider() == RtiProvider.Mak )
		{
			createMakFomFiles( defaultModules, new File("hla/rpr2") );
		}
		else
		{
			// Make sure we can find each default
			ClassLoader loader = getClass().getClassLoader();
			for( String module : defaultModules )
			{
				URL url = loader.getResource( module );
				if( url == null  )
					throw new DiscoException( "Could not find FOM module: "+url );
				else
					fomModules.add( url );
			}
		}
	}
	
	/**
	 * Mak refuses to load anything from a jar file, as such need to copy any
	 * module files in the jar outside into a folder where Mak can load them.
	 * This method does that, creating a folder for the modules in the destDir
	 * copying in the given module files, and adding them to the list of module URLs
	 */
	private void createMakFomFiles( String[] modules, File destDir )
	{
		ArrayList<String> modulesList = new ArrayList<>();
		for( String module : modules )
		{
			// Mak doesn't like this module being included
			if( module.contains("HLAstandardMIM") )
				continue;
				
			modulesList.add(module);
		}
		
		// add the URLs of the unjar'd files
		fomModules.addAll( FileUtils.extractFilesFromJar(modulesList, destDir, getClass().getClassLoader()) );
	}
	
	////////////////////////////////////////
	/// FOM Mappers   //////////////////////
	////////////////////////////////////////
	/**
	 * This method will look up the list of configured mappers and INSTANTIATE each of them
	 * in turn, returning an array of new mapper instances. The mapper must have a no-arg
	 * @return
	 */
	public AbstractMapper[] getRegisteredFomMappers()
	{
		if( fomMappers.isEmpty() )
			loadDefaultMappers();
		
		return this.fomMappers.toArray( new AbstractMapper[]{} );
	}

	/**
	 * Register a custom mapper with the configuration. On connection initialization, all default
	 * and extension mappers will be brought into service through an initialization call before
	 * being passed messages.
	 * 
	 * @param mappers The set of customer mappers that should be added to the list of defaults
	 */
	public void registerExtensionMappers( AbstractMapper... mappers )
	{
		if( fomMappers.isEmpty() )
			loadDefaultMappers();
		
		for( AbstractMapper mapper : mappers )
			fomMappers.add( mapper );
	}
	
	public void clearExtensionMappers()
	{
		this.fomMappers.clear();
	}
	
	/**
	 * Create the set of default mappers that should always be present for a RPR federation
	 */
	private final void loadDefaultMappers()
	{
		// Entity & Warfare
		this.fomMappers.add( new EntityStateMapper() );
		// Communications
		this.fomMappers.add( new TransmitterMapper() );
		this.fomMappers.add( new SignalMapper() );
		// Emissions
		this.fomMappers.add( new EmitterSystemMapper() );
		this.fomMappers.add( new EmitterBeamMapper() );
		// Sim Mgmt
		this.fomMappers.add( new DataQueryMapper() );
		this.fomMappers.add( new DataMapper() );
		this.fomMappers.add( new SetDataMapper() );
		this.fomMappers.add( new ActionRequestMapper() );
		this.fomMappers.add( new ActionResponseMapper() );
	}
	
	
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
