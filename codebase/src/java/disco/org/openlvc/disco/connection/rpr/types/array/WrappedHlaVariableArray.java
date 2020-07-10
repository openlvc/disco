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

import java.util.Iterator;

import org.openlvc.disco.DiscoException;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.exceptions.RTIinternalError;

public class WrappedHlaVariableArray<T extends DataElement> implements HLAvariableArray<T>, Iterable<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAvariableArray<T> internal;
	private boolean decodeCalled;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	@SuppressWarnings("unchecked")
	public WrappedHlaVariableArray( DataElementFactory<T> factory, T... values )
	{
		this.decodeCalled = true;

		try
		{
			this.internal = RtiFactoryFactory.getRtiFactory()
			                                 .getEncoderFactory()
			                                 .createHLAvariableArray( factory, values );
		}
		catch( RTIinternalError e )
		{
			throw new DiscoException( "Could not create HLAvariableArray: "+e.getMessage(), e );
		}
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public void addElement( T dataElement )
	{
		this.internal.addElement( dataElement );
	}
	
	@Override
	public int size()
	{
		return internal.size();
	}
	
	@Override
	public T get( int index )
	{
		return internal.get( index );
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return internal.iterator();
	}
	
	@Override
	public void resize( int newSize )
	{
		internal.resize( newSize );
	}
	
	// DataElement
	public int getOctetBoundary()			  { return internal.getOctetBoundary(); }
	public int getEncodedLength()			  { return internal.getEncodedLength(); }
	public void encode( ByteWrapper wrapper )
		throws EncoderException				  { internal.encode(wrapper); }
	public byte[] toByteArray()
		throws EncoderException				  { return internal.toByteArray(); }
	public void decode( ByteWrapper wrapper )
		throws DecoderException				  { internal.decode(wrapper); decodeCalled = true; }
	public void decode( byte[] bytes )
		throws DecoderException               { internal.decode(bytes); decodeCalled = true; }

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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
