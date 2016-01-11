/*
 *   Copyright 2015 Open LVC Project.
 *
 *   This file is part of Open LVC Disco.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"),
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

import java.util.HashSet;
import java.util.Set;

import org.openlvc.disco.configuration.DiscoConfiguration;

public enum Fuse
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0000 ),
	Intelligent_influence( 0010 ),
	Sensor( 0020 ),
	Selfdestruct( 0030 ),
	Ultra_quick( 0040 ),
	Body( 0050 ),
	Deep_intrusion( 0060 ),
	Multifunction( 0100 ),
	Point_detonation( 0200 ),
	Base_detonation( 0300 ),
	Contact( 1000 ),
	Contact_instant( 1100 ),
	Contact_delayed( 1200 ),
	Contact_electronic( 1300 ),
	Contact_graze( 1400 ),
	Contact_crush( 1500 ),
	Contact_hydrostatic( 1600 ),
	Contact_mechanical( 1700 ),
	Contact_chemical( 1800 ),
	Contact_piezoelectric( 1900 ),
	Contact_point_initiating( 1910 ),
	Contact_point_initiating_base_detonating( 1920 ),
	Contact_base_detonating( 1930 ),
	Contact_ballistic_cap_and_base( 1940 ),
	Contact_base( 1950 ),
	Contact_nose( 1960 ),
	Contact_fitted_in_standoff_probe( 1970 ),
	Contact_nonaligned( 1980 ),
	Timed( 2000 ),
	Timed_programmable( 2100 ),
	Timed_burnout( 2200 ),
	Timed_pyrotechnic( 2300 ),
	Timed_electronic( 2400 ),
	Timed_base_delay( 2500 ),
	Timed_reinforced_nose_impact_delay( 2600 ),
	Timed_short_delay_impact( 2700 ),
	Timed_nose_mounted_variable_delay( 2800 ),
	Timed_long_delay_side( 2900 ),
	Timed_selectable_delay( 2910 ),
	Timed_impact( 2920 ),
	Timed_sequence( 2930 ),
	Proximity( 3000 ),
	Proximity_active_laser( 3100 ),
	Proximity_magnetic( 3200 ),
	Proximity_active_radar( 3300 ),
	Proximity_radio_frequency( 3400 ),
	Proximity_programmable( 3500 ),
	Proximity_programmable_prefragmented( 3600 ),
	Proximity_infrared( 3700 ),
	Command( 4000 ),
	Command_electronic_remotely_set( 4100 ),
	Altitude( 5000 ),
	Altitude_radio_altimeter( 5100 ),
	Altitude_air_burst( 5200 ),
	Depth( 6000 ),
	Acoustic( 7000 ),
	Pressure( 8000 ),
	Pressure_delay( 8010 ),
	Inert( 8100 ),
	Dummy( 8110 ),
	Practice( 8120 ),
	Plug_representing( 8130 ),
	Training( 8150 ),
	Pyrotechnic( 9000 ),
	Pyrotechnic_delay( 9010 ),
	Electrooptical( 9100 ),
	Electromechanical( 9110 ),
	Electromechanical_nose( 9120 ),
	Strikerless( 9200 ),
	Strikerless_nose_impact( 9210 ),
	Strikerless_compression_ignition( 9220 ),
	Sompression_ignition( 9300 ),
	Sompression_ignition_strikerless_nose_impact( 9310 ),
	Percussion( 9400 ),
	Percussion_instantaneous( 9410 ),
	Electronic( 9500 ),
	Electronic_internally_mounted( 9510 ),
	Electronic_range_setting( 9520 ),
	Electronic_programmed( 9530 ),
	Mechanical( 9600 ),
	Mechanical_nose( 9610 ),
	Mechanical_tail( 9620 );

	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// values we don't have - cache for performance
	private static Set<Integer> MISSING = new HashSet<>();

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private Fuse( int value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int value()
	{
		return this.value;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Fuse fromValue( int value )
	{
		if( MISSING.contains(value) == false )
		{
			for( Fuse fuse : Fuse.values() )
				if( fuse.value == value )
					return fuse;
			
			MISSING.add( value );
		}

		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" not a valid Fuse number" );
		else
			return Other;
	}
}
