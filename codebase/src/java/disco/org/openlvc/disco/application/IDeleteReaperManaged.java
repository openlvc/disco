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
package org.openlvc.disco.application;

/**
 * Ensures that stores that are delete timeout managed provide implementation of the feature.
 */
public interface IDeleteReaperManaged
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Remove any records that haven't been updated since the given timestamp. Return the
	 * count of all the values removed.
	 * 
	 * @param oldestTimestamp Values must have been updated later than this timestamp to stay around
	 * @return The number of values that were removed
	 */
	public int removeStaleData( long oldestTimestamp );
}
