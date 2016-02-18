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
package org.openlvc.disco.pdu.field.appearance;

import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.utils.BitField32;

/**
 * This class will extract the Platform Apperance bits from a given 32-bit integer
 * are received in an {@link EntityStatePdu}.
 */
public class PlatformAppearance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final int INDEX_MKILL = 1;
	private static final int INDEX_POWERPLANT = 22;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private BitField32 bitfield;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PlatformAppearance( int bits )
	{
		this.bitfield = new BitField32( bits );
	}
	
	public PlatformAppearance()
	{
		this( 0 );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int getBits()
	{
		return bitfield.getInt();
	}
	
	public PlatformAppearance setBits( int bits )
	{
		this.bitfield.setInt( bits );
		return this;
	}
	
	public boolean isMobilityKilled()
	{
		return bitfield.isSet( INDEX_MKILL );
	}

	public PlatformAppearance setMobilityKilled( boolean mkill )
	{
		bitfield.setBit( INDEX_MKILL, mkill );
		return this;
	}
	
	public boolean isPowerplantOn()
	{
		return bitfield.isSet( INDEX_POWERPLANT );
	}
	
	public PlatformAppearance setPowerplantOn( boolean on )
	{
		bitfield.setBit( INDEX_POWERPLANT, on );
		return this;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
