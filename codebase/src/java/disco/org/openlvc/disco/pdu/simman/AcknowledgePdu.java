/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.pdu.simman;

import java.io.IOException;
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.AcknowledgeFlag;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.ResponseFlag;
import org.openlvc.disco.pdu.record.EntityId;

public class AcknowledgePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId originatingEntity;
	private EntityId receivingEntity;
	private AcknowledgeFlag acknowledgeFlag;
	private ResponseFlag responseFlag;
	private long requestId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AcknowledgePdu()
	{
		super( PduType.Acknowledge );
		this.originatingEntity = new EntityId();
		this.receivingEntity = new EntityId();
		this.acknowledgeFlag = AcknowledgeFlag.CreateEntity;
		this.requestId = 0;
		
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
			return false;
		
		if( other instanceof AcknowledgePdu )
		{
			AcknowledgePdu otherAck = (AcknowledgePdu)other;
			return Objects.equals( this.originatingEntity, otherAck.originatingEntity ) &&
			       Objects.equals( this.receivingEntity, otherAck.receivingEntity ) &&
			       this.acknowledgeFlag == otherAck.acknowledgeFlag &&
			       this.responseFlag == otherAck.responseFlag &&
			       this.requestId == otherAck.requestId;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.originatingEntity.from( dis );
		this.receivingEntity.from( dis );
		this.acknowledgeFlag = AcknowledgeFlag.fromValue( dis.readUI16() );
		this.responseFlag = ResponseFlag.fromValue( dis.readUI16() );
		this.requestId = dis.readUI32();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		originatingEntity.to( dos );
		receivingEntity.to( dos );
		dos.writeUI16( acknowledgeFlag.value() );
		dos.writeUI16( responseFlag.value() );
		dos.writeUI32( this.requestId );
	}
	
	@Override
	public int getContentLength()
	{
		/*
		int size = originatingEntity.getByteLength();            // 6
		size += receivingEntity.getByteLength();                 // 6
		size += AcknowledgeFlag.getByteLength();                 // 2
		size += DisSizes.UI16_SIZE; // Response Flag             // 2
		size += DisSizes.UI32_SIZE; // RequestId                 // 4
		*/
		
		return 20;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Misc PDU Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return originatingEntity.getSiteId();
	}

	@Override
	public int getAppId()
	{
		return originatingEntity.getAppId();
	}

	public EntityId getOriginatingEntity()
	{
		return originatingEntity;
	}

	public void setOriginatingEntity( EntityId originatingEntity )
	{
		this.originatingEntity = originatingEntity;
	}

	public EntityId getReceivingEntity()
	{
		return receivingEntity;
	}

	public void setReceivingEntity( EntityId receivingEntity )
	{
		this.receivingEntity = receivingEntity;
	}

	public AcknowledgeFlag getAcknowledgeFlag()
	{
		return acknowledgeFlag;
	}

	public void setAcknowledgeFlag( AcknowledgeFlag acknowledgeFlag )
	{
		this.acknowledgeFlag = acknowledgeFlag;
	}

	public ResponseFlag getResponseFlag()
	{
		return responseFlag;
	}

	public void setResponseFlag( ResponseFlag responseFlag )
	{
		this.responseFlag = responseFlag;
	}

	public long getRequestId()
	{
		return requestId;
	}

	public void setRequestId( long requestId )
	{
		this.requestId = requestId;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
