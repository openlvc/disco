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
package org.openlvc.disco.connection.rpr.mappers;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.exceptions.RTIexception;

/**
 * Parent mapper that can provide some useful utility methods to all children.
 */
public abstract class AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected ObjectModel model2;
	protected RprConverter rprConverter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected AbstractMapper( RprConverter rprConverter )
	{
		this.rprConverter = rprConverter;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Object Helpers   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	protected void registerObjectInstance( ObjectInstance object, RTIambassador rtiamb )
	{
		try
		{
			// register the object and store its handle
			ObjectInstanceHandle objectHandle =
				rtiamb.registerObjectInstance( object.getObjectClass().getHandle() );
			object.setObjectHandle( objectHandle );
			
			// create an AHVM for the object once, with enough room to hold all attributes
			int size = object.getObjectClass().getAllAttributes().size();
			AttributeHandleValueMap ahvm = rtiamb.getAttributeHandleValueMapFactory().create( size );
			object.setObjectAttributes( ahvm );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	protected void sendAttributeUpdate( ObjectInstance object,
	                                    AttributeHandleValueMap attributes,
	                                    RTIambassador rtiamb )
	{
		try
		{
			// update the cached attributes in the object
			// send the attributes out
			rtiamb.updateAttributeValues( object.getObjectHandle(),
			                              attributes,
			                              null );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}	
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Interaction Helpers   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void sendInteraction( InteractionInstance interaction,
	                                ParameterHandleValueMap parameters,
	                                RTIambassador rtiamb )
	{
		try
		{
			rtiamb.sendInteraction( interaction.getInteractionClass().getHandle(), parameters, null );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	protected ParameterHandleValueMap createParameters( InteractionClass clazz, RTIambassador rtiamb )
	{
		try
		{
			return rtiamb.getParameterHandleValueMapFactory().create( clazz.getAllParameters().size() );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
