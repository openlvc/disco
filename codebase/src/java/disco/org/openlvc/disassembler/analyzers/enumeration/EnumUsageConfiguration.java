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
package org.openlvc.disassembler.analyzers.enumeration;

import java.util.Queue;

import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disco.DiscoException;

public class EnumUsageConfiguration extends Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Analyzer Enumeration Usage
	public static final String KEY_ENUM_ORDER_BY   = "disassembler.analyzer.enum-usage.orderby";
	public static final String KEY_ENUM_ASCENDING  = "disassembler.analyzer.enum-usage.ascending";
	public static final String KEY_ENUM_FILTER     = "disassembler.analyzer.enum-usage.filterby";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EnumUsageConfiguration( String[] commandline )
	{
		super( commandline );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	protected boolean applyCommandLineArgument( String argument, Queue<String> arguments )
	{
		if( argument.equalsIgnoreCase("--order-by") )
			setOrderBy( arguments.remove() );
		else if( argument.equals("--ascending") )
			setAscending();
		else if( argument.equals("--descending") )
			setDescending();
		else if( argument.equals("--filter-by") )
			setFilterBy( arguments.remove(), arguments.remove() );
		else
			return super.applyCommandLineArgument( argument, arguments );
		
		// we processed it above which means we're all cool
		return true;
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
		return getProperty( KEY_ENUM_ORDER_BY, "pdu-count" );
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
			setProperty( KEY_ENUM_ORDER_BY, field );
		}
		else
		{
			throw new DiscoException( "Unknown field for order-by: "+field );
		}
	}

	public boolean getAscending()
	{
		return getProperty(KEY_ENUM_ASCENDING,"false").equalsIgnoreCase( "true" );
	}
	
	public void setAscending()
	{
		setProperty( KEY_ENUM_ASCENDING, "true" );
	}
	
	public void setDescending()
	{
		setProperty( KEY_ENUM_ASCENDING, "false" );
	}
	
	public void setFilterBy( String field, String value )
	{
		setProperty( KEY_ENUM_FILTER, field+" "+value );

	}
	
	public String getFilterBy()
	{
		return getProperty( KEY_ENUM_FILTER, "" );
	}
	
	public boolean hasFilterBy()
	{
		return hasProperty( KEY_ENUM_FILTER );
	}
	
}
