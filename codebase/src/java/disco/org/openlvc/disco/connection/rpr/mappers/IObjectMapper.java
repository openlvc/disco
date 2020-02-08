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

import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;

/**
 * Defines the interface that is used to map HLA Objects to DIS PDUs and vice versa.
 */
public interface IObjectMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	//
	// Supported HLA/DIS Type Methods
	//
	public PduType getSupportedPduType();
	
	public ObjectClass getSupportedHlaClass();
	
	//
	// Object Creation Methods
	//
	public ObjectInstance createObject( ObjectInstanceHandle handle );
	
	//
	// Conversion methods for DIS->HLA and HLA->DIS
	//
	public void sendDisToHla( PDU pdu, RTIambassador rtiamb );
	
	public void sendHlaToDis( ObjectInstance hlaObject, AttributeHandleValueMap attributes, OpsCenter opscenter );
}
