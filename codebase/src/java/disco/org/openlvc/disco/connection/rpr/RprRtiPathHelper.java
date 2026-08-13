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
package org.openlvc.disco.connection.rpr;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;

/// Correctly loading Java classes and native libraries for HLA RTI providers is constrained by
/// the following conditions:
///
/// 1. RTI service implementation classes cannot be assumed to be on the classpath when Disco is
/// started. This means that:
/// 
///     a. Disco is required to extend the classpath at runtime with configured and/or known
///     default paths.
/// 
///     b. Every attempt to load an RTI implementation class must be performed using a
///     classloader with access to the extended classpath.
/// 
/// 2. Paths cannot be removed from a classloader at runtime, only added.
/// 
/// 3. No native library can be loaded by more than one classloader.
/// 
/// 3. If a classloader contains paths to more than one implementation of a service, it will load
/// the first one. Combined with (2) and (3), this means that:
/// 
///     a. If a single classloader is used, attempting to switch to a different RTI provider at
///     runtime will fail as the original implementation classes will continue to be used even
///     after the new paths have been added.
/// 
///     b. If multiple classloaders are used, each must be permanently associated with an RTI
///     provider such that it is always and only used when accessing services of the provider.
/// 
/// This leads us to the following implementation:
///
/// - On the FIRST startup, we store the current thread's context class loader, to use as a base
/// for extension (and to avoid daisy-chaining extended loaders).
/// 
/// - We maintain a map, initially empty, associating RTI providers with extended classloaders.
/// 
/// - On ANY startup, we retrieve or create a child classloader under the original stored one,
/// extend it with the paths for the configured RTI provider, and set it as the current thread's
/// classloader.
/// 
/// - On ANY attempt to load an RtiFactory, we use the most recent extended classloader.
///
/// This effectively ensures that:
///
/// 1. RtiFactory implementations are always available, irrespective of which thread the factory
/// method is called from.
/// 
/// 2. Implementation classes from different RTI providers never conflict.
/// 
/// 3. Native libraries are always loaded using the same classloader.
///
public class RprRtiPathHelper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final Map<RtiProvider,DiscoClassLoader> rtiLoaderMap = new EnumMap<>(RtiProvider.class);
	
	/** The thread context classloader from the first call to {@link #extendClassPath} */
	private static ClassLoader threadContextLoader = null;
	
	/** The extended classloader from the most recent call to {@link #extendClassPath} */
	private static ClassLoader activeLoader = null;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private RprRtiPathHelper() {}

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
	 * @return the 'active' extended classloader, created on the most recent call to
	 *         {@link #extendClassPath}, or null if that method has never been called.
	 */
	public static ClassLoader getActiveLoader()
	{
		return activeLoader;
	}
	
	/**
	 * Adds a list of paths to a custom classloader and sets that as the classloader for the
	 * current thread.
	 * <p>
	 * This MUST be invoked prior to the first invocation of any method of
	 * {@link org.openlvc.disco.connection.rpr.RprRtiFactoryFactory}.
	 * <p>
	 * The first invocation of this method SHOULD be performed from the 'main' thread, or a thread
	 * that otherwise has a fully-populated context classloader.
	 * 
	 * @param rtiProvider The current RTI provider
	 * @param paths The paths to add to the lookup set
	 * @throws DiscoException If any file does not represent a valid URL
	 */
	public static ClassLoader extendClassPath( RtiProvider rtiProvider, List<File> paths )
		throws DiscoException
	{
		if( threadContextLoader == null )
			threadContextLoader = Thread.currentThread().getContextClassLoader();
		
		DiscoClassLoader discoLoader = rtiLoaderMap.get( rtiProvider );
		if( discoLoader == null )
		{
			discoLoader = new DiscoClassLoader( threadContextLoader );
			rtiLoaderMap.put( rtiProvider, discoLoader );
		}
		
		for( File file : paths )
			discoLoader.addPath( file );
		
		Thread.currentThread().setContextClassLoader( discoLoader );
		activeLoader = discoLoader;
		
		return discoLoader;
	}
	
	/**
	 * Attempts to add the given paths to the Java library path.
	 * <p>
	 * <b>Note:</b> this should be assumed to be nonfunctional on most implementations of Java 11
	 * or later.
	 *
	 * @param paths The paths to add to the library path
	 * @throws DiscoException If there is a problem loading the library path or extending it
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
			throw new DiscoException( "Error extending Java library path ("+
			                          throwable.getClass().getSimpleName()+"): "+
			                          throwable.getMessage(),
			                          throwable );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Class: DiscoClassLoader   ////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A custom classloader to allow extending the classpath at runtime.
	 * <p>
	 * Acts as a child of an existing classloader, transitively retaining all existing access.
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
