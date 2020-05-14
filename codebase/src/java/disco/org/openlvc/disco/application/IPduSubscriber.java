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
package org.openlvc.disco.application;

import org.openlvc.disco.IPduListener;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

/**
 * A {@link IPduSubscriber} has a registered interest in the reception of PDUs of a specific type.
 * When a {@link PduStore} receives PDUs of the registered type, all subscribers will be notified.
 * <p/>
 * 
 * Differences between {@link IPduSubscriber} and {@link IPduListener}:
 * <ul>
 *   <li>Subscribers register an interest in a particular PDU type, not all PDUs</li>
 *   <li>Subscribers are registered against a {@link PduStore}, not an {@link OpsCenter}</li>
 *   <li>A store can have multiple subscribers for a particular PDU type, but an OpsCenter
 *       can only have a single registered {@link IPduListener}</li>
 * </ul>
 */
public interface IPduSubscriber
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * A PDU that this implementation is interested in has been received.
	 *
	 * @param type The type of PDU that was received
	 * @param pdu  The PDU itself
	 */
	public void pduReceived( PduType type, PDU pdu );
}
