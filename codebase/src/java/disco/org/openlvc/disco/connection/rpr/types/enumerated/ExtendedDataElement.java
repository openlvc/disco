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

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;

/**
 * This is only intended to be used on java enumerations representing HLA enumerated types.
 * <p/>
 * 
 * We use Java enumerations to represent enumerated types. Each enumerator wraps a value, which
 * is a HLA data element. The way you read an incoming value from the HLA RTI is to call decode()
 * on a data element which replaces the value with the one from the incoming data.
 * <p/>
 * 
 * For enumerated data types this is bad, because it effectively redefines the enumerator value.
 * As such, we added the class {@link EnumHolder} to be a wrapper around an enumeration. The
 * problem is that we still need a standard way to turn incoming values from the HLA into their
 * enumerator value. Every enumeration class must implement this interface. It provides methods
 * for finding the right match for the values received from the HLA in a manner that will allow
 * us to switch between enumerators, rather than redefine their value.
 * <p/>
 * 
 * Further to this, we define default implementations of the standard HLA decode() methods that
 * throw exceptions, because these should <em>never</em> be used for java enumerations.
 * 
 * @param <T> Expected to be the java enumeration class
 */
public interface ExtendedDataElement<T> extends DataElement
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Turn the given byte wrapper into a type that we can then use to figure out which
	 * enumerator to return, and return that.
	 * 
	 * @param wrapper The wrapper to get the HLA value from
	 * @return The enumerator that represents that value
	 * @throws DecoderException If there is a problem decoding the data
	 */
	public T valueOf( ByteWrapper wrapper ) throws DecoderException;

	/**
	 * Turn the given byte array into a type that we can then use to figure out which
	 * enumerator to return, and return that.
	 * 
	 * @param bytes The byte array to get the HLA value from
	 * @return The enumerator that represents that value
	 * @throws DecoderException If there is a problem decoding the data
	 */
	public T valueOf( byte[] bytes ) throws DecoderException;
	
	
	/**
	 * This method WILL throw a DecoderException. It should never be called on Java enumerations
	 * in our HLA binding because it will redefine the value. 
	 * 
	 * @param wrapper Received value to decode
	 * @throws DecodeException Always
	 */
	@Override
	public default void decode( ByteWrapper wrapper ) throws DecoderException
	{
		throw new DecoderException( "This method should never be called on Java enumerations" );
	}

	/**
	 * This method WILL throw a DecoderException. It should never be called on Java enumerations
	 * in our HLA binding because it will redefine the value. 
	 * 
	 * @param bytes Received value to decode
	 * @throws DecodeException Always
	 */
	@Override
	public default void decode( byte[] bytes ) throws DecoderException
	{
		throw new DecoderException( "This method should never be called on Java enumerations" );
	}
	
}
