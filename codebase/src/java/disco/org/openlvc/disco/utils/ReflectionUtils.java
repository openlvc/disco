/*
 *   Copyright 2024 Open LVC Project.
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
package org.openlvc.disco.utils;

import org.openlvc.disco.DiscoException;

/**
 * A set of utility methods for working with java Class instances and reflection.
 */
public class ReflectionUtils
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Create a new instance of the given class and return it. The type must have a public,
	 * no-arg constructor or else it cannot be created.
	 * 
	 * @param <T>
	 * @param type The type to create a new instance of.
	 * @return     A new instance of the given type.
	 * @throws DiscoException If there is any underyling exception preventing instantiation.
	 */
	public static <T extends Object> T newInstance( Class<T> type ) throws DiscoException
	{
		try
		{
			return type.newInstance();
		}
		catch( Exception e )
		{
			throw new DiscoException( e,
			                          "Cannot instantiate type [%s]: %s",
			                          type.getName(),
			                          e.getMessage() );
		}
	}
}
