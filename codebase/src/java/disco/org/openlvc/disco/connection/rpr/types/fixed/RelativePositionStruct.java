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
import org.openlvc.disco.pdu.record.EntityCoordinate;

public class RelativePositionStruct extends HLAfixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE bodyXDistance;
	private HLAfloat32BE bodyYDistance;
	private HLAfloat32BE bodyZDistance;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RelativePositionStruct()
	{
		this.bodyXDistance = new HLAfloat32BE();
		this.bodyYDistance = new HLAfloat32BE();
		this.bodyZDistance = new HLAfloat32BE();
		
		// Add to the elements to the parent so that it can do its generic fixed-record stuff
		super.add( bodyXDistance );
		super.add( bodyYDistance );
		super.add( bodyZDistance );
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
	public void setValue( EntityCoordinate position )
	{
		this.bodyXDistance.setValue( position.getX() );
		this.bodyYDistance.setValue( position.getY() );
		this.bodyZDistance.setValue( position.getZ() );
	}
	
	public EntityCoordinate getDisValue()
	{
		return new EntityCoordinate( bodyXDistance.getValue(),
		                             bodyYDistance.getValue(),
		                             bodyZDistance.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
