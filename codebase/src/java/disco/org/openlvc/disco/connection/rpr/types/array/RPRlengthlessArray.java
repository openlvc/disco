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
import java.util.Iterator;
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
public class RPRlengthlessArray<T extends DataElement> implements DataElement, Iterable<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DataElementFactory<T> factory;
	private List<T> items;
	private int boundary;
	private boolean decodeCalled;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	@SafeVarargs
	public RPRlengthlessArray( DataElementFactory<T> factory, T... values )
	{
		// perform checks
		if( factory == null )
			throw new IllegalArgumentException( "Cannot create HLA array type with null factory" );
		
		// initialize
		this.factory = factory;
		this.items = new ArrayList<>();
		this.boundary = -1;
		this.decodeCalled = false;

		// populate
		for( T value : values )
			items.add(value);
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
		if( boundary == -1 )
		{
			// Get the max boundary of any contained item
			int maxBoundary = 1; // Minimum of HLAoctet
			for( T item : items )
				maxBoundary = Math.max(item.getOctetBoundary(),maxBoundary);
			
			// If we don't have any items yet, create one to get a size
			if( items.isEmpty() )
				maxBoundary = Math.max(factory.createElement(0).getOctetBoundary(),maxBoundary);
			
			// cache it
			this.boundary = maxBoundary;
		}
		
		return boundary;
	}

	@Override
	public int getEncodedLength()
	{
		int counter = 0;
		for( DataElement item : this.items )
		{
			int boundary = item.getOctetBoundary();
			int padding = counter % boundary;
			// add padding for previous element
			if( padding != 0 )
				counter += boundary-padding;

			// add element length
			counter += item.getEncodedLength();
		}

		return counter;
	}

	@Override
	public void encode( ByteWrapper buffer ) throws EncoderException
	{
		// no length to store, so just pass through to each data element
		items.forEach( item -> item.encode(buffer) );
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		ByteWrapper buffer = new ByteWrapper( getEncodedLength() );
		encode( buffer );
		return buffer.array();
	}

	@Override
	public void decode( ByteWrapper buffer ) throws DecoderException
	{
		int count = 0;
		while( buffer.remaining() > 0 )
		{
			// If we have an element already in the array, decode over it, otherwise create
			T element = null;
			if( items.size() > count )
				element = items.get(count);
			else
				element = createElement();
			
			// Decode into the element
			element.decode( buffer );
			++count;
		}
		
		// Burn any elements after what we just decoded
		while( items.size() > count )
			items.remove(count);
		
		this.decodeCalled = true;
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		this.decode( new ByteWrapper(bytes) );
	}

	private final T createElement()
	{
		T temp = factory.createElement(0);
		items.add( temp );
		return temp;
	}	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<T> iterator()
	{
		return items.iterator();
	}

	public int size()
	{
		return items.size();
	}
	
	public void clear()
	{
		items.clear();
	}
	
	public void add( T value )
	{
		items.add( value );
	}
	
	public T get( int index )
	{
		return items.get( index );
	}

	/**
	 * Determine whether we're decoded anything into this object successfully or not. Useful for
	 * understanding when a record has been initialized by an incoming update.
	 * 
	 * @return True if either of the decode methods has been called on this record. False otherwise.
	 */
	public boolean isDecodeCalled()
	{
		return this.decodeCalled;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
