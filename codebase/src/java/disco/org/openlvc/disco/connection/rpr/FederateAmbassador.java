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
package org.openlvc.disco.connection.rpr;

import org.apache.logging.log4j.Logger;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.TransportationTypeHandle;

public class FederateAmbassador extends NullFederateAmbassador
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RprConnection connection;
	private Logger logger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected FederateAmbassador( RprConnection connection )
	{
		this.connection = connection;
		this.logger = this.connection.logger;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Object Discovery & Removal Methods   ///////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	// 6.9
	public void discoverObjectInstance( ObjectInstanceHandle theObject,
	                                    ObjectClassHandle theObjectClass,
	                                    String objectName )
	{
		try
		{
			connection.receiveHlaDiscover( theObject, theObjectClass );
		}
		catch( Exception e )
		{
			logger.warn( "Exception processing object discovery [id="+theObject+",class="+
			             theObjectClass+"]: "+e.getMessage(), e );
		}
	}

	// 6.9
	public void discoverObjectInstance( ObjectInstanceHandle theObject,
	                                    ObjectClassHandle theObjectClass,
	                                    String objectName,
	                                    FederateHandle producingFederate )
	{
		discoverObjectInstance( theObject, theObjectClass, objectName );
	}

	
	// 6.15
	public void removeObjectInstance( ObjectInstanceHandle theObject,
	                                  byte[] userSuppliedTag,
	                                  OrderType sentOrdering,
	                                  SupplementalRemoveInfo removeInfo )
	{
		try
		{
			connection.receiveHlaRemove( theObject );
		}
		catch( Exception e )
		{
			logger.warn( "Exception processing object removal [id="+theObject+"]: "+e.getMessage(), e );
		}
	}

	// 6.15
	@SuppressWarnings("rawtypes") 
	public void removeObjectInstance( ObjectInstanceHandle theObject,
	                                  byte[] userSuppliedTag,
	                                  OrderType sentOrdering,
	                                  LogicalTime theTime,
	                                  OrderType receivedOrdering,
	                                  SupplementalRemoveInfo removeInfo )
	{
		removeObjectInstance( theObject, userSuppliedTag, sentOrdering, removeInfo );
	}

	// 6.15
	@SuppressWarnings("rawtypes")
	public void removeObjectInstance( ObjectInstanceHandle theObject,
	                                  byte[] userSuppliedTag,
	                                  OrderType sentOrdering,
	                                  LogicalTime theTime,
	                                  OrderType receivedOrdering,
	                                  MessageRetractionHandle retractionHandle,
	                                  SupplementalRemoveInfo removeInfo )
	{
		removeObjectInstance( theObject, userSuppliedTag, sentOrdering, removeInfo );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Reflection Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void reflectAttributeValues( ObjectInstanceHandle theObject,
	                                    AttributeHandleValueMap attributes,
	                                    byte[] tag,
	                                    OrderType ordering,
	                                    TransportationTypeHandle transport,
	                                    SupplementalReflectInfo reflectInfo )
	{
		logger.trace( "HLA::Reflection Received >> object=%s, attribute=%d", theObject, attributes.size() );

		try
		{
			connection.receiveHlaReflection( theObject, attributes );
		}
		catch( Exception e )
		{
			logger.warn( "Exception processing attribute reflection [id="+theObject+"]: "+e.getMessage(), e );
		}
	}

	// 6.11
	@SuppressWarnings("rawtypes")
	public void reflectAttributeValues( ObjectInstanceHandle theObject,
	                                    AttributeHandleValueMap attributes,
	                                    byte[] tag,
	                                    OrderType ordering,
	                                    TransportationTypeHandle transport,
	                                    LogicalTime time,
	                                    OrderType receiveOrdering,
	                                    SupplementalReflectInfo reflectInfo )
	{
		reflectAttributeValues( theObject, attributes, tag, ordering, transport, reflectInfo );
	}

	// 6.11
	@SuppressWarnings("rawtypes")
	public void reflectAttributeValues( ObjectInstanceHandle theObject,
	                                    AttributeHandleValueMap attributes,
	                                    byte[] tag,
	                                    OrderType ordering,
	                                    TransportationTypeHandle transport,
	                                    LogicalTime time,
	                                    OrderType receivedOrdering,
	                                    MessageRetractionHandle retractionHandle,
	                                    SupplementalReflectInfo reflectInfo )
	{
		reflectAttributeValues( theObject, attributes, tag, ordering, transport, reflectInfo );
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Reflection Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	// 6.13
	public void receiveInteraction( InteractionClassHandle theClass,
	                                ParameterHandleValueMap parameters,
	                                byte[] tag,
	                                OrderType sentOrdering,
	                                TransportationTypeHandle theTransport,
	                                SupplementalReceiveInfo receiveInfo )
	{
		logger.trace( "HLA::Interaction Received >> class=%s, parameters=%d", theClass, parameters.size() );
		try
		{
			connection.receiveHlaInteraction( theClass, parameters );
		}
		catch( Exception e )
		{
			logger.warn( "Exception processing received interaction [id="+theClass+"]: "+e.getMessage(), e );
		}
	}

	// 6.13
	@SuppressWarnings("rawtypes")
	public void receiveInteraction( InteractionClassHandle theClass,
	                                ParameterHandleValueMap parameters,
	                                byte[] tag,
	                                OrderType sentOrdering,
	                                TransportationTypeHandle theTransport,
	                                LogicalTime theTime,
	                                OrderType receivedOrdering,
	                                SupplementalReceiveInfo receiveInfo )
	{
		receiveInteraction( theClass, parameters, tag, sentOrdering, theTransport, receiveInfo );
	}

	// 6.13
	@SuppressWarnings("rawtypes")
	public void receiveInteraction( InteractionClassHandle theClass,
	                                ParameterHandleValueMap parameters,
	                                byte[] tag,
	                                OrderType sentOrdering,
	                                TransportationTypeHandle theTransport,
	                                LogicalTime theTime,
	                                OrderType receivedOrdering,
	                                MessageRetractionHandle retractionHandle,
	                                SupplementalReceiveInfo receiveInfo )
	{
		receiveInteraction( theClass, parameters, tag, sentOrdering, theTransport, receiveInfo );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
