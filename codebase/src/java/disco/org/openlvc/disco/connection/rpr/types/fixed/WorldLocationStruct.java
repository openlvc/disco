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

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE;
import org.openlvc.disco.pdu.record.WorldCoordinate;

public class WorldLocationStruct extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat64BE x;
	private HLAfloat64BE y;
	private HLAfloat64BE z;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public WorldLocationStruct()
	{
		this.x = new HLAfloat64BE( 0.0 );
		this.y= new HLAfloat64BE( 0.0 );
		this.z = new HLAfloat64BE( 0.0 );
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( this.x );
		super.add( this.y );
		super.add( this.z );
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
	public void setValue( WorldCoordinate location )
	{
		this.x.setValue( location.getX() );
		this.y.setValue( location.getY() );
		this.z.setValue( location.getZ() );
	}

	public WorldCoordinate getDisValue()
	{
		return new WorldCoordinate( x.getValue(), y.getValue(), z.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
