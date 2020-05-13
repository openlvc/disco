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

public enum AngleModulationTypeEnum16 implements ExtendedDataElement<AngleModulationTypeEnum16>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other                 ( new RPRunsignedInteger16BE(0) ),
	FrequencyModulation   ( new RPRunsignedInteger16BE(1) ),
	FrequencyShiftKeying  ( new RPRunsignedInteger16BE(2) ),
	PhaseModulation       ( new RPRunsignedInteger16BE(3) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger16BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private AngleModulationTypeEnum16( RPRunsignedInteger16BE value )
	{
		this.value = value;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public int getValue()
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
	public AngleModulationTypeEnum16 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger16BE temp = new RPRunsignedInteger16BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public AngleModulationTypeEnum16 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger16BE temp = new RPRunsignedInteger16BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static AngleModulationTypeEnum16 valueOf( int value )
	{
		for( AngleModulationTypeEnum16 temp : AngleModulationTypeEnum16.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		// Don't be so strict
		// throw new UnsupportedException( "Unknown enumerator value: "+value+" (AngleModulationTypeEnum16)" );
		return Other;
	}
}
