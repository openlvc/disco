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
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.ClockTime;
import org.openlvc.disco.pdu.record.EntityId;

public class StartResumePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId originatingEntity;
	private EntityId receivingEntity;
	private ClockTime realWorldTime;
	private ClockTime simulationTime;
	private long requestId;
	

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public StartResumePdu()
	{
		super( PduType.StartResume );
		this.originatingEntity = new EntityId();
		this.receivingEntity = new EntityId();
		this.realWorldTime = new ClockTime();
		this.simulationTime = new ClockTime();
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
		
		if( other instanceof StartResumePdu )
		{
			StartResumePdu otherStartResume = (StartResumePdu)other;
			return Objects.equals( this.originatingEntity, otherStartResume.originatingEntity ) &&
			       Objects.equals( this.receivingEntity, otherStartResume.receivingEntity ) &&
			       Objects.equals( this.realWorldTime, otherStartResume.realWorldTime ) &&
			       Objects.equals( this.simulationTime, otherStartResume.simulationTime ) &&
			       this.requestId == otherStartResume.requestId;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( this.originatingEntity, 
		                     this.receivingEntity,
		                     this.realWorldTime,
		                     this.simulationTime,
		                     this.requestId );
	}
	
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.originatingEntity.from( dis );
		this.receivingEntity.from( dis );
		this.realWorldTime.from( dis );
		this.simulationTime.from( dis );
		this.requestId = dis.readUI32();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.originatingEntity.to( dos );
		this.receivingEntity.to( dos );
		this.realWorldTime.to( dos );
		this.simulationTime.to( dos );
		dos.writeUI32( this.requestId );
	}

	@Override
	public int getContentLength()
	{
		/*
		int size = originatingEntity.getByteLength();            // 6
		size += receivingEntity.getByteLength();                 // 6
		size += realWorldTime.getByteLength();                   // 8
		size += simulationTime.getByteLength();                  // 8
		size += DisSizes.UI32_SIZE;                              // 4
		*/
		
		return 32;
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getOriginatingEntity()
	{
		return this.originatingEntity;
	}
	
	public void setOriginatingEntity( EntityId id )
	{
		this.originatingEntity = id;
	}
	
	public EntityId getReceivingEntity()
	{
		return this.receivingEntity;
	}
	
	public void setReceivingEntity( EntityId id )
	{
		this.receivingEntity = id;
	}
	
	public ClockTime getRealWorldTime()
	{
		return this.realWorldTime;
	}
	
	public void setRealWorldTime( ClockTime time )
	{
		this.realWorldTime = time;
	}
	
	public ClockTime getSimulationTime()
	{
		return this.simulationTime;
	}
	
	public void setSimulationTime( ClockTime time )
	{
		this.simulationTime = time;
	}
	
	public long getRequestId()
	{
		return this.requestId;
	}
	
	public void setRequestId( long requestId )
	{
		this.requestId = requestId;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
