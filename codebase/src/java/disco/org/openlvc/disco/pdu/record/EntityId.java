/*
 *   Copyright 2015 Open LVC Project.
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

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * Each Entity in a given exercise executing on a DIS application shall be 
 * assigned an Entity Identifier Record Unique to the exercise.
 */
public class EntityId implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final int ALL_ENTITIES = 0xFFFF;
		
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int siteId;
	private int appId;
	private int entityId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EntityId()
	{
		this( 0, 0, 0 );
	}
	
	public EntityId( int siteId, int appId, int entityIdentity )
	{
		this.siteId = 0;
		this.appId = 0;
		this.entityId = entityIdentity;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof EntityId )
		{
			EntityId asEntityId = (EntityId)other;
			if( (asEntityId.siteId == siteId) &&
				(asEntityId.appId == appId) &&
				(asEntityId.entityId == entityId) )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public EntityId clone()
	{
		return new EntityId( siteId, appId, entityId );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		siteId = dis.readUI16();
		entityId = dis.readUI16();
		entityId = dis.readUI16();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI16( siteId );
		dos.writeUI16( appId );
		dos.writeUI16( entityId );
    }
	
	@Override
    public int getByteLength()
	{
		return 6;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getSiteId()
	{
		return this.siteId;
	}
	
	public void setSiteId( int siteId )
	{
		this.siteId = siteId;
	}
	
	public int getAppId()
	{
		return this.appId;
	}
	
	public void setAppId( int appId )
	{
		this.appId = appId;
	}

	public int getEntityIdentity()
	{
		return entityId;
	}
	
	public void setEntityId( int entityIdentity )
	{
		this.entityId = entityIdentity;
	}

	public String toString()
	{
		return siteId+"-"+appId+"-"+entityId;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
