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

public class HLAvariableArray<T extends DataElement> implements hla.rti1516e.encoding.HLAvariableArray<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected DataElementFactory<T> factory;
	protected List<T> elements;
	private int boundary;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	@SafeVarargs
	public HLAvariableArray( DataElementFactory<T> factory, T... provided )
	{
		this.factory = factory;
		this.elements = new ArrayList<T>( provided.length );
		
		for( T element : provided )
			this.elements.add( element );
		
		this.boundary = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Adds an element to this variable array.
	 * 
	 * @param dataElement element to add
	 */
	@Override
	public void addElement( T dataElement )
	{
		this.elements.add( dataElement );
		this.boundary = 0; // rearm the flag
	}

	/**
	 * Resize the variable array to the <code>newSize</code>. Uses the
	 * <code>DataElementFactory</code> if new elements needs to be added.
	 * 
	 * @param newSize the new size
	 */
	@Override
	public void resize( int newSize )
	{
		int existingSize = this.elements.size();
		if( newSize > existingSize )
		{
			// Up-sizing to a larger capacity, so make up the difference using elements created
			// from the provided factory
			int deltaSize = newSize - existingSize;
			for( int i = 0 ; i < deltaSize ; ++i )
				this.elements.add( this.factory.createElement(existingSize + i) );
		}
		else if ( newSize < existingSize )
		{
			// Down-sizing to a smaller capacity, so cull items from the end of the list 
			while( this.elements.size() > newSize )
				this.elements.remove( this.elements.size() - 1 );
		}
	}

	@Override
	public int size()
    {
	    return this.elements.size();
    }

	@Override
	public T get( int index )
    {
	    return this.elements.get( index );
    }

	@Override
	public Iterator<T> iterator()
    {
	    return this.elements.iterator();
    }

	// Off Spec
	public void clear()
	{
		this.elements.clear();
	}

    /////////////////////////////////////////////////////////////////////////////////////////
    /// DataElement Methods    //////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
	private void resetBoundary()
	{
		// we don't have any, so how do we know what size to use? create one and use that
		if( elements.isEmpty() )
		{
			if( factory == null )
				this.boundary = 0;
			else
				this.boundary = factory.createElement(0).getOctetBoundary();
		}
		
		// we do have elements, and they may be variant structs (sad panda) so we can't
		// just assume the size of the element type will do, so find the biggest and use
		// that
		int temp = 0;
		for( DataElement element : this.elements )
			temp = Math.max( temp, element.getOctetBoundary() );

		this.boundary = temp;
	}

	@Override
    public int getOctetBoundary()
    {
		if( this.boundary == 0 )
			resetBoundary();
		
		return boundary;
    }

	@Override
    public void encode( ByteWrapper byteWrapper ) throws EncoderException
    {
		if( byteWrapper.remaining() < this.getEncodedLength() )
			throw new EncoderException( "Insufficient space remaining in buffer to encode this value" );
		
		// Write the number of elements encoded
		byteWrapper.putInt( this.elements.size() );
		
		// Write the elements
		for( T element : this.elements )
			element.encode( byteWrapper );
    }

	@Override
    public int getEncodedLength()
    {
		int length = 0;
		for( DataElement dataElement : this.elements )
		{
			// Again - might be array of variant structs, so ... more calculating
			// Need to pad each element out to the element boundary
			int elementBoundary = dataElement.getOctetBoundary();
			int remainder = length % elementBoundary;
			if( remainder != 0 )
				length += elementBoundary - remainder;
			
			length += dataElement.getEncodedLength();
		}

		return length+4;
    }

	@Override
    public byte[] toByteArray() throws EncoderException
    {
		// Create a ByteWrapper to encode into
		int length = this.getEncodedLength();
		ByteWrapper byteWrapper = new ByteWrapper( length );
		this.encode( byteWrapper );
		
		// Return the underlying array
	    return byteWrapper.array();
    }

	@Override
    public void decode( ByteWrapper byteWrapper ) throws DecoderException
    {
		// Hopefully we've been initialized
		// If not, we could just decode as many as we have in the array currently and pray
		// that there is enough space in the wrapper, but let's just exception out for now.
		if( this.factory == null )
			throw new DecoderException( "No factory to create elements from" );
		
		List<T> newlist = new ArrayList<T>();

		// Read off the number of elements we expect
		int count = byteWrapper.getInt();
		for( int i = 0; i < count; i++ )
		{
			T element = this.factory.createElement(0);
			element.decode( byteWrapper );
			newlist.add( element );
		}
		
		// store the new list
		this.elements = newlist;
    }

	@Override
    public void decode( byte[] bytes ) throws DecoderException
    {
		// Wrap the byte array in a ByteWrapper to decode from
		ByteWrapper byteWrapper = new ByteWrapper( bytes );
		this.decode( byteWrapper );
    }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
