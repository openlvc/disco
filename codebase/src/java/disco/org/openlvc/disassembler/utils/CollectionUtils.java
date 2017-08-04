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
package org.openlvc.disassembler.utils;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils
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

	///////////////////////////////////////////////////////////////////////
	/// Sorting Methods   /////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	public static <T extends FieldComparable<T>> Collection<T> sort( Collection<T> list,
	                                                                 String field,
	                                                                 boolean asc )
	{
		return list.stream()
			.sorted( (t1,t2) -> asc ? t1.compareTo(t2,field) : t2.compareTo(t1,field) )
			.collect( Collectors.toList() );
	}

	///////////////////////////////////////////////////////////////////////
	/// Searching Methods   ///////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	public static <T extends Searchable<T>> Collection<T> search( Collection<T> list,
	                                                              String queryString )
	{
		return search(list.parallelStream(),queryString).collect( Collectors.toList() );
	}
	
	public static <T extends Searchable<T>> Stream<T> search( Stream<T> stream, String queryString )
	{
		return stream.filter( item -> { 
				if( item.matches(queryString) )
						return true;
				return false;
			} );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
