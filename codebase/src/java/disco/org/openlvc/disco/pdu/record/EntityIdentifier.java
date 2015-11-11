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
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * Each Entity in a given exercise executing on a DIS application shall be 
 * assigned an Entity Identifier Record Unique to the exercise.
 */
public class EntityIdentifier implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final int ALL_ENTITIES = 0xFFFF;
		
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private SimulationAddress simulationAddress;
	private int entityIdentity;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityIdentifier()
	{
		this( new SimulationAddress(), 0 );
	}
	
	public EntityIdentifier( SimulationAddress simulationAddress, int entityIdentity )
	{
		this.simulationAddress = simulationAddress;
		this.entityIdentity = entityIdentity;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof EntityIdentifier )
		{
			EntityIdentifier asEntityIdentifier = (EntityIdentifier)other;
			if( asEntityIdentifier.simulationAddress.equals(this.simulationAddress) &&
				asEntityIdentifier.entityIdentity == entityIdentity )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public EntityIdentifier clone()
	{
		SimulationAddress simulationAddressClone = simulationAddress.clone();
		return new EntityIdentifier( simulationAddressClone, entityIdentity );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		simulationAddress.from( dis );
		entityIdentity = dis.readUI16();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		simulationAddress.to( dos );
		dos.writeUI16( entityIdentity );
    }
	
	@Override
    public int getByteLength()
	{
		int size = simulationAddress.getByteLength();
		size += DisSizes.UI16_SIZE;
		return size;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public SimulationAddress getSimulationAddress()
	{
		return simulationAddress;
	}
	
	public void setSimulationAddress( SimulationAddress simulationAddress )
	{
		this.simulationAddress = simulationAddress;
	}
	
	public int getEntityIdentity()
	{
		return entityIdentity;
	}
	
	public void setEntityIdentity( int entityIdentity )
	{
		this.entityIdentity = entityIdentity;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
