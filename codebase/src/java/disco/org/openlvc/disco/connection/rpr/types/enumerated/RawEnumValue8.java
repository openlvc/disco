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

/**
 * Unlike proper enumerated types which check the value against a set of fixed, logical values,
 * this class just wraps the raw number that is provided to identify the enum. These types do
 * <b>NOT</b> need to be held in an {@link EnumHolder} because they are safely mutable.
 */
public class RawEnumValue8 implements ExtendedDataElement<RawEnumValue8>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RawEnumValue8()
	{
		this.value = new HLAoctet();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

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

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// ExtendedDataElement Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public RawEnumValue8 valueOf( ByteWrapper wrapper ) throws DecoderException
	{
		value.decode( wrapper );
		return this;
	}

	@Override
	public RawEnumValue8 valueOf( byte[] bytes ) throws DecoderException
	{
		value.decode( bytes );
		return this;
	}
	
	
	@Override
	public void decode( ByteWrapper wrapper ) throws DecoderException
	{
		value.decode( wrapper );
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		value.decode( bytes );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public short getValue()
	{
		return this.value.getValue();
	}
	
	public void setUnsignedValue( short value )
	{
		this.value.setUnsignedValue( value );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
