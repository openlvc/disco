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
package org.openlvc.disco.pdu.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.ArticulationParameter;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EntityCapabilities;
import org.openlvc.disco.pdu.record.EntityIdentifier;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.PduHeader;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;

public class EntityStatePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityIdentifier entityID;
	private ForceId forceID;
	private EntityType entityType;
	private EntityType alternativeEntityType;
	private VectorRecord entityLinearVelocity;
	private WorldCoordinate entityLocation;
	private EulerAngles entityOrientation;
	private int entityAppearance;
	private DeadReckoningParameter deadReckoningParameters;
	private String entityMarking;
	private EntityCapabilities entityCapabilities;
	private List<ArticulationParameter> articulationParameters;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityStatePdu( PduHeader header )
	{
		super( header );

		if( header.getPduType() != PduType.EntityState )
			throw new IllegalStateException( "Expected EntityStatePdu header, found "+header.getPduType() );
		
		this.entityID = new EntityIdentifier();
		this.forceID = ForceId.Other;
		this.entityType = new EntityType();
		this.alternativeEntityType = new EntityType();
		this.entityLinearVelocity = new VectorRecord();
		this.entityLocation = new WorldCoordinate();
		this.entityOrientation = new EulerAngles();
		this.entityAppearance = 0;
		this.deadReckoningParameters = new DeadReckoningParameter();
		this.entityMarking = "DiscoObject";
		this.entityCapabilities = new EntityCapabilities();
		this.articulationParameters = new ArrayList<ArticulationParameter>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public String toString()
	{
		return "Marking: "+entityMarking;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		articulationParameters.clear();
		
		entityID.from( dis );
		forceID = ForceId.fromValue( dis.readUI8() );
		short numberOfArticulationParameters = dis.readUI8();
		entityType.from( dis );
		alternativeEntityType.from( dis );
		entityLinearVelocity.from( dis );
		entityLocation.from( dis );
		entityOrientation.from( dis );
		entityAppearance = dis.readInt();
		deadReckoningParameters.from( dis );
		entityMarking = dis.readString( 11 );
		entityCapabilities.from( dis );
		
		articulationParameters.clear();
		for( int i = 0; i < numberOfArticulationParameters; ++i )
		{
			ArticulationParameter articulationParameter = new ArticulationParameter();
			articulationParameter.from( dis );
			articulationParameters.add( articulationParameter );
		}
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		entityID.to( dos );
		dos.writeUI8( forceID.value() );
		
		int articulationParamCount = articulationParameters.size();
		if ( articulationParamCount > DisSizes.UI8_MAX_VALUE )
		{
			// TODO Warn about truncation
		}
		
		short paramCountAsShort = (short)articulationParamCount;
		dos.writeUI8( paramCountAsShort );
		entityType.to( dos );
		alternativeEntityType.to( dos );
		entityLinearVelocity.to( dos );
		entityLocation.to( dos );
		entityOrientation.to( dos );
		dos.writeInt( entityAppearance );
		deadReckoningParameters.to( dos );
		dos.writeString( entityMarking, 11 );
		entityCapabilities.to( dos );
		
		for ( ArticulationParameter parameter : articulationParameters )
			parameter.to( dos );
	}
	@Override
	public int getContentLength()
	{
		int size = entityID.getByteLength();
		size += ForceId.getByteLength();
		size += DisSizes.UI8_SIZE;				// ParamCount
		size += entityType.getByteLength();
		size += alternativeEntityType.getByteLength();
		size += entityLinearVelocity.getByteLength();
		size += entityLocation.getByteLength();
		size += entityOrientation.getByteLength();
		size += DisSizes.UI32_SIZE;				// Appearance
		size += deadReckoningParameters.getByteLength();
		size += 12;                             // Marking
		size += entityCapabilities.getByteLength();
		size += DisSizes.getByteLengthOfCollection( articulationParameters );
		
		return size;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityIdentifier getEntityID()
    {
    	return entityID;
    }

	public void setEntityID( EntityIdentifier entityID )
    {
    	this.entityID = entityID;
    }

	public ForceId getForceID()
    {
    	return forceID;
    }

	public void setForceID( ForceId forceID )
    {
    	this.forceID = forceID;
    }

	public EntityType getEntityType()
    {
    	return entityType;
    }

	public void setEntityType( EntityType entityType )
    {
    	this.entityType = entityType;
    }

	public EntityType getAlternativeEntityType()
    {
    	return alternativeEntityType;
    }

	public void setAlternativeEntityType( EntityType alternativeEntityType )
    {
    	this.alternativeEntityType = alternativeEntityType;
    }

	public VectorRecord getEntityLinearVelocity()
    {
    	return entityLinearVelocity;
    }

	public void setEntityLinearVelocity( VectorRecord entityLinearVelocity )
    {
    	this.entityLinearVelocity = entityLinearVelocity;
    }

	public WorldCoordinate getEntityLocation()
    {
    	return entityLocation;
    }

	public void setEntityLocation( WorldCoordinate entityLocation )
    {
    	this.entityLocation = entityLocation;
    }

	public EulerAngles getEntityOrientation()
    {
    	return entityOrientation;
    }

	public void setEntityOrientation( EulerAngles entityOrientation )
    {
    	this.entityOrientation = entityOrientation;
    }

	public int getEntityAppearance()
    {
    	return entityAppearance;
    }

	public void setEntityAppearance( int entityAppearance )
    {
    	this.entityAppearance = entityAppearance;
    }

	public DeadReckoningParameter getDeadReckoningParameters()
    {
    	return deadReckoningParameters;
    }

	public void setDeadReckoningParameters( DeadReckoningParameter deadReckoningParameters )
    {
    	this.deadReckoningParameters = deadReckoningParameters;
    }

	public String getEntityMarking()
    {
    	return entityMarking;
    }

	public void setEntityMarking( String entityMarking )
    {
    	this.entityMarking = entityMarking;
    }

	public EntityCapabilities getEntityCapabilities()
    {
    	return entityCapabilities;
    }

	public void setEntityCapabilities( EntityCapabilities entityCapabilities )
    {
    	this.entityCapabilities = entityCapabilities;
    }
	
	public List<ArticulationParameter> getArticulationParameter()
    {
    	return articulationParameters;
    }

	public void setArticulationParameter( List<ArticulationParameter> articulationParameter )
    {
		if( articulationParameter.size() > DisSizes.UI8_MAX_VALUE )
			throw new IllegalArgumentException( "A maximum of " + DisSizes.UI8_MAX_VALUE + 
			                                    " articulation parameters are supported by the DIS specification" );
			
		this.articulationParameters = articulationParameter;
    }
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
