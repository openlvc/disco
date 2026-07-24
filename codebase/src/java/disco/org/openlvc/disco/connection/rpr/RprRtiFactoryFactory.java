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
 * A custom implementation of {@link hla.rti1516e.RtiFactoryFactory} that ensures that the same
 * classloader is used for all attempts to {@link hla.rti1516e.RtiFactory}.
 * <p>
 * <b>Note:</b> the first invocation of a method from this class MUST occur on the same thread
 * as the prior invocation of {@link org.openlvc.disco.utils.ClassLoaderUtils#extendClasspath}.
 */
public class RprRtiFactoryFactory
{
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------
    private static ClassLoader classLoader;

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    public RprRtiFactoryFactory() {}
    
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
		for( RtiFactory rtiFactory : ServiceLoader.load(RtiFactory.class, getClassLoader()) )
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
		ServiceLoader<RtiFactory> loader = ServiceLoader.load( RtiFactory.class, getClassLoader() );
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
		
		for( RtiFactory rtiFactory : ServiceLoader.load(RtiFactory.class, getClassLoader()) )
		{
			factories.add( rtiFactory );
		}
		
		return factories;
	}

	/**
	 * @return the current thread's classloader if possible, or {@code null} if the system
	 *         classloader is to be used.
	 */
	private static ClassLoader getClassLoader()
	{
		if( classLoader == null )
		{
			// prefer thread context classloader, since that'll usually
			// be the one with the extended classpath
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		
		return classLoader;
	}
}
