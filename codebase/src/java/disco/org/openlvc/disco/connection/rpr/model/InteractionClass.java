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

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;

public class InteractionClass
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectModel             model;
	private String                  name;
	private String                  qualifiedName; // set on first access
	private InteractionClassHandle  handle;
	private InteractionClass        parent;
	private Set<InteractionClass>   children;
	
	private Map<String,ParameterClass> parameters;


	//private Order                   order;
	//private Transport               transport;
	//private Sharing 			    sharing;
	//private ObjectModel             model;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public InteractionClass( ObjectModel model, String name, InteractionClass parent )
	{
		this.model = model;
		this.name = name;
		this.qualifiedName = null;
		this.handle = null;
		this.parent = parent;
		this.children = new HashSet<>();
		this.parameters = new HashMap<>();
		
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
	/// Parameter Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public void addParameter( ParameterClass parameter )
	{
		notLocked();
		
		// make sure we don't already have one by this name
		if( parameters.containsKey(parameter.getName()) )
		{
			throw new DiscoException( "Add Parameter Failed: Class [%s] already has parameter with name [%s]",
			                          name, parameter.getName() );
		}
		
		// store the parameter
		parameter.setParent( this );
		parameters.put( parameter.getName(), parameter );
	}
	
	public ParameterClass getDeclaredParameter( String parameterName )
	{
		return parameters.get( parameterName );
	}
	
	public ParameterClass getDeclaredParameter( ParameterHandle parameterHandle )
	{
		throw new DiscoException( "Not Yet Implemented" );
	}
	
	public ParameterClass getParameter( String parameterName )
	{
		ParameterClass parameter = this.parameters.get( parameterName );
		if( parameter == null && this.parent != null )
			parameter = parent.getParameter( parameterName );

		return parameter;
	}
	
	public ParameterClass getParameter( ParameterHandle parameterHandle )
	{
		throw new DiscoException( "Not Yet Implemented" );
	}
	
	//
	// Parameter Group Methods
	//
	public Collection<ParameterClass> getDeclaredParameters()
	{
		return Collections.unmodifiableCollection( this.parameters.values() );
	}
	
	public Collection<ParameterClass> getAllParameters()
	{
		// if we don't have parent, all our attributes are just our local ones
		if( this.parent == null )
		{
			return this.getDeclaredParameters();
		}
		else
		{
			Collection<ParameterClass> inherited = new HashSet<>( this.parameters.values() );
			
			// loop through each parent and get their parameters - creates many fewer collections
			// doing it this way compared to recursing up
			InteractionClass currentParent = this.parent;
			while( currentParent != null )
			{
				inherited.addAll( currentParent.parameters.values() );
				currentParent = currentParent.parent;
			}
			
			// return the complete set
			return inherited;
		}
	}
	
	public boolean hasDeclaredParameter( String parameterName )
	{
		return parameters.containsKey( parameterName );
	}

	public boolean hasParameter( String parameterName )
	{
		boolean result = parameters.containsKey( parameterName );
		if( !result && parent != null )
			result = parent.hasParameter( parameterName );
		
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

	public InteractionClassHandle getHandle()
	{
		return handle;
	}

	public void setHandle( InteractionClassHandle handle )
	{
		this.handle = handle;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Class Hierarchy Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isInteractionRoot()
	{
		return this == model.getInteractionRoot();
	}

	/**
	 * Will return <code>true</code> if this class matches the given class, or any of our
	 * parents match the given class.
	 * 
	 * @param other The class we're looking to see if we have lineage with 
	 * @return True if the other class is either our class, or any of our inheritence tree
	 */
	public boolean isChildOf( InteractionClass other )
	{
		// look up our tree to see if the given class is one of our parents
		InteractionClass currentParent = this;
		while( currentParent != null )
		{
			if( currentParent == other )
				return true;
			else
				currentParent = currentParent.parent;
		}
		
		return false;
	}

	public InteractionClass getParent()
	{
		return parent;
	}

	public Collection<InteractionClass> getChildren()
	{
		return Collections.unmodifiableCollection( this.children );
	}

	protected void addChild( InteractionClass child )
	{
		notLocked();
		this.children.add( child );
	}

	public InteractionClass getChild( String childName )
	{
		for( InteractionClass child : children )
			if( child.getLocalName().equals(childName) )
				return child;
		
		return null;
	}
	
	public boolean containsChild( String childName )
	{
		return getChild(childName) != null;
	}
	
	protected void setParent( InteractionClass parent )
	{
		notLocked();
		this.parent = parent;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
