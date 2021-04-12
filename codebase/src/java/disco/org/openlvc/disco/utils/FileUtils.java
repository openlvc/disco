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
package org.openlvc.disco.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.DiscoException;

public class FileUtils
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
	 * Create the file identified by the given path. If it exists, just return. If it does
	 * not exist, create it and any path hierarchy needed and then return. An exception is
	 * thrown if there are any file or path creation issues.
	 * 
	 * @param path - abstract path to the file to create
	 * @throws DiscoException If we encounter any problems such as insufficient permissions, disk
	 *                        space issues or any of the many other common filesystem problems.
	 */
	public static void createFile( File path ) throws DiscoException
	{
		if( path.exists() )
			return;

		try
		{
			if( path.isDirectory() )
			{
				path.mkdirs();
				return;
			}

			// Try to get the directory we're in. This won't work if we have a path
			// like "file.txt" that is relative and missing any prefix. In that case,
			// getParent() will return null. Check for that first and then only try
			// to create the parent structure if we need to
			if( path.getParentFile() != null )
				path.getParentFile().mkdirs();

			// create the file
			path.createNewFile();
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error creating file path: " + path.getAbsolutePath(), e );
		}
	}
	
	/**
	 * Extract specified files from inside a jar to a specified directory, and returns a list of the
	 * new URLs. The main purpose of this is for using non-Java plugins etc. that need to load
	 * resources inside a Jar, but can't use the JVM to load in directly from a Jar.
	 *
	 * @param paths The relative paths of the files to extract from the jar 
	 * @param destDir The directory to put the new unjar'd files in
	 * @param loader The classloader to use to load the jar files from 
	 * @return A List of URLs to the new unjar'd files
	 * @throws DiscoException If there is an error creating the destDir or in extracting files from the jar
	 */
	public static List<URL> extractFilesFromJar( Iterable<String> paths, File destDir, ClassLoader loader ) throws DiscoException
	{
		List<URL> extractedFiles = new ArrayList<>();
		
		// ensure the destination directory exists, creating it if it doesn't
		try
		{
			Files.createDirectories( destDir.toPath() );
		} 
		catch ( IOException e )
		{
			throw new DiscoException( "Error creating directory: "+destDir, e );
		}
		
		try
		{
			// extract each file from the jar into the destDir
			for( String path : paths )
			{
				URL url = loader.getResource( path );
				File jarFile = new File( url.getFile() );
				File destFile = new File( destDir, jarFile.getName() );
				if( !destFile.exists() )
				{
					// copy data over to a regular file
					InputStream stream = url.openStream();
					Files.copy( stream, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
				}
				
				URL newUrl = destFile.toURI().toURL();
				extractedFiles.add( newUrl );
			}
		}
		catch( IOException e )
		{
			throw new DiscoException( "Error extracting files from jar", e);
		}
		
		return extractedFiles;
	}
}
