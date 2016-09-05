/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.disco.configuration;

/**
 * General DIS protocol settings that are applicable regardless of transport, sender or receiver
 * implementation.
 */
public class DisConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Keys for properties file
	private static final String PROP_EXERCISE_ID  = "disco.dis.exerciseId";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration parent;

	private short exerciseId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected DisConfiguration( DiscoConfiguration parent )
	{
		this.parent = parent;

		this.exerciseId = -1; // -1 so we can lazy load
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Exercise ID to use for outgoing packets. Defaults to 1.
	 * 
	 * A setting of 0 indicates that we should accept traffic from anywhere.
	 */
	public short getExerciseId()
	{
		// lazy load
		if( this.exerciseId == -1 )
			this.exerciseId = Short.parseShort( parent.getProperty(PROP_EXERCISE_ID,"1") );

		return this.exerciseId;
	}

	/**
	 * Sets the exercise to use for all outgoing PDUs and the only one we'll accept for incoming.
	 */
	public void setExerciseId( short exerciseId )
	{
		parent.setProperty( PROP_EXERCISE_ID, ""+exerciseId );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
