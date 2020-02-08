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

import org.openlvc.disco.connection.rpr.mappers.IInteractionMapper;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.pdu.PDU;

import hla.rti1516e.ParameterHandleValueMap;

public abstract class InteractionInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass interactionClass;
	private ParameterHandleValueMap parameters;
	private IInteractionMapper mapper;

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
	public InteractionClass getInteractionClass()
	{
		return interactionClass;
	}

	public void setInteractionClass( InteractionClass interactionClass )
	{
		this.interactionClass = interactionClass;
	}

	public ParameterHandleValueMap getParameters()
	{
		return parameters;
	}

	public void setParameters( ParameterHandleValueMap parameters )
	{
		this.parameters = parameters;
	}

	public IInteractionMapper getMapper()
	{
		return mapper;
	}

	public void setMapper( IInteractionMapper mapper )
	{
		this.mapper = mapper;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
