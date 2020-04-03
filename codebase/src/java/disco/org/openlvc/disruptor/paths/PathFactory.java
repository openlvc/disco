/*
 *   Copyright 2019 Open LVC Project.
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
package org.openlvc.disruptor.paths;

import java.util.Map;

import org.json.simple.JSONObject;
import org.openlvc.disco.utils.JsonUtils;
import org.openlvc.disco.utils.LLA;

/**
 * Factory class which produces path definitions from JSON objects
 */
public class PathFactory
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// JSON configuration keys
	private static final String PATH_TYPE = "type";
	private static final String POLYGON_PATH = "polygon";
	private static final String CIRCLE_PATH  = "circle";
	private static final String LINE_PATH    = "line";

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
	 * Create a path based on a JSON definition
	 * 
	 * @param json the {@link JSONObject} which contains the path definition
	 * @param locationLookup a {@link Map} which contains well-known location aliases for lookup
	 * @return a {@link IPath} instance
	 */
	public static IPath fromJson( JSONObject json, Map<String,LLA> locationLookup )
	{
		if( json == null )
		{
			throw new RuntimeException( "Could not create path - no definition was supplied." );
		}
		
		String pathType = JsonUtils.getString( json, PATH_TYPE, null );
		if( pathType == null || "".equals(pathType.trim()) )
		{
			throw new RuntimeException( "Could not create path - no path type was specified." );
		}
		
		if( pathType.equalsIgnoreCase( LINE_PATH ) )
		{
			return LinePath.fromJson( json, locationLookup );
		}
		else if( pathType.equalsIgnoreCase( CIRCLE_PATH ) )
		{
			return CircularPath.fromJson( json, locationLookup );
		}
		else if( pathType.equalsIgnoreCase( POLYGON_PATH ) )
		{
			return PolygonPath.fromJson( json, locationLookup );
		}
		
		throw new RuntimeException( "Could not create path - unknown path type '"+pathType+"' was specified." );
	}
}
