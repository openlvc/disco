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
package org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated;

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import org.openlvc.disco.connection.rpr.types.enumerated.ExtendedDataElement;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum WeatherType implements ExtendedDataElement<WeatherType>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Atmospheric( new HLAoctet(1) ),
	Ground( new HLAoctet(2) ),
	CloudLayer( new HLAoctet(3) ),
	OceanSurface( new HLAoctet(4) ),
	OceanSubSurface( new HLAoctet(5) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private WeatherType( HLAoctet value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public byte getValue()
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
	public WeatherType valueOf( ByteWrapper value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public WeatherType valueOf( byte[] value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static WeatherType valueOf( byte value )
	{
		for( WeatherType temp : WeatherType.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (WeatherType)" );
	}
}
