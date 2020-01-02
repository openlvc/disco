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
package org.openlvc.disco.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonUtils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final JSONParser JSON_PARSER = new JSONParser();
	
	private static final Pattern DIS_DELIMITER_REGEX = Pattern.compile( "[^\\d]" );
	
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
	///////////////////////////////////////////////////////////////////////////////////////
	// READING "ROOT" LEVEL FROM JSON FILE
	///////////////////////////////////////////////////////////////////////////////////////
	
	public static JSONObject readObjectFromFile( File jsonFile )
	{
		try( InputStreamReader jsonstream = new InputStreamReader(new FileInputStream(jsonFile)) )
		{
			Object temp = JSON_PARSER.parse( jsonstream );
			if( temp instanceof JSONObject )
				return (JSONObject)temp;

			throw new RuntimeException( "JSON file '" + jsonFile.getAbsolutePath() +
			                            "' did not have a root level object." );
		}
		catch( Exception e )
		{
			throw new RuntimeException( "Problem parsing JSON file '" + jsonFile.getAbsolutePath() +
			                            "': " + e.getMessage(), e );
		}
	}
	
	public static JSONArray readArrayFromFile( File jsonFile )
	{
		try( InputStreamReader jsonstream = new InputStreamReader(new FileInputStream(jsonFile)) )
		{
			Object temp = JSON_PARSER.parse( jsonstream );
			if( temp instanceof JSONArray )
				return (JSONArray)temp;
			
			throw new RuntimeException( "JSON file '" + jsonFile.getAbsolutePath() +
			                            "' did not have a root level array." );
		}
		catch( Exception e )
		{
			throw new RuntimeException( "Problem parsing JSON file '" + jsonFile.getAbsolutePath() +
			                            "': " + e.getMessage(), e );
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// GENERIC/COMMON JSON DATA TYPE EXTRACTIONS
	///////////////////////////////////////////////////////////////////////////////////////
	
	public static JSONObject getJSONObject( JSONObject src, String key, JSONObject defaultValue )
	{
		Object temp = get( src, key );
		if( temp instanceof JSONObject )
			return (JSONObject)temp;
		return defaultValue;
	}

	public static JSONArray getJSONArray( JSONObject src, String key, JSONArray defaultValue )
	{
		Object temp = get( src, key );
		if( temp instanceof JSONArray )
			return (JSONArray)temp;
		return defaultValue;
	}

	public static String getString( JSONObject src, String key, String defaultValue )
	{
		Object temp = get( src, key );
		if( temp instanceof String )
			return (String)temp;
		return defaultValue;
	}

	public static int getInteger( JSONObject src, String key, int defaultValue )
	{
		Object temp = get( src, key );
		// note that integers in JSON are interpreted as longs, not integers
		if( temp instanceof Long )
			return ((Long)temp).intValue();
		return defaultValue;
	}

	public static double getDouble( JSONObject src, String key, double defaultValue )
	{
		Object temp = get( src, key );
		if( temp instanceof Double )
			return (Double)temp;
		if( temp instanceof Long )
			return ((Long)temp).doubleValue();
		return defaultValue;
	}

	public static boolean getBoolean( JSONObject src, String key, boolean defaultValue )
	{
		Object temp = get( src, key );
		if( temp instanceof Boolean )
			return (Boolean)temp;
		return defaultValue;
	}

	public static JSONObject getJSONObject( JSONArray src, int index, JSONObject defaultValue )
	{
		Object temp = get( src, index );
		if( temp instanceof JSONObject )
			return (JSONObject)temp;
		return defaultValue;
	}

	public static JSONArray getJSONArray( JSONArray src, int index, JSONArray defaultValue )
	{
		Object temp = get( src, index );
		if( temp instanceof JSONArray )
			return (JSONArray)temp;
		return defaultValue;
	}

	public static String getString( JSONArray src, int index, String defaultValue )
	{
		Object temp = get( src, index );
		if( temp instanceof String )
			return (String)temp;
		return defaultValue;
	}

	public static int getInteger( JSONArray src, int index, int defaultValue )
	{
		Object temp = get( src, index );
		// note that integers in JSON are interpreted as longs, not integers
		if( temp instanceof Long )
			return ((Long)temp).intValue();
		return defaultValue;
	}

	public static double getDouble( JSONArray src, int index, double defaultValue )
	{
		Object temp = get( src, index );
		if( temp instanceof Double )
			return (Double)temp;
		if( temp instanceof Long )
			return ((Long)temp).doubleValue();
		return defaultValue;
	}

	public static boolean getBoolean( JSONArray src, int index, boolean defaultValue )
	{
		Object temp = get( src, index );
		if( temp instanceof Boolean )
			return (Boolean)temp;
		return defaultValue;
	}

	public static Object get( JSONObject src, String key )
	{
		if( src == null || !src.containsKey( key ) )
			return null;
		return src.get( key );
	}

	public static Object get( JSONArray src, int index )
	{
		if( src == null || index < 0 || index > src.size() - 1 )
			return null;
		return src.get( index );
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// SPECIALIZED DATA EXTRACTIONS
	///////////////////////////////////////////////////////////////////////////////////////
	public static int[] getIntegerArray( JSONObject src, String key, int[] defaultValue )
	{
		JSONArray jsonArray = getJSONArray( src, key, null );
		if( jsonArray != null )
		{
			int[] result = new int[jsonArray.size()];

			for( int idx = 0; idx < jsonArray.size(); idx++ )
				result[idx] = JsonUtils.getInteger( jsonArray, idx, 0 );

			return result;
		}
		return defaultValue;
	}
	
	public static int[] getDISEnumeration( JSONObject src, String key )
	{
		return getDISEnumeration( src, key, null );
	}
	
	public static int[] getDISEnumeration( JSONObject src, String key, int[] defaultValue )
	{
		int[] result = new int[7];
		
		// try for array of integers first
		JSONArray disArray = getJSONArray( src, key, null );
		if( disArray != null )
		{
			for( int idx = 0; idx < result.length && idx < disArray.size(); idx++ )
				result[idx] = JsonUtils.getInteger( disArray, idx, 0 );
			
			return result;
		}
		
		// try for a string
		String disString = getString( src, key, null );
		if( disString != null )
		{
			// split string on non-numeric
			String[] parts = DIS_DELIMITER_REGEX.split( disString );

			for( int idx = 0; idx < parts.length && idx < result.length; idx++ )
			{
				try
				{
					result[idx] = Integer.parseInt( parts[idx].trim(), 10 );
				}
				catch( NumberFormatException nfe )
				{
					throw new RuntimeException( "Unexpected non-numeric value '" + result[idx] +
					                            "' in DIS enumeration '" + disString + "'" );
				}
			}

			return result;
		}
		
		if( defaultValue != null )
			return defaultValue;
		
		throw new RuntimeException( "A DIS enumeration must be an array of integers or " +
		                            "a '.' delimited string of integers." );
	}
	

}
