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
package org.openlvc.disco.connection.rpr.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlvc.disco.DiscoException;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;

/**
 * This class represents a unified object model consisting of the content from multiple
 * FOM modules. Access classes, interactions and datatypes described in a unified set of
 * modules through here. Handles can also be stored with a model for caching when used
 * at federation runtime.
 */
public class ObjectModel
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<String> filenames;
	private boolean locked;
	
	//private Map<String,Datatype> datatypes;
	private Map<String,ObjectClass> oclasses;
	private Map<String,InteractionClass> iclasses;
	
	private ObjectClass objectRoot;
	private InteractionClass interactionRoot;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectModel()
	{
		this.filenames = new ArrayList<>();
		this.locked = false;
		
		//this.datatypes = new HashMap<>();
		this.oclasses = new HashMap<>();
		this.iclasses = new HashMap<>();
		
		// Create Object and Interaction roots
		this.objectRoot = this.createObjectClass( "HLAobjectRoot", null );
		this.objectRoot.addAttribute( new AttributeClass("HLAprivilegeToDelete") );
		this.interactionRoot = this.createInteractionClass( "HLAinteractionRoot", null );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public ObjectClass getObjectRoot()
	{
		return this.objectRoot;
	}
	
	public InteractionClass getInteractionRoot()
	{
		return this.interactionRoot;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getFomLocations()
	{
		return Collections.unmodifiableList( this.filenames );
	}
	
	public boolean isLocked()
	{
		return this.locked;
	}
	
	public void lock()
	{
		// go through and cache the various 
		
		
		this.locked = true;
	}
	
	public void unlock()
	{
		this.locked = false;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Object Class Management Methods   //////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ObjectClass getObjectClass( String qualifiedName )
	{
		return this.oclasses.get( qualifiedName );
	}
	
	public ObjectClass getObjectClass( ObjectClassHandle handle )
	{
		for( ObjectClass clazz : oclasses.values() )
			if( clazz.getHandle().equals(handle) )
				return clazz;
		
		throw new DiscoException( "Object Class not found [%s]", handle );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Interaction Class Management Methods   /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public InteractionClass getInteractionClass( String qualifiedName )
	{
		return this.iclasses.get( qualifiedName );
	}
	
	public InteractionClass getInteractionClass( InteractionClassHandle handle )
	{
		for( InteractionClass clazz : iclasses.values() )
			if( clazz.getHandle().equals(handle) )
				return clazz;
		
		throw new DiscoException( "Interaction Class not found [%s]", handle );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods   //////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public ObjectClass createObjectClass( String name, ObjectClass parent )
	{
		// create a new object class and link it to this model
		ObjectClass objectClass = new ObjectClass( this, name, parent );
		this.oclasses.put( objectClass.getQualifiedName(), objectClass );
		return objectClass;
	}
	
	public InteractionClass createInteractionClass( String name, InteractionClass parent )
	{
		// create a new interaction class and link it to this model
		InteractionClass interactionClass = new InteractionClass( this, name, parent );
		this.iclasses.put( interactionClass.getQualifiedName(), interactionClass );
		return interactionClass;
	}


	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
