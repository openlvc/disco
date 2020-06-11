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
package org.openlvc.disco.pdu;

import java.io.IOException;

import org.openlvc.disco.pdu.field.PduType;

/**
 * This class represetns a raw PDU whose body is not parsed, but rather just stored.
 * We pull the PDU header out so that we can get some basic information and support
 * the most basic of operations, but the body contents of the PDU are treated as an
 * arbitrary blob.
 * <p/>
 * 
 * This PDU is typically used in situations where we want to capture or process PDUs
 * that are not yet supported by Disco. By wrapping them in an {@link UnparsedPdu}
 * instance they can work through the framework safely even though we don't know what
 * to do with them.
 */
public class UnparsedPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final byte[] EMPTY = new byte[0];

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private byte[] payload;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public UnparsedPdu()
	{
		super( PduType.Other );
		
		this.payload = EMPTY;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public String toString()
	{
		return "(Unparsed) "+super.header.getPduType()+", length="+super.header.getPduLength();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.payload = new byte[header.getExpectedContentLength()];
		dis.read( payload );
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.write( payload );
	}
	
	@Override
	public int getContentLength()
	{
		return payload.length;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Abstract PDU Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return 0;
	}
	
	@Override
	public int getAppId()
	{
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
