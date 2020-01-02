/*
 *   Copyright 2019 Open LVC Project.
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
package org.openlvc.disillusion;

import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disillusion.paths.IPath;

/**
 * A simple container class to hold the details of a path and the PDUs of entities which are
 * travelling along it
 */
public class PDUAndPathContainer
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<EntityStatePdu> entityStatePdus;
	private IPath path;
	private double spacing;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PDUAndPathContainer(IPath path, double spacing)
	{
		this.path = path;
		this.spacing = spacing;
		this.entityStatePdus = new ArrayList<EntityStatePdu>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void addEntityStatePdu( EntityStatePdu pdu )
	{
		this.entityStatePdus.add( pdu );
	}
	
	public IPath getPath()
	{
		return this.path;
	}

	public double getSpacing()
	{
		return this.spacing;
	}
	
	public List<EntityStatePdu> getEntityStatePdus()
	{
		return this.entityStatePdus;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
