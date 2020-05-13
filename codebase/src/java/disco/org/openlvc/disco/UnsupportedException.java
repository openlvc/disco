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
package org.openlvc.disco;

/**
 * Exception to throw when some data has been received or processed that is unsupported. Often
 * times we do not want to cause a fatal exception for this, but rather log it and continue.
 * A separate exception class lets us distinguish hard problems from soft ones.
 */
public class UnsupportedException extends DiscoException
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
	public UnsupportedException()
	{
		super();
	}

	/**
	 * @param message The message to create the exception with
	 */
	public UnsupportedException( String message )
	{
		super( message );
	}

	/**
	 * @param cause The cause of the exception
	 */
	public UnsupportedException( Throwable cause )
	{
		super( cause );
	}

	/**
	 * @param message The message to create the exception with
	 * @param cause The cause of the exception
	 */
	public UnsupportedException( String message, Throwable cause )
	{
		super( message, cause );
	}

	/**
	 * @param formatString A format string to use for the message
	 * @param objects Arguments to apply to the format string
	 */
	public UnsupportedException( String formatString, Object... objects )
	{
		super( String.format( formatString, objects ) );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
