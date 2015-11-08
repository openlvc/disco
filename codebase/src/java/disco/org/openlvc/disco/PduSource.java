/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco;

import org.openlvc.disco.pdu.PDU;

/**
 * A PduSource is the place that PDU's will emerge from as they are received or read from
 * a {@link IProvider}. 
 */
public class PduSource
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private IProvider provider;
	private IPduReceiver receiver;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PduSource( IProvider provider )
	{
		this.provider = provider;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void setReceiver( IPduReceiver receiver )
	{
		this.receiver = receiver;
	}
	
	/**
	 * The following has been received from the {@link IProvider} for processing.
	 */
	public void queueForInjest( byte[] array )
	{
		
	}

	/**
	 * The given {@link PDU} has been injested and is ready for processing, queue
	 * it up to be sent to the receive in a dedicated thread so that we are not chewing
	 * up injest resources.
	 */
	public void queueForProcess( PDU pdu )
	{
		
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
