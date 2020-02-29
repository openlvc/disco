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

import org.openlvc.disco.DiscoException;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * <p>This class represents the parent type for all HLA enumerated data types. Due to the way that
 * the encoding helpers are specified, it is not simple to implement them as a Java enum. We must
 * be able to call the DataElement method decode() on them, which has the potential to change their
 * value, effectively losing the immutable nature of Java enumerations.</p>
 * 
 * <p>To get around this, we have implemented various protective measures into this base type.
 * These include:
 * <ul>
 *   <li>Each <code>HLAenum</code> has an <code>isConstant</code> property. This should be declared
 *       as true for all instances that are defined as statics in subclasses to represent the
 *       enumerated values. (should be done via a private constructor in that type).</li>
 *   <li>When decode() is called on an instant that is constant, an exception is thrown.</li>
 *   <li>Child classes do not expose the constructor that sets the isConstant property, meaning
 *       only the defining type can call it. Public constructors for child types should always set
 *       isConstant to false.</li>
 *   <li>A copy() method must be defined by all child types, allowing new enum instances that can
 *       have decode() called on them to be created from the static types.</li>
 * </p>
 * 
 * <p>Child classes should follow these rules.</p>
 * 
 * @param <T> The underlying datatype that forms the value for the enumerated named.
 */
public abstract class HLAenum<T extends DataElement> implements DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected T value;
	private boolean isConstant;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected HLAenum( T value, boolean isConstant )
	{
		this.value = value;
		this.isConstant = isConstant;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * @return This method will clone the data element that is used to store the enum value.
	 *         This allows us to safely replace a value on decode rather than edit the original,
	 *         which in turn prevents us modifying the value of the enumerated types.
	 */
	protected abstract T copyValue();

	/**
	 * Create a copy of this enum and return it. This allows us to copy a constant value of
	 * the enum type without maintaining a reference to the original (and avoiding us accidentally
	 * using {@link #decode(byte[])} to erase its known value.
	 *  
	 * @param <X> The subclass of {@link HLAenum} that the type we are copying is. Should be the
	 *            type of the class that this method is implemented in.
	 * @return    A copy of the value this is called on that can be freely changed.
	 */
	public abstract <X extends HLAenum<T>> X copy();
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public T getHlaValue()
	{
		return this.value;
	}
	
	public void setHlaValue( T value ) throws DiscoException
	{
		if( isConstant )
		{
			throw new DiscoException( "Cannot change the value of a constant. "+
			                          "Create a copy of constant using copy() first" );
		}
		else
		{
			this.value = value;
		}
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


	@Override
	public final void decode( ByteWrapper byteWrapper ) throws DecoderException
	{
		if( isConstant )
		{
			throw new DecoderException( "Cannot change the value of a constant. "+
			                            "Create a copy of constant using copy() first" );
		}

		T newValue = copyValue();
		newValue.decode( byteWrapper );
		this.value = newValue;
	}


	@Override
	public final void decode( byte[] bytes ) throws DecoderException
	{
		if( isConstant )
		{
			throw new DecoderException( "Cannot change the value of a constant. "+
			                            "Create a copy of constant using copy() first" );
		}

		T newValue = copyValue();
		newValue.decode( bytes );
		this.value = newValue;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
