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

import org.openlvc.disco.connection.rpr.types.basic.HLAinteger16BE;
import org.openlvc.disco.connection.rpr.types.basic.HLAinteger32BE;

public class SINCGARSModulationStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAinteger16BE fhNetID;
	private HLAinteger16BE hopSetID;
	private HLAinteger16BE lockoutSetID;
	private HLAinteger16BE transmissionSecurityKey;
	private HLAinteger32BE fhSynchronizationTimeOffset;  // TimeSecondInteger32

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SINCGARSModulationStruct()
	{
		this.fhNetID = new HLAinteger16BE( 0 );
		this.hopSetID = new HLAinteger16BE( 0 );
		this.lockoutSetID = new HLAinteger16BE( 0 );
		this.transmissionSecurityKey = new HLAinteger16BE( 0 );
		this.fhSynchronizationTimeOffset = new HLAinteger32BE( 0 );
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.fhNetID );
		super.add( this.hopSetID );
		super.add( this.lockoutSetID );
		super.add( this.transmissionSecurityKey );
		super.add( this.fhSynchronizationTimeOffset );
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
	public void setValue()
	{
		// FIXME
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
