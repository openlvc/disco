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

import hla.rti1516e.AttributeHandle;

public class AttributeClass
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String          name;
	private ObjectClass     parent;
	private AttributeHandle handle;
	
	//private Datatype        datatype;
	//private PubSub            pubsub;
	//private Order             order;
	//private Transport         transport;
	//private Sharing           sharing;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public AttributeClass( String name )
	{
		this.name = name;
		this.parent = null;
		this.handle = null;
	}

	public AttributeClass( String name, ObjectClass parent )
	{
		this( name );
		this.parent = parent;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public boolean hasHandle()
	{
		return this.handle != null;
	}

	/**
	 * @return The hash code for this attribute class is the hash code for its name
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}
	
	public AttributeHandle getHandle()
	{
		return handle;
	}
	
	public void setHandle( AttributeHandle handle )
	{
		this.handle = handle;
	}
	
	public ObjectClass getParent()
	{
		return parent;
	}

	protected void setParent( ObjectClass parent )
	{
		this.parent = parent;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
