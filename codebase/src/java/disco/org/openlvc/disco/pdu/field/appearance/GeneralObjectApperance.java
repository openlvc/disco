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

import org.openlvc.disco.pdu.field.appearance.enums.IedPresent;
import org.openlvc.disco.pdu.field.appearance.enums.ObjectDamage;
import org.openlvc.disco.utils.BitField32;

public class GeneralObjectApperance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int INDEX_PERCENTCOMPLETE = 0;   // 0-7
	private static final int INDEX_DAMAGE = 8;            // 8-9
	private static final int INDEX_PREDISTRIBUTED = 10;
	private static final int INDEX_STATE = 11;
	private static final int INDEX_SMOKING = 12;
	private static final int INDEX_FLAMING = 13;
	private static final int INDEX_IEDPRESENT = 14;       // 14-15

	// bit ending values for multi-bit parameters'
	private static final int INDEX_PERCENTCOMPLETE_END = 7;
	private static final int INDEX_DAMAGE_END = 9;
	private static final int INDEX_IEDPRESENT_END = 15;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BitField32 bitfield;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public GeneralObjectApperance()
	{
		this( 0 );
		this.setActive( true );
	}
	
	public GeneralObjectApperance( int bits )
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
		
		if( other instanceof GeneralObjectApperance )
		{
			GeneralObjectApperance otherGoa = (GeneralObjectApperance)other;
			return otherGoa.getBits() == this.getBits();
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
		return String.format( "complete=%d%%, damage=%s, predistributed=%b, state=%s, smoking=%b, flaming=%b, iedpresent=%s",
		                      getPercentComplete(),
		                      getDamage().toString(),
		                      isPredistributed(),
		                      isActive() ? "active" : "inactive",
		                      isSmoking(),
		                      isFlaming(),
		                      getIedPresent().toString() );
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
	
	public int getPercentComplete()
	{
		return this.bitfield.getBits( INDEX_PERCENTCOMPLETE, INDEX_PERCENTCOMPLETE_END );
	}
	
	public void setPercentComplete( int percentComplete )
	{
		this.bitfield.setBits( INDEX_PERCENTCOMPLETE, INDEX_PERCENTCOMPLETE_END, percentComplete );
	}
	
	public ObjectDamage getDamage()
	{
		return ObjectDamage.fromValue( (byte)this.bitfield.getBits(INDEX_DAMAGE, INDEX_DAMAGE_END) );
	}

	public void setDamage( ObjectDamage damage )
	{
		this.bitfield.setBits( INDEX_DAMAGE, INDEX_DAMAGE_END, damage.value() );
	}
	
	public boolean isPredistributed()
	{
		return this.bitfield.isSet( INDEX_PREDISTRIBUTED );
	}
	
	public void setPredistributed( boolean predistributed )
	{
		this.bitfield.setBit( INDEX_PREDISTRIBUTED, predistributed );
	}
	
	public boolean isActive()
	{
		return this.bitfield.isSet( INDEX_STATE );
	}
	
	public void setActive( boolean active )
	{
		this.bitfield.setBit( INDEX_STATE, active );
	}
	
	public boolean isSmoking()
	{
		return this.bitfield.isSet( INDEX_SMOKING );
	}
	
	public void setSmoking( boolean smoking )
	{
		this.bitfield.setBit( INDEX_SMOKING, smoking );
	}
	
	public boolean isFlaming()
	{
		return this.bitfield.isSet( INDEX_FLAMING );
	}
	
	public void setFlaming( boolean flaming )
	{
		this.bitfield.setBit( INDEX_FLAMING, flaming );
	}
	
	public IedPresent getIedPresent()
	{
		return IedPresent.fromValue( (byte)this.bitfield.getBits(INDEX_IEDPRESENT, 
		                                                         INDEX_IEDPRESENT_END) );
	}
	
	public void setIedPresent( IedPresent ied )
	{
		this.bitfield.setBits( INDEX_IEDPRESENT, INDEX_IEDPRESENT_END, ied.value() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
