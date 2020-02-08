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

import hla.rti1516e.ParameterHandle;

public class ParameterClass
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String name;
	//private Datatype datatype;
	private InteractionClass parent;
	private ParameterHandle handle;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ParameterClass( String name )
	{
		this.name = name;
		this.parent = null;
		this.handle = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

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

	public InteractionClass getParent()
	{
		return parent;
	}
	
	protected void setParent( InteractionClass parent )
	{
		this.parent = parent;
	}

	public ParameterHandle getHandle()
	{
		return handle;
	}

	public void setHandle( ParameterHandle handle )
	{
		this.handle = handle;
	}

	public boolean hasHandle()
	{
		return this.handle != null;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
