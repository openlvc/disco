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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAvariableArray;

/**
 * This is a custom implementation of HLAvariableArray that treats decoding a little differently.
 * Have noticed that some sims are throwing out arrays that list their count seemingly as a byte
 * value, rather than a count. This can cause underflow issues (bytes exhausted while we try to
 * decode 8 times the number of values). 
 * <p/>
 * This array will _try_ to honor the count, but it will check after decoding each element whether
 * or not there is space in the source it is decoding from still. If there is not, it will then
 * just quietly return and you can be thankful that you got as much as you did.
 * <p/>
 * This could cause problems if it starts eating up the data of values that use the same buffer,
 * but it should only do this if the encoding of the count value is wrong in the first place, so
 * there really isn't much I can do about that :( 
 */
public class PermissiveHlaVariableArray<T extends DataElement> implements HLAvariableArray<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<T> items;
	private DataElementFactory<T> factory;
	private int boundary;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	@SuppressWarnings("unchecked")
	public PermissiveHlaVariableArray( DataElementFactory<T> factory, T... initialItems )
	{
		this.items = new ArrayList<>();
		this.factory = factory;
		this.boundary = -1;
		this.items.addAll( Arrays.asList(initialItems) );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void addElement( T element )
	{
		items.add( element );
		resetBoundary();
	}

	@Override
	public int size()
	{
		return items.size();
	}

	@Override
	public T get( int index )
	{
		return items.get( index );
	}

	@Override
	public Iterator<T> iterator()
	{
		resetBoundary();
		return items.iterator();
	}

	@Override
	public int getEncodedLength()
	{
		int length = 4;
		for( DataElement item : this.items )
		{
			// add padding
			while( length % item.getOctetBoundary() != 0 )
				length++;
			
			// add item length
			length += item.getEncodedLength();
		}

		return length;
	}

	@Override
	public int getOctetBoundary()
	{
		return calculateBoundary();
	}

	@Override
	public void resize( int size )
	{
		if( size < items.size() )
		{
			// we have too many elements; ditch some from the back
			while( size < items.size() )
				items.remove( items.size()-1 );
		}
		else if( size > items.size() )
		{
			// we don't have enough space; add some empty elements to push it out
			while( size > items.size() )
				items.add( factory.createElement(0) );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Encoding Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void encode( ByteWrapper wrapper ) throws EncoderException
	{
		try
		{
			wrapper.align( getOctetBoundary() );
			// write the size
			wrapper.putInt( items.size() );
			// write the items
			for( DataElement item : this.items )
				item.encode( wrapper );
		}
		catch( Exception e )
		{
			throw new EncoderException( "Failed in HLAvariableArray::encode >> "+e.getMessage(), e );
		}
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		ByteWrapper byteWrapper = new ByteWrapper( getEncodedLength() );
		encode( byteWrapper );
		return byteWrapper.array();
	}

	@Override
	public void decode( ByteWrapper wrapper ) throws DecoderException
	{
		try
		{
			wrapper.align( getOctetBoundary() );
			// read the size and verify that we have space
			int count = wrapper.getInt();
			wrapper.verify( count );
			
			// NOTE: Looking at some Data interactions from VRF I'm notice that the
			//       value encoded here appears to be in bytes, rather than element
			//       number. Either that, or something is very wrong.
			
			// Clear out the array entirely and start from scratch -- safest
			items.clear();

			// Decode each item we think we need, checking to see if we can keep
			// going or if we've exhausted the buffer after each spin
			int i = 0;
			while( i < count )
			{
				// decode this element
				T value = factory.createElement(0);
				value.decode( wrapper );
				items.add( value );
				
				// make sure we have space remaining
				if( wrapper.remaining() == 0 )
					return;
				
				// increment and move on
				i++;
			}
		}
		catch( Exception e )
		{
			throw new DecoderException( "Failed in HLAvariableArray::decode >> "+e.getMessage(), e );
		}
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		decode( new ByteWrapper(bytes) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal Helper Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private final void resetBoundary()
	{
		this.boundary = -1;
	}
	
	private final int calculateBoundary()
	{
		if( this.boundary == -1 )
		{
			// If there are no items, create one and calculate the boundary off that
			if( items.isEmpty() )
			{
				this.boundary = Math.max( 4, factory.createElement(0).getOctetBoundary() );
			}
			else
			{
				// If there are items, get the largest boundary from what they provide.
				// Minimum size is what we use to store the array size.
				int temp = 4;
				for( DataElement item : this.items )
					temp = Math.max( temp, item.getOctetBoundary() );
				
				this.boundary = temp;
			}
		}
		
		return this.boundary;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
