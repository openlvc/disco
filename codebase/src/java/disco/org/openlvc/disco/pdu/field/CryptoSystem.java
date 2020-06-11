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

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
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
	None        ( 0 ),
	KY28        ( 1 ),
	KY58        ( 2 ),
	NSVE        ( 3 ),
	WSVE        ( 4 ),
	SincgarsIcom( 5 ),
	KY75        ( 6 ),
	KY100       ( 7 ),
	KY57        ( 8 ),
	KYV5        ( 9 ),
	KG40AP      ( 10 ),
	KG40AS      ( 11 ),
	KG40AR      ( 12 ),
	Invalid     ( Short.MAX_VALUE );

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
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI16_SIZE;
	}

	public static CryptoSystem fromValue( int value )
	{
		for( CryptoSystem system : CryptoSystem.values() )
		{
			if( system.value == value )
				return system;
		}

		// Missing
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid CryptoSystem" );
		else
			return Invalid;
	}
}
