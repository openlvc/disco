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

import java.io.EOFException;
import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * A collection of boolean fields which describe the capabilities of the Entity.
 */
public class EntityCapabilities implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean ammunitionSupply;
	private boolean fuelSupply;
	private boolean recovery;
	private boolean repair;
	private boolean adsb;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityCapabilities()
	{
		this( false, false, false, false, false );
	}
	
	public EntityCapabilities( boolean ammunitionSupply,
	                           boolean fuelSupply,
	                           boolean recovery,
	                           boolean repair,
	                           boolean adsb)
	{
		this.ammunitionSupply = ammunitionSupply;
		this.fuelSupply = fuelSupply;
		this.recovery = recovery;
		this.repair = repair;
		this.adsb = adsb;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof EntityCapabilities )
		{
			EntityCapabilities otherCapabilities = (EntityCapabilities)other;
			if( otherCapabilities.ammunitionSupply == this.ammunitionSupply &&
			    otherCapabilities.fuelSupply == this.fuelSupply &&
			    otherCapabilities.recovery == this.recovery &&
			    otherCapabilities.repair == this.repair &&
			    otherCapabilities.adsb == this.adsb )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public EntityCapabilities clone()
	{
		return new EntityCapabilities( ammunitionSupply, fuelSupply, recovery, repair, adsb );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		int ch1 = dis.readInt();
		
		if( ch1 < 0 )
			throw new EOFException();		
		
		ammunitionSupply = (ch1 & 0x01) != 0;
		fuelSupply = (ch1 & 0x02) != 0;
		recovery = (ch1 & 0x04) != 0;
		repair = (ch1 & 0x08) != 0;
		adsb = (ch1 & 0x10) != 0;
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		int ch1 = 0;
		
		if( ammunitionSupply )
			ch1 |= 0x01;
		
		if( fuelSupply )
			ch1 |= 0x02;
		
		if( recovery )
			ch1 |= 0x04;
		
		if( repair )
			ch1 |= 0x08;
		
		if ( adsb )
			ch1 |= 0x10;
		
		dos.writeInt( ch1 );
    }

	@Override
    public int getByteLength()
	{
		return DisSizes.UI32_SIZE;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean getAmmunitionSupply()
    {
    	return ammunitionSupply;
    }

	public void setAmmunitionSupply( boolean ammunitionSupply )
    {
    	this.ammunitionSupply = ammunitionSupply;
    }

	public boolean getFuelSupply()
    {
    	return fuelSupply;
    }

	public void setFuelSupply( boolean fuelSupply )
    {
    	this.fuelSupply = fuelSupply;
    }

	public boolean getRecovery()
    {
    	return recovery;
    }

	public void setRecovery( boolean recovery )
    {
    	this.recovery = recovery;
    }

	public boolean getRepair()
    {
    	return repair;
    }

	public void setRepair( boolean repair )
    {
    	this.repair = repair;
    }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
