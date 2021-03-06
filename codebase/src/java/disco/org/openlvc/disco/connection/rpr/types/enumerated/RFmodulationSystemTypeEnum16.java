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

public enum RFmodulationSystemTypeEnum16 implements ExtendedDataElement<RFmodulationSystemTypeEnum16>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other(new RPRunsignedInteger16BE( 0 )),
	Generic(new RPRunsignedInteger16BE( 1 )),
	HQ(new RPRunsignedInteger16BE( 2 )),
	HQII(new RPRunsignedInteger16BE( 3 )),
	HQIIA(new RPRunsignedInteger16BE( 4 )),
	SINCGARS(new RPRunsignedInteger16BE( 5 )),
	CCTT_SINCGARS(new RPRunsignedInteger16BE( 6 )),
	EPLRS(new RPRunsignedInteger16BE( 7 )),
	JtidsMids(new RPRunsignedInteger16BE( 8 )),
	Link11(new RPRunsignedInteger16BE( 9 )),
	Link11b(new RPRunsignedInteger16BE( 10 )),
	LbandSatcom(new RPRunsignedInteger16BE( 11 )),
	EnhancedSincgars73(new RPRunsignedInteger16BE( 12 )),
	NavigationAid(new RPRunsignedInteger16BE( 13 ));

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger16BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private RFmodulationSystemTypeEnum16( RPRunsignedInteger16BE value )
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
	public RFmodulationSystemTypeEnum16 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger16BE temp = new RPRunsignedInteger16BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public RFmodulationSystemTypeEnum16 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger16BE temp = new RPRunsignedInteger16BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static RFmodulationSystemTypeEnum16 valueOf( int value )
	{
		for( RFmodulationSystemTypeEnum16 temp : RFmodulationSystemTypeEnum16.values() )
			if( temp.value.getValue() == value )
				return temp;

		// Don't be so strict
		// throw new UnsupportedException( "Unknown enumerator value: "+value+" (RFmodulationSystemTypeEnum16)" );
		return Other;
	}
}
