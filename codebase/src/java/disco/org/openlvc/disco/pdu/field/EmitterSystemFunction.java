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
package org.openlvc.disco.pdu.field;

import java.util.HashMap;
import java.util.Map;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.DisSizes;

/**
 * 2019-SISO-REF-010-v27 [UID 76]
 */
public enum EmitterSystemFunction
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other(0),
	MultiFunction(1),
	EarlyWarning(2),
	HeightFinder(3),
	FireControl(4),
	AcquisitionDetection(5),
	Tracker(6),
	GuidanceIllumination(7),
	FiringPoint_LaunchPointLocation(8),
	RangeOnly(9),
	RadarAltimeter(10),
	Imaging(11),
	MotionDetection(12),
	Navigation(13),
	Weather(14),
	Instrumentation(15),
	IdentificationClassification(16),
	AaaFireControl(17),
	AirSearchBomb(18),
	AirIntercept(19),
	Altimeter(20),
	AirMapping(21),
	AirTrafficControl(22),
	Beacon(23),
	BattlefieldSurveillance(24),
	GroundControlApproach(25),
	GroundControlIntercept(26),
	CoastalSurveillance(27),
	DecoyMimic(28),
	DataTransmission(29),
	EarthSurveillance(30),
	GunLayBeacon(31),
	GroundMapping(32),
	HarborSurveillance(33),
	//Deprecated(34),                    // IFF
	ILS(35),
	IonosphericSound(36),
	Interrogator(37),
	//Deprecated(38),                    // BarrageJamming
	//Deprecated(39),                    // ClickJamming
	//Deprecated(40),                    // DeceptiveJamming
	//Deprecated(41),                    // FrequencySweptJamming
	Jammer(42),
	//Deprecated(43),                    // NoiseJamming
	//Deprecated(44),                    // PulsedJamming
	//Deprecated(45),                    // RepeaterJamming
	//Deprecated(46),                    // SpotNoiseJamming
	MissileAcquisition(47),
	MissileDownlink(48),
	//Deprecated(49),                    // Meteorological
	Space(50),
	SurfaceSearch(51),
	ShellTracking(52),
	Television(56),
	Unknown(57),
	VideoRemoting(58),
	Experimental(59),
	MissileGuidance(60),
	MissileHoming(61),
	MissileTracking(62),
	//Deprecated(64),                    // JammingNoise
	//Deprecated(65),                    // JammingDeception
	//Deprecated(66),                    // Decoy
	NavigationDistanceMeasuringEquipment(71),
	TerrainFollowing(72),
	WeatherAvoidance(73),
	ProximityFuse(74),
	//Deprecated(75),                    // Instrumentation
	Radiosonde(76),
	Sonobuoy(77),
	BathythermalSensor(78),
	TowedCounterMeasure(79),
	DippingSonar(80),
	TowedAcousticSensor(81),
	WeaponNonLethal(96),
	WeaponLethal(97),
	TestEquipment(98),
	AcquisitionTrack(99),
	TrackGuidance(100),
	GuidanceIlluminationTrackAcquisition(101),
	SearchAcquisition(102);

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static Map<Short,EmitterSystemFunction> CACHE = new HashMap<>();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private EmitterSystemFunction( int value )
	{
		this.value = (short)value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}
	
	public static EmitterSystemFunction fromValue( short value )
	{
		// lazy-load
		if( CACHE == null )
		{
			CACHE = new HashMap<>();
			for( EmitterSystemFunction temp : EmitterSystemFunction.values() )
				CACHE.put( temp.value(), temp );
		}

		EmitterSystemFunction found = CACHE.get( value );
		if( found != null )
			return found;
		else if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" is not a valid EmitterSystemFunction value" );
		else
			return Other;
	}
}
