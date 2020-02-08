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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.objects.RadioTransmitter;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;

/**
 * The RprConverter is the central management point for turning DIS data into HLA data, and
 * vice versa.
 * <p/>
 * 
 * As PDUs are received from the DIS application, we use a set of mappers (instances
 * of {@link IObjectMapper} and {@link IInteractionMapper}) to convert between their DIS and HLA
 * forms, and then hand them off to the RTI.
 * <p/>

 * On the reverse path, everything is the same, with reflections and interactions handed off
 * to mappers to turn them into PDUs, which in turn are handed off to the OpsCenter for delivery.
 */
public class RprConverter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Logger logger;
	protected ObjectModel model;
	
	// DIS Mappers
	private Map<PduType,IObjectMapper> objectMappersByDisType;
	private Map<PduType,IInteractionMapper> interactionMappersByDisType;

	// DIS Object Storage
	private Map<EntityId,RadioTransmitter> disTransmitters;

	// HLA Mappers
	private Map<ObjectClass,IObjectMapper> objectMappersByHlaType;
	private Map<InteractionClass,IInteractionMapper> interactionMappersByHlaType;
	
	// HLA Object Storage
	private Map<ObjectInstanceHandle,ObjectInstance> hlaObjects;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RprConverter( ObjectModel model, Logger logger )
	{
		this.logger = logger;
		this.model = model;
	
		// DIS Mappers
		this.objectMappersByDisType = new HashMap<>();
		this.interactionMappersByDisType = new HashMap<>();

		// DIS Object Storage
		this.disTransmitters = new HashMap<>();
		
		// HLA Mappers
		this.objectMappersByHlaType = new HashMap<>();
		this.interactionMappersByHlaType = new HashMap<>();
		
		// HLA Object Storage
		this.hlaObjects = new HashMap<>();

		//
		// Mapper Registration
		//
		// Register Object Mappers
		this.registerMapper( new TransmitterMapper(this) );
		
		// Register Interaction Mappers
		this.registerMapper( new SignalMapper(this) );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Mapper Management Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void registerMapper( IObjectMapper mapper )
	{
		this.objectMappersByDisType.put( mapper.getSupportedPduType(), mapper );
		this.objectMappersByHlaType.put( mapper.getSupportedHlaClass(), mapper );
	}

	private void registerMapper( IInteractionMapper mapper )
	{
		this.interactionMappersByDisType.put( mapper.getSupportedPduType(), mapper );
		this.interactionMappersByHlaType.put( mapper.getSupportedHlaClass(), mapper );
	}

	////////////////////////////////////////////////////////////////////////////////////
	///  DIS -> HLA Support Methods    /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Given a PDU, map it to an HLA object or interaction and send it out (as an update
	 * or an interaction) via the given RTIambassador
	 * 
	 * @param pdu    The PDU with the data we need to map
	 * @param rtiamb The RTIambassador to send the update/interaction out through
	 */
	public void sendDisToHla( PDU pdu, RTIambassador rtiamb )
	{
		if( this.objectMappersByDisType.containsKey(pdu.getType()) )
		{
			// This PDU maps to an HLA object type -- convert and send
			this.objectMappersByDisType.get(pdu.getType()).sendDisToHla( pdu, rtiamb );
		}
		else if( this.interactionMappersByDisType.containsKey(pdu.getType()) )
		{
			// This PDU maps to an HLA interaction type -- convert and send
			this.interactionMappersByDisType.get(pdu.getType()).sendDisToHla( pdu, rtiamb );
		}
		else
		{
			logger.warn( "(RprConnection) PDU type [%s] not supported yet", pdu.getType() );
		}
	}

	/////////////////////////////////////////////
	///  Object Storage Methods   ///////////////
	/////////////////////////////////////////////
	public void addDisTransmitter( EntityId disId, RadioTransmitter hlaObject )
	{
		logger.debug( "(DIS->HLA) Store RadioTransmitter for DIS id [%s]; HLA Object [%s]",
		              disId, hlaObject.getObjectHandle() );
		this.disTransmitters.put( disId, hlaObject );
	}
	
	public RadioTransmitter getDisTransmitter( EntityId disId )
	{
		return this.disTransmitters.get( disId );
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	///  HLA -> DIS Support Methods    /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A discovery has been received for an HLA object instance. Create a local version of the
	 * object and link it to a mapper so that we can unmarshal updates later.
	 * 
	 * @param objectHandle The HLA handle for the individual object
	 * @param classHandle  The HLA handle for the class that the object is of
	 */
	public void discoverHlaObject( ObjectInstanceHandle objectHandle, ObjectClassHandle classHandle )
	{
		logger.debug( "(HLA->DIS) Discovered object [%s] of class [%s]", objectHandle, classHandle );

		// Find the metadata information we have for the class
		ObjectClass objectClass = model.getObjectClass( classHandle );
		if( objectClass == null )
			throw new DiscoException( "Object discover for unknown type: class handle="+classHandle );

		// Find the mapper for this type of class
		IObjectMapper mapper = this.objectMappersByHlaType.get( objectClass );
		if( mapper == null )
			throw new DiscoException( "Cannot find type mapper for class "+objectClass.getQualifiedName() );

		// Using the mapper, make a new instance of the class and store it for later use
		ObjectInstance hlaObject = mapper.createObject( objectHandle );
		hlaObject.setObjectClass( objectClass );
		hlaObject.setObjectHandle( objectHandle );
		hlaObject.setMapper( mapper );

		// Store the object for later reference
		this.hlaObjects.put( objectHandle, hlaObject );
	}

	/**
	 * An attribute reflection has been received for the given object. We must find the mapper
	 * responsible for getting this information out, turn it into a PDU and hand it off to the
	 * OpsCenter.
	 * 
	 * @param objectHandle The HLA handle of the object we got a reflection for
	 * @param attributes   The attributes we received
	 * @param opscenter    The destination to send the generated PDU
	 */
	public void sendHlaToDis( ObjectInstanceHandle objectHandle,
	                          AttributeHandleValueMap attributes,
	                          OpsCenter opscenter )
	{
		logger.trace( "(HLA->DIS) Received update for [%s], with [%d] attributes", objectHandle, attributes.size() );

		// Find the mapper for this object handle
		ObjectInstance hlaObject = this.hlaObjects.get( objectHandle );
		if( hlaObject == null )
		{
			logger.warn( "(HLA->DIS) Reflection for object prior to discovery: "+objectHandle );
			return;
		}

		// Look up the mapper for this type of object
		IObjectMapper mapper = hlaObject.getMapper();

		try
		{
			// Do the conversion to a PDU
			mapper.sendHlaToDis( hlaObject, attributes, opscenter );
		}
		catch( DiscoException de )
		{
			logger.warn( "Error converting HLA update to DIS: "+de.getMessage(), de );
		}
	}

	/**
	 * An interaction has been received from the RTI. We must map it to its appropriate PDU
	 * format and send it out via the OpsCenter.
	 * 
	 * @param classHandle The class of interaction that was received
	 * @param parameters  The received parameters
	 * @param opscenter   The OpsCenter to hand the PDU over to
	 */
	public void sendHlaToDis( InteractionClassHandle classHandle,
	                          ParameterHandleValueMap parameters,
	                          OpsCenter opscenter )
	{
		logger.trace( "(HLA->DIS) Received interaction for [%s], with [%d] parameters", classHandle, parameters.size() );

		// Find the class of object that this is
		InteractionClass theClass = model.getInteractionClass( classHandle );
		if( theClass == null )
		{
			logger.warn( "(HLA->DIS) Interaction received for unknown class: "+classHandle );
			return;
		}
		
		// Find the mapper for this class
		IInteractionMapper mapper = this.interactionMappersByHlaType.get( theClass );
		if( mapper == null )
		{
			logger.warn( "(HLA->DIS) Interaction received but no mapper found: "+theClass.getQualifiedName() );
			return;
		}
		
		try
		{
			// Do the conversion to a PDU
			mapper.sendHlaToDis( theClass, parameters, opscenter );
		}
		catch( DiscoException de )
		{
			logger.warn( "Error converting HLA interaction to DIS: "+de.getMessage(), de );
		}
	}
	
	/**
	 * We have received an object removal notification from the RTI. Remove the local cached
	 * version of the object
	 * 
	 * @param objectHandle The handle of the object that was deleted
	 */
	public void removeHlaObject( ObjectInstanceHandle objectHandle )
	{
		logger.debug( "(HLA->DIS) Received delete for object [%s]", objectHandle );
		this.hlaObjects.remove( objectHandle );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
