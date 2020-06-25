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
package org.openlvc.disco.connection.rpr.types.array;

import java.nio.charset.StandardCharsets;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class RTIobjectId implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String value;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RTIobjectId()
	{
		this.value = "";
	}

	public RTIobjectId( String value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public String toString()
	{
		return this.value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public void setValue( String value )
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	@Override
	public boolean equals( Object object )
	{
		if( object instanceof RTIobjectId )
			return ((RTIobjectId)object).value.equals( this.value );
		else
			return false;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Methods /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public final int getOctetBoundary()
	{
		return 1;
	}

	@Override
	public final int getEncodedLength()
	{
		return this.value.length();
	}

	@Override
	public final void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		try
		{
			byteWrapper.put( toByteArray() );
		}
		catch( Exception e )
		{
			throw new EncoderException( e.getMessage(), e );
		}
	}

	@Override
	public final byte[] toByteArray() throws EncoderException
	{
		return this.value.getBytes( StandardCharsets.US_ASCII );
	}

	@Override
	public final void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		byte[] raw = new byte[byteWrapper.remaining()];
		byteWrapper.get( raw );
		this.value = new String( raw, StandardCharsets.US_ASCII );
	}

	@Override
	public final void decode( byte[] bytes ) throws DecoderException
	{
		this.value = new String( bytes, StandardCharsets.US_ASCII );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Factory /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	public static class Factory implements DataElementFactory<RTIobjectId>
	{
		public RTIobjectId createElement( int index )
		{
			return new RTIobjectId();
		}
	}
}
