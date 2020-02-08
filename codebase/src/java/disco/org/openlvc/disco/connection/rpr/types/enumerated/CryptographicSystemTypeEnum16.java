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
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum CryptographicSystemTypeEnum16 implements DataElement
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( new RPRunsignedInteger16BE(0) ),
	KY_28( new RPRunsignedInteger16BE(1) ),
	KY_58( new RPRunsignedInteger16BE(2) ),
	NarrowSpectrumSecureVoice_NSVE( new RPRunsignedInteger16BE(3) ),
	WideSpectrumSecureVoice_WSVE( new RPRunsignedInteger16BE(4) ),
	SINCGARS_ICOM( new RPRunsignedInteger16BE(5) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger16BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private CryptographicSystemTypeEnum16( RPRunsignedInteger16BE value )
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
	public static CryptographicSystemTypeEnum16 valueOf( int value )
	{
		for( CryptographicSystemTypeEnum16 temp : CryptographicSystemTypeEnum16.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (CryptographicSystemTypeEnum16)" );
	}
}
