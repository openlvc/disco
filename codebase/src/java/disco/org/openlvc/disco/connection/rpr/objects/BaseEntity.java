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

import org.openlvc.disco.connection.rpr.types.fixed.EntityIdentifierStruct;
import org.openlvc.disco.connection.rpr.types.fixed.EntityTypeStruct;
import org.openlvc.disco.connection.rpr.types.variant.SpatialVariantStruct;
import org.openlvc.disco.pdu.entity.EntityStatePdu;

/**
 * NOTE: IsPartOf and associated RelativeSpatial not yet supported as the PDUs that feed this
 *       are not supported in Disco yet.
 */
public abstract class BaseEntity extends ObjectInstance
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected EntityTypeStruct entityType;
	protected EntityIdentifierStruct entityIdentifier;
	//protected IsPartOfStruct isPartOf;
	protected SpatialVariantStruct spatial;
	//protected SpatialVariantStruct relativeSpatial;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected BaseEntity()
	{
		this.entityType = new EntityTypeStruct();
		this.entityIdentifier = new EntityIdentifierStruct();
		//this.isPartOf = new IsPartOfStruct();
		this.spatial = new SpatialVariantStruct();
		//this.relativeSpatial = new SpatialVariantStruct();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	protected boolean checkLoaded()
	{
		return entityType.isDefaults() == false &&
		       entityIdentifier.isDefaults() == false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityTypeStruct getEntityType()
	{
		return entityType;
	}

	public EntityIdentifierStruct getEntityIdentifier()
	{
		return entityIdentifier;
	}

	//public IsPartOfStruct getIsPartOf()
	//{
	//	return isPartOf;
	//}

	public SpatialVariantStruct getSpatial()
	{
		return spatial;
	}

	//public SpatialVariantStruct getRelativeSpatial()
	//{
	//	return relativeSpatial;
	//}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void fromPdu( EntityStatePdu incoming )
	{
		this.entityType.setValue( incoming.getEntityType() );
		this.entityIdentifier.setValue( incoming.getEntityID() );
		//this.isPartOf.setValue();
		this.spatial.setValue( incoming );
		//this.relativeSpatial.setValue();
	}
	
	protected void toPdu( EntityStatePdu pdu )
	{
		pdu.setEntityType( entityType.getDisValue() );
		pdu.setEntityID( entityIdentifier.getDisValue() );
		//isPartOf
		this.spatial.toPdu( pdu );
		//relativeSpatial
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
