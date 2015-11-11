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

import java.io.IOException;

/**
 * An interface that represents a serialisable component of a PDU
 */
public interface IPduComponent
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Reads new field values for the PDU Component from the provided DISInputStream replacing any 
	 * existing field values before the method was called.
	 * 
	 * If the method fails due to an IOException being raised, the object will be left in an
	 * undefined state.
	 * 
	 * @param dis The DISInputStream to read the new field values from
	 * 
	 * @throws IOException Thrown if there was an error reading from the provided DISInputStream
	 */
	public abstract void from( DisInputStream dis ) throws IOException;
	
	/**
	 * Writes the PDU Component's current field values to the provided DISOutputStream.
	 * 
	 * If the method fails due to an IOException being raised, the provided DISOutputStream will be 
	 * left in an undefined state.
	 * 
	 * @param dis The DISOutputStream to write field values to
	 * 
	 * @throws IOException Thrown if there was an error writing to the provided DISOutputStream
	 */
	public abstract void to( DisOutputStream dos ) throws IOException;
	
	/**
	 * Returns the length of this IPDUComponent in bytes
	 * 
	 * @return an int value representing the length of this IPDUComponent in bytes
	 */
	public abstract int getByteLength();
}
