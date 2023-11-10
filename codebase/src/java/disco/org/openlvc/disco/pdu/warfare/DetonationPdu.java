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
import org.openlvc.disco.pdu.field.ProtocolFamily;
import org.openlvc.disco.pdu.record.ArticulationParameter;
import org.openlvc.disco.pdu.record.BurstDescriptor;
import org.openlvc.disco.pdu.record.EntityCoordinate;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EventIdentifier;
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
	private EntityId firingEntityID;
	private EntityId targetEntityID;
	private EntityId munitionID;
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
	public DetonationPdu()
	{
		super( PduType.Detonation, ProtocolFamily.Warfare );

		this.firingEntityID = new EntityId();
		this.targetEntityID = new EntityId();
		this.munitionID = new EntityId();
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
	public final int getContentLength()
	{
		return 92 + DisSizes.getByteLengthOfCollection( articulationParameters );
		
		// int size = firingEntityID.getByteLength();              // 6
		// size += targetEntityID.getByteLength();                 // 6
		// size += munitionID.getByteLength();                     // 6
		// size += eventID.getByteLength();                        // 6
		// size += velocity.getByteLength();                       // 12
		// size += locationInWorld.getByteLength();                // 24
		// size += burstDescriptor.getByteLength();                // 16
		// size += locationInEntityCoordinates.getByteLength();    // 12
		// size += DisSizes.UI8_SIZE;      // DetonationResult     // 1
		// size += DisSizes.UI8_SIZE;		// Parameter Count      // 1
		// size += 2;						// Padding              // 2
		// size += DisSizes.getByteLengthOfCollection( articulationParameters );
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

	public VectorRecord getVelocity()
	{
		return velocity;
	}

	public void setVelocity( VectorRecord velocity )
	{
		this.velocity = velocity;
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

	public EntityCoordinate getLocationInEntityCoordinates()
	{
		return locationInEntityCoordinates;
	}

	public void setLocationInEntityCoordinates( EntityCoordinate locationInEntityCoordinates )
	{
		this.locationInEntityCoordinates = locationInEntityCoordinates;
	}

	public DetonationResult getDetonationResult()
	{
		return detonationResult;
	}

	public void setDetonationResult( DetonationResult detonationResult )
	{
		this.detonationResult = detonationResult;
	}

	public List<ArticulationParameter> getArticulationParameters()
	{
		return articulationParameters;
	}

	public void setArticulationParameter( List<ArticulationParameter> articulationParameters )
	{
		this.articulationParameters = articulationParameters;
	}
	
	@Override
	public int getSiteId()
	{
		return firingEntityID.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return firingEntityID.getAppId();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
