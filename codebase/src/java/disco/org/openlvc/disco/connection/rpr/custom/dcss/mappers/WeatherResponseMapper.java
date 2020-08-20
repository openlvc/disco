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
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.CloudLayerResponse;
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.GroundResponse;
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.WeatherResponse;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaInteraction;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.DecoderException;

public class WeatherResponseMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass weatherResponseClass;
	private InteractionClass groundResponseClass;
	private InteractionClass cloudLayerResponseClass;
	private ParameterClass instanceId;
	private ParameterClass dateTime;
	private ParameterClass timeOffset;
	private ParameterClass location;
	private ParameterClass weatherResponseType;
	private ParameterClass uuid;
	private ParameterClass entityId;
	
	private ParameterClass precipitationRate;
	private ParameterClass temperature;
	private ParameterClass humidity;
	private ParameterClass pressure;
	private ParameterClass totalCloudCover;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private InteractionClass tryGetInteractionClass( String name )
	{
		InteractionClass ic = rprConnection.getFom().getInteractionClass( name );
		if( ic == null )
			throw new DiscoException( "Could not find interaction: "+name );
		
		return ic;
	}
	
	@Override
	protected void initialize() throws DiscoException
	{
		this.weatherResponseClass = tryGetInteractionClass( "HLAinteractionRoot.Service.WeatherResponse" );
		this.groundResponseClass = tryGetInteractionClass( "HLAinteractionRoot.Service.WeatherResponse.GroundResponse" );
		this.cloudLayerResponseClass = tryGetInteractionClass( "HLAinteractionRoot.Service.WeatherResponse.CloudLayerResponse" );
		
		this.instanceId          = weatherResponseClass.getParameter( "InstanceID" );
		this.dateTime            = weatherResponseClass.getParameter( "DateTime" );
		this.timeOffset          = weatherResponseClass.getParameter( "TimeOffset" );
		this.location            = weatherResponseClass.getParameter( "Location" );
		this.weatherResponseType = weatherResponseClass.getParameter( "WeatherResponseType" );
		this.uuid                = weatherResponseClass.getParameter( "UUID" );
		this.entityId            = weatherResponseClass.getParameter( "EntityIdentifier" );
		
		this.precipitationRate   = groundResponseClass.getParameter( "PrecipitationRate" );
		this.temperature         = groundResponseClass.getParameter( "Temperature" );
		this.humidity            = groundResponseClass.getParameter( "Humidity" );
		this.pressure            = groundResponseClass.getParameter( "Pressure" );

		this.totalCloudCover     = cloudLayerResponseClass.getParameter( "TotalCloudCover" );
		
		// Publish and Subscribe
		// NOTE: We really only subscribe to this
		this.publishAndSubscribe( this.groundResponseClass );
		this.publishAndSubscribe( this.cloudLayerResponseClass );
	}

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.DcssWeatherResponse );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		InteractionInstance interaction = null;
		
		try
		{
			if( event.theClass == this.groundResponseClass )
				interaction = deserializeGroundResponseFromHla( event.parameters );
			else if( event.theClass == this.cloudLayerResponseClass )
				interaction = deserializeCloudLayerResponseFromHla( event.parameters );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		if( interaction != null )
		{
			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			opscenter.getPduReceiver().receive( interaction.toPdu().toByteArray() );
		}
	}
	
	private void deserializeCommon( ParameterHandleValueMap map, WeatherResponse interaction )
		throws DecoderException
	{
		deserializeInto( map, instanceId, interaction.getInstanceId() );
		deserializeInto( map, dateTime, interaction.getDateTime() );
		deserializeInto( map, timeOffset, interaction.getTimeOffset() );
		deserializeInto( map, location, interaction.getLocation() );
		deserializeInto( map, weatherResponseType, interaction.getWeatherResponseType() );
		deserializeInto( map, uuid, interaction.getUuid() );
		deserializeInto( map, entityId, interaction.getEntityId() );
	}
	
	private GroundResponse deserializeGroundResponseFromHla( ParameterHandleValueMap map ) 
		throws DecoderException
	{
		GroundResponse interaction = new GroundResponse();
		deserializeCommon( map, interaction );

		deserializeInto( map, precipitationRate, interaction.getPrecipitationRate() );
		deserializeInto( map, temperature, interaction.getTemperate() );
		deserializeInto( map, humidity, interaction.getHumidity() );
		deserializeInto( map, pressure, interaction.getPressure() );
		
		return interaction;
	}
	
	private CloudLayerResponse deserializeCloudLayerResponseFromHla( ParameterHandleValueMap map ) 
		throws DecoderException
	{
		CloudLayerResponse interaction = new CloudLayerResponse();
		deserializeCommon( map, interaction );

		deserializeInto( map, totalCloudCover, interaction.getTotalCloudCover() );
		
		return interaction;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
