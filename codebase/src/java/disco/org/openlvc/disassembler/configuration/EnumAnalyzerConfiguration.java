/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.disassembler.configuration;

import org.openlvc.disco.DiscoException;

public class EnumAnalyzerConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Analyzer Enumeration Usage
	public static final String KEY_ENUM_ORDER_BY   = "disassembler.analyzer.enum.orderby";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected EnumAnalyzerConfiguration( Configuration parent )
	{
		this.configuration = parent;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	protected void applyCommandLine( String[] args ) throws DiscoException
	{
		for( int i = 0; i < args.length; i++ )
		{
			String argument = args[i];
			if( argument.equalsIgnoreCase("--order-by") )
				setOrderBy( args[++i] );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The field that the resutls should be ordered by. Valid values are:
	 * 
	 *   - enumeration
	 *   - site-id
	 *   - pdu-count
	 *   - obj-count
	 *   
	 */
	public String getOrderBy()
	{
		return configuration.getProperty( KEY_ENUM_ORDER_BY, "pdu-count" );
	}

	/**
	 * Field to order results by. Valid values are:
	 * 
	 *   - enumeration
	 *   - site-id
	 *   - pdu-count
	 *   - obj-count
	 *   
	 * @param field String that equals one of the above (case insensitive)
	 * @throws DiscoException if the argument is not one of the valid set
	 */
	public void setOrderBy( String field ) throws DiscoException
	{
		if( field.equalsIgnoreCase("enumeration") ||
			field.equalsIgnoreCase("site-id")     ||
			field.equalsIgnoreCase("pdu-count")   ||
			field.equalsIgnoreCase("obj-count") )
		{
			configuration.setProperty( KEY_ENUM_ORDER_BY, field );
		}
		else
		{
			throw new DiscoException( "Unknown field for order-by: "+field );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
