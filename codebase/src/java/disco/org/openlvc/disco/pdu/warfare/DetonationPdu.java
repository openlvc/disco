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
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.DetonationResult;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.ArticulationParameter;
import org.openlvc.disco.pdu.record.BurstDescriptor;
import org.openlvc.disco.pdu.record.EntityCoordinate;
import org.openlvc.disco.pdu.record.EntityIdentifier;
import org.openlvc.disco.pdu.record.EventIdentifier;
import org.openlvc.disco.pdu.record.PduHeader;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;

public class DetonationPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityIdentifier firingEntityID;
	private EntityIdentifier targetEntityID;
	private EntityIdentifier munitionID;
	private EventIdentifier eventID;

	private VectorRecord velocity;
	private WorldCoordinate locationInWorld;
	private BurstDescriptor burstDescriptor;
	private EntityCoordinate locationInEntityCoordinates;
	private DetonationResult detonationResult;
	private List<ArticulationParameter> articulationParameters;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DetonationPdu( PduHeader header )
	{
		super( header );

		if( header.getPduType() != PduType.Detonation )
			throw new IllegalStateException( "Expected Detonation PDU header, found "+header.getPduType() );

		this.firingEntityID = new EntityIdentifier();
		this.targetEntityID = new EntityIdentifier();
		this.munitionID = new EntityIdentifier();
		this.eventID = new EventIdentifier();

		this.velocity = new VectorRecord();
		this.locationInWorld = new WorldCoordinate();
		this.burstDescriptor = new BurstDescriptor();
		this.locationInEntityCoordinates = new EntityCoordinate();
		this.detonationResult = DetonationResult.Other;
		this.articulationParameters = new ArrayList<ArticulationParameter>();
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

		velocity.from( dis );
		locationInWorld.from( dis );
		burstDescriptor.from( dis );
		locationInEntityCoordinates.from( dis );
		detonationResult = DetonationResult.fromValue( dis.readUI8() );

		short numberOfArticulationParameters = dis.readUI8();
		dis.skip16(); // Skip over padding
		
		articulationParameters.clear();
		for( int i = 0; i < numberOfArticulationParameters; ++i )
		{
			ArticulationParameter parameter = new ArticulationParameter();
			parameter.from( dis );
			articulationParameters.add( parameter );
		}
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		firingEntityID.to( dos );
		targetEntityID.to( dos );
		munitionID.to( dos );
		eventID.to( dos );

		velocity.to( dos );
		locationInWorld.to( dos );
		burstDescriptor.to( dos );
		locationInEntityCoordinates.to( dos );
		dos.writeUI8( detonationResult.value() );
		
		int parameterCountRaw = articulationParameters.size();
		if( parameterCountRaw > DisSizes.UI8_MAX_VALUE )
		{
			// TODO Warn about truncation
		}
		
		short parameterCount = (short)parameterCountRaw;
		dos.writeUI8( parameterCount );
		dos.writePadding16();
		
		for( ArticulationParameter parameter : articulationParameters )
			parameter.to( dos );
	}

	@Override
	public int getContentLength()
	{
		int size = firingEntityID.getByteLength();
		size += targetEntityID.getByteLength();
		size += munitionID.getByteLength();
		size += eventID.getByteLength();
		size += velocity.getByteLength();
		size += locationInWorld.getByteLength();
		size += burstDescriptor.getByteLength();
		size += locationInEntityCoordinates.getByteLength();
		size += DisSizes.UI8_SIZE;      // DetonationResult
		size += DisSizes.UI8_SIZE;		// Parameter Count
		size += 2;						// Padding
		size += DisSizes.getByteLengthOfCollection( articulationParameters );

		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityIdentifier getFiringEntityID()
	{
		return firingEntityID;
	}

	public void setFiringEntityID( EntityIdentifier firingEntityID )
	{
		this.firingEntityID = firingEntityID;
	}

	public EntityIdentifier getTargetEntityID()
	{
		return targetEntityID;
	}

	public void setTargetEntityID( EntityIdentifier targetEntityID )
	{
		this.targetEntityID = targetEntityID;
	}

	public EntityIdentifier getMunitionID()
	{
		return munitionID;
	}

	public void setMunitionID( EntityIdentifier munitionID )
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
