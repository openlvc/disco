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
package org.openlvc.disco.connection.rpr.types.enumerated;

import java.util.HashMap;

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum ArticulatedPartsTypeEnum32 implements ExtendedDataElement<ArticulatedPartsTypeEnum32>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( new RPRunsignedInteger32BE(0) ),
	Rudder( new RPRunsignedInteger32BE(1024) ),
	LeftFlap( new RPRunsignedInteger32BE(1056) ),
	RightFlap( new RPRunsignedInteger32BE(1088) ),
	LeftAileron( new RPRunsignedInteger32BE(1120) ),
	RightAileron( new RPRunsignedInteger32BE(1152) ),
	HelicopterMainRotor( new RPRunsignedInteger32BE(1184) ),
	HelicopterTailRotor( new RPRunsignedInteger32BE(1216) ),
	OtherAircraftControlSurfaces( new RPRunsignedInteger32BE(1248) ),
	Periscope( new RPRunsignedInteger32BE(2048) ),
	GenericAntenna( new RPRunsignedInteger32BE(2080) ),
	Snorkel( new RPRunsignedInteger32BE(2112) ),
	OtherExtendableParts( new RPRunsignedInteger32BE(2144) ),
	LandingGear( new RPRunsignedInteger32BE(3072) ),
	TailHook( new RPRunsignedInteger32BE(3104) ),
	SpeedBrake( new RPRunsignedInteger32BE(3136) ),
	LeftWeaponBayDoors( new RPRunsignedInteger32BE(3168) ),
	RightWeaponBayDoors( new RPRunsignedInteger32BE(3200) ),
	TankOrAPChatch( new RPRunsignedInteger32BE(3232) ),
	Wingsweep( new RPRunsignedInteger32BE(3264) ),
	BridgeLauncher( new RPRunsignedInteger32BE(3296) ),
	BridgeSection1( new RPRunsignedInteger32BE(3328) ),
	BridgeSection2( new RPRunsignedInteger32BE(3360) ),
	BridgeSection3( new RPRunsignedInteger32BE(3392) ),
	PrimaryBlade1( new RPRunsignedInteger32BE(3424) ),
	PrimaryBlade2( new RPRunsignedInteger32BE(3456) ),
	PrimaryBoom( new RPRunsignedInteger32BE(3488) ),
	PrimaryLauncherArm( new RPRunsignedInteger32BE(3520) ),
	OtherFixedPositionParts( new RPRunsignedInteger32BE(3552) ),
	PrimaryTurretNumber1( new RPRunsignedInteger32BE(4096) ),
	PrimaryTurretNumber2( new RPRunsignedInteger32BE(4128) ),
	PrimaryTurretNumber3( new RPRunsignedInteger32BE(4160) ),
	PrimaryTurretNumber4( new RPRunsignedInteger32BE(4192) ),
	PrimaryTurretNumber5( new RPRunsignedInteger32BE(4224) ),
	PrimaryTurretNumber6( new RPRunsignedInteger32BE(4256) ),
	PrimaryTurretNumber7( new RPRunsignedInteger32BE(4288) ),
	PrimaryTurretNumber8( new RPRunsignedInteger32BE(4320) ),
	PrimaryTurretNumber9( new RPRunsignedInteger32BE(4352) ),
	PrimaryTurretNumber10( new RPRunsignedInteger32BE(4384) ),
	PrimaryGunNumber1( new RPRunsignedInteger32BE(4416) ),
	PrimaryGunNumber2( new RPRunsignedInteger32BE(4448) ),
	PrimaryGunNumber3( new RPRunsignedInteger32BE(4480) ),
	PrimaryGunNumber4( new RPRunsignedInteger32BE(4512) ),
	PrimaryGunNumber5( new RPRunsignedInteger32BE(4544) ),
	PrimaryGunNumber6( new RPRunsignedInteger32BE(4576) ),
	PrimaryGunNumber7( new RPRunsignedInteger32BE(4608) ),
	PrimaryGunNumber8( new RPRunsignedInteger32BE(4640) ),
	PrimaryGunNumber9( new RPRunsignedInteger32BE(4672) ),
	PrimaryGunNumber10( new RPRunsignedInteger32BE(4704) ),
	PrimaryLauncher1( new RPRunsignedInteger32BE(4736) ),
	PrimaryLauncher2( new RPRunsignedInteger32BE(4768) ),
	PrimaryLauncher3( new RPRunsignedInteger32BE(4800) ),
	PrimaryLauncher4( new RPRunsignedInteger32BE(4832) ),
	PrimaryLauncher5( new RPRunsignedInteger32BE(4864) ),
	PrimaryLauncher6( new RPRunsignedInteger32BE(4896) ),
	PrimaryLauncher7( new RPRunsignedInteger32BE(4928) ),
	PrimaryLauncher8( new RPRunsignedInteger32BE(4960) ),
	PrimaryLauncher9( new RPRunsignedInteger32BE(4992) ),
	PrimaryLauncher10( new RPRunsignedInteger32BE(5024) ),
	PrimaryDefenseSystems1( new RPRunsignedInteger32BE(5056) ),
	PrimaryDefenseSystems2( new RPRunsignedInteger32BE(5088) ),
	PrimaryDefenseSystems3( new RPRunsignedInteger32BE(5120) ),
	PrimaryDefenseSystems4( new RPRunsignedInteger32BE(5152) ),
	PrimaryDefenseSystems5( new RPRunsignedInteger32BE(5184) ),
	PrimaryDefenseSystems6( new RPRunsignedInteger32BE(5216) ),
	PrimaryDefenseSystems7( new RPRunsignedInteger32BE(5248) ),
	PrimaryDefenseSystems8( new RPRunsignedInteger32BE(5280) ),
	PrimaryDefenseSystems9( new RPRunsignedInteger32BE(5312) ),
	PrimaryDefenseSystems10( new RPRunsignedInteger32BE(5344) ),
	PrimaryRadar1( new RPRunsignedInteger32BE(5376) ),
	PrimaryRadar2( new RPRunsignedInteger32BE(5408) ),
	PrimaryRadar3( new RPRunsignedInteger32BE(5440) ),
	PrimaryRadar4( new RPRunsignedInteger32BE(5472) ),
	PrimaryRadar5( new RPRunsignedInteger32BE(5504) ),
	PrimaryRadar6( new RPRunsignedInteger32BE(5536) ),
	PrimaryRadar7( new RPRunsignedInteger32BE(5568) ),
	PrimaryRadar8( new RPRunsignedInteger32BE(5600) ),
	PrimaryRadar9( new RPRunsignedInteger32BE(5632) ),
	PrimaryRadar10( new RPRunsignedInteger32BE(5664) ),
	SecondaryTurretNumber1( new RPRunsignedInteger32BE(5696) ),
	SecondaryTurretNumber2( new RPRunsignedInteger32BE(5728) ),
	SecondaryTurretNumber3( new RPRunsignedInteger32BE(5760) ),
	SecondaryTurretNumber4( new RPRunsignedInteger32BE(5792) ),
	SecondaryTurretNumber5( new RPRunsignedInteger32BE(5824) ),
	SecondaryTurretNumber6( new RPRunsignedInteger32BE(5856) ),
	SecondaryTurretNumber7( new RPRunsignedInteger32BE(5888) ),
	SecondaryTurretNumber8( new RPRunsignedInteger32BE(5920) ),
	SecondaryTurretNumber9( new RPRunsignedInteger32BE(5952) ),
	SecondaryTurretNumber10( new RPRunsignedInteger32BE(5984) ),
	SecondaryGunNumber1( new RPRunsignedInteger32BE(6016) ),
	SecondaryGunNumber2( new RPRunsignedInteger32BE(6048) ),
	SecondaryGunNumber3( new RPRunsignedInteger32BE(6080) ),
	SecondaryGunNumber4( new RPRunsignedInteger32BE(6112) ),
	SecondaryGunNumber5( new RPRunsignedInteger32BE(6144) ),
	SecondaryGunNumber6( new RPRunsignedInteger32BE(6176) ),
	SecondaryGunNumber7( new RPRunsignedInteger32BE(6208) ),
	SecondaryGunNumber8( new RPRunsignedInteger32BE(6240) ),
	SecondaryGunNumber9( new RPRunsignedInteger32BE(6272) ),
	SecondaryGunNumber10( new RPRunsignedInteger32BE(6304) ),
	SecondaryLauncher1( new RPRunsignedInteger32BE(6336) ),
	SecondaryLauncher2( new RPRunsignedInteger32BE(6368) ),
	SecondaryLauncher3( new RPRunsignedInteger32BE(6400) ),
	SecondaryLauncher4( new RPRunsignedInteger32BE(6432) ),
	SecondaryLauncher5( new RPRunsignedInteger32BE(6464) ),
	SecondaryLauncher6( new RPRunsignedInteger32BE(6496) ),
	SecondaryLauncher7( new RPRunsignedInteger32BE(6528) ),
	SecondaryLauncher8( new RPRunsignedInteger32BE(6560) ),
	SecondaryLauncher9( new RPRunsignedInteger32BE(6592) ),
	SecondaryLauncher10( new RPRunsignedInteger32BE(6624) ),
	SecondaryDefenseSystems1( new RPRunsignedInteger32BE(6656) ),
	SecondaryDefenseSystems2( new RPRunsignedInteger32BE(6688) ),
	SecondaryDefenseSystems3( new RPRunsignedInteger32BE(6720) ),
	SecondaryDefenseSystems4( new RPRunsignedInteger32BE(6752) ),
	SecondaryDefenseSystems5( new RPRunsignedInteger32BE(6784) ),
	SecondaryDefenseSystems6( new RPRunsignedInteger32BE(6816) ),
	SecondaryDefenseSystems7( new RPRunsignedInteger32BE(6848) ),
	SecondaryDefenseSystems8( new RPRunsignedInteger32BE(6880) ),
	SecondaryDefenseSystems9( new RPRunsignedInteger32BE(6912) ),
	SecondaryDefenseSystems10( new RPRunsignedInteger32BE(6944) ),
	SecondaryRadar1( new RPRunsignedInteger32BE(6976) ),
	SecondaryRadar2( new RPRunsignedInteger32BE(7008) ),
	SecondaryRadar3( new RPRunsignedInteger32BE(7040) ),
	SecondaryRadar4( new RPRunsignedInteger32BE(7072) ),
	SecondaryRadar5( new RPRunsignedInteger32BE(7104) ),
	SecondaryRadar6( new RPRunsignedInteger32BE(7136) ),
	SecondaryRadar7( new RPRunsignedInteger32BE(7168) ),
	SecondaryRadar8( new RPRunsignedInteger32BE(7200) ),
	SecondaryRadar9( new RPRunsignedInteger32BE(7232) ),
	SecondaryRadar10( new RPRunsignedInteger32BE(7264) ),
	DeckElevator1( new RPRunsignedInteger32BE(7296) ),
	DeckElevator2( new RPRunsignedInteger32BE(7328) ),
	Catapult1( new RPRunsignedInteger32BE(7360) ),
	Catapult2( new RPRunsignedInteger32BE(7392) ),
	JetBlastDeflector1( new RPRunsignedInteger32BE(7424) ),
	JetBlastDeflector2( new RPRunsignedInteger32BE(7456) ),
	ArrestorWires1( new RPRunsignedInteger32BE(7488) ),
	ArrestorWires2( new RPRunsignedInteger32BE(7520) ),
	ArrestorWires3( new RPRunsignedInteger32BE(7552) ),
	WingOrRotorFold( new RPRunsignedInteger32BE(7584) ),
	FuselageFold( new RPRunsignedInteger32BE(7616) );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static HashMap<Long,ArticulatedPartsTypeEnum32> MAP = new HashMap<>();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ArticulatedPartsTypeEnum32( RPRunsignedInteger32BE value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public long getValue()
	{
		return this.value.getValue();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Data Element Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return value.getOctetBoundary();
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		value.encode( byteWrapper );
	}


	@Override
	public int getEncodedLength()
	{
		return value.getEncodedLength();
	}


	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return value.toByteArray();
	}


	@Override
	public ArticulatedPartsTypeEnum32 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public ArticulatedPartsTypeEnum32 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static ArticulatedPartsTypeEnum32 valueOf( long value )
	{
		// Because there are so many values we put them in a map. Looking it up will be
		// faster than iterating through every single one each time
		if( MAP.isEmpty() )
		{
			for( ArticulatedPartsTypeEnum32 temp : ArticulatedPartsTypeEnum32.values() )
				MAP.put( temp.getValue(), temp );
		}
		
		ArticulatedPartsTypeEnum32 temp = MAP.get( value );
		if( temp == null )
			//throw new UnsupportedException( "Unknown enumerator value: "+value+" (AntennaPatternEnum32)" );
			// let it pass through -- discarding it is too strict
			return Other;
		else
			return temp;
	}

}
