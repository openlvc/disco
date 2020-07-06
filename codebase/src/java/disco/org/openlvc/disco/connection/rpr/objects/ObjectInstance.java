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
	private boolean loaded;

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
		this.loaded       = false;
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
	 * Implemented by child classes. This should return true once enough information has been
	 * set on the class (either via reflection or local settings) that it can be considered as
	 * "loaded". Once this call returns true, so will the public facing {@link #isLoaded()}.
	 * 
	 * @return True if the object has enough information to be considered loaded. False otherwise.
	 */
	protected abstract boolean checkLoaded();
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return True if the object should be considered as "loaded". An object is loaded once we
	 *         have both discovered it, and have received a reflection for all the _required_
	 *         attributes. What is required will differ on a class-by-class basis and is left to
	 *         subclasses of {@link ObjectInstance} to decide.
	 */
	public boolean isLoaded()
	{
		// If loaded is false, we should re-check it. This could be expensive, so we
		// only do it as long as we're _NOT_ loaded. Once we are we stop caring.
		if( this.loaded == false )
			this.loaded = checkLoaded();
		
		return this.loaded;
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
	
	public RTIobjectId getRtiObjectId()
	{
		return this.rtiId;
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
