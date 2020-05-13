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

import java.util.ArrayList;
import java.util.List;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * The RPRlengthlessArray is a specialized array type used in the RPR FOM. From the GRIM:
 * 
 * <blockquote>
 *   <p>The RPRlengthlessArray encoding is intended for arrays with variable cardinality and shall
 *   consist of the encoding of each element in sequence. In contrast to the HLAvariableArray
 *   encoding, the number of elements in the array is not included in the encoding. The number
 *   of elements is determined by the total number of bytes in the array divided by the size of
 *   a single element, including its padding if necessary. If the elements can be of variable
 *   size, use of HLAvariableArray is recommended instead.</p>
 *   
 *   <p>The number of padding bytes after each element in the array is calculated in the same way
 *   as for HLAfixedArray as described in section 4.13.9.3 of the IEEE Std 1516.2TM-2010 OMT
 *   specification [16]. The size of the RPRlengthlessArray shall include any padding bytes.</p>
 *   
 *   <footer>pg 31/181, <cite>SISO-STD-001-2015; RPR FOM 2.0 GRIM</cite></footer>
 * </blockquote>
 * 
 * Here we represent it as a child of <code>HLAvariableArray<code>, just with different encoding.
 * Not sure if that is a good idea or not, but it keeps array access code consistent. 
 * 
 * @param <T>
 */
public class RPRlengthlessArray<T extends DataElement> extends HLAvariableArray<T>
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
	@SafeVarargs
	public RPRlengthlessArray( DataElementFactory<T> factory, T... values )
	{
		super( factory, values );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Data Element Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getEncodedLength()
	{
		return super.getEncodedLength()-4;
	}

	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		if( byteWrapper.remaining() < this.getEncodedLength() )
		{
			throw new EncoderException( "Insufficient buffer space remaining to encode this value ("+
			                            "required="+this.getEncodedLength()+
			                            ", available="+byteWrapper.remaining() );
		}
		
		// Write the elements
		for( T element : super.elements )
			element.encode( byteWrapper );
	}

	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		// Hopefully we've been initialized
		// If not, we could just decode as many as we have in the array currently and pray
		// that there is enough space in the wrapper, but let's just exception out for now.
		if( super.factory == null )
			throw new DecoderException( "No factory to create elements from" );
		
		List<T> newlist = new ArrayList<T>();

		// Grab as many as we can until we exhause the wrapper
		// Don't know what the boundary will be, so let's start with the default
		int boundary = this.factory.createElement(0).getOctetBoundary();
		
		while( byteWrapper.remaining() >= boundary )
		{
			T element = this.factory.createElement(0);
			element.decode( byteWrapper );
			newlist.add( element );
		}
		
		// store the new list
		this.elements = newlist;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
