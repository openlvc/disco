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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openlvc.disco.DiscoException;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ObjectClassHandle;

public class ObjectClass
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectModel             model;
	private String                  name;
	private ObjectClass             parent;
	private ObjectClassHandle       handle;
	private Set<ObjectClass>        children; // TODO Map<String,ObjectClass?>

	private Map<String,AttributeClass> attributes;
	private String                  qualifiedName; // set on first access
	//private Sharing                 sharing;
	//private ObjectModel             model;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected ObjectClass( ObjectModel model, String name, ObjectClass parent )
	{
		this.model = model;
		this.name = name;
		this.parent = parent;
		this.handle = null;
		this.children = new HashSet<ObjectClass>();
		
		this.attributes = new HashMap<>();
		this.qualifiedName = null; // set on first access
		
		// add ourselves to the parent (unless we're the root, in which case we have no parent)
		if( parent != null )
			parent.addChild( this );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public boolean hasHandle()
	{
		return this.handle != null;
	}
	
	private final void notLocked() throws DiscoException
	{
		if( model.isLocked() )
			throw new DiscoException( "Cannot alter class [%s]: Object Model is locked", name );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Attribute Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public void addAttribute( AttributeClass attribute )
	{
		notLocked();
		
		// make sure we don't already have one by this name
		if( attributes.containsKey(attribute.getName()) )
		{
			throw new DiscoException( "Add Attribute Failed: Class [%s] already has attribute with name [%s]",
			                          name, attribute.getName() );
		}
		
		// store the attribute
		attribute.setParent( this );
		attributes.put( attribute.getName(), attribute );
	}
	
	public AttributeClass getDeclaredAttribute( String attributeName )
	{
		return attributes.get( attributeName );
	}
	
	public AttributeClass getDeclaredAttribute( AttributeHandle attributeHandle )
	{
		throw new DiscoException( "Not Yet Implemented" );
	}
	
	public AttributeClass getAttribute( String attributeName )
	{
		AttributeClass attribute = attributes.get( attributeName );
		if( attribute == null && this.parent != null )
			attribute = parent.getAttribute( attributeName );
		
		return attribute;
	}
	
	public AttributeClass getAttribute( AttributeHandle attributeHandle )
	{
		for( AttributeClass temp : attributes.values() )
			if( temp.getHandle().equals(attributeHandle) )
				return temp;

		// we didn't find it, so we need to look further up the tree
		if( parent != null )
			return parent.getAttribute( attributeHandle );
		else
			return null;
	}
	
	//
	// Attribute Group Methods
	//
	public Collection<AttributeClass> getDeclaredAttributes()
	{
		return Collections.unmodifiableCollection( this.attributes.values() );
	}
	
	public Collection<AttributeClass> getAllAttributes()
	{
		// if we don't have parent, all our attributes are just our local ones
		if( this.parent == null )
		{
			return this.getDeclaredAttributes();
		}
		else
		{
			Collection<AttributeClass> inherited = new HashSet<>( this.attributes.values() );
			
			// loop through each parent and get their attributes - creates many fewer collections
			// doing it this way compared to recursing up
			ObjectClass currentParent = this.parent;
			while( currentParent != null )
			{
				inherited.addAll( currentParent.attributes.values() );
				currentParent = currentParent.parent;
			}
			
			// return the complete set
			return inherited;
		}
	}
	
	public boolean hasDeclaredAttribute( String attributeName )
	{
		return attributes.containsKey( attributeName );
	}

	public boolean hasAttribute( String attributeName )
	{
		boolean result = attributes.containsKey( attributeName );
		if( !result && parent != null )
			result = parent.hasAttribute( attributeName );
		
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getLocalName()
	{
		return name;
	}

	public String getQualifiedName()
	{
		if( this.qualifiedName != null )
			return this.qualifiedName;

		// it's not cached, so we need to build it up
		if( this.parent != null )
			this.qualifiedName = parent.getQualifiedName()+"."+name;
		else
			this.qualifiedName = name;
		
		return this.qualifiedName;
	}

	public void setLocalName( String name )
	{
		notLocked();
		this.name = name;
		this.qualifiedName = null; // clear so it gets regenerated on next call
	}

	public ObjectClassHandle getHandle()
	{
		return handle;
	}

	public void setHandle( ObjectClassHandle handle )
	{
		this.handle = handle;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Class Hierarchy Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isObjectRoot()
	{
		return this == model.getObjectRoot();
	}

	/**
	 * Will return <code>true</code> if this class matches the given class, or any of our
	 * parents match the given class.
	 * 
	 * @param other The class we're looking to see if we have lineage with 
	 * @return True if the other class is either our class, or any of our inheritence tree
	 */
	public boolean isChildOf( ObjectClass other )
	{
		// look up our tree to see if the given class is one of our parents
		ObjectClass currentParent = this;
		while( currentParent != null )
		{
			if( currentParent == other )
				return true;
			else
				currentParent = currentParent.parent;
		}
		
		return false;
	}

	public ObjectClass getParent()
	{
		return parent;
	}

	public Collection<ObjectClass> getChildren()
	{
		return Collections.unmodifiableCollection( this.children );
	}

	protected void addChild( ObjectClass child )
	{
		notLocked();
		this.children.add( child );
	}

	public ObjectClass getChild( String childName )
	{
		for( ObjectClass child : children )
			if( child.getLocalName().equals(childName) )
				return child;
		
		return null;
	}
	
	public boolean containsChild( String childName )
	{
		return getChild(childName) != null;
	}
	
	protected void setParent( ObjectClass parent )
	{
		notLocked();
		this.parent = parent;
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
