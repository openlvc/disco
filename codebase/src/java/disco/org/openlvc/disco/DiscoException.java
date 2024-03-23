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
package org.openlvc.disco;

public class DiscoException extends RuntimeException
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 3112252018924L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Just create an empty exception
	 */
	public DiscoException()
	{
		super();
	}

	/**
	 * @param message The message to create the exception with
	 */
	public DiscoException( String message )
	{
		super( message );
	}

	/**
	 * @param formatString Format string to use for the message
	 * @param arguments Format arguments
	 */
	public DiscoException( String formatString, Object... arguments )
	{
		super( String.format(formatString,arguments) );
	}

	/**
	 * @param cause The cause of the exception
	 */
	public DiscoException( Throwable cause )
	{
		super( cause );
	}

	/**
	 * @param message The message to create the exception with
	 * @param cause The cause of the exception
	 */
	public DiscoException( String message, Throwable cause )
	{
		super( message, cause );
	}

	/**
	 * @param cause The root cause of the exception
	 * @param formatString Format string to use for the message
	 * @param arguments Format arguments
	 */
	public DiscoException( Throwable cause, String formatString, Object... arguments )
	{
		super( String.format(formatString,arguments), cause );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
