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
	public enum RtiProvider { Portico, Pitch; }

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
		
		String rtihome = getRtiInstallDir();
		switch( getRtiProvider() )
		{
			case Portico:
				paths.add( new File(rtihome+"/lib","portico.jar") );
				break;
			case Pitch:
				paths.add( new File(rtihome+"/lib","prtifull.jar") );
				break;
		}

		return paths;
	}
	
	public void setRtiProvider( RtiProvider provider )
	{
		parent.setProperty( PROP_RTI_PROVIDER, provider.name() );
	}
	
	public RtiProvider getRtiProvider()
	{
		return RtiProvider.valueOf( parent.getProperty(PROP_RTI_PROVIDER,RtiProvider.Portico.name()) );
	}

	public void setRtiInstallDir( String rtidir )
	{
		parent.setProperty( PROP_RTI_INSTALL_DIR, rtidir );
	}
	
	public String getRtiInstallDir()
	{
		String defaultPath = System.getenv( "RTI_HOME" );
		if( defaultPath == null )
			defaultPath = "";

		return parent.getProperty( PROP_RTI_INSTALL_DIR, defaultPath );
	}

	//
	// General HLA Properties
	//
	public String getFederationName()
	{
		return parent.getProperty( PROP_FEDERATION, "" );
	}
	
	public void setFederationName( String federationName )
	{
		parent.setProperty( PROP_FEDERATION, federationName );
	}

	public String getFederateName()
	{
		return parent.getProperty( PROP_FEDERATE, "Disco Federate" );
	}

	public void setFederateName( String federateName )
	{
		parent.setProperty( PROP_FEDERATE, federateName );
	}
	
	public String getFederateType()
	{
		return parent.getProperty( PROP_FEDERATE_TYPE, "Disco Federate" );
	}

	public void setFederateType( String federateType )
	{
		parent.setProperty( PROP_FEDERATE_TYPE, federateType );
	}

	public boolean isCreateFederation()
	{
		return Boolean.valueOf( parent.getProperty(PROP_CREATE_FEDERATION,"false") );
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
