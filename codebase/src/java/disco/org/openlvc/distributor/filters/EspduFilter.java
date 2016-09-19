/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.distributor.filters;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.PduType;

public class EspduFilter implements IFilter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private enum Field
	{
		Id             ( "entity.id" ),
		SiteId         ( "entity.siteId" ),
		AppId          ( "entity.appId" ),
		EntityId       ( "entity.entityId" ),
		Marking        ( "entity.marking" ),
		Force          ( "entity.force" ),
		Type           ( "entity.type" ),
		AlternateType  ( "entity.type" );
		
		private String string;
		private Field( String string )
		{
			this.string = string;
		}
		
		private static Field from( String givenString )
		{
			for( Field field : Field.values() )
				if( givenString.equalsIgnoreCase(field.string) )
					return field;
			
			throw new IllegalArgumentException( "No such ESPDU filter type: "+givenString );
		}
	}

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final Field field;
	private final Operand assessment;
	private final String value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EspduFilter( String field, Operand assessment, String value )
	{
		this.field = Field.from( field );
		this.assessment = assessment;
		this.value = value;
		
		validate( this.field, value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Return <code>true</code> if the given PDU matches the condition set in this filter.
	 */
	@Override
	public boolean matches( PDU pdu )
	{
		if( pdu.getType() != PduType.EntityState )
			return true;

		switch( field )
		{
			case Id:
				return compareId( (EntityStatePdu)pdu );
			case SiteId:
				return compareSiteId( (EntityStatePdu)pdu );
			case AppId:
				return compareAppId( (EntityStatePdu)pdu );
			case EntityId:
				return compareEntityId( (EntityStatePdu)pdu );
			case Marking:
				return compareMarking( (EntityStatePdu)pdu );
			case Force:
				return compareForce( (EntityStatePdu)pdu );
			case Type:
				return compareType( (EntityStatePdu)pdu );
			case AlternateType:
				return compareAlternateType( (EntityStatePdu)pdu );
			default:
				throw new IllegalArgumentException( "No such ESPDU filter type: "+field );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private boolean compareId( EntityStatePdu pdu )
	{
		return false;
	}
	
	private boolean compareSiteId( EntityStatePdu pdu )
	{
		return false;
	}

	private boolean compareAppId( EntityStatePdu pdu )
	{
		return false;
	}

	private boolean compareEntityId( EntityStatePdu pdu )
	{
		return false;
	}

	private boolean compareMarking( EntityStatePdu pdu )
	{
		return false;
	}

	private boolean compareForce( EntityStatePdu pdu )
	{
		return this.assessment.compare( ForceId.valueOf(value), pdu.getForceID() );
	}

	private boolean compareType( EntityStatePdu pdu )
	{
		return false;
	}

	private boolean compareAlternateType( EntityStatePdu pdu )
	{
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getFilterString()
	{
		return field+" "+assessment+" "+value;
	}

	@Override
	public String toString()
	{
		return field+" "+assessment.text+" "+value;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filter Validation Methods   ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void validate( Field field, String value )
	{
		switch( field )
		{
			case Id:
				validateId( value );
				break;
			case SiteId:
				validateSiteId( value );
				break;
			case AppId:
				validateAppId( value );
				break;
			case EntityId:
				validateEntityId( value );
				break;
			case Marking:
				validateMarking( value );
				break;
			case Force:
				validateForce( value );
				break;
			case Type:
				validateType( value );
				break;
			case AlternateType:
				validateAlternateType( value );
				break;
			default:
				throw new IllegalArgumentException( "No such ESPDU filter type: "+field );
		}
	}

	private void validateId( String value )
	{
	}
	
	private void validateSiteId( String value )
	{
	}

	private void validateAppId( String value )
	{
	}

	private void validateEntityId( String value )
	{
	}

	private void validateMarking( String value )
	{
	}

	private void validateForce( String value )
	{
		ForceId.valueOf( value );
	}

	private void validateType( String value )
	{
	}

	private void validateAlternateType( String value )
	{
	}

	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
