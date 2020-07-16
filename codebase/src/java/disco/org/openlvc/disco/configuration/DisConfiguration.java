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
package org.openlvc.disco.configuration;

import java.util.Random;

/**
 * General DIS protocol settings that are applicable regardless of transport, sender or receiver
 * implementation.
 */
public class DisConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Keys for properties file
	private static final String PROP_EXERCISE_ID  = "disco.dis.exerciseId"; 
	private static final String PROP_SITE_ID      = "disco.dis.siteId";
	private static final String PROP_APP_ID       = "disco.dis.appId";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration parent;

	private short exerciseId;
	private int   siteId;
	private int   appId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected DisConfiguration( DiscoConfiguration parent )
	{
		this.parent = parent;

		this.exerciseId = -1; // -1 so we can lazy load
		this.siteId     = Integer.MIN_VALUE; // will cause lazy load
		this.appId      = Integer.MIN_VALUE; // will cause lazy load
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * @return Exercise ID to use for outgoing packets. Defaults to 1.
	 * 
	 * A setting of 0 indicates that we should accept traffic from anywhere.
	 */
	public short getExerciseId()
	{
		// lazy load
		if( this.exerciseId == -1 )
			this.exerciseId = Short.parseShort( parent.getProperty(PROP_EXERCISE_ID,"0") );

		return this.exerciseId;
	}

	/**
	 * Sets the exercise to use for all outgoing PDUs and the only one we'll accept for incoming.
	 */
	public void setExerciseId( short exerciseId )
	{
		parent.setProperty( PROP_EXERCISE_ID, ""+exerciseId );
		this.exerciseId = -1; // flag reset
	}

	/**
	 * @return The SiteID that should be used for PDUs. Range is 16-bit unsigned int.
	 */
	public int getSiteId()
	{
		// lazy load
		if( this.siteId == Integer.MIN_VALUE )
			this.siteId = Integer.parseInt( parent.getProperty(PROP_SITE_ID,"-1") );
		
		// generate if required
		if( siteId == -1 )
			siteId = new Random().nextInt( 65534 ); // keep away from ALL_SITES spec constant
		
		return siteId;
	}

	/**
	 * Set the Site ID for this application. Valid values are:
	 * <ul>
	 *   <li>0: No Site</li>
	 *   <li>1-65534: Regular range</li>
	 *   <li>-1: Randomly generate</li>
	 * </ul>
	 * 
	 * @param siteId The Site ID
	 */
	public void setSiteId( int siteId )
	{
		if( siteId < -1 || siteId > 65534 )
			throw new IllegalArgumentException( "Site ID valid range is 0-65534 (-1 to randomly generate)" );
		
		this.siteId = siteId;
	}
	
	/**
	 * @return The ApplicationID that should be used for PDUs. Range is 16-bit unsigned int.
	 */
	public int getAppId()
	{
		// lazy load
		if( this.appId == Integer.MIN_VALUE )
			this.appId = Integer.parseInt( parent.getProperty(PROP_APP_ID,"-1") );
		
		// generate if required
		if( this.appId == -1 )
			this.appId = new Random().nextInt( 65534 ); // keep away from ALL_APPLIC spec contant
		
		return appId;
	}
	
	/**
	 * Set the Application ID for this application. Valid values are:
	 * <ul>
	 *   <li>0: No Application</li>
	 *   <li>1-65534: Regular range</li>
	 *   <li>-1: Randomly generate</li>
	 * </ul>
	 * 
	 * @param siteId The Site ID
	 */
	public void setAppId( int appId )
	{
		if( appId < -1 || appId > 65534 )
			throw new IllegalArgumentException( "App ID valid range is 0-65534 (-1 to randomly generate)" );
		
		this.appId = appId;
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
