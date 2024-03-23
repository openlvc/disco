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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.connection.rpr.ObjectStore;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectId;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.record.EntityId;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;

public abstract class ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass objectClass;
	private ObjectInstanceHandle objectHandle;
	private AttributeHandleValueMap attributes;
	private RTIobjectId rtiId; // This is so stupid. A second unique id, all because the HLA
	                           // spec doesn't define a concrete type for handles. Shoot me.

	protected ObjectStore objectStore;
	private boolean ready;
	private long lastUpdated;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected ObjectInstance()
	{
		this.objectClass  = null;
		this.objectHandle = null;
		this.attributes   = null;
		this.rtiId        = null;
		
		this.objectStore  = null;  // set when we are added to a store
		this.ready        = false;
		this.lastUpdated  = -1;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/**
	 * Update the values of the RPR representation from those contained in the provided PDU.
	 * 
	 * @param pdu The PDU to load the local values from.
	 */
	public abstract void fromPdu( PDU pdu );

	/**
	 * Serialize this RPR object representation to an appropriate PDU.<br/>
	 * A reference to the {@link ObjectStore} that caches important information is passed in
	 * to allow translation between any contained RPR types and their DIS equivalents (such
	 * as translating between RTIobjectIds and DIS EntityIds).
	 * 
	 * @return A serialized version of the object as the appropriate DIS PDU.
	 */
	public abstract PDU toPdu();

	/**
	 * Every object in some way or another has a DIS ID it can be mapped to. This method will
	 * return that ID regardless of the underlying object type.
	 * 
	 * @return The DIS ID, or null if one has not been assigned.
	 */
	public abstract EntityId getDisId();
	
	/**
	 * Do any checks to be sure that the object is ready to be turned into a PDU, including
	 * validating any transitive dependency information through the object store. Once this
	 * method returns true, it'll never be called again for the object.
	 * 
	 * @return True if we are ready, false otherwise.
	 */
	protected abstract boolean checkReady();
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns whether this object is ready to be converted into a PDU yet. To be ready, it must
	 * contain all the data it is expecting, and all the data that it needs to logically express
	 * itself as a PDU.
	 * <p/>
	 * Connections between RPR objects are expressed by including a unique id as a form of
	 * reference. Just having a valid id however doesn't mean the object on the other end has
	 * been either discovered, or is itself ready. Use our {@link ObjectStore} reference (set
	 * when we are added to an object store) to check transitive dependencies.
	 * <p/>
	 * The return value is cached, so we only perform the check until it turns true for the first
	 * time, then we will always return true without checking.
	 * 
	 * @return True if this object has all info it needs to be turned into a PDU; false otherwise
	 */
	public boolean isReady()
	{
		if( !ready )
			ready = checkReady();
		
		return ready;
	}
	
	public void setObjectClass( ObjectClass objectClass )
	{
		this.objectClass = objectClass;
	}

	public ObjectInstanceHandle getObjectHandle()
	{
		return objectHandle;
	}

	public void setObjectHandle( ObjectInstanceHandle objectHandle )
	{
		this.objectHandle = objectHandle;
	}

	public ObjectClass getObjectClass()
	{
		return objectClass;
	}

	public AttributeHandleValueMap getObjectAttributes()
	{
		return this.attributes;
	}
	
	public void setObjectAttributes( AttributeHandleValueMap attributes )
	{
		this.attributes = attributes;
	}
	
	public void setObjectName( String objectName )
	{
		this.rtiId = new RTIobjectId( objectName );
	}

	public String getObjectName()
	{
		return this.rtiId.getValue();
	}
	
	public RTIobjectId getRtiObjectId()
	{
		return this.rtiId;
	}
	
	public void addToStore( ObjectStore store )
	{
		this.objectStore = store;
	}
	
	public long getLastUpdatedTime()
	{
		return this.lastUpdated;
	}
	
	public void setLastUpdatedTimeToNow()
	{
		this.lastUpdated = System.currentTimeMillis();
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
