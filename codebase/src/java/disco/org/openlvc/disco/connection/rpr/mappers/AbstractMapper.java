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
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.FomHelpers;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.connection.rpr.model.PubSub;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Initialization   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
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
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Helper Methods: Objects   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void publishAndSubscribe( ObjectClass clazz )
	{
		logger.debug( "[ObjectClass] Publish and subscribe [%s]", clazz.getQualifiedName() );
		FomHelpers.pubsub( rti(), PubSub.Both, clazz );
	}

	/**
	 * Tell the RTI to register the given object instance. Will call the HLA's
	 * <code>registerObjectInstance()</code> method.
	 * 
	 * @param object The {@link ObjectInstance} to register.
	 * @throws DiscoException
	 */
	protected void registerObjectInstance( ObjectInstance object )
		throws DiscoException
	{
		try
		{
			// register the object and store its handle
			ObjectInstanceHandle objectHandle =
				rti().registerObjectInstance( object.getObjectClass().getHandle() );
			object.setObjectHandle( objectHandle );
			object.setObjectName( rti().getObjectInstanceName(objectHandle) );
			
			// create an AHVM for the object once, with enough room to hold all attributes
			int size = object.getObjectClass().getAllAttributes().size();
			AttributeHandleValueMap ahvm = rti().getAttributeHandleValueMapFactory().create( size );
			object.setObjectAttributes( ahvm );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	protected AttributeHandleValueMap createAttributes( ObjectClass clazz )
		throws DiscoException
	{
		try
		{
			return rti().getAttributeHandleValueMapFactory().create( clazz.getAllAttributes().size() );
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
			rti().updateAttributeValues( object.getObjectHandle(), attributes, null );
			
			object.setLastUpdatedTimeToNow();
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
			AttributeHandleSet ahs = rti().getAttributeHandleSetFactory().create();
			object.getObjectClass().getAllAttributes().forEach( ac -> ahs.add(ac.getHandle()) );
			
			// Sent the request
			rti().requestAttributeValueUpdate( object.getObjectHandle(), ahs, null );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Helper Methods: Interactions   /////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void publishAndSubscribe( InteractionClass clazz )
	{
		logger.debug( "[InteractionClass] Publish and subscribe [%s]", clazz.getQualifiedName() );
		FomHelpers.pubsub( rti(), PubSub.Both, clazz );
	}
	
	protected void sendInteraction( InteractionInstance interaction,
	                                ParameterHandleValueMap parameters )
	{
		try
		{
			rti().sendInteraction( interaction.getInteractionClass().getHandle(),
			                       parameters,
			                       null );
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
			return rti().getParameterHandleValueMapFactory().create( clazz.getAllParameters().size() );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( rtie.getMessage(), rtie );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Helper Methods: DataElement   //////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encode the given source <code>DatElement</code> into the given map, associating it with the
	 * identified parameter class.
	 * 
	 * @param source The <code>DatElement</code> we want to encode into the map
	 * @param pc     The <code>ParameterClass</code> with the handle we should link the value with
	 * @param map    The HLA map we should encode into
	 */
	protected final void hlaEncode( DataElement source,
	                                ParameterClass pc,
	                                ParameterHandleValueMap map )
	{
		ByteWrapper wrapper = new ByteWrapper( source.getEncodedLength() );
		source.encode( wrapper );
		map.put( pc.getHandle(), wrapper.array() );
	}

	/**
	 * Encode the given source <code>DatElement</code> into the given map, associating it with the
	 * identified attribute class.
	 * 
	 * @param source The <code>DatElement</code> we want to encode into the map
	 * @param pc     The <code>AttributeClass</code> with the handle we should link the value with
	 * @param map    The HLA map we should encode into
	 */
	protected final void hlaEncode( DataElement source,
	                                AttributeClass ac,
	                                AttributeHandleValueMap map )
	{
		ByteWrapper wrapper = new ByteWrapper( source.getEncodedLength() );
		source.encode( wrapper );
		map.put( ac.getHandle(), wrapper.array() );
	}

	/**
	 * Decode the parameter represented by the given {@link ParameterClass} and store it in the
	 * target {@link DataElement}. The value will be extrated from the given map.
	 * 
	 * @param target The location we want to decode in
	 * @param pc     The parameter class we should look in the map fore
	 * @param map    The map we should get the value from.
	 * @throws DiscoException If there is an HLA exception while trying to decode.
	 */
	protected final void hlaDecode( DataElement target,
	                                ParameterClass pc,
	                                ParameterHandleValueMap map )
		throws DiscoException
	{
		byte[] bytes = map.get( pc.getHandle() );
		if( bytes != null )
		{
			ByteWrapper wrapper = new ByteWrapper( bytes );
			try
			{
				target.decode( wrapper );
			}
			catch( DecoderException de )
			{
				throw new DiscoException( de.getMessage(), de );
			}
		}
	}

	/**
	 * Decode the attribute represented by the given {@link AttributeClass} and store it in the
	 * target {@link DataElement}. The value will be extrated from the given map.
	 * 
	 * @param target The location we want to decode in
	 * @param ac     The attribute class we should look in the map fore
	 * @param map    The map we should get the value from.
	 * @throws DiscoException If there is an HLA exception while trying to decode.
	 */
	protected final void hlaDecode( DataElement target,
	                                AttributeClass ac,
	                                AttributeHandleValueMap map )
		throws DiscoException
	{
		byte[] bytes = map.get( ac.getHandle() );
		if( bytes != null )
		{
			ByteWrapper wrapper = new ByteWrapper( bytes );
			try
			{
				target.decode( wrapper );
			}
			catch( DecoderException de )
			{
				throw new DiscoException( de.getMessage(), de );
			}
		}
	}	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected final RTIambassador rti()
	{
		return rprConnection.getRtiAmb();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	
	
	
	
	@Deprecated
	protected static void serializeInto( DataElement source,
	                                     ParameterClass pc,
	                                     ParameterHandleValueMap target )
	{
		ByteWrapper wrapper = new ByteWrapper( source.getEncodedLength() );
		source.encode( wrapper );
		target.put( pc.getHandle(), wrapper.array() );
	}

	@Deprecated
	protected static void deserializeInto( AttributeHandleValueMap source,
	                                       AttributeClass ac, 
	                                       DataElement target ) 
		throws DecoderException
	{
		byte[] raw = source.get( ac.getHandle() );
		if( raw != null )
		{
			ByteWrapper wrapper = new ByteWrapper( raw );
			target.decode( wrapper );
		}
	}

	@Deprecated
	protected static void deserializeInto( ParameterHandleValueMap source,
	                                       ParameterClass pc, 
	                                       DataElement target ) 
		throws DecoderException
	{
		byte[] raw = source.get( pc.getHandle() );
		if( raw != null )
		{
			ByteWrapper wrapper = new ByteWrapper( raw );
			target.decode( wrapper );
		}
	}
	
	// TIM: This is a param reversal of above, because when I read `deserializeInto` I anticipated
	//      that the first param will be what I want to deserialize into, with the latter params
	//      being the actual data
	@Deprecated
	protected static void deserializeInto( DataElement target,
	                                       ParameterHandleValueMap map,
	                                       ParameterClass clazz )
		throws DecoderException
	{
		byte[] bytes = map.get( clazz.getHandle() );
		if( bytes != null )
		{
			ByteWrapper wrapper = new ByteWrapper( bytes );
			target.decode( wrapper );
		}
	}
	
	// TIM: See RprConnection#receiveHlaInteraction()
	// TIM: See IRCChannelMessage#newInteractionInstance()
	@Deprecated
	public <X extends InteractionInstance> X newInteractionInstance( ParameterHandleValueMap parameters )
		throws DecoderException
	{
		return null;
	}


}
