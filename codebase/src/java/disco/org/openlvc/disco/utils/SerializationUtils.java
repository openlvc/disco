/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.disco.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.openlvc.disco.DiscoException;

public class SerializationUtils
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

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Object Serialization Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Convert the given object to a `byte[]` via a standard java ObjectOutputStream.
	 */
	public static byte[] objectToBytes( Object sourceObject ) throws IOException
	{
		ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( sourceObject );
		return baos.toByteArray();
	}

	/**
	 * Convert the given `byte[]` into an object via ObjectInputStream and return it.
	 * 
	 * @param buffer The buffer to read the bytes from
	 * @param offset The location in the buffer to start from
	 * @param length The max number of bytes to read from the buffer (boundary of the object)
	 * @return The contained object
	 * @throws IOException Couldn't read from the buffer
	 * @throws DiscoException Errors like found an object but we don't have the code for it!
	 */
	public static Object bytesToObject( byte[] buffer, int offset, int length )
		throws IOException, DiscoException
	{
		try( ByteArrayInputStream bais = new ByteArrayInputStream(buffer,offset,length);
		     ObjectInputStream ois = new ObjectInputStream(bais) )
		{
			return ois.readObject();
		}
		catch( ClassNotFoundException cnfe )
		{
			throw new DiscoException( "Error creating object from buffer - ClassNotFound: "+
			                          cnfe.getMessage(), cnfe );
		}
	}

	/**
	 * Convert the given `byte[]` into an object via ObjectInputStream and return it.
	 * We check to ensure it is of the expected type and then cast it prior to return
	 * 
	 * @param buffer The buffer to read the bytes from
	 * @param offset The location in the buffer to start from
	 * @param length The max number of bytes to read from the buffer (boundary of the object)
	 * @return The contained object
	 * @throws IOException Couldn't read from the buffer
	 * @throws DiscoException Creation errors like when we find an object but don't have the code
	 *                        for it, or when it isn't of the expected type.
	 */
	public static <T extends Object> T bytesToObject( byte[] buffer,
	                                                  int offset,
	                                                  int length,
	                                                  Class<T> expectedClass )
		throws IOException, DiscoException
	{
		Object value = bytesToObject( buffer, offset, length );
		if( expectedClass.isInstance(value) )
			return expectedClass.cast( value );
		else
			throw new DiscoException( "Incorrect type. Expected %s, found %s", expectedClass, value.getClass() );
	}

}
