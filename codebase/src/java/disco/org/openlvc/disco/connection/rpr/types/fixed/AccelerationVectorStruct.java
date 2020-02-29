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

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.pdu.record.VectorRecord;

public class AccelerationVectorStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE XAcceleration;
	private HLAfloat32BE YAcceleration;
	private HLAfloat32BE ZAcceleration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AccelerationVectorStruct()
	{
		this.XAcceleration = new HLAfloat32BE( 0.0f );
		this.YAcceleration = new HLAfloat32BE( 0.0f );
		this.ZAcceleration = new HLAfloat32BE( 0.0f );
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.XAcceleration );
		super.add( this.YAcceleration );
		super.add( this.ZAcceleration );
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
	public void setValue( VectorRecord record )
	{
		this.XAcceleration.setValue( record.getFirstComponent() );
		this.YAcceleration.setValue( record.getSecondComponent() );
		this.ZAcceleration.setValue( record.getThirdComponent() );
	}

	public VectorRecord getDisValue()
	{
		return new VectorRecord( XAcceleration.getValue(),
		                         YAcceleration.getValue(),
		                         ZAcceleration.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
