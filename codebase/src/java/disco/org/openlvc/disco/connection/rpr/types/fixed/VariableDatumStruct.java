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

import java.nio.ByteBuffer;

import org.openlvc.disco.connection.rpr.types.array.UnsignedInteger64Array1Plus;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger64BE;
import org.openlvc.disco.pdu.record.VariableDatum;

public class VariableDatumStruct extends DiscoHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE datumId;
	private RPRunsignedInteger32BE datumLength;
	private UnsignedInteger64Array1Plus datumValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public VariableDatumStruct()
	{
		super();
		this.datumId     = new RPRunsignedInteger32BE();
		this.datumLength = new RPRunsignedInteger32BE();
		this.datumValue  = new UnsignedInteger64Array1Plus();
		
		// Add elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.datumId );
		super.add( this.datumLength );
		super.add( this.datumValue );
	}

	public VariableDatumStruct( VariableDatum disRecord )
	{
		this();
		this.setValue( disRecord );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue( VariableDatum record )
	{
		this.datumId.setValue( record.getDatumId() );
		this.datumLength.setValue( record.getDatumLengthInBits() );
		
		ByteBuffer buffer = ByteBuffer.wrap( record.getDatumValueWithPadding() );
		while( buffer.hasRemaining() )
			datumValue.addElement( new RPRunsignedInteger64BE(buffer.getLong()) );
	}

	public VariableDatum getDisValue()
	{
		VariableDatum record = new VariableDatum();
		record.setDatumId( datumId.getValue() );
		
		// Turn value into a byte[] and set it on the record, which will in turn compute its length
		ByteBuffer buffer = ByteBuffer.allocate( (int)(datumLength.getValue()*8) ); // value is in bits
		for( RPRunsignedInteger64BE temp : datumValue )
			buffer.putLong( temp.getLongValue() );
		
		record.setDatumValue( buffer.array() );
		
		return record;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getDatumId()
	{
		return this.datumId.getValue();
	}
	
	public long getDatumLengthInBits()
	{
		return this.datumLength.getValue();
	}
	
	public UnsignedInteger64Array1Plus getDatumValue()
	{
		return this.datumValue;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
