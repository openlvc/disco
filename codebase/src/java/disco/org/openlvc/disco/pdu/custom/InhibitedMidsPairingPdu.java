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
package org.openlvc.disco.pdu.custom;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;

public class InhibitedMidsPairingPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// Max PDU length less field values
	public static final int MAX_MESSAGE_LENGTH = DisSizes.PDU_MAX_SIZE-6-32-32-8;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int tdlType;
	private EntityId sourceEntityId;
	private EntityId destinationEntityId;
	private boolean isMidsTerminalEnabled;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public InhibitedMidsPairingPdu()
	{
		super( PduType.InhibitedMidsPairing );

		this.tdlType = 0;
		this.sourceEntityId = new EntityId();
		this.destinationEntityId = new EntityId();
		this.isMidsTerminalEnabled = true;
	}

	public InhibitedMidsPairingPdu( int tdlType, EntityId source, EntityId destination, boolean isEnabled )
	{
		this();
		this.tdlType = tdlType;
		this.sourceEntityId = source;
		this.destinationEntityId = destination;
		this.isMidsTerminalEnabled = isEnabled;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.tdlType = dis.readInt();
		this.sourceEntityId.from( dis );
		this.destinationEntityId.from( dis );
		this.isMidsTerminalEnabled = dis.readBoolean();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeInt( this.tdlType );
		this.sourceEntityId.to( dos );
		this.destinationEntityId.to( dos );
		dos.writeBoolean( this.isMidsTerminalEnabled );
	}

	@Override
	public final int getContentLength()
	{
		return 4 + // integer is 4 bytes
			   sourceEntityId.getByteLength() +
			   destinationEntityId.getByteLength() +
		       1; // boolean is 1 byte
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filtering Support Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return sourceEntityId.getSiteId();
	}

	@Override
	public int getAppId()
	{
		return sourceEntityId.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getTdlType()
	{
		return this.tdlType;
	}

	public void setTdlType( int tdlType )
	{
		this.tdlType = tdlType;
	}

	public EntityId getSourceEntityId()
	{
		return this.sourceEntityId;
	}

	public void setSourceEntityId( EntityId sourceEntityId )
	{
		if( sourceEntityId != null )
			this.sourceEntityId = sourceEntityId;
	}

	public EntityId getDestinationEntityId()
	{
		return this.destinationEntityId;
	}

	public void setDestinationEntityId( EntityId destinationEntityId )
	{
		if( destinationEntityId != null )
			this.destinationEntityId = destinationEntityId;
	}

	public boolean isMidsTerminalEnabled()
	{
		return this.isMidsTerminalEnabled;
	}

	public void setMidsTerminalEnabled( boolean isEnabled )
	{
		this.isMidsTerminalEnabled = isEnabled;
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
