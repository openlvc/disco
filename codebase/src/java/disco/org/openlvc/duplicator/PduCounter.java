/*
 *   Copyright 2023 Open LVC Project.
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
package org.openlvc.duplicator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

public class PduCounter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Map<PduType,AtomicLong> counts;
	private AtomicLong total;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PduCounter()
	{
		this.counts = new HashMap<>();
		this.total = new AtomicLong();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void handle( PDU pdu )
	{
		PduType type = pdu.getType();
		AtomicLong count = counts.get( type );
		if( count == null )
		{
			count = new AtomicLong();
			counts.put( type, count );
		}
		
		count.incrementAndGet();
		this.total.incrementAndGet();
	}

	public void reset()
	{
		this.counts.clear();
		this.total.set( 0 );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getCount( PduType type )
	{
		AtomicLong count = counts.get( type );
		return count != null ? count.get() : 0;
	}
	
	public long getCount()
	{
		return total.get();
	}
	
	public Map<PduType,Long> getCounts()
	{
		return counts.entrySet().stream()
		                        .collect( Collectors.toMap(e -> e.getKey(), 
		                                                   e -> e.getValue().get()) );
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
