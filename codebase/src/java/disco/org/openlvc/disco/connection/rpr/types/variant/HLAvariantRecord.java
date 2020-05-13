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
package org.openlvc.disco.connection.rpr.types.variant;

import java.util.HashMap;
import java.util.Map;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.ExtendedDataElement;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAvariantRecord<T extends ExtendedDataElement<T>> implements hla.rti1516e.encoding.HLAvariantRecord<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected Map<T,DataElement> associations;
	protected T activeDiscriminant;

	// boundary caches
	private int recordBoundary;
	private int valueBoundary;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public HLAvariantRecord()
	{
		this.associations = new HashMap<>();
		this.activeDiscriminant = null;
		
		// boundary caches
		this.recordBoundary = 0;
		this.valueBoundary = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// VariantRecord Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Associates the data element for a specified discriminant.
	 * 
	 * @param discriminant discriminant to associate data element with
	 * @param dataElement  data element to associate the discriminant with
	 */
	@Override
	public void setVariant( T discriminant, DataElement dataElement )
	{
		if( dataElement instanceof Enum )
			throw new DiscoException( "Data Element cannot be an Java enum, must be in EnumHolder" );
		
		if( discriminant == null )
			throw new IllegalArgumentException( "Discriminant cannot be null" );
		else if( dataElement == null )
			throw new IllegalArgumentException( "Data Element cannot be null" );
		
		this.associations.put( discriminant, dataElement );
		// reset our bounds caches
		this.recordBoundary = 0;
		this.valueBoundary = 0;
		
		// if there is no active discriminant, make this be it
		if( this.activeDiscriminant == null )
			this.activeDiscriminant = discriminant;
	}

	/**
	 * Sets the active discriminant.
	 *
	 * @param discriminant active discriminant
	 */
	@Override
	public void setDiscriminant( T discriminant )
	{
		if( this.associations.containsKey(discriminant) == false )
			throw new IllegalArgumentException( "Discriminant is not registered with this record: "+discriminant );
		
		this.activeDiscriminant = discriminant;
	}

	/**
	 * Returns the active discriminant.
	 *
	 * @return the active discriminant
	 */
	@Override
	public T getDiscriminant()
	{
		return this.activeDiscriminant;
	}

	/**
	 * Returns element associated with the active discriminant.
	 *
	 * @return value
	 */
	@Override
	public DataElement getValue()
	{
		return this.associations.get( this.activeDiscriminant );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DataElement Methods   //////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
//		byteWrapper.align( getOctetBoundary() );
		EnumHolder<T> temp = new EnumHolder<>( this.activeDiscriminant );
		temp.decode( byteWrapper );
		this.activeDiscriminant = temp.getEnum();
		byteWrapper.align( getValueBoundary() );

		if( associations.containsKey(activeDiscriminant) == false )
			throw new DecoderException( "Unknown discriminant: "+activeDiscriminant );
		
		DataElement dataElement = getValue();
		if( dataElement != null )
			dataElement.decode( byteWrapper );
	}

	@Override
	public void decode( byte[] bytes ) throws DecoderException
	{
		// Wrap in a byte wrapper and decode
		ByteWrapper byteWrapper = new ByteWrapper( bytes );
		this.decode( byteWrapper );
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
//		byteWrapper.align( getOctetBoundary() );  // getEncodedLength() does not take this into account
//		throw new RuntimeException( "Starting here -- need to figure out getEncodedLength to incorporate alignment" );
		this.activeDiscriminant.encode( byteWrapper );
		
		byteWrapper.align( getValueBoundary() );
		DataElement dataElement = getValue();
		if( dataElement != null )
			dataElement.encode( byteWrapper );
	}

	@Override
	public int getEncodedLength()
	{
		int discriminantLength = activeDiscriminant.getEncodedLength();
		int valueBoundary = getValueBoundary();
		// push the discrimiant length up to a multiple of the value length
		while( discriminantLength % valueBoundary != 0 )
			discriminantLength++;
		
		DataElement dataElement = getValue();
		if( dataElement != null )
			discriminantLength += dataElement.getEncodedLength();
		
		return discriminantLength;
	}

	@Override
	public int getOctetBoundary()
	{
		if( this.recordBoundary == 0 )
			this.recordBoundary = Math.max( activeDiscriminant.getOctetBoundary(), getValueBoundary() );
		
		return this.recordBoundary;
	}

	private int getValueBoundary()
	{
		if( this.valueBoundary == 0 )
			for( DataElement temp : this.associations.values() )
				valueBoundary = Math.max( valueBoundary, temp.getOctetBoundary() );
		
		return this.valueBoundary;
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

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
