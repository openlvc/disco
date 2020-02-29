/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.disco.connection.rpr.types.fixed;

import org.openlvc.disco.pdu.entity.EntityStatePdu;

/**
 * Provide some common serialization/deserialization methods so that we don't have to cast
 * any child in processing code.
 * 
 *
 */
public abstract class AbstractSpatialStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Set the values of the struct from the values in the given PDU. Return ourselves to
	 * support chaining.
	 * 
	 * @param pdu The PDU to pull the values from
	 */
	public abstract AbstractSpatialStruct fromPdu( EntityStatePdu pdu );

	/**
	 * Write our spatial values to the given PDU.
	 * 
	 * @param pdu The PDU to write to
	 */
	public abstract void toPdu( EntityStatePdu pdu );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
