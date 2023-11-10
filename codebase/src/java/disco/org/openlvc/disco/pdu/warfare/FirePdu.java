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
package org.openlvc.disco.pdu.warfare;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.ProtocolFamily;
import org.openlvc.disco.pdu.record.BurstDescriptor;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EventIdentifier;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;

/**
 * This class represents an Fire PDU. Fire PDUs represent the firing of ordinates in the Simulated
 * World. The detonation of the ordinates is reported in a corresponding Detonation PDU when the
 * modeled munitions eventually detonate.
 * 
 * @see "IEEE Std 1278.1-1995 section 4.5.3.2"
 */
public class FirePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId firingEntityID;
	private EntityId targetEntityID;
	private EntityId munitionID;
	private EventIdentifier eventID;

	private long fireMissionIndex;
	private WorldCoordinate locationInWorld;
	private BurstDescriptor burstDescriptor;
	private VectorRecord velocity;
	private float range;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FirePdu()
	{
		super( PduType.Fire, ProtocolFamily.Warfare );

		this.firingEntityID = new EntityId();
		this.targetEntityID = new EntityId();
		this.munitionID = new EntityId();
		this.eventID = new EventIdentifier();

		this.fireMissionIndex = 0;
		this.locationInWorld = new WorldCoordinate();
		this.burstDescriptor = new BurstDescriptor();
		this.velocity = new VectorRecord();
		this.range = 0f;
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
		firingEntityID.from( dis );
		targetEntityID.from( dis );
		munitionID.from( dis );
		eventID.from( dis );
		
		fireMissionIndex = dis.readUI32();
		locationInWorld.from( dis );
		burstDescriptor.from( dis );
		velocity.from( dis );
		range = dis.readFloat();
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		firingEntityID.to( dos );
		targetEntityID.to( dos );
		munitionID.to( dos );
		eventID.to( dos );

		dos.writeUI32( fireMissionIndex );
		locationInWorld.to( dos );
		burstDescriptor.to( dos );
		velocity.to( dos );
		dos.writeFloat( range );
	}

	@Override
	public final int getContentLength()
	{
		return 78;

		// int size = firingEntityID.getByteLength();          // 6
		// size += targetEntityID.getByteLength();             // 6
		// size += munitionID.getByteLength();                 // 6
		// size += eventID.getByteLength();

		// size += DisSizes.UI32_SIZE;	// Fire Mission Index  // 4
		// size += locationInWorld.getByteLength();            // 24
		// size += burstDescriptor.getByteLength();            // 16
		// size += velocity.getByteLength();                   // 12
		// size += DisSizes.FLOAT32_SIZE;	// Range           // 4
		
		// return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getFiringEntityID()
	{
		return firingEntityID;
	}

	public void setFiringEntityID( EntityId firingEntityID )
	{
		this.firingEntityID = firingEntityID;
	}

	public EntityId getTargetEntityID()
	{
		return targetEntityID;
	}

	public void setTargetEntityID( EntityId targetEntityID )
	{
		this.targetEntityID = targetEntityID;
	}

	public EntityId getMunitionID()
	{
		return munitionID;
	}

	public void setMunitionID( EntityId munitionID )
	{
		this.munitionID = munitionID;
	}

	public EventIdentifier getEventID()
	{
		return eventID;
	}

	public void setEventID( EventIdentifier eventID )
	{
		this.eventID = eventID;
	}

	public long getFireMissionIndex()
	{
		return fireMissionIndex;
	}

	public void setFireMissionIndex( long fireMissionIndex )
	{
		this.fireMissionIndex = fireMissionIndex;
	}

	public WorldCoordinate getLocationInWorld()
	{
		return locationInWorld;
	}

	public void setLocationInWorld( WorldCoordinate locationInWorld )
	{
		this.locationInWorld = locationInWorld;
	}

	public BurstDescriptor getBurstDescriptor()
	{
		return burstDescriptor;
	}

	public void setBurstDescriptor( BurstDescriptor burstDescriptor )
	{
		this.burstDescriptor = burstDescriptor;
	}

	public VectorRecord getVelocity()
	{
		return velocity;
	}

	public void setVelocity( VectorRecord velocity )
	{
		this.velocity = velocity;
	}

	public float getRange()
	{
		return range;
	}

	public void setRange( float range )
	{
		this.range = range;
	}
	
	@Override
	public int getSiteId()
	{
		return this.firingEntityID.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return this.firingEntityID.getAppId();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
