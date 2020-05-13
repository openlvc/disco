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

import org.openlvc.disco.UnsupportedException;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum ArticulatedTypeMetricEnum32 implements ExtendedDataElement<ArticulatedTypeMetricEnum32>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Position( new RPRunsignedInteger32BE(1) ),
	PositionRate( new RPRunsignedInteger32BE(2) ),
	Extension( new RPRunsignedInteger32BE(3) ),
	ExtensionRate( new RPRunsignedInteger32BE(4) ),
	X( new RPRunsignedInteger32BE(5) ),
	XRate( new RPRunsignedInteger32BE(6) ),
	Y( new RPRunsignedInteger32BE(7) ),
	YRate( new RPRunsignedInteger32BE(8) ),
	Z( new RPRunsignedInteger32BE(9) ),
	ZRate( new RPRunsignedInteger32BE(10) ),
	Azimuth( new RPRunsignedInteger32BE(11) ),
	AzimuthRate( new RPRunsignedInteger32BE(12) ),
	Elevation( new RPRunsignedInteger32BE(13) ),
	ElevationRate( new RPRunsignedInteger32BE(14) ),
	Rotation( new RPRunsignedInteger32BE(15) ),
	RotationRate( new RPRunsignedInteger32BE(16) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ArticulatedTypeMetricEnum32( RPRunsignedInteger32BE value )
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
	public ArticulatedTypeMetricEnum32 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public ArticulatedTypeMetricEnum32 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static ArticulatedTypeMetricEnum32 valueOf( long value )
	{
		for( ArticulatedTypeMetricEnum32 temp : ArticulatedTypeMetricEnum32.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new UnsupportedException( "Unknown enumerator value: "+value+" (ArticulatedTypeMetricEnum32)" );
	}
}
