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
package org.openlvc.distributor.filters.espdu;

import java.util.regex.Pattern;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.distributor.filters.IFilter;
import org.openlvc.distributor.filters.Operator;

public abstract class AbstractEntityStateFilter implements IFilter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected String fieldName;
	protected Operator operator;
	protected String value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected AbstractEntityStateFilter( String fieldName, Operator operator, String value )
	{
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Return <code>true</code> if the given PDU matches the condition set in this filter.
	 */
	public abstract boolean matches( PDU pdu );

	/**
	 * Essentially <code>toString()</code> for a filter. Returns the query string that the
	 * filter encapsultates.
	 */
	public String getFilterString()
	{
		return fieldName+" "+operator+" "+value;
	}
	
	public String toString()
	{
		return getFilterString();
	}
	
	protected final EntityStatePdu cast( PDU pdu )
	{
		if( pdu instanceof EntityStatePdu )
			return (EntityStatePdu)pdu;
		else
			return null;
	}
	
	protected final boolean patternMatches( Pattern pattern, String incoming )
	{
		boolean matches = pattern.matcher(incoming).matches();
		return operator == Operator.Equals ? matches : !matches;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
