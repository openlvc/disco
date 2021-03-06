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
package org.openlvc.disco.common;

public class TimeoutException extends RuntimeException
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	/**
     * Just create an empty exception
     */
    public TimeoutException()
    {
	    super();
    }

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
    /**
	 * @param message The message to create the exception with
	 */
	public TimeoutException( String message )
	{
		super( message );
	}

	/**
	 * @param cause The cause of the exception
	 */
	public TimeoutException( Throwable cause )
	{
		super( cause );
	}

	/**
	 * @param message The message to create the exception with
	 * @param cause The cause of the exception
	 */
	public TimeoutException( String message, Throwable cause )
	{
		super( message, cause );
	}

	// ----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
