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
import org.openlvc.disco.pdu.field.Fuse;
import org.openlvc.disco.pdu.field.Warhead;

/**
 * The firing of a round or a burst of ammunition shall be represented by a Burst Descriptor
 * Record. This record shall specify the type of munition fired, the type of warhead, the type of
 * fuse, the number of rounds fired, and the rate at which the rounds are fired in rounds per
 * minute.
 */
public class BurstDescriptor implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityType munition;
	private Warhead warhead;
	private Fuse fuse;
	private int quantity;
	private int rate;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public BurstDescriptor()
	{
		this( new EntityType(), Warhead.Other, Fuse.Other, 0, 0 );
	}
	
	public BurstDescriptor( EntityType munition, Warhead warhead, Fuse fuse, int quantity, int rate )
	{
		this.munition = munition;
		this.warhead = warhead;
		this.fuse = fuse;
		this.quantity = quantity;
		this.rate = rate;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if ( other instanceof BurstDescriptor )
		{
			BurstDescriptor asBurstDescriptor = (BurstDescriptor)other;
			if( asBurstDescriptor.munition.equals(this.munition) && 
			    asBurstDescriptor.warhead == this.warhead &&
			    asBurstDescriptor.fuse == this.fuse &&
			    asBurstDescriptor.quantity == this.quantity &&
			    asBurstDescriptor.rate == this.rate )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public BurstDescriptor clone()
	{
		EntityType munitionClone = munition.clone();
		return new BurstDescriptor( munitionClone, warhead, fuse, quantity, rate );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		munition.from( dis );
		warhead  = Warhead.fromValue( dis.readUI16() );
		fuse     = Fuse.fromValue( dis.readUI16() );
		quantity = dis.readUI16();
		rate     = dis.readUI16();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		munition.to( dos );
		dos.writeUI16( warhead.value() );
		dos.writeUI16( fuse.value() );
		dos.writeUI16( quantity );
		dos.writeUI16( rate );
	}
	
	@Override
	public final int getByteLength()
	{
		return 16;

		// int size = munition.getByteLength();   // 8
		// size += DisSizes.UI16_SIZE * 4;        // 8
		// return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityType getMunition()
	{
		return munition;
	}

	public void setMunition( EntityType munition )
	{
		this.munition = munition;
	}

	public Warhead getWarhead()
	{
		return warhead;
	}

	public void setWarhead( Warhead warhead )
	{
		this.warhead = warhead;
	}

	public Fuse getFuse()
	{
		return fuse;
	}

	public void setFuse( Fuse fuse )
	{
		this.fuse = fuse;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity( int quantity )
	{
		this.quantity = quantity;
	}

	public int getRate()
	{
		return rate;
	}

	public void setRate( int rate )
	{
		this.rate = rate;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
