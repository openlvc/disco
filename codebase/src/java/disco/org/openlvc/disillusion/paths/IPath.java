/*
 *   Copyright 2019 Open LVC Project.
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
package org.openlvc.disillusion.paths;

import org.openlvc.disco.utils.LLA;

/**
 * Interface for movement paths used by DISillusion
 * 
 * The general idea is that a path is defined, and then one can
 * query the path for where something would be given the
 * current simulation time.
 */
public interface IPath
{
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Obtain the LLA along the path for the given time (starting at 0 at the start of the
	 * simulation, in milliseconds)
	 * 
	 * @param time the current time, in milliseconds, since the start of the simulation
	 */
	public LLA getLLA( long time );

	/**
	 * Obtain the LLA along the path for the given time (starting at 0 at the start of the
	 * simulation, in milliseconds), with an offset (useful for multiple items on the same path)
	 * 
	 * @param time the current time, in milliseconds, since the start of the simulation
	 * @param offset the offset, in meters, along the path
	 */
	public LLA getLLA( long time, double offset );

	/**
	 * Obtain the LLA along the path for the given distance along the path
	 * 
	 * @param distance the distance along the path
	 */
	public LLA getLLA( double distance );
}
