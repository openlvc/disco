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

import org.openlvc.disco.connection.rpr.types.array.ArticulatedParameterStructLengthlessArray;
import org.openlvc.disco.connection.rpr.types.enumerated.CamouflageEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.DamageStatusEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.ForceIdentifierEnum8;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.enumerated.TrailingEffectsCodeEnum32;
import org.openlvc.disco.connection.rpr.types.fixed.ArticulatedParameterStruct;
import org.openlvc.disco.connection.rpr.types.fixed.EntityTypeStruct;
import org.openlvc.disco.connection.rpr.types.fixed.MarkingStruct;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.record.ArticulationParameter;

/**
 * Partially implemented. We only use some of this at the moment, so postponing some work for 
 * low impact areas.
 */
public abstract class PhysicalEntity extends BaseEntity
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	// Metadata
	protected EntityTypeStruct alternateEntityType;
	protected EnumHolder<ForceIdentifierEnum8> forceIdentifier;
	protected MarkingStruct marking;

	// Status
	protected EnumHolder<DamageStatusEnum32> damageState;
	protected EnumHolder<CamouflageEnum32> camouflageType;
	protected EnumHolder<TrailingEffectsCodeEnum32> trailingEffectsCode;

	// Apperance
	protected RPRboolean engineSmokeOn;
	protected RPRboolean firePowerDisabled;
	protected RPRboolean flamesPresent;
	protected RPRboolean immobilized;
	protected RPRboolean isConcealed;
	protected RPRboolean smokePlumePresent;
	protected RPRboolean tentDeployed;
	protected RPRboolean powerplantOn;
	
	// Capabilities
	protected RPRboolean hasAmmunitionSupplyCap;
	protected RPRboolean hasFuelSupplyCap;
	protected RPRboolean hasRecoveryCap;
	protected RPRboolean hasRepairCap;

	// Other - SEES PDU
	//protected RPRunsignedInteger16BE liveEntityMeasuredSpeed;
	//protected HLAinteger16BE radarCrossSectionSignatureIndex;
	//protected HLAinteger16BE infraredSignatureIndex;
	//protected HLAinteger16BE acousticSignatureIndex;

	// Arrays
	protected ArticulatedParameterStructLengthlessArray articulatedParametersArray;
	//protected PropulsionSystemDataStructLengthlessArray propulsionSystemsData;
	//protected VectoringNozzleSystemDataStructLengthlessArray vectoringNozzleSystemData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PhysicalEntity()
	{
		super();

		// Metadata
		this.alternateEntityType = new EntityTypeStruct();
		this.forceIdentifier = new EnumHolder<>( ForceIdentifierEnum8.Friendly );
		this.marking = new MarkingStruct();
		
		// Status
		this.damageState = new EnumHolder<>( DamageStatusEnum32.NoDamage );
		this.camouflageType = new EnumHolder<>( CamouflageEnum32.GenericCamouflage );
		this.trailingEffectsCode = new EnumHolder<>( TrailingEffectsCodeEnum32.NoTrail );
		
		// Appearance Bits
		this.engineSmokeOn = new RPRboolean(false);
		this.firePowerDisabled = new RPRboolean(false);
		this.flamesPresent = new RPRboolean(false);
		this.immobilized = new RPRboolean(false);
		this.isConcealed = new RPRboolean(false);
		this.smokePlumePresent = new RPRboolean(false);
		this.tentDeployed = new RPRboolean(false);
		this.powerplantOn = new RPRboolean(false);
		
		// Capabilities
		this.hasAmmunitionSupplyCap = new RPRboolean(false);
		this.hasFuelSupplyCap = new RPRboolean(false);
		this.hasRecoveryCap = new RPRboolean(false);
		this.hasRepairCap = new RPRboolean(false);

		// Other - SEES PDU
		//this.liveEntityMeasuredSpeed = new RPRunsignedInteger16BE();
		//this.radarCrossSectionSignatureIndex = new HLAinteger16BE();
		//this.infraredSignatureIndex = new HLAinteger16BE();
		//this.acousticSignatureIndex = new HLAinteger16BE();

		// Arrays
		this.articulatedParametersArray = new ArticulatedParameterStructLengthlessArray();
		//this.propulsionSystemsData = new PropulsionSystemDataStructLengthlessArray();
		//this.vectoringNozzleSystemData = new VectoringNozzleSystemDataStructLengthlessArray();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityTypeStruct getAlternateEntityType()
	{
		return alternateEntityType;
	}

	public EnumHolder<ForceIdentifierEnum8> getForceIdentifier()
	{
		return forceIdentifier;
	}

	public MarkingStruct getMarking()
	{
		return marking;
	}

	public EnumHolder<DamageStatusEnum32> getDamageState()
	{
		return damageState;
	}

	public EnumHolder<CamouflageEnum32> getCamouflageType()
	{
		return camouflageType;
	}

	public EnumHolder<TrailingEffectsCodeEnum32> getTrailingEffectsCode()
	{
		return trailingEffectsCode;
	}

	public RPRboolean getEngineSmokeOn()
	{
		return engineSmokeOn;
	}

	public RPRboolean getFirePowerDisabled()
	{
		return firePowerDisabled;
	}

	public RPRboolean getFlamesPresent()
	{
		return flamesPresent;
	}

	public RPRboolean getHasAmmunitionSupplyCap()
	{
		return hasAmmunitionSupplyCap;
	}

	public RPRboolean getHasFuelSupplyCap()
	{
		return hasFuelSupplyCap;
	}

	public RPRboolean getHasRecoveryCap()
	{
		return hasRecoveryCap;
	}

	public RPRboolean getHasRepairCap()
	{
		return hasRepairCap;
	}

	public RPRboolean getImmobilized()
	{
		return immobilized;
	}

	public RPRboolean getIsConcealed()
	{
		return isConcealed;
	}

	public RPRboolean getSmokePlumePresent()
	{
		return smokePlumePresent;
	}

	public RPRboolean getTentDeployed()
	{
		return tentDeployed;
	}

	public RPRboolean getPowerplantOn()
	{
		return powerplantOn;
	}

	//public RPRunsignedInteger16BE getLiveEntityMeasuredSpeed()
	//{
	//	return liveEntityMeasuredSpeed;
	//}

	//public HLAinteger16BE getRadarCrossSectionSignatureIndex()
	//{
	//	return radarCrossSectionSignatureIndex;
	//}

	//public HLAinteger16BE getInfraredSignatureIndex()
	//{
	//	return infraredSignatureIndex;
	//}

	//public HLAinteger16BE getAcousticSignatureIndex()
	//{
	//	return acousticSignatureIndex;
	//}

	public ArticulatedParameterStructLengthlessArray getArticulatedParametersArray()
	{
		return articulatedParametersArray;
	}

	//public PropulsionSystemDataStructLengthlessArray getPropulsionSystemsData()
	//{
	//	return propulsionSystemsData;
	//}

	//public VectoringNozzleSystemDataStructLengthlessArray getVectoringNozzleSystemData()
	//{
	//	return vectoringNozzleSystemData;
	//}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void fromPdu( EntityStatePdu incoming )
	{
		// pass up the tree
		super.fromPdu( incoming );

		// Metadata
		this.alternateEntityType.setValue( incoming.getAlternativeEntityType() );
		this.forceIdentifier.setEnum( ForceIdentifierEnum8.valueOf(incoming.getForceID().value()) );
		this.marking.setValue( incoming.getMarking() );

		// Status -- Delegated to child class
		
		// Appearance -- Delegated to child class
		
		// Capabilities
		this.hasAmmunitionSupplyCap.setValue( incoming.getCapabilities().getAmmunitionSupply() );
		this.hasFuelSupplyCap.setValue( incoming.getCapabilities().getFuelSupply() );
		this.hasRecoveryCap.setValue( incoming.getCapabilities().getRecovery() );
		this.hasRepairCap.setValue( incoming.getCapabilities().getRepair() );

		
		// Other
		//this.liveEntityMeasuredSpeed.setValue(  );
		//this.radarCrossSectionSignatureIndex.setValue(  );
		//this.infraredSignatureIndex.setValue(  );
		//this.acousticSignatureIndex.setValue(  );
		
		// Arrays
		this.articulatedParametersArray.clear();
		for( ArticulationParameter param : incoming.getArticulationParameter() )
		{
			ArticulatedParameterStruct struct = new ArticulatedParameterStruct();
			struct.setValue( param );
			articulatedParametersArray.addElement( struct );
		}
	}
	
	protected void toPdu( EntityStatePdu pdu )
	{
		// pass up the tree
		super.toPdu( pdu );

		// Metadata
		pdu.setAlternativeEntityType( alternateEntityType.getDisValue() );
		pdu.setForceID( ForceId.fromValue(forceIdentifier.getEnum().getValue()) );
		pdu.setMarking( marking.getDisValue() );
		
		// Status -- Delegated to child class
		
		// Apperance Bits -- Deletated to child class
		
		// Capabilities
		pdu.getCapabilities().setAmmunitionSupply( hasAmmunitionSupplyCap.getValue() );
		pdu.getCapabilities().setFuelSupply( hasFuelSupplyCap.getValue() );
		pdu.getCapabilities().setRecovery( hasRecoveryCap.getValue() );
		pdu.getCapabilities().setRepair( hasRepairCap.getValue() );
		
		// Other
		// liveEntityMeasuredSpeed -- Not in ESPDU
		// radarCrossSectionSignatureIndex -- Not in ESPDU
		// infraredSignatureIndex -- Not in ESPDU
		// acousticSignatureIndex -- Not in ESPDU
		
		// Arrays
		for( ArticulatedParameterStruct struct : this.articulatedParametersArray )
			pdu.getArticulationParameter().add( struct.getDisValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
