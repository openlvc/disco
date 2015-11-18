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

import org.openlvc.disco.DiscoException;

public class UnsupportedPDU extends DiscoException
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
	public UnsupportedPDU()
	{
		super();
	}

	/**
	 * @param message The message to create the exception with
	 */
	public UnsupportedPDU( String message )
	{
		super( message );
	}

	/**
	 * @param cause The cause of the exception
	 */
	public UnsupportedPDU( Throwable cause )
	{
		super( cause );
	}

	/**
	 * @param message The message to create the exception with
	 * @param cause The cause of the exception
	 */
	public UnsupportedPDU( String message, Throwable cause )
	{
		super( message, cause );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
