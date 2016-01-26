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
package org.openlvc.disco.pdu.field;

import java.util.HashMap;
import java.util.Map;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.DisSizes;

/**
 * This field shall identify the crypto equipment utilized if such equipment is 
 * used with the Transmitter PDU
 * 
 * This field shall be represented by a 16-bit enumeration
 * 
 * @see "Section 9 in EBV-DOC"
 */
public enum CryptoSystem
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other       ( 0 ),
	KY28        ( 1 ),
	KY58        ( 2 ),
	NSVE        ( 3 ),
	WSVE        ( 4 ),
	SincgarsIcom( 5 ),
	Invalid     ( Integer.MAX_VALUE );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// fast lookup
	private static Map<Integer,CryptoSystem> CACHE = new HashMap<>();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private CryptoSystem( int value )
	{
		this.value = value;
		store( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int value()
	{
		return this.value;
	}

	private void store( int value )
	{
		if( CACHE == null )
			CACHE = new HashMap<>();

		CACHE.put( value, this );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static CryptoSystem fromValue( int value )
	{
		CryptoSystem result = CACHE.get( value );
		if( result != null )
			return result;

		// Missing
		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" not a valid CryptoSystem" );
		else
			return Invalid;
	}
}
