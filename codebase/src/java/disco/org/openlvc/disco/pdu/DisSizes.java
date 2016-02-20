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
package org.openlvc.disco.pdu;

import java.util.Collection;

/**
 * A Utility class that provides size constants and functions relating to the DIS specification.
 */
public class DisSizes
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// SIZES (specified in bytes)
	public static final int UI8_SIZE = 1;
	public static final int UI16_SIZE = 2;
	public static final int UI32_SIZE = 4;
	public static final int UI64_SIZE = 8;
	public static final int FLOAT32_SIZE = 4;
	public static final int FLOAT64_SIZE = 8;
	
	// MAXIMUM VALUES
	public static final short UI8_MAX_VALUE = 0xFF; 
	public static final int   UI16_MAX_VALUE = 0xFFFF;
	public static final long  UI32_MAX_VALUE = 0xFFFFFFFFl;
	
	/**
	 * The maximum value that can fit into the PDU Header's length field (specified in bytes). 
	 */
	//   MTU - IPv4 Header - UDP Header
	//  1500       20            8       = 1472
	public static final int PDU_MAX_SIZE = 1472; 
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Returns the size, in bytes, of the the specified collection of DIS <code>IPDUComponent</code>s
	 * 
	 * @param collection The collection to calculate the size of
	 * 
	 * @return An int representing the size, in bytes, of the specified collection
	 */
	public static int getByteLengthOfCollection( Collection<? extends IPduComponent> collection )
	{
		int size = 0;
		
		for ( IPduComponent component : collection )
			size += component.getByteLength();
			
		return size;
	}
}
