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

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected FederateAmbassador( RprConnection connection )
	{
		this.connection = connection;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Object Discovery & Removal Methods   ///////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	// 6.9
	public void discoverObjectInstance( ObjectInstanceHandle theObject,
	                                    ObjectClassHandle theClass,
	                                    String objectName )
	{
		connection.receiveHlaDiscover( theObject, theClass );
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
		connection.receiveHlaRemove( theObject );
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
		connection.receiveHlaReflection( theObject, attributes );
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
		connection.receiveHlaInteraction( theClass, parameters );
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
