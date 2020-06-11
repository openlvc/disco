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
package org.openlvc.disco.configuration;

/**
 * PDU parsing/processing flags that should be obeyed when PDUs are being read or written.
 */
public enum Flag
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	/**
	 * Throw an exception if there are value range or validity issues
	 * found for fields when deserializing PDUs.
	 */
	Strict,
	
	/**
	 * When unsupported PDUs are found, parse them into an {@link UnparsedPdu}.
	 * This type parses the header, but not the body. This is useful for wrapping
	 * unsupported PDUs in a cloak that lets them pass safely through the framework
	 * to PDU listeners that don't want to process the PDU body (forwarder, logger, ...).
	 */ 
	Unparsed,

	/**
	 * Use {@link UnparsedPdu}s exclusively. Never attempt to turn a PDU into a type
	 * that we can interrogate more deeply. Useful for forwarders/loggers who just
	 * want to pass the data without processing it deeply.
	 */
	UnparsedExclusive;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Flag valueOfIgnoreCase( String name )
	{
		for( Flag flag : values() )
			if( flag.name().equalsIgnoreCase(name) )
				return flag;
		
		throw new IllegalArgumentException( "Not a valid flag: "+name );
	}
}
