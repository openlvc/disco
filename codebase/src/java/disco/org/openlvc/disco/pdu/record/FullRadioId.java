/*
 *   Copyright 2021 Open LVC Project.
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

/**
 * THIS DOES NOT REPRESENT A STANDARD DIS TYPE.<p/>
 * 
 * This class is used for internal purposes primarily. It aggregated a DIS {@link EntityId} with
 * a forth radio id field.<p/>
 * 
 * It is not an IPduComponent, and it provided only as a convenient wrapper for identifying
 * radios uniquely.
 */
public class FullRadioId
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId entityId;
	private int radioId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FullRadioId( EntityId entityId, int radioId )
	{
		this.entityId = entityId;
		this.radioId = radioId;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals( Object value )
	{
		if( value != null && value instanceof FullRadioId )
		{
			FullRadioId other = (FullRadioId)value;
			return (other.radioId == this.radioId) &&
			       (other.entityId.equals(this.entityId));
		}
		
		return false;
	}
	
	@Override
	public final int hashCode()
	{
		return (entityId.toString()+"-"+radioId).hashCode();
	}
	
	public EntityId getEntityId()
	{
		return entityId;
	}

	public void setEntityId( EntityId entityId )
	{
		this.entityId = entityId;
	}

	public int getRadioId()
	{
		return radioId;
	}

	public void setRadioId( int radioId )
	{
		this.radioId = radioId;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
