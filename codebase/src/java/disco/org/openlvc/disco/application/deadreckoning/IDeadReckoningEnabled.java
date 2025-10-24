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
package org.openlvc.disco.application.deadreckoning;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;

/**
 * An interface representing an object that can undergo dead-reckoning.
 * 
 * @see DeadReckoningModel
 */
public interface IDeadReckoningEnabled {
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * @see PDU#getLocalTimestamp()
	 */
	public long getLocalTimestamp(); // ms since epoch

	/**
	 * @see EntityStatePdu#getDeadReckoningParams()
	 */
	public DeadReckoningParameter getDeadReckoningParams();

	/**
	 * @see EntityStatePdu#getLocation()
	 */
	public WorldCoordinate getLocation();

	/**
	 * @see EntityStatePdu#getLinearVelocity()
	 */
	public VectorRecord getLinearVelocity();

	// public VectorRecord getLinearAcceleration(); // in DeadReckoningParameter
	
	/**
	 * @see EntityStatePdu#getOrientation()
	 */
	public EulerAngles getOrientation();

	// public AngularVelocityVector getAngularVelocity(); // in DeadReckoningParameter
}
