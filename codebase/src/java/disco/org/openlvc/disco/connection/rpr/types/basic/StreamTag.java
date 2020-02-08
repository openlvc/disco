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
package org.openlvc.disco.connection.rpr.types.basic;

import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.EntityId;

public class StreamTag
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final long encode( EntityId entity, int radioId )
	{
		return ((long)entity.getSiteId() << 48) |
		       ((long)entity.getAppId() << 32)  |
		       ((long)entity.getEntityIdentity() << 16) |
		        (long)radioId;
	}

	private static final int[] decode( long streamId )
	{
		int[] values = new int[4];
		values[0] = (int)((streamId >> 48) & 0xffff);
		values[1] = (int)((streamId >> 32) & 0xffff);
		values[2] = (int)((streamId >> 16) & 0xffff);
		values[3] = (int)(streamId & 0xffff);
		return values;
	}

	public static final void decode( long streamId, TransmitterPdu target )
	{
		int[] values = decode( streamId );
		target.getEntityId().setSiteId( values[0] );
		target.getEntityId().setAppId( values[1] );
		target.getEntityId().setEntityId( values[2] );
		target.setRadioID( values[3] );
	}
	
	public static final void decode( long streamId, SignalPdu target )
	{
		int[] values = decode( streamId );
		target.getEntityId().setSiteId( values[0] );
		target.getEntityId().setAppId( values[1] );
		target.getEntityId().setEntityId( values[2] );
		target.setRadioID( values[3] );
	}
}
