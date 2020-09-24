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
package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import java.util.Collection;

import org.openlvc.disco.connection.rpr.custom.dcss.types.array.ArrayOfDomain;
import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.Domain;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class WeatherRequestData extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RequestIdentifier id;
	private HLAfloat64BE timeOffset;
	private GeoPoint3D location;
	private ArrayOfDomain domains;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WeatherRequestData()
	{
		this.id = new RequestIdentifier();
		this.timeOffset = new HLAfloat64BE();
		this.location = new GeoPoint3D();
		this.domains = new ArrayOfDomain();
		
		this.add( this.id, 
		          this.timeOffset, 
		          this.location, 
		          this.domains );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public RequestIdentifier getId()
	{
		return this.id;
	}
	
	public HLAfloat64BE getTimeOffset()
	{
		return this.timeOffset;
	}
	
	public GeoPoint3D getLocation()
	{
		return this.location;
	}
	
	public ArrayOfDomain getDomains()
	{
		return this.domains;
	}
	
	public void setDomains( Collection<? extends Domain> domains )
	{
		// The HLAvariableArray doesn't have a clear() method, so we just resize to 0 instead
		if( this.domains.size() > 0 )
			this.domains.resize( 0 );
		
		for( Domain domain : domains )
			this.domains.addElement( new EnumHolder<Domain>(domain) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
