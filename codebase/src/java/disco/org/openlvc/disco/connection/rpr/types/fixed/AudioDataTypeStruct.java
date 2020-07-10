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

import org.openlvc.disco.connection.rpr.types.array.SignalDataLengthlessArray1Plus;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger16BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger64BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EncodingTypeEnum32;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;

public class AudioDataTypeStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger64BE streamTag;
	private EnumHolder<EncodingTypeEnum32> encodingType;
	private RPRunsignedInteger32BE sampleRate;
	private RPRunsignedInteger16BE dataLength;
	private RPRunsignedInteger32BE sampleCount;
	private SignalDataLengthlessArray1Plus data;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AudioDataTypeStruct()
	{
		this.streamTag = new RPRunsignedInteger64BE();
		this.encodingType = new EnumHolder<>( EncodingTypeEnum32.Unknown );
		this.sampleRate = new RPRunsignedInteger32BE();
		this.dataLength = new RPRunsignedInteger16BE();
		this.sampleCount = new RPRunsignedInteger32BE();
		this.data = new SignalDataLengthlessArray1Plus();
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.streamTag );
		super.add( this.encodingType );
		super.add( this.sampleRate );
		super.add( this.dataLength );
		super.add( this.sampleCount );
		super.add( this.data );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getStreamTag()
	{
		return streamTag.getLongValue();
	}
	
	public void setStreamTag( long value )
	{
		streamTag.setValue( value );
	}

	public long getEncodingType()
	{
		return encodingType.getEnum().getValue();
	}

	public void setEncodingType( long value )
	{
		this.encodingType.setEnum( EncodingTypeEnum32.valueOf(value) );
	}

	public long getSampleRate()
	{
		return sampleRate.getValue();
	}

	public void setSampleRate( long sampleRate )
	{
		this.sampleRate.setValue( sampleRate );
	}

	public int getDataLength()
	{
		return dataLength.getValue();
	}

	public void setDataLength( int dataLength )
	{
		this.dataLength.setValue( dataLength );
	}

	public long getSampleCount()
	{
		return sampleCount.getValue();
	}

	public void setSampleCount( long sampleCount )
	{
		this.sampleCount.setValue( sampleCount );
	}

	public byte[] getData()
	{
		return data.getDisValue();
	}

	public void setData( byte[] data )
	{
		this.data.setValue( data );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
