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

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum EmitterFunctionEnum8 implements ExtendedDataElement<EmitterFunctionEnum8>
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
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private EmitterFunctionEnum8( int value )
	{
		this.value = new HLAoctet( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public byte getValue()
	{
		return this.value.getValue();
	}
	
	public short getUnsignedValue()
	{
		return this.value.getUnsignedValue();
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
	public EmitterFunctionEnum8 valueOf( ByteWrapper value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public EmitterFunctionEnum8 valueOf( byte[] value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static EmitterFunctionEnum8 valueOf( short value )
	{
		for( EmitterFunctionEnum8 temp : EmitterFunctionEnum8.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (EmitterFunctionEnum8)" );
	}
}
