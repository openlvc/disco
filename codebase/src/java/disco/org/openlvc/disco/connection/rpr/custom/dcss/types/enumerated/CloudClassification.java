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
package org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated;

import org.openlvc.disco.connection.rpr.types.basic.HLAoctet;
import org.openlvc.disco.connection.rpr.types.enumerated.ExtendedDataElement;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum CloudClassification implements ExtendedDataElement<CloudClassification>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	InvalidCloudClassification( new HLAoctet(0) ),
	NoCloud( new HLAoctet(1) ),
	Cirrus( new HLAoctet(2) ),
	Cirrocumulus( new HLAoctet(3) ),
	Cirrostratus( new HLAoctet(4) ),
	Altocumulus( new HLAoctet(5) ),
	Altostratus( new HLAoctet(6) ),
	Stratocumulus( new HLAoctet(7) ),
	Nimbostratus( new HLAoctet(8) ),
	Cumulus( new HLAoctet(9) ),
	Stratus( new HLAoctet(10) ),
	CumulusCongestus( new HLAoctet(11) ),
	Cumulonimbus( new HLAoctet(12) ),
	Unknown( new HLAoctet(13) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAoctet value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private CloudClassification( HLAoctet value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public byte getValue()
	{
		return this.value.getValue();
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
	public CloudClassification valueOf( ByteWrapper value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public CloudClassification valueOf( byte[] value ) throws DecoderException
	{
		HLAoctet temp = new HLAoctet();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static CloudClassification valueOf( byte value )
	{
		for( CloudClassification temp : CloudClassification.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (CloudClassification)" );
	}
}
