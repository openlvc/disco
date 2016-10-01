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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.openlvc.distributor.filters.espdu.EntityDomainFilter;
import org.openlvc.distributor.filters.espdu.EntityForceFilter;
import org.openlvc.distributor.filters.espdu.EntityIdFilter;
import org.openlvc.distributor.filters.espdu.EntityKindFilter;
import org.openlvc.distributor.filters.espdu.EntityMarkingFilter;
import org.openlvc.distributor.filters.espdu.EntityTypeFilter;
import org.openlvc.distributor.filters.pdu.PduAppIdFilter;
import org.openlvc.distributor.filters.pdu.PduExerciseIdFilter;
import org.openlvc.distributor.filters.pdu.PduFamilyFilter;
import org.openlvc.distributor.filters.pdu.PduSiteIdFilter;
import org.openlvc.distributor.filters.pdu.PduTypeFilter;
import org.openlvc.distributor.filters.pdu.PduVersionFilter;

/**
 * This class provides a static list of all filter types. You can simply ask it for a new
 * filter of a specific type and it will return you the approrpiate one.
 */
public class FilterRegistry
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static Map<String,Class<? extends IFilter>> implementations = new HashMap<>();
	static
	{
		//
		// NOTE: All types need a constructor of the form: Constructor(Operator,String)
		//
		// PDU
		implementations.put( PduTypeFilter.FILTER_KEY,       PduTypeFilter.class );
		implementations.put( PduFamilyFilter.FILTER_KEY,     PduFamilyFilter.class );
		implementations.put( PduVersionFilter.FILTER_KEY,    PduVersionFilter.class );
		implementations.put( PduExerciseIdFilter.FILTER_KEY, PduExerciseIdFilter.class );
		implementations.put( PduSiteIdFilter.FILTER_KEY,     PduSiteIdFilter.class );
		implementations.put( PduAppIdFilter.FILTER_KEY,      PduAppIdFilter.class );
		
		// Entity State PDU
		implementations.put( EntityIdFilter.FILTER_KEY,      EntityIdFilter.class );
		implementations.put( EntityMarkingFilter.FILTER_KEY, EntityMarkingFilter.class );
		implementations.put( EntityTypeFilter.FILTER_KEY,    EntityTypeFilter.class );
		implementations.put( EntityForceFilter.FILTER_KEY,   EntityForceFilter.class );
		implementations.put( EntityDomainFilter.FILTER_KEY,  EntityDomainFilter.class );
		implementations.put( EntityKindFilter.FILTER_KEY,    EntityKindFilter.class );
	}

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
	/**
	 * Create a new instance of the named filter with the given operator and value to use
	 * when assessing whether PDUs meet the conditions.
	 * <p/>
	 * Typical filter definitions are of the form: <code>name operator value</code>. We use
	 * the name to locate the filter class inside the internal static registry and pass the
	 * operator and value as constructor arguments. As such, a constructor of the form
	 * <code>Constructor(Operator,String)</code> is required to be present for all filter
	 * implementations. If not, an exception will be thrown.
	 * 
	 * @param name Name of the filter
	 * @param operator Operator applied to the filter in configuration
	 * @param value The value against which to apply the operator when assessing individual PDUs
	 * @return A new instance of the named filter with the given operator and value
	 * @throws IllegalArgumentException If there is no filter for the given name, or the filter
	 *                                  implementation doesn't have an appropriate constructor
	 * @throws RuntimeException If there is a problem creating the filter
	 */
	public static IFilter create( String name, Operator operator, String value )
	{
		Class<? extends IFilter> clazz = implementations.get( name );
		if( clazz == null )
			throw new IllegalArgumentException( "Filter type not known: "+name );
		
		try
		{
			Constructor<? extends IFilter> constructor = clazz.getConstructor( Operator.class, String.class );
			return constructor.newInstance( operator, value );
		}
		catch( NoSuchMethodException nsm )
		{
			throw new IllegalArgumentException( "Filter type must declare constructor(Operator,String): "+name );
		}
		catch( Exception e )
		{
			throw new RuntimeException( "Problem creating filter ("+name+"): "+e.getMessage(), e );
		}
	}
	
}
