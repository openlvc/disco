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
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
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
	 * Add a given path or set of paths to a custom classloader, and sets that as the ClassLoader
	 * for the current Thread
	 * 
	 * @param paths The paths to add to the lookup set
	 * @throws DiscoException If there any invalid files are provided
	 */
	@SuppressWarnings("resource")
	public static void extendClasspath( List<File> paths ) throws DiscoException
	{
		// Get the thread's current classloader which any Disco loader should chain off
		ClassLoader threadContextLoader = Thread.currentThread().getContextClassLoader();

		// If we've called this before, the classloader will already be a DiscoClassLoader
		// and we can just extend the path (if we didn't do this, multiple calls to this
		// method would cause the last extension to be used
		if( threadContextLoader instanceof DiscoClassLoader )
		{
			// We've call this method before, just use the current classloader
			DiscoClassLoader discoLoader = (DiscoClassLoader)threadContextLoader;
			for( File file : paths )
				discoLoader.addPath( file );
		}
		else
		{
			// The current classloader isn't one of ours, so let's extend it
			DiscoClassLoader discoLoader = new DiscoClassLoader( threadContextLoader );
			for( File file : paths )
				discoLoader.addPath( file );
			
			// Set the new DiscoLoader as the context loader for the thread
			Thread.currentThread().setContextClassLoader( discoLoader );
		}
	}
	
	/**
	 * Adds the given paths to the Java library path
	 *
	 * @param paths The paths to add to the library path
	 * @throws DiscoException If there is a problem loading the library path or
	 *                        extending it
	 */
	public static void extendLibraryPath( List<File> paths ) throws DiscoException
	{
		try
		{
			// apparently need to use usr_paths in place of java.library.path
			// if we want to append to path rather than replace it
			final Field libPathsField = ClassLoader.class.getDeclaredField( "usr_paths" );
			libPathsField.setAccessible( true );
			
			// get array of paths and copy to new larger array
			final String[] libPaths = (String[])libPathsField.get(null);
			final String[] newPaths = Arrays.copyOf( libPaths, libPaths.length + paths.size() );
			
			// add the new library paths
			int i = libPaths.length;
			for( File file : paths )
			{
				newPaths[i] = file.getAbsolutePath();
				i++;
			}
			
			libPathsField.set( null, newPaths );
		}
		catch( Throwable throwable )
		{
			throw new DiscoException( "Error extending Java library path: "+
			                          throwable.getMessage(), throwable );
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Class: DiscoClassLoader   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A custom class loader to allow us to extend the classpath. Chains off the current thread's
	 * context class loader.
	 */
	private static class DiscoClassLoader extends URLClassLoader
	{
		public DiscoClassLoader( ClassLoader parent )
		{
			super( new URL[0], parent );
		}
		
		@Override
		public void addURL( URL url )
		{
			super.addURL( url );
		}
		
		public void addPath( File file )
		{
			try
			{
				super.addURL( file.toURI().toURL() );
			}
			catch( MalformedURLException mfe )
			{
				throw new DiscoException( "Cannot extend classpath (Bad Path): "+
				                          file.getAbsolutePath() );
			}
		}
	}
	
}
