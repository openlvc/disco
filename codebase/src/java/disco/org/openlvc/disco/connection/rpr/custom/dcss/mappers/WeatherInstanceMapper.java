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
package org.openlvc.disco.connection.rpr.custom.dcss.mappers;

import java.util.Arrays;
import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.custom.dcss.objects.WeatherInstance;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaDiscover;
import org.openlvc.disco.connection.rpr.mappers.HlaReflect;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.DecoderException;

public class WeatherInstanceMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String HLA_CLASS_NAME = "HLAobjectRoot.DCSSBaseObject.WeatherInstance";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass hlaClass;
	private AttributeClass id;
	private AttributeClass instanceType;
	private AttributeClass geoArea;
	private AttributeClass geoAreaType;
	private AttributeClass startTime;
	private AttributeClass endTime;
	private AttributeClass domains;
	private AttributeClass active;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		ObjectModel fom = rprConnection.getFom();
		this.hlaClass = fom.getObjectClass( HLA_CLASS_NAME );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: "+HLA_CLASS_NAME );
		
		this.id = this.hlaClass.getAttribute( "Id" );
		this.instanceType = this.hlaClass.getAttribute( "InstanceType" );
		this.geoArea = this.hlaClass.getAttribute( "GeoArea" );
		this.geoAreaType = this.hlaClass.getAttribute( "GeoAreaType" );
		this.startTime = this.hlaClass.getAttribute( "StartTime" );
		this.endTime = this.hlaClass.getAttribute( "EndTime" );
		this.domains = this.hlaClass.getAttribute( "Domains" );
		this.active = this.hlaClass.getAttribute( "Active" );
		
		// Do publication and subscription
		super.publishAndSubscribe( hlaClass );
	}
	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.DcssWeatherInstance );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( hlaClass == event.theClass )
		{
			WeatherInstance hlaObject = new WeatherInstance();
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			objectStore.addDiscoveredHlaObject( hlaObject );

			if( logger.isDebugEnabled() )
			{
				logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
				              event.theClass.getLocalName(), 
				              event.theObject );
			}
			
			// Request an attribute update for the object so that we can get everything we need
			super.requestAttributeUpdate( hlaObject );
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		if( (event.hlaObject instanceof WeatherInstance) == false )
			return;
		
		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( (WeatherInstance)event.hlaObject, event.attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		opscenter.getPduReceiver().receive( event.hlaObject.toPdu().toByteArray() );
	}
	
	private void deserializeFromHla( WeatherInstance instance, AttributeHandleValueMap map )
		throws DecoderException
	{
		deserializeInto( map, this.id, instance.getId() );
		deserializeInto( map, this.instanceType, instance.getInstanceType() );
		deserializeInto( map, this.geoArea, instance.getGeoArea() );
		deserializeInto( map, this.geoAreaType, instance.getGeoAreaType() );
		deserializeInto( map, this.startTime, instance.getStartTime() );
		deserializeInto( map, this.endTime, instance.getEndTime() );
		deserializeInto( map, this.domains, instance.getDomains() );
		deserializeInto( map, this.active, instance.getActive() );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
