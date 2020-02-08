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
package org.openlvc.disco.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.openlvc.disco.DiscoException;

public class ClassLoaderUtils
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Add a given path or set of paths to the system classloader.
	 * 
	 * @param paths The paths to add to the lookup set
	 * @throws DiscoException If there is a problem fetching the classloader or
	 *                        extending its search path
	 */
	public static void extendClasspath( List<File> paths ) throws DiscoException
	{
		// Find the classloader and use reflection to make the addURL method visible
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try
		{
			// Make the method accessible
			Method method = sysclass.getDeclaredMethod( "addURL", URL.class );
			method.setAccessible( true );
			
			// Call it
			for( File file : paths )
				method.invoke( sysloader, new Object[]{ file.toURI().toURL() } );
		}
		catch( Throwable throwable )
		{
			throw new DiscoException( "Error extending system classloader lookup path: "+
			                          throwable.getMessage(), throwable );
		}
	}
}
