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
package org.openlvc.disco.pdu.synthenv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.LinearSegmentParameter;
import org.openlvc.disco.pdu.record.ObjectId;
import org.openlvc.disco.pdu.record.ObjectType;
import org.openlvc.disco.pdu.record.SimulationAddress;

public class LinearObjectStatePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectId objectId;
	private ObjectId referencedObjectId;
	private int updateNumber;
	private ForceId forceId;
	private SimulationAddress requestorId;
	private SimulationAddress receivingId;
	private ObjectType objectType;
	private List<LinearSegmentParameter> segments;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LinearObjectStatePdu()
	{
		super( PduType.LinearObjectState );
		this.objectId = new ObjectId();
		this.referencedObjectId = new ObjectId();
		this.updateNumber = -1;
		this.forceId = ForceId.Other;
		this.requestorId = new SimulationAddress();
		this.receivingId = new SimulationAddress();
		this.objectType = new ObjectType();
		this.segments = new ArrayList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
			return false;
		
		if( other instanceof LinearObjectStatePdu )
		{
			LinearObjectStatePdu otherAos = (LinearObjectStatePdu)other;
			return Objects.equals( this.objectId, otherAos.objectId ) &&
			       Objects.equals( this.referencedObjectId, otherAos.referencedObjectId ) &&
			       this.updateNumber == otherAos.updateNumber &&
			       this.forceId == otherAos.forceId &&
			       Objects.equals( this.requestorId, otherAos.requestorId ) &&
			       Objects.equals( this.receivingId, otherAos.receivingId ) &&
			       Objects.equals( this.objectType, otherAos.objectType ) &&
			       Objects.equals( this.segments, otherAos.segments );
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( this.objectId, 
		                     this.referencedObjectId, 
		                     this.updateNumber,
		                     this.forceId,
		                     this.requestorId,
		                     this.receivingId,
		                     this.objectType,
		                     this.segments );
	}
	
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.objectId.from( dis );
		this.referencedObjectId.from( dis );
		this.updateNumber = dis.readUI16();
		this.forceId = ForceId.fromValue( dis.readUI8() );
		short numberOfSegments = dis.readUI8();
		this.requestorId.from( dis );
		this.receivingId.from( dis );
		this.objectType.from( dis );
		this.segments.clear();
		
		// Spec states that this value should be capped to 24 as that's all that can be wedged
		// into a UDP packet
		final int MAX_PDU_SEGMENTS = 24;
		numberOfSegments = (short)Math.min( numberOfSegments, MAX_PDU_SEGMENTS );
		
		for( int i = 0 ; i < numberOfSegments ; ++i )
		{
			LinearSegmentParameter segmentI = new LinearSegmentParameter();
			segmentI.from( dis );
			this.segments.add( segmentI );
		}
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		this.objectId.to( dos );
		this.referencedObjectId.to( dos );
		dos.writeUI16( this.updateNumber );
		dos.writeUI8( this.forceId.value() );
		dos.writeUI8( (short)this.segments.size() );
		this.requestorId.to( dos );
		this.receivingId.to( dos );
		this.objectType.to( dos );
		for( LinearSegmentParameter segment : this.segments )
			segment.to( dos );
	}
	
	@Override
	public final int getContentLength()
	{
		return 40 + (this.segments.size() * 64);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Convenience Methods   //////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return objectId.getSiteId();
	}

	@Override
	public int getAppId()
	{
		return objectId.getAppId();
	}

	public ObjectId getObjectId()
	{
		return objectId;
	}

	public void setObjectId( ObjectId objectId )
	{
		this.objectId = objectId;
	}

	public ObjectId getReferencedObjectId()
	{
		return referencedObjectId;
	}

	public void setReferencedObjectId( ObjectId referencedObjectId )
	{
		this.referencedObjectId = referencedObjectId;
	}

	public int getUpdateNumber()
	{
		return updateNumber;
	}

	public void setUpdateNumber( int updateNumber )
	{
		this.updateNumber = updateNumber;
	}

	public ForceId getForceId()
	{
		return forceId;
	}

	public void setForceId( ForceId forceId )
	{
		this.forceId = forceId;
	}

	public SimulationAddress getRequestorId()
	{
		return requestorId;
	}

	public void setRequestorId( SimulationAddress requestorId )
	{
		this.requestorId = requestorId;
	}

	public SimulationAddress getReceivingId()
	{
		return receivingId;
	}

	public void setReceivingId( SimulationAddress receivingId )
	{
		this.receivingId = receivingId;
	}

	public ObjectType getObjectType()
	{
		return objectType;
	}

	public void setObjectType( ObjectType objectType )
	{
		this.objectType = objectType;
	}

	public List<LinearSegmentParameter> getSegments()
	{
		return new ArrayList<>( segments );
	}

	public void setSegments( List<LinearSegmentParameter> segments )
	{
		this.segments = new ArrayList<>( segments );
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
