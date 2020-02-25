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

import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;

public class SpatialRPStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private WorldLocationStruct worldLocation;
	private EnumHolder<RPRboolean> isFrozen;
	private OrientationStruct orientation;
	private VelocityVectorStruct velocityVector;
	private AngularVelocityVectorStruct angularVelocity;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SpatialRPStruct()
	{
		this.worldLocation = new WorldLocationStruct();
		this.isFrozen = new EnumHolder<>( RPRboolean.False );
		this.orientation = new OrientationStruct();
		this.velocityVector = new VelocityVectorStruct();
		this.angularVelocity = new AngularVelocityVectorStruct();
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( worldLocation );
		super.add( isFrozen );
		super.add( orientation );
		super.add( velocityVector );
		super.add( angularVelocity );
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
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
