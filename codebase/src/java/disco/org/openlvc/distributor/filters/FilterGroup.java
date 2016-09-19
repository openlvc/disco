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
package org.openlvc.distributor.filters;

import java.util.LinkedList;
import java.util.List;

import org.openlvc.disco.pdu.PDU;

/**
 * A group of filter expressions that can be matched against as a unit. Each group has
 * a match mode: <code>AND</code>, or <code>OR</echo>. If the mode is <code>AND</code>,
 * all filters much return a match for the group to match. If the mode is <code>OR</code>,
 * only one of the filters must return a match.
 */
public class FilterGroup implements IFilter
{
	//----------------------------------------------------------
	//                      ENUMERATIONS
	//----------------------------------------------------------
	public enum Type { AND, OR };

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Type type;
	private List<IFilter> filters;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FilterGroup()
	{
		this.type = Type.OR;
		this.filters = new LinkedList<IFilter>();
	}

	public FilterGroup( Type type )
	{
		this();
		this.type = type;
	}
	
	public FilterGroup( Type type, IFilter... filters )
	{
		this();
		this.type = type;
		for( IFilter temp : filters )
			this.filters.add( temp );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean matches( PDU pdu )
	{
		if( type == Type.AND )
			return filters.stream().allMatch( filter -> filter.matches(pdu) );
		else
			return filters.stream().anyMatch( filter -> filter.matches(pdu) );
	}

	@Override
	public String getFilterString()
	{
		return toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void add( IFilter filter )
	{
		this.filters.add( filter );
	}
	
	public void remove( IFilter filter )
	{
		this.filters.remove( filter );
	}
	
	public List<IFilter> getFilters()
	{
		return this.filters;
	}
	
	@Override
	public String toString()
	{
		return "("+this.type+"/"+this.filters+")";
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
