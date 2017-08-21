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
    		
    		File parentDirectory = path.getParentFile();
    		parentDirectory.mkdirs();
    		path.createNewFile();
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error creating file path: "+path.getAbsolutePath(), e );
		}
	}
}
