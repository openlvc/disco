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
package org.openlvc.distributor.filters.pdu;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.distributor.filters.AbstractFilter;
import org.openlvc.distributor.filters.IFilter;
import org.openlvc.distributor.filters.Operator;

public class PduSiteIdFilter extends AbstractFilter implements IFilter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String FILTER_KEY = "pdu.siteId";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final short siteId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PduSiteIdFilter( Operator operator, String value )
	{
		super( FILTER_KEY, operator, value );
		this.siteId = Short.parseShort( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public final boolean matches( PDU pdu )
	{
		return operator.compare( siteId, pdu.getSiteId() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
