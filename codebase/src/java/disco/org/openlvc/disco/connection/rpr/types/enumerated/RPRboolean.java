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
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class RPRboolean implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	
	public RPRboolean()
	{
		this.value = false;
	}

	public RPRboolean( boolean value )
	{
		this.value = value;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public boolean getValue()
	{
		return this.value;
	}
	
	public void setValue( boolean value )
	{
		this.value = value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Data Element Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private final HLAoctet getHlaVersion()
	{
		return new HLAoctet( value ? 1 : 0 );
	}

	@Override
	public int getOctetBoundary()
	{
		return 1;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		getHlaVersion().encode( byteWrapper );
	}


	@Override
	public int getEncodedLength()
	{
		return 1;
	}


	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return getHlaVersion().toByteArray();
	}

	@Override
	public void decode( ByteWrapper wrapper ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( wrapper );
		this.value = temp.getUnsignedValue() == 1;
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( bytes );
		this.value = temp.getUnsignedValue() == 1;
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
