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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.UnsupportedException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.objects.Aircraft;
import org.openlvc.disco.connection.rpr.objects.GroundVehicle;
import org.openlvc.disco.connection.rpr.objects.Human;
import org.openlvc.disco.connection.rpr.objects.Lifeform;
import org.openlvc.disco.connection.rpr.objects.MultiDomainPlatform;
import org.openlvc.disco.connection.rpr.objects.NonHuman;
import org.openlvc.disco.connection.rpr.objects.PhysicalEntity;
import org.openlvc.disco.connection.rpr.objects.Platform;
import org.openlvc.disco.connection.rpr.objects.Spacecraft;
import org.openlvc.disco.connection.rpr.objects.SubmersibleVessel;
import org.openlvc.disco.connection.rpr.objects.SurfaceVessel;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.EntityKind;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityType;

import hla.rti1516e.AttributeHandleValueMap;

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
	private Map<Class<? extends PhysicalEntity>,ObjectClass> javaTypeToHlaClassMap;
	private Map<ObjectClass,Supplier<? extends PhysicalEntity>> hlaClassToJavaTypeMap;
	// Lifeform
	private ObjectClass lifeformClass;
	private ObjectClass humanClass;
	private ObjectClass nonHumanClass;
	
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
	public EntityStateMapper() throws DiscoException
	{
		this.javaTypeToHlaClassMap = new HashMap<>();
		this.hlaClassToJavaTypeMap = new HashMap<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.EntityState );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
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
		
		
		this.lifeformClass     = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform" );
		if( this.lifeformClass == null )
			throw new DiscoException( "Could not find class HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform" );
		
		this.humanClass        = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform.Human" );
		this.nonHumanClass     = rprConnection.getFom().getObjectClass( "HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform.NonHuman" );
		if( this.humanClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform.Human" );
		if( this.nonHumanClass == null )
			throw new DiscoException( "Could not find class: HLAobjectRoot.BaseEntity.PhysicalEntity.Lifeform.NonHuman" );
		
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
		this.javaTypeToHlaClassMap.put( Human.class, this.humanClass );
		this.javaTypeToHlaClassMap.put( GroundVehicle.class, this.groundClass );
		this.javaTypeToHlaClassMap.put( MultiDomainPlatform.class, this.multiDomainClass );
		this.javaTypeToHlaClassMap.put( NonHuman.class, this.nonHumanClass );
		this.javaTypeToHlaClassMap.put( SurfaceVessel.class, this.surfaceClass );
		this.javaTypeToHlaClassMap.put( SubmersibleVessel.class, this.subsurfaceClass );
		this.javaTypeToHlaClassMap.put( Spacecraft.class, this.spaceClass );
		
		this.hlaClassToJavaTypeMap.put( this.airClass, Aircraft::new );
//		this.hlaClassToJavaTypeMap.put( this.amphibClass, Amphib...::new );
		this.hlaClassToJavaTypeMap.put( this.humanClass, Human::new );
		this.hlaClassToJavaTypeMap.put( this.groundClass, GroundVehicle::new );
		this.hlaClassToJavaTypeMap.put( this.multiDomainClass, MultiDomainPlatform::new );
		this.hlaClassToJavaTypeMap.put( this.nonHumanClass, NonHuman::new );
		this.hlaClassToJavaTypeMap.put( this.surfaceClass, SurfaceVessel::new );
		this.hlaClassToJavaTypeMap.put( this.subsurfaceClass, SubmersibleVessel::new );
		this.hlaClassToJavaTypeMap.put( this.spaceClass, Spacecraft::new );
		
		//
		// Publication and Subscription
		//
		super.publishAndSubscribe( airClass );
		super.publishAndSubscribe( humanClass );
		super.publishAndSubscribe( groundClass );
		super.publishAndSubscribe( multiDomainClass );
		super.publishAndSubscribe( nonHumanClass );
		super.publishAndSubscribe( surfaceClass );
		super.publishAndSubscribe( subsurfaceClass );
		super.publishAndSubscribe( spaceClass );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( EntityStatePdu pdu )
	{
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
		EntityKind kind = type.getKindEnum();
		if( kind == EntityKind.Platform || kind == EntityKind.Munition )
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
		else if( kind == EntityKind.Lifeform )
		{
			return new Human();
		}
		else
		{
			throw new UnsupportedException( "Unsupported Entity Kind: "+kind.name() );
		}
	}

	private AttributeHandleValueMap serializeToHla( PhysicalEntity hlaObject )
	{
		AttributeHandleValueMap map = hlaObject.getObjectAttributes();
		
		////////////////////////////////
		// Base Entity  ////////////////
		////////////////////////////////
		// Entity Type
		hlaEncode( hlaObject.getEntityType(), entityType, map );
		
		// Entity Identifier
		hlaEncode( hlaObject.getEntityIdentifier(), entityIdentifier, map );

		// IsPartOf -- Not Supported Yet. See IsPartOf Fixed Record
		//hlaEncode( hlaObject.getIsPartOf(), isPartOf, map );

		// Spatial
		hlaEncode( hlaObject.getSpatial(), spatial, map );

		// Relative Spatial -- Not Supported Yet. See IsPartOf Fixed Record (Related)
		//hlaEncode( hlaObject.getRelativeSpatial(), relativeSpatial, map );

		////////////////////////////////
		// Physical Entity  ////////////
		////////////////////////////////
		// Metadata
		// Alternate Entity Id
		hlaEncode( hlaObject.getAlternateEntityType(), alternateEntityType, map );

		// Force Identifier
		hlaEncode( hlaObject.getForceIdentifier(), forceIdentifier, map );

		// Marking
		hlaEncode( hlaObject.getMarking(), marking, map );

		//
		// Status & Appearance
		//
		// Write out the common elements
		//this.toHlaCommonAppearance( object, map ); -- done inside each of the subtypes
		
		//
		// Kind & Domain Specific Appearance
		//
		EntityKind disKind = hlaObject.getEntityType().getDisKind();
		if( disKind == EntityKind.Platform )
		{
			switch( hlaObject.getEntityType().getDisDomain() )
			{
				case Land: toHlaGroundPlatformAppearance(hlaObject,map); break;
				case Air:  toHlaAirPlatformAppearance(hlaObject,map); break;
				case Surface: toHlaSurfacePlatformAppearance(hlaObject,map); break;
				case Subsurface: toHlaSubsurfacePlatformAppearance(hlaObject,map); break;
				case Space: toHlaSpacePlatformAppearance(hlaObject,map); break;
				default:   break;
			}
		}
		else if( disKind == EntityKind.Lifeform )
		{
			toHlaLifeformAppearance( map );
		}
		
		//
		// Capabilities
		//
		// Has Ammunition Supply Capability
		hlaEncode( hlaObject.getHasAmmunitionSupplyCap(), hasAmmunitionSupplyCap, map );

		// Has Fuel Supply Capability
		hlaEncode( hlaObject.getHasFuelSupplyCap(), hasFuelSupplyCap, map );

		// Has Recovery Supply Capability
		hlaEncode( hlaObject.getHasRecoveryCap(), hasRecoveryCap, map );

		// Has Repair Capability
		hlaEncode( hlaObject.getHasRepairCap(), hasRepairCap, map );

		//
		// Other
		//
		// Live Entity Measured Speed
		//hlaEncode( hlaObject.getLiveEntityMeasuredSpeed(), liveEntityMeasuredSpeed, map );

		// Radar Cross Section Signature Index
		//hlaEncode( hlaObject.getRadarCrossSectionSignatureIndex(), radarCrossSectionSignatureIndex, map );

		// Infrared Signature Index
		//hlaEncode( hlaObject.getInfraredSignatureIndex(), infraredSignatureIndex, map );

		// Acoustic Signature Index
		//hlaEncode( hlaObject.getAcousticSignatureIndex(), acousticSignatureIndex, map );

		// Arrays
		// Articulated Parameters Array
		hlaEncode( hlaObject.getArticulatedParametersArray(), articulatedParametersArray, map );

		// Propulsion Systems Data
		//hlaEncode( hlaObject.getPropulsionSystemsData(), propulsionSystemsData, map );

		// Vectoring Nozzel System Data
		//hlaEncode( hlaObject.getVectoringNozzleSystemData(), vectoringNozzleSystemData, map );
		
		return map;
	}
	
	private void toHlaCommonAppearance( PhysicalEntity hlaObject, AttributeHandleValueMap map )
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
		hlaEncode( hlaObject.getCamouflageType(), camouflageType, map );

		// Damage State
		hlaEncode( hlaObject.getDamageState(), damageState, map );

		// Engine Smoke On
		hlaEncode( hlaObject.getEngineSmokeOn(), engineSmokeOn, map );

		// Flames Present
		hlaEncode( hlaObject.getFlamesPresent(), flamesPresent, map );

		// Immobilized
		hlaEncode( hlaObject.getImmobilized(), immobilized, map );

		// Powerplant On
		hlaEncode( hlaObject.getPowerplantOn(), powerplantOn, map );

		// Smoke Plume Present
		hlaEncode( hlaObject.getSmokePlumePresent(), smokePlumePresent, map );

		// Trailing Effects Code
		hlaEncode( hlaObject.getTrailingEffectsCode(), trailingEffectsCode, map );
	}

	private void toHlaGroundPlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		toHlaCommonAppearance(entity,map);

		// Cast it down to a ground platform
		Platform platform = (Platform)entity;

		// Fire Power Disabled
		hlaEncode( entity.getFirePowerDisabled(), firePowerDisabled, map );

		// IsConcealed
		hlaEncode( entity.getIsConcealed(), isConcealed, map );

		// Black Out Lights On
		hlaEncode( platform.getBlackOutLightsOn(), blackOutLightsOn, map );
		
		// Black Out Brake Lights On
		hlaEncode( platform.getBlackOutBrakeLightsOn(), blackOutBrakeLightsOn, map );
		
		// Brake Lights On
		hlaEncode( platform.getBrakeLightsOn(), brakeLightsOn, map );
		
		// Hatch State
		hlaEncode( platform.getHatchState(), hatchState, map );

		// Headlights On
		hlaEncode( platform.getHeadLightsOn(), headLightsOn, map );
		
		// Interior Lights On
		hlaEncode( platform.getInteriorLightsOn(), interiorLightsOn, map );
		
		// Launcher Raised
		hlaEncode( platform.getLauncherRaised(), launcherRaised, map );
		
		// Ramp Deployed
		hlaEncode( platform.getRampDeployed(), rampDeployed, map );

		// Spot Lights On
		hlaEncode( platform.getSpotLightsOn(), spotLightsOn, map );
		
		// Tail lights On
		hlaEncode( platform.getTailLightsOn(), tailLightsOn, map );
	}
	
	private void toHlaAirPlatformAppearance( PhysicalEntity entity, AttributeHandleValueMap map )
	{
		toHlaCommonAppearance(entity,map);

		Platform platform = (Platform)entity;

		// AfterburnerOn
		hlaEncode( platform.getAfterburnerOn(), afterburnerOn, map );

		// AntiCollisionLightsOn
		hlaEncode( platform.getAntiCollisionLightsOn(), antiCollisionLightsOn, map );
		
		// Formation Lights On
		hlaEncode( platform.getFormationLightsOn(), formationLightsOn, map );
		
		// Interior Lights On
		hlaEncode( platform.getInteriorLightsOn(), interiorLightsOn, map );
		
		// Landing Lights On
		hlaEncode( platform.getLandingLightsOn(), landingLightsOn, map );
		
		// Navigation Lights On
		hlaEncode( platform.getNavigationLightsOn(), navigationLightsOn, map );

		// Spot Lights On
		hlaEncode( platform.getSpotLightsOn(), spotLightsOn, map );
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
		if( isValid(event.theClass) )
		{
			PhysicalEntity hlaObject = createEntityOf( event.theClass );
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			hlaObject.setObjectName( event.objectName );
			hlaObject.setObjectAttributes( super.createAttributes(event.theClass) );
			objectStore.addDiscoveredHlaObject( hlaObject );

			if( logger.isDebugEnabled() )
			{
    			logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
    			              event.theClass.getLocalName(),
    			              event.theObject );
			}
			
			// Request an attribute update for the object so that we can get everything we need
			super.requestAttributeUpdate( hlaObject );
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		if( (event.hlaObject instanceof PhysicalEntity) == false )
			return;
		
		PhysicalEntity rprEntity = (PhysicalEntity)event.hlaObject;
		
		// Update the local object representations from the received attributes
		deserializeFromHla( rprEntity, event.attributes );
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		if( isReady(rprEntity) )
		{
			opscenter.getPduReceiver().receive( rprEntity.toPdu().toByteArray() );
			event.hlaObject.setLastUpdatedTimeToNow();
			
			// We need to update the object store so that is has an accurate list of
			// objects by entity id so that we can look up RPR object by DIS site/app/entity id.
			objectStore.updateRtiIdForDisId( rprEntity.getDisId(), rprEntity.getRtiObjectId() );
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
	
	private PhysicalEntity createEntityOf( ObjectClass type )
	{
		return hlaClassToJavaTypeMap.get(type).get();
	}
	
	
	private void deserializeFromHla( PhysicalEntity hlaObject, AttributeHandleValueMap map )
	{
		////////////////////////////////
		// Base Entity  ////////////////
		////////////////////////////////
		// Entity Type
		hlaDecode( hlaObject.getEntityType(), entityType, map );
		
		// Entity Identifier
		hlaDecode( hlaObject.getEntityIdentifier(), entityIdentifier, map );

		// IsPartOf -- Not Supported Yet. See IsPartOf Fixed Record
		//hlaDecode( hlaObject.getIsPartOf(), isPartOf, map );

		// Spatial
		hlaDecode( hlaObject.getSpatial(), spatial, map );

		// Relative Spatial -- Not Supported Yet. See IsPartOf Fixed Record (Related)
		//hlaDecode( hlaObject.getRelativeSpatial(), relativeSpatial, map );

		////////////////////////////////
		// Physical Entity  ////////////
		////////////////////////////////
		// Metadata
		// Alternate Entity Id
		hlaDecode( hlaObject.getAlternateEntityType(), alternateEntityType, map );

		// Force Identifier
		hlaDecode( hlaObject.getForceIdentifier(), forceIdentifier, map );

		// Marking
		hlaDecode( hlaObject.getMarking(), marking, map );

		//
		// Status & Appearance
		//
		// Write out the common elements
		this.deserializePhysicalEntityAppearanceFromHla( hlaObject, map );
		
		//
		// Kind & Domain Specific Appearance
		//
		EntityKind disKind = hlaObject.getEntityType().getDisKind();
		if( disKind == EntityKind.Platform )
		{
			switch( hlaObject.getEntityType().getDisDomain() )
			{
				case Land: deserializeGroundPlatformAppearanceFromHla( (Platform)hlaObject, map );break;
				case Air:  deserializeAirPlatformFromHla( (Platform)hlaObject, map );break;
				default:   break;
			}
		}
		else if( disKind == EntityKind.Lifeform )
		{
			deserializeLifeformFromHla( (Lifeform)hlaObject, map );
		}
		
		//
		// Capabilities
		//
		// Has Ammunition Supply Capability
		hlaDecode( hlaObject.getHasAmmunitionSupplyCap(), hasAmmunitionSupplyCap, map );

		// Has Fuel Supply Capability
		hlaDecode( hlaObject.getHasFuelSupplyCap(), hasFuelSupplyCap, map );

		// Has Recovery Supply Capability
		hlaDecode( hlaObject.getHasRecoveryCap(), hasRecoveryCap, map );

		// Has Repair Capability
		hlaDecode( hlaObject.getHasRepairCap(), hasRepairCap, map );

		//
		// Other
		//
		// Live Entity Measured Speed
		//hlaDecode( hlaObject.getLiveEntityMeasuredSpeed(), liveEntityMeasuredSpeed, map );

		// Radar Cross Section Signature Index
		//hlaDecode( hlaObject.getRadarCrossSectionSignatureIndex(), radarCrossSectionSignatureIndex, map );

		// Infrared Signature Index
		//hlaDecode( hlaObject.getInfraredSignatureIndex(), infraredSignatureIndex, map );

		// Acoustic Signature Index
		//hlaDecode( hlaObject.getAcousticSignatureIndex(), acousticSignatureIndex, map );

		// Arrays
		// Articulated Parameters Array
		hlaDecode( hlaObject.getArticulatedParametersArray(), articulatedParametersArray, map );

		// Propulsion Systems Data
		//hlaDecode( hlaObject.getPropulsionSystemsData(), propulsionSystemsData, map );

		// Vectoring Nozzel System Data
		//hlaDecode( hlaObject.getVectoringNozzleSystemData(), vectoringNozzleSystemData, map );
	}

	private void deserializePhysicalEntityAppearanceFromHla( PhysicalEntity hlaObject,
	                                                         AttributeHandleValueMap map )
	{
		//
		// Status
		//
		// Camouflage Type
		hlaDecode( hlaObject.getCamouflageType(), camouflageType, map );

		// Damage State
		hlaDecode( hlaObject.getDamageState(), damageState, map );

		// Engine Smoke On
		hlaDecode( hlaObject.getEngineSmokeOn(), engineSmokeOn, map );

		// Firepower Disabled
		hlaDecode( hlaObject.getFirePowerDisabled(), firePowerDisabled, map );
		
		// Flames Present
		hlaDecode( hlaObject.getFlamesPresent(), flamesPresent, map );

		// Immobilized
		hlaDecode( hlaObject.getImmobilized(), immobilized, map );
		
		// Concealed
		hlaDecode( hlaObject.getIsConcealed(), isConcealed, map );

		// Powerplant On
		hlaDecode( hlaObject.getPowerplantOn(), powerplantOn, map );

		// Smoke Plume Present
		hlaDecode( hlaObject.getSmokePlumePresent(), smokePlumePresent, map );
		
		// Tent Deployed
		hlaDecode( hlaObject.getTentDeployed(), tentDeployed, map );

		// Trailing Effects Code
		hlaDecode( hlaObject.getTrailingEffectsCode(), trailingEffectsCode, map );
	}
	
	private void deserializeGroundPlatformAppearanceFromHla( Platform hlaObject,
	                                                         AttributeHandleValueMap map )
	{
		// Blackout Brake Lights
		hlaDecode( hlaObject.getBlackOutBrakeLightsOn(), blackOutBrakeLightsOn, map );

		// Blackout Lights
		hlaDecode( hlaObject.getBlackOutLightsOn(), blackOutLightsOn, map );

		// Brake Lights
		hlaDecode( hlaObject.getBrakeLightsOn(), brakeLightsOn, map );
		
		// Hatch State
		hlaDecode( hlaObject.getHatchState(), hatchState, map );

		// Headlights
		hlaDecode( hlaObject.getHeadLightsOn(), headLightsOn, map );

		// Interior Lights
		hlaDecode( hlaObject.getInteriorLightsOn(), interiorLightsOn, map );

		// Launcher Raised
		hlaDecode( hlaObject.getLauncherRaised(), launcherRaised, map );

		// Ramp Deployed
		hlaDecode( hlaObject.getRampDeployed(), rampDeployed, map );

		// Spot Lights
		hlaDecode( hlaObject.getSpotLightsOn(), spotLightsOn, map );

		// Tail Lights
		hlaDecode( hlaObject.getTailLightsOn(), tailLightsOn, map );
	}
	
	private void deserializeAirPlatformFromHla( Platform hlaObject, AttributeHandleValueMap map )
	{
		// Afterburner On
		hlaDecode( hlaObject.getAfterburnerOn(), afterburnerOn, map );
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
