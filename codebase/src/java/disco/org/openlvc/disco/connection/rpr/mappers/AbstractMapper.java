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

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.rpr.ObjectStore;
import org.openlvc.disco.connection.rpr.RprConnection;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.exceptions.RTIexception;

/**
 * Parent type to all mappers. Just exists to capture the common infrastructure pieces we
 * want to pull out and use in pretty much every mapper. Those attributes are declared protected
 * so that they are directly available to child mappers.
 */
public class AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected RprConnection rprConnection;
	protected ObjectStore   objectStore;
	protected Logger        logger;
	protected OpsCenter     opscenter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected AbstractMapper( RprConnection connection )
	{
		this.rprConnection = connection;
		this.objectStore = rprConnection.getObjectStore();
		this.logger = rprConnection.getLogger();
		this.opscenter = rprConnection.getOpsCenter();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private final RTIambassador getRtiAmb()
	{
		return rprConnection.getRtiAmb();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Object Helpers   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void registerObjectInstance( ObjectInstance object )
		throws DiscoException
	{
		try
		{
			// register the object and store its handle
			ObjectInstanceHandle objectHandle =
				getRtiAmb().registerObjectInstance( object.getObjectClass().getHandle() );
			object.setObjectHandle( objectHandle );
			
			// create an AHVM for the object once, with enough room to hold all attributes
			int size = object.getObjectClass().getAllAttributes().size();
			AttributeHandleValueMap ahvm = getRtiAmb().getAttributeHandleValueMapFactory().create( size );
			object.setObjectAttributes( ahvm );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	protected void sendAttributeUpdate( ObjectInstance object, AttributeHandleValueMap attributes )
		throws DiscoException
	{
		try
		{
			// update the cached attributes in the object
			// send the attributes out
			getRtiAmb().updateAttributeValues( object.getObjectHandle(),
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
	                                ParameterHandleValueMap parameters )
	{
		try
		{
			getRtiAmb().sendInteraction( interaction.getInteractionClass().getHandle(), parameters, null );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	protected ParameterHandleValueMap createParameters( InteractionClass clazz )
	{
		try
		{
			return getRtiAmb().getParameterHandleValueMapFactory().create( clazz.getAllParameters().size() );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
