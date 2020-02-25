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
import org.openlvc.disco.connection.rpr.types.array.PropulsionSystemDataStructLengthlessArray;
import org.openlvc.disco.connection.rpr.types.array.VectoringNozzleSystemDataStructLengthlessArray;
import org.openlvc.disco.connection.rpr.types.basic.HLAinteger16BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.enumerated.CamouflageEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.DamageStatusEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.ForceIdentifierEnum8;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.connection.rpr.types.enumerated.TrailingEffectsCodeEnum32;
import org.openlvc.disco.connection.rpr.types.fixed.EntityTypeStruct;
import org.openlvc.disco.connection.rpr.types.fixed.MarkingStruct;
import org.openlvc.disco.pdu.entity.EntityStatePdu;

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
	protected EnumHolder<RPRboolean> engineSmokeOn;
	protected EnumHolder<RPRboolean> firePowerDisabled;
	protected EnumHolder<RPRboolean> flamesPresent;
	protected EnumHolder<RPRboolean> hasAmmunitionSupplyCap;
	protected EnumHolder<RPRboolean> hasFuelSupplyCap;
	protected EnumHolder<RPRboolean> hasRecoveryCap;
	protected EnumHolder<RPRboolean> hasRepairCap;
	protected EnumHolder<RPRboolean> immobilized;
	protected EnumHolder<RPRboolean> isConcealed;
	protected EnumHolder<RPRboolean> smokePlumePresent;
	protected EnumHolder<RPRboolean> tentDeployed;
	protected EnumHolder<RPRboolean> powerplantOn;

	// Other
	protected RPRunsignedInteger16BE liveEntityMeasuredSpeed;
	protected HLAinteger16BE radarCrossSectionSignatureIndex;
	protected HLAinteger16BE infraredSignatureIndex;
	protected HLAinteger16BE acousticSignatureIndex;

	// Arrays
	protected ArticulatedParameterStructLengthlessArray articulatedParametersArray;
	protected PropulsionSystemDataStructLengthlessArray propulsionSystemsData;
	protected VectoringNozzleSystemDataStructLengthlessArray vectoringNozzleSystemData;

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
		this.engineSmokeOn = new EnumHolder<>( RPRboolean.False );
		this.firePowerDisabled = new EnumHolder<>( RPRboolean.False );
		this.flamesPresent = new EnumHolder<>( RPRboolean.False );
		this.hasAmmunitionSupplyCap = new EnumHolder<>( RPRboolean.False );
		this.hasFuelSupplyCap = new EnumHolder<>( RPRboolean.False );
		this.hasRecoveryCap = new EnumHolder<>( RPRboolean.False );
		this.hasRepairCap = new EnumHolder<>( RPRboolean.False );
		this.immobilized = new EnumHolder<>( RPRboolean.False );
		this.isConcealed = new EnumHolder<>( RPRboolean.False );
		this.smokePlumePresent = new EnumHolder<>( RPRboolean.False );
		this.tentDeployed = new EnumHolder<>( RPRboolean.False );
		this.powerplantOn = new EnumHolder<>( RPRboolean.False );

		// Other
		this.liveEntityMeasuredSpeed = new RPRunsignedInteger16BE();
		this.radarCrossSectionSignatureIndex = new HLAinteger16BE();
		this.infraredSignatureIndex = new HLAinteger16BE();
		this.acousticSignatureIndex = new HLAinteger16BE();

		// Arrays
		this.articulatedParametersArray = new ArticulatedParameterStructLengthlessArray();
		this.propulsionSystemsData = new PropulsionSystemDataStructLengthlessArray();
		this.vectoringNozzleSystemData = new VectoringNozzleSystemDataStructLengthlessArray();
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

	public EnumHolder<RPRboolean> getEngineSmokeOn()
	{
		return engineSmokeOn;
	}

	public EnumHolder<RPRboolean> getFirePowerDisabled()
	{
		return firePowerDisabled;
	}

	public EnumHolder<RPRboolean> getFlamesPresent()
	{
		return flamesPresent;
	}

	public EnumHolder<RPRboolean> getHasAmmunitionSupplyCap()
	{
		return hasAmmunitionSupplyCap;
	}

	public EnumHolder<RPRboolean> getHasFuelSupplyCap()
	{
		return hasFuelSupplyCap;
	}

	public EnumHolder<RPRboolean> getHasRecoveryCap()
	{
		return hasRecoveryCap;
	}

	public EnumHolder<RPRboolean> getHasRepairCap()
	{
		return hasRepairCap;
	}

	public EnumHolder<RPRboolean> getImmobilized()
	{
		return immobilized;
	}

	public EnumHolder<RPRboolean> getIsConcealed()
	{
		return isConcealed;
	}

	public EnumHolder<RPRboolean> getSmokePlumePresent()
	{
		return smokePlumePresent;
	}

	public EnumHolder<RPRboolean> getTentDeployed()
	{
		return tentDeployed;
	}

	public EnumHolder<RPRboolean> getPowerplantOn()
	{
		return powerplantOn;
	}

	public RPRunsignedInteger16BE getLiveEntityMeasuredSpeed()
	{
		return liveEntityMeasuredSpeed;
	}

	public HLAinteger16BE getRadarCrossSectionSignatureIndex()
	{
		return radarCrossSectionSignatureIndex;
	}

	public HLAinteger16BE getInfraredSignatureIndex()
	{
		return infraredSignatureIndex;
	}

	public HLAinteger16BE getAcousticSignatureIndex()
	{
		return acousticSignatureIndex;
	}

	public ArticulatedParameterStructLengthlessArray getArticulatedParametersArray()
	{
		return articulatedParametersArray;
	}

	public PropulsionSystemDataStructLengthlessArray getPropulsionSystemsData()
	{
		return propulsionSystemsData;
	}

	public VectoringNozzleSystemDataStructLengthlessArray getVectoringNozzleSystemData()
	{
		return vectoringNozzleSystemData;
	}

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

		// Status
		//this.damageState.setEnum( 
		//this.camouflageType.setEnum( 
		//this.trailingEffectsCode.setEnum( 
		
		// Appearance Bits
		
		// Other
		
		// Arrays

		throw new RuntimeException( "Not Implemented Yet" );
	}
	
	protected void toPdu( EntityStatePdu pdu )
	{
		// pass up the tree
		super.toPdu( pdu );

		// Metadata
		
		// Status
		
		// Apperance Bits
		
		// Other
		
		// Arrays
		
		throw new RuntimeException( "Not Implemented Yet" );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
