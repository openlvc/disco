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
package org.openlvc.disco.pdu;

import org.openlvc.disco.pdu.field.PduHeader;
import org.openlvc.disco.pdu.field.PduType;

public abstract class PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected PduHeader header;
	protected long received;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PDU( PduHeader header )
	{
		this.header = header;
		this.received = System.currentTimeMillis();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Each PDU has a specific type, as enumerated in {@link PduType}. 
	 */
	public final PduType getType()
	{
		if( header == null )
			throw new IllegalStateException( "The PDU does not contain a header" );
		
		return header.getPDUType();
	}

	public PduHeader getHeader()
	{
		return header;
	}
	
	public void setHeader( PduHeader header )
	{
		this.header = header;
	}
	
	public long getReceived()
	{
		return received;
	}
	
	public void setReceived( long received )
	{
		this.received = received;
	}
	
	/**
	 * Returns the length of this PDU's content section in bytes
	 * 
	 * @return An int value representing the length of this PDU's content section in bytes
	 */
	public abstract int getContentLength();

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
