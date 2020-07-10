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

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.pdu.record.FixedDatum;

public class FixedDatumStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE fixedDatumIdentifier;
	private RPRunsignedInteger32BE fixedDatumValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FixedDatumStruct()
	{
		super();
		this.fixedDatumIdentifier = new RPRunsignedInteger32BE();
		this.fixedDatumValue = new RPRunsignedInteger32BE();
		
		// Add elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.fixedDatumIdentifier );
		super.add( this.fixedDatumValue );
	}

	public FixedDatumStruct( long id, long value )
	{
		this();
		this.fixedDatumIdentifier.setValue( id );
		this.fixedDatumValue.setValue( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Mappings Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void setValue( FixedDatum record )
	{
		this.fixedDatumIdentifier.setValue( record.getDatumId() );
		this.fixedDatumValue.setValue( record.getDatumValue() );
	}

	public FixedDatum getDisValue()
	{
		return new FixedDatum( fixedDatumIdentifier.getValue(), fixedDatumValue.getValue() );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public long getFixedDatumIdentifier()
	{
		return fixedDatumIdentifier.getValue();
	}

	public long getFixedDatumValue()
	{
		return fixedDatumValue.getValue();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
