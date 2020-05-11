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

public class HLAfixedArray<T extends DataElement> implements hla.rti1516e.encoding.HLAfixedArray<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected List<T> elements;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	@SafeVarargs
	public HLAfixedArray( T... provided )
	{
		this.elements = new ArrayList<T>( provided.length );
		for( T element : provided )
			this.elements.add( element );
	}

	/**
	 * Create a new fixed array of the provided size and prepopulate it with
	 * the identified number of T instances (using the factory) 
	 */
	public HLAfixedArray( DataElementFactory<T> factory, int size )
	{
		this.elements = new ArrayList<T>( size );
		for( int i = 0; i < size; i++ )
			elements.add( factory.createElement(i) );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the number of elements in this fixed array.
	 * 
	 * @return the number of elements in this fixed array
	 */
	public int size()
	{
		return this.elements.size();
	}

	/**
	 * Returns the element at the specified <code>index</code>.
	 * 
	 * @param index index of element to get
	 * 
	 * @return the element at the specified <code>index</code>
	 */
	public T get( int index )
	{
		return this.elements.get( index );
	}

	/**
	 * Returns an iterator for the elements in this fixed array.
	 * 
	 * @return an iterator for the elements in this fixed array
	 */
	public Iterator<T> iterator()
	{
		return this.elements.iterator();
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// DataElement Methods //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		// Return the size of the largest element
		int maxSize = 1;
		
		for( T element : this.elements )
			maxSize = Math.max( maxSize, element.getEncodedLength() );
		
		return maxSize;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		int length = this.size();
		if( byteWrapper.remaining() < this.getEncodedLength() )
			throw new EncoderException( "Insufficient space remaining in buffer to encode this value" );
		
		// Write the array length
		byteWrapper.putInt( length );
		
		// Write the array contents
		for( T element : elements )
			element.encode( byteWrapper );
	}

	@Override
	public int getEncodedLength()
	{
		int size = 4;
		for( T element : this.elements )
			size += element.getEncodedLength();
		
		return size;
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		// Encode the array and then use the ByteWrapper's underlying byte[]
		int length = this.getEncodedLength();
		ByteWrapper byteWrapper = new ByteWrapper( length );
		this.encode( byteWrapper );
		
		return byteWrapper.array();
	}

	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		// Need at least 4 bytes to read the number of elements
		if( byteWrapper.remaining() < 4 )
			throw new DecoderException( "Insufficient space remaining in buffer to decode this value" );
		
		// Incoming size must match the size that the array was initialised with
		int length = byteWrapper.getInt();
		if( length != this.elements.size() )
		{
			throw new DecoderException( "Element count in decoded array differs. Expected [" + 
										this.elements.size() + 
										"] Received [" + 
										length + 
										"]" );
		}
		
		// Decode the elements
		for( T element : elements )
			element.decode( byteWrapper );
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		// Decode via a ByteWrapper
		ByteWrapper byteWrapper = new ByteWrapper( bytes );
		this.decode( byteWrapper );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
