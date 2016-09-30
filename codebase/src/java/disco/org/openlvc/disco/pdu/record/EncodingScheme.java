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
package org.openlvc.disco.pdu.record;

import java.io.EOFException;
import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.EncodingClass;
import org.openlvc.disco.pdu.field.EncodingType;

/**
 * This field shall specify the encoding used in the Data field of this PDU. 
 * The Encoding Scheme shall be composed of a 2-bit field specifying the 
 * encoding class and a 14-bit field specifying either the encoding type, or the 
 * number of TDL messages contained in this Signal PDU.<br/>
 * <br/>
 * The fourteen least significant bits of the encoding scheme shall represent 
 * encoding type when the encoding class is encoded audio. The valid values of 
 * encoding type are enumerated in Section 9 of EBV-DOC.<br/>
 * <br/>
 * The fourteen least significant bits of the encoding scheme shall be zero when 
 * the encoding class is not encoded audio and the TDL Type (see 5.4.8.2(e)) is 
 * zero.<br/>
 * <br/>
 * Otherwise, the fourteen least significant bits of the encoding scheme shall 
 * represent the number of tactical data link messages contained in the data 
 * section of the Signal PDU.
 * 
 * @see "IEEE Std 1278.1-1995 section 5.4.8.2(e)"
 * @see "Section 9 of EBV-DOC"
 */
public class EncodingScheme implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EncodingClass encodingClass;
	private EncodingType encodingType;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EncodingScheme()
	{
		this( EncodingClass.EncodedVoice, EncodingType.Mulaw8 );
	}
	
	public EncodingScheme( EncodingClass encodingClass, EncodingType encodingType )
	{
		this.encodingClass = encodingClass;
		this.encodingType = encodingType;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;

		if( other instanceof EncodingScheme )
		{
			EncodingScheme asEncodingScheme = (EncodingScheme)other;
			if( asEncodingScheme.encodingClass == this.encodingClass &&
				asEncodingScheme.encodingType == this.encodingType )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public EncodingScheme clone()
	{
		return new EncodingScheme( encodingClass, encodingType );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		int ch1 = dis.read();
		int ch2 = dis.read();
		
		if ( (ch1|ch2) < 0 )
			throw new EOFException();
		
		// Encoding class is held in the top two bits, encoding type is the rest
		byte encodingClassValue = (byte)((ch1 & 0xC0) >> 6);
		short encodingTypeValue = (short)(((ch1 & 0x3F) << 8) + ch2);
		this.encodingClass = EncodingClass.fromValue( encodingClassValue );
		this.encodingType = EncodingType.fromValue( encodingTypeValue );
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		// Encoding class is held in the top two bits, encoding type is the rest
		byte encodingClassValue = encodingClass.value();
		short encodingTypeValue = encodingType.value();
	    int ch1 = ((encodingClassValue & 0x03) << 6) | ((encodingTypeValue & 0x3F00) >> 8);
	    int ch2 = encodingTypeValue & 0xFF;
	    
	    dos.write( ch1 );
	    dos.write( ch2 );
    }
	
	@Override
    public final int getByteLength()
	{
		// Two bytes
		return 2; //DisSizes.UI8_SIZE * 2;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EncodingClass getEncodingClass()
	{
		return encodingClass;
	}
	
	public void setEncodingClass( EncodingClass encodingClass )
	{
		this.encodingClass = encodingClass;
	}
	
	public EncodingType getEncodingType()
	{
		return encodingType;
	}
	
	public void setEncodingType( EncodingType encodingType )
	{
		this.encodingType = encodingType;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
