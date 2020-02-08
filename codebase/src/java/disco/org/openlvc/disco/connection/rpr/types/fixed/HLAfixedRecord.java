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
package org.openlvc.disco.connection.rpr.types.fixed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAfixedRecord implements hla.rti1516e.encoding.HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<DataElement> elements;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAfixedRecord()
	{
		this.elements = new ArrayList<DataElement>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Adds an element to this fixed record.
	 * 
	 * @param dataElement element to add
	 */
	@Override
	public void add( DataElement dataElement )
	{
		if( dataElement != null )
			this.elements.add( dataElement );
	}

	/**
	 * Returns the number of elements in this fixed record.
	 * 
	 * @return the number of elements in this fixed record
	 */
	@Override
	public int size()
	{
		return this.elements.size();
	}

	/**
	 * Returns element at the specified index.
	 * 
	 * @param index index of element to get
	 * 
	 * @return the element at the specified <code>index</code>
	 */
	@Override
	public DataElement get( int index )
	{
		return this.elements.get( index );
	}

	/**
	 * Returns an iterator for the elements in this fixed record.
	 * 
	 * @return an iterator for the elements in this fixed record.
	 */
	@Override
	public Iterator<DataElement> iterator()
	{
		return this.elements.iterator();
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Methods /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		// Return the size of the largest element
		int maxSize = 1;
		
		for( DataElement element : this.elements )
			maxSize = Math.max( maxSize, element.getEncodedLength() );
		
		return maxSize;
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		if( this.elements.size() == 0 )
			throw new EncoderException( "Cannot encode an empty fixed record!" );
		
		for( DataElement element : this.elements )
			element.encode( byteWrapper );
	}

	@Override
	public int getEncodedLength()
	{
		int size = 0;
		
		for( DataElement element : this.elements )
			size += element.getEncodedLength();
		
		return size;
	}

	@Override
	public byte[] toByteArray() throws EncoderException
	{
		// Encode into a byte wrapper
		int length = this.getEncodedLength();
		ByteWrapper byteWrapper = new ByteWrapper( length );
		this.encode( byteWrapper );
		
		// Return the underlying array
		return byteWrapper.array();
	}

	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		if( this.elements.size() == 0 )
			throw new EncoderException( "Cannot decode into an empty fixed record!" );
		
		for( DataElement element : this.elements )
			element.decode( byteWrapper );
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		// Wrap in a byte wrapper and decode
		ByteWrapper byteWrapper = new ByteWrapper( bytes );
		this.decode( byteWrapper );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
