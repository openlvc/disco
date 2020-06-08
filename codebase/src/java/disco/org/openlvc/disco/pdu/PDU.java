/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.PduHeader;

public abstract class PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	/** Max size of a single PDU as defined by IEEE 1278.2 */
	public static final int MAX_SIZE = 8912;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected PduHeader header;
	protected long localTimestamp; // NOT the DIS timestamp, just a regular millis since epoch thang

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected PDU( PduHeader header )
	{
		this.header = header;
		this.localTimestamp = System.currentTimeMillis();
	}
	
	protected PDU( PduType type )
	{
		this.header = new PduHeader();
		this.header.setPduType( type );
		this.header.setProtocolFamily( type.getProtocolFamily() );
		this.localTimestamp = System.currentTimeMillis();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Each PDU has a specific type, as enumerated in {@link PduType}. 
	 */
	public final PduType getType()
	{
		if( header == null )
			throw new IllegalStateException( "The PDU does not contain a header" );
		
		return header.getPduType();
	}

	/**
	 * Return this given PDU as one of the subtypes. Will throw a `ClassCastException` if
	 * this PDU is not of the specified type.
	 */
	public <T extends PDU> T as( Class<T> type )
	{
		return type.cast( this );
	}

	public PduHeader getHeader()
	{
		return header;
	}
	
	public void setHeader( PduHeader header )
	{
		this.header = header;
	}
	
	public final short getExerciseId()
	{
		return header.getExerciseId();
	}
	
	public final void setExerciseId( short exerciseId )
	{
		header.setExerciseId( exerciseId );
	}
	
	public long getLocalTimestamp()
	{
		return localTimestamp;
	}
	
	public void setLocalTimestamp( long received )
	{
		this.localTimestamp = received;
	}
	
	public final void writePdu( DisOutputStream dos ) throws IOException
	{
		writeHeader( dos );
		to( dos );
	}

	public final void writeHeader( DisOutputStream dos ) throws IOException
	{
		this.header.to( dos, getContentLength() );
	}
	
	/**
	 * Reads new field values for the PDU Component from the provided DISInputStream replacing any 
	 * existing field values before the method was called.
	 * 
	 * If the method fails due to an IOException being raised, the object will be left in an
	 * undefined state.
	 * 
	 * @param dis The DISInputStream to read the new field values from
	 * 
	 * @throws IOException Thrown if there was an error reading from the provided DISInputStream
	 */
	public abstract void from( DisInputStream dis ) throws IOException;
	
	/**
	 * Writes the PDU Component's current field values to the provided DISOutputStream.
	 * 
	 * If the method fails due to an IOException being raised, the provided DISOutputStream will be 
	 * left in an undefined state.
	 * 
	 * @param dis The DISOutputStream to write field values to
	 * 
	 * @throws IOException Thrown if there was an error writing to the provided DISOutputStream
	 */
	public abstract void to( DisOutputStream dos ) throws IOException;

	/**
	 * Returns the length of this PDU's content section in bytes
	 * 
	 * @return An int value representing the length of this PDU's content section in bytes
	 */
	public abstract int getContentLength();

	/**
	 * Gets the full PDU length. Header + Content.
	 */
	public int getPduLength()
	{
		return header.getHeaderLength() + getContentLength();
	}

	/**
	 * Convert the given PDU into a `byte[]`.
	 */
	public byte[] toByteArray()
	{
		try
		{
    		ByteArrayOutputStream baos = new ByteArrayOutputStream( PDU.MAX_SIZE );
    		DisOutputStream dos = new DisOutputStream( baos );
    		this.writeHeader( dos );
    		this.to( dos );
    		return baos.toByteArray();
		}
		catch( IOException ioex )
		{
			throw new DiscoException( ioex.getMessage(), ioex );
		}
	}

	/**
	 * Return the Site ID for the system from which this PDU originates.
	 * <p/>
	 * This information isn't in the header, but some concept of it seems to be in each PDU
	 * in some form or another. In an ESPDU, it is in the entity ID. For a designator, it is
	 * in the originating entity. This source of this information will differ from PDU to PDU,
	 * but the intent is that it will return the Site ID of the originating site.
	 * the children.
	 * 
	 * @return Site ID from which which PDU came, or -1 if it can not be determined for the
	 *         PDU type.
	 */
	public abstract int getSiteId();
	
	/**
	 * Return the App ID for the system from which this PDU originates.
	 * <p/>
	 * This information isn't in the header, but some concept of it seems to be in each PDU
	 * in some form or another. In an ESPDU, it is in the entity ID. For a designator, it is
	 * in the originating entity. This source of this information will differ from PDU to PDU,
	 * but the intent is that it will return the App ID of the originating site.
	 * the children.
	 * 
	 * @return App ID from which which PDU came, or -1 if it can not be determined for the
	 *         PDU type.
	 */
	public abstract int getAppId();

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Convert the given bytes into a PDU. This just delegates to {@link PduFactory#create(byte[])}.
	 */
	public static PDU fromByteArray( byte[] bytes ) throws IOException
	{
		return PduFactory.create( bytes );
	}
}
