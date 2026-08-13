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
package org.openlvc.disco.connection.rpr;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import hla.rti1516e.RtiFactory;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * A custom implementation of {@link hla.rti1516e.RtiFactoryFactory} that enforces usage of a
 * specific classloader, provided by {@link RprRtiPathHelper}.
 * <p>
 * <b>Note:</b> {@link RprRtiPathHelper#extendClassPath} MUST be used to create an extended
 * classloader before the first invocation of a method from this class. Else, the system class
 * loader will be used.
 */
public class RprRtiFactoryFactory
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
    private RprRtiFactoryFactory() {}
    
    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    //==========================================================================================
    //----------------------------- Accessor and Mutator Methods -------------------------------
    //==========================================================================================

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    
	public static RtiFactory getRtiFactory( String name ) throws RTIinternalError
	{
		for( RtiFactory rtiFactory : ServiceLoader.load(RtiFactory.class,
		                                                RprRtiPathHelper.getActiveLoader()) )
		{
			if( rtiFactory.rtiName().equals(name) )
			{
				return rtiFactory;
			}
		}
		
		throw new RTIinternalError( "Cannot find factory matching "+name );
	}
	
	public static RtiFactory getRtiFactory() throws RTIinternalError
	{
		ServiceLoader<RtiFactory> loader = ServiceLoader.load( RtiFactory.class,
		                                                       RprRtiPathHelper.getActiveLoader() );
		Iterator<RtiFactory> iterator = loader.iterator();
		if( iterator.hasNext() )
		{
			return iterator.next();
		}
		else
		{
			throw new RTIinternalError( "Cannot find factory" );
		}
	}
	
	public static Set<RtiFactory> getAvailableRtiFactories()
	{
		Set<RtiFactory> factories = new HashSet<>();
		
		for( RtiFactory rtiFactory : ServiceLoader.load(RtiFactory.class,
		                                                RprRtiPathHelper.getActiveLoader()) )
		{
			factories.add( rtiFactory );
		}
		
		return factories;
	}
}
