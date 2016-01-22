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

import org.openlvc.disco.pdu.DisSizes;

public enum ParameterTypeDesignator
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	ArticulatedPart( (short)0 ),
	AttachedPart( (short)1 ),
	Invalid( Short.MAX_VALUE );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ParameterTypeDesignator( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static ParameterTypeDesignator fromValue( short value )
	{
		switch( value )
		{
			case 0: return ArticulatedPart;
			case 1: return AttachedPart;
			default: // drop through
		}

		return Invalid;
	}
}
