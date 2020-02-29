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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.connection.rpr.types.enumerated.CamouflageEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.DamageStatusEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.HatchStateEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.enumerated.TrailingEffectsCodeEnum32;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.Domain;
import org.openlvc.disco.pdu.field.appearance.AircraftAppearance;
import org.openlvc.disco.pdu.field.appearance.CommonAppearance;
import org.openlvc.disco.pdu.field.appearance.GroundPlatformAppearance;
import org.openlvc.disco.pdu.field.appearance.enums.CamouflageType;

public abstract class Platform extends PhysicalEntity
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected RPRboolean afterburnerOn;
	protected RPRboolean antiCollisionLightsOn;
	protected RPRboolean blackOutBrakeLightsOn;
	protected RPRboolean blackOutLightsOn;
	protected RPRboolean brakeLightsOn;
	protected RPRboolean formationLightsOn;
	protected EnumHolder<HatchStateEnum32> hatchState;
	protected RPRboolean headLightsOn;
	protected RPRboolean interiorLightsOn;
	protected RPRboolean landingLightsOn;
	protected RPRboolean launcherRaised;
	protected RPRboolean navigationLightsOn;
	protected RPRboolean rampDeployed;
	protected RPRboolean runningLightsOn;
	protected RPRboolean spotLightsOn;
	protected RPRboolean tailLightsOn;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected Platform()
	{
		super();
		
		this.afterburnerOn = new RPRboolean();
		this.antiCollisionLightsOn = new RPRboolean();
		this.blackOutBrakeLightsOn = new RPRboolean();
		this.blackOutLightsOn = new RPRboolean();
		this.brakeLightsOn = new RPRboolean();
		this.formationLightsOn = new RPRboolean();
		this.hatchState = new EnumHolder<HatchStateEnum32>( HatchStateEnum32.NotApplicable );
		this.headLightsOn = new RPRboolean();
		this.interiorLightsOn = new RPRboolean();
		this.landingLightsOn = new RPRboolean();
		this.launcherRaised = new RPRboolean();
		this.navigationLightsOn = new RPRboolean();
		this.rampDeployed = new RPRboolean();
		this.runningLightsOn = new RPRboolean();
		this.spotLightsOn = new RPRboolean();
		this.tailLightsOn = new RPRboolean();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RPRboolean getAfterburnerOn()
	{
		return afterburnerOn;
	}
	
	public RPRboolean getAntiCollisionLightsOn()
	{
		return antiCollisionLightsOn;
	}

	public RPRboolean getBlackOutBrakeLightsOn()
	{
		return blackOutBrakeLightsOn;
	}

	public RPRboolean getBlackOutLightsOn()
	{
		return blackOutLightsOn;
	}

	public RPRboolean getBrakeLightsOn()
	{
		return brakeLightsOn;
	}

	public RPRboolean getFormationLightsOn()
	{
		return formationLightsOn;
	}

	public EnumHolder<HatchStateEnum32> getHatchState()
	{
		return hatchState;
	}

	public RPRboolean getHeadLightsOn()
	{
		return headLightsOn;
	}

	public RPRboolean getInteriorLightsOn()
	{
		return interiorLightsOn;
	}
	
	public RPRboolean getLandingLightsOn()
	{
		return landingLightsOn;
	}

	public RPRboolean getLauncherRaised()
	{
		return launcherRaised;
	}

	public RPRboolean getNavigationLightsOn()
	{
		return navigationLightsOn;
	}

	public RPRboolean getRampDeployed()
	{
		return rampDeployed;
	}

	public RPRboolean getTentDeployed()
	{
		return tentDeployed;
	}

	public RPRboolean getRunningLightsOn()
	{
		return runningLightsOn;
	}

	public RPRboolean getSpotLightsOn()
	{
		return spotLightsOn;
	}

	public RPRboolean getTailLightsOn()
	{
		return tailLightsOn;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void fromPdu( EntityStatePdu incoming )
	{
		// pass up the tree
		super.fromPdu( incoming );
		
		//
		// APPEARANCE >> Platform Common -- ALWAYS
		//
		{
			CommonAppearance appearance = new CommonAppearance( incoming.getAppearance() );
			// Status
			this.damageState.setEnum( DamageStatusEnum32.valueOf(appearance.getDamageStateValue()) );
			
			// Appearance
			this.engineSmokeOn.setValue( appearance.isEngineSmoking() );
			this.flamesPresent.setValue( appearance.isFlaming() );
			this.smokePlumePresent.setValue( appearance.isSmokeEmanating() );
			this.powerplantOn.setValue( appearance.isPowerplantOn() );
		}
		
		if( incoming.getEntityType().getDomainEnum() == Domain.Land )
		{
			//
			// Land Vehicle Only
			//
			GroundPlatformAppearance appearance = new GroundPlatformAppearance( incoming.getAppearance() );
			this.camouflageType.setEnum( CamouflageEnum32.valueOf(appearance.getCamouflageType().rprValue()) );
			this.trailingEffectsCode.setEnum( TrailingEffectsCodeEnum32.valueOf(appearance.getDustTrailValue()) );
			this.immobilized.setValue( appearance.isMobilityKilled() );
			this.firePowerDisabled.setValue( appearance.isFirepowerKilled() );
			this.blackOutLightsOn.setValue( appearance.isBlackOutLightsOn() );
			this.blackOutBrakeLightsOn.setValue( appearance.isBlackoutBreakLightsOn() );
			this.brakeLightsOn.setValue( appearance.isBreakLightsOn() );
			this.headLightsOn.setValue( appearance.isHeadLightsOn() );
			this.interiorLightsOn.setValue( appearance.isInteriorLightsOn() );
			this.tailLightsOn.setValue( appearance.isTailLightsOn() );
			this.launcherRaised.setValue( appearance.isLauncherRaised() );
			this.rampDeployed.setValue( appearance.isRampDeployed() );
			this.tentDeployed.setValue( appearance.isTentExtended() );
			this.spotLightsOn.setValue( appearance.isSpotLightsOn() );
			this.isConcealed.setValue( appearance.isConcealed() );
			this.hatchState.setEnum( HatchStateEnum32.valueOf(appearance.getHatchStateValue()) );
		}
		else if( incoming.getEntityType().getDomainEnum() == Domain.Air )
		{
			//
			// Aircraft Only
			//
			AircraftAppearance appearance = new AircraftAppearance( incoming.getAppearance() );
			this.afterburnerOn.setValue( appearance.isAfterburnerOn() );
			this.antiCollisionLightsOn.setValue( appearance.isAntiCollisionLightsOn() );
			this.formationLightsOn.setValue( appearance.isFormationLightsOn() );
			this.interiorLightsOn.setValue( appearance.isInteriorLightsOn() );
			this.landingLightsOn.setValue( appearance.isLandingLightsOn() );
			this.navigationLightsOn.setValue( appearance.isNavigationLightsOn() );
			this.spotLightsOn.setValue( appearance.isSpotLightsOn() );
		}
	}
	
	protected void toPdu( EntityStatePdu pdu ) // TODO Why am I passing in the PDU?
	{
		// pass up the tree
		super.toPdu( pdu );
		
		// Appearance
		switch( super.entityType.getDisDomain() )
		{
			case Land: // GroundVehicle
				pdu.setAppearance( toGroundVehicleAppearance() );
				break;
			case Air: // Aircraft
				pdu.setAppearance( toAircraftAppearance() );
				break;
			default:  // Not Supported Yet -- Do nothing
				break;
		}
	}
	
	private int toGroundVehicleAppearance()
	{
		GroundPlatformAppearance appearance = new GroundPlatformAppearance();
		
		// Appearance -- Common
		appearance.setDamageState( (int)this.damageState.getEnum().getValue() );
		appearance.setEngineSmoking( this.engineSmokeOn.getValue() );
		appearance.setFlaming( this.flamesPresent.getValue() );
		appearance.setSmokeEmanating( this.smokePlumePresent.getValue() );
		appearance.setPowerplantOn( this.powerplantOn.getValue() );
		
		// Appearance -- Ground Vehicle
		appearance.setCamouflageType( CamouflageType.fromValue((byte)this.camouflageType.getEnum().getValue()) );
		appearance.setDustTrail( (byte)this.trailingEffectsCode.getEnum().getValue() );
		appearance.setMobilityKilled( this.immobilized.getValue() );
		appearance.setFirepowerKilled( this.firePowerDisabled.getValue() );
		appearance.setBlackOutLightsOn( this.blackOutLightsOn.getValue() );
		appearance.setBlackoutBreakLightsOn( this.blackOutBrakeLightsOn.getValue() );
		appearance.setBreakLightsOn( this.brakeLightsOn.getValue() );
		appearance.setHeadLightsOn( this.headLightsOn.getValue() );
		appearance.setInteriorLightsOn( this.interiorLightsOn.getValue() );
		appearance.setTailLightsOn( this.tailLightsOn.getValue() );
		appearance.setLauncherRaised( this.launcherRaised.getValue() );
		appearance.setRampDeployed( this.rampDeployed.getValue() );
		appearance.setTentExtended( this.tentDeployed.getValue() );
		appearance.setConcealed( this.isConcealed.getValue() );
		appearance.setSpotLightsOn( this.spotLightsOn.getValue() );
		appearance.setHatchState( (byte)this.hatchState.getEnum().getValue() );
		
		return appearance.getBits();
	}
	
	private int toAircraftAppearance()
	{
		AircraftAppearance appearance = new AircraftAppearance();
		
		// Appearance -- Common
		appearance.setDamageState( (int)this.damageState.getEnum().getValue() );
		appearance.setEngineSmoking( this.engineSmokeOn.getValue() );
		appearance.setFlaming( this.flamesPresent.getValue() );
		appearance.setSmokeEmanating( this.smokePlumePresent.getValue() );
		appearance.setPowerplantOn( this.powerplantOn.getValue() );
		
		// Appearance -- Aircraft
		appearance.setAfterburnerOn( this.afterburnerOn.getValue() );
		appearance.setAntiCollisionLightsOn( this.antiCollisionLightsOn.getValue() );
		appearance.setFormationLightsOn( this.formationLightsOn.getValue() );
		appearance.setInteriorLightsOn( this.interiorLightsOn.getValue() );
		appearance.setLandingLightsOn( this.landingLightsOn.getValue() );
		appearance.setNavigationLightsOn( this.navigationLightsOn.getValue() );
		appearance.setSpotLightsOn( this.spotLightsOn.getValue() );

		return appearance.getBits();
	}

	
	public void printMyAppearance()
	{
		System.out.println( "setDamageState: "+(int)this.damageState.getEnum().getValue() );
		System.out.println( "setEngineSmoking: "+this.engineSmokeOn.getValue() );
		System.out.println( "setFlaming: "+this.flamesPresent.getValue() );
		System.out.println( "setSmokeEmanating: "+this.smokePlumePresent.getValue() );
		System.out.println( "setPowerplantOn: "+this.powerplantOn.getValue() );
		// Appearance -- Ground Vehicle
		System.out.println( "setMobilityKilled: "+this.immobilized.getValue() );
		System.out.println( "setFirepowerKilled: "+this.firePowerDisabled.getValue() );
		System.out.println( "setBlackOutLightsOn: "+this.blackOutLightsOn.getValue() );
		System.out.println( "setBlackoutBreakLightsOn: "+this.blackOutBrakeLightsOn.getValue() );
		System.out.println( "setBreakLightsOn: "+this.brakeLightsOn.getValue() );
		System.out.println( "setHeadLightsOn: "+this.headLightsOn.getValue() );
		System.out.println( "setInteriorLightsOn: "+this.interiorLightsOn.getValue() );
		System.out.println( "setTailLightsOn: "+this.tailLightsOn.getValue() );
		System.out.println( "setLauncherRaised: "+this.launcherRaised.getValue() );
		System.out.println( "setRampDeployed: "+this.rampDeployed.getValue() );
		System.out.println( "setConealed: "+this.isConcealed.getValue() );
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
