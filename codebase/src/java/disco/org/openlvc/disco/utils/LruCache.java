/*
 *   Copyright 2025 Open LVC Project.
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

import java.util.LinkedHashMap;
import java.util.Map;

// based on (not thread safe) implementation in https://stackoverflow.com/questions/40239485/concurrent-lru-cache-implementation

/**
 * An implementation of a Least Recently Used Cache (LRU Cache).
 * Not thread safe; synchronization should be performed on the map when accessed.
 */
public class LruCache<K,V> extends LinkedHashMap<K,V>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	@java.io.Serial
	private static final long serialVersionUID = 6228500846571341824L;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final int capacity;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LruCache( int capacity )
	{
		// load factor of 1.0 so it doesn't auto resize
		super( capacity + 1, 1.0f, true );
		this.capacity = capacity;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getCapacity()
	{
		return this.capacity;
	}

	@Override
	protected boolean removeEldestEntry( final Map.Entry<K,V> eldest )
	{
        return super.size() > capacity;
    }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
