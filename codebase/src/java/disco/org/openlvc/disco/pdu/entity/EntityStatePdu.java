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
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.EntityKind;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.ArticulationParameter;
import org.openlvc.disco.pdu.record.DeadReckoningParameter;
import org.openlvc.disco.pdu.record.EntityCapabilities;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.BitField32;

public class EntityStatePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId entityID;
	private ForceId forceID;
	private EntityType entityType;
	private EntityType alternativeEntityType;
	private VectorRecord linearVelocity;
	private WorldCoordinate location;
	private EulerAngles orientation;
	private int appearance;
	private DeadReckoningParameter deadReckoningParams;
	private String marking;
	private EntityCapabilities capabilities;
	private List<ArticulationParameter> articulationParameters;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityStatePdu()
	{
		super( PduType.EntityState );

		this.entityID = new EntityId();
		this.forceID = ForceId.Other;
		this.entityType = new EntityType();
		this.alternativeEntityType = new EntityType();
		this.linearVelocity = new VectorRecord();
		this.location = new WorldCoordinate();
		this.orientation = new EulerAngles();
		this.appearance = 0;
		this.deadReckoningParams = new DeadReckoningParameter();
		this.marking = "DiscoObject";
		this.capabilities = new EntityCapabilities();
		this.articulationParameters = new ArrayList<ArticulationParameter>();
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public String toString()
	{
		return "Marking: " + marking;
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
		linearVelocity.from( dis );
		location.from( dis );
		orientation.from( dis );
		appearance = dis.readInt();
		deadReckoningParams.from( dis );
		marking = dis.readFixedString( 11 ).trim(); // Should we do the trim?? Not sure
		capabilities.from( dis );

		articulationParameters.clear();
		for( int i = 0; i < numberOfArticulationParameters; i++ )
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
		if( articulationParamCount > DisSizes.UI8_MAX_VALUE )
		{
			// TODO Warn about truncation
		}

		short paramCountAsShort = (short)articulationParamCount;
		dos.writeUI8( paramCountAsShort );
		entityType.to( dos );
		alternativeEntityType.to( dos );
		linearVelocity.to( dos );
		location.to( dos );
		orientation.to( dos );
		dos.writeInt( appearance );
		deadReckoningParams.to( dos );
		dos.writeFixedString( marking, 11 );
		capabilities.to( dos );

		for( ArticulationParameter parameter : articulationParameters )
			parameter.to( dos );
	}

	@Override
	public final int getContentLength()
	{
		return 132 + DisSizes.getByteLengthOfCollection(articulationParameters);

		/*
		int size = entityID.getByteLength();            // 6
		size += ForceId.getByteLength();                // 1
		size += DisSizes.UI8_SIZE; // ParamCount        // 1
		size += entityType.getByteLength();             // 8
		size += alternativeEntityType.getByteLength();  // 8
		size += linearVelocity.getByteLength();         // 12
		size += location.getByteLength();               // 24
		size += orientation.getByteLength();            // 12
		size += DisSizes.UI32_SIZE; // Appearance       // 4
		size += deadReckoningParams.getByteLength();    // 40
		size += 12; // Marking                          // 12
		size += capabilities.getByteLength();           // 4
		size += DisSizes.getByteLengthOfCollection( articulationParameters );

		return size;
		*/
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Convenience Methods   //////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityKind getKind()
	{
		return entityType.getKindEnum();
	}

	public Domain getDomain()
	{
		return entityType.getDomainEnum();
	}
	
	@Override
	public int getSiteId()
	{
		return entityID.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return entityID.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getEntityID()
	{
		return entityID;
	}

	public void setEntityID( EntityId entityID )
	{
		this.entityID = entityID;
	}

	public void setEntityID( int siteId, int appId, int entityId )
	{
		this.entityID.setSiteId( siteId );
		this.entityID.setAppId( appId );
		this.entityID.setEntityId( entityId );
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

	public VectorRecord getLinearVelocity()
	{
		return linearVelocity;
	}

	public void setLinearVelocity( VectorRecord velocity )
	{
		this.linearVelocity = velocity;
	}

	public WorldCoordinate getLocation()
	{
		return location;
	}

	public void setLocation( WorldCoordinate location )
	{
		this.location = location;
	}

	public EulerAngles getOrientation()
	{
		return orientation;
	}

	public void setOrientation( EulerAngles orientation )
	{
		this.orientation = orientation;
	}

	public int getAppearance()
	{
		return appearance;
	}
	
	public void setAppearance( int appearance )
	{
		this.appearance = appearance;
	}

	/**
	 * @return True if the "Is Frozen" bit in the appearance bitfield is set. On all types of
	 *         entities (lifeforms, platforms, munitions, ...) this is always Bit 21 in the field.
	 */
	public boolean isFrozen()
	{
		return BitField32.isSet( this.appearance, 21 );
	}
	
	/**
	 * Set's the "Is Frozen" apperance bit to the given value. On all entity types (lifeform,
	 * platform, munition, ...) this is always Bit 21 in the field, so we're safe to speak to
	 * it generically.
	 * 
	 * @param isFrozen Should the bit be set to 0 (if false) or 1 (if true)
	 */
	public void setFrozen( boolean isFrozen )
	{
		this.appearance = BitField32.set( this.appearance, 21, isFrozen );
	}

	public DeadReckoningParameter getDeadReckoningParams()
	{
		return deadReckoningParams;
	}

	public void setDeadReckoningParams( DeadReckoningParameter deadReckoningParams )
	{
		this.deadReckoningParams = deadReckoningParams;
	}

	public String getMarking()
	{
		return marking;
	}

	public void setMarking( String marking )
	{
		this.marking = marking;
	}

	public EntityCapabilities getCapabilities()
	{
		return capabilities;
	}

	public void setCapabilities( EntityCapabilities capabilities )
	{
		this.capabilities = capabilities;
	}

	public List<ArticulationParameter> getArticulationParameter()
	{
		return articulationParameters;
	}

	public void setArticulationParameters( List<ArticulationParameter> articulationParameters )
	{
		if( articulationParameters.size() > DisSizes.UI8_MAX_VALUE )
			throw new IllegalArgumentException( "A maximum of " + DisSizes.UI8_MAX_VALUE +
			                                    " articulation parameters are supported by the DIS specification" );

		this.articulationParameters = articulationParameters;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
