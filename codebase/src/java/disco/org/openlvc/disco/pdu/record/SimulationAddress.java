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
package org.openlvc.disco.pdu.record;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * An Entity's simulation address shall be specified by a Simulation Address 
 * Record. A Simulation Address record shall consist of the Site ID number and 
 * the Application ID number.
 */
public class SimulationAddress implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final int ALL_SITES = 0xFFFF;
	public static final int ALL_APPLIC = 0xFFFF;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int siteIdentifier;
	private int applicationIdentifier;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SimulationAddress()
	{
		this( 0, 0 );
	}
	
	public SimulationAddress( int siteIdentifier, int applicationIdentifier )
	{
		this.siteIdentifier = siteIdentifier;
		this.applicationIdentifier = applicationIdentifier;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;

		if( other instanceof SimulationAddress )
		{
			SimulationAddress asSimulationAddress = (SimulationAddress)other;
			if( asSimulationAddress.applicationIdentifier == this.applicationIdentifier &&
				asSimulationAddress.siteIdentifier == this.siteIdentifier )
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public SimulationAddress clone()
	{
		return new SimulationAddress( siteIdentifier, applicationIdentifier );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		siteIdentifier = dis.readUI16();
		applicationIdentifier = dis.readUI16();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI16( siteIdentifier );
		dos.writeUI16( applicationIdentifier );
    }
	
	@Override
    public final int getByteLength()
	{
		return 4; // DisSizes.UI16_SIZE * 2;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getSiteIdentifier()
    {
    	return siteIdentifier;
    }

	public void setSiteIdentifier( int siteIdentifier )
    {
		this.siteIdentifier = siteIdentifier;
    }

	public int getApplicationIdentifier()
    {
    	return applicationIdentifier;
    }

	public void setApplicationIdentifier( int applicationIdentifier )
    {
		this.applicationIdentifier = applicationIdentifier;
    }
	
	public String toString()
	{
		return this.siteIdentifier+"-"+this.applicationIdentifier;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static SimulationAddress fromString( String value )
	{
		String[] array = value.split( "," );
		if( array.length != 2 )
			throw new IllegalArgumentException( "Not a valid simulation address (must be 'siteId-appId'): "+value );
		
		return new SimulationAddress( Integer.valueOf(array[0]), Integer.valueOf(array[1]) );
	}

}
