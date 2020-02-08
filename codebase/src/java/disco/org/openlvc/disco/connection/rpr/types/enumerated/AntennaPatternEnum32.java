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

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum AntennaPatternEnum32 implements DataElement
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	OmniDirectional( new RPRunsignedInteger32BE(0) ),
	Beam( new RPRunsignedInteger32BE(1) ),
	SphericalHarmonic( new RPRunsignedInteger32BE(1) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private AntennaPatternEnum32( RPRunsignedInteger32BE value )
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
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		value.decode( byteWrapper );
	}


	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		value.decode( bytes );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static AntennaPatternEnum32 valueOf( long value )
	{
		for( AntennaPatternEnum32 temp : AntennaPatternEnum32.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (AntennaPatternEnum32)" );
	}
}
