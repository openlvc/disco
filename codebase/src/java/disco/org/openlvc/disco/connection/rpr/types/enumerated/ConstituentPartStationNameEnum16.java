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

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum ConstituentPartStationNameEnum16 implements ExtendedDataElement<ConstituentPartStationNameEnum16>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( new RPRunsignedInteger16BE(0) ),
	AircraftWingstation( new RPRunsignedInteger16BE(1) ),
	ShipsForwardGunmountStarboard( new RPRunsignedInteger16BE(2) ),
	ShipsForwardGunmountPort( new RPRunsignedInteger16BE(3) ),
	ShipsForwardGunmountCenterline( new RPRunsignedInteger16BE(4) ),
	ShipsAftGunmountStarboard( new RPRunsignedInteger16BE(5) ),
	ShipsAftGunmountPort( new RPRunsignedInteger16BE(6) ),
	ShipsAftGunmountCenterline( new RPRunsignedInteger16BE(7) ),
	ForwardTorpedoTube( new RPRunsignedInteger16BE(8) ),
	AftTorpedoTube( new RPRunsignedInteger16BE(9) ),
	BombBay( new RPRunsignedInteger16BE(10) ),
	CargoBay( new RPRunsignedInteger16BE(11) ),
	TruckBed( new RPRunsignedInteger16BE(12) ),
	TrailerBed( new RPRunsignedInteger16BE(13) ),
	WellDeck( new RPRunsignedInteger16BE(14) ),
	OnStationRangeBearing( new RPRunsignedInteger16BE(15) ),
	OnStationXYZ( new RPRunsignedInteger16BE(16) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger16BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ConstituentPartStationNameEnum16( RPRunsignedInteger16BE value )
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
	public ConstituentPartStationNameEnum16 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger16BE temp = new RPRunsignedInteger16BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public ConstituentPartStationNameEnum16 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger16BE temp = new RPRunsignedInteger16BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static ConstituentPartStationNameEnum16 valueOf( long value )
	{
		for( ConstituentPartStationNameEnum16 temp : ConstituentPartStationNameEnum16.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (ConstituentPartStationNameEnum16)" );
	}
}
