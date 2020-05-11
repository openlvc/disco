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

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * For fixed records especially, we can't just change the enum value that we point to, because
 * we add a reference to the enum member into the fixed record, so when we change the local
 * member later by re-assigning, that's good, but it doesn't change the value added to the fixed
 * record. We also can't update that value, because it's an enum, so it's immutable.
 * <p/>
 * For this to work properly, we need to reference an enum holder, which we can update (unlike
 * an enum directly) later on to point to the value we want, allowing that redirection.
 */
public class EnumHolder<T extends ExtendedDataElement<T>> implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private T value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EnumHolder( T value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public T getEnum()
	{
		return this.value;
	}
	
	public void setEnum( T value )
	{
		this.value = value;
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


	/**
	 * Replaces the value we are wrapping with one that matches whatever comes out of the
	 * given HLA byte wrapper. Calls through to {@link ExtendedDataElement#valueOf(ByteWrapper)}.
	 * 
	 * @param byteWrapper The value we need to find a match against the enum for
	 * @throws DecoderException If we have trouble processing the given byte wrapper
	 */
	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		value = value.valueOf( byteWrapper );
	}


	/**
	 * Replaces the value we are wrapping with one that matches whatever comes out of the
	 * array. This calls through to {@link ExtendedDataElement#valueOf(ByteWrapper)}.
	 * 
	 * @param bytes The value we need to find a match against the enum for
	 * @throws DecoderException If we have trouble processing the given byte array
	 */
	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		value = value.valueOf( bytes );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static <T extends ExtendedDataElement<T>> EnumHolder<T> from( T value )
	{
		return new EnumHolder<>( value );
	}
	
}
