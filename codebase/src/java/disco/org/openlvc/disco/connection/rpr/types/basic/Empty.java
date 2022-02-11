/*
 *   Copyright 2022 Open LVC Project.
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
package org.openlvc.disco.connection.rpr.types.basic;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * A dummy class for when a no-value object is needed, e.g. in an HLAVariantStruct
 * with an empty alternative
 */
public class Empty implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Empty()
	{
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Methods /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return 0;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		// No-op
	}

	@Override
	public int getEncodedLength()
	{
		return 0;
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return new byte[0];
	}

	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		// No-op
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		// No-op
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
