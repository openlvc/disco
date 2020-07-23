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

import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.rpr.ObjectStore;
import org.openlvc.disco.connection.rpr.RprConnection;
import org.openlvc.disco.connection.rpr.model.FomHelpers;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.PubSub;
import org.openlvc.disco.connection.rpr.objects.InteractionInstance;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleSet;
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
public abstract class AbstractMapper
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
	protected AbstractMapper()
	{
		this.rprConnection = null;  // set in initialize()
		this.objectStore = null;    // set in initialize()
		this.logger = null;         // set in initialize()
		this.opscenter = null;      // set in initialize()
	}
	
//	protected AbstractMapper( RprConnection connection )
//	{
//		this.rprConnection = connection;
//		this.objectStore = rprConnection.getObjectStore();
//		this.logger = rprConnection.getLogger();
//		this.opscenter = rprConnection.getOpsCenter();
//	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private final RTIambassador getRtiAmb()
	{
		return rprConnection.getRtiAmb();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Initialization   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method is called when a mapper is added to a {@link RprConnection}. It allows the
	 * mapper to get access to all the internal state that it requires. A mapper will not be
	 * registered or passed messages until this method has been successfully called.
	 * <p/>
	 * Concrete types are expected to override this method and perform their FOM lookups 
	 * @param connection
	 */
	public void initialize( RprConnection connection ) throws DiscoException
	{
		// Do our local initalization
		this.rprConnection = connection;
		this.objectStore = rprConnection.getObjectStore();
		this.logger = rprConnection.getLogger();
		this.opscenter = rprConnection.getOpsCenter();
		
		// Tell the concrete type to initialize itself
		this.initialize();
	}

	/**
	 * This method is to be implemented by all child types of {@link AbstractMapper} and will be
	 * called once the mapper itself has performed its internal initialization after being linked
	 * to a {@link RprConnection}. This will give each mapper the opportunity to initialize itself
	 * on startup.
	 * 
	 * @throws DiscoException Throw if there is a problem initializing the concrete mapper
	 */
	protected abstract void initialize() throws DiscoException;

	/**
	 * This allows the mapper to declare the PDU types it supports converting to/from.
	 * @return The set of all PDU types it supports.
	 */
	public abstract Collection<PduType> getSupportedPdus();
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Object Helpers   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void publishAndSubscribe( ObjectClass clazz )
	{
		logger.debug( "[ObjectClass] Publish and subscribe [%s]", clazz.getQualifiedName() );
		FomHelpers.pubsub( getRtiAmb(), PubSub.Both, clazz );
	}
	
	protected void publishAndSubscribe( InteractionClass clazz )
	{
		logger.debug( "[InteractionClass] Publish and subscribe [%s]", clazz.getQualifiedName() );
		FomHelpers.pubsub( getRtiAmb(), PubSub.Both, clazz );
	}
	
	protected void registerObjectInstance( ObjectInstance object )
		throws DiscoException
	{
		try
		{
			// register the object and store its handle
			ObjectInstanceHandle objectHandle =
				getRtiAmb().registerObjectInstance( object.getObjectClass().getHandle() );
			object.setObjectHandle( objectHandle );
			object.setObjectName( getRtiAmb().getObjectInstanceName(objectHandle) );
			
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

	protected void requestAttributeUpdate( ObjectInstance object )
	{
		try
		{
			// Generate the attribute handle set
			AttributeHandleSet ahs = getRtiAmb().getAttributeHandleSetFactory().create();
			object.getObjectClass().getAllAttributes().forEach( ac -> ahs.add(ac.getHandle()) );
			
			// Sent the request
			getRtiAmb().requestAttributeValueUpdate( object.getObjectHandle(), ahs, null );
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
