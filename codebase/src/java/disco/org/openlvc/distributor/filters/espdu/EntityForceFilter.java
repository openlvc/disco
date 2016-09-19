/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.distributor.filters.espdu;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.distributor.filters.IFilter;
import org.openlvc.distributor.filters.Operator;

public class EntityForceFilter extends AbstractEntityStateFilter implements IFilter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String FILTER_KEY = "entity.force";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityForceFilter( Operator operator, String value )
	{
		super( FILTER_KEY, operator, value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Return <code>true</code> if the given PDU matches the condition set in this filter.
	 */
	public final boolean matches( PDU pdu )
	{
		EntityStatePdu espdu = cast(pdu);
		if( espdu == null )
			return false;
		else
			return operator.compare( ForceId.valueOf(value), espdu.getForceID() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}