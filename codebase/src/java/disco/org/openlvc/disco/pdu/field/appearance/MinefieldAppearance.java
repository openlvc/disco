/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.pdu.field.appearance;

import org.openlvc.disco.pdu.field.appearance.enums.MinefieldType;
import org.openlvc.disco.utils.BitField32;

public class MinefieldAppearance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int INDEX_MINEFIELDTYPE      = 0;       // 0-1
	private static final int INDEX_ACTIVESTATUS       = 2;       // 2
	private static final int INDEX_LANE               = 3;       // 3
	private static final int INDEX_STATE              = 13;      // 13 (Not 100% sure how this differs to active field)

	// bit ending values for multi-bit parameters'
	private static final int INDEX_MINEFIELDTYPE_END  = 1;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BitField32 bitfield; // Only using 16 bits of this

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public MinefieldAppearance()
	{
		this( 0 );
		this.setActive( true );
		this.setState( true );
	}
	
	public MinefieldAppearance( int bits )
	{
		this.bitfield = new BitField32( bits );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
			return false;
		
		if( other instanceof MinefieldAppearance )
		{
			MinefieldAppearance otherApperance = (MinefieldAppearance)other;
			return otherApperance.getBits() == this.getBits();
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return this.getBits();
	}

	@Override
	public String toString()
	{
		return String.format( "minefieldtype=%s, active=%s, activelane=%b, state=%s",
		                      getMinefieldType(),
		                      isActive() ? "active" : "inactive",
		                      isActiveLane(),
		                      getState() ? "active" : "deactivated" );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getBits()
	{
		return this.bitfield.getBitsUI16();
	}
	
	public void setBits( int bits )
	{
		this.bitfield.setInt( bits );
	}
	
	public MinefieldType getMinefieldType()
	{
		int raw = this.bitfield.getBits( INDEX_MINEFIELDTYPE, INDEX_MINEFIELDTYPE_END ); 
		return MinefieldType.fromValue( raw );
	}
	
	public void setMinefieldType( MinefieldType type )
	{
		this.bitfield.setBits( INDEX_MINEFIELDTYPE, INDEX_MINEFIELDTYPE_END, type.value() );
	}
	
	public boolean isActive()
	{
		return this.bitfield.isSet( INDEX_ACTIVESTATUS );
	}
	
	public void setActive( boolean active )
	{
		this.bitfield.setBit( INDEX_ACTIVESTATUS, active );
	}
	
	public boolean isActiveLane()
	{
		return this.bitfield.isSet( INDEX_LANE );
	}
	
	public void setActiveLane( boolean active )
	{
		this.bitfield.setBit( INDEX_LANE, active );
	}
	
	public boolean getState()
	{
		return this.bitfield.isSet( INDEX_STATE );
	}
	
	public void setState( boolean state )
	{
		this.bitfield.setBit( INDEX_STATE, state );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
