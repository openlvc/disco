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
package org.openlvc.disco.connection.rpr.mappers;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.Aircraft;
import org.openlvc.disco.connection.rpr.objects.Human;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.connection.rpr.objects.PhysicalEntity;
import org.openlvc.disco.connection.rpr.objects.Platform;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.Kind;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

/**
 * BaseEntity
 *   > EntityType : EntityTypeStruct
 *   > EntityIdentifier : EntityIdentifierStruct
 *   > IsPartOf : IsPartOfStruct
 *   > Spatial : SpatialVariantStruct
 *   > RelativeSpatial : SpatialVariantStruct
 * 
 * PhysicalEntity
 *   > AcousticSignatureIndex : Integer16
 *   > AlternateEntityType : EntityTypeStruct
 *   > ArticulatedParametersArray : ArticulatedParameterStructLengthlessArray  >> ??
 *   > CamouflageType : CamouflageEnum32
 *   > DamageState : DamageStatusEnum32
 *   > EngineSmokeOn : RPRboolean
 *   > FirePowerDisabled : RPRboolean
 *   > FlamesPresent : RPRboolean
 *   > ForceIdentifier : ForceIdentifierEnum8
 *   > HasAmmunitionSupplyCap : RPRboolean
 *   > HasFuelSupplyCap : RPRboolean
 *   > HasRecoveryCap : RPRboolean
 *   > HasRepairCap : RPRboolean
 *   > Immobilized : RPRboolean
 *   > InfraredSignatureIndex : Integer16
 *   > IsConcealed : RPRboolean
 *   > LiveEntityMeasuredSpeed : VelocityDecimeterPerSecondInteger16
 *   > Marking : MarkingStruct
 *   > PowerPlantOn : RPRboolean
 *   > PropulsionSystemsData : PropulsionSystemDataStructLengthlessArray >> ??
 *   > RadarCrossSectionSignatureIndex : Integer16
 *   > SmokePlumePresent : RPRboolean
 *   > TentDeployed : RPRboolean
 *   > TrailingEffectsCode : TrailingEffectsCodeEnum32
 *   > VectoringNozzleSystemData : VectoringNozzleSystemDataStructLengthlessArray >> ??
 * 
 * PhysicalEntity::Platform
 *   > AfterburnerOn : RPRboolean
 *   > AntiCollisionLightsOn : RPRboolean
 *   > BlackOutBrakeLightsOn : RPRboolean
 *   > BlackOutLightsOn : RPRboolean
 *   > BrakeLightsOn : RPRboolean
 *   > FormationLightsOn : RPRboolean
 *   > HatchState : HatchStateEnum32
 *   > HeadLightsOn : RPRboolean
 *   > InteriorLightsOn : RPRboolean
 *   > LandingLightsOn : RPRboolean
 *   > LauncherRaised : RPRboolean
 *   > NavigationLightsOn : RPRboolean
 *   > RampDeployed : RPRboolean
 *   > RunningLightsOn : RPRboolean
 *   > SpotLightsOn : RPRboolean
 *   > TailLightsOn : RPRboolean
 * 
 *   >> PhysicalEntity::Platform::Aircraft
 *   >> PhysicalEntity::Platform::GroundVehicle
 *   >> PhysicalEntity::Platform::MultiDomainPlatform
 *   >> PhysicalEntity::Platform::Spacecraft
 *   >> PhysicalEntity::Platform::SubmersibleVessel
 *   >> PhysicalEntity::Platform::SurfaceVessel
 * 
 * PhysicalEntity::Lifeform
 *   > FlashLightsOn : RPRboolean
 *   > StanceCode : StanceCodeEnum32
 *   > PrimaryWeaponState : WeaponStateEnum32
 *   > SecondaryWeaponState : WeaponStateEnum32
 *   > ComplianceState : ComplianceStateEnum32
 * 
 *   >> PhysicalEntity::Lifeform::Human
 *   >> PhysicalEntity::Lifeform::NonHuman
 */
public class EntityStateMapper extends AbstractMapper implements IObjectMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass hlaClass;

	// Base Entity
	private AttributeClass entityType;
	private AttributeClass entityIdentifier;
	private AttributeClass isPartOf;
	private AttributeClass spatial;
	private AttributeClass relativeSpatial;

	// Physical Entity
	// Metadata
	private AttributeClass alternateEntityType;
	private AttributeClass forceIdentifier;
	private AttributeClass marking;

	// Status
	private AttributeClass damageState;
	private AttributeClass camouflageType;
	private AttributeClass trailingEffectsCode;

	// Apperance
	private AttributeClass engineSmokeOn;
	private AttributeClass firePowerDisabled;
	private AttributeClass flamesPresent;
	private AttributeClass hasAmmunitionSupplyCap;
	private AttributeClass hasFuelSupplyCap;
	private AttributeClass hasRecoveryCap;
	private AttributeClass hasRepairCap;
	private AttributeClass immobilized;
	private AttributeClass isConcealed;
	private AttributeClass smokePlumePresent;
	private AttributeClass tentDeployed;
	private AttributeClass powerplantOn;

	// Other
	private AttributeClass liveEntityMeasuredSpeed;
	private AttributeClass radarCrossSectionSignatureIndex;
	private AttributeClass infraredSignatureIndex;
	private AttributeClass acousticSignatureIndex;

	// Arrays
	private AttributeClass articulatedParametersArray;
	private AttributeClass propulsionSystemsData;
	private AttributeClass vectoringNozzleSystemData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityStateMapper( RprConverter rprConverter )
	{
		super( rprConverter );
		
		// Cache up all the attributes we need
		this.hlaClass = rprConverter.model.getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity" );
		
		// Base Entity
		this.entityType = hlaClass.getAttribute( "EntityType" );
		this.entityIdentifier = hlaClass.getAttribute( "EntityIdentifier" );
		this.isPartOf = hlaClass.getAttribute( "IsPartOf" );
		this.spatial = hlaClass.getAttribute( "Spatial" );
		this.relativeSpatial = hlaClass.getAttribute( "RelativeSpatial" );

		// Physical Entity
		// Metadata
		this.alternateEntityType = hlaClass.getAttribute( "AlternateEntityType" );
		this.forceIdentifier = hlaClass.getAttribute( "ForceIdentifier" );
		this.marking = hlaClass.getAttribute( "Marking" );

		// Status
		this.damageState = hlaClass.getAttribute( "DamageState" );
		this.camouflageType = hlaClass.getAttribute( "CamouflageType" );
		this.trailingEffectsCode = hlaClass.getAttribute( "TrailingEffectsCode" );

		// Apperance
		this.engineSmokeOn = hlaClass.getAttribute( "EngineSmokeOn" );
		this.firePowerDisabled = hlaClass.getAttribute( "FirePowerDisabled" );
		this.flamesPresent = hlaClass.getAttribute( "FlamesPresent" );
		this.hasAmmunitionSupplyCap = hlaClass.getAttribute( "HasAmmunitionSupplyCap" );
		this.hasFuelSupplyCap = hlaClass.getAttribute( "HasFuelSupplyCap" );
		this.hasRecoveryCap = hlaClass.getAttribute( "HasRecoveryCap" );
		this.hasRepairCap = hlaClass.getAttribute( "HasRepairCap" );
		this.immobilized = hlaClass.getAttribute( "Immobilized" );
		this.isConcealed = hlaClass.getAttribute( "IsConcealed" );
		this.smokePlumePresent = hlaClass.getAttribute( "SmokePlumePresent" );
		this.tentDeployed = hlaClass.getAttribute( "TentDeployed" );
		this.powerplantOn = hlaClass.getAttribute( "PowerPlantOn" );

		// Other
		this.liveEntityMeasuredSpeed = hlaClass.getAttribute( "LiveEntityMeasuredSpeed" );
		this.radarCrossSectionSignatureIndex = hlaClass.getAttribute( "RadarCrossSectionSignatureIndex" );
		this.infraredSignatureIndex = hlaClass.getAttribute( "InfraredSignatureIndex" );
		this.acousticSignatureIndex = hlaClass.getAttribute( "AcousticSignatureIndex" );

		// Arrays
		this.articulatedParametersArray = hlaClass.getAttribute( "ArticulatedParametersArray" );
		this.propulsionSystemsData = hlaClass.getAttribute( "PropulsionSystemsData" );
		this.vectoringNozzleSystemData = hlaClass.getAttribute( "VectoringNozzleSystemData" );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public PduType getSupportedPduType()
	{
		return PduType.EntityState;
	}
	
	@Override
	public ObjectClass getSupportedHlaClass()
	{
		return this.hlaClass;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods   //////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectInstance createObject( ObjectInstanceHandle objectHandle )
	{
		Platform object = new Aircraft();
		object.setObjectHandle( objectHandle );
		object.setObjectClass( hlaClass );
		object.setMapper( this );
		return object;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendDisToHla( PDU genericPdu, RTIambassador rtiamb )
	{
		EntityStatePdu pdu = genericPdu.as( EntityStatePdu.class );
		
		// Do we already have an object cached for this entity?
		PhysicalEntity hlaObject = rprConverter.getDisEntity( pdu.getEntityID() );

		// If there is no known HLA object, we have to register one
		if( hlaObject == null )
		{
			// No object registered yet, do it now
			hlaObject = createObject( pdu.getKind() );
			hlaObject.setObjectClass( this.hlaClass );
			hlaObject.setMapper( this );
			super.registerObjectInstance( hlaObject, rtiamb );
			rprConverter.addDisEntity( pdu.getEntityID(), hlaObject );
		}

		// Extract PDU values from the object
		hlaObject.fromPdu( pdu );
		
		// Send an update for the object
		super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject), rtiamb );
	}

	private PhysicalEntity createObject( Kind kind )
	{
		switch( kind )
		{
			case Platform: return new Aircraft(); // Who cares for now
			case Lifeform: return new Human();    // Again, who cares Human or Non
			default: throw new DiscoException( "Unsupported Entity Kind: "+kind.name() );
		}
	}
	
	private AttributeHandleValueMap serializeToHla( PhysicalEntity object )
	{
		if( System.currentTimeMillis() > 0 ) throw new RuntimeException( "Not Supported Yet" );

		AttributeHandleValueMap map = object.getObjectAttributes();
		
		////////////////////////////////
		// Base Entity  ////////////////
		////////////////////////////////
		// Entity Type
		ByteWrapper wrapper = new ByteWrapper( object.getEntityType().getEncodedLength() );
		object.getEntityType().encode(wrapper);
		map.put( entityType.getHandle(), wrapper.array() );
		
		// Entity Identifier
		wrapper = new ByteWrapper( object.getEntityIdentifier().getEncodedLength() );
		object.getEntityIdentifier().encode(wrapper);
		map.put( entityIdentifier.getHandle(), wrapper.array() );

		// IsPartOf
		wrapper = new ByteWrapper( object.getIsPartOf().getEncodedLength() );
		object.getIsPartOf().encode(wrapper);
		map.put( isPartOf.getHandle(), wrapper.array() );

		// Spatial
		wrapper = new ByteWrapper( object.getSpatial().getEncodedLength() );
		object.getSpatial().encode(wrapper);
		map.put( spatial.getHandle(), wrapper.array() );

		// Relative Spatial
		wrapper = new ByteWrapper( object.getRelativeSpatial().getEncodedLength() );
		object.getRelativeSpatial().encode(wrapper);
		map.put( relativeSpatial.getHandle(), wrapper.array() );

		////////////////////////////////
		// Physical Entity  ////////////
		////////////////////////////////
		// Metadata
		// Alternate Entity Id
		wrapper = new ByteWrapper( object.getAlternateEntityType().getEncodedLength() );
		object.getAlternateEntityType().encode(wrapper);
		map.put( alternateEntityType.getHandle(), wrapper.array() );

		// Force Identifier
		wrapper = new ByteWrapper( object.getForceIdentifier().getEncodedLength() );
		object.getForceIdentifier().encode(wrapper);
		map.put( forceIdentifier.getHandle(), wrapper.array() );

		// Marking
		wrapper = new ByteWrapper( object.getMarking().getEncodedLength() );
		object.getMarking().encode(wrapper);
		map.put( marking.getHandle(), wrapper.array() );

		// Status
		// Damage State
		wrapper = new ByteWrapper( object.getDamageState().getEncodedLength() );
		object.getDamageState().encode(wrapper);
		map.put( damageState.getHandle(), wrapper.array() );

		// Camouflage Type
		wrapper = new ByteWrapper( object.getCamouflageType().getEncodedLength() );
		object.getCamouflageType().encode(wrapper);
		map.put( camouflageType.getHandle(), wrapper.array() );

		// Trailing Effects Code
		wrapper = new ByteWrapper( object.getTrailingEffectsCode().getEncodedLength() );
		object.getTrailingEffectsCode().encode(wrapper);
		map.put( trailingEffectsCode.getHandle(), wrapper.array() );

		// Apperance
		// Engine Smoke On
		wrapper = new ByteWrapper( object.getEngineSmokeOn().getEncodedLength() );
		object.getEngineSmokeOn().encode(wrapper);
		map.put( engineSmokeOn.getHandle(), wrapper.array() );

		// Fire Power Disabled
		wrapper = new ByteWrapper( object.getFirePowerDisabled().getEncodedLength() );
		object.getFirePowerDisabled().encode(wrapper);
		map.put( firePowerDisabled.getHandle(), wrapper.array() );

		// Flames Present
		wrapper = new ByteWrapper( object.getFlamesPresent().getEncodedLength() );
		object.getFlamesPresent().encode(wrapper);
		map.put( flamesPresent.getHandle(), wrapper.array() );

		// Has Ammunition Supply Capability
		wrapper = new ByteWrapper( object.getHasAmmunitionSupplyCap().getEncodedLength() );
		object.getHasAmmunitionSupplyCap().encode(wrapper);
		map.put( hasAmmunitionSupplyCap.getHandle(), wrapper.array() );

		// Has Fuel Supply Capability
		wrapper = new ByteWrapper( object.getHasFuelSupplyCap().getEncodedLength() );
		object.getHasFuelSupplyCap().encode(wrapper);
		map.put( hasFuelSupplyCap.getHandle(), wrapper.array() );

		// Has Recovery Supply Capability
		wrapper = new ByteWrapper( object.getHasRecoveryCap().getEncodedLength() );
		object.getHasRecoveryCap().encode(wrapper);
		map.put( hasRecoveryCap.getHandle(), wrapper.array() );

		// Has Repair Capability
		wrapper = new ByteWrapper( object.getHasRepairCap().getEncodedLength() );
		object.getHasRepairCap().encode(wrapper);
		map.put( hasRepairCap.getHandle(), wrapper.array() );

		// Immobilized
		wrapper = new ByteWrapper( object.getImmobilized().getEncodedLength() );
		object.getImmobilized().encode(wrapper);
		map.put( immobilized.getHandle(), wrapper.array() );

		// IsConcealed
		wrapper = new ByteWrapper( object.getIsConcealed().getEncodedLength() );
		object.getIsConcealed().encode(wrapper);
		map.put( isConcealed.getHandle(), wrapper.array() );

		// Smoke Plume Present
		wrapper = new ByteWrapper( object.getSmokePlumePresent().getEncodedLength() );
		object.getSmokePlumePresent().encode(wrapper);
		map.put( smokePlumePresent.getHandle(), wrapper.array() );

		// Tent Deployed
		wrapper = new ByteWrapper( object.getTentDeployed().getEncodedLength() );
		object.getTentDeployed().encode(wrapper);
		map.put( tentDeployed.getHandle(), wrapper.array() );

		// Powerplant On
		wrapper = new ByteWrapper( object.getPowerplantOn().getEncodedLength() );
		object.getPowerplantOn().encode(wrapper);
		map.put( powerplantOn.getHandle(), wrapper.array() );

		// Other
		// Live Entity Measured Speed
		wrapper = new ByteWrapper( object.getLiveEntityMeasuredSpeed().getEncodedLength() );
		object.getLiveEntityMeasuredSpeed().encode(wrapper);
		map.put( liveEntityMeasuredSpeed.getHandle(), wrapper.array() );

		// Radar Cross Section Signature Index
		wrapper = new ByteWrapper( object.getRadarCrossSectionSignatureIndex().getEncodedLength() );
		object.getRadarCrossSectionSignatureIndex().encode(wrapper);
		map.put( radarCrossSectionSignatureIndex.getHandle(), wrapper.array() );

		// Infrared Signature Index
		wrapper = new ByteWrapper( object.getInfraredSignatureIndex().getEncodedLength() );
		object.getInfraredSignatureIndex().encode(wrapper);
		map.put( infraredSignatureIndex.getHandle(), wrapper.array() );

		// Acoustic Signature Index
		wrapper = new ByteWrapper( object.getAcousticSignatureIndex().getEncodedLength() );
		object.getAcousticSignatureIndex().encode(wrapper);
		map.put( acousticSignatureIndex.getHandle(), wrapper.array() );

		// Arrays
		// Articulated Parameters Array
		wrapper = new ByteWrapper( object.getArticulatedParametersArray().getEncodedLength() );
		object.getArticulatedParametersArray().encode(wrapper);
		map.put( articulatedParametersArray.getHandle(), wrapper.array() );

		// Propulsion Systems Data
		wrapper = new ByteWrapper( object.getPropulsionSystemsData().getEncodedLength() );
		object.getPropulsionSystemsData().encode(wrapper);
		map.put( propulsionSystemsData.getHandle(), wrapper.array() );

		// Vectoring Nozzel System Data
		wrapper = new ByteWrapper( object.getVectoringNozzleSystemData().getEncodedLength() );
		object.getVectoringNozzleSystemData().encode(wrapper);
		map.put( vectoringNozzleSystemData.getHandle(), wrapper.array() );
		
		return map;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendHlaToDis( ObjectInstance hlaObject,
	                          AttributeHandleValueMap attributes,
	                          OpsCenter opscenter )
	{
		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( (PhysicalEntity)hlaObject, attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		// Turn the attribute into a PDU
		PDU pdu = hlaObject.toPdu();
		
		// Send the PDU off
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		opscenter.getPduReceiver().receive( pdu.toByteArray() );
	}

	private void deserializeFromHla( PhysicalEntity object, AttributeHandleValueMap map )
		throws DecoderException
	{
		////////////////////////////////
		// Base Entity  ////////////////
		////////////////////////////////
		// Entity Type
		if( map.containsKey(entityType.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(entityType.getHandle()) );
			object.getEntityType().decode( wrapper );
		}
		
		// Entity Identifier
		if( map.containsKey(entityIdentifier.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(entityIdentifier.getHandle()) );
    		object.getEntityIdentifier().decode( wrapper );
		}

		// IsPartOf
		if( map.containsKey(isPartOf.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(isPartOf.getHandle()) );
			object.getIsPartOf().decode( wrapper );
		}

		// Spatial
		if( map.containsKey(spatial.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(spatial.getHandle()) );
			object.getSpatial().decode( wrapper );
		}

		// Relative Spatial
		if( map.containsKey(relativeSpatial.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(relativeSpatial.getHandle()) );
			object.getRelativeSpatial().decode( wrapper );
		}

		////////////////////////////////
		// Physical Entity  ////////////
		////////////////////////////////
		// Metadata
		// Alternate Entity Id
		if( map.containsKey(alternateEntityType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(alternateEntityType.getHandle()) );
			object.getAlternateEntityType().decode( wrapper );
		}

		// Force Identifier
		if( map.containsKey(forceIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(forceIdentifier.getHandle()) );
			object.getForceIdentifier().decode( wrapper );
		}

		// Marking
		if( map.containsKey(marking.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(marking.getHandle()) );
			object.getMarking().decode( wrapper );
		}

		// Status
		// Damage State
		if( map.containsKey(damageState.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(damageState.getHandle()) );
			object.getDamageState().decode( wrapper );
		}

		// Camouflage Type
		if( map.containsKey(camouflageType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(camouflageType.getHandle()) );
			object.getCamouflageType().decode( wrapper );
		}

		// Trailing Effects Code
		if( map.containsKey(trailingEffectsCode.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(trailingEffectsCode.getHandle()) );
			object.getTrailingEffectsCode().decode( wrapper );
		}

		// Apperance
		// Engine Smoke On
		if( map.containsKey(engineSmokeOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(engineSmokeOn.getHandle()) );
			object.getEngineSmokeOn().decode( wrapper );
		}

		// Fire Power Disabled
		if( map.containsKey(firePowerDisabled.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(firePowerDisabled.getHandle()) );
			object.getFirePowerDisabled().decode( wrapper );
		}

		// Flames Present
		if( map.containsKey(flamesPresent.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(flamesPresent.getHandle()) );
			object.getFlamesPresent().decode( wrapper );
		}

		// Has Ammunition Supply Capability
		if( map.containsKey(hasAmmunitionSupplyCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasAmmunitionSupplyCap.getHandle()) );
			object.getHasAmmunitionSupplyCap().decode( wrapper );
		}

		// Has Fuel Supply Capability
		if( map.containsKey(hasFuelSupplyCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasFuelSupplyCap.getHandle()) );
			object.getHasFuelSupplyCap().decode( wrapper );
		}

		// Has Recovery Supply Capability
		if( map.containsKey(hasRecoveryCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasRecoveryCap.getHandle()) );
			object.getHasRecoveryCap().decode( wrapper );
		}

		// Has Repair Capability
		if( map.containsKey(hasRepairCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasRepairCap.getHandle()) );
			object.getHasRepairCap().decode( wrapper );
		}

		// Immobilized
		if( map.containsKey(immobilized.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(immobilized.getHandle()) );
			object.getImmobilized().decode( wrapper );
		}

		// IsConcealed
		if( map.containsKey(isConcealed.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(isConcealed.getHandle()) );
			object.getIsConcealed().decode( wrapper );
		}

		// Smoke Plume Present
		if( map.containsKey(smokePlumePresent.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(smokePlumePresent.getHandle()) );
			object.getSmokePlumePresent().decode( wrapper );
		}

		// Tent Deployed
		if( map.containsKey(tentDeployed.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(tentDeployed.getHandle()) );
			object.getTentDeployed().decode( wrapper );
		}

		// Powerplant On
		if( map.containsKey(powerplantOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(powerplantOn.getHandle()) );
			object.getPowerplantOn().decode( wrapper );
		}

		// Other
		// Live Entity Measured Speed
		if( map.containsKey(liveEntityMeasuredSpeed.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(liveEntityMeasuredSpeed.getHandle()) );
			object.getLiveEntityMeasuredSpeed().decode( wrapper );
		}

		// Radar Cross Section Signature Index
		if( map.containsKey(radarCrossSectionSignatureIndex.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(radarCrossSectionSignatureIndex.getHandle()) );
			object.getRadarCrossSectionSignatureIndex().decode( wrapper );
		}

		// Infrared Signature Index
		if( map.containsKey(infraredSignatureIndex.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(infraredSignatureIndex.getHandle()) );
			object.getInfraredSignatureIndex().decode( wrapper );
		}

		// Acoustic Signature Index
		if( map.containsKey(acousticSignatureIndex.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(acousticSignatureIndex.getHandle()) );
			object.getAcousticSignatureIndex().decode( wrapper );
		}

		// Arrays
		// Articulated Parameters Array
		if( map.containsKey(articulatedParametersArray.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(articulatedParametersArray.getHandle()) );
			object.getArticulatedParametersArray().decode( wrapper );
		}

		// Propulsion Systems Data
		if( map.containsKey(propulsionSystemsData.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(propulsionSystemsData.getHandle()) );
			object.getPropulsionSystemsData().decode( wrapper );
		}

		// Vectoring Nozzel System Data
		if( map.containsKey(vectoringNozzleSystemData.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(vectoringNozzleSystemData.getHandle()) );
			object.getVectoringNozzleSystemData().decode( wrapper );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
