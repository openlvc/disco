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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.connection.rpr.mappers.IObjectMapper;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.pdu.PDU;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;

public abstract class ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass objectClass;
	private ObjectInstanceHandle objectHandle;
	private AttributeHandleValueMap attributes;
	private IObjectMapper mapper;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public abstract void fromPdu( PDU pdu );

	public abstract PDU toPdu();
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public void setObjectClass( ObjectClass objectClass )
	{
		this.objectClass = objectClass;
	}

	public ObjectInstanceHandle getObjectHandle()
	{
		return objectHandle;
	}

	public void setObjectHandle( ObjectInstanceHandle objectHandle )
	{
		this.objectHandle = objectHandle;
	}

	public ObjectClass getObjectClass()
	{
		return objectClass;
	}

	public AttributeHandleValueMap getObjectAttributes()
	{
		return this.attributes;
	}
	
	public void setObjectAttributes( AttributeHandleValueMap attributes )
	{
		this.attributes = attributes;
	}
	
	public void setMapper( IObjectMapper mapper )
	{
		this.mapper = mapper;
	}
	
	public IObjectMapper getMapper()
	{
		return this.mapper;
	}	

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
