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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.UnsupportedException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.RprConnection;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.Aircraft;
import org.openlvc.disco.connection.rpr.objects.GroundVehicle;
import org.openlvc.disco.connection.rpr.objects.Human;
import org.openlvc.disco.connection.rpr.objects.Lifeform;
import org.openlvc.disco.connection.rpr.objects.MultiDomainPlatform;
import org.openlvc.disco.connection.rpr.objects.PhysicalEntity;
import org.openlvc.disco.connection.rpr.objects.Platform;
import org.openlvc.disco.connection.rpr.objects.Spacecraft;
import org.openlvc.disco.connection.rpr.objects.SubmersibleVessel;
import org.openlvc.disco.connection.rpr.objects.SurfaceVessel;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.Kind;
import org.openlvc.disco.pdu.record.EntityType;

import hla.rti1516e.AttributeHandleValueMap;
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
public class EntityStateMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass platformClass;
	private ObjectClass airClass;
//	private ObjectClass amphibClass;
	private ObjectClass groundClass;
	private ObjectClass multiDomainClass;
	private ObjectClass surfaceClass;
	private ObjectClass subsurfaceClass;
	private ObjectClass spaceClass;
	private Map<Class<? extends Platform>,ObjectClass> javaTypeToHlaClassMap;
	private Map<ObjectClass,Supplier<? extends Platform>> hlaClassToJavaTypeMap;
	// Lifeform
	private ObjectClass lifeformClass;
	
	// Base Entity
	private AttributeClass entityType;
	private AttributeClass entityIdentifier;
	//private AttributeClass isPartOf;
	private AttributeClass spatial;
	//private AttributeClass relativeSpatial;

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
	private AttributeClass immobilized;
	private AttributeClass isConcealed;
	private AttributeClass smokePlumePresent;
	private AttributeClass tentDeployed;
	private AttributeClass powerplantOn;
	
	// Apperance >> Platform/Air Detailed
	private AttributeClass afterburnerOn;
	private AttributeClass antiCollisionLightsOn;
	private AttributeClass blackOutBrakeLightsOn;
	private AttributeClass blackOutLightsOn;
	private AttributeClass brakeLightsOn;
	private AttributeClass formationLightsOn;
	private AttributeClass hatchState;
	private AttributeClass headLightsOn;
	private AttributeClass interiorLightsOn;
	private AttributeClass landingLightsOn;
	private AttributeClass launcherRaised;
	private AttributeClass navigationLightsOn;
	private AttributeClass rampDeployed;
	//private AttributeClass runningLightsOn;
	private AttributeClass spotLightsOn;
	private AttributeClass tailLightsOn;


	// Capabilities
	private AttributeClass hasAmmunitionSupplyCap;
	private AttributeClass hasFuelSupplyCap;
	private AttributeClass hasRecoveryCap;
	private AttributeClass hasRepairCap;

	// Other
	//private AttributeClass liveEntityMeasuredSpeed;
	//private AttributeClass radarCrossSectionSignatureIndex;
	//private AttributeClass infraredSignatureIndex;
	//private AttributeClass acousticSignatureIndex;

	// Arrays
	private AttributeClass articulatedParametersArray;
	//private AttributeClass propulsionSystemsData;
	//private AttributeClass vectoringNozzleSystemData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityStateMapper( RprConnection connection ) throws DiscoException
	{
		super( connection );
		
		this.javaTypeToHlaClassMap = new HashMap<>();
		this.hlaClassToJavaTypeMap = new HashMap<>();
		initializeHandles();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void initializeHandles() throws DiscoException
	{
		// Cache up all the attributes we need
		this.platformClass = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform" );
		if( this.platformClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform" );
		
		// Get all the classes
		this.airClass          = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.Aircraft" );
//		this.amphibClass       = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.AmphibiousVehicle" );
		this.groundClass       = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.GroundVehicle" );
		this.multiDomainClass  = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.MultiDomainPlatform" );
		this.surfaceClass      = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.SurfaceVessel" );
		this.subsurfaceClass   = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.SubmersibleVessel" );
		this.spaceClass        = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.Spacecraft" );
		this.lifeformClass     = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform" );

		if( this.airClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.Aircraft" );
//		if( this.amphibClass == null )
//			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.AmphibiousVehicle" );
		if( this.groundClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.GroundVehicle" );
		if( this.multiDomainClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.MultiDomainPlatform" );
		if( this.surfaceClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.SurfaceVessel" );
		if( this.subsurfaceClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.SubmersibleVessel" );
		if( this.spaceClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.Spacecraft" );
		if( this.lifeformClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform" );

		// Base Entity
		this.entityType = platformClass.getAttribute( "EntityType" );
		this.entityIdentifier = platformClass.getAttribute( "EntityIdentifier" );
		//this.isPartOf = hlaClass.getAttribute( "IsPartOf" );
		this.spatial = platformClass.getAttribute( "Spatial" );
		//this.relativeSpatial = hlaClass.getAttribute( "RelativeSpatial" );

		// Physical Entity
		// Metadata
		this.alternateEntityType = platformClass.getAttribute( "AlternateEntityType" );
		this.forceIdentifier = platformClass.getAttribute( "ForceIdentifier" );
		this.marking = platformClass.getAttribute( "Marking" );

		// Status
		this.damageState = platformClass.getAttribute( "DamageState" );
		this.camouflageType = platformClass.getAttribute( "CamouflageType" );
		this.trailingEffectsCode = platformClass.getAttribute( "TrailingEffectsCode" );

		// Apperance
		this.engineSmokeOn = platformClass.getAttribute( "EngineSmokeOn" );
		this.firePowerDisabled = platformClass.getAttribute( "FirePowerDisabled" );
		this.flamesPresent = platformClass.getAttribute( "FlamesPresent" );
		this.immobilized = platformClass.getAttribute( "Immobilized" );
		this.isConcealed = platformClass.getAttribute( "IsConcealed" );
		this.smokePlumePresent = platformClass.getAttribute( "SmokePlumePresent" );
		this.tentDeployed = platformClass.getAttribute( "TentDeployed" );
		this.powerplantOn = platformClass.getAttribute( "PowerPlantOn" );
		
		// Appearance >> Detailed
		this.afterburnerOn = platformClass.getAttribute( "AfterburnerOn" );
		this.antiCollisionLightsOn = platformClass.getAttribute( "AntiCollisionLightsOn" );
		this.blackOutBrakeLightsOn = platformClass.getAttribute( "BlackOutBrakeLightsOn" );
		this.blackOutLightsOn = platformClass.getAttribute( "BlackOutLightsOn" );
		this.brakeLightsOn = platformClass.getAttribute( "BrakeLightsOn" );
		this.formationLightsOn = platformClass.getAttribute( "FormationLightsOn" );
		this.hatchState = platformClass.getAttribute( "HatchState" );
		this.headLightsOn = platformClass.getAttribute( "HeadLightsOn" );
		this.interiorLightsOn = platformClass.getAttribute( "InteriorLightsOn" );
		this.landingLightsOn = platformClass.getAttribute( "LandingLightsOn" );
		this.launcherRaised = platformClass.getAttribute( "LauncherRaised" );
		this.navigationLightsOn = platformClass.getAttribute( "NavigationLightsOn" );
		this.rampDeployed = platformClass.getAttribute( "RampDeployed" );
		//this.runningLightsOn = hlaClass.getAttribute( "RunningLightsOn" );
		this.spotLightsOn = platformClass.getAttribute( "SpotLightsOn" );
		this.tailLightsOn = platformClass.getAttribute( "TailLightsOn" );

		// Capabilities
		this.hasAmmunitionSupplyCap = platformClass.getAttribute( "HasAmmunitionSupplyCap" );
		this.hasFuelSupplyCap = platformClass.getAttribute( "HasFuelSupplyCap" );
		this.hasRecoveryCap = platformClass.getAttribute( "HasRecoveryCap" );
		this.hasRepairCap = platformClass.getAttribute( "HasRepairCap" );

		// Other
		//this.liveEntityMeasuredSpeed = hlaClass.getAttribute( "LiveEntityMeasuredSpeed" );
		//this.radarCrossSectionSignatureIndex = hlaClass.getAttribute( "RadarCrossSectionSignatureIndex" );
		//this.infraredSignatureIndex = hlaClass.getAttribute( "InfraredSignatureIndex" );
		//this.acousticSignatureIndex = hlaClass.getAttribute( "AcousticSignatureIndex" );

		// Arrays
		this.articulatedParametersArray = platformClass.getAttribute( "ArticulatedParametersArray" );
		//this.propulsionSystemsData = hlaClass.getAttribute( "PropulsionSystemsData" );
		//this.vectoringNozzleSystemData = hlaClass.getAttribute( "VectoringNozzleSystemData" );


		//
		// Set up our internal type <> HLA class maps
		//
		this.javaTypeToHlaClassMap.put( Aircraft.class, this.airClass );
//		this.javaTypeToHlaClassMap.put( Amphib..., this.amphibClass );
		this.javaTypeToHlaClassMap.put( GroundVehicle.class, this.groundClass );
		this.javaTypeToHlaClassMap.put( MultiDomainPlatform.class, this.multiDomainClass );
		this.javaTypeToHlaClassMap.put( SurfaceVessel.class, this.surfaceClass );
		this.javaTypeToHlaClassMap.put( SubmersibleVessel.class, this.subsurfaceClass );
		this.javaTypeToHlaClassMap.put( Spacecraft.class, this.spaceClass );
		
		this.hlaClassToJavaTypeMap.put( this.airClass, Aircraft::new );
//		this.hlaClassToJavaTypeMap.put( this.amphibClass, Amphib...::new );
		this.hlaClassToJavaTypeMap.put( this.groundClass, GroundVehicle::new );
		this.hlaClassToJavaTypeMap.put( this.multiDomainClass, MultiDomainPlatform::new );
		this.hlaClassToJavaTypeMap.put( this.surfaceClass, SurfaceVessel::new );
		this.hlaClassToJavaTypeMap.put( this.subsurfaceClass, SubmersibleVessel::new );
		this.hlaClassToJavaTypeMap.put( this.spaceClass, Spacecraft::new );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( EntityStatePdu pdu )
	{
System.out.println( "ENTITY STATE: "+pdu.getMarking() );
		// Do we already have an object cached for this entity?
		PhysicalEntity hlaObject = objectStore.getLocalEntity( pdu.getEntityID() );

		// If there is no HLA object yet, we have to register one
		if( hlaObject == null )
		{
			try
			{
    			// No object registered yet, do it now
    			hlaObject = createObject( pdu.getEntityType() );
    			hlaObject.setObjectClass( this.javaTypeToHlaClassMap.get(hlaObject.getClass()) );
    			super.registerObjectInstance( hlaObject );
    			objectStore.addLocalEntity( pdu.getEntityID(), hlaObject );
			}
			catch( UnsupportedException ue )
			{
				logger.trace( "dis >> hla (PhysicalEntity) DISCARD "+ue.getMessage() );
				return;
			}
		}

		// Suck the values out of the PDU and into the object
		hlaObject.fromPdu( pdu );
		
		// Send an update for the object
		super.sendAttributeUpdate( hlaObject, serializeToHla(hlaObject) );
		
		if( logger.isTraceEnabled() )
			logger.trace( "dis >> hla (PhysicalEntity) Updated attributes for entity: id=%s, handle=%s",
			              pdu.getEntityID(), hlaObject.getObjectHandle() );
	}

	private PhysicalEntity createObject( EntityType type )
	{
		// Kind
		// 0 = Other
		// 1 = Platform
		// 2 = Munition
		// 3 = Lifeform
		// 4 = Environmental
		// 5 = CulturalFeature
		// 6 = Supply
		// 7 = Radio
		// 8 = Expendable
		// 9 = SensorEmitter
		Kind kind = type.getKindEnum();
		if( kind == Kind.Platform || kind == Kind.Munition )
		{
			// Domain
			// 0 = Other
			// 1 = Land
			// 2 = Air
			// 3 = Surface
			// 4 = Subsurface
			// 5 = Space
			switch( type.getDomainEnum() )
			{
				case Land:       return new GroundVehicle();
				case Air:        return new Aircraft();
				case Surface:    return new SurfaceVessel();
				case Subsurface: return new SubmersibleVessel();
				case Space:      return new Spacecraft();
				default: throw new UnsupportedException( "[RPR] Unsupported Platform Domain: "+type.getDomainEnum().name() );
			}
		}
		else if( kind == Kind.Lifeform )
		{
			return new Human();
		}
		else
		{
			throw new UnsupportedException( "Unsupported Entity Kind: "+kind.name() );
		}
	}

	private AttributeHandleValueMap serializeToHla( PhysicalEntity object )
	{
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

		// IsPartOf -- Not Supported Yet. See IsPartOf Fixed Record
		//wrapper = new ByteWrapper( object.getIsPartOf().getEncodedLength() );
		//object.getIsPartOf().encode(wrapper);
		//map.put( isPartOf.getHandle(), wrapper.array() );

		// Spatial
		wrapper = new ByteWrapper( object.getSpatial().getEncodedLength() );
		object.getSpatial().encode(wrapper);
		map.put( spatial.getHandle(), wrapper.array() );

		// Relative Spatial -- Not Supported Yet. See IsPartOf Fixed Record (Related)
		//wrapper = new ByteWrapper( object.getRelativeSpatial().getEncodedLength() );
		//object.getRelativeSpatial().encode(wrapper);
		//map.put( relativeSpatial.getHandle(), wrapper.array() );

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

		//
		// Status & Appearance
		//
		// Write out the common elements
		//this.toHlaCommonAppearance( object, map ); -- done inside each of the subtypes
		
		//
		// Kind & Domain Specific Appearance
		//
		Kind disKind = object.getEntityType().getDisKind();
		if( disKind == Kind.Platform )
		{
			switch( object.getEntityType().getDisDomain() )
			{
				case Land: toHlaGroundPlatformAppearance(object,map); break;
				case Air:  toHlaAirPlatformAppearance(object,map); break;
				case Surface: toHlaSurfacePlatformAppearance(object,map); break;
				case Subsurface: toHlaSubsurfacePlatformAppearance(object,map); break;
				case Space: toHlaSpacePlatformAppearance(object,map); break;
				default:   break;
			}
		}
		else if( disKind == Kind.Lifeform )
		{
			toHlaLifeformAppearance( map );
		}
		
		//
		// Capabilities
		//
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

		//
		// Other
		//
		// Live Entity Measured Speed
		//wrapper = new ByteWrapper( object.getLiveEntityMeasuredSpeed().getEncodedLength() );
		//object.getLiveEntityMeasuredSpeed().encode(wrapper);
		//map.put( liveEntityMeasuredSpeed.getHandle(), wrapper.array() );

		// Radar Cross Section Signature Index
		//wrapper = new ByteWrapper( object.getRadarCrossSectionSignatureIndex().getEncodedLength() );
		//object.getRadarCrossSectionSignatureIndex().encode(wrapper);
		//map.put( radarCrossSectionSignatureIndex.getHandle(), wrapper.array() );

		// Infrared Signature Index
		//wrapper = new ByteWrapper( object.getInfraredSignatureIndex().getEncodedLength() );
		//object.getInfraredSignatureIndex().encode(wrapper);
		//map.put( infraredSignatureIndex.getHandle(), wrapper.array() );

		// Acoustic Signature Index
		//wrapper = new ByteWrapper( object.getAcousticSignatureIndex().getEncodedLength() );
		//object.getAcousticSignatureIndex().encode(wrapper);
		//map.put( acousticSignatureIndex.getHandle(), wrapper.array() );

		// Arrays
		// Articulated Parameters Array
		wrapper = new ByteWrapper( object.getArticulatedParametersArray().getEncodedLength() );
		object.getArticulatedParametersArray().encode(wrapper);
		map.put( articulatedParametersArray.getHandle(), wrapper.array() );

		// Propulsion Systems Data
		//wrapper = new ByteWrapper( object.getPropulsionSystemsData().getEncodedLength() );
		//object.getPropulsionSystemsData().encode(wrapper);
		//map.put( propulsionSystemsData.getHandle(), wrapper.array() );

		// Vectoring Nozzel System Data
		//wrapper = new ByteWrapper( object.getVectoringNozzleSystemData().getEncodedLength() );
		//object.getVectoringNozzleSystemData().encode(wrapper);
		//map.put( vectoringNozzleSystemData.getHandle(), wrapper.array() );
		
		return map;
	}
	
	private void toHlaCommonAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		// Camouflage Type
		// Damage State
		// Engine Smoking
		// Flames Present
		// Immobilized
		// Powerplant On
		// Smoke Plume Present
		// Trailing Effects Code
		
		// Camouflage Type
		ByteWrapper wrapper = new ByteWrapper( entity.getCamouflageType().getEncodedLength() );
		entity.getCamouflageType().encode(wrapper);
		map.put( camouflageType.getHandle(), wrapper.array() );

		// Damage State
		wrapper = new ByteWrapper( entity.getDamageState().getEncodedLength() );
		entity.getDamageState().encode(wrapper);
		map.put( damageState.getHandle(), wrapper.array() );

		// Engine Smoke On
		wrapper = new ByteWrapper( entity.getEngineSmokeOn().getEncodedLength() );
		entity.getEngineSmokeOn().encode(wrapper);
		map.put( engineSmokeOn.getHandle(), wrapper.array() );

		// Flames Present
		wrapper = new ByteWrapper( entity.getFlamesPresent().getEncodedLength() );
		entity.getFlamesPresent().encode(wrapper);
		map.put( flamesPresent.getHandle(), wrapper.array() );

		// Immobilized
		wrapper = new ByteWrapper( entity.getImmobilized().getEncodedLength() );
		entity.getImmobilized().encode(wrapper);
		map.put( immobilized.getHandle(), wrapper.array() );

		// Powerplant On
		wrapper = new ByteWrapper( entity.getPowerplantOn().getEncodedLength() );
		entity.getPowerplantOn().encode(wrapper);
		map.put( powerplantOn.getHandle(), wrapper.array() );

		// Smoke Plume Present
		wrapper = new ByteWrapper( entity.getSmokePlumePresent().getEncodedLength() );
		entity.getSmokePlumePresent().encode(wrapper);
		map.put( smokePlumePresent.getHandle(), wrapper.array() );

		// Trailing Effects Code
		wrapper = new ByteWrapper( entity.getTrailingEffectsCode().getEncodedLength() );
		entity.getTrailingEffectsCode().encode(wrapper);
		map.put( trailingEffectsCode.getHandle(), wrapper.array() );
	}

	private void toHlaGroundPlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		toHlaCommonAppearance(entity,map);

		// Cast it down to a ground platform
		Platform platform = (Platform)entity;

		// Fire Power Disabled
		ByteWrapper wrapper = new ByteWrapper( entity.getFirePowerDisabled().getEncodedLength() );
		entity.getFirePowerDisabled().encode(wrapper);
		map.put( firePowerDisabled.getHandle(), wrapper.array() );

		// IsConcealed
		wrapper = new ByteWrapper( entity.getIsConcealed().getEncodedLength() );
		entity.getIsConcealed().encode(wrapper);
		map.put( isConcealed.getHandle(), wrapper.array() );

		// Black Out Lights On
		wrapper = new ByteWrapper( platform.getBlackOutLightsOn().getEncodedLength() );
		platform.getBlackOutLightsOn().encode(wrapper);
		map.put( blackOutLightsOn.getHandle(), wrapper.array() );
		
		// Black Out Brake Lights On
		wrapper = new ByteWrapper( platform.getBlackOutBrakeLightsOn().getEncodedLength() );
		platform.getBlackOutBrakeLightsOn().encode(wrapper);
		map.put( blackOutBrakeLightsOn.getHandle(), wrapper.array() );
		
		// Brake Lights On
		wrapper = new ByteWrapper( platform.getBrakeLightsOn().getEncodedLength() );
		platform.getBrakeLightsOn().encode(wrapper);
		map.put( brakeLightsOn.getHandle(), wrapper.array() );
		
		// Hatch State
		wrapper = new ByteWrapper( platform.getHatchState().getEncodedLength() );
		platform.getHatchState().encode(wrapper);
		map.put( hatchState.getHandle(), wrapper.array() );

		// Headlights On
		wrapper = new ByteWrapper( platform.getHeadLightsOn().getEncodedLength() );
		platform.getHeadLightsOn().encode(wrapper);
		map.put( headLightsOn.getHandle(), wrapper.array() );
		
		// Interior Lights On
		wrapper = new ByteWrapper( platform.getInteriorLightsOn().getEncodedLength() );
		platform.getInteriorLightsOn().encode(wrapper);
		map.put( interiorLightsOn.getHandle(), wrapper.array() );
		
		// Launcher Raised
		wrapper = new ByteWrapper( platform.getLauncherRaised().getEncodedLength() );
		platform.getLauncherRaised().encode(wrapper);
		map.put( launcherRaised.getHandle(), wrapper.array() );
		
		// Ramp Deployed
		wrapper = new ByteWrapper( platform.getRampDeployed().getEncodedLength() );
		platform.getRampDeployed().encode(wrapper);
		map.put( rampDeployed.getHandle(), wrapper.array() );

		// Spot Lights On
		wrapper = new ByteWrapper( platform.getSpotLightsOn().getEncodedLength() );
		platform.getSpotLightsOn().encode(wrapper);
		map.put( spotLightsOn.getHandle(), wrapper.array() );
		
		// Tail lights On
		wrapper = new ByteWrapper( platform.getTailLightsOn().getEncodedLength() );
		platform.getTailLightsOn().encode(wrapper);
		map.put( tailLightsOn.getHandle(), wrapper.array() );
	}
	
	private void toHlaAirPlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		toHlaCommonAppearance(entity,map);

		Platform platform = (Platform)entity;

		// AfterburnerOn
		ByteWrapper wrapper = new ByteWrapper( platform.getAfterburnerOn().getEncodedLength() );
		platform.getAfterburnerOn().encode(wrapper);
		map.put( afterburnerOn.getHandle(), wrapper.array() );

		// AntiCollisionLightsOn
		wrapper = new ByteWrapper( platform.getAntiCollisionLightsOn().getEncodedLength() );
		platform.getAntiCollisionLightsOn().encode(wrapper);
		map.put( antiCollisionLightsOn.getHandle(), wrapper.array() );
		
		// Formation Lights On
		wrapper = new ByteWrapper( platform.getFormationLightsOn().getEncodedLength() );
		platform.getFormationLightsOn().encode(wrapper);
		map.put( formationLightsOn.getHandle(), wrapper.array() );
		
		// Interior Lights On
		wrapper = new ByteWrapper( platform.getInteriorLightsOn().getEncodedLength() );
		platform.getInteriorLightsOn().encode(wrapper);
		map.put( interiorLightsOn.getHandle(), wrapper.array() );
		
		// Landing Lights On
		wrapper = new ByteWrapper( platform.getLandingLightsOn().getEncodedLength() );
		platform.getLandingLightsOn().encode(wrapper);
		map.put( landingLightsOn.getHandle(), wrapper.array() );
		
		// Navigation Lights On
		wrapper = new ByteWrapper( platform.getNavigationLightsOn().getEncodedLength() );
		platform.getNavigationLightsOn().encode(wrapper);
		map.put( navigationLightsOn.getHandle(), wrapper.array() );

		// Spot Lights On
		wrapper = new ByteWrapper( platform.getSpotLightsOn().getEncodedLength() );
		platform.getSpotLightsOn().encode(wrapper);
		map.put( spotLightsOn.getHandle(), wrapper.array() );
	}
	
	private void toHlaSurfacePlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		// Not yet implemented
	}
	
	private void toHlaSubsurfacePlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		// Not yet implemented
	}
	
	private void toHlaSpacePlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		// Not yet implemented
	}
	
	private void toHlaLifeformAppearance( AttributeHandleValueMap map )
	{
		// Not yet implemented
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( event.theClass == this.lifeformClass )
		{
			// Just skip it for now
		}
		else if( isValid(event.theClass) )
		{
			Platform hlaObject = createPlatformOf( event.theClass );
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			hlaObject.setObjectName( event.objectName );
			objectStore.addDiscoveredHlaObject( hlaObject );

			if( logger.isDebugEnabled() )
			{
    			logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
    			              event.theClass.getLocalName(),
    			              event.theObject );
			}
			
			// Request an attribute update for the object so that we can get everything we need
			super.requestAttributeUpdate( hlaObject );
			if( logger.isTraceEnabled() )
			{
				logger.trace( "hla >> dis (Discover) Requested attribute update for [%d] attributes of [%s]",
				              hlaObject.getObjectAttributes().size(),
				              hlaObject.getObjectHandle().toString() );
			}
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		if( (event.hlaObject instanceof PhysicalEntity) == false )
			return;
		
		PhysicalEntity rprEntity = (PhysicalEntity)event.hlaObject;

		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( rprEntity, event.attributes );
		}
		catch( DecoderException de )
		{
			de.printStackTrace();
			throw new DiscoException( de.getMessage(), de );
		}
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		if( isReady(rprEntity) )
		{
			opscenter.getPduReceiver().receive( rprEntity.toPdu().toByteArray() );
			event.hlaObject.setLastUpdatedTimeToNow();
		}
	}
	
	private boolean isReady( PhysicalEntity rprEntity )
	{
		return rprEntity.getEntityType().isDecodeCalled() &&
		       rprEntity.getEntityIdentifier().isDecodeCalled();
	}
	
	private boolean isValid( ObjectClass type )
	{
		return hlaClassToJavaTypeMap.containsKey( type );
	}
	
	private Platform createPlatformOf( ObjectClass type )
	{
		return hlaClassToJavaTypeMap.get(type).get();
	}
	
	
	private void deserializeFromHla( PhysicalEntity entity, AttributeHandleValueMap map )
		throws DecoderException
	{
		////////////////////////////////
		// Base Entity  ////////////////
		////////////////////////////////
		// Entity Type
		if( map.containsKey(entityType.getHandle()) )
		{
   		    ByteWrapper wrapper = new ByteWrapper( map.get(entityType.getHandle()) );
			entity.getEntityType().decode( wrapper );
		}
		
		// Entity Identifier
		if( map.containsKey(entityIdentifier.getHandle()) )
		{
    		ByteWrapper wrapper = new ByteWrapper( map.get(entityIdentifier.getHandle()) );
    		entity.getEntityIdentifier().decode( wrapper );
		}

		// IsPartOf
		//if( map.containsKey(isPartOf.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(isPartOf.getHandle()) );
		//	object.getIsPartOf().decode( wrapper );
		//}

		// Spatial
		if( map.containsKey(spatial.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(spatial.getHandle()) );
			entity.getSpatial().decode( wrapper );
		}

		// Relative Spatial
		//if( map.containsKey(relativeSpatial.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(relativeSpatial.getHandle()) );
		//	object.getRelativeSpatial().decode( wrapper );
		//}

		////////////////////////////////
		// Physical Entity  ////////////
		////////////////////////////////
		//
		// Metadata
		//
		// Alternate Entity Id
		if( map.containsKey(alternateEntityType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(alternateEntityType.getHandle()) );
			entity.getAlternateEntityType().decode( wrapper );
		}

		// Force Identifier
		if( map.containsKey(forceIdentifier.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(forceIdentifier.getHandle()) );
			entity.getForceIdentifier().decode( wrapper );
		}

		// Marking
		if( map.containsKey(marking.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(marking.getHandle()) );
			entity.getMarking().decode( wrapper );
		}

		//
		// Status & Appearance
		//
		// Write out the common elements
		this.deserializePhysicalEntityAppearanceFromHla( entity, map );
		
		//
		// Kind & Domain Specific Appearance
		//
		Kind disKind = entity.getEntityType().getDisKind();
		if( disKind == Kind.Platform )
		{
			switch( entity.getEntityType().getDisDomain() )
			{
				case Land: deserializeGroundPlatformAppearanceFromHla( (Platform)entity, map );break;
				case Air:  deserializeAirPlatformFromHla( (Platform)entity, map );break;
				default:   break;
			}
		}
		else if( disKind == Kind.Lifeform )
		{
			deserializeLifeformFromHla( (Lifeform)entity, map );
		}
		
		//
		// Capabilities
		//
		// Has Ammunition Supply Capability
		if( map.containsKey(hasAmmunitionSupplyCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasAmmunitionSupplyCap.getHandle()) );
			entity.getHasAmmunitionSupplyCap().decode( wrapper );
		}

		// Has Fuel Supply Capability
		if( map.containsKey(hasFuelSupplyCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasFuelSupplyCap.getHandle()) );
			entity.getHasFuelSupplyCap().decode( wrapper );
		}

		// Has Recovery Supply Capability
		if( map.containsKey(hasRecoveryCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasRecoveryCap.getHandle()) );
			entity.getHasRecoveryCap().decode( wrapper );
		}

		// Has Repair Capability
		if( map.containsKey(hasRepairCap.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hasRepairCap.getHandle()) );
			entity.getHasRepairCap().decode( wrapper );
		}

		//
		// Other
		//
		// Live Entity Measured Speed
		//if( map.containsKey(liveEntityMeasuredSpeed.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(liveEntityMeasuredSpeed.getHandle()) );
		//	object.getLiveEntityMeasuredSpeed().decode( wrapper );
		//}

		// Radar Cross Section Signature Index
		//if( map.containsKey(radarCrossSectionSignatureIndex.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(radarCrossSectionSignatureIndex.getHandle()) );
		//	object.getRadarCrossSectionSignatureIndex().decode( wrapper );
		//}

		// Infrared Signature Index
		//if( map.containsKey(infraredSignatureIndex.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(infraredSignatureIndex.getHandle()) );
		//	object.getInfraredSignatureIndex().decode( wrapper );
		//}

		// Acoustic Signature Index
		//if( map.containsKey(acousticSignatureIndex.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(acousticSignatureIndex.getHandle()) );
		//	object.getAcousticSignatureIndex().decode( wrapper );
		//}

		// Arrays
		// Articulated Parameters Array
		if( map.containsKey(articulatedParametersArray.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(articulatedParametersArray.getHandle()) );
			entity.getArticulatedParametersArray().decode( wrapper );
		}

		// Propulsion Systems Data
		//if( map.containsKey(propulsionSystemsData.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(propulsionSystemsData.getHandle()) );
		//	object.getPropulsionSystemsData().decode( wrapper );
		//}

		// Vectoring Nozzel System Data
		//if( map.containsKey(vectoringNozzleSystemData.getHandle()) )
		//{
		//	ByteWrapper wrapper = new ByteWrapper( map.get(vectoringNozzleSystemData.getHandle()) );
		//	object.getVectoringNozzleSystemData().decode( wrapper );
		//}
	}

	private void deserializePhysicalEntityAppearanceFromHla( PhysicalEntity entity,
	                                                         AttributeHandleValueMap map )
		throws DecoderException
	{
		//
		// Status
		//
		// Camouflage Type
		if( map.containsKey(camouflageType.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(camouflageType.getHandle()) );
			entity.getCamouflageType().decode( wrapper );
		}

		// Damage State
		if( map.containsKey(damageState.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(damageState.getHandle()) );
			entity.getDamageState().decode( wrapper );
		}

		// Engine Smoke On
		if( map.containsKey(engineSmokeOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(engineSmokeOn.getHandle()) );
			entity.getEngineSmokeOn().decode( wrapper );
		}

		// Firepower Disabled
		if( map.containsKey(firePowerDisabled.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(firePowerDisabled.getHandle()) );
			entity.getFirePowerDisabled().decode( wrapper );
		}
		
		// Flames Present
		if( map.containsKey(flamesPresent.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(flamesPresent.getHandle()) );
			entity.getFlamesPresent().decode( wrapper );
		}

		// Immobilized
		if( map.containsKey(immobilized.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(immobilized.getHandle()) );
			entity.getImmobilized().decode( wrapper );
		}
		
		// Concealed
		if( map.containsKey(isConcealed.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(isConcealed.getHandle()) );
			entity.getIsConcealed().decode( wrapper );
		}

		// Powerplant On
		if( map.containsKey(powerplantOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(powerplantOn.getHandle()) );
			entity.getPowerplantOn().decode( wrapper );
		}

		// Smoke Plume Present
		if( map.containsKey(smokePlumePresent.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(smokePlumePresent.getHandle()) );
			entity.getSmokePlumePresent().decode( wrapper );
		}
		
		// Tent Deployed
		if( map.containsKey(tentDeployed.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(tentDeployed.getHandle()) );
			entity.getTentDeployed().decode( wrapper );
		}

		// Trailing Effects Code
		if( map.containsKey(trailingEffectsCode.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(trailingEffectsCode.getHandle()) );
			entity.getTrailingEffectsCode().decode( wrapper );
		}
	}
	
	private void deserializeGroundPlatformAppearanceFromHla( Platform platform,
	                                                         AttributeHandleValueMap map )
		throws DecoderException
	{
		// Blackout Brake Lights
		if( map.containsKey(blackOutBrakeLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(blackOutBrakeLightsOn.getHandle()) );
			platform.getBlackOutBrakeLightsOn().decode( wrapper );
		}

		// Blackout Lights
		if( map.containsKey(blackOutLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(blackOutLightsOn.getHandle()) );
			platform.getBlackOutLightsOn().decode( wrapper );
		}

		// Brake Lights
		if( map.containsKey(brakeLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(brakeLightsOn.getHandle()) );
			platform.getBrakeLightsOn().decode( wrapper );
		}
		
		// Hatch State
		if( map.containsKey(hatchState.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(hatchState.getHandle()) );
			platform.getHatchState().decode( wrapper );
		}

		// Headlights
		if( map.containsKey(headLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(headLightsOn.getHandle()) );
			platform.getHeadLightsOn().decode( wrapper );
		}

		// Interior Lights
		if( map.containsKey(interiorLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(interiorLightsOn.getHandle()) );
			platform.getInteriorLightsOn().decode( wrapper );
		}

		// Launcher Raised
		if( map.containsKey(launcherRaised.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(launcherRaised.getHandle()) );
			platform.getLauncherRaised().decode( wrapper );
		}

		// Ramp Deployed
		if( map.containsKey(rampDeployed.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(rampDeployed.getHandle()) );
			platform.getRampDeployed().decode( wrapper );
		}

		// Spot Lights
		if( map.containsKey(spotLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(spotLightsOn.getHandle()) );
			platform.getSpotLightsOn().decode( wrapper );
		}

		// Tail Lights
		if( map.containsKey(tailLightsOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(tailLightsOn.getHandle()) );
			platform.getTailLightsOn().decode( wrapper );
		}
	}
	
	private void deserializeAirPlatformFromHla( Platform platform, AttributeHandleValueMap map )
		throws DecoderException
	{
		// Afterburner On
		if( map.containsKey(afterburnerOn.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(afterburnerOn.getHandle()) );
			platform.getAfterburnerOn().decode( wrapper );
		}
	}
	
	private void deserializeLifeformFromHla( Lifeform lifeform, AttributeHandleValueMap map )
	{
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
