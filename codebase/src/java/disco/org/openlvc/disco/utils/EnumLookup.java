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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;

public class EnumLookup<E extends Enum<E>>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Class<? extends E> enumClasz;
	private Function<E,Number> valueMapper;
	private E defaultValue;
	
	private Map<Number,E> cache;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EnumLookup( Class<? extends E> e,
	                   Function<E,Number> valueMapper,
	                   E defaultValue )
	{
		this.cache = null; // Created on first call to fromValue()
		this.enumClasz = e;
		this.valueMapper = valueMapper;
		this.defaultValue = defaultValue;
	}

	public EnumLookup( Class<? extends E> e,
	                   Function<E,Number> valueMapper )
	{
		this( e, valueMapper, null );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public E fromValue( Number value )
	{
		synchronized( this )
		{
			if( cache == null )
			{
				cache = new HashMap<>();
				for( E constant : enumClasz.getEnumConstants() )
				{
					Number key = this.valueMapper.apply( constant );
					cache.put( key, constant );
				}
			}
		}
		
		E constant = cache.get( value );
		if( constant == null )
		{
			if( DiscoConfiguration.isSet(Flag.Strict) || defaultValue == null )
				throw new IllegalArgumentException( value+" not a valid "+enumClasz.getName() );
			
			constant = defaultValue;
		}

		return constant;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
