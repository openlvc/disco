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
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * The unique designation of an environmental object shall be specified by the Object Identifier 
 * record.
 */
public class ObjectId implements IPduComponent
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int siteId;
	private int appId;
	private int objectId;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectId()
	{
		this( 0, 0, 0 );
	}
	
	public ObjectId( int siteId, int appId, int objectIdentity )
	{
		this.siteId = siteId;
		this.appId = appId;
		this.objectId = objectIdentity;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof ObjectId )
		{
			ObjectId asEntityId = (ObjectId)other;
			if( (asEntityId.siteId == siteId) &&
				(asEntityId.appId == appId) &&
				(asEntityId.objectId == objectId) )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( siteId, appId, objectId );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		siteId = dis.readUI16();
		appId  = dis.readUI16();
		objectId = dis.readUI16();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI16( siteId );
		dos.writeUI16( appId );
		dos.writeUI16( objectId );
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
		return siteId != 0 || appId != 0 || objectId != 0;
	}
	
	public int getSiteId()
	{
		return this.siteId;
	}
	
	public ObjectId setSiteId( int siteId )
	{
		this.siteId = siteId;
		return this;
	}
	
	public int getAppId()
	{
		return this.appId;
	}
	
	public ObjectId setAppId( int appId )
	{
		this.appId = appId;
		return this;
	}

	public int getObjectId()
	{
		return objectId;
	}
	
	public ObjectId setObjectId( int objectId )
	{
		this.objectId = objectId;
		return this;
	}

	public void setFullObjectId( int siteId, int appId, int objectId )
	{
		this.setSiteId( siteId );
		this.setAppId( appId );
		this.setObjectId( objectId );
	}
	
	public String toString()
	{
		return siteId+"-"+appId+"-"+objectId;
	}
	
	public String getSiteAndAppId()
	{
		return siteId+"-"+appId;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
