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
	public static final int ALL_SITES    = 0xFFFF;
	public static final int ALL_APPS     = 0xFFFF;
	public static final int MAX_SITE_ID  = 0xFFFD; // 2^16-1 (all), 2^16-2 (reserved)
	public static final int MAX_APP_ID   = 0xFFFD; // 2^16-1 (all), 2^16-2 (reserved)

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
		this.siteId = siteId;
		this.appId = appId;
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
	
	@Override
	public int hashCode()
	{
		// Limitations: Max Site & App IDs == 255 :(
		
		// -------------------------------------------------------------------------
		// Shift Hash: Anything over 255 for Site/App ID will be 0
		// -------------------------------------------------------------------------
		// For Site & App ID: Clear out high-24 bits, then move into top two octet slots
		// For EntityID     : Clear out high-16 bits and keep in the bottom two octet slots
		//
		//return ((siteId  & 255) << 24) + // &255 cleans out high-24, then we move low 8 to top slot
		//       ((appId   & 255) << 16) + // &255 cleans out high-24, then we move low 8 to 2nd slot
		//       (entityId & 65535);       // &65535 cleans out high-16, and we keep the low 16
		
		// -------------------------------------------------------------------------
		// Modulo Hash: Anything over 255 for Site/App ID will wrap (256=0, 257=1, ...)
		// -------------------------------------------------------------------------
		// First, we wrap the Site and App IDs back around to something below 256.
		// This also has the effect of clearly out the high-24 bits
		//
		// Second, we move them into the top and second-top octet slots
		//
		// Finally, we clean out the high-16 bits for the Entity ID and keep them
		//
		int siteId = this.siteId % 256;
		int appId = this.appId % 256;
		return (siteId << 24) + (appId << 16) + (entityId & 65535);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		siteId = dis.readUI16();
		appId  = dis.readUI16();
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
    public final int getByteLength()
	{
		return 6;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isValid()
	{
		return siteId != 0 || appId != 0 || entityId != 0;
	}
	
	public int getSiteId()
	{
		return this.siteId;
	}
	
	public EntityId setSiteId( int siteId )
	{
		this.siteId = siteId;
		return this;
	}
	
	public int getAppId()
	{
		return this.appId;
	}
	
	public EntityId setAppId( int appId )
	{
		this.appId = appId;
		return this;
	}

	public int getEntityId()
	{
		return entityId;
	}
	
	public int getEntityIdentity()
	{
		return entityId;
	}
	
	public EntityId setEntityId( int entityIdentity )
	{
		this.entityId = entityIdentity;
		return this;
	}

	public void setFullEntityId( int siteId, int appId, int entityId )
	{
		this.setSiteId( siteId );
		this.setAppId( appId );
		this.setEntityId( entityId );
	}
	
	public String toString()
	{
		return siteId+"-"+appId+"-"+entityId;
	}
	
	public String getSiteAndAppId()
	{
		return siteId+"-"+appId;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
